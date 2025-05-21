package views;

import controllers.CategoryController;
import models.Category;
import utils.AppColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import car_rental.Main;

import java.awt.*;
import java.util.List;

public class ManageCategoryView extends JPanel {
    private final CategoryController categoryController;
    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private final Main mainFrame;

    public ManageCategoryView(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.categoryController = new CategoryController();
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

        loadCategories();
        mainFrame.setSize(800, 600);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(AppColors.ACCENT_TIFFANY);
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Category Management", SwingConstants.CENTER);
        titleLabel.setForeground(AppColors.LIGHT_TEXT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(AppColors.MAIN_BG);
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Description", "Image"}, 0);
        categoryTable = new JTable(tableModel);
        categoryTable.setFillsViewportHeight(true);
        categoryTable.setRowHeight(30);
        categoryTable.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(categoryTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Categories"));
        scrollPane.setPreferredSize(new Dimension(700, 400));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footerPanel.setBackground(AppColors.ACCENT_TIFFANY);

        JButton addButton = new JButton("Add Category");
        styleButton(addButton);
        addButton.addActionListener(_ -> showAddCategoryDialog());
        footerPanel.add(addButton);

        JButton updateButton = new JButton("Update Category");
        styleButton(updateButton);
        updateButton.addActionListener(_ -> showUpdateCategoryDialog());
        footerPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete Category");
        styleButton(deleteButton);
        deleteButton.setBackground(AppColors.ERROR_RED);
        deleteButton.addActionListener(_ -> deleteSelectedCategory());
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

    private void loadCategories() {
        tableModel.setRowCount(0);
        List<Category> categories = categoryController.getAllCategories();
        if (categories == null || categories.isEmpty()) {
            tableModel.addRow(new Object[]{"No categories found", "", "", ""});
            categoryTable.setEnabled(false);
        } else {
            for (Category category : categories) {
                tableModel.addRow(new Object[]{
                        category.getCategoryID(),
                        category.getName(),
                        category.getDescription(),
                        category.getCategoryIMG()
                });
            }
            categoryTable.setEnabled(true);
        }
    }

    private void showAddCategoryDialog() {
        JTextField nameField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField imageField = new JTextField();
        Object[] fields = {
                "Name:", nameField,
                "Description:", descriptionField,
                "Image URL:", imageField
        };
        int option = JOptionPane.showConfirmDialog(this, fields, "Add Category", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                boolean success = categoryController.addCategory(
                        nameField.getText(),
                        descriptionField.getText(),
                        imageField.getText()
                );
                if (success) {
                    JOptionPane.showMessageDialog(this, "Category added successfully!");
                    loadCategories();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add category.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showUpdateCategoryDialog() {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a category to update.");
            return;
        }
        int categoryId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentDescription = (String) tableModel.getValueAt(selectedRow, 2);
        String currentImage = (String) tableModel.getValueAt(selectedRow, 3);

        JTextField nameField = new JTextField(currentName);
        JTextField descriptionField = new JTextField(currentDescription);
        JTextField imageField = new JTextField(currentImage);
        Object[] fields = {
                "Name:", nameField,
                "Description:", descriptionField,
                "Image URL:", imageField
        };
        int option = JOptionPane.showConfirmDialog(this, fields, "Update Category", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                boolean success = categoryController.updateCategory(
                        categoryId,
                        nameField.getText(),
                        descriptionField.getText(),
                        imageField.getText()
                );
                if (success) {
                    JOptionPane.showMessageDialog(this, "Category updated successfully!");
                    loadCategories();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update category.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedCategory() {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a category to delete.");
            return;
        }
        int categoryId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this category?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = categoryController.deleteCategory(categoryId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Category deleted successfully!");
                loadCategories();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete category.");
            }
        }
    }

}