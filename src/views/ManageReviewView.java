package views;

import controllers.ReviewController;
import models.Review;
import utils.AppColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import car_rental.Main;

import java.awt.*;
import java.util.List;

public class ManageReviewView extends JPanel {
    private final ReviewController reviewController;
    private JTable reviewTable;
    private DefaultTableModel tableModel;
    private final Main mainFrame;
    private JTextField searchField;

    public ManageReviewView(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.reviewController = new ReviewController();

        setLayout(new BorderLayout(10, 10));
        setBackground(AppColors.MAIN_BG);

        add(createFooterPanel(), BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel(new BorderLayout(10, 10));
        centerWrapper.setBackground(AppColors.MAIN_BG);
        centerWrapper.add(createHeaderPanel(), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(AppColors.MAIN_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10);
        centerPanel.add(createTablePanel(), gbc);
        centerWrapper.add(centerPanel, BorderLayout.CENTER);

        add(centerWrapper, BorderLayout.CENTER);

        loadReviews(null);
        mainFrame.setSize(800, 600);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(AppColors.ACCENT_TIFFANY);
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Review Management", SwingConstants.CENTER);
        titleLabel.setForeground(AppColors.LIGHT_TEXT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(AppColors.MAIN_BG);
        tableModel = new DefaultTableModel(new Object[] {
                "Review ID", "User Name", "Booking ID", "Review", "Rating"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reviewTable = new JTable(tableModel);
        reviewTable.setFillsViewportHeight(true);
        reviewTable.setRowHeight(30);
        reviewTable.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(reviewTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Reviews"));
        scrollPane.setPreferredSize(new Dimension(700, 400));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footerPanel.setBackground(AppColors.ACCENT_TIFFANY);

        searchField = new JTextField(20);
        footerPanel.add(searchField);

        JButton searchButton = new JButton("Search");
        styleButton(searchButton);
        searchButton.addActionListener(_ -> loadReviews(searchField.getText().trim()));
        footerPanel.add(searchButton);

        JButton deleteButton = new JButton("Delete Review");
        styleButton(deleteButton);
        deleteButton.setBackground(AppColors.ERROR_RED);
        deleteButton.addActionListener(_ -> deleteSelectedReview());
        footerPanel.add(deleteButton);

        return footerPanel;
    }

    private void styleButton(JButton button) {
        button.setBackground(AppColors.ACCENT_TIFFANY);
        button.setForeground(AppColors.LIGHT_TEXT);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    private void loadReviews(String search) {
        tableModel.setRowCount(0);
        List<Review> reviews = reviewController.getAllReviewsWithUsername();
        boolean found = false;
        for (Review review : reviews) {
            boolean matches = true;
            if (search != null && !search.isEmpty()) {
                String searchLower = search.toLowerCase();
                matches = (String.valueOf(review.getReviewID()).contains(searchLower))
                        || (review.getUsername() != null && review.getUsername().toLowerCase().contains(searchLower))
                        || (String.valueOf(review.getBookingID()).contains(searchLower))
                        || (review.getReview() != null && review.getReview().toLowerCase().contains(searchLower));
            }
            if (matches) {
                tableModel.addRow(new Object[] {
                        review.getReviewID(),
                        review.getUsername(),
                        review.getBookingID(),
                        review.getReview(),
                        review.getRating()
                });
                found = true;
            }
        }
        if (!found) {
            tableModel.addRow(new Object[] { "No reviews found", "", "", "", "", "" });
            reviewTable.setEnabled(false);
        } else {
            reviewTable.setEnabled(true);
        }
    }

    private void deleteSelectedReview() {
        int selectedRow = reviewTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a review to delete.");
            return;
        }
        int reviewId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this review?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = reviewController.deleteReview(reviewId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Review deleted successfully!");
                loadReviews(searchField.getText().trim());
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete review.");
            }
        }
    }

}