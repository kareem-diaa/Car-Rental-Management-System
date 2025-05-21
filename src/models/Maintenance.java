package models;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Maintenance {
    private int maintenanceID;
    private int carId; // Added car_id field as per DB schema
    private String status, details;
    private float cost;
    private LocalDateTime maintenanceDate, createdAt, updatedAt;
    
    // Transient reference to Car object
    private transient Car car;

    private static List<Maintenance> maintenanceHistory=new ArrayList<>();    public void setMaintenanceDate(LocalDateTime maintenanceDate) {
        this.maintenanceDate = maintenanceDate;
    }
    
    public void setCarId(int carId) {
        this.carId = carId;
    }
    
    public int getCarId() {
        return carId;
    }
    
    public Car getCar() {
        return car;
    }
    
    public void setCar(Car car) {
        this.car = car;
    }
    public static void setMaintenanceHistory(List<Maintenance> maintenanceHistory) {
        Maintenance.maintenanceHistory = maintenanceHistory;
    }
    public void setCost(float cost) {
        if (cost<0){
            throw new IllegalArgumentException("Cost cannot be negative");
        }
        this.cost = cost;
    }
    public void setDetails(String details) {
        this.details = details;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setMaintenanceID(int maintenanceID) {
        this.maintenanceID = maintenanceID;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public float getCost() {
        return cost;
    }
    public String getDetails() {
        return details;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getMaintenanceDate() {
        return maintenanceDate;
    }
    public int getMaintenanceID() {
        return maintenanceID;
    }
    public String getStatus() {
        return status;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Maintenance(int maintenanceID, String status, String details, float cost, LocalDateTime maintenanceDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.maintenanceID = maintenanceID;
        this.status = status;
        this.details = details;
        this.cost = cost;
        this.maintenanceDate = maintenanceDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Maintenance() {
    }

    // used to log the details of a mantenance actions for a car
    public void logMaintenance(){
        this.maintenanceDate = LocalDateTime.now();
        this.createdAt= LocalDateTime.now();
        this.updatedAt= LocalDateTime.now();
        maintenanceHistory.add(this);
        System.out.println("Maintenance Logged.");
    }

    // to update the maintenance details
    public void updateMaintenance(String status, float cost, String details){
        this.status=status;
        this.cost=cost;
        this.details=details;
        this.updatedAt=LocalDateTime.now();
        System.out.println("Maintenance Updated.");
    }

    // to retreive maintenance history
    public static List<Maintenance> getMaintenanceHistory(){
        return maintenanceHistory;
    }

    // method to print all maintenance history details for reporting.
    public static void printMaintenanceHistory() {
        System.out.println("---- Maintenance History ----");
        for (Maintenance m : maintenanceHistory) {
            System.out.printf("ID: %d | Status: %s | Cost: %.2f | Date: %s\nDetails: %s\n\n",
                    m.getMaintenanceID(), m.getStatus(), m.getCost(), m.getMaintenanceDate(), m.getDetails());
        }
    }
    
}

