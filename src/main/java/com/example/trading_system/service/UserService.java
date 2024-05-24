package com.example.trading_system.service;

import com.example.trading_system.domain.stores.StorePolicy;

import java.time.LocalDate;

public interface UserService {
    void enter(int id);
    boolean register(int id, String username, String password, LocalDate birthdate);
    boolean addToCart(int id,int productId,String storeName,int quantity);
    boolean openStore(String username,String storeName,String description , StorePolicy policy);

    boolean login(int id, String username, String password);

}
