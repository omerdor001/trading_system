package com.example.trading_system.domain.stores;


import lombok.*;

@Getter
@Setter
public class Product {
    private int product_id;
    private String product_name;
    private String product_description;
    private int product_price;
    private int product_quantity;
    private int rating;
    private String store_id;
    private String category_id;
    public Product(int product_id, String product_name, String product_description,
                   int product_price, int product_quantity, int rating) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.product_description = product_description;
        this.product_price = product_price;
        this.product_quantity = product_quantity;
        this.rating = rating;
        this.store_id = "";
        this.category_id = "";
    }

    public int getProduct_id() {
        return product_id;
    }
    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }
    public String getProduct_name() {
        return product_name;
    }
    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }
    public String getProduct_description() {
        return product_description;
    }
    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }
    public int getProduct_price() {
        return product_price;
    }
    public void setProduct_price(int product_price) {
        this.product_price = product_price;
    }
    public int getProduct_quantity() {
        return product_quantity;
    }
    public void setProduct_quantity(int product_quantity) {
        this.product_quantity = product_quantity;
    }
    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
    public String getStore_id() {
        return store_id;
    }
    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }
    public String getCategory_id() {
        return category_id;
    }
    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

}
