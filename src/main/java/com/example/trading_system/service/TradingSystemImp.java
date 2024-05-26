package com.example.trading_system.service;

import com.example.trading_system.domain.externalservices.Service;
import com.example.trading_system.domain.externalservices.ServiceFacade;
import com.example.trading_system.domain.externalservices.ServiceFacadeImp;
import com.example.trading_system.domain.stores.Category;
import com.example.trading_system.domain.users.UserFacade;
import com.example.trading_system.domain.users.UserFacadeImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.management.InstanceAlreadyExistsException;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class TradingSystemImp implements TradingSystem{
    private static final Logger logger = LoggerFactory.getLogger(TradingSystemImp.class);
    public UserFacade userFacade = UserFacadeImp.getInstance();
    public UserService userService = UserServiceImp.getInstance();
    public ServiceFacade serviceFacade;
    public ExternalServices externalServices;
    public MarketService marketService;
    public int counter_user = 0;

    private boolean systemOpen;

    private TradingSystemImp() {
        systemOpen = false;  // Initialize the system as closed
    }

    static class Singleton {
        private static final TradingSystemImp INSTANCE = new TradingSystemImp();
    }

    public static TradingSystemImp getInstance() {
        return TradingSystemImp.Singleton.INSTANCE;
    }

    public ResponseEntity<String> openSystem() {
        logger.info("Attempting to open system");
        try {
            if (userService.isAdminRegistered()) {
                serviceFacade = ServiceFacadeImp.getInstance();
                externalServices = ExternalServicesImp.getInstance();
                marketService = MarketServiceImp.getInstance();
                systemOpen = true;
                logger.info("System opened successfully");
                return new ResponseEntity<>("System opened successfully.", HttpStatus.OK);
            } else {
                logger.warn("System cannot be opened without at least one admin registered");
                return new ResponseEntity<>("System cannot be opened without at least one admin registered.", HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            logger.error("Error occurred while opening the system: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> enter() {
        logger.info("Attempting to enter system");
        try {
            if (!checkSystemOpen()) {
                logger.warn("System is not open, entry forbidden");
                return new ResponseEntity<>("", HttpStatus.FORBIDDEN);  // Return empty string if the system is not open
            }
            String token = userService.enter(counter_user);
            counter_user++;
            logger.info("User entered successfully with token: {}", token);
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while entering the system: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean checkSystemOpen() {
        return systemOpen;
    }

    private ResponseEntity<String> systemClosedResponse() {
        logger.warn("System is not open. Only registration is allowed.");
        return new ResponseEntity<>("System is not open. Only registration is allowed.", HttpStatus.FORBIDDEN);
    }

    public ResponseEntity<String> exit(String token, int id) {
        logger.info("Attempting to exit user with id: {}", id);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            userService.exit(id);
            Security.makeTokenExpire(token);
            logger.info("User exited successfully with id: {}", id);
            return new ResponseEntity<>("User exited successfully.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while exiting user with id: {}: {}", id, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> exit(String token, String username) {
        logger.info("Attempting to exit user: {}", username);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            userService.exit(username);
            Security.makeTokenExpire(token);
            logger.info("User exited successfully: {}", username);
            return new ResponseEntity<>("User exited successfully.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while exiting user: {}: {}", username, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> register(int id, String username, String password, LocalDate birthdate) {
        logger.info("Attempting to register user: {}", username);
        // Registration is allowed even if the system is not open
        try {
            userFacade.register(id, username, password, birthdate);
            logger.info("User registered successfully: {}", username);
            return new ResponseEntity<>("User registered successfully.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while registering user: {}: {}", username, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> addService(Service service) {
        return null;
    }

    @Override
    public ResponseEntity<String> replaceService(Service newService, Service oldService) {
        return null;
    }

    @Override
    public ResponseEntity<String> changeServiceName(Service serviceToChangeAt, String newName) {
        return null;
    }

    public ResponseEntity<String> addService(String service) {
        logger.info("Attempting to add service: {}", service);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            serviceFacade.addService(service);
            logger.info("Service added successfully: {}", service);
            return new ResponseEntity<>("Service added successfully.", HttpStatus.OK);
        } catch (InstanceAlreadyExistsException e) {
            logger.error("Service already exists: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            logger.error("Error occurred while adding service: {}: {}", service, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> replaceService(String newService, String oldService) {
        logger.info("Attempting to replace service: {} with new service: {}", oldService, newService);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            serviceFacade.replaceService(newService, oldService);
            logger.info("Service replaced successfully: {} with new service: {}", oldService, newService);
            return new ResponseEntity<>("Service replaced successfully.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while replacing service: {}: {}", oldService, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> changeServiceName(String serviceToChangeAt, String newName) {
        logger.info("Attempting to change service name of service: {} to new name: {}", serviceToChangeAt, newName);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            serviceFacade.changeServiceName(serviceToChangeAt, newName);
            logger.info("Service name changed successfully for service: {} to new name: {}", serviceToChangeAt, newName);
            return new ResponseEntity<>("Service name changed successfully.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while changing service name of service: {}: {}", serviceToChangeAt, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> makePayment(String serviceName, double amount) {
        logger.info("Attempting to make payment to service: {} for amount: {}", serviceName, amount);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            serviceFacade.makePayment(serviceName, amount);
            logger.info("Payment made successfully to service: {} for amount: {}", serviceName, amount);
            return new ResponseEntity<>("Payment made successfully.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while making payment to service: {}: {}", serviceName, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> makeDelivery(String serviceName, String address) {
        logger.info("Attempting to make delivery to address: {} using service: {}", address, serviceName);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            serviceFacade.makeDelivery(serviceName, address);
            logger.info("Delivery made successfully to address: {} using service: {}", address, serviceName);
            return new ResponseEntity<>("Delivery made successfully.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while making delivery to address: {} using service: {}: {}", address, serviceName, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> addProduct(String username, int product_id, String store_name, String product_name, String product_description,
                                             double product_price, int product_quantity, double rating, Category category, List<String> keyWords) {
        logger.info("Attempting to add product: {} to store: {}", product_name, store_name);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            marketService.addProduct(username, product_id, store_name, product_name, product_description, product_price, product_quantity, rating, category, keyWords);
            logger.info("Product added successfully: {} to store: {}", product_name, store_name);
            return new ResponseEntity<>("Product was added successfully.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while adding product: {} to store: {}: {}", product_name, store_name, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> removeProduct(String username, String storeName, int productId) {
        logger.info("Attempting to remove product with id: {} from store: {}", productId, storeName);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            marketService.removeProduct(username, storeName, productId);
            logger.info("Product removed successfully with id: {} from store: {}", productId, storeName);
            return new ResponseEntity<>("Product was removed successfully.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while removing product with id: {} from store: {}: {}", productId, storeName, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> setProductName(String username, String storeName, int productId, String productName) {
        logger.info("Attempting to set product name for product id: {} in store: {}", productId, storeName);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            marketService.setProductName(username, storeName, productId, productName);
            logger.info("Product name set successfully for product id: {} in store: {}", productId, storeName);
            return new ResponseEntity<>("Product name was set successfully.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while setting product name for product id: {} in store: {}: {}", productId, storeName, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> setProductDescription(String username, String storeName, int productId, String productDescription) {
        logger.info("Attempting to set product description for product id: {} in store: {}", productId, storeName);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            marketService.setProductDescription(username, storeName, productId, productDescription);
            logger.info("Product description set successfully for product id: {} in store: {}", productId, storeName);
            return new ResponseEntity<>("Product description was set successfully.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while setting product description for product id: {} in store: {}: {}", productId, storeName, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> setProductPrice(String username, String storeName, int productId, int productPrice) {
        logger.info("Attempting to set product price for product id: {} in store: {}", productId, storeName);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            marketService.setProductPrice(username, storeName, productId, productPrice);
            logger.info("Product price set successfully for product id: {} in store: {}", productId, storeName);
            return new ResponseEntity<>("Product price was set successfully.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while setting product price for product id: {} in store: {}: {}", productId, storeName, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> setProductQuantity(String username, String storeName, int productId, int productQuantity) {
        logger.info("Attempting to set product quantity for product id: {} in store: {}", productId, storeName);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            marketService.setProductQuantity(username, storeName, productId, productQuantity);
            logger.info("Product quantity set successfully for product id: {} in store: {}", productId, storeName);
            return new ResponseEntity<>("Product quantity was set successfully.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while setting product quantity for product id: {} in store: {}: {}", productId, storeName, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> setRating(String username, String storeName, int productId, int rating) {
        logger.info("Attempting to set rating for product id: {} in store: {}", productId, storeName);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            marketService.setRating(username, storeName, productId, rating);
            logger.info("Rating set successfully for product id: {} in store: {}", productId, storeName);
            return new ResponseEntity<>("Rating was set successfully.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while setting rating for product id: {} in store: {}: {}", productId, storeName, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> setCategory(String username, String storeName, int productId, Category category) {
        logger.info("Attempting to set category for product id: {} in store: {}", productId, storeName);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            marketService.setCategory(username, storeName, productId, category);
            logger.info("Category set successfully for product id: {} in store: {}", productId, storeName);
            return new ResponseEntity<>("Category was set successfully.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while setting category for product id: {} in store: {}: {}", productId, storeName, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<String> login(String token, int id, String username, String password) {
        logger.info("Attempting to login user: {}", username);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            if (userService.login(id, username, password)) {
                if(!token.isEmpty())
                    Security.makeTokenExpire(token);
                String newToken = Security.generateToken(username);
                logger.info("User: {} logged in successfully", username);
                return new ResponseEntity<>(newToken, HttpStatus.OK);
            } else {
                logger.warn("Login failed for user: {}", username);
                return new ResponseEntity<>("Login failed.", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            logger.error("Error occurred while logging in user: {}: {}", username, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> logout(int id, String userName) {
        logger.info("Attempting to logout user: {}", userName);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            userService.logout(id, userName);
            logger.info("User: {} logged out successfully", userName);
            return new ResponseEntity<>("Logout successful.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while logging out user: {}: {}", userName, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
