package dao;

import models.Discount;
import utils.MySQLConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiscountDAO {
    private final Connection conn;

    public DiscountDAO() {
        this.conn = MySQLConnection.getInstance().getConnection();
    }

    // Create a new discount
    public boolean addDiscount(Discount discount) {
        String sql = "INSERT INTO Discount (discount_id, promotion_code, discount_percent) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, discount.getDiscountId());
            stmt.setString(2, discount.getPromotionCode());
            stmt.setDouble(3, discount.getDiscountPercentage());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Read a discount by ID
    public Discount getDiscountById(int discountId) {
        String sql = "SELECT * FROM Discount WHERE discount_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, discountId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Discount(
                    rs.getInt("discount_id"),
                    rs.getString("promotion_code"),
                    rs.getDouble("discount_percent")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update an existing discount
    public boolean updateDiscount(Discount discount) {
        String sql = "UPDATE Discount SET promotion_code = ?, discount_percent = ? WHERE discount_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, discount.getPromotionCode());
            stmt.setDouble(2, discount.getDiscountPercentage());
            stmt.setInt(3, discount.getDiscountId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete a discount by ID
    public boolean deleteDiscount(int discountId) {
        String sql = "DELETE FROM Discount WHERE discount_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, discountId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all discounts
    public List<Discount> getAllDiscounts() {
        String sql = "SELECT * FROM Discount";
        List<Discount> discounts = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                discounts.add(new Discount(
                    rs.getInt("discount_id"),
                    rs.getString("promotion_code"),
                    rs.getDouble("discount_percent")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return discounts;
    }
}