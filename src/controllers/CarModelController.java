package controllers;

import dao.CarModelDAO;
import models.CarModel;
import utils.ValidationException;
import utils.ValidationUtil;

import java.util.List;

public class CarModelController {
    private final CarModelDAO carModelDAO;

    public CarModelController() {
        this.carModelDAO = new CarModelDAO();
    }

    public boolean addCarModel(int brandId, String modelName, String fuelType) throws ValidationException {
        CarModel carModel = new CarModel();
        carModel.setBrandId(brandId);
        carModel.setModelName(modelName);
        carModel.setFuelType(fuelType);
        ValidationUtil.isValidName(carModel.getModelName());
        ValidationUtil.isValidName(carModel.getFuelType());
        return carModelDAO.addCarModel(carModel);
    }

    public boolean updateCarModel(int modelId, int brandId, String modelName, String fuelType) throws ValidationException {
        CarModel carModel = new CarModel();
        carModel.setModelId(modelId);
        carModel.setBrandId(brandId);
        carModel.setModelName(modelName);
        carModel.setFuelType(fuelType);
        ValidationUtil.isValidName(carModel.getModelName());
        ValidationUtil.isValidName(carModel.getFuelType());
        return carModelDAO.updateCarModel(carModel);
    }

    public boolean deleteCarModel(int modelId) {
        return carModelDAO.deleteCarModel(modelId);
    }

    public CarModel getCarModelById(int modelId) {
        return carModelDAO.getCarModelById(modelId);
    }

    public List<CarModel> getAllCarModels() {
        return carModelDAO.getAllCarModels();
    }
}