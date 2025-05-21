package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Review;
import utils.MySQLConnection;

public class ReviewDAO {
    private Connection conn;

    public ReviewDAO() {
        this.conn = MySQLConnection.getInstance().getConnection();
    }

    // Add a new review to the database
    public boolean addReview(Review review) {
        String sql = "INSERT INTO review (customer_id, booking_id, review, rating) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, review.getCustomerID());
            stmt.setInt(2, review.getBookingID());
            stmt.setString(3, review.getReview());
            stmt.setInt(4, review.getRating());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Retrieve a review by ID
    public Review getReviewById(int id) {
        String sql = "SELECT * FROM review WHERE review_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Review review = new Review();
                    review.setReviewID(rs.getInt("review_id"));
                    review.setCustomerID(rs.getInt("customer_id"));
                    review.setBookingID(rs.getInt("booking_id"));
                    review.setReview(rs.getString("review"));
                    review.setRating(rs.getInt("rating"));
                    return review;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Retrieve all reviews
    public List<Review> getAllReviews() {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM review ORDER BY review_id DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Review review = new Review();
                review.setReviewID(rs.getInt("review_id"));
                review.setCustomerID(rs.getInt("customer_id"));
                review.setBookingID(rs.getInt("booking_id"));
                review.setReview(rs.getString("review"));
                review.setRating(rs.getInt("rating"));
                reviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public Review getReviewByBookingId(int bookingId) {
        String sql = "SELECT * FROM review WHERE booking_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Review review = new Review(
                            rs.getInt("review_id"),
                            rs.getInt("customer_id"),
                            rs.getInt("booking_id"),
                            rs.getString("review"),
                            rs.getInt("rating"));
                    return review;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Review> getAllReviewsWithUsername() {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, u.username FROM review r JOIN customer u ON r.customer_id = u.customer_id ORDER BY r.review_id DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Review review = new Review(
                        rs.getInt("review_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("booking_id"),
                        rs.getString("review"),
                        rs.getInt("rating"));
                review.setUsername(rs.getString("username"));
                reviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    // Update an existing review
    public boolean updateReview(Review review) {
        String sql = "UPDATE review SET review = ?, rating = ? WHERE review_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, review.getReview());
            stmt.setInt(2, review.getRating());
            stmt.setInt(3, review.getReviewID());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete a review by ID
    public boolean deleteReview(int id) {
        String sql = "DELETE FROM review WHERE review_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}