package com.example.trading_system.domain.stores;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;

public class ShoppingBagDTO {
    private String storeId;
    private HashMap<Integer, ProductInSaleDTO> products_list;

    public ShoppingBagDTO() {
        products_list = new HashMap<>();
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

    public HashMap<Integer, ProductInSaleDTO> getProducts_list() {
        return products_list;
    }

    public void setProducts_list(HashMap<Integer, ProductInSaleDTO> products_list) {
        this.products_list = products_list;
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
