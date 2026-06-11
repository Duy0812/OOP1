package pattern.strategy;

public class CreditCard implements IPayment {
    private String cardNumber;
    private String cardHolder;

    public CreditCard(String cardNumber, String cardHolder) {
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
    }

    @Override
    public boolean processPayment(double amount) {
        System.out.println("Đang kết nối cổng ngân hàng...");
        System.out.println(">> Giao dịch THÀNH CÔNG: " + String.format("%,.0f", amount) + " VND qua Thẻ tín dụng (" + cardNumber + ")");
        return true;
    }
}
