package OOP.model;

public class BanquetHall {
    private String hallId;
    private String hallName;
    private int capacity;
    private double basePrice;
    private String status; // Trống, Đang bảo trì...

    public BanquetHall(String hallId, String hallName, int capacity, double basePrice, String status) {
        this.hallId = hallId;
        this.hallName = hallName;
        this.capacity = capacity;
        this.basePrice = basePrice;
        this.status = status;
    }

    public String getHallId() { return hallId; }
    public String getHallName() { return hallName; }
    public int getCapacity() { return capacity; }
    public double getBasePrice() { return basePrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double calculateRentPrice() {
        return this.basePrice;
    }

    @Override
    public String toString() {
        return String.format("Mã Sảnh: %-5s | Tên: %-15s | Sức chứa: %-4d | Giá nền: %,.0f VNĐ | Trạng thái: %s", 
                hallId, hallName, capacity, basePrice, status);
    }
}