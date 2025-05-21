package views;

import controllers.BrandController;
import controllers.CarModelController;
import models.CarBrand;
import models.CarModel;
import utils.AppColors;
import utils.ValidationException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import car_rental.Main;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageCarModelView extends JPanel {
    private final BrandController brandController;
    private JTable carModelTable;
    private DefaultTableModel tableModel;
    private final Main mainFrame;
    private Map<String, Integer> brandNameToIdMap;
    private CarModelController carModelController;

    public ManageCarModelView(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.carModelController = new CarModelController();
        this.brandController = new BrandController();
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

        loadCarModels();
        mainFrame.setSize(800, 600);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(AppColors.ACCENT_TIFFANY);
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Car Model Management", SwingConstants.CENTER);
        titleLabel.setForeground(AppColors.LIGHT_TEXT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(AppColors.MAIN_BG);
        tableModel = new DefaultTableModel(new Object[] { "ID", "Brand", "Model", "Fuel Type" }, 0);
        carModelTable = new JTable(tableModel);
        carModelTable.setFillsViewportHeight(true);
        carModelTable.setRowHeight(30);
        carModelTable.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(carModelTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Car Models"));
        scrollPane.setPreferredSize(new Dimension(700, 400));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footerPanel.setBackground(AppColors.ACCENT_TIFFANY);

        JButton addButton = new JButton("Add Car Model");
        styleButton(addButton);
        addButton.addActionListener(_ -> {
            try {
                showAddCarModelDialog();
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        });
        footerPanel.add(addButton);

        JButton updateButton = new JButton("Update Car Model");
        styleButton(updateButton);
        updateButton.addActionListener(_ -> {
            try {
                showUpdateCarModelDialog();
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        });
        footerPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete Car Model");
        styleButton(deleteButton);
        deleteButton.setBackground(AppColors.ERROR_RED);
        deleteButton.addActionListener(_ -> deleteSelectedCarModel());
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

    private void loadCarModels() {
        tableModel.setRowCount(0);
        List<CarModel> carModels = carModelController.getAllCarModels();
        List<CarBrand> brands = brandController.getAllBrands();
        brandNameToIdMap = new HashMap<>();
        for (CarBrand brand : brands) {
            brandNameToIdMap.put(brand.getBrandName(), brand.getBrandId());
        }
        if (carModels == null || carModels.isEmpty()) {
            tableModel.addRow(new Object[] { "No car models found", "", "", "" });
            carModelTable.setEnabled(false);
        } else {
            for (CarModel carModel : carModels) {
                String brandName = brands.stream()
                        .filter(b -> b.getBrandId() == carModel.getBrandId())
                        .map(CarBrand::getBrandName)
                        .findFirst()
                        .orElse("Unknown");
                tableModel.addRow(new Object[] {
                        carModel.getModelId(),
                        brandName,
                        carModel.getModelName(),
                        carModel.getFuelType()
                });
            }
            carModelTable.setEnabled(true);
        }
    }

    private void showAddCarModelDialog() throws ValidationException {
        List<CarBrand> brands = brandController.getAllBrands();
        if (brands == null || brands.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No brands available. Please add a brand first.");
            return;
        }
        String[] brandNames = brands.stream().map(CarBrand::getBrandName).toArray(String[]::new);
        JComboBox<String> brandComboBox = new JComboBox<>(brandNames);
        JTextField modelNameField = new JTextField();
        String[] fuelTypes = { "Petrol", "Diesel", "Electric", "Hybrid" };
        JComboBox<String> fuelTypeComboBox = new JComboBox<>(fuelTypes);
        Object[] fields = {
                "Brand:", brandComboBox,
                "Model Name:", modelNameField,
                "Fuel Type:", fuelTypeComboBox
        };
        int option = JOptionPane.showConfirmDialog(this, fields, "Add Car Model", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String selectedBrandName = (String) brandComboBox.getSelectedItem();
            int brandId = brandNameToIdMap.getOrDefault(selectedBrandName, -1);
            if (brandId == -1) {
                JOptionPane.showMessageDialog(this, "Invalid brand selected.");
                return;
            }
            boolean success = carModelController.addCarModel(
                    brandId,
                    modelNameField.getText(),
                    (String) fuelTypeComboBox.getSelectedItem());
            if (success) {
                JOptionPane.showMessageDialog(this, "Car model added successfully!");
                loadCarModels();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add car model.");
            }
        }
    }

    private void showUpdateCarModelDialog() throws ValidationException {
        int selectedRow = carModelTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a car model to update.");
            return;
        }
        int modelId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentBrandName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentModelName = (String) tableModel.getValueAt(selectedRow, 2);
        String currentFuelType = (String) tableModel.getValueAt(selectedRow, 3);

        List<CarBrand> brands = brandController.getAllBrands();
        if (brands == null || brands.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No brands available. Please add a brand first.");
            return;
        }
        String[] brandNames = brands.stream().map(CarBrand::getBrandName).toArray(String[]::new);
        JComboBox<String> brandComboBox = new JComboBox<>(brandNames);
        brandComboBox.setSelectedItem(currentBrandName);
        JTextField modelNameField = new JTextField(currentModelName);
        String[] fuelTypes = { "Petrol", "Diesel", "Electric", "Hybrid" };
        JComboBox<String> fuelTypeComboBox = new JComboBox<>(fuelTypes);
        fuelTypeComboBox.setSelectedItem(currentFuelType);
        Object[] fields = {
                "Brand:", brandComboBox,
                "Model Name:", modelNameField,
                "Fuel Type:", fuelTypeComboBox
        };
        int option = JOptionPane.showConfirmDialog(this, fields, "Update Car Model", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String selectedBrandName = (String) brandComboBox.getSelectedItem();
            int brandId = brandNameToIdMap.getOrDefault(selectedBrandName, -1);
            if (brandId == -1) {
                JOptionPane.showMessageDialog(this, "Invalid brand selected.");
                return;
            }
            boolean success = carModelController.updateCarModel(
                    modelId,
                    brandId,
                    modelNameField.getText(),
                    (String) fuelTypeComboBox.getSelectedItem());
            if (success) {
                JOptionPane.showMessageDialog(this, "Car model updated successfully!");
                loadCarModels();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update car model.");
            }
        }
    }

    private void deleteSelectedCarModel() {
        int selectedRow = carModelTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a car model to delete.");
            return;
        }
        int modelId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this car model?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = carModelController.deleteCarModel(modelId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Car model deleted successfully!");
                loadCarModels();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete car model.");
            }
        }
    }

}