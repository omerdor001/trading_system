package com.example.trading_system.domain.users;

import com.example.trading_system.domain.stores.ProductInSale;
import com.example.trading_system.domain.stores.StoreRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cart {
    private final HashMap<String, ShoppingBag> shoppingBags;

    public Cart() {
        shoppingBags = new HashMap<>();
    }

    public static Cart fromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, Cart.class);
    }

    public HashMap<String, ShoppingBag> getShoppingBags() {
        return shoppingBags;
    }

    public void addShoppingBag(ShoppingBag shoppingBag, String storeId) {
        shoppingBags.put(storeId, shoppingBag);
    }

    public void addProductToCart(int productId, int quantity, String storeId, double price, int category) {
        ShoppingBag shoppingBag = shoppingBags.get(storeId);
        if (shoppingBag == null) {
            shoppingBag = new ShoppingBag(storeId);
            shoppingBags.put(storeId, shoppingBag);
        }
        shoppingBag.addProduct(productId, quantity, price, category);
    }

    public void removeProductFromCart(int productId, int quantity, String storeId) {
        ShoppingBag shoppingBag = shoppingBags.get(storeId);
        if (shoppingBag != null) {
            shoppingBag.removeProduct(productId, quantity);
            if (shoppingBag.getProducts_list().isEmpty()) {
                shoppingBags.remove(storeId);
            }
        } else {
            throw new IllegalArgumentException("Shopping bag for store " + storeId + " does not exist.");
        }
    }

    public void addShoppingBagToCart(ShoppingBag shoppingBag, String storeId) {
        shoppingBags.put(storeId, shoppingBag);
    }

    public void removeShoppingBagFromCart(ShoppingBag shoppingBag, String storeId) {
        shoppingBags.remove(storeId);
    }

    public void removeShoppingBagFromCartByStore(String storeName) {
        shoppingBags.remove(storeName);
    }

    public void saveCart() {
        //TODO when connecting to database.
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cart: {");
        double totalCartPrice = 0;
        for (Map.Entry<String, ShoppingBag> entry : shoppingBags.entrySet()) {
            ShoppingBag shoppingBag = entry.getValue();
            double bagTotalPrice = shoppingBag.calculateTotalPrice();
            totalCartPrice += bagTotalPrice;
            sb.append("\n  Store ID: ").append(entry.getKey())
                    .append(", Shopping Bag: ").append(shoppingBag)
                    .append(", Total Price: $").append(bagTotalPrice);
        }
        sb.append("\n  Total Cart Price: $").append(totalCartPrice);
        sb.append("\n}");
        return sb.toString();
    }

    private List<ProductInSale> getProductsToList() {
        List list = new ArrayList<>();
        for (ShoppingBag shoppingBag : shoppingBags.values()) {
            list.add(shoppingBag.getProducts_list().values());
        }
        return list;
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }

    public int checkProductQuantity(int productId, String storeName) {
        if (shoppingBags.get(storeName) == null)
            return 0;
        return shoppingBags.get(storeName).checkProductQuantity(productId);
    }

    public void removeReservedProducts(StoreRepository storeRepository) {
        for (ShoppingBag shoppingBagInStore : shoppingBags.values()) {
            shoppingBagInStore.removeReservedProducts(storeRepository);
        }
    }

    public void releaseReservedProducts(StoreRepository storeRepository) {
        for (ShoppingBag shoppingBagInStore : shoppingBags.values()) {
            shoppingBagInStore.releaseReservedProducts(storeRepository);
        }
    }

    public void checkAvailabilityAndConditions(StoreRepository storeRepository) {
        for (ShoppingBag shoppingBagInStore : shoppingBags.values()) {
            shoppingBagInStore.checkAvailabilityAndConditions(storeRepository);
        }
    }

    public void addPurchase(StoreRepository storeRepository,String username) {
        for (ShoppingBag shoppingBagInStore : shoppingBags.values()) {
            shoppingBagInStore.addPurchase(storeRepository,username);
        }
    }
}
