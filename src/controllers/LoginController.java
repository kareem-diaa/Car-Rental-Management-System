package controllers;

import dao.AdminDAO;
import dao.CustomerDAO;
import utils.ValidationException;
import utils.ValidationUtil;

public class LoginController {
    private AdminDAO adminDAO;
    private CustomerDAO customerDAO;

    public LoginController() {
        this.adminDAO = new AdminDAO();
        this.customerDAO = new CustomerDAO();
    }

    public boolean login(String email, String password, boolean isAdmin)
            throws ValidationException {
        ValidationUtil.isValidEmail(email); // Validate email
        ValidationUtil.isValidPassword(password); // Validate password

        // Check if the email belongs to admin or customer
        if (isAdmin) {
            return adminDAO.login(email, password);
        }

        // Check email verification for customers
        if (!customerDAO.isEmailExists(email)) {
            throw new ValidationException("EMAIL_NOT_FOUND");
        }
        if (!customerDAO.isVerified(email)) {
            throw new ValidationException("NOT_VERIFIED");
        } else {
            return customerDAO.login(email, password);
        }
    }
}