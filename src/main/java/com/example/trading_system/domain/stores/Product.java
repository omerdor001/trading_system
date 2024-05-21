package com.example.trading_system.domain.stores;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private int product_id;
    private String store_name;
    private String product_name;
    private String product_description;
    private int product_price;
    private int product_quantity;
    private int rating;
    private Category category;
    private List<String> keyWords;

    public Product(int product_id, String product_name, String product_description,
                   int product_price, int product_quantity, int rating) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.product_description = product_description;
        this.product_price = product_price;
        this.product_quantity = product_quantity;
        this.rating = rating;
        this.store_name = "";
        this.category = null;
        this.keyWords = new ArrayList<String>();
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
    public String getStore_name() {
        return store_name;
    }
    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }
    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }
    public List<String> getKeyWords() {return keyWords;}

    public void addKeyWord(String keyWord) {
        this.keyWords.add(keyWord);
    }
    public String toString() {
        return "{" +
                "\"product_id\":" + product_id +
                ", \"store_name\":\"" + store_name + "\"" +
                ", \"product_name\":\"" + product_name + "\"" +
                ", \"product_description\":\"" + product_description + "\"" +
                ", \"product_price\":" + product_price +
                ", \"product_quantity\":" + product_quantity +
                ", \"rating\":" + rating +
                ", \"category\":" + (category != null ? category.toString() : "null") +
                ", \"keyWords\":" + keyWords +
                '}';
    }

}
