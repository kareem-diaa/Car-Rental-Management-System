package views;

import controllers.BrandController;
import models.CarBrand;
import utils.AppColors;
import utils.ValidationException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import car_rental.Main;

import java.awt.*;
import java.util.List;

public class ManageCarBrandView extends JPanel {
    private final BrandController brandController;
    private JTable brandTable;
    private DefaultTableModel tableModel;
    private final Main mainFrame;

    public ManageCarBrandView(Main mainFrame) {
        this.mainFrame = mainFrame;
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

        loadBrands();
        mainFrame.setSize(800, 600);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(AppColors.ACCENT_TIFFANY);
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Brand Management", SwingConstants.CENTER);
        titleLabel.setForeground(AppColors.LIGHT_TEXT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(AppColors.MAIN_BG);
        tableModel = new DefaultTableModel(new Object[]{"ID", "Brand Name"}, 0);
        brandTable = new JTable(tableModel);
        brandTable.setFillsViewportHeight(true);
        brandTable.setRowHeight(30);
        brandTable.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(brandTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Brands"));
        scrollPane.setPreferredSize(new Dimension(700, 400));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footerPanel.setBackground(AppColors.ACCENT_TIFFANY);

        JButton addButton = new JButton("Add Brand");
        styleButton(addButton);
        addButton.addActionListener(_ -> {
            try {
                showAddBrandDialog();
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        });
        footerPanel.add(addButton);

        JButton updateButton = new JButton("Update Brand");
        styleButton(updateButton);
        updateButton.addActionListener(_ -> {
            try {
                showUpdateBrandDialog();
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        });
        footerPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete Brand");
        styleButton(deleteButton);
        deleteButton.setBackground(AppColors.ERROR_RED);
        deleteButton.addActionListener(_ -> deleteSelectedBrand());
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

    private void loadBrands() {
        tableModel.setRowCount(0);
        List<CarBrand> brands = brandController.getAllBrands();
        if (brands == null || brands.isEmpty()) {
            tableModel.addRow(new Object[]{"No brands found", ""});
            brandTable.setEnabled(false);
        } else {
            for (CarBrand brand : brands) {
                tableModel.addRow(new Object[]{
                        brand.getBrandId(),
                        brand.getBrandName()
                });
            }
            brandTable.setEnabled(true);
        }
    }

    private void showAddBrandDialog() throws ValidationException {
        JTextField brandNameField = new JTextField();
        Object[] fields = {
                "Brand Name:", brandNameField
        };
        int option = JOptionPane.showConfirmDialog(this, fields, "Add Brand", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            boolean success = brandController.addBrand(brandNameField.getText());
            if (success) {
                JOptionPane.showMessageDialog(this, "Brand added successfully!");
                loadBrands();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add brand.");
            }
        }
    }

    private void showUpdateBrandDialog() throws ValidationException {
        int selectedRow = brandTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a brand to update.");
            return;
        }
        int brandId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentBrandName = (String) tableModel.getValueAt(selectedRow, 1);

        JTextField brandNameField = new JTextField(currentBrandName);
        Object[] fields = {
                "Brand Name:", brandNameField
        };
        int option = JOptionPane.showConfirmDialog(this, fields, "Update Brand", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            boolean success = brandController.updateBrand(brandId, brandNameField.getText());
            if (success) {
                JOptionPane.showMessageDialog(this, "Brand updated successfully!");
                loadBrands();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update brand.");
            }
        }
    }

    private void deleteSelectedBrand() {
        int selectedRow = brandTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a brand to delete.");
            return;
        }
        int brandId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this brand?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = brandController.deleteBrand(brandId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Brand deleted successfully!");
                loadBrands();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete brand.");
            }
        }
    }

}