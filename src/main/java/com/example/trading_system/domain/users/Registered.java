package com.example.trading_system.domain.users;

import java.util.LinkedList;
import java.util.List;

public class Registered extends User {
    private int userId;
    private String userName;
    private String address;
    private int age;
    private boolean isAdmin;
    private boolean isLogged;
    private List<Notification> notifications;

    public Registered(int id, int userId, String userName, String address, int age) {
        super(id);
        this.userId = userId;
        this.userName = userName; // Can be changed to email
        this.address = address;
        this.age = age;
        this.isAdmin = false;
        this.isLogged = false;
        this.notifications = new LinkedList<>();
    }

    public void logout() {}
    public void openStore() {}
    public void performBuying(Cart shopping_cart) {}
    public boolean approveAppointment(int userId) { return false; }
    public void searchProduct(int productId) {}
    public void seeHistoryPurchase() {}
    public void seeHistoryPurchase(int storeId, int userId, int productId) {}
    public void openMarket() {}
    public void closeMarket() {}

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
        //TODO add distinction if logged in or not
        Notification notification = Notification.fromString(notificationString);
        this.notifications.add(notification);
    }
}
