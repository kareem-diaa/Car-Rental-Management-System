package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Car;
import utils.MySQLConnection;

public class CarDAO {
    private final Connection conn;

    public CarDAO() {
        this.conn = MySQLConnection.getInstance().getConnection();
    }

    // Create - Insert a new car
    public boolean insertCar(Car car) {
        String sql = "INSERT INTO car (model_id, category_id, mileage, availability_status, rental_price, plate_no, image_url, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, car.getModelID());
            stmt.setInt(2, car.getCategoryID());
            stmt.setInt(3, car.getMileage());
            stmt.setBoolean(4, car.getAvailabilityStatus());
            stmt.setFloat(5, car.getRentalPrice());
            stmt.setString(6, car.getPlateNo());
            stmt.setString(7, car.getImageURL());
            stmt.setTimestamp(8, Timestamp.valueOf(car.getCreatedAt()));
            stmt.setTimestamp(9, Timestamp.valueOf(car.getUpdatedAt()));

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting car: " + e.getMessage());
            return false;
        }
    }

    // Read - Retrieve a car by ID
    public Car getCarById(int carId) {
        String sql = "SELECT * FROM car WHERE car_id = ?";
        Car car = null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, carId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    car = mapResultSetToCar(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving car by ID: " + e.getMessage());
        }

        return car;
    }

    // Read - Retrieve all cars
    public List<Car> getAllCars() {
        List<Car> carList = new ArrayList<>();
        String sql = "SELECT * FROM car";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                carList.add(mapResultSetToCar(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving all cars: " + e.getMessage());
        }

        return carList;
    }

    // Update - Modify an existing car
    public boolean updateCar(Car car) {
        String sql = "UPDATE car SET model_id=?, category_id=?, mileage=?, availability_status=?, rental_price=?, plate_no=?, image_url=?, updated_at=? WHERE car_id=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, car.getModelID());
            stmt.setInt(2, car.getCategoryID());
            stmt.setInt(3, car.getMileage());
            stmt.setBoolean(4, car.getAvailabilityStatus());
            stmt.setFloat(5, car.getRentalPrice());
            stmt.setString(6, car.getPlateNo());
            stmt.setString(7, car.getImageURL());
            stmt.setTimestamp(8, Timestamp.valueOf(car.getUpdatedAt()));
            stmt.setInt(9, car.getCarID());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating car: " + e.getMessage());
            return false;
        }
    }

    // Delete - Remove a car by ID
    public boolean deleteCar(int carId) {
        String sql = "DELETE FROM car WHERE car_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, carId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting car: " + e.getMessage());
            return false;
        }
    }

    // Helper method: map SQL result to Car object
    private Car mapResultSetToCar(ResultSet rs) throws SQLException {
        Car car = new Car();
        car.setCarID(rs.getInt("car_id"));
        car.setModelID(rs.getInt("model_id"));
        car.setCategoryID(rs.getInt("category_id"));
        car.setMileage(rs.getInt("mileage"));
        car.setAvailabilityStatus(rs.getBoolean("availability_status"));
        car.setRentalPrice(rs.getFloat("rental_price"));
        car.setPlateNo(rs.getString("plate_no"));
        car.setImageURL(rs.getString("image_url"));
        car.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        car.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return car;
    }
}