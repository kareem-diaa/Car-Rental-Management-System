package views;

import controllers.BookingController;
import controllers.PaymentController;
import controllers.ReviewController;
import models.Booking;
import models.Customer;
import models.Payment;
import models.Review;
import utils.AppColors;

import car_rental.Main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReservationsView extends JPanel {
    private final BookingController bookingController;
    private final PaymentController paymentController;
    private final ReviewController reviewController;
    private final Main mainFrame;
    private final Customer customer;
    private JTable reservationsTable;
    private DefaultTableModel tableModel;

    public ReservationsView(Main mainFrame, Customer customer) {
        this.mainFrame = mainFrame;
        this.customer = customer;
        this.bookingController = new BookingController();
        this.paymentController = new PaymentController();
        this.reviewController = new ReviewController();

        setLayout(new BorderLayout(10, 10));
        setBackground(AppColors.MAIN_BG);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        loadReservations();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(AppColors.ACCENT_TIFFANY);
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Your Reservations", SwingConstants.CENTER);
        titleLabel.setForeground(AppColors.LIGHT_TEXT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(AppColors.MAIN_BG);

        tableModel = new DefaultTableModel(new Object[] {
                "Booking ID", "Car ID", "Start Date", "End Date", "Status", "Payment Status", "Invoice", "Review"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only the "Invoice" and "Review" columns are editable (for the buttons)
                return column == 6 || column == 7;
            }
        };

        reservationsTable = new JTable(tableModel);
        reservationsTable.setFillsViewportHeight(true);
        reservationsTable.setRowHeight(30);
        reservationsTable.setFont(new Font("Arial", Font.PLAIN, 14));

        // Set custom renderer and editor for the "Invoice" button column
        reservationsTable.getColumn("Invoice").setCellRenderer(new InvoiceButtonRenderer());
        reservationsTable.getColumn("Invoice").setCellEditor(new InvoiceButtonEditor(new JCheckBox()));

        // Set custom renderer and editor for the "Review" button column
        reservationsTable.getColumn("Review").setCellRenderer(new ReviewButtonRenderer());
        reservationsTable.getColumn("Review").setCellEditor(new ReviewButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(reservationsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Reservations"));
        scrollPane.setPreferredSize(new Dimension(900, 400));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footerPanel.setBackground(AppColors.ACCENT_TIFFANY);

        JButton backButton = new JButton("Back to Dashboard");
        styleButton(backButton);
        backButton.addActionListener(_ -> navigateBack());
        footerPanel.add(backButton);

        return footerPanel;
    }

    private void styleButton(JButton button) {
        button.setBackground(AppColors.ACCENT_TIFFANY);
        button.setForeground(AppColors.LIGHT_TEXT);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    private void loadReservations() {
        tableModel.setRowCount(0);
        List<Booking> bookings = bookingController.getBookingsByUserId(customer.getCustomerId());
        if (bookings == null || bookings.isEmpty()) {
            tableModel.addRow(new Object[] { "No reservations found", "", "", "", "", "", "", "" });
            reservationsTable.setEnabled(false);
        } else {
            for (Booking booking : bookings) {
                if ("Accepted".equalsIgnoreCase(booking.getStatus())) {
                    List<Payment> payments = paymentController.getPaymentsByBookingId(booking.getBookingId());
                    Payment paidPayment = payments.stream()
                            .filter(p -> "Paid".equalsIgnoreCase(p.getPaymentStatus()))
                            .findFirst().orElse(null);
                    if (paidPayment != null) {
                        // Check if review exists for this booking
                        Review review = reviewController.getReviewByBookingId(booking.getBookingId());
                        String reviewBtnLabel = (review != null) ? "View Review" : "Add Review";
                        tableModel.addRow(new Object[] {
                                booking.getBookingId(),
                                booking.getCarId(),
                                booking.getStartDate(),
                                booking.getEndDate(),
                                booking.getStatus(),
                                "Paid",
                                "Show Invoice",
                                reviewBtnLabel
                        });
                    }
                }
            }
            reservationsTable.setEnabled(tableModel.getRowCount() > 0);
        }
    }

    private void navigateBack() {
        mainFrame.setSize(600, 400);
        mainFrame.getContentPane().removeAll();
        mainFrame.add(new AppView(mainFrame, customer));
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    // Renderer for the "Invoice" button
    class InvoiceButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public InvoiceButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            setText(value == null ? "Show Invoice" : value.toString());
            return this;
        }
    }

    // Editor for the "Invoice" button
    class InvoiceButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean isPushed;
        private int row;

        public InvoiceButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Show Invoice");
            button.setOpaque(true);
            button.addActionListener(_ -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            this.row = row;
            isPushed = true;
            button.setText(value == null ? "Show Invoice" : value.toString());
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int bookingId = (int) tableModel.getValueAt(row, 0);
                List<Payment> payments = paymentController.getPaymentsByBookingId(bookingId);
                Payment paidPayment = payments.isEmpty() ? null : payments.get(0);
                if (paidPayment == null) {
                    JOptionPane.showMessageDialog(button, "No payment found for this booking.");
                } else {
                    InvoiceView invoiceView = new InvoiceView(
                            mainFrame,
                            "INV-" + paidPayment.getPaymentId(),
                            customer.getUsername(),
                            "Booking-"+paidPayment.getBookingId(),  
                            paidPayment.getAmount(),
                            paidPayment.getPaymentDate() != null ? paidPayment.getPaymentDate().toString() : "");
                    JFrame invoiceFrame = new JFrame("Invoice");
                    invoiceFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    invoiceFrame.setContentPane(invoiceView);
                    invoiceFrame.pack();
                    invoiceFrame.setLocationRelativeTo(button);
                    invoiceFrame.setVisible(true);
                }
            }
            isPushed = false;
            return "Show Invoice";
        }
    }

    // Renderer for the "Review" button
    class ReviewButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ReviewButtonRenderer() {
            setOpaque(true);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "Add Review" : value.toString());
            return this;
        }
    }

    // Editor for the "Review" button
    class ReviewButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean isPushed;
        private int row;

        public ReviewButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(_ -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            isPushed = true;
            button.setText(value == null ? "Add Review" : value.toString());
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int bookingId = (int) tableModel.getValueAt(row, 0);
                Review review = reviewController.getReviewByBookingId(bookingId);
                if (review != null) {
                    // Show review in a dialog
                    JFrame viewFrame = new JFrame("View Review");
                    viewFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    viewFrame.setContentPane(new ReviewView(review));
                    viewFrame.pack();
                    viewFrame.setLocationRelativeTo(button);
                    viewFrame.setVisible(true);
                } else {
                    // Open add review form
                    JFrame reviewFrame = new JFrame("Add Review");
                    reviewFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    reviewFrame.setContentPane(new AddReviewView(mainFrame, customer, bookingId));
                    reviewFrame.pack();
                    reviewFrame.setLocationRelativeTo(button);
                    reviewFrame.setVisible(true);
                }
            }
            isPushed = false;
            return button.getText();
        }
    }
}