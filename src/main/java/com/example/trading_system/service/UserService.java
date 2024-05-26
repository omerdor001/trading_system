package com.example.trading_system.service;

import com.example.trading_system.domain.stores.StorePolicy;

import java.time.LocalDate;

public interface UserService {
    String enter(int id);
    boolean visitorAddToCart(int id, int productId, String storeName, int quantity);

    boolean visitorRemoveFromCart(int id, int productId, String storeName, int quantity);

    boolean registeredAddToCart(String username, int productId, String storeName, int quantity);

    boolean registeredRemoveFromCart(String username, int productId, String storeName, int quantity) throws Exception;

    boolean openStore(String username, String storeName, String description, StorePolicy policy);

    boolean register(int id, String username, String password, LocalDate birthdate);

    boolean login(int id, String username, String password);
    boolean logout(int id, String username);

    void exit(int id) throws Exception;
    void exit(String username) throws Exception;
    boolean registration(int id, String username, String password, LocalDate birthdate);
    boolean addToCart(int id,int productId,String storeName,int quantity);

    String registerdViewCart(String username);

    String visitorViewCart(int id);

//    boolean logout(int id);
}
