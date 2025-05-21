package views;

import models.Review;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ReviewView extends JPanel {
    public ReviewView(Review review) {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Review for Booking ID: " + review.getBookingID());
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JTextArea reviewArea = new JTextArea(review.getReview());
        reviewArea.setEditable(false);
        reviewArea.setLineWrap(true);
        reviewArea.setWrapStyleWord(true);

        JLabel ratingLabel = new JLabel("Rating: " + review.getRating() + " / 5");
        ratingLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.add(title);
        panel.add(new JLabel("Review:"));
        panel.add(new JScrollPane(reviewArea));
        panel.add(ratingLabel);

        add(panel, BorderLayout.CENTER);
    }
}