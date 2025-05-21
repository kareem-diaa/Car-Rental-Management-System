package views;

import car_rental.Main;
import controllers.RegisterController;
import utils.AppColors;
import utils.ValidationException;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegisterView extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField licenseField;
    private JButton registerButton;
    private JLabel switchToLoginLabel;

    public RegisterView(Main mainFrame) {
        setBackground(AppColors.MAIN_BG);
        setLayout(new MigLayout("fill, insets 20", "[center]", "[center]"));

        // Title
        JLabel titleLabel = new JLabel("Register");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(AppColors.LIGHT_TEXT);
        add(titleLabel, "span, center, gapbottom 20");

        // Username field
        usernameField = new JTextField(20);
        usernameField.setBackground(AppColors.DIVIDER_DARK_GRAY);
        usernameField.setForeground(AppColors.LIGHT_TEXT);
        usernameField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(new JLabel("Username", JLabel.RIGHT)).setForeground(AppColors.LIGHT_TEXT);
        add(usernameField, "wrap, growx, gapbottom 10");

        // Password field
        passwordField = new JPasswordField(20);
        passwordField.setBackground(AppColors.DIVIDER_DARK_GRAY);
        passwordField.setForeground(AppColors.LIGHT_TEXT);
        passwordField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(new JLabel("Password", JLabel.RIGHT)).setForeground(AppColors.LIGHT_TEXT);
        add(passwordField, "wrap, growx, gapbottom 10");

        // Email field
        emailField = new JTextField(20);
        emailField.setBackground(AppColors.DIVIDER_DARK_GRAY);
        emailField.setForeground(AppColors.LIGHT_TEXT);
        emailField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(new JLabel("Email", JLabel.RIGHT)).setForeground(AppColors.LIGHT_TEXT);
        add(emailField, "wrap, growx, gapbottom 10");

        // Phone field
        phoneField = new JTextField(20);
        phoneField.setBackground(AppColors.DIVIDER_DARK_GRAY);
        phoneField.setForeground(AppColors.LIGHT_TEXT);
        phoneField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(new JLabel("Phone", JLabel.RIGHT)).setForeground(AppColors.LIGHT_TEXT);
        add(phoneField, "wrap, growx, gapbottom 10");

        // License number field
        licenseField = new JTextField(20);
        licenseField.setBackground(AppColors.DIVIDER_DARK_GRAY);
        licenseField.setForeground(AppColors.LIGHT_TEXT);
        licenseField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(new JLabel("License Number", JLabel.RIGHT)).setForeground(AppColors.LIGHT_TEXT);
        add(licenseField, "wrap, growx, gapbottom 20");

        // Register button
        registerButton = new JButton("Register");
        styleButton(registerButton);
        registerButton.addActionListener(_ -> {
            try {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String email = emailField.getText();
                String phone = phoneField.getText();
                String licenseNumber = licenseField.getText();
                RegisterController registerController = new RegisterController();
                boolean success = registerController.register(username, password, email, phone, licenseNumber);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Registration successful! Please verify your email.");
                    // Switch to VerificationView
                    mainFrame.getContentPane().removeAll();
                    mainFrame.add(new VerificationView(mainFrame, email));
                    mainFrame.revalidate();
                    mainFrame.repaint();
                } else {
                    JOptionPane.showMessageDialog(this, "Registration failed!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error sending OTP: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(registerButton, "span, center, gapbottom 10");

        // Switch to Login label
        switchToLoginLabel = new JLabel("Already have an account? Login");
        switchToLoginLabel.setForeground(AppColors.ACCENT_TIFFANY);
        switchToLoginLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        switchToLoginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Switch to LoginView
                mainFrame.getContentPane().removeAll();
                mainFrame.add(new LoginView(mainFrame));
                mainFrame.revalidate();
                mainFrame.repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                switchToLoginLabel.setForeground(AppColors.ACCENT_PURPLE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                switchToLoginLabel.setForeground(AppColors.ACCENT_TIFFANY);
            }
        });
        add(switchToLoginLabel, "span, center");
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