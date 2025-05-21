package car_rental;

import com.formdev.flatlaf.FlatDarkLaf;

import views.LoginView;
import javax.swing.*;
import migrations.DBMigration;
import utils.MySQLConnection;
import java.sql.Connection;


public class Main extends JFrame {

    public Main() {
        // For better UI, set the FlatLaf theme
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.err.println("Failed to set FlatLaf theme:");
            e.printStackTrace();
        }


        // set icon 
        ImageIcon icon = new ImageIcon("src/assets/logo.png");
        // Basic JFrame setup
        setTitle("Car Rental Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        setLocationRelativeTo(null);
        setResizable(false);
        setIconImage(icon.getImage());

        // Load Login View as a default view
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS)); 
        LoginView LoginView = new LoginView(this);
        add(LoginView);
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        // Optional: Uncomment the following lines to run DB migrations on startup

        // try (Connection conn = MySQLConnection.getInstance().getConnection()) {
        //     DBMigration.migrate(conn);
        // } catch (Exception e) {
        //     System.err.println("Failed to run DB migrations:");
        //     e.printStackTrace();
        // }

        // Launch the application
        SwingUtilities.invokeLater(() -> {
            Main mainApp = new Main();
            mainApp.setVisible(true);
        });
    }
}
