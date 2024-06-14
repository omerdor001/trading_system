package com.example.trading_system.domain.stores;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;

public class ShoppingBagDTO {
    private String storeId;
    private HashMap<Integer, ProductInSaleDTO> productsList;

    public ShoppingBagDTO() {
        productsList = new HashMap<>();
    }

    public static ShoppingBagDTO fromJson(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, ShoppingBagDTO.class);
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public HashMap<Integer, ProductInSaleDTO> getProductsList() {
        return productsList;
    }

    public void setProductsList(HashMap<Integer, ProductInSaleDTO> productsList) {
        this.productsList = productsList;
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
