package com.example.trading_system.domain.users;

import com.example.trading_system.domain.stores.StorePolicy;

import java.time.LocalDate;
import java.util.HashMap;

public interface UserFacade {

    public HashMap<Integer, Visitor> getVisitors();
    public HashMap<String, Registered> getRegisters();
    void exit();
    void enter(int id);
    void exit(int id) throws Exception;
    void exit(String username) throws Exception;
    void registration(int id, String username, String token, LocalDate birthdate) throws Exception;    //Complete with what to register


    void login(String username);

  void addUser(User user);
    void removeUser(User user);
    boolean sendNotification(User sender, User receiver, String content);
    void addToCart(int id, int productId, String storeName, int quantity);
    void openStore(String username, String storeName, String description, StorePolicy policy);

    String getUserPassword(String username);
    public void removeVisitor(int id);
}
