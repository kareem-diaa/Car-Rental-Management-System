# 🚗 Car Rental Management System (CRMS)

A desktop application for managing car rentals, built with Java Swing and MySQL.  
It follows a modular MVC-inspired architecture with migrations, DAOs, domain models, and a Swing GUI.

<p align="center">
  <img src="https://github.com/Abram122/Car_Rental/blob/main/src/assets/logo.png" alt="Project Logo" width="200"/>
</p>

---

## 📈 Project Status
<div align="center">

[![Repo Stats](https://github-readme-stats.vercel.app/api/pin/?username=Abram122&repo=Car_Rental&show_owner=false&title_color=blue&icon_color=yellow&bg_color=0d1117)](https://github.com/Abram122/Car_Rental)  
<br>
<img src="https://tokei.rs/b1/github/Abram122/Car_Rental" alt="Lines of Code">
<img src="https://img.shields.io/github/commit-activity/y/Abram122/Car_Rental" alt="Total Commits">
<img src="https://img.shields.io/github/last-commit/Abram122/Car_Rental" alt="Last Commit">

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-00758F?style=for-the-badge&logo=mysql&logoColor=white)
![Swing](https://img.shields.io/badge/Swing-007396?style=for-the-badge&logo=java&logoColor=white)

</div>

---

## 📑 Table of Contents

- [About](#about)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Architecture](#project-architecture)
- [Design Patterns](#design-patterns)
- [Project Components](#project-components)
  - [Models](#models)
  - [DAO Layer](#dao-layer)
  - [Controllers](#controllers)
  - [Views](#views)
  - [Utils](#utils)
  - [Migrations](#migrations)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Database Setup](#database-setup)
- [Running the Application](#running-the-application)
- [Screenshots](#screenshots)
- [Team Members](#team-members)
- [License](#license)

---

## 📝 About

The Car Rental Management System (CRMS) is a comprehensive desktop application designed to simplify the car rental process for businesses. It provides a complete solution for managing customers, vehicles, bookings, payments, and maintenance all within a single interface.

This application is built using Java Swing for the GUI and MySQL for data persistence, following an MVC (Model-View-Controller) architecture pattern. It's designed with modularity and extensibility in mind, making it easy to maintain and extend.

---

## ✨ Features

- **User Authentication**
  - ✅ Customer and Admin login and registration
  - 🔑 Secure password hashing with jBCrypt
  - 📧 Email verification system
  
- **Customer Management**
  - 👤 Customer registration with license information
  - 📋 Customer profile management
  - 🔍 Customer search and filtering
  
- **Car Management**
  - 🚘 Complete car inventory system (CRUD operations)
  - 🏷️ Car categorization and brand management
  - 📊 Car availability tracking
  - 🖼️ Car image upload and display
  
- **Booking System**
  - 📅 Advanced booking with date validation
  - 💳 Payment processing
  - 🧾 Invoice generation
  - 🔄 Rental history tracking
  
- **Maintenance & Reviews**
  - 🔧 Vehicle maintenance logging
  - 💰 Maintenance cost tracking
  - ⭐ Customer review and rating system
  
- **Admin Features**
  - 📊 Dashboard with key metrics
  - 👥 User management
  - 💵 Payment oversight
  - 📝 Discount management

---

## 💻 Tech Stack

- **Language**: Java SE
- **GUI Framework**: Java Swing with FlatLaf for modern UI
- **Database**: MySQL
- **JDBC Connector**: MySQL Connector/J 9.1.0
- **Additional Libraries**:
  - jBCrypt for password hashing
  - FlatLaf for modern UI
  - PDFBox for PDF generation
  - JavaMail for email functionality
  - JDatePicker for date selection
  - MigLayout for flexible GUI layouts

---

## 🏗️ Project Architecture

The Car Rental Management System follows the **Model-View-Controller (MVC)** architectural pattern, structured as follows:

```
📂 src/
 ┣ 📂 models/            # Domain models representing business entities
 ┣ 📂 dao/               # Data Access Objects for database operations
 ┣ 📂 controllers/       # Controllers to handle business logic
 ┣ 📂 views/             # UI components and screens
 ┣ 📂 utils/             # Utility classes and helpers
 ┣ 📂 migrations/        # Database setup and migrations
 ┣ 📂 services/          # Business services (e.g., OTP service)
 ┣ 📂 resources/         # Configuration files
 ┣ 📂 assets/            # Images and other static resources
 ┗ 📂 fonts/             # Custom fonts for the application
```

This architecture provides:
- **Separation of concerns**: Each component has a clear responsibility
- **Maintainability**: Changes to one layer don't affect other layers
- **Testability**: Components can be tested in isolation
- **Flexibility**: Easy to extend or modify functionality

---

## 📦 Project Components

### 📊 Models

Models represent the business entities and encapsulate the data structure. Key models include:

- **Car**: Represents a vehicle available for rental
  - Attributes: ID, model, category, mileage, availability, price, etc.
  - Related models: `CarBrand`, `CarModel`, `Category`

- **Customer**: Represents a registered user
  - Attributes: ID, username, password hash, email, license number, etc.
  - Authentication and profile details

- **Booking**: Represents a car reservation
  - Attributes: ID, car ID, customer ID, dates, status, etc.
  - Links customers to cars for a specific period

- **Payment**: Handles financial transactions
  - Attributes: ID, booking ID, amount, status, date, method
  - Tracks all financial aspects of rentals

- **RentalHistory**: Records past rentals
  - Attributes: ID, booking ID, return date, condition notes, etc.
  - Maintains a history of all completed rentals

- **Maintenance**: Tracks vehicle upkeep
  - Attributes: ID, car ID, date, description, cost, etc.
  - Records all maintenance activities for each vehicle

- **Review**: Customer feedback about rentals
  - Attributes: ID, booking ID, rating, comments, date
  - Allows customers to rate their rental experience

### 🗄️ DAO Layer

The Data Access Object (DAO) layer provides an abstraction between the database and the application. Each DAO class:

- Handles CRUD operations (Create, Read, Update, Delete)
- Manages SQL queries and database connections
- Maps database records to model objects

Key DAO classes:

- **CarDAO**: Manages car data in the database
  - Methods: `insertCar()`, `getCarById()`, `updateCar()`, `deleteCar()`, etc.
  - Handles car-specific database operations

- **CustomerDAO**: Manages customer accounts
  - Methods: `registerCustomer()`, `getCustomerByUsername()`, `verifyCustomer()`, etc.
  - Handles customer authentication and profile management

- **BookingDAO**: Manages rental bookings
  - Methods: `createBooking()`, `getBookingsByCustomer()`, `updateBookingStatus()`, etc.
  - Handles reservation-related database operations

All DAO classes follow a similar pattern, providing a consistent interface for database operations while encapsulating SQL complexity.

### 🎮 Controllers

Controllers act as intermediaries between the Views and the Models/DAOs. They:

- Validate user input before processing
- Implement business logic
- Call appropriate DAO methods
- Return results to the Views

Key controllers:

- **CarController**: Manages car-related operations
  - Methods: `addCar()`, `updateCar()`, `deleteCar()`, `getCarById()`, etc.
  - Validates car data before database operations

- **BookingController**: Handles rental bookings
  - Methods: `createBooking()`, `cancelBooking()`, `getActiveBookings()`, etc.
  - Manages the booking workflow and status transitions

- **LoginController**: Handles user authentication
  - Methods: `loginCustomer()`, `loginAdmin()`, `verifyCredentials()`
  - Securely authenticates users and manages sessions

### 🖼️ Views

Views are the user interface components built with Java Swing. The application uses FlatLaf for a modern look and feel. Key views include:

- **LoginView**: User authentication screen
  - Components: Username/password fields, login button
  - Functionality: Authenticate users, reset password

- **AdminDashboard**: Main admin interface
  - Components: Navigation menu, summary statistics
  - Functionality: Access to all admin functions

- **AvailableCarsView**: Displays cars for rental
  - Components: Car listing table, filters, booking button
  - Functionality: Browse and select cars for rental

- **BookingView**: Booking creation interface
  - Components: Date pickers, customer details, payment options
  - Functionality: Complete the booking process

- **ManageCarView**: Car management interface
  - Components: Car listing, add/edit/delete buttons
  - Functionality: CRUD operations for car inventory

Each view is designed to be responsive and user-friendly, with consistent styling and navigation.

### 🔧 Utils

Utility classes provide common functionality used across the application:

- **MySQLConnection**: Database connection management (Singleton)
  - Methods: `getInstance()`, `getConnection()`
  - Maintains a single connection to the MySQL database

- **EmailUtil**: Email notification services
  - Methods: `sendVerificationEmail()`, `sendPasswordReset()`
  - Handles all email communications

- **HashUtil**: Password security
  - Methods: `hashPassword()`, `verifyPassword()`
  - Uses jBCrypt for secure password handling

- **ValidationUtil**: Input validation
  - Methods: `isValidEmail()`, `isValidPhone()`, etc.
  - Ensures data integrity before processing

- **AppColors**: UI color constants
  - Defines color scheme for consistent UI appearance

### 📜 Migrations

The migration system automates database setup and schema changes:

- **DBMigration**: Database setup and schema management
  - Methods: `migrate()`, `applyMigration()`
  - Creates all required tables and initial data

---

## 🛠 Prerequisites

- Java Development Kit (JDK) 8 or higher
- MySQL Server 5.7 or higher
- MySQL Connector/J 9.1.0
- (Optional) An IDE such as Eclipse, IntelliJ IDEA, or NetBeans

---

## 🚀 Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Abram122/Car_Rental.git
   cd Car_Rental
   ```

2. **Configure database connection**
   - Edit `src/resources/DBPropFile.properties` with your MySQL credentials
   - Ensure MySQL server is running

3. **Add required libraries**
   - All required JAR files are in the `lib/` directory
   - Add these to your build path if not already included

4. **Compile the project**
   - Use your IDE's build functionality or compile manually

---

## ⚙️ Configuration

### Database Configuration

Edit the `src/resources/DBPropFile.properties` file:
```properties
MYSQL_DB_URL=jdbc:mysql://localhost:3306/car_rental
USER=your_username
PASSWORD=your_password
```

### Email Configuration

Edit the `src/resources/smtp.properties` file:
```properties
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_AUTH=true
SMTP_STARTTLS=true
SMTP_USERNAME=your_email@gmail.com
SMTP_PASSWORD=your_app_password
```

---

## 📊 Database Setup

To initialize the database:

1. **Create a MySQL database**
   ```sql
   CREATE DATABASE car_rental;
   ```

2. **Run migrations**
   - Uncomment the migration code in `Main.java`
   - Run the application once to create all tables
   - Re-comment the migration code for subsequent runs

The migration system will automatically create all required tables, indexes, and relationships.

---

## 🏃‍♂️ Running the Application

1. **Compile and run the Main class**
   ```bash
   javac -cp "lib/*" -d bin src/car_rental/Main.java
   java -cp "bin;lib/*" car_rental.Main
   ```

2. **Login**
   - Use the default admin credentials (check admin_passwords.txt)
   - Or register a new customer account

3. **Navigate the interface**
   - Use the menu options to access different features
   - Admin users have access to management features
   - Customers can browse cars, make bookings, and view their history

---

## 🏆 Team Members
* **Abram Mina**
* **Ahmed Mohamed**
* **Kareem Diaa**
* **Zeyad Mahmoud**



---

## 📬 Contact

For inquiries or collaboration, feel free to reach out via GitHub Issues or Discussions.

---

## 🙌 Acknowledgements

* SU TECH Assistant Professors, Teaching Assistants, and Mentors
* Java & Swing Documentation
* MySQL Community Edition
* GitHub Community
* Open-source contributors

