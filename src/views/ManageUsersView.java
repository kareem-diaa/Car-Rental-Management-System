package views;

import car_rental.Main;
import controllers.RegisterController;
import models.Customer;
import utils.AppColors;
import utils.ValidationException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ManageUsersView extends JPanel {
    private JTable usersTable;
    private DefaultTableModel tableModel;
    private RegisterController controller;

    public ManageUsersView(Main mainFrame) {
        this(mainFrame, null);
    }

    public ManageUsersView(Main mainFrame, String adminEmail) {
        controller = new RegisterController();
        setLayout(new BorderLayout());
        setBackground(AppColors.MAIN_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(AppColors.ACCENT_TIFFANY);
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setForeground(AppColors.LIGHT_TEXT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(AppColors.MAIN_BG);
        tablePanel.setBorder(new EmptyBorder(15, 0, 15, 0));

        String[] columnNames = { "ID", "Username", "Email", "Phone", "License Number", "Delete", "Promote" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5 || column == 6;
            }
        };

        usersTable = new JTable(tableModel);
        usersTable.setBackground(AppColors.MAIN_BG);
        usersTable.setForeground(AppColors.LIGHT_TEXT);
        usersTable.setSelectionBackground(AppColors.ACCENT_PURPLE);
        usersTable.setSelectionForeground(AppColors.LIGHT_TEXT);
        usersTable.setGridColor(AppColors.DIVIDER_DARK_GRAY);
        usersTable.getTableHeader().setBackground(AppColors.ACCENT_TIFFANY);
        usersTable.getTableHeader().setForeground(AppColors.LIGHT_TEXT);
        usersTable.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppColors.ACCENT_TIFFANY));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);

        loadUsers();

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(AppColors.MAIN_BG);
        JButton backButton = new JButton("Back to Dashboard");
        styleButton(backButton, AppColors.ACCENT_TIFFANY);
        backButton.addActionListener(_ -> {
            mainFrame.getContentPane().removeAll();
            mainFrame.add(new AdminDashboard(mainFrame, adminEmail));
            mainFrame.revalidate();
            mainFrame.repaint();
        });
        footerPanel.add(backButton);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        List<Customer> customers = controller.getAllCustomers();
        for (Customer customer : customers) {
            Object[] rowData = {
                    customer.getCustomerId(),
                    customer.getUsername(),
                    customer.getEmail(),
                    customer.getPhone(),
                    customer.getLicenseNumber(),
                    "Delete",
                    "Promote"
            };
            tableModel.addRow(rowData);
        }
        usersTable.getColumn("Delete").setCellRenderer(new DeleteButtonRenderer());
        usersTable.getColumn("Delete").setCellEditor(new DeleteButtonEditor(new JCheckBox()));
        usersTable.getColumn("Promote").setCellRenderer(new PromoteButtonRenderer());
        usersTable.getColumn("Promote").setCellEditor(new PromoteButtonEditor(new JCheckBox()));
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(AppColors.LIGHT_TEXT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(AppColors.ACCENT_PURPLE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }

    private class DeleteButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public DeleteButtonRenderer() {
            setText("Delete");
            setBackground(AppColors.ACCENT_TIFFANY);
            setForeground(AppColors.LIGHT_TEXT);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            return this;
        }
    }

    private class DeleteButtonEditor extends DefaultCellEditor {
        private JButton button;

        public DeleteButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Delete");
            button.setBackground(AppColors.ACCENT_TIFFANY);
            button.setForeground(AppColors.LIGHT_TEXT);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
            button.addActionListener(_ -> {
                fireEditingStopped();
                int selectedRow = usersTable.getSelectedRow();
                String customerEmail = (String) usersTable.getValueAt(selectedRow, 2);
                int confirmResult = JOptionPane.showConfirmDialog(
                        button,
                        "Are you sure you want to delete this user?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION);
                if (confirmResult == JOptionPane.YES_OPTION) {
                    boolean success = controller.delete(customerEmail);
                    if (success) {
                        JOptionPane.showMessageDialog(button, "User deleted successfully!");
                        tableModel.removeRow(selectedRow);
                    } else {
                        JOptionPane.showMessageDialog(button, "Failed to delete user!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Delete";
        }
    }

    private class PromoteButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public PromoteButtonRenderer() {
            setText("⭐ Promote to Admin");
            setBackground(new Color(96, 2, 204));
            setForeground(AppColors.LIGHT_TEXT);
            setFocusPainted(false);
            setFont(new Font("Arial", Font.BOLD, 12));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.WHITE, 1),
                    BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            return this;
        }
    }

    private class PromoteButtonEditor extends DefaultCellEditor {
        private JButton button;

        public PromoteButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("⭐ Promote to Admin");
            button.setBackground(new Color(96, 2, 204));
            button.setForeground(AppColors.LIGHT_TEXT);
            button.setFocusPainted(false);
            button.setFont(new Font("Arial", Font.BOLD, 12));
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.WHITE, 1),
                    BorderFactory.createEmptyBorder(5, 8, 5, 8)));
            button.addActionListener(_ -> {
                fireEditingStopped();
                int selectedRow = usersTable.getSelectedRow();
                int customerId = (Integer) usersTable.getValueAt(selectedRow, 0);
                String customerEmail = (String) usersTable.getValueAt(selectedRow, 2);
                String customerUsername = (String) usersTable.getValueAt(selectedRow, 1);
                promoteUser(selectedRow, customerId, customerEmail, customerUsername);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Promote";
        }

        private void promoteUser(int selectedRow, int customerId, String customerEmail, String customerUsername) {
            JPanel dialogPanel = new JPanel();
            dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
            dialogPanel.setBackground(new Color(30, 30, 40));
            dialogPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel titleLabel = new JLabel("⭐ Promote User to Admin ⭐");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
            titleLabel.setForeground(new Color(255, 215, 0));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            dialogPanel.add(titleLabel);

            dialogPanel.add(Box.createVerticalStrut(15));

            JLabel instructionLabel = new JLabel("Set password for the new admin:");
            instructionLabel.setForeground(Color.WHITE);
            instructionLabel.setFont(new Font("Arial", Font.BOLD, 12));
            instructionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            dialogPanel.add(instructionLabel);

            dialogPanel.add(Box.createVerticalStrut(10));

            JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            passwordPanel.setBackground(new Color(30, 30, 40));

            JLabel passwordLabel = new JLabel("Password: ");
            passwordLabel.setForeground(Color.WHITE);
            passwordPanel.add(passwordLabel);

            JTextField passwordField = new JTextField(20);
            passwordField.setBackground(new Color(50, 50, 60));
            passwordField.setForeground(Color.WHITE);
            passwordField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(96, 2, 204), 1),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            String defaultPassword = customerUsername + "_Admin123!";
            passwordField.setText(defaultPassword);
            passwordPanel.add(passwordField);

            dialogPanel.add(passwordPanel);

            dialogPanel.add(Box.createVerticalStrut(15));

            JPanel requirementsPanel = new JPanel();
            requirementsPanel.setLayout(new BoxLayout(requirementsPanel, BoxLayout.Y_AXIS));
            requirementsPanel.setBackground(new Color(40, 40, 50));
            requirementsPanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(100, 100, 120), 1),
                    "Password Requirements",
                    javax.swing.border.TitledBorder.LEFT,
                    javax.swing.border.TitledBorder.TOP,
                    new Font("Arial", Font.BOLD, 12),
                    new Color(200, 200, 200)));

            String[] requirements = {
                    "✓ At least 8 characters long",
                    "✓ Contains uppercase and lowercase letters",
                    "✓ Contains at least one number",
                    "✓ Contains at least one special character (@#$%^&+=!_*-)"
            };
            for (String req : requirements) {
                JLabel reqLabel = new JLabel(req);
                reqLabel.setForeground(new Color(150, 255, 150));
                reqLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                reqLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
                requirementsPanel.add(reqLabel);
            }
            dialogPanel.add(requirementsPanel);

            int result = JOptionPane.showConfirmDialog(
                    button,
                    dialogPanel,
                    "Promote User to Admin",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                String password = passwordField.getText().trim();
                if (password.isEmpty()) {
                    password = defaultPassword;
                }
                try {
                    boolean success = controller.promoteCustomerToAdmin(
                            customerEmail,
                            customerUsername,
                            password,
                            "admin");
                    if (success) {
                        JPanel successPanel = new JPanel();
                        successPanel.setLayout(new BoxLayout(successPanel, BoxLayout.Y_AXIS));
                        successPanel.setBackground(new Color(30, 40, 50));
                        successPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

                        JLabel iconLabel = new JLabel("✅ SUCCESS! ✅");
                        iconLabel.setFont(new Font("Arial", Font.BOLD, 18));
                        iconLabel.setForeground(new Color(100, 255, 100));
                        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        successPanel.add(iconLabel);

                        successPanel.add(Box.createVerticalStrut(15));

                        JLabel messageLabel = new JLabel("<html><center>" +
                                customerUsername + " has been successfully<br>promoted to Admin!</center></html>");
                        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
                        messageLabel.setForeground(Color.WHITE);
                        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        successPanel.add(messageLabel);

                        successPanel.add(Box.createVerticalStrut(15));

                        JPanel passwordDisplayPanel = new JPanel();
                        passwordDisplayPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                        passwordDisplayPanel.setBackground(new Color(30, 40, 50));

                        JLabel pwdLabel = new JLabel("Password: ");
                        pwdLabel.setForeground(Color.WHITE);
                        passwordDisplayPanel.add(pwdLabel);

                        JTextField pwdField = new JTextField(password);
                        pwdField.setColumns(15);
                        pwdField.setEditable(false);
                        pwdField.setBackground(new Color(60, 60, 70));
                        pwdField.setForeground(new Color(255, 220, 100));
                        pwdField.setFont(new Font("Monospaced", Font.BOLD, 12));
                        pwdField.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(new Color(100, 100, 120)),
                                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
                        passwordDisplayPanel.add(pwdField);

                        successPanel.add(passwordDisplayPanel);

                        successPanel.add(Box.createVerticalStrut(10));

                        JLabel noteLabel = new JLabel(
                                "<html><center><i>This password has been stored securely<br>for testing purposes only.</i></center></html>");
                        noteLabel.setFont(new Font("Arial", Font.ITALIC, 11));
                        noteLabel.setForeground(new Color(200, 200, 200));
                        noteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        successPanel.add(noteLabel);

                        JOptionPane.showMessageDialog(
                                button,
                                successPanel,
                                "Admin Promotion Successful",
                                JOptionPane.PLAIN_MESSAGE);

                        tableModel.removeRow(selectedRow);
                    } else {
                        JPanel errorPanel = new JPanel();
                        errorPanel.setLayout(new BoxLayout(errorPanel, BoxLayout.Y_AXIS));
                        errorPanel.setBackground(new Color(50, 30, 30));
                        errorPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

                        JLabel iconLabel = new JLabel("❌ ERROR ❌");
                        iconLabel.setFont(new Font("Arial", Font.BOLD, 18));
                        iconLabel.setForeground(new Color(255, 100, 100));
                        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        errorPanel.add(iconLabel);

                        errorPanel.add(Box.createVerticalStrut(15));

                        JLabel messageLabel = new JLabel("<html><center>Failed to promote user to admin!<br><br>" +
                                "Please try again or contact system administrator.</center></html>");
                        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                        messageLabel.setForeground(Color.WHITE);
                        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        errorPanel.add(messageLabel);

                        JOptionPane.showMessageDialog(
                                button,
                                errorPanel,
                                "Promotion Failed",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (ValidationException ex) {
                    String errorMessage = ex.getMessage();
                    if (errorMessage.contains("Password must be")) {
                        errorMessage = "<html><div style='color:#ffcccc; font-weight:bold'>Invalid Password Format</div><br>"
                                +
                                "<div style='color:#ffffff'>Please ensure your password has:</div><br>" +
                                "<ul>" +
                                "<li>At least 8 characters</li>" +
                                "<li>At least one uppercase letter (A-Z)</li>" +
                                "<li>At least one lowercase letter (a-z)</li>" +
                                "<li>At least one digit (0-9)</li>" +
                                "<li>At least one special character (@#$%^&+=!_*-)</li>" +
                                "</ul></html>";
                    }
                    JPanel errorPanel = new JPanel();
                    errorPanel.setLayout(new BorderLayout());
                    errorPanel.setBackground(new Color(50, 30, 30));
                    errorPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                    JLabel errorLabel = new JLabel(errorMessage);
                    errorLabel.setForeground(Color.WHITE);
                    errorPanel.add(errorLabel, BorderLayout.CENTER);

                    JOptionPane.showMessageDialog(
                            button,
                            errorPanel,
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}