package dao;

import models.RentalHistory;
import utils.MySQLConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RentalHistoryDAO {

    private Connection conn;

    public RentalHistoryDAO() {
        this.conn = MySQLConnection.getInstance().getConnection();
    }

    // CREATE
    public boolean addRentalHistory(RentalHistory rh) {
        String sql = "INSERT INTO Rental_History (customer_id, booking_id, return_date, extra_charges, comments) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, rh.getCustomerId());
            pstmt.setInt(2, rh.getBookingId());
            if (rh.getReturnDate() != null) {
                pstmt.setDate(3, Date.valueOf(rh.getReturnDate()));
            } else {
                pstmt.setNull(3, Types.DATE);
            }
            pstmt.setBigDecimal(4, rh.getExtraCharges());
            pstmt.setString(5, rh.getComments());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ (by rental_id)
    public RentalHistory getRentalHistoryById(int rentalId) {
        String sql = "SELECT * FROM Rental_History WHERE rental_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, rentalId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToRentalHistory(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // READ (all)
    public List<RentalHistory> getAllRentalHistories() {
        List<RentalHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM Rental_History ORDER BY created_at DESC";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToRentalHistory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // UPDATE
    public boolean updateRentalHistory(RentalHistory rh) {
        String sql = "UPDATE Rental_History SET customer_id=?, booking_id=?, return_date=?, extra_charges=?, comments=? WHERE rental_id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, rh.getCustomerId());
            pstmt.setInt(2, rh.getBookingId());
            if (rh.getReturnDate() != null) {
                pstmt.setDate(3, Date.valueOf(rh.getReturnDate()));
            } else {
                pstmt.setNull(3, Types.DATE);
            }
            pstmt.setBigDecimal(4, rh.getExtraCharges());
            pstmt.setString(5, rh.getComments());
            pstmt.setInt(6, rh.getRentalId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean deleteRentalHistory(int rentalId) {
        String sql = "DELETE FROM Rental_History WHERE rental_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, rentalId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private RentalHistory mapResultSetToRentalHistory(ResultSet rs) throws SQLException {
        return new RentalHistory(
            rs.getInt("rental_id"),
            rs.getInt("customer_id"),
            rs.getInt("booking_id"),
            rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null,
            rs.getBigDecimal("extra_charges"),
            rs.getString("comments"),
            rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
            rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null
        );
    }
}