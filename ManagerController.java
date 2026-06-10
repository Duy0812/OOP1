package OOP.controller;

import OOP.model.Menu;
import java.sql.*;

public class ManagerController {

    // ==================== 1. QUẢN LÝ MÓN ĂN (MENU) ====================
    
    // THÊM MÓN ĂN (Kiểm tra trùng ID trước khi thêm)
    public boolean addMenu(Menu menu) {
        String checkSql = "SELECT COUNT(*) FROM Menu WHERE menuId = ?";
        String insertSql = "INSERT INTO Menu (menuId, dishName, category, pricePerTable) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setString(1, menu.getMenuId());
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) return false; // Trùng ID món ăn
                }
            }
            try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                psInsert.setString(1, menu.getMenuId());
                psInsert.setString(2, menu.getDishName());
                psInsert.setString(3, menu.getCategory());
                psInsert.setDouble(4, menu.getPricePerTable());
                return psInsert.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // SỬA MÓN ĂN (Cập nhật thông tin dựa trên ID)
    public boolean updateMenu(Menu menu) {
        String sql = "UPDATE Menu SET dishName = ?, category = ?, pricePerTable = ? WHERE menuId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, menu.getDishName());
            ps.setString(2, menu.getCategory());
            ps.setDouble(3, menu.getPricePerTable());
            ps.setString(4, menu.getMenuId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // XÓA MÓN ĂN AN TOÀN (Không cho xóa nếu món ăn đang nằm trong tiệc chưa diễn ra)
    public boolean removeMenu(String menuId) {
        String checkSql = "SELECT COUNT(*) FROM bookingmenu bm JOIN Booking b ON bm.bookingId = b.bookingId " +
                          "WHERE bm.menuId = ? AND b.status IN ('PENDING', 'CONFIRMED')";
        String deleteSql = "DELETE FROM Menu WHERE menuId = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setString(1, menuId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) return false; // Đang có tiệc đặt món này, không được xóa
                }
            }
            try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
                psDelete.setString(1, menuId);
                return psDelete.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== 2. QUẢN LÝ AN TOÀN SẢNH TIỆC (HALL SAFETY) ====================

    /**
     * KIỂM TRA SẢNH TRỐNG: Đảm bảo an toàn dữ liệu lịch trình.
     * Tránh tình trạng trùng sảnh (Overbooking) trong cùng một ngày và một ca tiệc (Shift).
     */
    public boolean isHallAvailable(String hallId, Date weddingDate, String shift) {
        String sql = "SELECT COUNT(*) FROM Booking WHERE hallId = ? AND weddingDate = ? AND shift = ? AND status IN ('PENDING', 'CONFIRMED')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hallId);
            ps.setDate(2, weddingDate);
            ps.setString(3, shift);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0; // Trả về true nếu không có tiệc nào trùng lịch
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * THAY ĐỔI TRẠNG THÁI SẢNH AN TOÀN: 
     * Nếu chuyển sảnh sang bảo trì (MAINTENANCE), hệ thống phải kiểm tra xem tương lai sảnh này có tiệc nào không.
     */
    public boolean updateHallStatus(String hallId, String targetStatus) {
        if ("MAINTENANCE".equalsIgnoreCase(targetStatus)) {
            String checkSql = "SELECT COUNT(*) FROM Booking WHERE hallId = ? AND weddingDate >= GETDATE() AND status IN ('PENDING', 'CONFIRMED')";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setString(1, hallId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("Lỗi: Sảnh đang có tiệc được đặt trong tương lai, không thể chuyển sang bảo trì!");
                        return false; 
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        String updateSql = "UPDATE Hall SET status = ? WHERE hallId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
            psUpdate.setString(1, targetStatus);
            psUpdate.setString(2, hallId);
            return psUpdate.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * XÓA SẢNH TIỆC AN TOÀN: 
     * Ràng buộc dữ liệu không cho phép xóa sảnh nếu sảnh đó từng có lịch sử tổ chức tiệc (để giữ tính toàn vẹn dữ liệu).
     */
    public boolean removeHall(String hallId) {
        String checkSql = "SELECT COUNT(*) FROM Booking WHERE hallId = ?";
        String deleteSql = "DELETE FROM Hall WHERE hallId = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setString(1, hallId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("Lỗi: Sảnh có dữ liệu tiệc liên kết (Khóa ngoại), không được xóa!");
                        return false; 
                    }
                }
            }
            try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
                psDelete.setString(1, hallId);
                return psDelete.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
