package com.example.trading_system.domain.users;

import com.example.trading_system.domain.stores.StorePolicy;

import java.time.LocalDate;

public interface UserFacade {

    void createVisitor(int id);
    void exit();
    void registration(int id, String username, String token, LocalDate birthdate) throws Exception;    //Complete with what to register


    void login();     //Complete with what to login
    void addUser(User user);
    void removeUser(User user);
    boolean sendNotification(User sender, User receiver, String content);

    void addToCart(int id, int productId, String storeName, int quantity);

    void openStore(String username, String storeName, String description, StorePolicy policy);
}
