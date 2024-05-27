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
    public ResponseEntity<String> addPaymentService(String serviceName) {
        logger.info("Trying adding external payment service: {}", serviceName);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            externalServices.addPaymentService(serviceName);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed trying adding external payment service: {}", e.getMessage(), serviceName);
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);

        }
        logger.info("Finish adding external payment service: {}", serviceName);
        return new ResponseEntity<String>("Success adding external payment service", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> addPaymentProxyService(String serviceName) {
        logger.info("Trying adding external payment proxy service: {}", serviceName);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            externalServices.addPaymentProxyService(serviceName);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed trying adding external payment proxy service: {}", e.getMessage(), serviceName);
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);

        }
        logger.info("Finish adding external payment proxy service: {}", serviceName);
        return new ResponseEntity<String>("Success adding external payment proxy service", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> addDeliveryService(String serviceName) {
        logger.info("Trying adding external delivery service: {}", serviceName);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            externalServices.addDeliveryService(serviceName);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed trying adding external delivery service: {}", e.getMessage(), serviceName);
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);

        }
        logger.info("Finish adding external delivery service: {}", serviceName);
        return new ResponseEntity<String>("Success adding external delivery service", HttpStatus.OK);
    }


    @Override
    public ResponseEntity<String> addDeliveryProxyService(String serviceName) {
        logger.info("Trying adding external delivery proxy service: {}", serviceName);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            externalServices.addDeliveryProxyService(serviceName);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed trying adding external delivery proxy service: {}", e.getMessage(), serviceName);
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);

        }
        logger.info("Finish adding external delivery proxy service: {}", serviceName);
        return new ResponseEntity<String>("Success adding external delivery proxy service", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> replaceService(String newServiceName, String oldServiceName) {
        logger.info("Attempting to replace service: {} with new service: {}", oldServiceName, newServiceName);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            serviceFacade.replaceService(newServiceName, oldServiceName);
            logger.info("Service replaced successfully: {} with new service: {}", oldServiceName, newServiceName);
            return new ResponseEntity<>("Service replaced successfully.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while replacing service: {}: {}", oldServiceName, e.getMessage());
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
                                             double product_price, int product_quantity, double rating, int category, List<String> keyWords) {
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

    public ResponseEntity<String> setCategory(String username, String storeName, int productId, int category) {
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

    @Override
    public ResponseEntity<String> suggestManage(String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) {
        logger.info("Trying to suggest user : {} to be a manager in store : {}", newManager,store_name_id);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            userFacade.suggestManage(appoint,newManager,store_name_id,watch,editSupply,editBuyPolicy,editDiscountPolicy);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to suggest the user : {} to be a manager in store : {}", e.getMessage(),appoint,store_name_id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished suggesting manager : {} to be a manager in store : {}", newManager,store_name_id);
        return new ResponseEntity<>("Success suggesting manager", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> suggestOwner(String appoint, String newOwner, String storeName) {
        logger.info("{} trying to suggest user : {} to be a owner in store : {}", appoint, newOwner,storeName);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            userFacade.suggestOwner(appoint,newOwner,storeName);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to suggest the user : {} to be a owner in store : {}", e.getMessage(),appoint,storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished suggesting  : {} to be a owner in store : {}", newOwner,storeName);
        return new ResponseEntity<>("Success suggesting owner", HttpStatus.OK);
    }


    @Override
    public ResponseEntity<String> approveManage(String newManager, String store_name_id, String appoint) {
        logger.info("Trying to approve manage to store : {}",store_name_id);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            userFacade.approveManage(newManager,store_name_id, appoint);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to approve management to store : {}", e.getMessage(),store_name_id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished approving manage to store : {}", store_name_id);
        return new ResponseEntity<>("Success approving manage", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> approveOwner(String newOwner, String storeName, String appoint) {
        logger.info("{} trying to approve owner to store : {}",newOwner, storeName);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            userFacade.approveOwner(newOwner,storeName, appoint);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to approve owner to store : {}", e.getMessage(),storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished approving owner to store : {}", storeName);
        return new ResponseEntity<>("Success approving owner", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> appointManager(String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) {
        logger.info("Trying to appoint manager : {} to store : {}", newManager,store_name_id);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            userFacade.appointManager(appoint,newManager,store_name_id,watch,editSupply,editBuyPolicy,editDiscountPolicy);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to appoint the user : {} to store : {}", e.getMessage(),appoint,store_name_id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished appointing manager : {} to store : {}", newManager,store_name_id);
        return new ResponseEntity<>("Success appointing manager", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> appointOwner(String appoint, String newOwner, String storeName) {
        logger.info("Trying to appoint owner : {} to store : {}", newOwner,storeName);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            userFacade.appointOwner(appoint,newOwner,storeName);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to appoint the user : {} to be owner in store : {}", e.getMessage(),appoint,storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished appointing owner : {} to store : {}", newOwner, storeName);
        return new ResponseEntity<>("Success appointing owner", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> editPermissionForManager(String userId, String managerToEdit, String storeNameId, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) {
        logger.info("{} is Trying to edit permission for manager : {} in store : {}", userId, managerToEdit,storeNameId);
        try {
            if (!checkSystemOpen()) {
                return systemClosedResponse();
            }
            userFacade.editPermissionForManager(userId,managerToEdit,storeNameId,watch,editSupply,editBuyPolicy,editDiscountPolicy);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while {} is trying to edit permission for manager : {} : in store : {}", e.getMessage(),userId ,managerToEdit,storeNameId);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished edit permission to manager : {}  in store : {}", managerToEdit,storeNameId);
        return new ResponseEntity<>("Success edit permission for manager ", HttpStatus.OK);
    }

}
