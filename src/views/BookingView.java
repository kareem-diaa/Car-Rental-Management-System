package views;

import controllers.BookingController;
import controllers.CarController;
import controllers.DiscountController;
import models.Booking;
import models.Car;
import models.Customer;
import models.Discount;
import utils.AppColors;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import car_rental.Main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookingView extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(BookingView.class.getName());
    private final BookingController bookingController;
    private final CarController carController;
    private final Main mainFrame;
    private final Customer customer;
    private JTable bookingTable;
    private DefaultTableModel tableModel;
    private JComboBox<Integer> carComboBox;
    private JDatePickerImpl startDatePicker;
    private JDatePickerImpl endDatePicker;
    private JTextField discountField;
    private JLabel rentalPriceLabel;
    private JLabel daysLabel;
    private JLabel totalPriceLabel;
    private JTextField promoCodeField;
    private JButton applyPromoButton;
    private double appliedPromoDiscount = 0.0;

    public BookingView(Main mainFrame, Customer customer) {
        this.mainFrame = mainFrame;
        this.customer = customer;
        this.bookingController = new BookingController();
        this.carController = new CarController();
        setLayout(new BorderLayout(10, 10));
        setBackground(AppColors.MAIN_BG);

        // Ensure UI creation happens on EDT
        SwingUtilities.invokeLater(() -> {
            add(createHeaderPanel(), BorderLayout.NORTH);
            add(createCenterPanel(), BorderLayout.CENTER);
            add(createFooterPanel(), BorderLayout.SOUTH);

            loadBookings();
            mainFrame.setSize(800, 600);
            mainFrame.revalidate();
            mainFrame.repaint();
        });
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

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(AppColors.MAIN_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel formPanel = createBookingFormPanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        centerPanel.add(formPanel, gbc);

        JPanel tablePanel = createTablePanel();
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        centerPanel.add(tablePanel, gbc);

        return centerPanel;
    }

    private JPanel createBookingFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(AppColors.MAIN_BG);
        formPanel.setBorder(BorderFactory.createTitledBorder("Create New Booking"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Car selection
        JLabel carLabel = new JLabel("Select Car:");
        carLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(carLabel, gbc);

        carComboBox = new JComboBox<>();
        carComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        carComboBox.addActionListener(_ -> updatePriceDisplay());
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(carComboBox, gbc);

        // Start date
        JLabel startDateLabel = new JLabel("Start Date:");
        startDateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(startDateLabel, gbc);

        startDatePicker = createDatePicker();
        if (startDatePicker != null) {
            startDatePicker.getModel().addChangeListener(_ -> updatePriceDisplay());
        }
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(startDatePicker, gbc);

        // End date
        JLabel endDateLabel = new JLabel("End Date:");
        endDateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(endDateLabel, gbc);

        endDatePicker = createDatePicker();
        if (endDatePicker != null) {
            endDatePicker.getModel().addChangeListener(_ -> updatePriceDisplay());
        }
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(endDatePicker, gbc);

        // Discount
        JLabel discountLabel = new JLabel("Discount (%):");
        discountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(discountLabel, gbc);

        discountField = new JTextField("0");
        discountField.setFont(new Font("Arial", Font.PLAIN, 14));
        discountField.setToolTipText("Enter discount percentage (0-100)");
        discountField.setEditable(false);
        // discountField.addKeyListener(new KeyAdapter() {
        //     @Override
        //     public void keyReleased(KeyEvent e) {
        //         updatePriceDisplay();
        //     }
        // });
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(discountField, gbc);

        // Promo code (add this block)
        JLabel promoLabel = new JLabel("Promo Code:");
        promoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        formPanel.add(promoLabel, gbc);

        promoCodeField = new JTextField();
        promoCodeField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(promoCodeField, gbc);

        applyPromoButton = new JButton("Apply");
        styleButton(applyPromoButton);
        applyPromoButton.addActionListener(_ -> applyPromoCode());
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        formPanel.add(applyPromoButton, gbc);

        // Price details (increment all gbc.gridy by +1)
        JLabel rentalPriceTitleLabel = new JLabel("Rental Price (per day):");
        rentalPriceTitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        formPanel.add(rentalPriceTitleLabel, gbc);

        rentalPriceLabel = new JLabel("$0.00");
        rentalPriceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(rentalPriceLabel, gbc);

        JLabel daysTitleLabel = new JLabel("Number of Days:");
        daysTitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.0;
        formPanel.add(daysTitleLabel, gbc);

        daysLabel = new JLabel("0");
        daysLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(daysLabel, gbc);

        JLabel totalPriceTitleLabel = new JLabel("Total Price:");
        totalPriceTitleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0.0;
        formPanel.add(totalPriceTitleLabel, gbc);

        totalPriceLabel = new JLabel("$0.00");
        totalPriceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(totalPriceLabel, gbc);

        // Submit button
        JButton submitButton = new JButton("Book Now");
        styleButton(submitButton);
        submitButton.addActionListener(_ -> createBooking());
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.CENTER;
        formPanel.add(submitButton, gbc);

        // Load available cars after all components are initialized
        loadAvailableCars();

        return formPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(AppColors.MAIN_BG);

        tableModel = new DefaultTableModel(new Object[] {
                "Booking ID", "Car ID", "Start Date", "End Date", "Status", "Discount", "Total Price"
        }, 0);
        bookingTable = new JTable(tableModel);
        bookingTable.setFillsViewportHeight(true);
        bookingTable.setRowHeight(30);
        bookingTable.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(bookingTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Your Bookings"));
        scrollPane.setPreferredSize(new Dimension(700, 300));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footerPanel.setBackground(AppColors.ACCENT_TIFFANY);

        JButton cancelBookingButton = new JButton("Cancel Booking");
        styleButton(cancelBookingButton);
        cancelBookingButton.addActionListener(_ -> cancelBooking());
        footerPanel.add(cancelBookingButton);

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

    private JDatePickerImpl createDatePicker() {
        try {
            UtilDateModel model = new UtilDateModel();
            Properties p = new Properties();
            p.put("text.today", "Today");
            p.put("text.month", "Month");
            p.put("text.year", "Year");
            JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
            JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
            datePicker.setFont(new Font("Arial", Font.PLAIN, 14));
            return datePicker;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to create JDatePicker", e);
            JOptionPane.showMessageDialog(this, "Error initializing date picker.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void loadAvailableCars() {
        carComboBox.removeAllItems();
        List<Car> cars = carController.getAllCars();
        if (cars != null) {
            for (Car car : cars) {
                if (car.getAvailabilityStatus()) {
                    carComboBox.addItem(car.getCarID());
                }
            }
        }
        if (carComboBox.getItemCount() == 0) {
            carComboBox.addItem(null);
            carComboBox.setEnabled(false);
        }
        updatePriceDisplay();
    }

    private void loadBookings() {
        tableModel.setRowCount(0);
        List<Booking> bookings = bookingController.getBookingsByUserId(customer.getCustomerId());
        if (bookings == null || bookings.isEmpty()) {
            tableModel.addRow(new Object[] { "No bookings found", "", "", "", "", "", "" });
            bookingTable.setEnabled(false);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (Booking booking : bookings) {
                Car car = carController.getCarById(booking.getCarId());
                float rentalPrice = car != null ? car.getRentalPrice() : 0.0f;
                long diffInMillies = Math.abs(booking.getEndDate().getTime() - booking.getStartDate().getTime());
                long days = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) + 1;
                float discount = 0.0f; // Default to 0 if Booking doesn't support getDiscount()
                // Uncomment if Booking model has getDiscount()
                // float discount = booking.getDiscount();
                float totalPrice = rentalPrice * days * (1 - discount / 100);

                tableModel.addRow(new Object[] {
                        booking.getBookingId(),
                        booking.getCarId(),
                        sdf.format(booking.getStartDate()),
                        sdf.format(booking.getEndDate()),
                        booking.getStatus(),
                        String.format("%.2f%%", discount),
                        String.format("$%.2f", totalPrice)
                });
            }
            bookingTable.setEnabled(true);
        }
    }

    private void updatePriceDisplay() {
        if (carComboBox == null || startDatePicker == null || endDatePicker == null || discountField == null ||
                rentalPriceLabel == null || daysLabel == null || totalPriceLabel == null) {
            return; // Prevent premature calls during initialization
        }

        Integer carId = (Integer) carComboBox.getSelectedItem();
        Date startDate = (Date) startDatePicker.getModel().getValue();
        Date endDate = (Date) endDatePicker.getModel().getValue();
        String discountText = discountField.getText();

        float rentalPrice = 0.0f;
        long days = 0;
        float discount = 0.0f;
        float totalPrice = 0.0f;

        if (carId != null) {
            Car car = carController.getCarById(carId);
            if (car != null) {
                rentalPrice = car.getRentalPrice();
            }
        }

        if (startDate != null && endDate != null && !endDate.before(startDate)) {
            long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
            days = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) + 1;
        }

        try {
            discount = Float.parseFloat(discountText);
            if (discount < 0 || discount > 100) {
                discount = 0;
                discountField.setText("0");
            }
        } catch (NumberFormatException e) {
            discount = 0;
            discountField.setText("0");
        }

        totalPrice = rentalPrice * days * (1 - discount / 100);

        rentalPriceLabel.setText(String.format("$%.2f", rentalPrice));
        daysLabel.setText(String.valueOf(days));
        totalPriceLabel.setText(String.format("$%.2f", totalPrice));
    }

    private void createBooking() {
        Integer carId = (Integer) carComboBox.getSelectedItem();
        Date startDate = (Date) startDatePicker.getModel().getValue();
        Date endDate = (Date) endDatePicker.getModel().getValue();
        String discountText = discountField.getText();

        if (carId == null || startDate == null || endDate == null) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (endDate.before(startDate)) {
            JOptionPane.showMessageDialog(this, "End date must be after start date.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        float discount;
        try {
            discount = Float.parseFloat(discountText);
            if (discount < 0 || discount > 100) {
                JOptionPane.showMessageDialog(this, "Discount must be between 0 and 100.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid discount value.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Car car = carController.getCarById(carId);
        if (car == null) {
            JOptionPane.showMessageDialog(this, "Selected car is not available.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
        long days = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) + 1;
        float totalPrice = car.getRentalPrice() * days * (1 - discount / 100);

        boolean success = bookingController.addBooking(
                customer.getCustomerId(),
                carId,
                "Pending",
                startDate,
                endDate
        // , discount, totalPrice // Uncomment if Booking model supports these
        );

        if (success) {
            JOptionPane.showMessageDialog(this,
                    String.format("Booking created successfully! Total Price: $%.2f", totalPrice), "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadBookings();
            loadAvailableCars();
            discountField.setText("0");
            updatePriceDisplay();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create booking.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelBooking() {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel booking ID " + bookingId + "?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = bookingController.deleteBooking(bookingId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Booking cancelled successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadBookings();
                loadAvailableCars();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel booking.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void applyPromoCode() {
        String promoCode = promoCodeField.getText().trim();
        if (promoCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a promo code.", "Error", JOptionPane.ERROR_MESSAGE);
            appliedPromoDiscount = 0.0;
            updatePriceDisplay();
            return;
        }
        DiscountController discountController = new DiscountController();
        Discount discount = discountController.getAllDiscounts().stream()
                .filter(d -> promoCode.equalsIgnoreCase(d.getPromotionCode()))
                .findFirst()
                .orElse(null);
        if (discount != null) {
            appliedPromoDiscount = discount.getDiscountPercentage();
            discountField.setText(String.valueOf(appliedPromoDiscount));
            JOptionPane.showMessageDialog(this, "Promo applied: " + appliedPromoDiscount + "% off!", "Promo Applied",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            appliedPromoDiscount = 0.0;
            discountField.setText("0");
            JOptionPane.showMessageDialog(this, "Invalid promo code.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        updatePriceDisplay();
    }

    private void navigateBack() {
        mainFrame.setSize(800, 800);
        mainFrame.getContentPane().removeAll();
        mainFrame.add(new AppView(mainFrame, customer));
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    private static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        @Override
        public Object stringToValue(String text) throws java.text.ParseException {
            return dateFormatter.parse(text);
        }

        @Override
        public String valueToString(Object value) {
            if (value instanceof java.util.Calendar) {
                return dateFormatter.format(((java.util.Calendar) value).getTime());
            } else if (value instanceof Date) {
                return dateFormatter.format(value);
            }
            return "";
        }
    }
}