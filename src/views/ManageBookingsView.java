package views;

import controllers.BookingController;
import models.Booking;
import utils.AppColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import car_rental.Main;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class ManageBookingsView extends JPanel {
    private final BookingController bookingController;
    private final Main mainFrame; // Use Main instead of JFrame
    private JTable bookingTable;
    private DefaultTableModel tableModel;

    public ManageBookingsView(Main mainFrame) { // Accept Main as a parameter
        this.mainFrame = mainFrame;
        this.bookingController = new BookingController();
        setLayout(new BorderLayout(10, 10));
        setBackground(AppColors.MAIN_BG);

        // Add header and table
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);

        loadBookings();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(AppColors.ACCENT_TIFFANY);
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Manage Bookings", SwingConstants.CENTER);
        titleLabel.setForeground(AppColors.LIGHT_TEXT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(AppColors.MAIN_BG);

        tableModel = new DefaultTableModel(new Object[] {
                "Booking ID", "User ID", "Car ID", "Start Date", "End Date", "Status", "Action"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make only the "Action" column editable
                return column == 6;
            }
        };

        bookingTable = new JTable(tableModel);
        bookingTable.setFillsViewportHeight(true);
        bookingTable.setRowHeight(30);
        bookingTable.setFont(new Font("Arial", Font.PLAIN, 14));

        // Add custom renderer and editor for the "Action" column
        bookingTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        bookingTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(bookingTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Bookings"));
        scrollPane.setPreferredSize(new Dimension(700, 400));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }


    private void loadBookings() {
        tableModel.setRowCount(0);
        List<Booking> bookings = bookingController.getAllBookings();
        if (bookings == null || bookings.isEmpty()) {
            tableModel.addRow(new Object[] { "No bookings found", "", "", "", "", "", "" });
            bookingTable.setEnabled(false);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (Booking booking : bookings) {
                tableModel.addRow(new Object[] {
                        booking.getBookingId(),
                        booking.getCustomerId(),
                        booking.getCarId(),
                        sdf.format(booking.getStartDate()),
                        sdf.format(booking.getEndDate()),
                        booking.getStatus(),
                        "Action" 
                });
            }
            bookingTable.setEnabled(true);
        }
    }

    private void acceptBooking(int bookingId) {
        boolean success = bookingController.updateBookingStatus(bookingId, "Accepted");
        if (success) {
            JOptionPane.showMessageDialog(this, "Booking accepted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadBookings();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to accept booking.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rejectBooking(int bookingId) {
        boolean success = bookingController.updateBookingStatus(bookingId, "Rejected");
        if (success) {
            JOptionPane.showMessageDialog(this, "Booking rejected successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadBookings();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to reject booking.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    // Custom renderer for the "Action" column
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Action");
            return this;
        }
    }

    // Custom editor for the "Action" column
    class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(_ -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = "Action";
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int selectedRow = bookingTable.getSelectedRow();
                int bookingId = (int) tableModel.getValueAt(selectedRow, 0);

                // Show options for Accept or Reject
                int option = JOptionPane.showOptionDialog(
                        mainFrame,
                        "Choose an action for Booking ID " + bookingId,
                        "Action",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[] { "Accept", "Reject" },
                        "Accept"
                );

                if (option == JOptionPane.YES_OPTION) {
                    acceptBooking(bookingId);
                } else if (option == JOptionPane.NO_OPTION) {
                    rejectBooking(bookingId);
                }
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}