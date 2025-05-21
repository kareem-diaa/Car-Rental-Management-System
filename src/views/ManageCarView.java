package views;

import controllers.CarController;
import controllers.CategoryController;
import dao.CarModelDAO;
import models.Car;
import models.CarModel;
import models.Category;
import utils.AppColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import car_rental.Main;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class ManageCarView extends JPanel {
    private final CarController carController;
    private JTable carTable;
    private DefaultTableModel tableModel;
    private final Main mainFrame;
    private final CarModelDAO carModelDAO;

    public ManageCarView(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.carController = new CarController();
        this.carModelDAO = new CarModelDAO();
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
        JLabel titleLabel = new JLabel("Car Management", SwingConstants.CENTER);
        titleLabel.setForeground(AppColors.LIGHT_TEXT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(AppColors.MAIN_BG);
        tableModel = new DefaultTableModel(new Object[] {
                "ID", "Model",  "Name" , "Category", "Mileage", "Availability",
                "Rental Price", "Fuel Type", "Plate No", "Image URL"
        }, 0);
        carTable = new JTable(tableModel);
        carTable.setFillsViewportHeight(true);
        carTable.setRowHeight(30);
        carTable.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(carTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Cars"));
        scrollPane.setPreferredSize(new Dimension(700, 400));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footerPanel.setBackground(AppColors.ACCENT_TIFFANY);

        JButton addButton = new JButton("Add Car");
        styleButton(addButton);
        addButton.addActionListener(_ -> showAddCarDialog());
        footerPanel.add(addButton);

        JButton updateButton = new JButton("Update Car");
        styleButton(updateButton);
        updateButton.addActionListener(_ -> showUpdateCarDialog());
        footerPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete Car");
        styleButton(deleteButton);
        deleteButton.setBackground(AppColors.ERROR_RED);
        deleteButton.addActionListener(_ -> deleteSelectedCar());
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

    private void loadCars() {
        tableModel.setRowCount(0);
        List<Car> cars = carController.getAllCars();
        if (cars == null || cars.isEmpty()) {
            tableModel.addRow(new Object[] { "No cars found", "", "", "", "", "", "", "", "", "" });
            carTable.setEnabled(false);
        } else {
            for (Car car : cars) {
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

    private JComboBox<String> loadCategoryOptions() {
        JComboBox<String> categoryComboBox = new JComboBox<>();
        List<Category> categories = new CategoryController().getAllCategories();
        if (categories != null && !categories.isEmpty()) {
            for (Category category : categories) {
                categoryComboBox.addItem(category.getCategoryID() + " - " + category.getName());
            }
        } else {
            categoryComboBox.addItem("No categories available");
            categoryComboBox.setEnabled(false);
        }
        return categoryComboBox;
    }

    private JComboBox<String> loadCarModelOptions() {
        JComboBox<String> carModelComboBox = new JComboBox<>();
        List<CarModel> carModels = carModelDAO.getAllCarModels();
        if (carModels != null && !carModels.isEmpty()) {
            for (CarModel carModel : carModels) {
                carModelComboBox
                        .addItem(carModel.getModelId() + " - " + carModel.getBrandId() + " " + carModel.getModelId());
            }
        } else {
            carModelComboBox.addItem("No car models available");
            carModelComboBox.setEnabled(false);
        }
        return carModelComboBox;
    }

    private void showAddCarDialog() {
        JComboBox<String> carModelComboBox = loadCarModelOptions();
        JComboBox<String> categoryComboBox = loadCategoryOptions();
        JTextField mileageField = new JTextField();
        JComboBox<Boolean> availabilityComboBox = new JComboBox<>(new Boolean[] { true, false });
        JTextField rentalPriceField = new JTextField();
        JComboBox<String> fuelTypeComboBox = new JComboBox<>(new String[] { "Petrol", "Diesel", "Electric", "Hybrid" });
        JTextField plateNoField = new JTextField();
        JTextField imageURLField = new JTextField();

        Object[] fields = {
                "Car Model:", carModelComboBox,
                "Category:", categoryComboBox,
                "Mileage:", mileageField,
                "Availability:", availabilityComboBox,
                "Rental Price:", rentalPriceField,
                "Fuel Type:", fuelTypeComboBox,
                "Plate Number:", plateNoField,
                "Image URL:", imageURLField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Add Car", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String selectedCarModel = (String) carModelComboBox.getSelectedItem();
                int carModelId = Integer.parseInt(selectedCarModel.split(" - ")[0]);

                String selectedCategory = (String) categoryComboBox.getSelectedItem();
                int categoryId = Integer.parseInt(selectedCategory.split(" - ")[0]);

                Car car = new Car();
                car.setModelID(carModelId);
                car.setCategoryID(categoryId);
                car.setMileage(Integer.parseInt(mileageField.getText()));
                car.setAvailabilityStatus((Boolean) availabilityComboBox.getSelectedItem());
                car.setRentalPrice(Float.parseFloat(rentalPriceField.getText()));
                car.setPlateNo(plateNoField.getText());
                car.setImageURL(imageURLField.getText());
                car.setCreatedAt(LocalDateTime.now());
                car.setUpdatedAt(LocalDateTime.now());

                boolean success = carController.addCar(car);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Car added successfully!");
                    loadCars();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add car.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showUpdateCarDialog() {
        int selectedRow = carTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a car to update.");
            return;
        }

        int carId = (int) tableModel.getValueAt(selectedRow, 0);
        Car car = carController.getCarById(carId);
        if (car == null) {
            JOptionPane.showMessageDialog(this, "Car not found.");
            return;
        }

        JComboBox<String> carModelComboBox = loadCarModelOptions();
        JComboBox<String> categoryComboBox = loadCategoryOptions();
        JTextField mileageField = new JTextField(String.valueOf(car.getMileage()));
        JComboBox<Boolean> availabilityComboBox = new JComboBox<>(new Boolean[] { true, false });
        availabilityComboBox.setSelectedItem(car.getAvailabilityStatus());
        JTextField rentalPriceField = new JTextField(String.valueOf(car.getRentalPrice()));
        JTextField plateNoField = new JTextField(car.getPlateNo());
        JTextField imageURLField = new JTextField(car.getImageURL());

        Object[] fields = {
                "Car Model:", carModelComboBox,
                "Category:", categoryComboBox,
                "Mileage:", mileageField,
                "Availability:", availabilityComboBox,
                "Rental Price:", rentalPriceField,
                "Plate Number:", plateNoField,
                "Image URL:", imageURLField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Update Car", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String selectedCarModel = (String) carModelComboBox.getSelectedItem();
                int carModelId = Integer.parseInt(selectedCarModel.split(" - ")[0]);

                String selectedCategory = (String) categoryComboBox.getSelectedItem();
                int categoryId = Integer.parseInt(selectedCategory.split(" - ")[0]);

                car.setModelID(carModelId);
                car.setCategoryID(categoryId);
                car.setMileage(Integer.parseInt(mileageField.getText()));
                car.setAvailabilityStatus((Boolean) availabilityComboBox.getSelectedItem());
                car.setRentalPrice(Float.parseFloat(rentalPriceField.getText()));
                car.setPlateNo(plateNoField.getText());
                car.setImageURL(imageURLField.getText());
                car.setUpdatedAt(LocalDateTime.now());

                boolean success = carController.updateCar(car);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Car updated successfully!");
                    loadCars();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update car.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
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

    private void deleteSelectedCar() {
        int selectedRow = carTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a car to delete.");
            return;
        }
        int carId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this car?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = carController.deleteCar(carId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Car deleted successfully!");
                loadCars();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete car.");
            }
        }
    }

}