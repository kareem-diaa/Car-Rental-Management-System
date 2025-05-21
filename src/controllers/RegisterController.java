package controllers;

import utils.HashUtil;
import dao.AdminDAO;
import dao.CustomerDAO;
import models.Admin;
import models.Customer;
import utils.ValidationException;
import utils.ValidationUtil;
import java.util.List;

public class RegisterController {
    private CustomerDAO customerDAO;
    private AdminDAO adminDAO;

    public RegisterController() {
        this.customerDAO = new CustomerDAO();
        this.adminDAO = new AdminDAO();
    }

    public boolean register(String username, String password, String email, String phone, String licenseNumber)
            throws ValidationException {
        // Validate input fields using ValidationUtil
        ValidationUtil.isValidName(username);
        ValidationUtil.isValidEmail(email);
        ValidationUtil.isValidEgyptianPhone(phone);
        ValidationUtil.isValidLicenseNumber(licenseNumber);
        ValidationUtil.isValidPassword(password);

        // Create a customerID (similar to MongoDB system)
        int customerId = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);

        // Hash password
        String hashed = HashUtil.hashPassword(password);

        // Pass the hash (not plain text) to the DAO
        return customerDAO.register(customerId, username, hashed, email, phone, licenseNumber);
        // return adminDAO.register(username, password, email , "super_admin");
    }

    public boolean registerAdmin(String username, String password, String email)
            throws ValidationException {
        // Validate input fields
        ValidationUtil.isValidName(username);
        ValidationUtil.isValidEmail(email);
        ValidationUtil.isValidPassword(password);



        // Register the admin with the hashed password
        return adminDAO.register(username, password, email , "super_admin");
    }

    public boolean registerAdmin(String username, String password, String email , String role)
            throws ValidationException {
        // Validate input fields
        ValidationUtil.isValidName(username);
        ValidationUtil.isValidEmail(email);
        ValidationUtil.isValidPassword(password);
        // Register the admin with the hashed password
        return adminDAO.register(username, password, email, role);
    }



    public boolean isSuperAdmin(String email) {
        return adminDAO.isSuperAdmin(email);
    }

    public List<Admin> getAllAdmins() {
        return adminDAO.getAllAdmins();
    }

    public boolean deleteAdmin(int adminId) {
        return adminDAO.deleteAdmin(adminId);
    }

    public boolean delete(String email) {
        return customerDAO.delete(email);
    }

    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }



    public boolean promoteCustomerToAdmin(String email, String username, String password ,String role)
            throws ValidationException {
        try {
            // Validate the username, email and password
            ValidationUtil.isValidName(username);
            ValidationUtil.isValidEmail(email);
            ValidationUtil.isValidPassword(password);
            // Register as admin
            boolean success = adminDAO.register(username, password, email, role);

            if (!success) {
                System.out.println("Failed to register admin in database");
                return false;
            }

            // Delete the customer account
            boolean deleteSuccess = customerDAO.delete(email);
            if (!deleteSuccess) {
                System.out.println("Failed to delete customer account after promotion");
                // If we can't delete the customer, we should roll back the admin creation
                Admin admin = adminDAO.getAdminByEmail(email);
                if (admin != null) {
                    adminDAO.deleteAdmin(admin.getAdminId());
                }
                return false;
            }

            return true;
        } catch (Exception e) {
            System.out.println("Exception during promotion: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}