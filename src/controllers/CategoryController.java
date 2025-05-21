package controllers;

import dao.CategoryDAO;
import models.Category;
import utils.ValidationException;
import utils.ValidationUtil;

public class CategoryController {
    private final CategoryDAO categoryDAO;

    public CategoryController() {
        this.categoryDAO = new CategoryDAO();
    }

    public boolean addCategory(String name, String description, String categoryImg) throws ValidationException {
        ValidationUtil.isValidName(name);  
        ValidationUtil.isValidDescription(description); 
        ValidationUtil.isValidURL(categoryImg);

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setCategoryIMG(categoryImg);
        category.setCreatedAt(java.time.LocalDateTime.now());
        category.setUpdatedAt(java.time.LocalDateTime.now());
        return categoryDAO.addCategory(category);
    }

    public boolean updateCategory(int id, String name, String description, String categoryImg) throws ValidationException {
        ValidationUtil.isValidName(name); 
        ValidationUtil.isValidDescription(description); 
        ValidationUtil.isValidURL(categoryImg); 

        Category category = new Category();
        category.setCategoryID(id);
        category.setName(name);
        category.setDescription(description);
        category.setCategoryIMG(categoryImg);
        category.setUpdatedAt(java.time.LocalDateTime.now());
        return categoryDAO.updateCategory(category);
    }

    public boolean deleteCategory(int id) {
        return categoryDAO.deleteCategory(id);
    }

    public java.util.List<Category> getAllCategories() {
        return categoryDAO.getAllCategories();
    }
}