package views;

import controllers.CarController;
import controllers.MaintenanceController;
import models.Car;
import models.Maintenance;
import utils.AppColors;
import utils.ValidationException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import car_rental.Main;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ManageMaintenanceView extends JPanel {
    private final MaintenanceController maintenanceController;
    private final CarController carController;
    private JTable maintenanceTable;
    private DefaultTableModel tableModel;
    private final Main mainFrame;

    public ManageMaintenanceView(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.maintenanceController = new MaintenanceController();
        this.carController = new CarController();
        
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

        loadMaintenance();
        mainFrame.setSize(800, 600);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(AppColors.ACCENT_TIFFANY);
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Maintenance Management", SwingConstants.CENTER);
        titleLabel.setForeground(AppColors.LIGHT_TEXT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(AppColors.MAIN_BG);
        tableModel = new DefaultTableModel(new Object[] {
                "ID", "Car ID", "Car Details", "Status", "Cost", "Date", "Details"
        }, 0);
        maintenanceTable = new JTable(tableModel);
        maintenanceTable.setFillsViewportHeight(true);
        maintenanceTable.setRowHeight(30);
        maintenanceTable.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(maintenanceTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Maintenance Records"));
        scrollPane.setPreferredSize(new Dimension(700, 400));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footerPanel.setBackground(AppColors.ACCENT_TIFFANY);

        JButton addButton = new JButton("Add Maintenance Record");
        styleButton(addButton);
        addButton.addActionListener(_ -> showAddMaintenanceDialog());
        footerPanel.add(addButton);

        JButton updateButton = new JButton("Update Maintenance");
        styleButton(updateButton);
        updateButton.addActionListener(_ -> showUpdateMaintenanceDialog());
        footerPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete Maintenance");
        styleButton(deleteButton);
        deleteButton.setBackground(AppColors.ERROR_RED);
        deleteButton.addActionListener(_ -> deleteSelectedMaintenance());
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

    private void loadMaintenance() {
        tableModel.setRowCount(0);
        List<Maintenance> maintenanceList = maintenanceController.getAllMaintenance();
        
        if (maintenanceList == null || maintenanceList.isEmpty()) {
            tableModel.addRow(new Object[] { "No maintenance records found", "", "", "", "", "", "" });
            maintenanceTable.setEnabled(false);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (Maintenance maintenance : maintenanceList) {
                String carDetails = "Unknown";
                Car car = maintenance.getCar();
                
                if (car != null) {
                    carDetails = "Plate: " + car.getPlateNo() + " - ";
                }
                
                tableModel.addRow(new Object[] {
                        maintenance.getMaintenanceID(),
                        maintenance.getCarId(),
                        carDetails,
                        maintenance.getStatus(),
                        maintenance.getCost(),
                        maintenance.getMaintenanceDate().format(formatter),
                        maintenance.getDetails()
                });
            }
            maintenanceTable.setEnabled(true);
        }
    }

    private JComboBox<String> loadCarOptions() {
        JComboBox<String> carComboBox = new JComboBox<>();
        List<Car> cars = carController.getAllCars();
        
        if (cars != null && !cars.isEmpty()) {
            for (Car car : cars) {
                carComboBox.addItem(car.getCarID() + " - " + car.getPlateNo());
            }
        } else {
            carComboBox.addItem("No cars available");
            carComboBox.setEnabled(false);
        }
        
        return carComboBox;
    }

    private void showAddMaintenanceDialog() {
        JComboBox<String> carComboBox = loadCarOptions();
        JComboBox<String> statusComboBox = new JComboBox<>(new String[] { 
            "Pending", "In Progress", "Completed", "Postponed", "Cancelled" 
        });
        JTextField costField = new JTextField();
        JTextArea detailsArea = new JTextArea(5, 20);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        JScrollPane detailsScrollPane = new JScrollPane(detailsArea);

        Object[] fields = {
                "Car:", carComboBox,
                "Status:", statusComboBox,
                "Cost:", costField,
                "Details:", detailsScrollPane
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Add Maintenance Record", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String selectedCar = (String) carComboBox.getSelectedItem();
                if (selectedCar == null || selectedCar.equals("No cars available")) {
                    JOptionPane.showMessageDialog(this, "Please select a valid car.");
                    return;
                }
                
                int carId = Integer.parseInt(selectedCar.split(" - ")[0]);
                String status = (String) statusComboBox.getSelectedItem();
                float cost = Float.parseFloat(costField.getText());
                String details = detailsArea.getText();

                boolean success = maintenanceController.addMaintenance(carId, status, details, cost);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, "Maintenance record added successfully!");
                    loadMaintenance();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add maintenance record.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for cost.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (ValidationException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showUpdateMaintenanceDialog() {
        int selectedRow = maintenanceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a maintenance record to update.");
            return;
        }

        int maintenanceId = (int) tableModel.getValueAt(selectedRow, 0);
        Maintenance maintenance = maintenanceController.getMaintenanceById(maintenanceId);
        
        if (maintenance == null) {
            JOptionPane.showMessageDialog(this, "Maintenance record not found.");
            return;
        }

        JComboBox<String> carComboBox = loadCarOptions();
        
        // Try to select the current car in the combo box
        for (int i = 0; i < carComboBox.getItemCount(); i++) {
            if (carComboBox.getItemAt(i).startsWith(maintenance.getCarId() + " - ")) {
                carComboBox.setSelectedIndex(i);
                break;
            }
        }

        JComboBox<String> statusComboBox = new JComboBox<>(new String[] { 
            "Pending", "In Progress", "Completed", "Postponed", "Cancelled" 
        });
        statusComboBox.setSelectedItem(maintenance.getStatus());
        
        JTextField costField = new JTextField(String.valueOf(maintenance.getCost()));
        JTextArea detailsArea = new JTextArea(maintenance.getDetails(), 5, 20);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        JScrollPane detailsScrollPane = new JScrollPane(detailsArea);

        Object[] fields = {
                "Car:", carComboBox,
                "Status:", statusComboBox,
                "Cost:", costField,
                "Details:", detailsScrollPane
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Update Maintenance Record", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String selectedCar = (String) carComboBox.getSelectedItem();
                if (selectedCar == null || selectedCar.equals("No cars available")) {
                    JOptionPane.showMessageDialog(this, "Please select a valid car.");
                    return;
                }
                
                int carId = Integer.parseInt(selectedCar.split(" - ")[0]);
                String status = (String) statusComboBox.getSelectedItem();
                float cost = Float.parseFloat(costField.getText());
                String details = detailsArea.getText();

                boolean success = maintenanceController.updateMaintenance(
                    maintenanceId, carId, status, details, cost);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, "Maintenance record updated successfully!");
                    loadMaintenance();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update maintenance record.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for cost.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (ValidationException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedMaintenance() {
        int selectedRow = maintenanceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a maintenance record to delete.");
            return;
        }
        
        int maintenanceId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to delete this maintenance record?", 
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = maintenanceController.deleteMaintenance(maintenanceId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Maintenance record deleted successfully!");
                loadMaintenance();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete maintenance record.");
            }
        }
    }

}
