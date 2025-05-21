package views;

import controllers.ReviewController;
import models.Customer;
import models.Review;
import utils.AppColors;
import utils.ValidationException;
import car_rental.Main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AddReviewView extends JPanel {
    public AddReviewView(Main mainFrame, Customer customer, int bookingId) {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(AppColors.MAIN_BG);

        JLabel label = new JLabel("Add your review for Booking ID: " + bookingId);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        JTextArea reviewArea = new JTextArea(5, 30);
        JSpinner ratingSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 5, 1));
        JButton submitBtn = new JButton("Submit Review");

        submitBtn.setBackground(AppColors.ACCENT_TIFFANY);
        submitBtn.setForeground(AppColors.LIGHT_TEXT);
        submitBtn.setFont(new Font("Arial", Font.BOLD, 14));

        submitBtn.addActionListener(e -> {
            String reviewText = reviewArea.getText().trim();
            int rating = (int) ratingSpinner.getValue();
            if (reviewText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your review.");
                return;
            }
            ReviewController reviewController = new ReviewController();
            Review review = new Review(0, customer.getCustomerId(), bookingId, reviewText, rating);
            try {
                if (reviewController.addReview(review)) {
                    JOptionPane.showMessageDialog(this, "Review submitted!");
                    SwingUtilities.getWindowAncestor(this).dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to submit review.");
                }
            } catch (HeadlessException | ValidationException e1) {
                e1.printStackTrace();
            }
        });

        JPanel form = new JPanel(new GridLayout(0, 1, 10, 10));
        form.setBackground(AppColors.MAIN_BG);
        form.add(label);
        form.add(new JLabel("Review:"));
        form.add(new JScrollPane(reviewArea));
        form.add(new JLabel("Rating (1-5):"));
        form.add(ratingSpinner);
        form.add(submitBtn);

        add(form, BorderLayout.CENTER);
    }
}