package com.example.trading_system.domain.stores;

import java.util.HashMap;

public class Store {
    private String name_id;//this will be the ID for the store
    private String description;
    private HashMap<Integer, Product> products;

    public Store( String name_id,String description){
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
}
