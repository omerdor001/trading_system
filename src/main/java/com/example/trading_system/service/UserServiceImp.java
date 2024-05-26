package com.example.trading_system.service;

import com.example.trading_system.domain.stores.Purchase;
import com.example.trading_system.domain.stores.StorePolicy;
import com.example.trading_system.domain.users.UserFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

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
        try{
            String encrypted_pass = userFacade.getUserPassword(username);
            if (Security.checkPassword(password, encrypted_pass)) {
                userFacade.login(username);
                userFacade.removeVisitor(id);
            }
            else
                throw new RuntimeException("Wrong password");
            logger.info("User: {} logged in", username);
            return true;
        }catch (Exception e){
            logger.error("Error occurred : {} , Failed login user: {}", e.getMessage(), username);
            return false;
        }
    }

    @Override
    public boolean logout(int id, String username) {
        logger.info("Trying to logout user: {}", username);
        try{
            userFacade.logout(id, username);
            logger.info("User: {} logged out", username);
            return true;
        }catch (Exception e){
            logger.error("Error occurred : {} , Failed logging out user: {}", e.getMessage(), username);
            return false;
        }
    }


    @Override
    public boolean register(int id, String username, String password, LocalDate birthdate) {
        logger.info("Trying registering a new user: {}", username);
        try {
            String encrypted_pass = Security.encrypt(password);
            userFacade.register(id, username, encrypted_pass, birthdate);
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
        }catch (Exception e){
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
    public ResponseEntity<String> suggestManage(String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) {
        logger.info("Trying to suggest user : {} to be a manager in store : {}", newManager,store_name_id);
        try {
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
            userFacade.approveOwner(newOwner,storeName, appoint);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to approve owner to store : {}", e.getMessage(),storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished approving owner to store : {}", storeName);
        return new ResponseEntity<>("Success approving owner", HttpStatus.OK);
    }

//    @Override
//    public ResponseEntity<String> appointManager(String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) {
//        logger.info("Trying to appoint manager : {} to store : {}", newManager,store_name_id);
//        try {
//            userFacade.appointManager(appoint,newManager,store_name_id,watch,editSupply,editBuyPolicy,editDiscountPolicy);
//        } catch (Exception e) {
//            logger.error("Error occurred : {} , while trying to appoint the user : {} to store : {}", e.getMessage(),appoint,store_name_id);
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//        logger.info("Finished appointing manager : {} to store : {}", newManager,store_name_id);
//        return new ResponseEntity<>("Success appointing manager", HttpStatus.OK);
//    }
//
//    @Override
//    public ResponseEntity<String> appointOwner(String appoint, String newOwner, String storeName) {
//        logger.info("Trying to appoint owner : {} to store : {}", newOwner,storeName);
//        try {
//            userFacade.appointOwner(appoint,newOwner,storeName);
//        } catch (Exception e) {
//            logger.error("Error occurred : {} , while trying to appoint the user : {} to be owner in store : {}", e.getMessage(),appoint,storeName);
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//        logger.info("Finished appointing owner : {} to store : {}", newOwner, storeName);
//        return new ResponseEntity<>("Success appointing owner", HttpStatus.OK);
//    }

    @Override
    public ResponseEntity<String> editPermissionForManager(String userId, String managerToEdit, String storeNameId, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) {
        logger.info("{} is Trying to edit permission for manager : {} in store : {}", userId, managerToEdit,storeNameId);
        try {
            userFacade.editPermissionForManager(userId,managerToEdit,storeNameId,watch,editSupply,editBuyPolicy,editDiscountPolicy);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while {} is trying to edit permission for manager : {} : in store : {}", e.getMessage(),userId ,managerToEdit,storeNameId);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished edit permission to manager : {}  in store : {}", managerToEdit,storeNameId);
        return new ResponseEntity<>("Success edit permission for manager ", HttpStatus.OK);
    }



}

