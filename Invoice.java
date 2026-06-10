package OOP.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Invoice implements Subject {
    private String invoiceId;
    private Date paymentDate;
    private double tax;
    private double totalAmount;
    private String paymentMethod;
    private Booking booking;
    
    // 🔔 OBSERVER PATTERN: Danh sách lưu trữ các thực thể đăng ký lắng nghe sự kiện từ Hóa đơn
    private List<Observer> observers = new ArrayList<>();

    // Constructor chuẩn khớp luồng khởi tạo hóa đơn
    public Invoice(String invoiceId, double tax, Booking booking) {
        this.invoiceId = invoiceId;
        this.tax = tax;
        this.booking = booking;
        this.totalAmount = booking.calculateTotal(); // Lấy tổng tiền cơ bản từ đơn đặt tiệc mẫu
        this.paymentDate = new Date(); // Khởi tạo ngày hóa đơn mặc định
        
        // Tự động đăng ký Khách hàng của đơn tiệc này làm người quan sát (Observer)
        if (booking != null && booking.getCustomer() != null) {
            registerObserver(booking.getCustomer());
        }
    }

    // Các hàm Getters / Setters dữ liệu
    public String getInvoiceId() { return invoiceId; }
    public double getTotalAmount() { return totalAmount; }
    public Date getPaymentDate() { return paymentDate; }
    public String getPaymentMethod() { return paymentMethod; }

    /**
     * Logic nghiệp vụ xử lý thanh toán hoàn tất (Kết hợp với Strategy)
     */
    public void processFullPayment(IPayment method) {
        this.paymentMethod = method.getClass().getSimpleName();
        
        // Cập nhật trạng thái của đơn đặt tiệc trong bộ nhớ sang trạng thái ĐÃ THANH TOÁN HOÀN TẤT
        if (booking != null) {
            booking.setStatus("PAID");
            if (booking.getHall() != null) {
                booking.getHall().setStatus("Trống"); // Giải phóng sảnh về trạng thái sẵn sàng đón khách mới
            }
        }
        
        System.out.println("🎉 [Invoice Model]: Đã xử lý thành công dữ liệu hóa đơn: " + invoiceId);
        
        // 🔥 PHÁT TIN HIỆU THỜI GIAN THỰC: Tự động gửi thông báo đến toàn bộ các Observers trong danh sách
        notifyObservers();
    }

    // =========================================================================
    // TRIỂN KHAI CÁC PHƯƠNG THỨC CỦA INTERFACE SUBJECT (OBSERVER PATTERN)
    // =========================================================================
    
    @Override
    public void registerObserver(Observer o) {
        if (o != null && !observers.contains(o)) {
            observers.add(o);
        }
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        // Duyệt Stream hoặc Vòng lặp kích hoạt hàm update() đồng loạt thời gian thực
        for (Observer observer : observers) {
            observer.update(this);
        }
    }
}package OOP.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Invoice implements Subject {
    private String invoiceId;
    private Date paymentDate;
    private double tax;
    private double totalAmount;
    private String paymentMethod;
    private Booking booking;
    
    // 🔔 OBSERVER PATTERN: Danh sách lưu trữ các thực thể đăng ký lắng nghe sự kiện từ Hóa đơn
    private List<Observer> observers = new ArrayList<>();

    // Constructor chuẩn khớp luồng khởi tạo hóa đơn
    public Invoice(String invoiceId, double tax, Booking booking) {
        this.invoiceId = invoiceId;
        this.tax = tax;
        this.booking = booking;
        this.totalAmount = booking.calculateTotal(); // Lấy tổng tiền cơ bản từ đơn đặt tiệc mẫu
        this.paymentDate = new Date(); // Khởi tạo ngày hóa đơn mặc định
        
        // Tự động đăng ký Khách hàng của đơn tiệc này làm người quan sát (Observer)
        if (booking != null && booking.getCustomer() != null) {
            registerObserver(booking.getCustomer());
        }
    }

    // Các hàm Getters / Setters dữ liệu
    public String getInvoiceId() { return invoiceId; }
    public double getTotalAmount() { return totalAmount; }
    public Date getPaymentDate() { return paymentDate; }
    public String getPaymentMethod() { return paymentMethod; }

    /**
     * Logic nghiệp vụ xử lý thanh toán hoàn tất (Kết hợp với Strategy)
     */
    public void processFullPayment(IPayment method) {
        this.paymentMethod = method.getClass().getSimpleName();
        
        // Cập nhật trạng thái của đơn đặt tiệc trong bộ nhớ sang trạng thái ĐÃ THANH TOÁN HOÀN TẤT
        if (booking != null) {
            booking.setStatus("PAID");
            if (booking.getHall() != null) {
                booking.getHall().setStatus("Trống"); // Giải phóng sảnh về trạng thái sẵn sàng đón khách mới
            }
        }
        
        System.out.println("🎉 [Invoice Model]: Đã xử lý thành công dữ liệu hóa đơn: " + invoiceId);
        
        // 🔥 PHÁT TIN HIỆU THỜI GIAN THỰC: Tự động gửi thông báo đến toàn bộ các Observers trong danh sách
        notifyObservers();
    }

    // =========================================================================
    // TRIỂN KHAI CÁC PHƯƠNG THỨC CỦA INTERFACE SUBJECT (OBSERVER PATTERN)
    // =========================================================================
    
    @Override
    public void registerObserver(Observer o) {
        if (o != null && !observers.contains(o)) {
            observers.add(o);
        }
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        // Duyệt Stream hoặc Vòng lặp kích hoạt hàm update() đồng loạt thời gian thực
        for (Observer observer : observers) {
            observer.update(this);
        }
    }
}
