package OOP.controller;

import OOP.model.Booking;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BookingController {
    
    // Tạo một Thread Pool ngầm để quản lý các tiến trình đếm ngược hủy đơn tự động
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

    /**
     * 1. LUỒNG TẠO ĐƠN ONLINE (CREATE BOOKING)
     * Lưu thông tin đơn đặt tiệc mới từ giao diện Figma/Khách hàng vào Database.
     * Sau khi tạo thành công, tự động kích hoạt Timer đếm ngược 24h để thanh toán cọc.
     */
    public boolean createOnlineBooking(Booking booking) {
        String sql = "INSERT INTO Booking (bookingId, bookingDate, eventDate, shift, status, basePrice, customerId, hallId) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, booking.getBookingId());
            // bookingDate là ngày hiện tại tạo đơn online
            ps.setTimestamp(2, new Timestamp(booking.getBookingDate().getTime())); 
            ps.setTimestamp(3, new Timestamp(booking.getEventDate().getTime()));
            ps.setString(4, booking.getShift());
            ps.setString(5, booking.getStatus()); // Thường mặc định ban đầu là "PENDING"
            ps.setDouble(6, booking.getBasePrice());
            ps.setString(7, booking.getCustomer().getCustomerId());
            ps.setString(8, booking.getHall().getHallId());
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ [Online] Tạo thành công đơn đặt tiệc trực tuyến mã: " + booking.getBookingId());
                
                // 🔥 KÍCH HOẠT TIMER: Tự động đưa đơn này vào hàng đợi quét hủy sau 24h nếu chưa cọc
                setupTimeoutCancel(booking);
                return true;
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi hệ thống khi tạo đơn online: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 2. TÍNH TIỀN CỌC (GET DEPOSIT AMOUNT)
     * Kết hợp chặt chẽ với Model Booking để lấy số tiền cọc (30% tổng chi phí gồm sảnh + món ăn + dịch vụ).
     */
    public double getDepositAmount(Booking booking) {
        // Gọi trực tiếp logic nghiệp vụ cốt lõi đã được đóng gói an toàn tại lớp Model
        return booking.DepositAmount(); 
    }

    /**
     * 3. XỬ LÝ KHÁCH HÀNG HỦY TIỆC & TÍNH PHÍ PHẠT (CUSTOMER CANCEL BOOKING)
     * Cập nhật trạng thái Database thành CANCELLED và gọi Model tính toán hoàn tiền phạt.
     */
    public void customerCancelBooking(Booking booking) {
        if ("CANCELLED".equals(booking.getStatus())) {
            System.out.println("⚠️ Thông báo: Đơn đặt tiệc này đã được hủy trước đó.");
            return;
        }

        String sql = "UPDATE Booking SET status = 'CANCELLED' WHERE bookingId = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, booking.getBookingId());
            int rows = ps.executeUpdate();
            
            if (rows > 0) {
                // Đồng bộ trạng thái sảnh tiệc về "Trống" và tính phí phạt/hoàn tiền dựa trên quy định tại Model
                booking.processCancel(); 
                System.out.println("✅ Cập nhật trạng thái hủy đơn thành công trên Cơ sở dữ liệu.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi khi thực hiện khách hàng chủ động hủy đơn: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 4. TIMER TỰ ĐỘNG QUÉT HỦY ĐƠN QUÁ HẠN (AUTOMATIC TIMEOUT CANCEL)
     * Luồng chạy ngầm đếm ngược đúng 24 giờ kể từ lúc tạo đơn online.
     * Nếu trạng thái trên DB vẫn là 'PENDING' (Chưa đặt cọc), hệ thống tự động hủy đơn và giải phóng sảnh.
     */
    private void setupTimeoutCancel(Booking booking) {
        System.out.println("⏳ [Hệ thống] Bắt đầu đếm ngược 24 giờ thanh toán tiền cọc cho đơn: " + booking.getBookingId());

        // Lập lịch thực thi tác vụ kiểm tra sau 24 tiếng (Bạn có thể đổi sang TimeUnit.SECONDS để test nhanh)
        scheduler.schedule(() -> {
            String checkSql = "SELECT status FROM Booking WHERE bookingId = ?";
            String updateSql = "UPDATE Booking SET status = 'CANCELLED' WHERE bookingId = ? AND status = 'PENDING'";
            
            try (Connection conn = DatabaseConnection.getConnection()) {
                // Bước 1: Kiểm tra trạng thái thực tế mới nhất trên Database (Đề phòng khách đã cọc tại quầy)
                try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                    psCheck.setString(1, booking.getBookingId());
                    try (ResultSet rs = psCheck.executeQuery()) {
                        if (rs.next()) {
                            String currentStatus = rs.getString("status");
                            
                            // Bước 2: Nếu vẫn là PENDING, tiến hành hủy tự động
                            if ("PENDING".equalsIgnoreCase(currentStatus)) {
                                try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                                    psUpdate.setString(1, booking.getBookingId());
                                    int rows = psUpdate.executeUpdate();
                                    
                                    if (rows > 0) {
                                        // Đồng bộ hóa trạng thái đối tượng bộ nhớ và giải phóng sảnh tiệc
                                        booking.setStatus("CANCELLED");
                                        if (booking.getHall() != null) {
                                            booking.getHall().setStatus("Trống");
                                        }
                                        System.out.println("\n🚨 ------------------------------------------------");
                                        System.out.println("⏰ HỆ THỐNG TỰ ĐỘNG HỦY TIỆC (TIMEOUT):");
                                        System.out.println("-> Mã đơn: " + booking.getBookingId());
                                        System.out.println("-> Lý do: Quá hạn 24 giờ kể từ khi đăng ký online mà chưa thanh toán tiền cọc.");
                                        System.out.println("-> Trạng thái sảnh '" + booking.getHall().getHallId() + "' đã được trả về: Trống.");
                                        System.out.println("---------------------------------------------------\n");
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                System.out.println("❌ Lỗi trong tiến trình quét hủy tự động ngầm: " + e.getMessage());
                e.printStackTrace();
            }
        }, 24, TimeUnit.HOURS);
    }
    
    /**
     * Hàm giải phóng tài nguyên của Thread Pool khi tắt ứng dụng
     */
    public void shutdownController() {
        scheduler.shutdown();
    }
}