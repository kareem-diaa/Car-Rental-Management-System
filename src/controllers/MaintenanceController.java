package controllers;

import dao.MaintenanceDAO;
import models.Maintenance;
import utils.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

public class MaintenanceController {
    private final MaintenanceDAO maintenanceDAO;

    public MaintenanceController() {
        this.maintenanceDAO = new MaintenanceDAO();
    }

    // Add a new maintenance record
    public boolean addMaintenance(int carId, String status, String details, float cost) throws ValidationException {
        // Validate inputs
        if (cost < 0) {
            throw new ValidationException("Cost cannot be negative");
        }
        if (status == null || status.trim().isEmpty()) {
            throw new ValidationException("Status cannot be empty");
        }
        
        Maintenance maintenance = new Maintenance();
        maintenance.setCarId(carId);
        maintenance.setStatus(status);
        maintenance.setDetails(details);
        maintenance.setCost(cost);
        maintenance.setMaintenanceDate(LocalDateTime.now());
        maintenance.setCreatedAt(LocalDateTime.now());
        maintenance.setUpdatedAt(LocalDateTime.now());
        
        return maintenanceDAO.addMaintenance(maintenance, carId);
    }

    // Update an existing maintenance record
    public boolean updateMaintenance(int maintenanceId, int carId, String status, String details, float cost) throws ValidationException {
        // Validate inputs
        if (cost < 0) {
            throw new ValidationException("Cost cannot be negative");
        }
        if (status == null || status.trim().isEmpty()) {
            throw new ValidationException("Status cannot be empty");
        }
        
        Maintenance maintenance = maintenanceDAO.getMaintenanceById(maintenanceId);
        if (maintenance != null) {
            maintenance.setCarId(carId);
            maintenance.setStatus(status);
            maintenance.setDetails(details);
            maintenance.setCost(cost);
            maintenance.setUpdatedAt(LocalDateTime.now());
            
            return maintenanceDAO.updateMaintenance(maintenance);
        }
        return false;
    }

    // Delete a maintenance record
    public boolean deleteMaintenance(int maintenanceId) {
        return maintenanceDAO.deleteMaintenance(maintenanceId);
    }

    // Get a maintenance record by ID
    public Maintenance getMaintenanceById(int maintenanceId) {
        return maintenanceDAO.getMaintenanceById(maintenanceId);
    }

    // Get all maintenance records
    public List<Maintenance> getAllMaintenance() {
        return maintenanceDAO.getAllMaintenance();
    }
}
