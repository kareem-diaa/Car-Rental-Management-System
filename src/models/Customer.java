package models;

import java.time.LocalDate;
import java.util.Date;

public class Customer {
    private int customerId;
    private String username;
    private String password;
    private String phone;
    private String email;
    private Date createdAt;
    private Date updatedAt;

    private String licenseNumber;
    private boolean isVerified;

    public Customer(int customerId, String username, String password, String phone, String licenseNumber) {
        this.customerId = customerId;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.licenseNumber = licenseNumber;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters
    public int getCustomerId() { return customerId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public String getLicenseNumber() { return licenseNumber; }
    public boolean isVerified() { return isVerified; }

    // Setters
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public void setVerified(boolean isVerified) { this.isVerified = isVerified; }
}