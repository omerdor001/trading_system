package com.example.trading_system.domain.users;

import com.example.trading_system.domain.stores.Product;

import java.util.HashMap;
import java.util.Map;

public class ShoppingBag {
    private String storeId;
    private HashMap<Integer,Integer> products_list;
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
    public HashMap<Integer,Integer> getProducts_list() {
        return products_list;
    }

    public synchronized void addProduct(int productId, int quantity) {
        if (products_list.containsKey(productId)) {
            products_list.put(productId, products_list.get(productId) + quantity);
        }
        else
            products_list.put(productId, quantity);
    }
    public synchronized void removeProduct(int productId,int quantity) {
        if (products_list.get(productId)-quantity == 0) {
            products_list.remove(productId);
        }
        else
            products_list.put(productId, products_list.get(productId) - quantity);
    }


}
