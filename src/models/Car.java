package models;

import java.time.LocalDateTime;

public class Car {
    private int carID;
    private int modelID;
    private int categoryID;
    private int mileage;
    private Boolean availabilityStatus;
    private float rentalPrice;
    private String plateNo;
    private String imageURL;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Car() {
    }

    public Car(int carID, int modelID, int categoryID, int mileage, Boolean availabilityStatus, float rentalPrice,
                String plateNo, String imageURL, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.carID = carID;
        this.modelID = modelID;
        this.categoryID = categoryID;
        this.mileage = mileage;
        this.availabilityStatus = availabilityStatus;
        this.rentalPrice = rentalPrice;
        this.plateNo = plateNo;
        this.imageURL = imageURL;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getCarID() {
        return carID;
    }

    public void setCarID(int carID) {
        this.carID = carID;
    }

    public int getModelID() {
        return modelID;
    }

    public void setModelID(int modelID) {
        this.modelID = modelID;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public boolean getAvailabilityStatus() {
        return this.availabilityStatus;
    }

    public void setAvailabilityStatus(Boolean availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public float getRentalPrice() {
        return rentalPrice;
    }

    public void setRentalPrice(float rentalPrice) {
        this.rentalPrice = rentalPrice;
    }


    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Car{" +
                "carID=" + carID +
                ", modelID=" + modelID +
                ", categoryID=" + categoryID +
                ", mileage=" + mileage +
                ", availabilityStatus='" + availabilityStatus + '\'' +
                ", rentalPrice=" + rentalPrice +
                ", plateNo='" + plateNo + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}