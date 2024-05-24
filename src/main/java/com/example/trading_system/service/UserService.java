package com.example.trading_system.service;

import com.example.trading_system.domain.stores.StorePolicy;

import java.time.LocalDate;

public interface UserService {
    void enter(int id);

    boolean registration(int id, String username, String password, LocalDate birthdate);


    boolean visitorAddToCart(int id, int productId, String storeName, int quantity);

    boolean visitorRemoveFromCart(int id, int productId, String storeName, int quantity);

    boolean registerdAddToCart(String username, int productId, String storeName, int quantity);

    boolean registerdRemoveFromCart(String username, int productId, String storeName, int quantity) throws Exception;

    boolean openStore(String username, String storeName, String description, StorePolicy policy);

}
