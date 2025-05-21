package dao;

import models.CarModel;
import utils.MySQLConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarModelDAO {
    private final Connection conn;

    public CarModelDAO() {
        this.conn = MySQLConnection.getInstance().getConnection();
    }

    public boolean addCarModel(CarModel carModel) {
        String sql = "INSERT INTO Car_Model (brand_id, model_name, fuel_type) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, carModel.getBrandId());
            stmt.setString(2, carModel.getModelName());
            stmt.setString(3, carModel.getFuelType());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateCarModel(CarModel carModel) {
        String sql = "UPDATE Car_Model SET brand_id = ?, model_name = ?, fuel_type = ? WHERE model_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, carModel.getBrandId());
            stmt.setString(2, carModel.getModelName());
            stmt.setString(3, carModel.getFuelType());
            stmt.setInt(4, carModel.getModelId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteCarModel(int modelId) {
        String sql = "DELETE FROM Car_Model WHERE model_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, modelId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public CarModel getCarModelById(int modelId) {
        String sql = "SELECT * FROM Car_Model WHERE model_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, modelId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new CarModel(
                            rs.getInt("model_id"),
                            rs.getInt("brand_id"),
                            rs.getString("model_name"),
                            rs.getString("fuel_type"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<CarModel> getAllCarModels() {
        List<CarModel> carModels = new ArrayList<>();
        String sql = "SELECT * FROM Car_Model";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                carModels.add(new CarModel(
                        rs.getInt("model_id"),
                        rs.getInt("brand_id"),
                        rs.getString("model_name"),
                        rs.getString("fuel_type")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return carModels;
    }
}