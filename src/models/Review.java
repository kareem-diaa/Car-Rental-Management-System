package models;

public class Review {
    private int reviewID;
    private int customerID;
    private int bookingID;
    private String review;
    private int rating;
    private String username;

    public Review() {
    }

    public Review(int reviewID, int customerID, int bookingID, String review, int rating) {
        this.reviewID = reviewID;
        this.customerID = customerID;
        this.bookingID = bookingID;
        this.review = review;
        this.rating = rating;
    }

    public int getReviewID() {
        return reviewID;
    }

    public void setReviewID(int reviewID) {
        this.reviewID = reviewID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getBookingID() {
        return bookingID;
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}