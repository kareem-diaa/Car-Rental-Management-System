package dao;

import models.CarBrand;
import utils.MySQLConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarBrandDAO {
    private final Connection conn;

    public CarBrandDAO() {
        this.conn = MySQLConnection.getInstance().getConnection();
    }

    public boolean addBrand(CarBrand brand) {
        String sql = "INSERT INTO Brand (brand_name) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, brand.getBrandName());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateBrand(CarBrand brand) {
        String sql = "UPDATE Brand SET brand_name = ? WHERE brand_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, brand.getBrandName());
            stmt.setInt(2, brand.getBrandId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteBrand(int brandId) {
        String sql = "DELETE FROM Brand WHERE brand_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, brandId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public CarBrand getBrandById(int brandId) {
        String sql = "SELECT * FROM Brand WHERE brand_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, brandId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new CarBrand(
                            rs.getInt("brand_id"),
                            rs.getString("brand_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<CarBrand> getAllBrands() {
        List<CarBrand> brands = new ArrayList<>();
        String sql = "SELECT * FROM Brand";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                brands.add(new CarBrand(
                        rs.getInt("brand_id"),
                        rs.getString("brand_name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return brands;
    }
}