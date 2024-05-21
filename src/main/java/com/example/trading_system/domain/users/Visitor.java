package com.example.trading_system.domain.users;


import java.util.List;

public class Visitor extends User{
    public Visitor(int id) {
        super(id);
    }

    @Override
    public boolean getLogged() {
        return false;
    }

    @Override
    public List<Notification> getNotifications() {
        throw new UnsupportedOperationException("Visitors cannot receive notifications");
    }

    @Override
    public void receiveNotification(String notification) {
        throw new UnsupportedOperationException("Visitors cannot receive notifications");
    }

    public void registration(){ }   //Missing details to register

    public void login(){ }   //Missing details to register

}
