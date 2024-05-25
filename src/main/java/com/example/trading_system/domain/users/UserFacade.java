package com.example.trading_system.domain.users;

import com.example.trading_system.domain.stores.StorePolicy;

import java.time.LocalDate;
import java.util.HashMap;

public interface UserFacade {

    void createVisitor(int id);

    public HashMap<Integer, Visitor> getVisitors();
    public HashMap<String, Registered> getRegistered();
    void exit();
    void enter(int id);
    void exit(int id) throws Exception;
    void exit(String username) throws Exception;
    void register(int id, String username, String token, LocalDate birthdate) throws Exception;    //Complete with what to register

    void login(String username);

    boolean sendNotification(User sender, User receiver, String content);

    void visitorAddToCart(int id, int productId, String storeName, int quantity);

    void visitorRemoveFromCart(int id, int productId, String storeName, int quantity);

    void registerdAddToCart(String username, int productId, String storeName, int quantity);

    void registerdRemoveFromCart(String username, int productId, String storeName, int quantity) throws Exception;

    void openStore(String username, String storeName, String description, StorePolicy policy);

    String getUserPassword(String username);

    public void removeVisitor(int id);

}
