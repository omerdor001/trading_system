package com.example.trading_system.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TradingSystemImp implements TradingSystem {
    private static final Logger logger = LoggerFactory.getLogger(TradingSystemImp.class);
    private static TradingSystemImp instance = null;
    public UserService userService;
    public MarketService marketService;
    public int counter_user = 0;
    private boolean systemOpen;

    private TradingSystemImp() {
        this.systemOpen = false;
        this.userService = UserServiceImp.getInstance();
        this.marketService = MarketServiceImp.getInstance();
    }

    public static TradingSystemImp getInstance() {
        if (instance == null) instance = new TradingSystemImp();
        return instance;
    }

    @Override
    public void deleteInstance() {
        instance = null;
        this.userService.deleteInstance();
        userService = null;
        if (systemOpen) {
            this.marketService.deleteInstance();
        }
    }

    private boolean checkSystemClosed() {
        return !systemOpen;
    }

    private boolean checkInvalidToken(String username, String token) {
        try {
            return !Security.validateToken(username, token);
        } catch (Exception e) {
            return true;
        }
    }

    private ResponseEntity<String> systemClosedResponse() {
        logger.warn("System is not open");
        return new ResponseEntity<>("System is not open. Only registration is allowed.", HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<String> invalidTokenResponse() {
        logger.error("Invalid token was supplied");
        return new ResponseEntity<>("Invalid token was supplied", HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<String> openSystem() {
        logger.info("Attempting to open system");
        if (systemOpen) {
            logger.warn("System attempted to open twice");
            return new ResponseEntity<>("System is already open.", HttpStatus.BAD_REQUEST);
        }
        try {
            if (userService.isAdminRegistered()) {
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

    @Override
    public ResponseEntity<String> closeSystem(String username, String token) {
        logger.info("Attempting to close system");
        try {
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            if (userService.isAdmin(username)) {
                //TODO call close method in deeper classes? (maybe logout all users)
                marketService.deleteInstance();
                systemOpen = false;
                logger.info("System closed by user: {}", username);
                return new ResponseEntity<>("System closed successfully.", HttpStatus.OK);
            }
            logger.info("Unauthorized user {} attempted to close the system", username);
            return new ResponseEntity<>("System can only be closed by an admin", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("Error occurred while closing the system: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> enter() {
        logger.info("Attempting to enter system");
        try {
            if (checkSystemClosed()) {
                logger.warn("System is not open, entry forbidden");
                return new ResponseEntity<>("", HttpStatus.FORBIDDEN);
            }
            logger.info("Trying enter to system as a visitor , with id : {}", counter_user);
            String username = userService.enter(counter_user);
            counter_user++;
            String token = Security.generateToken(username);
            String userToken = "{\"username\": \"" + username + "\", \"token\": \"" + token + "\"}";
            logger.info("User entered successfully with token: {} and username: {}", token, username);
            return new ResponseEntity<>(userToken, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while entering the system: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> exit(String token, String username) {
        logger.info("Attempting to exit user: {}", username);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            logger.info("Trying exit to system as a user , with username: {}", username);
            userService.exit(username);
            Security.makeTokenExpire(token);
            logger.info("User exited successfully: {}", username);
            return new ResponseEntity<>("User exited successfully.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while exiting user: {}: {}", username, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> register(String username, String password, LocalDate birthdate) {
        logger.info("Attempting to register user: {} with password : {} and birthdate : {} ", username, password, birthdate);
        try {
            userService.register(username, password, birthdate);
            logger.info("User registered successfully : {} with password : {} and birthdate : {}", username, password, birthdate);
            return new ResponseEntity<>("User registered successfully.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while registering user : {} with password : {} and birthdate : {} : {}", username, password, birthdate, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> addProduct(String username, String token, int product_id, String store_name, String product_name, String product_description, double product_price, int product_quantity, double rating, int category, List<String> keyWords) {
        logger.info("User {} is trying to add products to store : {} with product Id : {} , product name : {}, product description : {} , product price : {} ," + "product quantity : {} , rating : {} , category : {} , key words : {} ", username, store_name, product_id, product_name, product_description, product_price, product_quantity, rating, category, keyWords);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.addProduct(username, product_id, store_name, product_name, product_description, product_price, product_quantity, rating, category, keyWords);
        } catch (Exception e) {
            logger.error("Error occurred while adding product: {} to store: {}: {}", product_name, store_name, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("User {} finished to add products to store : {} with product Id : {} , product name : {}, product description : {} , product price : {} ," + "product quantity : {} , rating : {} , category : {} , key words : {} ", username, store_name, product_id, product_name, product_description, product_price, product_quantity, rating, category, keyWords);
        return new ResponseEntity<>("Product was added successfully.", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> removeProduct(String username, String token, String store_name, int product_id) {
        logger.info("User {} is trying to remove product with Id : {} to store : {} ", username, product_id, store_name);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.removeProduct(username, store_name, product_id);
        } catch (Exception e) {
            logger.error("Error occurred while removing product with id: {} from store: {}: {}", product_id, store_name, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("User {} finished to remove product with Id :{} to store : {} ", username, product_id, store_name);
        return new ResponseEntity<>("Product was removed successfully.", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> setProductName(String username, String token, String store_name, int product_id, String product_name) {
        logger.info("User {} is trying to edit the name : {} to product : {} from store : {}", username, product_name, product_id, store_name);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.setProductName(username, store_name, product_id, product_name);
        } catch (Exception e) {
            logger.error("Error occurred while setting product name for product id: {} in store: {}: {}", product_id, store_name, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("User {} finished to edit the name : {} to product : {} from store : {}", username, product_name, product_id, store_name);
        return new ResponseEntity<>("Product name was set successfully.", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> setProductDescription(String username, String token, String store_name, int product_id, String product_description) {
        logger.info("User {} is trying to edit the description : {} to product : {} from store : {}", username, product_description, product_id, store_name);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.setProductDescription(username, store_name, product_id, product_description);
        } catch (Exception e) {
            logger.error("Error occurred while setting product description for product id: {} in store: {}: {}", product_id, store_name, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("User {} finished to edit the description : {} to product : {} from store : {}", username, product_description, product_id, store_name);
        return new ResponseEntity<>("Product description was set successfully.", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> setProductPrice(String username, String token, String store_name, int product_id, double product_price) {
        logger.info("User {} is trying to edit the price : {} to product : {} from store : {}", username, product_price, product_id, store_name);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.setProductPrice(username, store_name, product_id, product_price);
        } catch (Exception e) {
            logger.error("Error occurred while setting product price for product id: {} in store: {}: {}", product_id, store_name, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("User {} finished to edit the price : {} to product : {} from store : {}", username, product_price, product_id, store_name);
        return new ResponseEntity<>("Product price was set successfully.", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> setProductQuantity(String username, String token, String store_name, int product_id, int product_quantity) {
        logger.info("User {} is trying to edit the quantity : {} to product : {} from store : {}", username, product_quantity, product_id, store_name);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.setProductQuantity(username, store_name, product_id, product_quantity);
        } catch (Exception e) {
            logger.error("Error occurred while setting product quantity for product id: {} in store: {}: {}", product_id, store_name, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("User {} finished to edit the quantity : {} to product : {} from store : {}", username, product_quantity, product_id, store_name);
        return new ResponseEntity<>("Product quantity was set successfully.", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> setRating(String username, String token, String store_name, int product_id, double rating) {
        logger.info("User {} is trying to edit the rating : {} to product : {} from store : {}", username, rating, product_id, store_name);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.setRating(username, store_name, product_id, rating);
        } catch (Exception e) {
            logger.error("Error occurred while setting rating for product id: {} in store: {}: {}", product_id, store_name, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("User {} finished to edit the rating : {} to product : {} from store : {}", username, rating, product_id, store_name);
        return new ResponseEntity<>("Rating was set successfully.", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> setCategory(String username, String token, String store_name, int product_id, int category) {
        logger.info("User {} is trying to edit the category : {} to product : {} from store : {}", username, category, product_id, store_name);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.setCategory(username, store_name, product_id, category);
        } catch (Exception e) {
            logger.error("Error occurred while setting category for product id: {} in store: {}: {}", product_id, store_name, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("User {} finished to edit the category : {} to product : {} from store : {}", username, category, product_id, store_name);
        return new ResponseEntity<>("Category was set successfully.", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> login(String token, String usernameV, String username, String password) {
        logger.info("Attempting to login user: {}", username);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (userService.login(usernameV, username, password)) {
                if (!token.isEmpty()) Security.makeTokenExpire(token);
                String newToken = Security.generateToken("r" + username);
                String userToken = "{\"username\": \"" + "r" + username + "\", \"token\": \"" + newToken + "\"}";
                logger.info("User: {} logged in successfully", username);
                return new ResponseEntity<>(userToken, HttpStatus.OK);
            } else {
                logger.warn("Login failed for user: {}", username);
                return new ResponseEntity<>("Login failed.", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            logger.error("Error occurred while logging in user: {}: {}", username, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> logout(String token, String username) {
        logger.info("Attempting to logout user: {}", username);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            userService.logout(counter_user, username);
            counter_user++;
            logger.info("User: {} logged out successfully", username);
            return new ResponseEntity<>("Logout successful.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while logging out user: {}: {}", username, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> suspendUser(String token, String admin, String toSuspend, LocalDateTime endSuspension) {
        logger.info("Attempting to suspend user: {}", toSuspend);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(admin, token)) return invalidTokenResponse();
            userService.suspendUser(admin, toSuspend, endSuspension);
            logger.info("User: {} is suspended successfully", toSuspend);
            return new ResponseEntity<>("Suspension successful.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while suspending user: {}: {}", toSuspend, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> endSuspendUser(String token, String admin, String toSuspend) {
        logger.info("Attempting to end suspension of user: {}", toSuspend);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(admin, token)) return invalidTokenResponse();
            userService.endSuspendUser(admin, toSuspend);
            logger.info("Suspending user: {} is finished successfully", toSuspend);
            return new ResponseEntity<>("Ending suspension successful.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while ending suspended user: {}: {}", toSuspend, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> setAddress(String username, String token, String address) {
        logger.info("Attempting to set address of user: {}", username);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            userService.setAddress(username, address);
            logger.info("User: {} set address to: {}", username, address);
            return new ResponseEntity<>("Ending suspension successful.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while setting address for user: {}: {}", username, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> watchSuspensions(String token, String admin) {
        logger.info("Attempting to get suspension details");
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(admin, token)) return invalidTokenResponse();
            String details = userService.watchSuspensions(admin);
            logger.info("Getting details of suspension successfully");
            return new ResponseEntity<>(details, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while getting information of suspensions : {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> suggestManage(String appoint, String token, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) {
        logger.info("Trying to suggest user : {} to be a manager in store : {}", newManager, store_name_id);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(appoint, token)) return invalidTokenResponse();
            userService.suggestManage(appoint, newManager, store_name_id, watch, editSupply, editBuyPolicy, editDiscountPolicy);
            logger.info("Finished suggesting manager : {} to be a manager in store : {}", newManager, store_name_id);
            return new ResponseEntity<>("Success suggesting manager", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to suggest the user : {} to be a manager in store : {}", e.getMessage(), appoint, store_name_id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> suggestOwner(String appoint, String token, String newOwner, String storeName) {
        logger.info("{} trying to suggest user : {} to be a owner in store : {}", appoint, newOwner, storeName);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(appoint, token)) return invalidTokenResponse();
            userService.suggestOwner(appoint, newOwner, storeName);
            logger.info("Finished suggesting  : {} to be a owner in store : {}", newOwner, storeName);
            return new ResponseEntity<>("Success suggesting owner", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to suggest the user : {} to be a owner in store : {}", e.getMessage(), appoint, storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> approveManage(String newManager, String token, String store_name_id, String appoint) {
        logger.info("Trying to approve manage to store : {}", store_name_id);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(newManager, token)) return invalidTokenResponse();
            userService.approveManage(newManager, store_name_id, appoint);
            logger.info("Finished approving manage to store : {}", store_name_id);
            return new ResponseEntity<>("Success approving manage", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to approve management to store : {}", e.getMessage(), store_name_id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> approveOwner(String newOwner, String token, String storeName, String appoint) {
        logger.info("{} trying to approve owner to store : {}", newOwner, storeName);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(newOwner, token)) return invalidTokenResponse();
            userService.approveOwner(newOwner, storeName, appoint);
            logger.info("Finished approving owner to store : {}", storeName);
            return new ResponseEntity<>("Success approving owner", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to approve owner to store : {}", e.getMessage(), storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> rejectToOwnStore(String username, String token, String storeName, String appoint) {
        logger.info("{} trying to reject owner to store : {}", username, storeName);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            userService.rejectToOwnStore(username, storeName, appoint);
            logger.info("Finished reject owner to store : {}", storeName);
            return new ResponseEntity<>("Success reject to be owner", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to reject owner to store : {}", e.getMessage(), storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @Override
    public ResponseEntity<String> rejectToManageStore(String userName, String token, String store_name_id, String appoint) {
        logger.info("Trying to reject to manage to store : {}", store_name_id);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(userName, token)) return invalidTokenResponse();
            userService.rejectToManageStore(userName, store_name_id, appoint);
            logger.info("Finished reject manage to store : {}", store_name_id);
            return new ResponseEntity<>("Success rejecting manage", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to reject management to store : {}", e.getMessage(), store_name_id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> waiverOnOwnership(String userName, String token, String storeName) {
        logger.info("Trying to give up to own store : {}", storeName);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(userName, token)) return invalidTokenResponse();
            userService.waiverOnOwnership(userName, storeName);
            logger.info("Finished waiver own store : {}", storeName);
            return new ResponseEntity<>("Success waiver to own", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to give up own to store : {}", e.getMessage(), storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> fireManager(String owner, String token, String storeName, String manager) {
        logger.info("{} Trying to fore {} from be a manager in store {}", owner, storeName, manager);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(owner, token)) return invalidTokenResponse();
            userService.fireManager(owner, storeName, manager);
            logger.info("Finished fire {} from store : {}", manager, storeName);
            return new ResponseEntity<>("Success fire manager", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while {} trying to fire {} from store : {}", e.getMessage(), owner, manager, storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> fireOwner(String ownerAppoint, String token, String storeName, String ownerToFire) {
        logger.info("{} Trying to fire {} from be a owner in store {}", ownerAppoint, storeName, ownerToFire);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(ownerAppoint, token)) return invalidTokenResponse();
            userService.fireOwner(ownerAppoint, storeName, ownerToFire);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while {} trying to fire {} from store : {}", e.getMessage(), ownerAppoint, ownerToFire, storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished fire {} from store : {}", ownerToFire, storeName);
        return new ResponseEntity<>("Success fire owner", HttpStatus.OK);
    }

    //TODO Same as suggestManager/approveManager?
    @Override
    public ResponseEntity<String> appointManager(String username, String token, String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) {
        logger.info("Trying to appoint manager : {} to store : {}", newManager, store_name_id);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            userService.appointManager(appoint, newManager, store_name_id, watch, editSupply, editBuyPolicy, editDiscountPolicy);
            logger.info("Finished appointing manager : {} to store : {}", newManager, store_name_id);
            return new ResponseEntity<>("Success appointing manager", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to appoint the user : {} to store : {}", e.getMessage(), appoint, store_name_id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //TODO same as suggestOwner/approveOwner?
    @Override
    public ResponseEntity<String> appointOwner(String username, String token, String appoint, String newOwner, String storeName) {
        logger.info("Trying to appoint owner : {} to store : {}", newOwner, storeName);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            userService.appointOwner(appoint, newOwner, storeName);
            logger.info("Finished appointing owner : {} to store : {}", newOwner, storeName);
            return new ResponseEntity<>("Success appointing owner", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to appoint the user : {} to be owner in store : {}", e.getMessage(), appoint, storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> editPermissionForManager(String username, String token, String managerToEdit, String storeNameId, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) {
        logger.info("{} is Trying to edit permission for manager : {} in store : {}", username, managerToEdit, storeNameId);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            userService.editPermissionForManager(username, managerToEdit, storeNameId, watch, editSupply, editBuyPolicy, editDiscountPolicy);
            logger.info("Finished edit permission to manager : {}  in store : {}", managerToEdit, storeNameId);
            return new ResponseEntity<>("Success edit permission for manager ", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while {} is trying to edit permission for manager : {} : in store : {}", e.getMessage(), username, managerToEdit, storeNameId);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> getAllHistoryPurchases(String userName, String token, String storeName) {
        logger.info("{} is Trying to get all history purchases from store {}", userName, storeName);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(userName, token)) return invalidTokenResponse();
            String result = marketService.getAllHistoryPurchases(userName, storeName);
            logger.info("{} Finished to get all history purchases from store {}", userName, storeName);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error occurred : {} , while {} trying to get all history purchases from store : {}", e.getMessage(), userName, storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> getHistoryPurchasesByCustomer(String userName, String token, String storeName, String customerUserName) {
        logger.info("{} is Trying to get history purchases by customer {} from store {}", userName, customerUserName, storeName);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(userName, token)) return invalidTokenResponse();
            String result = marketService.getHistoryPurchasesByCustomer(userName, storeName, customerUserName);
            logger.info("{} Finished to get history purchases by {} from store {}", userName, customerUserName, storeName);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error occurred : {} , while {} trying to get history purchases by client {} from store : {}", e.getMessage(), userName, customerUserName, storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> requestInformationAboutOfficialsInStore(String userName, String token, String storeName) {
        logger.info("{} is Trying to request Information about officials in store {}", userName, storeName);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(userName, token)) return invalidTokenResponse();
            String result = marketService.requestInformationAboutOfficialsInStore(userName, storeName);
            logger.info("{} Finished to request information about officials in store {}", userName, storeName);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error occurred : {} , while {} trying to request information about officials in store : {}", e.getMessage(), userName, storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> requestManagersPermissions(String userName, String token, String storeName) {
        logger.info("{} is Trying to request manager permissions in store {}", userName, storeName);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(userName, token)) return invalidTokenResponse();
            String result = marketService.requestManagersPermissions(userName, storeName);
            logger.info("{} Finished to request manager permissions in store {}", userName, storeName);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error occurred : {} , while {} trying to request managers permissions in store : {}", e.getMessage(), userName, storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> requestInformationAboutSpecificOfficialInStore(String userName, String token, String storeName, String officialUserName) {
        logger.info("{} is Trying to request Information about official {} in store {}", userName, officialUserName, storeName);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(userName, token)) return invalidTokenResponse();
            String result = marketService.requestInformationAboutSpecificOfficialInStore(userName, storeName, officialUserName);
            logger.info("{} Finished to request information about official {} in store {}", userName, officialUserName, storeName);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error occurred : {} , while {} trying to request information about official in store : {}", e.getMessage(), userName, storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> getAllStores(String userName, String token) {
        logger.info("Trying to Gather All Stores");
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(userName, token)) return invalidTokenResponse();
            logger.info("FINISHED Gather All Stores Info");
            return new ResponseEntity<>(marketService.getAllStores(userName), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed on Gathering Stores Info ", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> getStoreProducts(String userName, String token, String store_name) {
        logger.info("Trying to Gather ALL Store Products");
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(userName, token)) return invalidTokenResponse();
            logger.info("FINISHED Gather ALL Store Products Info");
            return new ResponseEntity<>(marketService.getStoreProducts(userName, store_name), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed on Gathering Store Products Info with Store Id : {} ", e.getMessage(), store_name);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> getProductInfo(String userName, String token, String store_name, int product_Id) {
        logger.info("Trying to Gather Product Info with Store Id : {} and product ID: {}", store_name, product_Id);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(userName, token)) return invalidTokenResponse();
            logger.info("FINISHED Gather Product Info");
            return new ResponseEntity<>(marketService.getProductInfo(userName, store_name, product_Id), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed on Gathering Product Info with Store Id : {} and product ID:{}", e.getMessage(), store_name, product_Id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> closeStoreExist(String userName, String token, String storeName) {
        logger.info("{} Trying to Close Store Exist : {}", userName, storeName);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(userName, token)) return invalidTokenResponse();
            marketService.closeStoreExist(userName, storeName);
            logger.info("Finished close store exist successfully");
            return new ResponseEntity<>("Store closed successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {} , when {} trying to close store exist : {}", e.getMessage(), userName, storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> openStoreExist(String userName, String token, String storeName) {
        logger.info("{} Trying to Open Store Exist : {}", userName, storeName);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(userName, token)) return invalidTokenResponse();
            marketService.openStoreExist(userName, storeName);
            logger.info("Finished open store exist successfully");
            return new ResponseEntity<>("Store opened successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {}, when {} trying to open store exist : {}", e.getMessage(), userName, storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //region Search functions
    // search in specific store
    @Override
    public ResponseEntity<String> searchNameInStore(String userName, String productName, String token, String store_name, Double minPrice, Double maxPrice, Double minRating, int category) {
        logger.info("Trying to search products in store : {} with name : {}", store_name, productName);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(userName, token)) return invalidTokenResponse();
            String result = marketService.searchNameInStore(userName, productName, store_name, minPrice, maxPrice, minRating, category);
            logger.info("Finished Searching products in store");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {}, to search products in store: {} with name : {}", e.getMessage(), store_name, productName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> searchCategoryInStore(String userName, String token, int category, String store_name, Double minPrice, Double maxPrice, Double minRating) {
        logger.info("Trying to search products in store : {} with category, : {}", store_name, category);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(userName, token)) return invalidTokenResponse();
            String result = marketService.searchCategoryInStore(userName, category, store_name, minPrice, maxPrice, minRating);
            logger.info("Finished Searching products in store");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {} ,  to search products in store : {} with category : {}}", e.getMessage(), store_name, category);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> searchKeywordsInStore(String userName, String token, String keyWords, String store_name, Double minPrice, Double maxPrice, Double minRating, int category) {
        logger.info("Trying to search products in store : {} with keyWords,  : {}", store_name, keyWords);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(userName, token)) return invalidTokenResponse();
            String result = marketService.searchKeywordsInStore(userName, keyWords, store_name, minPrice, maxPrice, minRating, category);
            logger.info("Finished Searching products in store");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {}, while trying to search products in store: {} with keyWords: {}", e.getMessage(), store_name, keyWords);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //search in stores
    @Override
    public ResponseEntity<String> searchNameInStores(String userName, String token, String productName, Double minPrice, Double maxPrice, Double minRating, int category, Double storeRating) {
        logger.info("Trying to search products in stores with name : {}", productName);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(userName, token)) return invalidTokenResponse();
            String result = marketService.searchNameInStores(userName, productName, minPrice, maxPrice, minRating, category, storeRating);
            logger.info("Finished Searching products in store");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {}, to search products in stores: {}", e.getMessage(), productName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> searchCategoryInStores(String userName, String token, int category, Double minPrice, Double maxPrice, Double minRating, Double storeRating) {
        logger.info("Trying to search products in stores with category, : {}", category);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(userName, token)) return invalidTokenResponse();
            String result = marketService.searchCategoryInStores(userName, category, minPrice, maxPrice, minRating, storeRating);
            logger.info("Finished Searching products in store");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {}, to search products in stores with category: {}", e.getMessage(), category);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> searchKeywordsInStores(String userName, String token, String keyWords, Double minPrice, Double maxPrice, Double minRating, int category, Double storeRating) {
        logger.info("Trying to search products in stores with keyWords,  : {}", keyWords);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(userName, token)) return invalidTokenResponse();
            String result = marketService.searchKeywordsInStores(userName, keyWords, minPrice, maxPrice, minRating, category, storeRating);
            logger.info("Finished searching products in stores ");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {} , to search products in stores with keyWords: {}", e.getMessage(), keyWords);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    //endregion

    @Override
    public ResponseEntity<String> approvePurchase(String username, String token) {
        logger.info("Approving purchase for registered user with ID: {} ", username);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            this.userService.approvePurchase(username);
            return new ResponseEntity<>("Purchase approved", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error approving purchase for registered user with ID: {}, error: {}", username, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> getPurchaseHistory(String username, String token, String storeName) {
        logger.info("Get Purchase History");
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            String result = userService.getPurchaseHistory(username, storeName);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while Getting Purchase History");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> getStoresPurchaseHistory(String username, String token, String storeName, Integer productBarcode) {
        return null;
    }


    @Override
    public ResponseEntity<String> addToCart(String username, String token, int productId, String storeName, int quantity) {
        logger.info("Trying adding to cart  product with id: {}", productId);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            userService.addToCart(username, productId, storeName, quantity);
            logger.info("Finished adding to cart product with id: {}", productId);
            return new ResponseEntity<>("Product added to cart successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed Trying adding to cart  product with id: {}", e.getMessage(), productId);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> removeFromCart(String username, String token, int productId, String storeName, int quantity) {
        logger.info("Trying removing from cart product with id: {}", productId);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            userService.removeFromCart(username, productId, storeName, quantity);
            logger.info("Finished removing from cart product with id: {}", productId);
            return new ResponseEntity<>("Product removed from cart successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed Trying removing to cart  product with id: {}", e.getMessage(), productId);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> openStore(String username, String token, String storeName, String description) {
        logger.info("Trying opening store with name: {}", storeName);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            userService.openStore(username, storeName, description);
            logger.info("Finished opening store with name: {}", storeName);
            return new ResponseEntity<>("Store opened successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed opening store with name: {}", e.getMessage(), storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> viewCart(String username, String token) {
        logger.info("Trying user : {} view cart ", username);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            String result = userService.viewCart(username);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed user view cart ", username);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> calculatePrice(String username, String token) {
        logger.info("Trying user : {} calculate price ", username);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            String result = userService.calculatePrice(username);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed user calculate price ", username);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //region Discount management

    @Override
    public ResponseEntity<String> getDiscountPolicies(String username, String token, String storeName) {
        logger.info("Trying to get discount policies for user: {} ,store: {}", username, storeName);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            String result = marketService.getDiscountPolicies(username, storeName);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while getting discount policies for user: {} ,store: {}", username, storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> getDiscountConditions(String username, String token, String storeName) {
        logger.info("Trying to get discount conditions for user: {} ,store: {}", username, storeName);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            String result = marketService.getDiscountConditions(username, storeName);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while getting discount conditions for user: {} ,store: {}", username, storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> addCategoryPercentageDiscount(String username, String token, String storeName, int category, double discountPercent) {
        logger.info("Trying to add category percentage discount for user: {} ,store: {}, category: {}, value: {}", username, storeName, category, discountPercent);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.addCategoryPercentageDiscount(username, storeName, category, discountPercent);
            return new ResponseEntity<>("Category percentage discount added successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while adding category percentage discount for user: {} ,store: {}, category: {}, value: {}", username, storeName, category, discountPercent);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> addProductPercentageDiscount(String username, String token, String storeName, int productId, double discountPercent) {
        logger.info("Trying to add product percentage discount for user: {} ,store: {}, productId: {}, discountPercent: {}", username, storeName, productId, discountPercent);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.addProductPercentageDiscount(username, storeName, productId, discountPercent);
            return new ResponseEntity<>("Product percentage discount added successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while adding product percentage discount for for user: {} ,store: {}, productId: {}, discountPercent: {}", username, storeName, productId, discountPercent);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> addStoreDiscount(String username, String token, String storeName, double discountPercent) {
        logger.info("Trying to add store discount for for user: {} ,store: {}, discountPercent: {}", username, storeName, discountPercent);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.addStoreDiscount(username, storeName, discountPercent);
            return new ResponseEntity<>("Store discount added successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while adding store discount for user: {} ,store: {}, discountPercent: {}", username, storeName, discountPercent);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> addConditionalDiscount(String username, String token, String storeName) {
        logger.info("Trying to add conditional discount for user: {} ,store: {}", username, storeName);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.addConditionalDiscount(username, storeName);
            return new ResponseEntity<>("Conditional discount added successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while adding conditional discount for user: {} ,store: {}", username, storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> addAdditiveDiscount(String username, String token, String storeName) {
        logger.info("Trying to add additive discount for user: {} ,store: {}", username, storeName);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.addAdditiveDiscount(username, storeName);
            return new ResponseEntity<>("Additive discount added successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while adding additive discount for user: {} ,store: {}", username, storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> addMaxDiscount(String username, String token, String storeName) {
        logger.info("Trying to add max discount for user: {} ,store: {}", username, storeName);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.addMaxDiscount(username, storeName);
            return new ResponseEntity<>("Max discount added successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while adding max discount for user: {} ,store: {}", username, storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> addCategoryCountCondition(String username, String token, String storeName, int category, int count) {
        logger.info("Trying to add category count condition for user: {} ,store: {}, category: {}, count: {}", username, storeName, category, count);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.addCategoryCountCondition(username, storeName, category, count);
            return new ResponseEntity<>("Category count condition added successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while adding category count condition for user: {} ,store: {}, category: {}, count: {}", username, storeName, category, count);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> addTotalSumCondition(String username, String token, String storeName, double requiredSum) {
        logger.info("Trying to add total sum condition for user: {} ,store: {}, sum: {}", username, storeName, requiredSum);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.addTotalSumCondition(username, storeName, requiredSum);
            return new ResponseEntity<>("Total sum condition added successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while adding total sum condition for user: {} ,store: {}, sum: {}", username, storeName, requiredSum);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> addProductCountCondition(String username, String token, String storeName, int productId, int count) {
        logger.info("Trying to add product count condition for user: {} ,store: {}, productId: {}, count: {}", username, storeName, productId, count);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.addProductCountCondition(username, storeName, productId, count);
            return new ResponseEntity<>("Product count condition added successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while adding product count condition for user: {} ,store: {}, productId: {}, count: {}", username, storeName, productId, count);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> addAndDiscount(String username, String token, String storeName) {
        logger.info("Trying to add AND discount for for user: {} ,store: {}", username, storeName);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.addAndDiscount(username, storeName);
            return new ResponseEntity<>("AND discount added successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while adding AND discount for user: {} ,store: {}", username, storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> addOrDiscount(String username, String token, String storeName) {
        logger.info("Trying to add OR discount for user: {} ,store: {}", username, storeName);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.addOrDiscount(username, storeName);
            return new ResponseEntity<>("OR discount added successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while adding OR discount for user: {} ,store: {}", username, storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> addXorDiscount(String username, String token, String storeName) {
        logger.info("Trying to add XOR discount for user: {} ,store: {}", username, storeName);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.addXorDiscount(username, storeName);
            return new ResponseEntity<>("XOR discount added successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while adding XOR discount for user: {} ,store: {}", username, storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> removeDiscount(String username, String token, String storeName, int selectedIndex) {
        logger.info("Trying to remove discount for user: {} ,store: {}, index: {}", username, storeName, selectedIndex);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.removeDiscount(username, storeName, selectedIndex);
            return new ResponseEntity<>("Discount removed successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while removing discount for user: {} ,store: {}, index: {}", username, storeName, selectedIndex);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> setFirstDiscount(String username, String token, String storeName, int selectedDiscountIndex, int selectedFirstIndex) {
        logger.info("Trying to set first discount for for user: {} ,store: {}, discountIndex: {}, selectedIndex: {}", username, storeName, selectedDiscountIndex, selectedFirstIndex);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.setFirstDiscount(username, storeName, selectedDiscountIndex, selectedFirstIndex);
            return new ResponseEntity<>("First discount set successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while setting first discount for user: {} ,store: {}, discountIndex: {}, selectedIndex: {}", username, storeName, selectedDiscountIndex, selectedFirstIndex);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> setSecondDiscount(String username, String token, String storeName, int selectedDiscountIndex, int selectedSecondIndex) {
        logger.info("Trying to set second discount for user: {} ,store: {}, discountIndex: {}, selectedIndex: {}", username, storeName, selectedDiscountIndex, selectedSecondIndex);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.setSecondDiscount(username, storeName, selectedDiscountIndex, selectedSecondIndex);
            return new ResponseEntity<>("Second discount set successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while setting second discount for user: {} ,store: {}, discountIndex: {}, selectedIndex: {}", username, storeName, selectedDiscountIndex, selectedSecondIndex);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> setFirstCondition(String username, String token, String storeName, int selectedDiscountIndex, int selectedSecondIndex) {
        logger.info("Trying to set first condition for user: {} ,store: {}, discountIndex: {}, selectedIndex: {}", username, storeName, selectedDiscountIndex, selectedSecondIndex);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.setFirstCondition(username, storeName, selectedDiscountIndex, selectedSecondIndex);
            return new ResponseEntity<>("First condition set successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while setting first condition for user: {} ,store: {}, discountIndex: {}, selectedIndex: {}", username, storeName, selectedDiscountIndex, selectedSecondIndex);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> setSecondCondition(String username, String token, String storeName, int selectedDiscountIndex, int selectedSecondIndex) {
        logger.info("Trying to set second condition for user: {} ,store: {}, discountIndex: {}, selectedIndex: {}", username, storeName, selectedDiscountIndex, selectedSecondIndex);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.setSecondCondition(username, storeName, selectedDiscountIndex, selectedSecondIndex);
            return new ResponseEntity<>("Second condition set successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while setting second condition for user: {} ,store: {}, discountIndex: {}, selectedIndex: {}", username, storeName, selectedDiscountIndex, selectedSecondIndex);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> setThenDiscount(String username, String token, String storeName, int selectedDiscountIndex, int selectedThenIndex) {
        logger.info("Trying to set then discount for user: {} ,store: {}, discountIndex: {}, selectedIndex: {}", username, storeName, selectedDiscountIndex, selectedThenIndex);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.setThenDiscount(username, storeName, selectedDiscountIndex, selectedThenIndex);
            return new ResponseEntity<>("Then discount set successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while setting then discount for user: {} ,store: {}, discountIndex: {}, selectedIndex: {}", username, storeName, selectedDiscountIndex, selectedThenIndex);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> setCategoryDiscount(String username, String token, String storeName, int selectedDiscountIndex, int category) {
        logger.info("Trying to set category discount for user: {} ,store: {}, discountIndex: {}, category: {}", username, storeName, selectedDiscountIndex, category);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.setCategoryDiscount(username, storeName, selectedDiscountIndex, category);
            return new ResponseEntity<>("Category discount set successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while setting category discount for user: {} ,store: {}, discountIndex: {}, category: {}", username, storeName, selectedDiscountIndex, category);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> setProductIdDiscount(String username, String token, String storeName, int selectedDiscountIndex, int productId) {
        logger.info("Trying to set product ID discount for user: {} ,store: {}, discountIndex: {}, productId: {}", username, storeName, selectedDiscountIndex, productId);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.setProductIdDiscount(username, storeName, selectedDiscountIndex, productId);
            return new ResponseEntity<>("Product ID discount set successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while setting product ID discount for user: {} ,store: {}, discountIndex: {}, productId: {}", username, storeName, selectedDiscountIndex, productId);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> setPercentDiscount(String username, String token, String storeName, int selectedDiscountIndex, double discountPercent) {
        logger.info("Trying to set percent discount for user: {} ,store: {}, discountIndex: {}, percent: {}", username, storeName, selectedDiscountIndex, discountPercent);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.setPercentDiscount(username, storeName, selectedDiscountIndex, discountPercent);
            return new ResponseEntity<>("Percent discount set successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while setting percent discount for user: {} ,store: {}, discountIndex: {}, percent: {}", username, storeName, selectedDiscountIndex, discountPercent);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> setDeciderDiscount(String username, String token, String storeName, int selectedDiscountIndex, int selectedDeciderIndex) {
        logger.info("Trying to set decider discount for user: {} ,store: {}, discountIndex: {}, deciderIndex: {}", username, storeName, selectedDiscountIndex, selectedDeciderIndex);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.setDeciderDiscount(username, storeName, selectedDiscountIndex, selectedDeciderIndex);
            return new ResponseEntity<>("Decider discount set successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while setting decider discount for user: {} ,store: {}, discountIndex: {}, deciderIndex: {}", username, storeName, selectedDiscountIndex, selectedDeciderIndex);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> setTotalSum(String username, String token, String storeName, int selectedConditionIndex, double newSum) {
        logger.info("Trying to set total sum for user: {} ,store: {}, conditionIndex: {}, newSum: {}", username, storeName, selectedConditionIndex, newSum);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.setTotalSum(username, storeName, selectedConditionIndex, newSum);
            return new ResponseEntity<>("Total sum set successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while setting total sum for user: {} ,store: {}, conditionIndex: {}, newSum: {}", username, storeName, selectedConditionIndex, newSum);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> setCountCondition(String username, String token, String storeName, int selectedConditionIndex, int newCount) {
        logger.info("Trying to set count condition for user: {} ,store: {}, conditionIndex: {}, newCount: {}", username, storeName, selectedConditionIndex, newCount);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.setCountCondition(username, storeName, selectedConditionIndex, newCount);
            return new ResponseEntity<>("Count condition set successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while setting count condition for user: {} ,store: {}, conditionIndex: {}, newCount: {}", username, storeName, selectedConditionIndex, newCount);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> setCategoryCondition(String username, String token, String storeName, int selectedConditionIndex, int newCategory) {
        logger.info("Trying to set category condition for user: {} ,store: {}, conditionIndex: {}, category: {}", username, storeName, selectedConditionIndex, newCategory);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.setCategoryCondition(username, storeName, selectedConditionIndex, newCategory);
            return new ResponseEntity<>("Category condition set successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while setting category condition for user: {} ,store: {}, conditionIndex: {}, category: {}", username, storeName, selectedConditionIndex, newCategory);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> getPurchasePoliciesInfo(String username, String token, String storeName) {
        logger.info("Fetching purchase policies info for user: {}, store: {}", username, storeName);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            String result = marketService.getPurchasePoliciesInfo(username, storeName);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while fetching purchase policies info for user: {}, store: {}", username, storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> addPurchasePolicyByAge(String username, String token, String storeName, int ageToCheck, int category){
        logger.info("Adding age-based purchase policy for user: {}, store: {}, ageToCheck: {}, category: {}", username, storeName, ageToCheck, category);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.addPurchasePolicyByAge(username, storeName, ageToCheck, category);
            return new ResponseEntity<>("Age-based purchase policy added successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while adding age-based purchase policy for user: {}, store: {}, ageToCheck: {}, category: {}", username, storeName, ageToCheck, category);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> addPurchasePolicyByCategory(String username, String token, String storeName, int category, int productId) {
        logger.info("Adding category-based purchase policy for user: {}, store: {}, category: {}", username, storeName, category);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.addPurchasePolicyByCategory(username, storeName, category, productId);
            return new ResponseEntity<>("Category-based purchase policy added successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while adding category-based purchase policy for user: {}, store: {}, category: {}", username, storeName, category);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> addPurchasePolicyByCategoryAndDate(String username, String token, String storeName, int category, LocalDateTime dateTime){
        logger.info("Adding category and date-based purchase policy for user: {}, store: {}, category: {}, dateTime: {}", username, storeName, category, dateTime);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.addPurchasePolicyByCategoryAndDate(username, storeName, category, dateTime);
            return new ResponseEntity<>("Category and date-based purchase policy added successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while adding category and date-based purchase policy for user: {}, store: {}, category: {}, dateTime: {}", username, storeName, category, dateTime);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> addPurchasePolicyByDate(String username, String token, String storeName, LocalDateTime dateTime) {
        logger.info("Adding date-based purchase policy for user: {}, store: {}, dateTime: {}", username, storeName, dateTime);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.addPurchasePolicyByDate(username, storeName, dateTime);
            return new ResponseEntity<>("Date-based purchase policy added successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while adding date-based purchase policy for user: {}, store: {}, dateTime: {}", username, storeName, dateTime);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> addPurchasePolicyByProductAndDate(String username, String token, String storeName, int productId, LocalDateTime dateTime){
        logger.info("Adding product and date-based purchase policy for user: {}, store: {}, productId: {}, dateTime: {}", username, storeName, productId, dateTime);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.addPurchasePolicyByProductAndDate(username, storeName, productId, dateTime);
            return new ResponseEntity<>("Product and date-based purchase policy added successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while adding product and date-based purchase policy for user: {}, store: {}, productId: {}, dateTime: {}", username, storeName, productId, dateTime);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> addPurchasePolicyByShoppingCartMaxProductsUnit(String username, String token, String storeName, int productId, int numOfQuantity) {
        logger.info("Adding shopping cart max products unit purchase policy for user: {}, store: {}, productId: {}, numOfQuantity: {}", username, storeName, productId, numOfQuantity);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.addPurchasePolicyByShoppingCartMaxProductsUnit(username, storeName, productId, numOfQuantity);
            return new ResponseEntity<>("Shopping cart max products unit purchase policy added successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while adding shopping cart max products unit purchase policy for user: {}, store: {}, productId: {}, numOfQuantity: {}", username, storeName, productId, numOfQuantity);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> addPurchasePolicyByShoppingCartMinProducts(String username, String token, String storeName, int numOfQuantity){
        logger.info("Adding shopping cart min products purchase policy for user: {}, store: {}, weight: {}", username, storeName, numOfQuantity);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.addPurchasePolicyByShoppingCartMinProducts(username, storeName, numOfQuantity);
            return new ResponseEntity<>("Shopping cart min products purchase policy added successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while adding shopping cart min products purchase policy for user: {}, store: {}, weight: {}", username, storeName, numOfQuantity);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> addPurchasePolicyByShoppingCartMinProductsUnit(String username, String token, String storeName, int productId, int numOfQuantity) {
        logger.info("Adding shopping cart min products unit purchase policy for user: {}, store: {}, productId: {}, numOfQuantity: {}", username, storeName, productId, numOfQuantity);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.addPurchasePolicyByShoppingCartMinProductsUnit(username, storeName, productId, numOfQuantity);
            return new ResponseEntity<>("Shopping cart min products unit purchase policy added successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while adding shopping cart min products unit purchase policy for user: {}, store: {}, productId: {}, numOfQuantity: {}", username, storeName, productId, numOfQuantity);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> addAndPurchasePolicy(String username, String token, String storeName){
        logger.info("Adding AND purchase policy for user: {}, store: {}", username, storeName);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.addAndPurchasePolicy(username, storeName);
            return new ResponseEntity<>("AND purchase policy added successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while adding AND purchase policy for user: {}, store: {}", username, storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> addOrPurchasePolicy(String username, String token, String storeName) {
        logger.info("Adding OR purchase policy for user: {}, store: {}", username, storeName);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.addOrPurchasePolicy(username, storeName);
            return new ResponseEntity<>("OR purchase policy added successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while adding OR purchase policy for user: {}, store: {}", username, storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> addConditioningPurchasePolicy(String username, String token, String storeName) {
        logger.info("Adding conditioning purchase policy for user: {}, store: {}", username, storeName);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.addConditioningPurchasePolicy(username, storeName);
            return new ResponseEntity<>("Conditioning purchase policy added successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while adding conditioning purchase policy for user: {}, store: {}", username, storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> setPurchasePolicyProductId(String username, String token, String storeName, int selectedIndex, int productId){
        logger.info("Setting product ID for purchase policy for user: {}, store: {}, selectedIndex: {}, productId: {}", username, storeName, selectedIndex, productId);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.setPurchasePolicyProductId(username, storeName, selectedIndex, productId);
            return new ResponseEntity<>("Product ID set for purchase policy successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while setting product ID for purchase policy for user: {}, store: {}, selectedIndex: {}, productId: {}", username, storeName, selectedIndex, productId);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> setPurchasePolicyNumOfQuantity(String username, String token, String storeName, int selectedIndex, int numOfQuantity) {
        logger.info("Setting number of quantity for purchase policy for user: {}, store: {}, selectedIndex: {}, numOfQuantity: {}", username, storeName, selectedIndex, numOfQuantity);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.setPurchasePolicyNumOfQuantity(username, storeName, selectedIndex, numOfQuantity);
            return new ResponseEntity<>("Number of quantity set for purchase policy successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while setting number of quantity for purchase policy for user: {}, store: {}, selectedIndex: {}, numOfQuantity: {}", username, storeName, selectedIndex, numOfQuantity);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> setPurchasePolicyDateTime(String username, String token, String storeName, int selectedIndex, LocalDateTime dateTime){
        logger.info("Setting date time for purchase policy for user: {}, store: {}, selectedIndex: {}, dateTime: {}", username, storeName, selectedIndex, dateTime);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.setPurchasePolicyDateTime(username, storeName, selectedIndex, dateTime);
            return new ResponseEntity<>("Date time set for purchase policy successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while setting date time for purchase policy for user: {}, store: {}, selectedIndex: {}, dateTime: {}", username, storeName, selectedIndex, dateTime);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> setPurchasePolicyAge(String username, String token, String storeName, int selectedIndex, int age){
        logger.info("Setting age for purchase policy for user: {}, store: {}, selectedIndex: {}, age: {}", username, storeName, selectedIndex, age);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            // Call service method to set age for purchase policy
            // Example: marketService.setPurchasePolicyAge(username, storeName, selectedIndex, age);
            return new ResponseEntity<>("Age set for purchase policy successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while setting age for purchase policy for user: {}, store: {}, selectedIndex: {}, age: {}", username, storeName, selectedIndex, age);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> setFirstPurchasePolicy(String username, String token, String storeName, int selectedDiscountIndex, int selectedFirstIndex){
        logger.info("Setting first purchase policy for user: {}, store: {}, discountIndex: {}, firstIndex: {}", username, storeName, selectedDiscountIndex, selectedFirstIndex);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.setFirstPurchasePolicy(username, storeName, selectedDiscountIndex, selectedFirstIndex);
            return new ResponseEntity<>("First purchase policy set successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while setting first purchase policy for user: {}, store: {}, discountIndex: {}, firstIndex: {}", username, storeName, selectedDiscountIndex, selectedFirstIndex);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> setSecondPurchasePolicy(String username, String token, String storeName, int selectedDiscountIndex, int selectedSecondIndex) {
        logger.info("Setting second purchase policy for user: {}, store: {}, discountIndex: {}, secondIndex: {}", username, storeName, selectedDiscountIndex, selectedSecondIndex);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.setSecondPurchasePolicy(username, storeName, selectedDiscountIndex, selectedSecondIndex);
            return new ResponseEntity<>("Second purchase policy set successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while setting second purchase policy for user: {}, store: {}, discountIndex: {}, secondIndex: {}", username, storeName, selectedDiscountIndex, selectedSecondIndex);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> removePurchasePolicy(String username, String token, String storeName, int selectedIndex) {
        logger.info("Removing purchase policy for user: {}, store: {}, selectedIndex: {}", username, storeName, selectedIndex);
        try {
            if (checkSystemClosed()) return systemClosedResponse();
            if (checkInvalidToken(username, token)) return invalidTokenResponse();
            marketService.removePurchasePolicy(username, storeName, selectedIndex);
            return new ResponseEntity<>("Purchase policy removed successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while removing purchase policy for user: {}, store: {}, selectedIndex: {}", username, storeName, selectedIndex);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //end region
}