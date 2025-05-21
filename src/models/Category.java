package models;
import java.time.LocalDateTime;

public class Category {
    private int categoryID;
    private String name, description, categoryIMG;
    private LocalDateTime createdAt,updatedAt;

    public Category() {
    }

    public Category(int categoryID, String name, String description, String categoryIMG, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.categoryID = categoryID;
        this.name = name;
        this.description = description;
        this.categoryIMG = categoryIMG;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }
    public void setCategoryIMG(String categoryIMG) {
        this.categoryIMG = categoryIMG;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getCategoryID() {
        return categoryID;
    }
    public String getCategoryIMG() {
        return categoryIMG;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public String getDescription() {
        return description;
    }
    public String getName() {
        return name;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    

}

