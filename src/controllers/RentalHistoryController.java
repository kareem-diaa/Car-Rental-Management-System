package controllers;

import dao.RentalHistoryDAO;
import models.RentalHistory;

import java.util.List;

public class RentalHistoryController {
    private final RentalHistoryDAO rentalHistoryDAO;

    public RentalHistoryController() {
        this.rentalHistoryDAO = new RentalHistoryDAO();
    }

    public boolean addRentalHistory(RentalHistory rh) {
        return rentalHistoryDAO.addRentalHistory(rh);
    }

    public RentalHistory getRentalHistoryById(int rentalId) {
        return rentalHistoryDAO.getRentalHistoryById(rentalId);
    }

    public List<RentalHistory> getAllRentalHistories() {
        return rentalHistoryDAO.getAllRentalHistories();
    }

    public boolean updateRentalHistory(RentalHistory rh) {
        return rentalHistoryDAO.updateRentalHistory(rh);
    }

    public boolean deleteRentalHistory(int rentalId) {
        return rentalHistoryDAO.deleteRentalHistory(rentalId);
    }
}