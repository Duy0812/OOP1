package OOP.model;

public class Menu {
    private String menuId;
    private String dishName;
    private String category;
    private double pricePerTable;

    public Menu(String menuId, String dishName, String category, double pricePerTable) {
        this.menuId = menuId;
        this.dishName = dishName;
        this.category = category;
        this.pricePerTable = pricePerTable;
    }

    public String getMenuId() { return menuId; }
    public String getDishName() { return dishName; }
    public double getPricePerTable() { return pricePerTable; }

    @Override
    public String toString() {
        return String.format("Mã Món: %-6s | Tên món: %-25s | Danh mục: %-10s | Giá/Bàn: %,.0f VNĐ", 
                menuId, dishName, category, pricePerTable);
    }
}