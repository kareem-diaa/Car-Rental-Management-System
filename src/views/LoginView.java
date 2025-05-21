package views;

import car_rental.Main;
import controllers.LoginController;
import dao.CustomerDAO;
import models.Customer;
import utils.AppColors;
import utils.ValidationException;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginView extends JPanel {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JCheckBox adminCheckBox;
    private JButton loginButton;
    private JLabel switchToRegisterLabel;
    private LoginController loginController;
    private Main mainFrame;

    public LoginView(Main topFrame) {
        this.mainFrame = topFrame;
        this.loginController = new LoginController();

        setBackground(AppColors.MAIN_BG);
        setLayout(new MigLayout("fill, insets 20", "[center]", "[center]"));

        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(AppColors.LIGHT_TEXT);
        add(titleLabel, "span, center, gapbottom 20");

        emailField = new JTextField(20);
        emailField.setBackground(AppColors.DIVIDER_DARK_GRAY);
        emailField.setForeground(AppColors.LIGHT_TEXT);
        emailField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JLabel userLabel = new JLabel("email", JLabel.RIGHT);
        userLabel.setForeground(AppColors.LIGHT_TEXT);
        add(userLabel);
        add(emailField, "wrap, growx, gapbottom 10");

        passwordField = new JPasswordField(20);
        passwordField.setBackground(AppColors.DIVIDER_DARK_GRAY);
        passwordField.setForeground(AppColors.LIGHT_TEXT);
        passwordField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JLabel passLabel = new JLabel("Password", JLabel.RIGHT);
        passLabel.setForeground(AppColors.LIGHT_TEXT);
        add(passLabel);
        add(passwordField, "wrap, growx, gapbottom 10");

        adminCheckBox = new JCheckBox("Login as Admin");
        adminCheckBox.setForeground(AppColors.LIGHT_TEXT);
        adminCheckBox.setBackground(AppColors.MAIN_BG);
        add(adminCheckBox, "span, center, gapbottom 20");

        loginButton = new JButton("Login");
        styleButton(loginButton);
        loginButton.addActionListener(_e -> onLogin());
        add(loginButton, "span, center, gapbottom 10");

        switchToRegisterLabel = new JLabel("Don't have an account? Register");
        switchToRegisterLabel.setForeground(AppColors.ACCENT_TIFFANY);
        switchToRegisterLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        switchToRegisterLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                topFrame.getContentPane().removeAll();
                topFrame.add(new RegisterView(topFrame));
                topFrame.revalidate();
                topFrame.repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                switchToRegisterLabel.setForeground(AppColors.ACCENT_PURPLE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                switchToRegisterLabel.setForeground(AppColors.ACCENT_TIFFANY);
            }
        });
        add(switchToRegisterLabel, "span, center");
    }

    private void onLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        boolean isAdmin = adminCheckBox.isSelected();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter both email and password.",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean success = loginController.login(email, password, isAdmin);
            if (success) {
                JOptionPane.showMessageDialog(
                        this,
                        "Login successful!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                mainFrame.getContentPane().removeAll();
                if (isAdmin) {
                    mainFrame.add(new AdminDashboard(mainFrame, email));
                } else {
                    CustomerDAO dao = new CustomerDAO();
                    Customer customer = dao.getByEmail(email);
                    if (customer != null) {
                        mainFrame.add(new AppView(mainFrame, customer));
                    } else {
                        JOptionPane.showMessageDialog(
                                this,
                                "No account found with this email.",
                                "User Not Found",
                                JOptionPane.ERROR_MESSAGE);
                        resetFields();
                        return;
                    }
                }
                mainFrame.revalidate();
                mainFrame.repaint();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid credentials! Please check your email and password.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
                resetFields();
            }
        } catch (ValidationException ve) {
            if ("EMAIL_NOT_FOUND".equals(ve.getMessage())) {
                JOptionPane.showMessageDialog(
                        this,
                        "No account found with this email.",
                        "User Not Found",
                        JOptionPane.ERROR_MESSAGE);
                mainFrame.getContentPane().removeAll();
                mainFrame.add(new RegisterView(mainFrame));
                mainFrame.revalidate();
                mainFrame.repaint();
            } else if ("NOT_VERIFIED".equals(ve.getMessage())) {
                JOptionPane.showMessageDialog(
                        this,
                        "Your email is not verified. Please verify your email.",
                        "Email Not Verified",
                        JOptionPane.WARNING_MESSAGE);

                mainFrame.getContentPane().removeAll();
                mainFrame.add(new VerificationView(mainFrame, email));
                mainFrame.revalidate();
                mainFrame.repaint();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        ve.getMessage(),
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                resetFields();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "An unexpected error occurred:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            resetFields();
        }
    }

    private void resetFields() {
        passwordField.setText("");
        passwordField.requestFocus();
    }

    private void styleButton(JButton button) {
        button.setBackground(AppColors.ACCENT_TIFFANY);
        button.setForeground(AppColors.MAIN_BG);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(AppColors.ACCENT_PURPLE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(AppColors.ACCENT_TIFFANY);
            }
        });
    }
}