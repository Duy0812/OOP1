package pattern.strategy;

public class CreditCardPayment implements IPayment {
    private String cardNumber;
    private String cardHolder;

    public CreditCardPayment(String cardNumber, String cardHolder) {
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
    }

    @Override
    public boolean processPayment(double amount) {
        System.out.println("Đang kết nối cổng thanh toán thẻ...");
        System.out.println("Giao dịch thành công: " + amount + " VND qua Thẻ tín dụng (" + cardNumber + ")");
        return true;
    }
}