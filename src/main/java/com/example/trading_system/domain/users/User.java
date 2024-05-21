package com.example.trading_system.domain.users;

import java.util.Date;
import java.util.List;

public abstract class User {
    public int id;
    private Cart shopping_cart;

    public User(int id) {
        this.id = id;            //Might be change ?
        this.shopping_cart = new Cart();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cart getShopping_cart() {
        return shopping_cart;
    }

    public void setShopping_cart(Cart shopping_cart) {
        this.shopping_cart = shopping_cart;
    }

    public abstract boolean getLogged();
    public abstract List<Notification> getNotifications();

    public abstract void receiveNotification(String notification);

    public String sendNotification(int receiverId, String content){
        Notification notification = new Notification(this.id, receiverId, new Date(), content);
        return notification.toString();
    }

}
