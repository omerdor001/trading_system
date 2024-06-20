package com.example.trading_system.domain.users;

import com.example.trading_system.domain.stores.Purchase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public abstract class User {
    public String username;
    private Cart cart;
    private boolean suspended;
    private LocalDateTime suspendedStart;
    private LocalDateTime suspendedEnd;
    private String address;
    private static final Logger logger = LoggerFactory.getLogger(User.class);


    public User(String username) {
        this.username = username;
        this.cart = new Cart();
        this.suspended = false;
        this.suspendedStart = null;
        this.suspendedEnd = null;
        this.address = "";
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

    public boolean isSuspended() {
        return suspended;
    }

    public LocalDateTime getSuspendedStart() {
        return suspendedStart;
    }

    public LocalDateTime getSuspendedEnd() {
        return suspendedEnd;
    }

    public void setSuspendedEnd(LocalDateTime suspendedEnd) {
        this.suspendedEnd = suspendedEnd;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void suspend(LocalDateTime suspendedUntil) {
        suspended = true;
        suspendedStart = LocalDateTime.now();
        setSuspendedEnd(suspendedUntil);
    }

    public void finishSuspension() {
        suspendedStart = null;
        suspendedEnd = null;
        suspended = false;
    }

    public abstract void login();

    public abstract void logout();

    public abstract void openStore(String storeName);

    public abstract boolean isOwner(String store_name_id);

    public abstract void addWaitingAppoint_Owner(String storeName);

    public abstract boolean isManager(String store_name_id);

    public abstract void addWaitingAppoint_Manager(String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy);

    public abstract boolean removeWaitingAppoint_Owner(String storeName);

    public abstract List<Boolean> removeWaitingAppoint_Manager(String store_name_id);

    public abstract void addManagerRole(String appoint, String store_name_id);

    public abstract void setPermissionsToManager(String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy);

    public abstract void addOwnerRole(String appoint, String storeName);

    public void removeOwnerRole(String storeName) {
        throw new RuntimeException("Only registered users can be owners.");
    }

    public void removeManagerRole(String storeName) {
        throw new RuntimeException("Only registered users can be managers.");
    }

    public abstract Role getRoleByStoreId(String store_name_id);

    public abstract boolean isAdmin();

    public abstract void setAdmin(boolean value);

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public abstract boolean getLogged();

    public abstract List<String> getOwnerToApprove();

    public abstract HashMap<String, List<Boolean>> getManagerToApprove();

    public abstract List<Notification> getNotifications();

    public abstract void receiveDelayedNotification(Notification notification);

    public abstract LocalDate getBirthdate();

    public abstract int getAge();

    public void addProductToCart(int productId, int quantity, String storeName, double price, int category) {
        this.cart.addProductToCart(productId, quantity, storeName, price, category);
    }

    public void removeProductFromCart(int productId, int quantity, String storeName) {
        this.cart.removeProductFromCart(productId, quantity, storeName);
    }


    public String getShoppingCart_ToString() {
        return cart.toString();
    }

    public int checkProductQuantity(int productId, String storeName) {
        return cart.checkProductQuantity(productId, storeName);
    }

    public void removeReservedProducts() {
        cart.removeReservedProducts();
    }

    public void releaseReservedProducts() {
        cart.releaseReservedProducts();
    }

    public void checkAvailabilityAndConditions() {
        if (cart == null || cart.getShoppingBags().isEmpty()) {
            logger.error("Cart is empty or null");
            throw new RuntimeException("Cart is empty or null");
        }
        if (!getLogged()) {
            logger.error("User is not logged in");
            throw new RuntimeException("User is not logged in");
        }
        cart.checkAvailabilityAndConditions();
    }

    public void addPurchase(String username) {
        cart.addPurchase(username);
    }
}
