package com.example.trading_system.domain.stores;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.util.List;

@Getter

public class Purchase {
    private List<ProductInSaleDTO> productInSaleList;
    @Getter
    private String customerUsername;
    private double totalPrice;
    @Getter
    private String storeName;


    public Purchase(String customerUsername, List<ProductInSaleDTO> productInSaleList, double totalPrice, String storeName) {
        this.customerUsername = customerUsername;
        this.productInSaleList = productInSaleList;
        this.totalPrice = totalPrice;
        this.storeName = storeName;
    }

    public void addProduct(ProductInSaleDTO product) {
        productInSaleList.add(product);
        totalPrice += product.getPrice();
    }

    @Override
    public String toString() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(this);
        }
        catch (JsonProcessingException e){
            throw new RuntimeException("Error converting Purchase to JSON");
        }
    }
}
