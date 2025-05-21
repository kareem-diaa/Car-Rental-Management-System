package models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RentalHistory {
    private int rentalId;
    private int customerId;
    private int bookingId;
    private LocalDate returnDate;
    private BigDecimal extraCharges;
    private String comments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RentalHistory() {}

    public RentalHistory(int rentalId, int customerId, int bookingId, LocalDate returnDate, BigDecimal extraCharges, String comments, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.rentalId = rentalId;
        this.customerId = customerId;
        this.bookingId = bookingId;
        this.returnDate = returnDate;
        this.extraCharges = extraCharges;
        this.comments = comments;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getRentalId() {
        return rentalId;
    }

    public void setRentalId(int rentalId) {
        this.rentalId = rentalId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public BigDecimal getExtraCharges() {
        return extraCharges;
    }

    public void setExtraCharges(BigDecimal extraCharges) {
        this.extraCharges = extraCharges;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}