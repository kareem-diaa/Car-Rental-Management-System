package views;

import controllers.DiscountController;
import models.Discount;
import utils.AppColors;
import utils.ValidationException;
import utils.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import car_rental.Main;

import java.awt.*;
import java.util.List;

public class ManageDiscountView extends JPanel {
    private final DiscountController discountController;
    private JTable discountTable;
    private DefaultTableModel tableModel;
    private final Main mainFrame;

    public ManageDiscountView(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.discountController = new DiscountController();
        setLayout(new BorderLayout(10, 10));
        setBackground(AppColors.MAIN_BG);

        // Add the footer panel (with buttons) at the top
        add(createFooterPanel(), BorderLayout.NORTH);

        // Create a center panel to hold the header and table vertically
        JPanel centerWrapper = new JPanel(new BorderLayout(10, 10));
        centerWrapper.setBackground(AppColors.MAIN_BG);
        centerWrapper.add(createHeaderPanel(), BorderLayout.NORTH);

        // Wrap the table panel in a panel with GridBagLayout
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

        loadDiscounts();
        mainFrame.setSize(800, 600);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(AppColors.ACCENT_TIFFANY);
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Discount Management", SwingConstants.CENTER);
        titleLabel.setForeground(AppColors.LIGHT_TEXT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(AppColors.MAIN_BG);
        tableModel = new DefaultTableModel(new Object[] { "ID", "Promotion Code", "Percentage" }, 0);
        discountTable = new JTable(tableModel);
        discountTable.setFillsViewportHeight(true);
        discountTable.setRowHeight(30);
        discountTable.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(discountTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Discounts"));
        scrollPane.setPreferredSize(new Dimension(700, 400));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footerPanel.setBackground(AppColors.ACCENT_TIFFANY);

        JButton addButton = new JButton("Add Discount");
        styleButton(addButton);
        addButton.addActionListener(_ -> showAddDiscountDialog());
        footerPanel.add(addButton);

        JButton updateButton = new JButton("Update Discount");
        styleButton(updateButton);
        updateButton.addActionListener(_ -> showUpdateDiscountDialog());
        footerPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete Discount");
        styleButton(deleteButton);
        deleteButton.setBackground(AppColors.ERROR_RED);
        deleteButton.addActionListener(_ -> deleteSelectedDiscount());
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

    private void loadDiscounts() {
        tableModel.setRowCount(0);
        List<Discount> discounts = discountController.getAllDiscounts();
        if (discounts == null || discounts.isEmpty()) {
            tableModel.addRow(new Object[] { "No discounts found", "", "" });
            discountTable.setEnabled(false);
        } else {
            for (Discount discount : discounts) {
                tableModel.addRow(new Object[] {
                        discount.getDiscountId(),
                        discount.getPromotionCode(),
                        discount.getDiscountPercentage()
                });
            }
            discountTable.setEnabled(true);
        }
    }

    private void showAddDiscountDialog() {
        JTextField promotionCodeField = new JTextField();
        JTextField percentageField = new JTextField();
        Object[] fields = {
                "Promotion Code:", promotionCodeField,
                "Discount Percentage:", percentageField
        };
        int option = JOptionPane.showConfirmDialog(this, fields, "Add Discount", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                Discount discount = new Discount();
                discount.setPromotionCode(promotionCodeField.getText());
                discount.setDiscountPercentage(Double.parseDouble(percentageField.getText()));
                boolean success = discountController.addDiscount(discount);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Discount added successfully!");
                    loadDiscounts();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add discount.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showUpdateDiscountDialog() {
        int selectedRow = discountTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a discount to update.");
            return;
        }

        int discountId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentPromotionCode = (String) tableModel.getValueAt(selectedRow, 1);
        double currentPercentage = (double) tableModel.getValueAt(selectedRow, 2);

        JTextField promotionCodeField = new JTextField(currentPromotionCode);
        JTextField percentageField = new JTextField(String.valueOf((int) currentPercentage));
        Object[] fields = {
                "Promotion Code:", promotionCodeField,
                "Discount Percentage:", percentageField
        };
        int option = JOptionPane.showConfirmDialog(this, fields, "Update Discount", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                Discount discount = new Discount();
                discount.setDiscountId(discountId);
                discount.setPromotionCode(promotionCodeField.getText());
                discount.setDiscountPercentage(Double.parseDouble(percentageField.getText()));
                boolean success = discountController.updateDiscount(discount);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Discount updated successfully!");
                    loadDiscounts();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update discount.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedDiscount() {
        int selectedRow = discountTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a discount to delete.");
            return;
        }
        int discountId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this discount?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = discountController.deleteDiscount(discountId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Discount deleted successfully!");
                loadDiscounts();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete discount.");
            }
        }
    }

}