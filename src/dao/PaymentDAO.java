package dao;

import models.Payment;
import utils.MySQLConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {
    private final Connection conn;

    public PaymentDAO() {
        this.conn = MySQLConnection.getInstance().getConnection();
    }

    public boolean addPayment(Payment payment, int bookingId) {
        String sql = "INSERT INTO Payment (customer_id, booking_id, amount, payment_status, payment_method, payment_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, payment.getUserId());
            stmt.setInt(2, bookingId);
            stmt.setDouble(3, payment.getAmount());
            stmt.setString(4, payment.getPaymentStatus());
            stmt.setString(5, payment.getPaymentMethod());
            stmt.setDate(6, new java.sql.Date(payment.getPaymentDate().getTime()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Payment getPaymentById(int paymentId) {
        String sql = "SELECT p.*, u.username as user_name FROM Payment p join Customer u ON p.customer_id = u.customer_id WHERE payment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, paymentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Payment payment = new Payment(
                        rs.getInt("payment_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("booking_id"),
                        rs.getDouble("amount"),
                        rs.getString("payment_status"),
                        rs.getString("payment_method"),
                        rs.getDate("payment_date"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"));
                payment.setUserName(rs.getString("user_name"));
                return payment;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updatePayment(Payment payment) {
        String sql = "UPDATE Payment SET customer_id=?, booking_id=?, amount=?, payment_status=?, payment_method=?, payment_date=? WHERE payment_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, payment.getUserId());
            stmt.setInt(2, payment.getBookingId());
            stmt.setDouble(3, payment.getAmount());
            stmt.setString(4, payment.getPaymentStatus());
            stmt.setString(5, payment.getPaymentMethod());
            stmt.setDate(6, new java.sql.Date(payment.getPaymentDate().getTime()));
            stmt.setInt(7, payment.getPaymentId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePayment(int paymentId) {
        String sql = "DELETE FROM Payment WHERE payment_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, paymentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.*, u.username as user_name FROM Payment p join Customer u ON p.customer_id = u.customer_id";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Payment payment = new Payment(
                        rs.getInt("payment_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("booking_id"),
                        rs.getDouble("amount"),
                        rs.getString("payment_status"),
                        rs.getString("payment_method"),
                        rs.getDate("payment_date"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"));
                payment.setUserName(rs.getString("user_name"));
                payments.add(payment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    public List<Payment> getPaymentsByBookingId(int bookingId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.*, u.username as user_name FROM Payment p join Customer u ON p.customer_id = u.customer_id WHERE booking_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Payment payment = new Payment(
                        rs.getInt("payment_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("booking_id"),
                        rs.getDouble("amount"),
                        rs.getString("payment_status"),
                        rs.getString("payment_method"),
                        rs.getDate("payment_date"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"));
                payment.setUserName(rs.getString("user_name"));
                payments.add(payment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    public List<Payment> getPaymentsByUserName(String userName) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.*, u.username as user_name FROM Payment p join Customer u ON p.customer_id = u.customer_id WHERE LOWER(u.username) LIKE ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + userName.toLowerCase() + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Payment payment = new Payment(
                        rs.getInt("payment_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("booking_id"),
                        rs.getDouble("amount"),
                        rs.getString("payment_status"),
                        rs.getString("payment_method"),
                        rs.getDate("payment_date"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"));
                payment.setUserName(rs.getString("user_name"));
                payments.add(payment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }
}