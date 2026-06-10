package controller;

import pattern.strategy.IPayment;
import model.entity.Invoice;

public class PaymentController {
    private IPayment paymentStrategy;

    public void setPaymentStrategy(IPayment paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public void checkout(Invoice invoice) {
        if (invoice.isPaid()) {
            System.out.println("Hóa đơn này đã được thanh toán từ trước.");
            return;
        }

        if (paymentStrategy != null) {
            boolean success = paymentStrategy.processPayment(invoice.getTotalAmount());
            if (success) {
                invoice.setPaid(true);      // Cập nhật trạng thái
                invoice.notifyObservers();  // Gửi thông báo Observer
            }
        } else {
            System.out.println("Vui lòng chọn phương thức thanh toán!");
        }
    }
}