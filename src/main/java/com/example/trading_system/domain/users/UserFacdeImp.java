package com.example.trading_system.domain.users;

import java.util.HashMap;
import java.time.LocalDate;

public class UserFacdeImp implements UserFacade{

    private HashMap<Integer, Visitor> visitors;
    private HashMap<String, Registered> registered_users;

    @Override
    public void exit() {

    }

    @Override
    public void registration(int id, String username, String encryption, LocalDate birthdate) throws Exception {
        registerChecks(id, username, encryption, birthdate);
        Cart shopping_cart = new Cart();
        Registered newUser = new Registered(id,shopping_cart,username,birthdate);
        registered_users.put(username,newUser);
        visitors.remove(id);
    }

    private void registerChecks(int id, String username, String encryption, LocalDate birthdate) throws Exception {
        if(username == null) throw new Exception("Username is null");
        if(username.isEmpty()) throw new Exception("Username is empty");
        if(encryption == null) throw new Exception("Encrypted password is null");
        if(encryption.isEmpty()) throw new Exception("Encrypted password is empty");
        if(birthdate == null) throw new Exception("Birthdate password is null");
        if(!visitors.containsKey(id)) throw new Exception("No visitor with id - " + id);
        if(registered_users.containsKey(username)) throw new Exception("username already exists - " + username);
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
}
