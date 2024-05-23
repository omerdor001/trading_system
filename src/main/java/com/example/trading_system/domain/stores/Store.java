package com.example.trading_system.domain.stores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Store {
    private String name_id;//this will be the ID for the store
    private String description;
    private HashMap<Integer, Product> products;
    private static final Logger logger = LoggerFactory.getLogger(Store.class);

    public Store(String name_id, String description) {
        this.name_id = name_id;
        this.description = description;
        this.products = new HashMap<>();
    }

    public String getName_id() {
        return name_id;
    }

    public void setName_id(String name_id) {
        this.name_id = name_id;
    }

    public HashMap<Integer, Product> getProducts() {
        return products;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Product> filterProducts(List<Product> productList, Double minPrice, Double maxPrice, Double minRating, Category category) {
        return productList.stream()
                .filter(p -> minPrice == null || p.getProduct_price() >= minPrice)
                .filter(p -> maxPrice == null || p.getProduct_price() <= maxPrice)
                .filter(p -> minRating == null || p.getRating() >= minRating)
                .filter(p -> category == null || p.getCategory() == category)
                .collect(Collectors.toList());
    }

    public List<Product> searchName(String name, Double minPrice, Double maxPrice, Double minRating, Category category) {
        List<Product> list_products = new ArrayList<>();
        for (Product p : products.values()) {
            if (p.getProduct_name().equals(name))
                list_products.add(p);
        }
        return filterProducts(list_products, minPrice, maxPrice, minRating, category);
    }

    public List<Product> searchCategory(Category category, Double minPrice, Double maxPrice, Double minRating) {
        List<Product> list_products = new ArrayList<>();
        for (Product p : products.values()) {
            if (p.getCategory().equals(category))
                list_products.add(p);
        }
        return filterProducts(list_products, minPrice, maxPrice, minRating, category);
    }

    public List<Product> searchKeywords(String keyWords, Double minPrice, Double maxPrice, Double minRating, Category category) {
        List<Product> list_products = new ArrayList<>();
        for (Product p : products.values()) {
            if (p.getKeyWords().contains(keyWords))
                list_products.add(p);
        }
        return filterProducts(list_products, minPrice, maxPrice, minRating, category);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"name_id\":\"").append(name_id).append("\"");
        sb.append(", \"description\":\"").append(description).append("\"");
        sb.append(", \"products\":[");

        for (Product p : products.values()) {
            sb.append(p.toString());
            sb.append(", ");
        }

        sb.append("]");
        sb.append("}");
        return sb.toString();
    }

    public Product getProduct(int productId) {
        return products.get(productId);
    }

    public void addProductToStore(Product product) {
        products.put(product.getProduct_id(), product);
    }

    public void removeProductToStore(Product product) {
        products.remove(product.getProduct_id(), product);
    }

    public void setProduct_name(int productId,String product_name) {
        Product product=getProduct(productId);
        product.setProduct_name(product_name);
    }

    public void setProduct_description(int productId,String product_description){
        Product product=getProduct(productId);
        product.setProduct_description(product_description);
    }

    public void setProduct_price(int productId,int product_price) {
        Product product=getProduct(productId);
        product.setProduct_price(product_price);
    }

    public void setProduct_quantity(int productId,int product_quantity) {
        Product product=getProduct(productId);
        product.setProduct_quantity(product_quantity);
    }

    public void setRating(int productId,int rating){
        Product product=getProduct(productId);
        product.setRating(rating);
    }

    public void setCategory(int productId,Category category) {
        Product product=getProduct(productId);
        product.setCategory(category);
    }


    }
