package com.example.trading_system.domain.stores;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;

public class CartDTO {
    private HashMap<String, ShoppingBagDTO> shoppingBags;

    public CartDTO() {
        shoppingBags = new HashMap<>();
    }

    public static CartDTO fromJson(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, CartDTO.class);
    }

    public HashMap<String, ShoppingBagDTO> getShoppingBags() {
        return shoppingBags;
    }

    public void setShoppingBags(HashMap<String, ShoppingBagDTO> shoppingBags) {
        this.shoppingBags = shoppingBags;
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
