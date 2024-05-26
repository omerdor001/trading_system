package com.example.trading_system.domain.users;

import java.util.HashMap;

public class Cart {
    private HashMap<String, ShoppingBag> shoppingBags;

    public Cart() {
        shoppingBags = new HashMap<>();
    }

    public HashMap<String, ShoppingBag> getShoppingBags() {
        return shoppingBags;
    }

    public void addShoppingBag(ShoppingBag shoppingBag, String storeId) {
        shoppingBags.put(storeId, shoppingBag);
    }

    public void addProductToCart(int productId, int quantity, String storeId) {
        ShoppingBag shoppingBag = shoppingBags.get(storeId);
        shoppingBag.addProduct(productId, quantity);
    }

    public void removeProductFromCart(int productId, int quantity, String storeId) {
        ShoppingBag shoppingBag = shoppingBags.get(storeId);
        shoppingBag.removeProduct(productId, quantity);
    }

    public void addShoppingBagToCart(ShoppingBag shoppingBag, String storeId) {
        shoppingBags.put(storeId, shoppingBag);
    }

    public void removeShoppingBagFromCart(ShoppingBag shoppingBag, String storeId) {
        shoppingBags.remove(storeId);
    }

    public void saveCart() {
        //TODO when connecting to database.
    }
}
