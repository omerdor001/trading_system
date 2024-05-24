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


    void visitorAddToCart(int id, int productId, String storeName, int quantity);

    void visitorRemoveFromCart(int id, int productId, String storeName, int quantity);

    void registerdAddToCart(String username, int productId, String storeName, int quantity);

    void registerdRemoveFromCart(String username, int productId, String storeName, int quantity) throws Exception;

    void openStore(String username, String storeName, String description, StorePolicy policy);

}
