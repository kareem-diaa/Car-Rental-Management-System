package views;
import models.Customer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import car_rental.Main;
import java.awt.*;
import controllers.ProfileController;
public class ProfileView extends JPanel {
    private final Customer customer;
    private final ProfileController profileController = new ProfileController();
    private final Main mainFrame;

    private JTextField phoneField;
    private JTextField licenseField;

    public ProfileView(Main mainFrame, Customer customer) {
        this.mainFrame = mainFrame;
        this.customer = customer;
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        // Title
        JLabel titleLabel = new JLabel("Your Profile", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Center panel for profile form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Personal details section
        JPanel personalPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        personalPanel.setBorder(BorderFactory.createTitledBorder("Personal Information"));

        personalPanel.add(new JLabel("Username:"));
        JTextField usernameField = new JTextField(customer.getUsername());
        usernameField.setEditable(false);
        personalPanel.add(usernameField);

        personalPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField(customer.getPhone());
        personalPanel.add(phoneField);

        personalPanel.add(new JLabel("License Number:"));
        licenseField = new JTextField(customer.getLicenseNumber());
        personalPanel.add(licenseField);

        formPanel.add(personalPanel);

        // Add some spacing
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(_ -> saveProfile());
        buttonPanel.add(saveButton);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(_ -> goBack());
        buttonPanel.add(backButton);

        formPanel.add(buttonPanel);

        add(formPanel, BorderLayout.CENTER);

        // Bookings table panel
        JPanel bookingsPanel = createBookingsPanel();
        bookingsPanel.setPreferredSize(new Dimension(550, 150));
        add(bookingsPanel, BorderLayout.SOUTH);

        // Make main frame larger to accommodate all content
        mainFrame.setSize(650, 500);
    }

    private void saveProfile() {
        String newPhone = phoneField.getText();
        String newLicense = licenseField.getText();

        try {
            // Validate the input fields
            utils.ValidationUtil.isValidEgyptianPhone(newPhone);
            utils.ValidationUtil.isValidLicenseNumber(newLicense);

            // If validation passes, update the customer object
            customer.setPhone(newPhone);
            customer.setLicenseNumber(newLicense);

            boolean success = profileController.save(customer);
            if (success) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update profile.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (utils.ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goBack() {
        mainFrame.setSize(800, 800);
        mainFrame.getContentPane().removeAll();
        mainFrame.add(new AppView(mainFrame, customer));
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    private JPanel createBookingsPanel() {
        String[] cols = { "Booking ID", "Start Date", "End Date", "Status" };
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        JTable table = new JTable(model);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createTitledBorder("Your Bookings"));
        return panel;
    }
}