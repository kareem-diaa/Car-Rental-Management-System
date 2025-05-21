package migrations;

import java.sql.*;

public class DBMigration {

    public static void migrate(Connection conn) {
        try {
            createMigrationsTable(conn);

            applyMigration(conn, "create_customer_table", """
                        CREATE TABLE Customer (
                            customer_id INT PRIMARY KEY AUTO_INCREMENT,
                            username VARCHAR(100) UNIQUE NOT NULL,
                            password_hash VARCHAR(255) NOT NULL,
                            email VARCHAR(150) UNIQUE NOT NULL,
                            phone VARCHAR(20) UNIQUE,
                            license_number VARCHAR(50) UNIQUE,
                            is_verified BOOLEAN NOT NULL DEFAULT FALSE,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                        )
                    """);

            applyMigration(conn, "create_admin_table", """
                        CREATE TABLE Admin (
                            admin_id INT PRIMARY KEY AUTO_INCREMENT,
                            username VARCHAR(100) UNIQUE NOT NULL,
                            password_hash VARCHAR(255) NOT NULL,
                            email VARCHAR(150) UNIQUE NOT NULL,
                            role VARCHAR(50),
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                        )
                    """);

            applyMigration(conn, "create_category_table", """
                        CREATE TABLE Category (
                            category_id INT PRIMARY KEY AUTO_INCREMENT,
                            name VARCHAR(100) UNIQUE,
                            description TEXT,
                            category_img TEXT,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                        )
                    """);

            applyMigration(conn, "create_brand_table", """
                        CREATE TABLE Brand (
                            brand_id INT PRIMARY KEY AUTO_INCREMENT,
                            brand_name VARCHAR(100) UNIQUE
                        )
                    """);

            applyMigration(conn, "create_car_model_table", """
                        CREATE TABLE Car_Model (
                            model_id INT PRIMARY KEY AUTO_INCREMENT,
                            brand_id INT,
                            model_name VARCHAR(100),
                            fuel_type VARCHAR(50),
                            FOREIGN KEY (brand_id) REFERENCES Brand(brand_id)
                        )
                    """);

            applyMigration(conn, "create_car_table", """
                        CREATE TABLE Car (
                            car_id INT PRIMARY KEY AUTO_INCREMENT,
                            model_id INT,
                            category_id INT,
                            mileage INT,
                            availability_status VARCHAR(50),
                            rental_price DECIMAL(10, 2),
                            plate_no VARCHAR(20) UNIQUE,
                            image_url TEXT,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            FOREIGN KEY (model_id) REFERENCES Car_Model(model_id),
                            FOREIGN KEY (category_id) REFERENCES Category(category_id)
                        )
                    """);


            applyMigration(conn, "create_maintenance_table", """
                        CREATE TABLE Maintenance (
                            maintenance_id INT PRIMARY KEY AUTO_INCREMENT,
                            car_id INT,
                            details TEXT,
                            cost DECIMAL(10, 2),
                            maintenance_date DATE,
                            status VARCHAR(50),
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            FOREIGN KEY (car_id) REFERENCES Car(car_id)
                        )
                    """);

            applyMigration(conn, "create_booking_table", """
                        CREATE TABLE Booking (
                            booking_id INT PRIMARY KEY AUTO_INCREMENT,
                            customer_id INT,
                            car_id INT,
                            status VARCHAR(50),
                            start_date DATE,
                            end_date DATE,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            FOREIGN KEY (customer_id) REFERENCES Customer(customer_id),
                            FOREIGN KEY (car_id) REFERENCES Car(car_id)
                        )
                    """);

            applyMigration(conn, "create_review_table", """
                        CREATE TABLE Review (
                            review_id INT PRIMARY KEY AUTO_INCREMENT,
                            customer_id INT,
                            booking_id INT,
                            review TEXT,
                            rating INT,
                            FOREIGN KEY (customer_id) REFERENCES Customer(customer_id),
                            FOREIGN KEY (booking_id) REFERENCES Booking(booking_id)
                        )
                    """);

            applyMigration(conn, "create_payment_table", """
                        CREATE TABLE Payment (
                            payment_id INT PRIMARY KEY AUTO_INCREMENT,
                            customer_id INT,
                            booking_id INT,
                            amount DECIMAL(10, 2),
                            payment_status VARCHAR(50),
                            payment_method VARCHAR(50),
                            payment_date DATE,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            FOREIGN KEY (booking_id) REFERENCES Booking(booking_id),
                            FOREIGN KEY (customer_id) REFERENCES Customer(customer_id)
                        )
                    """);

            applyMigration(conn, "create_rental_history_table", """
                        CREATE TABLE Rental_History (
                            rental_id INT PRIMARY KEY AUTO_INCREMENT,
                            customer_id INT,
                            booking_id INT,
                            return_date DATE,
                            extra_charges DECIMAL(10, 2),
                            comments TEXT,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            FOREIGN KEY (customer_id) REFERENCES Customer(customer_id),
                            FOREIGN KEY (booking_id) REFERENCES booking(booking_id)
                        )
                    """);

            applyMigration(conn, "create_discount_table", """
                        CREATE TABLE Discount (
                            discount_id INT PRIMARY KEY AUTO_INCREMENT,
                            promotion_code VARCHAR(30) UNIQUE,
                            discount_percent int,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                        )
                    """);

            System.out.println("All applicable migrations processed.");

        } catch (Exception e) {
            System.err.println("Critical failure in migration system:");
            e.printStackTrace();
        }
    }

    private static void createMigrationsTable(Connection conn) throws SQLException {
        String sql = """
                    CREATE TABLE IF NOT EXISTS migrations (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) UNIQUE,
                        executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        }
    }

    private static boolean isMigrationApplied(Connection conn, String name) throws SQLException {
        String sql = "SELECT COUNT(*) FROM migrations WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private static void applyMigration(Connection conn, String name, String sql) {
        try {
            if (isMigrationApplied(conn, name)) {
                System.out.println("✔ Migration '" + name + "' already applied.");
                return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.execute();
            }

            try (PreparedStatement logStmt = conn.prepareStatement(
                    "INSERT INTO migrations (name) VALUES (?)")) {
                logStmt.setString(1, name);
                logStmt.execute();
            }

            System.out.println("Migration '" + name + "' applied.");

        } catch (SQLException e) {
            System.err.println("Migration '" + name + "' failed: " + e.getMessage());
        }
    }
}
