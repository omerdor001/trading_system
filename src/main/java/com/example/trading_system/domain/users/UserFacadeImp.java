package com.example.trading_system.domain.users;

import java.time.LocalDate;
import java.util.HashMap;

public class UserFacadeImp implements UserFacade{
    private HashMap<Integer, Visitor> visitors;
    private HashMap<String, Registered> registers;

    public UserFacadeImp() {
        this.registers = new HashMap<>();
        this.visitors = new HashMap<>();
    }

    public HashMap<Integer, Visitor> getVisitors() {
        return visitors;
    }

    public HashMap<String, Registered> getRegisters() {
        return registers;
    }

    @Override
    public void exit() {

    }


    @Override
    public void registration(int id, String username, String encryption, LocalDate birthdate) throws Exception {
        registerChecks(id, username, encryption, birthdate);
        Cart shopping_cart = new Cart();
        Registered newUser = new Registered(id,username,encryption, birthdate);
        registers.put(username,newUser);
        visitors.remove(id);
    }

    private void registerChecks(int id, String username, String encryption, LocalDate birthdate) throws Exception {
        if(username == null) throw new Exception("Username is null");
        if(username.isEmpty()) throw new Exception("Username is empty");
        if(encryption == null) throw new Exception("Encrypted password is null");
        if(encryption.isEmpty()) throw new Exception("Encrypted password is empty");
        if(birthdate == null) throw new Exception("Birthdate password is null");
        if(!visitors.containsKey(id)) throw new Exception("No visitor with id - " + id);
        if(registers.containsKey(username)) throw new Exception("username already exists - " + username);
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
