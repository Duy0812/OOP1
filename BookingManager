package OOP;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BookingManager {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    // --- PHƯƠNG THỨC 1: KHÁCH HÀNG CHỦ ĐỘNG HỦY ---
    public void customerCancelBooking(Booking booking) {
        if ("CANCELLED".equals(booking.getStatus())) {
            System.out.println("Thông báo: Đơn đặt tiệc này đã được hủy trước đó.");
            return;
        }

        // Kiểm tra nếu khách đã đặt cọc thì thực hiện xử lý hoàn tiền theo quy định
        if (booking.getDepositAmount() > 0) {
            System.out.println("Hệ thống: Khách hàng đã đặt cọc. Tiến hành xử lý phí hủy đơn...");
            booking.processCancel(); // Gọi hàm xử lý hủy có tính phí đã viết trong Booking.java
        } else {
            booking.setStatus("CANCELLED");
            System.out.println("Hệ thống: Đã hủy đơn hàng chưa đặt cọc của khách: " + booking.getCustomer().getFullName());
        }
    }

    // --- PHƯƠNG THỨC 2: TỰ ĐỘNG HỦY DO QUÁ HẠN 24 GIỜ ---
    public void setupTimeoutCancel(Booking booking) {
        System.out.println("Hệ thống: Bắt đầu đếm ngược 24 giờ cho đơn hàng mã: " + booking.getBookingId());

        scheduler.schedule(() -> {
            // Kiểm tra: Nếu vẫn chưa thanh toán cọc VÀ trạng thái vẫn là chờ (Pending)
            if (!"CONFIRMED".equals(booking.getStatus()) && !"CANCELLED".equals(booking.getStatus())) {
                booking.setStatus("CANCELLED");
                
                System.out.println("\n------------------------------------------------");
                System.out.println("HỆ THỐNG TỰ ĐỘNG HỦY:");
                System.out.println("Mã đơn: " + booking.getBookingId());
                System.out.println("Lý do: Quá hạn 24 giờ thanh toán tiền cọc.");
                System.out.println("------------------------------------------------\n");
            }
        }, 24, TimeUnit.HOURS); 
    }
}
