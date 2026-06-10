package pattern.strategy;

public class CashPayment implements IPayment {
    @Override
    public boolean processPayment(double amount) {
        System.out.println("Đã nhận đủ tiền mặt: " + amount + " VND tại quầy Lễ tân.");
        return true;
    }
}