package models;

public class Discount {
    private int discountId;
    private String promotionCode; // New field for promotion code
    private double discountPercentage;

    public Discount() {}

    public Discount(int discountId, String promotionCode, double discountPercentage) {
        this.discountId = discountId;
        this.promotionCode = promotionCode;
        this.discountPercentage = discountPercentage;
    }

    public int getDiscountId() {
        return discountId;
    }

    public void setDiscountId(int discountId) {
        this.discountId = discountId;
    }

    public String getPromotionCode() {
        return promotionCode;
    }

    public void setPromotionCode(String promotionCode) {
        this.promotionCode = promotionCode;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
}