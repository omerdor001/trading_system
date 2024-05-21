package com.example.trading_system.domain.users;

public class UserFacadeImp implements UserFacade{
    @Override
    public void exit() {

    }

    @Override
    public void registration() {

    }

    @Override
    public void login() {

    }

    @Override
    public void addUser(User user) {

    }

    @Override
    public void removeUser(User user) {

    }

    @Override
    public boolean sendNotification(User sender, User receiver, String content){
        String notification = sender.sendNotification(receiver.getId(), content);
        receiver.receiveNotification(notification);
        return receiver.getLogged();
        //TODO return something to show the notification if receiver is logged in - maybe boolean if logged in
    }
}
