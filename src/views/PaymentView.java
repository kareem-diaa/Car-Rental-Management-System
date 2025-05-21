package views;

import controllers.BookingController;
import controllers.PaymentController;
import models.Booking;
import models.Customer;
import models.Payment;
import utils.AppColors;

import car_rental.Main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.Date;

public class PaymentView extends JPanel {
    private final BookingController bookingController;
    private final PaymentController paymentController;
    private final Main mainFrame;
    private final Customer customer;
    private JTable bookingTable;
    private DefaultTableModel tableModel;

    public PaymentView(Main mainFrame, Customer customer) {
        this.mainFrame = mainFrame;
        this.customer = customer;
        this.bookingController = new BookingController();
        this.paymentController = new PaymentController();

        setLayout(new BorderLayout(10, 10));
        setBackground(AppColors.MAIN_BG);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        loadBookings();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(AppColors.ACCENT_TIFFANY);
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Your Pending Bookings (Unpaid)", SwingConstants.CENTER);
        titleLabel.setForeground(AppColors.LIGHT_TEXT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(AppColors.MAIN_BG);

        tableModel = new DefaultTableModel(new Object[] {
                "Booking ID", "Car ID", "Start Date", "End Date", "Status", "Pay"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only the "Pay" column is editable (for the button)
                return column == 5;
            }
        };

        bookingTable = new JTable(tableModel);
        bookingTable.setFillsViewportHeight(true);
        bookingTable.setRowHeight(30);
        bookingTable.setFont(new Font("Arial", Font.PLAIN, 14));

        // Add custom renderer and editor for the "Pay" column
        bookingTable.getColumn("Pay").setCellRenderer(new PayButtonRenderer());
        bookingTable.getColumn("Pay").setCellEditor(new PayButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(bookingTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Bookings"));
        scrollPane.setPreferredSize(new Dimension(700, 400));
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

    private void loadBookings() {
        tableModel.setRowCount(0);
        List<Booking> bookings;
        try {
            bookings = bookingController.getBookingsByUserId(customer.getCustomerId());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading bookings: " + ex.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            tableModel.setRowCount(0);
            tableModel.addRow(new Object[] { "Error loading bookings", "", "", "", "", "" });
            bookingTable.setEnabled(false);
            return;
        }
        if (bookings == null || bookings.isEmpty()) {
            tableModel.addRow(new Object[] { "No bookings found", "", "", "", "", "" });
            bookingTable.setEnabled(false);
        } else {
            for (Booking booking : bookings) {
                // Only show bookings with status "Pending" and no payment
                if ("Pending".equalsIgnoreCase(booking.getStatus())
                        && paymentController.getPaymentsByBookingId(booking.getBookingId()).isEmpty()) {
                    tableModel.addRow(new Object[] {
                            booking.getBookingId(),
                            booking.getCarId(),
                            booking.getStartDate(),
                            booking.getEndDate(),
                            booking.getStatus(),
                            "Pay"
                    });
                }
            }
            bookingTable.setEnabled(tableModel.getRowCount() > 0);
        }
    }

    // Renderer for the "Pay" button
    class PayButtonRenderer extends JButton implements TableCellRenderer {
        public PayButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            setText("Pay");
            setEnabled(true);
            return this;
        }
    }

    // Editor for the "Pay" button
    class PayButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private boolean isPushed;

        public PayButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Pay");
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int selectedRow = bookingTable.getSelectedRow();
                int bookingId = (int) tableModel.getValueAt(selectedRow, 0);

                // Create a new Payment object (now includes userId)
                Payment payment = new Payment();
                payment.setBookingId(bookingId);
                payment.setUserId(customer.getCustomerId()); // Set the userId here!
                payment.setAmount(100.0); // Set the amount as needed (e.g., fetch from booking/price)
                payment.setPaymentStatus("Paid");
                payment.setPaymentMethod("Cash"); // Or prompt user for method
                payment.setPaymentDate(new Date());
                payment.setCreatedAt(new Date());
                payment.setUpdatedAt(new Date());

                boolean success = paymentController.addPayment(payment);
                if (success) {
                    JOptionPane.showMessageDialog(mainFrame, "Payment successful for Booking ID: " + bookingId,
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    // Reload table after editing is stopped
                    SwingUtilities.invokeLater(() -> loadBookings());
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Payment failed for Booking ID: " + bookingId, "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            isPushed = false;
            return "Pay";
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    private void navigateBack() {
        mainFrame.setSize(800, 800);
        mainFrame.getContentPane().removeAll();
        mainFrame.add(new AppView(mainFrame, customer));
        mainFrame.revalidate();
        mainFrame.repaint();
    }
}