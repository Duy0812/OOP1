package controller;

import model.entity.Invoice;
import java.util.List;

public class ReportController {
    
    public void generateRevenueReport(List<Invoice> invoiceList) {
        System.out.println("\n--- BÁO CÁO DOANH THU (MANAGER VIEW) ---");
        
        // Sử dụng Java 8 Stream API để lọc hóa đơn đã thanh toán và tính tổng
        double totalRevenue = invoiceList.stream()
            .filter(Invoice::isPaid)
            .mapToDouble(Invoice::getTotalAmount)
            .sum();

        long paidCount = invoiceList.stream().filter(Invoice::isPaid).count();

        System.out.println("Số lượng hóa đơn đã thanh toán: " + paidCount);
        System.out.println("Tổng doanh thu thực tế: " + String.format("%,.0f", totalRevenue) + " VND");
        System.out.println("----------------------------------------");
    }
}