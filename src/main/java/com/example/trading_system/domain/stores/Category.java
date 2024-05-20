package com.example.trading_system.domain.stores;

public class Category {
    private String categoryName;
    private String categoryDescription;
    private List<Product> products = new ArrayList<>();
    public  Category(String categoryName,String categoryDescription){
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
        this.products = new ArrayList<>();
    }
    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryDescription) {
        this.categoryName = categoryName;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }
    public List<Product> getProducts() {
        return products;
    }
    public void addProduct(Product product) {
        products.add(product);
        product.setCategory(categoryName);
    }
    public void removeProduct(Product product) {
        products.remove(product);
    }
    
}
