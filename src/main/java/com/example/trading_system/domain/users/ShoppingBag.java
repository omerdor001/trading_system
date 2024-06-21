package com.example.trading_system.domain.users;

import com.example.trading_system.domain.stores.MarketFacadeImp;
import com.example.trading_system.domain.stores.ProductInSale;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;

public class ShoppingBag {
    private String storeId;
    private HashMap<Integer, ProductInSale> products_list;

    public ShoppingBag(String storeId) {
        this.storeId = storeId;
        products_list = new HashMap<>();
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public HashMap<Integer, ProductInSale> getProducts_list() {
        return products_list;
    }

    public synchronized void addProduct(int productId, int quantity, double price, int category) {
        if (products_list.containsKey(productId)) {
            products_list.get(productId).addQuantity(quantity);
        } else products_list.put(productId, new ProductInSale(storeId, productId, price, quantity, category));
    }

    public synchronized void removeProduct(int productId, int quantity) {
        if (!products_list.containsKey(productId)) {
            throw new IllegalArgumentException("Product not found in shopping bag.");
        }
        if (products_list.get(productId).getQuantity() - quantity <= 0) {
            products_list.remove(productId);
        } else products_list.get(productId).reduceQuantity(quantity);
    }

    public double calculateTotalPrice() {
        double total = 0;
        for (ProductInSale product : products_list.values()) {
            total += product.sumTotalPrice();
        }
        return total;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ShoppingBag: {storeId: ").append(storeId).append(", products_list: [");
        for (ProductInSale product : products_list.values()) {
            sb.append("\n    ").append(product.toString());
        }
        sb.append("\n  ]");
        sb.append(", Total Price: $").append(calculateTotalPrice());
        sb.append("}");
        return sb.toString();
    }

    public int checkProductQuantity(int productId) {
        return products_list.get(productId).getQuantity();
    }

    public void removeReservedProducts() {
        for (ProductInSale product : products_list.values()) {
            MarketFacadeImp.getInstance().removeReservedProducts(product.getId(), product.getQuantity(), product.getStoreId());
        }
    }

    public void releaseReservedProducts() {
        for (ProductInSale product : products_list.values()) {
            MarketFacadeImp.getInstance().releaseReservedProducts(product.getId(), product.getQuantity(), product.getStoreId());
        }
    }

    public void checkAvailabilityAndConditions() {
        for (ProductInSale product : products_list.values()) {
            MarketFacadeImp.getInstance().checkAvailabilityAndConditions(product.getId(), product.getQuantity(), product.getStoreId());
        }
    }

    public void addPurchase(String username) {
        try {
            MarketFacadeImp.getInstance().addPurchase(username, productsListToJson(), calculateTotalPrice(), storeId);
        } catch (IOException e) {
            throw new RuntimeException("error converting shopping bag to json");
        }
    }

    public String productsListToJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(products_list.values());
    }
}
