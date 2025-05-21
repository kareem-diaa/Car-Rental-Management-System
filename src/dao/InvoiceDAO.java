package dao;

import models.Invoice;
import utils.MySQLConnection;

import java.sql.*;

public class InvoiceDAO {
    private final Connection conn;

    public InvoiceDAO() {
        this.conn = MySQLConnection.getInstance().getConnection();
    }

    public boolean addInvoice(Invoice invoice, int paymentId) {
        String sql = "INSERT INTO Invoice (payment_id, invoice_date, total_price, details) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, paymentId);
            stmt.setDate(2, new java.sql.Date(invoice.getInvoiceDate().getTime()));
            stmt.setDouble(3, invoice.getTotalPrice());
            stmt.setString(4, invoice.getDetails());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Invoice getInvoiceById(int invoiceId) {
        String sql = "SELECT * FROM Invoice WHERE invoice_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, invoiceId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Invoice(
                    rs.getInt("invoice_id"),
                    rs.getDate("invoice_date"),
                    rs.getDouble("total_price"),
                    rs.getString("details")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
