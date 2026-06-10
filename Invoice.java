package model.entity;

import pattern.observer.IObserver;
import pattern.observer.ISubject;
import model.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class Invoice implements ISubject {
    private String invoiceId;
    private String bookingId;
    private double totalAmount;
    private boolean isPaid;
    private List<IObserver> observers = new ArrayList<>();

    public Invoice(String invoiceId, String bookingId, double totalAmount) {
        this.invoiceId = invoiceId;
        this.bookingId = bookingId;
        this.totalAmount = totalAmount;
        this.isPaid = false;
    }

    public double getTotalAmount() { return totalAmount; }
    public boolean isPaid() { return isPaid; }

    public void setPaid(boolean paid) {
        this.isPaid = paid;
        if(paid) saveToDatabase(); // Tự động cập nhật DB khi thanh toán xong
    }

    private void saveToDatabase() {
        String sql = "UPDATE Invoice SET is_paid = ?, payment_date = GETDATE() WHERE invoice_id = ?";
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            // Gán giá trị 1/0 cho kiểu BIT, tránh lỗi Conversion Failed
            pstmt.setInt(1, this.isPaid ? 1 : 0); 
            pstmt.setString(2, this.invoiceId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Lỗi cập nhật hóa đơn: " + e.getMessage());
        }
    }

    @Override
    public void attach(IObserver observer) { observers.add(observer); }

    @Override
    public void notifyObservers() {
        for (IObserver obs : observers) {
            obs.update("Hóa đơn " + invoiceId + " trị giá " + String.format("%,.0f", totalAmount) + " VND đã thanh toán thành công!");
        }
    }
}