package com.example.trading_system.service;

import com.example.trading_system.domain.stores.StorePolicy;
import com.example.trading_system.domain.users.UserFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Map;

public class UserServiceImp implements UserService {

    private UserFacade userFacade;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);

    public UserServiceImp(UserFacade facade) {
        this.userFacade = facade;
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
        try {
            String encrypted_pass = userFacade.getUserPassword(username);
            if (Security.checkPassword(password, encrypted_pass)) {
                userFacade.login(username);
                userFacade.removeVisitor(id);
            } else
                throw new RuntimeException("Wrong password");
            logger.info("User: {} logged in", username);
            return true;
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed login user: {}", e.getMessage(), username);
            return false;
        }
    }

    @Override
    public boolean logout(int id, String username) {
        logger.info("Trying to logout user: {}", username);
        try {
            userFacade.saveUserCart(username);
            userFacade.logout(username);
            userFacade.enter(id);
            logger.info("User: {} logged out", username);
            return true;
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed logging out user: {}", e.getMessage(), username);
            return false;
        }
    }

    @Override
    public boolean register(int id, String username, String password, LocalDate birthdate) {
        logger.info("Trying registering a new user: {}", username);
        try {
            userFacade.register(id, username, password, birthdate);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed trying registering user: {}", e.getMessage(), username);
            return false;
        }
        logger.info("Finished registering user: {}", username);
        return true;
    }

    @Override
    public boolean visitorAddToCart(int id, int productId, String storeName, int quantity) {
        boolean result;
        logger.info("Trying adding to cart  product with id: {}", productId);
        try {
            userFacade.visitorAddToCart(id, productId, storeName, quantity);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed Trying adding to cart  product with id: {}", e.getMessage(), productId);
            return false;
        }
        logger.info("Finished adding to cart product with id: {}", productId);
        return true;
    }

    @Override
    public boolean visitorRemoveFromCart(int id, int productId, String storeName, int quantity) {
        boolean result;
        logger.info("Trying removing from cart product with id: {}", productId);
        try {
            userFacade.visitorRemoveFromCart(id, productId, storeName, quantity);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed Trying removing to cart  product with id: {}", e.getMessage(), productId);
            return false;
        }
        logger.info("Finished removing from cart product with id: {}", productId);
        return true;
    }

    @Override
    public boolean registeredAddToCart(String username, int productId, String storeName, int quantity) {
        boolean result;
        logger.info("Trying adding to cart product with id: {}", productId);
        try {
            userFacade.registeredAddToCart(username, productId, storeName, quantity);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed Trying adding to cart  product with id: {}", e.getMessage(), productId);
            return false;
        }
        logger.info("Finished adding to cart product with id: {}", productId);
        return true;
    }

    @Override
    public boolean registeredRemoveFromCart(String username, int productId, String storeName, int quantity) throws Exception {
        boolean result;
        logger.info("Trying removing from cart product with id: {}", productId);
        try {
            userFacade.registeredRemoveFromCart(username, productId, storeName, quantity);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed Trying removing to cart  product with id: {}", e.getMessage(), productId);
            return false;
        }
        logger.info("Finished removing from cart product with id: {}", productId);
        return true;
    }

    @Override
    public boolean openStore(String username, String storeName, String description, StorePolicy policy) {
        boolean result;
        logger.info("Trying opening store with name: {}", storeName);
        try {
            userFacade.openStore(username, storeName, description, policy);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed opening store with name: {}", e.getMessage(), storeName);
            return false;
        }
        logger.info("Finished opening store with name: {}", storeName);
        return true;
    }

    @Override
    public String registeredViewCart(String username) {
        String result = "";
        logger.info("Trying registerd : {} view cart ", username);
        try {
            result = userFacade.registeredViewCart(username);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed registerd view cart ", username);
            return "";
        }
        logger.info("Finished registerd view cart ");
        return result;
    }

    @Override
    public String visitorViewCart(int id) {
        String result;
        logger.info("Trying view cart");
        try {
            result = userFacade.visitorViewCart(id);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed view cart ", id);
            return "";
        }
        logger.info("Finished view cart ");
        return result;
    }

    @Override
    public boolean isAdminRegistered() {
        return userFacade.isAdminRegistered();
    }
}

