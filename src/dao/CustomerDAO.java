package dao;

import models.Customer;
import models.RentalHistory;
import utils.HashUtil;
import utils.MySQLConnection;
import utils.ValidationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    private Connection conn;

    public CustomerDAO() {
        this.conn = MySQLConnection.getInstance().getConnection();
    }

    public boolean register(int customerId, String username, String password, String email, String phone,
            String licenseNumber) throws ValidationException {
        String sql = "INSERT INTO customer (customer_id, username, password_hash, email, phone, license_number) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.setString(4, email);
            pstmt.setString(5, phone);
            pstmt.setString(6, licenseNumber);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                throw new ValidationException("Error: Duplicate entry for username or email.");
            }
            e.printStackTrace();
            return false;
        }
    }

    public boolean login(String email, String password) {
        String sql = "SELECT password_hash from customer WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                return HashUtil.verifyPassword(password, storedHash);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String email) {
        String sql = "DELETE from customer WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isVerified(String email) {
        String sql = "SELECT is_verified from customer WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("is_verified");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean verifyUser(String email) {
        String sql = "UPDATE customer SET is_verified = TRUE WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Customer getByEmail(String email) {
        String sql = "SELECT customer_id, username, email, phone, license_number from customer WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Customer customer = new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("username"),
                        null,
                        rs.getString("phone"),
                        rs.getString("license_number"));

                // Make sure to set the email
                customer.setEmail(rs.getString("email"));

                return customer;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateProfile(Customer c) {
        String sql = "UPDATE customer SET phone = ?, license_number = ? WHERE customer_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getPhone());
            stmt.setString(2, c.getLicenseNumber());
            stmt.setInt(3, c.getCustomerId());
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<RentalHistory> getRentalHistory(int customerId) {
        List<RentalHistory> list = new ArrayList<>();
        String sql = "SELECT rental_id, customer_id, booking_id, return_date, extra_charges, comments, created_at, updated_at "
                +
                "FROM Rental_History WHERE customer_id = ? ORDER BY created_at DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                RentalHistory rh = new RentalHistory(
                        rs.getInt("rental_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("booking_id"),
                        rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null,
                        rs.getBigDecimal("extra_charges"),
                        rs.getString("comments"),
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
                        rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
                list.add(rh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Get all customers
    // This method retrieves all customers from the database and returns them as a
    // list.
    // It does not include sensitive information like passwords.

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT customer_id, username, email, phone, license_number, is_verified from customer";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("username"),
                        null, // We don't retrieve passwords
                        rs.getString("phone"),
                        rs.getString("license_number"));
                customer.setEmail(rs.getString("email"));
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customers;
    }

    public boolean isEmailExists(String email) {
        String sql = "SELECT 1 FROM customer WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
