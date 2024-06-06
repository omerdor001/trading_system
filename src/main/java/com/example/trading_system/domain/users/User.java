package com.example.trading_system.domain.users;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public abstract class User {
    public String username;
    private Cart shopping_cart;
    private boolean suspended;
    private LocalDateTime suspendedStart;
    private LocalDateTime suspendedEnd;

    public User(String username) {
        this.username=username;
        this.shopping_cart = new Cart();
        this.suspended=false;
        this.suspendedStart=null;
        this.suspendedEnd=null;
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

    public boolean isSuspended(){
        return suspended;
    }

    public void setSuspended(){
        this.suspended=!suspended;
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

    public void suspend(LocalDateTime suspendedUntil){
        setSuspended();
        this.suspendedStart=LocalDateTime.now();
        setSuspendedEnd(suspendedUntil);
    }

    public void setSuspendedUntilToDefault(){
        if(suspendedEnd.compareTo(LocalDateTime.now())==0){
            suspendedStart=null;
            suspendedEnd=null;
        }
    }

    public void finishSuspension(){
        if(suspended){
            setSuspendedUntilToDefault();
            setSuspended();
        }
    }

    public void finishSuspensionByAdmin(){
        suspendedStart=null;
        suspendedEnd=null;
        setSuspended();
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

    public Cart getShopping_cart() {
        return shopping_cart;
    }

    public void setShopping_cart(Cart shopping_cart) {
        this.shopping_cart = shopping_cart;
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

}
