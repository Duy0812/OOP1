package model.entity;
import pattern.observer.IObserver;

public class Customer implements IObserver {
    private String customerId;
    private String fullName;

    public Customer(String customerId, String fullName) {
        this.customerId = customerId;
        this.fullName = fullName;
    }

    // View đóng vai trò nhận thông báo
    @Override
    public void update(String message) {
        System.out.println("\n[🔔 THÔNG BÁO TỚI " + fullName.toUpperCase() + "] " + message);
    }
    
    public String getCustomerId() { return customerId; }
}