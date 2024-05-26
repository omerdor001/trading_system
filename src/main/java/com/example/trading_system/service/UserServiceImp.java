package com.example.trading_system.service;

import com.example.trading_system.domain.stores.MarketFacadeImp;
import com.example.trading_system.domain.stores.StorePolicy;
import com.example.trading_system.domain.users.UserFacade;
import com.example.trading_system.domain.users.UserFacadeImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Map;

public class UserServiceImp implements UserService {

    private UserFacade userFacade =  UserFacadeImp.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);

    private UserServiceImp() {
    }

    static class Singleton {
        private static final UserServiceImp INSTANCE = new UserServiceImp();
    }

    public static UserServiceImp getInstance() {
        return UserServiceImp.Singleton.INSTANCE;
    }

    public String enter(int id) {
        logger.info("Trying enter to system as a visitor , with id : {}", id);
        userFacade.createVisitor(id);
        String token = Security.generateToken("v" + id);
        logger.info("Finish enter to system as a visitor , with id : {}", id);
        return token;
    }

    public void exit(int id) throws Exception {
        logger.info("Trying exit to system as a visitor , with id : {}", id);
        userFacade.exit(id);
        logger.info("Finish exit to system as a visitor , with id : {}", id);
    }

    public void exit(String username) throws Exception {
        logger.info("Trying exit to system as a user , with username: {}", username);
        userFacade.exit(username);
        logger.info("Finish exit to system as a user , with username : {}", username);
    }

    @Override
    public boolean login(int id, String username, String password) {
        logger.info("Trying to login user: {}", username);
        String encrypted_pass = userFacade.getUserPassword(username);
        if (Security.checkPassword(password, encrypted_pass)) {
            userFacade.login(username);
            userFacade.removeVisitor(id);
            logger.info("User: {} logged in", username);
            return true;
        } else {
            logger.error("Wrong password, Failed login user: {}", username);
            return false;
        }
    }

    @Override
    public boolean logout(int id, String username) {
        logger.info("Trying to logout user: {}", username);
        userFacade.logout(id, username);
        logger.info("User: {} logged out", username);
        return true;
    }


    @Override
    public boolean register(int id, String username, String password, LocalDate birthdate) throws Exception {
        logger.info("Trying registering a new user: {}", username);
        userFacade.register(id, username, password, birthdate);
        logger.info("Finished registering user: {}", username);
        return true;
    }

    @Override
    public boolean visitorAddToCart(int id, int productId, String storeName, int quantity) {
        logger.info("Trying adding to cart  product with id: {}", productId);
        userFacade.visitorAddToCart(id, productId, storeName, quantity);
        logger.info("Finished adding to cart product with id: {}", productId);
        return true;
    }
    @Override
    public boolean visitorRemoveFromCart(int id, int productId, String storeName, int quantity) {
        logger.info("Trying removing from cart product with id: {}", productId);
        userFacade.visitorRemoveFromCart(id, productId, storeName, quantity);
        logger.info("Finished removing from cart product with id: {}", productId);
        return true;
    }

    @Override
    public boolean registeredAddToCart(String username, int productId, String storeName, int quantity) {
        logger.info("Trying adding to cart product with id: {}", productId);
        userFacade.registeredAddToCart(username, productId, storeName, quantity);
        logger.info("Finished adding to cart product with id: {}", productId);
        return true;
    }

    @Override
    public boolean registeredRemoveFromCart(String username, int productId, String storeName, int quantity) throws Exception {
        logger.info("Trying removing from cart product with id: {}", productId);
        userFacade.registeredRemoveFromCart(username, productId, storeName, quantity);
        logger.info("Finished removing from cart product with id: {}", productId);
        return true;
    }
    @Override
    public boolean openStore(String username, String storeName, String description, StorePolicy policy) {
        logger.info("Trying opening store with name: {}", storeName);
        userFacade.openStore(username, storeName, description, policy);
        logger.info("Finished opening store with name: {}", storeName);
        return true;
    }

    @Override
    public String registeredViewCart(String username) {
        logger.info("Trying registered : {} view cart ", username);
        String result = userFacade.registeredViewCart(username);
        logger.info("Finished registered view cart: {} ", username);
        return result;
    }

    @Override
    public String visitorViewCart(int id) {
        logger.info("Trying view cart");
        String result = userFacade.visitorViewCart(id);
        logger.info("Finished visitor view cart: {}", id);
        return result;
    }

    @Override
    public boolean isAdminRegistered() {
        return userFacade.isAdminRegistered();
    }
}
