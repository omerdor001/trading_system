package com.example.trading_system.domain.users;

import com.example.trading_system.domain.stores.Product;
import com.example.trading_system.domain.stores.ProductInSale;
import com.example.trading_system.domain.stores.Purchase;
import com.example.trading_system.domain.stores.Store;

import java.time.LocalDate;
import java.util.*;

public abstract class User {
    public String username;
    private Cart cart;

    public User(String username) {
        this.username=username;
        this.cart = new Cart();
    }

    public String getUsername() {
        return username;
    }

    public void setId(String username) {
        this.username = username;
    }

    public String getPass() {
        throw new RuntimeException("Only registered users have a password");
    }

    public abstract void login();

    public abstract void logout();

    public abstract void openStore(String storeName);

    public abstract boolean isOwner(String store_name_id);

    public abstract void addWaitingAppoint_Owner(String storeName);

    public abstract boolean isManager(String store_name_id);

    public abstract void addWaitingAppoint_Manager(String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy);

    public abstract void removeWaitingAppoint_Owner(String storeName);

    public abstract List<Boolean> removeWaitingAppoint_Manager(String store_name_id);

    public abstract void addManagerRole(String appoint, String store_name_id);

    public abstract void setPermissionsToManager(String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy);

    public abstract void addOwnerRole(String appoint, String storeName);

    public abstract Role getRoleByStoreId(String store_name_id);

    public abstract boolean isAdmin();

    public abstract void setAdmin(boolean value);

    public Cart getCart() {
        return cart;
    }

    public String getShoppingCart_ToString() {
        return cart.getShoppingBags_ToString();
    }
    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public abstract boolean getLogged();

    public abstract List<Notification> getNotifications();

    public abstract void receiveNotification(String notification);

    public abstract String getAddress();

    public abstract LocalDate getBirthdate();

    public String sendNotification(String receiverUsername, String content) {
        Notification notification = new Notification(this.username, receiverUsername, new Date(), content);
        return notification.toString();
    }

    public void addProductToCart(int productId, int quantity, String storeName,double price) {
        this.cart.addProductToCart(productId,quantity,storeName,price);
    }

    public void removeProductFromCart(int productId, int quantity, String storeName) {
        this.cart.removeProductFromCart(productId, quantity, storeName);
    }

    public List<Purchase> addPurchasedProduct() {
       return  cart.purchaseProduct(this.username);
    }


    public int checkProductQuantity(int productId, String storeName) {
        return cart.checkProductQuantity(productId,storeName);
    }

    public void releaseReservedProducts() {
//        cart.releaseReservedProducts();

    }
}
