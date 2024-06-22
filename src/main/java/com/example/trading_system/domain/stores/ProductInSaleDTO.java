package com.example.trading_system.domain.stores;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ProductInSaleDTO {
    private String storeId;
    private int id;
    private double price;
    private int quantity;
    private int category;

    public ProductInSaleDTO() {
    }

    public static ProductInSaleDTO fromJson(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, ProductInSaleDTO.class);
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }

    public static LinkedList<ProductInSaleDTO> fromJsonList(String json){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<ProductInSaleDTO> productList = objectMapper.readValue(
                    json, TypeFactory.defaultInstance().constructCollectionType(List.class, ProductInSaleDTO.class));
            return new LinkedList<>(productList);
        }
        catch (JsonProcessingException e){
            throw new RuntimeException("Error reading product list json");
        }
    }
}
