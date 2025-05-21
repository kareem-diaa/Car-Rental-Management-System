package controllers;

import dao.CarBrandDAO;
import models.CarBrand;
import utils.ValidationException;
import utils.ValidationUtil;

import java.util.List;

public class BrandController {
    private final CarBrandDAO brandDAO;

    public BrandController() {
        this.brandDAO = new CarBrandDAO();
    }

    public boolean addBrand(String brandName) throws ValidationException {
        CarBrand brand = new CarBrand();
        brand.setBrandName(brandName);
        ValidationUtil.isValidName(brand.getBrandName());
        return brandDAO.addBrand(brand);
    }

    public boolean updateBrand(int brandId, String brandName)throws ValidationException {
        CarBrand brand = new CarBrand();
        brand.setBrandId(brandId);
        brand.setBrandName(brandName);
        ValidationUtil.isValidName(brand.getBrandName());
        return brandDAO.updateBrand(brand);
    }

    public boolean deleteBrand(int brandId) {
        return brandDAO.deleteBrand(brandId);
    }

    public CarBrand getBrandById(int brandId) {
        return brandDAO.getBrandById(brandId);
    }

    public List<CarBrand> getAllBrands() {
        return brandDAO.getAllBrands();
    }
}