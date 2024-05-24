package com.example.trading_system.domain.users;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class Registered extends User {
    private String userName;
    private String encrypted_pass;
    private String address;
    private LocalDate birthdate;
    private boolean isAdmin;
    private boolean isLogged;
    private List<Notification> notifications;

    public Registered(int id, String userName, String encrypted_pass, String address, LocalDate birthdate) {
        super(id);
        this.userName = userName; // Can be changed to email
        this.encrypted_pass = encrypted_pass;
        this.address = address;
        this.birthdate= birthdate;
        this.isAdmin = false;
        this.isLogged = false;
        this.notifications = new LinkedList<>();
    }

    public Registered(int id, String userName, String encryption, LocalDate birthdate) {
        super(id);
        this.userName = userName; // Can be changed to email
        this.encrypted_pass = encryption;
        this.address = "No address";
        this.birthdate= birthdate;
        this.isAdmin = false;
        this.isLogged = false;
        this.notifications = new LinkedList<>();
    }

    public void logout() {}
    public void performBuying(Cart shopping_cart) {}
    public boolean approveAppointment(int userId) { return false; }
    public void searchProduct(int productId) {}
    public void seeHistoryPurchase() {}
    public void seeHistoryPurchase(int storeId, int userId, int productId) {}
    public void openMarket() {}
    public void closeMarket() {}

    public void openStore() {
        this.isAdmin=true;
        //SET THE ROLE TO OWNER OF STORE

    }

    @Override
    public String getPass(){
        return this.encrypted_pass;
    }

    @Override
    public void login(){
        this.isLogged = true;
    }

    @Override
    public boolean getLogged() {
        return this.isLogged;
    }

    @Override
    public List<Notification> getNotifications() {
        return notifications;
    }

    @Override
    public void receiveNotification(String notificationString) {
        if(!isLogged) {
            Notification notification = Notification.fromString(notificationString);
            this.notifications.add(notification);
        }
        else{
            //TODO show in UI
        }
    }
}
