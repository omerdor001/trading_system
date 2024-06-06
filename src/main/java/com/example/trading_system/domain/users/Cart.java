package com.example.trading_system.domain.users;

import com.example.trading_system.domain.stores.ProductInSale;
import com.example.trading_system.domain.stores.Purchase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void addProductToCart(int productId, int quantity, String storeId,double price) {
        ShoppingBag shoppingBag = shoppingBags.get(storeId);
        if (shoppingBag == null) {
            shoppingBag = new ShoppingBag(storeId);
            shoppingBags.put(storeId, shoppingBag);
        }
        shoppingBag.addProduct(productId, quantity,price);
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

    public void saveCart() {
        //TODO when connecting to database.
    }

    public String getShoppingBags_ToString() {
        StringBuilder cartDetails = new StringBuilder();
/*        double totalAllStores = 0.0;
        for (Map.Entry<String, ShoppingBag> entry : shoppingBags.entrySet()) {
            String storeId = entry.getKey();
            ShoppingBag shoppingBag = entry.getValue();
            cartDetails.append("Store name: ").append(storeId).append("\n");
            double totalStore = 0.0;
            for (Map.Entry<Integer, Integer> productEntry : shoppingBag.getProducts_list().entrySet()) {
                Product product = marketFacade.getStores().get(storeId).getProducts().get(productEntry.getKey());
                int quantity = productEntry.getValue();
                double price = product.getProduct_price();
                double totalPrice = price * quantity;
                totalStore += totalPrice;
                cartDetails.append("Product Id: ").append(product.getProduct_id()).append(", Name: ").append(product.getProduct_name())
                        .append(", Quantity: ").append(quantity).append(", Price per unit: ").append(price).append(", Total Price: ").append(totalPrice).append("\n");
            }
            cartDetails.append("Total for Store name ").append(storeId).append(": ").append(totalStore).append("\n\n");
            totalAllStores += totalStore;
        }
        cartDetails.append("Overall Total for All Stores: ").append(totalAllStores).append("\n");*/
        return cartDetails.toString();
    }

    private List<ProductInSale> getProductsToList(){
        List list = new ArrayList<>();
        for(ShoppingBag shoppingBag : shoppingBags.values()){
            list.add(shoppingBag.getProducts_list().values());
        }
        return list;
    }

    public Purchase purchaseProduct(String username) {
        double totalcount = 0;
        List<ProductInSale> productInSales = new ArrayList<>();
        for (Map.Entry<String, ShoppingBag> entry : shoppingBags.entrySet()) {
            ShoppingBag shoppingBag = entry.getValue();
            for (Map.Entry<Integer, ProductInSale> productEntry : shoppingBag.getProducts_list().entrySet()) {
                totalcount += productEntry.getValue().sumTotalPrice();
                productInSales.add(productEntry.getValue());
            }
        }
            Purchase purchase = new Purchase(username, getProductsToList(), totalcount);
            return purchase;

        }

}
