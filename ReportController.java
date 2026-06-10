package controller;

import model.entity.Invoice;
import java.util.List;

public class ReportController {
    
   // Thực thi chính xác logic: stream().filter(isPaid == 1).mapToDouble(...).sum()
    public double getRevenueReport(List<Invoice> invoices) {
        if (invoices == null) return 0.0;
        
        return invoices.stream()
                .filter(invoice -> invoice.getIsPaid() == 1) // Chỉ tính hóa đơn đã thanh toán thành công
                .mapToDouble(Invoice::getTotalAmount)
                .sum();
    }

        System.out.println("Số lượng hóa đơn đã thanh toán: " + paidCount);
        System.out.println("Tổng doanh thu thực tế: " + String.format("%,.0f", totalRevenue) + " VND");
        System.out.println("----------------------------------------");
    }
}
