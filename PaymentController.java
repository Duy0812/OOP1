package OOP.controller;

import OOP.model.Invoice;
import OOP.model.IPayment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class PaymentController {

    /**
     * THỰC THI LUỒNG THANH TOÁN ĐA PHƯƠNG THỨC (STRATEGY PATTERN)
     * @param invoice Đối tượng hóa đơn cần thanh toán
     * @param paymentMethod Chiến lược thanh toán được truyền vào từ View (Cash hoặc CreditCard)
     * @param tax Tiền thuế áp dụng (ví dụ: 0.1 tương đương 10%)
     */
    public boolean processInvoicePayment(Invoice invoice, IPayment paymentMethod, double tax) {
        if (paymentMethod == null) {
            System.out.println("❌ Lỗi: Chưa lựa chọn phương thức thanh toán!");
            return false;
        }

        // 1. Tính tổng số tiền cuối cùng phải trả (Gồm giá gốc + thuế)
        double finalAmount = invoice.getTotalAmount() * (1 + tax);

        // 2. Gọi hàm processPayment() xử lý đa hình của Strategy Pattern
        boolean isSuccess = paymentMethod.processPayment(finalAmount);

        if (isSuccess) {
            // 3. Cập nhật dữ liệu trạng thái thanh toán hóa đơn vào Database SQL Server
            String sql = "UPDATE Invoice SET paymentDate = ?, paymentMethod = ?, totalAmount = ? WHERE invoiceId = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setTimestamp(1, new Timestamp(System.currentTimeMillis())); // Lưu ngày giờ thanh toán hiện tại
                ps.setString(2, paymentMethod.getClass().getSimpleName()); // Lưu tên lớp phương thức (CashPayment / CreditCardPayment)
                ps.setDouble(3, finalAmount);
                ps.setString(4, invoice.getInvoiceId());
                
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    // 4. Kích hoạt cốt lõi: Gọi Model Invoice thực thi xử lý thanh toán hoàn tất
                    // Hàm này bên trong Model sẽ kích hoạt tiếp Observer Pattern để thông báo cho Khách hàng
                    invoice.processFullPayment(paymentMethod); 
                    return true;
                }
            } catch (SQLException e) {
                System.out.println("❌ Lỗi cập nhật trạng thái hóa đơn trên DB: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("❌ Hệ thống: Giao dịch thất bại. Vui lòng kiểm tra lại tài khoản!");
        }
        return false;
    }
}