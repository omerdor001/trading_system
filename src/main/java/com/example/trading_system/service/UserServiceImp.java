package com.example.trading_system.service;

import com.example.trading_system.domain.stores.StorePolicy;
import com.example.trading_system.domain.users.UserFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Map;

public class UserServiceImp implements UserService {

    private UserFacade facade;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);

    public UserServiceImp(UserFacade facade) {
        this.facade = facade;
    }

    public void enter(int id){
        logger.info("Trying enter to system as a visitor , with id : {}", id);
        facade.createVisitor(id);
        Security.generateToken("v"+id);
        logger.info("Finish enter to system as a visitor , with id : {}", id);
    }

    @Override
    public boolean registration(int id, String username, String password, LocalDate birthdate) {
        boolean result;
        logger.info("Trying registering a new user: {}", username);
        try {
            String encryption = Security.encrypt(password);
            facade.registration(id, username, encryption, birthdate);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed trying registering user: {}", e.getMessage(), username);
            return false;
        }
        logger.info("Finished registering user: {}", username);
        return true;
    }

    @Override
    public boolean addToCart(int id, int productId, String storeName, int quantity) {
        boolean result;
        logger.info("Trying adding to cart  product with id: {}", productId);
        try {
            facade.addToCart(id, productId, storeName, quantity);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed Trying adding to cart  product with id: {}", e.getMessage(), productId);
            return false;
        }
        logger.info("Finished adding to cart product with id: {}", productId);
        return true;
    }

    @Override
    public boolean openStore(String username, String storeName, String description, StorePolicy policy) {
        boolean result;
        logger.info("Trying opening store with name: {}", storeName);
        try{
            facade.openStore(username,storeName,description,policy);
        }catch (Exception e){
            logger.error("Error occurred : {} , Failed opening store with name: {}", e.getMessage(), storeName);
            return false;
        }
        logger.info("Finished opening store with name: {}", storeName);
        return true;
    }

}

