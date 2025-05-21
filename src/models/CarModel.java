package models;

public class CarModel {
    private int modelId;
    private int brandId;
    private String modelName;
    private String fuelType;

    public CarModel() {
    }

    public CarModel(int modelId, int brandId, String modelName, String fuelType) {
        this.modelId = modelId;
        this.brandId = brandId;
        this.modelName = modelName;
        this.fuelType = fuelType;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    @Override
    public String toString() {
        return "CarModel{" +
                "modelId=" + modelId +
                ", brandId=" + brandId +
                ", modelName='" + modelName + '\'' +
                ", fuelType='" + fuelType + '\'' +
                '}';
    }
}