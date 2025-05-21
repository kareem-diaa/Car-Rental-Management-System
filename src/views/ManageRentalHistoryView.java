package views;

import controllers.RentalHistoryController;
import dao.BookingDAO;
import dao.CustomerDAO;
import models.RentalHistory;
import models.Booking;
import models.Customer;
import utils.AppColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import car_rental.Main;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageRentalHistoryView extends JPanel {
    private final RentalHistoryController rentalHistoryController;
    private JTable rentalHistoryTable;
    private DefaultTableModel tableModel;
    private final Main mainFrame;

    // Map userId to username for quick lookup
    private Map<Integer, String> userIdToUsername = new HashMap<>();

    public ManageRentalHistoryView(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.rentalHistoryController = new RentalHistoryController();

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

        loadUserMap();
        loadRentalHistories();
        mainFrame.setSize(900, 600);
    }

    private void loadUserMap() {
        userIdToUsername.clear();
        List<Customer> users = new CustomerDAO().getAllCustomers();
        for (Customer user : users) {
            userIdToUsername.put(user.getCustomerId(), user.getUsername());
        }
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(AppColors.ACCENT_TIFFANY);
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Rental History Management", SwingConstants.CENTER);
        titleLabel.setForeground(AppColors.LIGHT_TEXT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(AppColors.MAIN_BG);
        tableModel = new DefaultTableModel(new Object[]{
                "ID", "Username", "Booking ID", "Return Date", "Extra Charges", "Comments", "Created At", "Updated At"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        rentalHistoryTable = new JTable(tableModel);
        rentalHistoryTable.setFillsViewportHeight(true);
        rentalHistoryTable.setRowHeight(30);
        rentalHistoryTable.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(rentalHistoryTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Rental History Records"));
        scrollPane.setPreferredSize(new Dimension(800, 400));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footerPanel.setBackground(AppColors.ACCENT_TIFFANY);

        JButton addButton = new JButton("Add Rental History");
        styleButton(addButton);
        addButton.addActionListener(_ -> showAddRentalHistoryDialog());
        footerPanel.add(addButton);

        JButton updateButton = new JButton("Update Rental History");
        styleButton(updateButton);
        updateButton.addActionListener(_ -> showUpdateRentalHistoryDialog());
        footerPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete Rental History");
        styleButton(deleteButton);
        deleteButton.setBackground(AppColors.ERROR_RED);
        deleteButton.addActionListener(_ -> deleteSelectedRentalHistory());
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

    private void loadRentalHistories() {
        tableModel.setRowCount(0);
        List<RentalHistory> histories = rentalHistoryController.getAllRentalHistories();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        boolean found = false;
        for (RentalHistory rh : histories) {
            String username = userIdToUsername.getOrDefault(rh.getCustomerId(), "Unknown");
            tableModel.addRow(new Object[]{
                    rh.getRentalId(),
                    username,
                    rh.getBookingId(),
                    rh.getReturnDate() != null ? rh.getReturnDate().format(dtf) : "",
                    rh.getExtraCharges(),
                    rh.getComments(),
                    rh.getCreatedAt() != null ? rh.getCreatedAt().toString() : "",
                    rh.getUpdatedAt() != null ? rh.getUpdatedAt().toString() : ""
            });
            found = true;
        }
        if (!found) {
            tableModel.addRow(new Object[]{"No records found", "", "", "", "", "", "", ""});
            rentalHistoryTable.setEnabled(false);
        } else {
            rentalHistoryTable.setEnabled(true);
        }
    }

    private void showAddRentalHistoryDialog() {
        // User ComboBox
        JComboBox<String> userComboBox = new JComboBox<>();
        List<Customer> users = new CustomerDAO().getAllCustomers();
        for (Customer user : users) {
            userComboBox.addItem(user.getCustomerId() + " - " + user.getUsername());
        }

        // Booking ComboBox
        JComboBox<String> bookingComboBox = new JComboBox<>();

        // Populate bookings when user changes
        userComboBox.addActionListener(_ -> {
            bookingComboBox.removeAllItems();
            if (userComboBox.getSelectedItem() != null) {
                int selectedUserId = Integer.parseInt(userComboBox.getSelectedItem().toString().split(" - ")[0]);
                List<Booking> bookings = new BookingDAO().getBookingsByUserId(selectedUserId);
                for (Booking booking : bookings) {
                    bookingComboBox.addItem(booking.getBookingId() + " - " + booking.getStartDate() + " to " + booking.getEndDate());
                }
            }
        });
        if (userComboBox.getItemCount() > 0) userComboBox.setSelectedIndex(0);

        // Return Date Picker (JSpinner)
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));

        JTextField extraChargesField = new JTextField();
        JTextArea commentsArea = new JTextArea(3, 20);
        JScrollPane commentsScroll = new JScrollPane(commentsArea);

        Object[] fields = {
                "User:", userComboBox,
                "Booking:", bookingComboBox,
                "Return Date:", dateSpinner,
                "Extra Charges:", extraChargesField,
                "Comments:", commentsScroll
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Add Rental History", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int userId = Integer.parseInt(userComboBox.getSelectedItem().toString().split(" - ")[0]);
                int bookingId = Integer.parseInt(bookingComboBox.getSelectedItem().toString().split(" - ")[0]);
                Date selectedDate = (Date) dateSpinner.getValue();
                LocalDate returnDate = selectedDate != null ? selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
                BigDecimal extraCharges = new BigDecimal(extraChargesField.getText());
                String comments = commentsArea.getText();

                RentalHistory rh = new RentalHistory(0, userId, bookingId, returnDate, extraCharges, comments, null, null);
                boolean success = rentalHistoryController.addRentalHistory(rh);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Rental history added successfully!");
                    loadUserMap();
                    loadRentalHistories();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add rental history.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + e.getMessage());
            }
        }
    }

    private void showUpdateRentalHistoryDialog() {
        int selectedRow = rentalHistoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a rental history record to update.");
            return;
        }
        int rentalId = (int) tableModel.getValueAt(selectedRow, 0);
        RentalHistory rh = rentalHistoryController.getRentalHistoryById(rentalId);
        if (rh == null) {
            JOptionPane.showMessageDialog(this, "Rental history record not found.");
            return;
        }

        // User ComboBox
        JComboBox<String> userComboBox = new JComboBox<>();
        List<Customer> users = new CustomerDAO().getAllCustomers();
        int userIndex = 0, userSelectedIndex = 0;
        for (Customer user : users) {
            userComboBox.addItem(user.getCustomerId() + " - " + user.getUsername());
            if (user.getCustomerId() == rh.getCustomerId()) userSelectedIndex = userIndex;
            userIndex++;
        }
        userComboBox.setSelectedIndex(userSelectedIndex);

        // Booking ComboBox
        JComboBox<String> bookingComboBox = new JComboBox<>();
        int selectedUserId = rh.getCustomerId();
        List<Booking> bookings = new BookingDAO().getBookingsByUserId(selectedUserId);
        int bookingIndex = 0, bookingSelectedIndex = 0;
        for (Booking booking : bookings) {
            bookingComboBox.addItem(booking.getBookingId() + " - " + booking.getStartDate() + " to " + booking.getEndDate());
            if (booking.getBookingId() == rh.getBookingId()) bookingSelectedIndex = bookingIndex;
            bookingIndex++;
        }
        bookingComboBox.setSelectedIndex(bookingSelectedIndex);

        // Update bookings when user changes
        userComboBox.addActionListener(_ -> {
            bookingComboBox.removeAllItems();
            int newUserId = Integer.parseInt(userComboBox.getSelectedItem().toString().split(" - ")[0]);
            List<Booking> newBookings = new BookingDAO().getBookingsByUserId(newUserId);
            for (Booking booking : newBookings) {
                bookingComboBox.addItem(booking.getBookingId() + " - " + booking.getStartDate() + " to " + booking.getEndDate());
            }
        });

        // Return Date Picker (JSpinner)
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        if (rh.getReturnDate() != null) {
            Date d = Date.from(rh.getReturnDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
            dateSpinner.setValue(d);
        }

        JTextField extraChargesField = new JTextField(rh.getExtraCharges() != null ? rh.getExtraCharges().toString() : "");
        JTextArea commentsArea = new JTextArea(rh.getComments(), 3, 20);
        JScrollPane commentsScroll = new JScrollPane(commentsArea);

        Object[] fields = {
                "User:", userComboBox,
                "Booking:", bookingComboBox,
                "Return Date:", dateSpinner,
                "Extra Charges:", extraChargesField,
                "Comments:", commentsScroll
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Update Rental History", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int userId = Integer.parseInt(userComboBox.getSelectedItem().toString().split(" - ")[0]);
                int bookingId = Integer.parseInt(bookingComboBox.getSelectedItem().toString().split(" - ")[0]);
                Date selectedDate = (Date) dateSpinner.getValue();
                LocalDate returnDate = selectedDate != null ? selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
                BigDecimal extraCharges = new BigDecimal(extraChargesField.getText());
                String comments = commentsArea.getText();

                RentalHistory updatedRh = new RentalHistory(rh.getRentalId(), userId, bookingId, returnDate, extraCharges, comments, rh.getCreatedAt(), rh.getUpdatedAt());
                boolean success = rentalHistoryController.updateRentalHistory(updatedRh);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Rental history updated successfully!");
                    loadUserMap();
                    loadRentalHistories();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update rental history.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + e.getMessage());
            }
        }
    }

    private void deleteSelectedRentalHistory() {
        int selectedRow = rentalHistoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a rental history record to delete.");
            return;
        }
        int rentalId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this rental history record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = rentalHistoryController.deleteRentalHistory(rentalId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Rental history deleted successfully!");
                loadUserMap();
                loadRentalHistories();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete rental history.");
            }
        }
    }

}