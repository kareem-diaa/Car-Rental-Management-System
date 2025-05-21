package controllers;

import java.util.List;
import dao.DiscountDAO;
import models.Discount;
import utils.ValidationException;
import utils.ValidationUtil;

public class DiscountController {
    private final DiscountDAO discountDAO;

    public DiscountController() {
        this.discountDAO = new DiscountDAO();
    }

    // Create a new discount
    public boolean addDiscount(Discount discount) throws ValidationException {
        ValidationUtil.isValidPromotionCode(discount.getPromotionCode());
        ValidationUtil.isValidDiscountPercentage(String.valueOf(discount.getDiscountPercentage()));
        return discountDAO.addDiscount(discount);
    }

    // Read a discount by ID
    public Discount getDiscountById(int discountId) {
        return discountDAO.getDiscountById(discountId);
    }

    // Update an existing discount
    public boolean updateDiscount(Discount discount) throws ValidationException {
        ValidationUtil.isValidPromotionCode(discount.getPromotionCode());
        ValidationUtil.isValidDiscountPercentage(String.valueOf(discount.getDiscountPercentage()));
        return discountDAO.updateDiscount(discount);
    }

    // Delete a discount by ID
    public boolean deleteDiscount(int discountId) {
        return discountDAO.deleteDiscount(discountId);
    }

    // Get all discounts
    public List<Discount> getAllDiscounts() {
        return discountDAO.getAllDiscounts();
    }
}