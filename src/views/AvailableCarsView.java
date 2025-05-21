package views;

import controllers.CarController;
import controllers.CategoryController;
import dao.CarModelDAO;
import models.Car;
import models.CarModel;
import models.Category;
import models.Customer;
import utils.AppColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import car_rental.Main;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class AvailableCarsView extends JPanel {
    private final CarController carController;
    private JTable carTable;
    private DefaultTableModel tableModel;
    private final Main mainFrame;
    private final CarModelDAO carModelDAO;
    private final Customer customer;

    public AvailableCarsView(Main mainFrame, Customer customer) {
        this.mainFrame = mainFrame;
        this.carController = new CarController();
        this.carModelDAO = new CarModelDAO();
        this.customer = customer;
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

        loadCars();
        mainFrame.setSize(800, 600);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(AppColors.ACCENT_TIFFANY);
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Available Cars", SwingConstants.CENTER);
        titleLabel.setForeground(AppColors.LIGHT_TEXT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(AppColors.MAIN_BG);
        tableModel = new DefaultTableModel(new Object[] {
                "ID", "Model", "Name", "Category", "Mileage", "Availability",
                "Rental Price", "Fuel Type", "Plate No", "Image URL"
        }, 0);
        carTable = new JTable(tableModel);
        carTable.setFillsViewportHeight(true);
        carTable.setRowHeight(30);
        carTable.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(carTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Available Cars"));
        scrollPane.setPreferredSize(new Dimension(700, 400));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footerPanel.setBackground(AppColors.ACCENT_TIFFANY);

        JButton bookButton = new JButton("Book Car");
        styleButton(bookButton);
        bookButton.addActionListener(_ -> selectCarForBooking());
        footerPanel.add(bookButton);

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

    private void loadCars() {
        tableModel.setRowCount(0);
        List<Car> cars = carController.getAllCars();
        if (cars == null || cars.isEmpty()) {
            tableModel.addRow(new Object[] { "No cars found", "", "", "", "", "", "", "", "", "" });
            carTable.setEnabled(false);
        } else {
            List<Car> availableCars = cars.stream()
                    .filter(car -> true == car.getAvailabilityStatus())
                    .collect(Collectors.toList());
            if (availableCars.isEmpty()) {
                tableModel.addRow(new Object[] { "No available cars", "", "", "", "", "", "", "", "", "" });
                carTable.setEnabled(false);
            } else {
                for (Car car : availableCars) {
                    CarModel carModel = carModelDAO.getCarModelById(car.getModelID());
                    int brand = carModel != null ? carModel.getBrandId() : 0;
                    int model = carModel != null ? carModel.getModelId() : 0;
                    String categoryName = getCategoryNameById(car.getCategoryID());
                    tableModel.addRow(new Object[] {
                            car.getCarID(),
                            brand,
                            model,
                            categoryName,
                            car.getMileage(),
                            car.getAvailabilityStatus(),
                            car.getRentalPrice(),
                            carModel.getFuelType(),
                            car.getPlateNo(),
                            car.getImageURL()
                    });
                }
                carTable.setEnabled(true);
            }
        }
    }

    private String getCategoryNameById(int categoryId) {
        List<Category> categories = new CategoryController().getAllCategories();
        for (Category category : categories) {
            if (category.getCategoryID() == categoryId) {
                return category.getName();
            }
        }
        return "Unknown";
    }

    private void selectCarForBooking() {
        int selectedRow = carTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a car to book.");
            return;
        }

        int carId = (int) tableModel.getValueAt(selectedRow, 0);
        Car car = carController.getCarById(carId);
        if (car == null || false == car.getAvailabilityStatus()) {
            JOptionPane.showMessageDialog(this, "Selected car is not available.");
            return;
        }

        // Navigate to BookingView
        mainFrame.getContentPane().removeAll();
        mainFrame.add(new BookingView(mainFrame, customer));
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    private void navigateBack() {
        mainFrame.setSize(800, 800);
        mainFrame.getContentPane().removeAll();
        mainFrame.add(new AppView(mainFrame, customer));
        mainFrame.revalidate();
        mainFrame.repaint();
    }
}