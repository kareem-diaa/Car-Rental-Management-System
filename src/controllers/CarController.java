package controllers;

import dao.CarDAO;
import models.Car;
import utils.ValidationException;
import utils.ValidationUtil;

import java.time.LocalDateTime;
import java.util.List;

public class CarController {
    private final CarDAO carDAO;

    public CarController() {
        this.carDAO = new CarDAO();
    }

    // Add a new car
    public boolean addCar(Car car) throws ValidationException {

        if (car.getCreatedAt() == null) {
            car.setCreatedAt(LocalDateTime.now());
        }
        if (car.getUpdatedAt() == null) {
            car.setUpdatedAt(LocalDateTime.now());
        }

        ValidationUtil.isNumeric(Integer.toString(car.getMileage()));
        ValidationUtil.isValidFloat(Float.toString(car.getRentalPrice()));
        ValidationUtil.isValidURL(car.getImageURL());

        return carDAO.insertCar(car);
    }

    // Update an existing car
    public boolean updateCar(Car car) throws ValidationException {
        car.setUpdatedAt(LocalDateTime.now());
        ValidationUtil.isNumeric(car.getPlateNo());
        ValidationUtil.isValidURL(car.getImageURL());
        return carDAO.updateCar(car);
    }

    // Delete a car by ID
    public boolean deleteCar(int carId) {
        return carDAO.deleteCar(carId);
    }

    // Retrieve a car by ID
    public Car getCarById(int carId) {
        return carDAO.getCarById(carId);
    }

    // Retrieve all cars
    public List<Car> getAllCars() {
        return carDAO.getAllCars();
    }
}