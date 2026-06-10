package OOP.controller;

import OOP.model.Menu;
import OOP.model.Staff;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ManagerController {

    // ==================== QUẢN LÝ MÓN ĂN (MENU) ====================
    
    public boolean addMenu(Menu menu) {
        String checkSql = "SELECT COUNT(*) FROM Menu WHERE menuId = ?";
        String insertSql = "INSERT INTO Menu (menuId, dishName, category, pricePerTable) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setString(1, menu.getMenuId());
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) return false;
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

    public boolean updateMenu(Menu menu) {
        String sql = "UPDATE Menu SET dishName = ?, pricePerTable = ? WHERE menuId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, menu.getDishName());
            ps.setDouble(2, menu.getPricePerTable());
            ps.setString(3, menu.getMenuId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeMenu(String menuId) {
        String checkSql = "SELECT COUNT(*) FROM bookingmenu bm JOIN Booking b ON bm.bookingId = b.bookingId WHERE bm.menuId = ? AND b.status IN ('PENDING', 'CONFIRMED')";
        String deleteSql = "DELETE FROM Menu WHERE menuId = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setString(1, menuId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) return false; // Đang có đơn đặt, không cho xóa
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

    // ==================== QUẢN LÝ NHÂN SỰ (STAFF) ====================

    public boolean addStaffAccount(Staff staff) {
        String sql = "INSERT INTO Staff (staffId, fullName, role, phone) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, staff.getStaffId());
            ps.setString(2, staff.getFullName());
            ps.setString(3, staff.getRole());
            ps.setString(4, staff.getPhone());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== BÁO CÁO THỐNG KÊ (REPORT) ====================

    public double reportRevenue() {
        String sql = "SELECT SUM(totalAmount) FROM Invoice";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public Map<String, Integer> reportBookingStats() {
        String sql = "SELECT status, COUNT(*) FROM Booking GROUP BY status";
        Map<String, Integer> stats = new HashMap<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                stats.put(rs.getString("status"), rs.getInt(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
}