package com.example.trading_system.service;
import com.example.trading_system.domain.stores.StorePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TradingSystemImp implements TradingSystem{
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
        if(systemOpen) {
            this.marketService.deleteInstance();
        }
    }

    private boolean checkSystemOpen() {
        return systemOpen;
    }

    private boolean checkToken(String username, String token) {
        return Security.validateToken(username, token);
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
    public ResponseEntity<String> closeSystem(String username, String token){
        logger.info("Attempting to close system");
        try {
            if (!checkToken(username,token))
                return invalidTokenResponse();
            if (userService.isAdmin(username)) {
                //TODO call close method in deeper classes? (maybe logout all users)
                marketService.deleteInstance();
                systemOpen = false;
                logger.info("System closed by user: {}", username);
                return new ResponseEntity<>("System closed successfully.", HttpStatus.OK);
            }
            logger.info("Unauthorized user {} attempted to close the system", username);
            return new ResponseEntity<>("System can only be closed by an admin",HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("Error occurred while closing the system: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> enter() {
        logger.info("Attempting to enter system");
        try {
            if (!checkSystemOpen()) {
                logger.warn("System is not open, entry forbidden");
                return new ResponseEntity<>("", HttpStatus.FORBIDDEN);
            }
            logger.info("Trying enter to system as a visitor , with id : {}", counter_user);
            String username = userService.enter(counter_user);
            String token = Security.generateToken(username);
            counter_user++;
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
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(username,token))
                return invalidTokenResponse();
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
    public ResponseEntity<String> register(int id, String username, String password, LocalDate birthdate) {
        logger.info("Attempting to register user: {} with password : {} and birthdate : {} ", username,password,birthdate);
        try {
            userService.register(id, username, password, birthdate);
            logger.info("User registered successfully : {} with password : {} and birthdate : {}", username,password,birthdate);
            return new ResponseEntity<>("User registered successfully.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while registering user : {} with password : {} and birthdate : {} : {}", username,password,birthdate, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //TODO check if user is admin is external service functions?

    @Override
    public ResponseEntity<String> addProduct(String username, String token, int product_id, String store_name, String product_name, String product_description, double product_price, int product_quantity, double rating, int category, List<String> keyWords) {
        logger.info("User {} is trying to add products to store : {} with product Id : {} , product name : {}, product description : {} , product price : {} ," +
                        "product quantity : {} , rating : {} , category : {} , key words : {} ",username, store_name,product_id,product_name,product_description,product_price,
                product_quantity,rating,category,keyWords);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(username,token))
                return invalidTokenResponse();
            marketService.addProduct(username, product_id, store_name, product_name, product_description, product_price, product_quantity, rating, category, keyWords);
        } catch (Exception e) {
            logger.error("Error occurred while adding product: {} to store: {}: {}", product_name, store_name, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("User {} finished to add products to store : {} with product Id : {} , product name : {}, product description : {} , product price : {} ," +
                        "product quantity : {} , rating : {} , category : {} , key words : {} ",username, store_name,product_id,product_name,product_description,product_price,
                product_quantity,rating,category,keyWords);
        return new ResponseEntity<>("Product was added successfully.", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> removeProduct(String username, String token, String store_name, int product_id) {
        logger.info("User {} is trying to remove product with Id : {} to store : {} ",username,product_id, store_name);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(username,token))
                return invalidTokenResponse();
            marketService.removeProduct(username, store_name, product_id);
        } catch (Exception e) {
            logger.error("Error occurred while removing product with id: {} from store: {}: {}", product_id, store_name, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("User {} finished to remove product with Id :{} to store : {} ",username,product_id, store_name);
        return new ResponseEntity<>("Product was removed successfully.", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> setProductName(String username, String token, String store_name, int product_id, String product_name) {
        logger.info("User {} is trying to edit the name : {} to product : {} from store : {}",username,product_name, product_id,store_name);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(username,token))
                return invalidTokenResponse();
            marketService.setProductName(username, store_name, product_id, product_name);
        } catch (Exception e) {
            logger.error("Error occurred while setting product name for product id: {} in store: {}: {}", product_id, store_name, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("User {} finished to edit the name : {} to product : {} from store : {}",username,product_name, product_id,store_name);
        return new ResponseEntity<>("Product name was set successfully.", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> setProductDescription(String username, String token, String store_name, int product_id, String product_description) {
        logger.info("User {} is trying to edit the description : {} to product : {} from store : {}",username,product_description, product_id,store_name);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(username,token))
                return invalidTokenResponse();
            marketService.setProductDescription(username, store_name, product_id, product_description);
        } catch (Exception e) {
            logger.error("Error occurred while setting product description for product id: {} in store: {}: {}", product_id, store_name, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("User {} finished to edit the description : {} to product : {} from store : {}",username,product_description, product_id,store_name);
        return new ResponseEntity<>("Product description was set successfully.", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> setProductPrice(String username, String token, String store_name, int product_id, double product_price) {
        logger.info("User {} is trying to edit the price : {} to product : {} from store : {}",username,product_price, product_id,store_name);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(username,token))
                return invalidTokenResponse();
            marketService.setProductPrice(username, store_name, product_id, product_price);
        } catch (Exception e) {
            logger.error("Error occurred while setting product price for product id: {} in store: {}: {}", product_id, store_name, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("User {} finished to edit the price : {} to product : {} from store : {}",username,product_price, product_id,store_name);
        return new ResponseEntity<>("Product price was set successfully.", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> setProductQuantity(String username, String token, String store_name, int product_id, int product_quantity) {
        logger.info("User {} is trying to edit the quantity : {} to product : {} from store : {}",username,product_quantity, product_id,store_name);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(username,token))
                return invalidTokenResponse();
            marketService.setProductQuantity(username, store_name, product_id, product_quantity);
        } catch (Exception e) {
            logger.error("Error occurred while setting product quantity for product id: {} in store: {}: {}", product_id, store_name, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("User {} finished to edit the quantity : {} to product : {} from store : {}",username,product_quantity, product_id,store_name);
        return new ResponseEntity<>("Product quantity was set successfully.", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> setRating(String username, String token, String store_name, int product_id, double rating) {
        logger.info("User {} is trying to edit the rating : {} to product : {} from store : {}",username,rating, product_id,store_name);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(username,token))
                return invalidTokenResponse();
            marketService.setRating(username, store_name, product_id, rating);
        } catch (Exception e) {
            logger.error("Error occurred while setting rating for product id: {} in store: {}: {}", product_id, store_name, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("User {} finished to edit the rating : {} to product : {} from store : {}",username,rating, product_id,store_name);
        return new ResponseEntity<>("Rating was set successfully.", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> setCategory(String username, String token, String store_name, int product_id, int category) {
        logger.info("User {} is trying to edit the category : {} to product : {} from store : {}",username,category, product_id,store_name);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(username,token))
                return invalidTokenResponse();
            marketService.setCategory(username, store_name, product_id, category);
        } catch (Exception e) {
            logger.error("Error occurred while setting category for product id: {} in store: {}: {}", product_id, store_name, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("User {} finished to edit the category : {} to product : {} from store : {}",username,category, product_id,store_name);
        return new ResponseEntity<>("Category was set successfully.", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> login(String token, String usernameV, String username, String password) {
        logger.info("Attempting to login user: {}", username);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            if (userService.login(usernameV, username, password)) {
                if (!token.isEmpty())
                    Security.makeTokenExpire(token);
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

    //TODO why id? also create new visitor and return new token + username (maybe return result of enter?)
    @Override
    public ResponseEntity<String> logout(String token, int id, String username) {
        logger.info("Attempting to logout user: {}", username);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(username,token))
                return invalidTokenResponse();
            userService.logout(id, username);
            logger.info("User: {} logged out successfully", username);
            return new ResponseEntity<>("Logout successful.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while logging out user: {}: {}", username, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> suspendUser(String token, String admin, String toSuspend, LocalDateTime endSuspention) {
        logger.info("Attempting to suspend user: {}", toSuspend);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(admin,token))
                return invalidTokenResponse();
            userService.suspendUser(admin,toSuspend,endSuspention);
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
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(admin,token))
                return invalidTokenResponse();
            userService.endSuspendUser(admin,toSuspend);
            logger.info("Suspending user: {} is finished successfully", toSuspend);
            return new ResponseEntity<>("Ending suspension successful.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while ending suspended user: {}: {}", toSuspend, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //TODO fix this function if needed
    @Override
    public ResponseEntity<String> checkForEndingSuspension(String toSuspend) {
        logger.info("Checking to end suspension of user: {}", toSuspend);
        try {
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            Runnable task = () -> {
                userService.checkForEndingSuspension(toSuspend);
                logger.info("Checking to end suspension of user: {}", toSuspend);
            };
            executor.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
            return new ResponseEntity<>("Ending checking suspension successful.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while checking suspending user: {}: {}", toSuspend, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> watchSuspensions(String token, String admin) {
        logger.info("Attempting to get suspension details");
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(admin,token))
                return invalidTokenResponse();
            String details=userService.watchSuspensions(admin);
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
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(appoint,token))
                return invalidTokenResponse();
            userService.suggestManage(appoint, newManager, store_name_id, watch, editSupply, editBuyPolicy, editDiscountPolicy);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to suggest the user : {} to be a manager in store : {}", e.getMessage(), appoint, store_name_id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished suggesting manager : {} to be a manager in store : {}", newManager, store_name_id);
        return new ResponseEntity<>("Success suggesting manager", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> suggestOwner(String appoint, String token, String newOwner, String storeName) {
        logger.info("{} trying to suggest user : {} to be a owner in store : {}", appoint, newOwner, storeName);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(appoint,token))
                return invalidTokenResponse();
            userService.suggestOwner(appoint, newOwner, storeName);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to suggest the user : {} to be a owner in store : {}", e.getMessage(), appoint, storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished suggesting  : {} to be a owner in store : {}", newOwner, storeName);
        return new ResponseEntity<>("Success suggesting owner", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> approveManage(String newManager, String token, String store_name_id, String appoint) {
        logger.info("Trying to approve manage to store : {}", store_name_id);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(newManager,token))
                return invalidTokenResponse();
            userService.approveManage(newManager, store_name_id, appoint);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to approve management to store : {}", e.getMessage(), store_name_id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished approving manage to store : {}", store_name_id);
        return new ResponseEntity<>("Success approving manage", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> approveOwner(String newOwner, String token, String storeName, String appoint) {
        logger.info("{} trying to approve owner to store : {}", newOwner, storeName);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(newOwner,token))
                return invalidTokenResponse();
            userService.approveOwner(newOwner, storeName, appoint);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to approve owner to store : {}", e.getMessage(), storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished approving owner to store : {}", storeName);
        return new ResponseEntity<>("Success approving owner", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> rejectToOwnStore(String username, String token, String storeName, String appoint) {
        logger.info("{} trying to reject owner to store : {}", username, storeName);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(username,token))
                return invalidTokenResponse();
            userService.rejectToOwnStore(username, storeName, appoint);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to reject owner to store : {}", e.getMessage(), storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished reject owner to store : {}", storeName);
        return new ResponseEntity<>("Success reject to be owner", HttpStatus.OK);
    }


    @Override
    public ResponseEntity<String> rejectToManageStore(String userName, String token, String store_name_id, String appoint) {
        logger.info("Trying to reject to manage to store : {}", store_name_id);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(userName,token))
                return invalidTokenResponse();
            userService.rejectToManageStore(userName, store_name_id, appoint);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to reject management to store : {}", e.getMessage(), store_name_id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished reject manage to store : {}", store_name_id);
        return new ResponseEntity<>("Success rejecting manage", HttpStatus.OK);
    }


    //TODO Same as suggestManager/approveManager?
    @Override
    public ResponseEntity<String> appointManager(String username, String token, String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) {
        logger.info("Trying to appoint manager : {} to store : {}", newManager, store_name_id);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(username,token))
                return invalidTokenResponse();
            userService.appointManager(appoint, newManager, store_name_id, watch, editSupply, editBuyPolicy, editDiscountPolicy);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to appoint the user : {} to store : {}", e.getMessage(), appoint, store_name_id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished appointing manager : {} to store : {}", newManager, store_name_id);
        return new ResponseEntity<>("Success appointing manager", HttpStatus.OK);
    }

    //TODO same as suggestOwner/approveOwner?
    @Override
    public ResponseEntity<String> appointOwner(String username, String token, String appoint, String newOwner, String storeName) {
        logger.info("Trying to appoint owner : {} to store : {}", newOwner, storeName);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(username,token))
                return invalidTokenResponse();
            userService.appointOwner(appoint, newOwner, storeName);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while trying to appoint the user : {} to be owner in store : {}", e.getMessage(), appoint, storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished appointing owner : {} to store : {}", newOwner, storeName);
        return new ResponseEntity<>("Success appointing owner", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> editPermissionForManager(String username, String token, String userId, String managerToEdit, String storeNameId, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) {
        logger.info("{} is Trying to edit permission for manager : {} in store : {}", userId, managerToEdit, storeNameId);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(username,token))
                return invalidTokenResponse();
            userService.editPermissionForManager(userId, managerToEdit, storeNameId, watch, editSupply, editBuyPolicy, editDiscountPolicy);
        } catch (Exception e) {
            logger.error("Error occurred : {} , while {} is trying to edit permission for manager : {} : in store : {}", e.getMessage(), userId, managerToEdit, storeNameId);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished edit permission to manager : {}  in store : {}", managerToEdit, storeNameId);
        return new ResponseEntity<>("Success edit permission for manager ", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> getAllStores() {
        logger.info("Trying to Gather All Stores");
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            logger.info("FINISHED Gather All Stores Info");
            return new ResponseEntity<>(marketService.getAllStores(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed on Gathering Stores Info ", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> getStoreProducts(String store_name) {
        logger.info("Trying to Gather ALL Store Products");
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            logger.info("FINISHED Gather ALL Store Products Info");
            return new ResponseEntity<>(marketService.getStoreProducts(store_name), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed on Gathering Store Products Info with Store Id : {} ", e.getMessage(), store_name);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> getProductInfo(String store_name, int product_Id) {
        logger.info("Trying to Gather Product Info with Store Id : {} and product ID: {}", store_name, product_Id);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            logger.info("FINISHED Gather Product Info");
            return new ResponseEntity<>(marketService.getProductInfo(store_name, product_Id), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed on Gathering Product Info with Store Id : {} and product ID:{}", e.getMessage(), store_name, product_Id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> closeStoreExist(String userName, String storeName) {
        logger.info("{} Trying to Close Store Exist : {}", userName, storeName);
        try{
            if(!checkSystemOpen())
                return systemClosedResponse();
            marketService.closeStoreExist(storeName);
        }
        catch(Exception e) {
            logger.error("Error occurred : {} , when {} trying to close store exist : {}", e.getMessage(), userName, storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        }
        logger.info("FINISHED close store exist successfully");
        return new ResponseEntity<>("FINISHED Close store exist successfully", HttpStatus.OK);


    }

    @Override
    public ResponseEntity<String> openStoreExist(String userName, String storeName) {
        logger.info("{} Trying to Open Store Exist : {}", userName, storeName);
        try {
            if(!checkSystemOpen())
                return systemClosedResponse();
            marketService.openStoreExist(storeName);
        }
        catch(Exception e) {
            logger.error("Error occurred : {} , when {} trying to open store exist : {}", e.getMessage(), userName, storeName);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        }
        logger.info("FINISHED open store exist successfully");
        return new ResponseEntity<>("FINISHED open store exist successfully", HttpStatus.OK);


    }

    //search in specific store
    @Override
    public ResponseEntity<String> searchNameInStore(String name, String store_name, Double minPrice, Double maxPrice, Double minRating, int category) {
        logger.info("Trying to search products in store : {} with name : {}", store_name, name);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            marketService.searchNameInStore(name, store_name, minPrice, maxPrice, minRating, category);
        } catch (Exception e) {
            logger.error("Error occurred : {} ,  to search products in store : {} with name : {}}", e.getMessage(), store_name, name);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("FINISHED Searching products in store ");
        return new ResponseEntity<>("FINISHED Searching products in store ", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> searchCategoryInStore(int category, String store_name, Double minPrice, Double maxPrice, Double minRating) {
        logger.info("Trying to search products in store : {} with category, : {}", store_name, category);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            marketService.searchCategoryInStore(category, store_name, minPrice, maxPrice, minRating);
        } catch (Exception e) {
            logger.error("Error occurred : {} ,  to search products in store : {} with category : {}}", e.getMessage(), store_name, category);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("FINISHED Searching products in store ");
        return new ResponseEntity<>("FINISHED Searching products in store ", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> searchKeywordsInStore(String keyWords, String store_name, Double minPrice, Double maxPrice, Double minRating, int category) {
        logger.info("Trying to search products in store : {} with keyWords,  : {}", store_name, keyWords);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            marketService.searchKeywordsInStore(keyWords, store_name, minPrice, maxPrice, minRating, category);
        } catch (Exception e) {
            logger.error("Error occurred : {} ,  to search products in store : {} with keyWords,  : {}}", e.getMessage(), store_name, keyWords);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("FINISHED Searching products in store ");
        return new ResponseEntity<>("FINISHED Searching products in store ", HttpStatus.OK);
    }

    //search in stores
    @Override
    public ResponseEntity<String> searchNameInStores(String name, Double minPrice, Double maxPrice, Double minRating, int category,Double storeRating) {
        logger.info("Trying to search products in stores with name : {}", name);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            marketService.searchNameInStores(name, minPrice, maxPrice, minRating, category,storeRating);
        } catch (Exception e) {
            logger.error("Error occurred : {} ,  to search products in stores: {}}", e.getMessage(), name);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("FINISHED Searching products in store ");
        return new ResponseEntity<>("FINISHED Searching products in store ", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> searchCategoryInStores(int category, Double minPrice, Double maxPrice, Double minRating,Double storeRating) {
        logger.info("Trying to search products in stores with category, : {}", category);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            marketService.searchCategoryInStores(category, minPrice, maxPrice, minRating,storeRating);
        } catch (Exception e) {
            logger.error("Error occurred : {} ,  to search products in stores with category : {}}", e.getMessage(), category);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("FINISHED Searching products in store ");
        return new ResponseEntity<>("FINISHED Searching products in store ", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> searchKeywordsInStores(String keyWords, Double minPrice, Double maxPrice, Double minRating, int category,Double storeRating) {
        logger.info("Trying to search products in stores with keyWords,  : {}", keyWords);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            marketService.searchKeywordsInStores(keyWords, minPrice, maxPrice, minRating, category,storeRating);
        } catch (Exception e) {
            logger.error("Error occurred : {} ,  to search products in stores with keyWords,  : {}}", e.getMessage(), keyWords);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("FINISHED Searching products in stores ");
        return new ResponseEntity<>("FINISHED Searching products in store ", HttpStatus.OK);
    }


    @Override
    public ResponseEntity<String> approvePurchase(String username, String token) {
        logger.info("Approving purchase for registered user with ID: {} ", username);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(username,token))
                return invalidTokenResponse();
            this.userService.approvePurchase(username);
            return new ResponseEntity<>("FINISHED Registered Approve Purchase ", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while approving purchase for registered user with ID: {}", username,e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> getPurchaseHistory(String username, String token, String storeName) {
        logger.info("Get Purchase History");
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(username,token))
                return invalidTokenResponse();
            String result = userService.getPurchaseHistory(username, storeName);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while Getting Purchase History");
            return new ResponseEntity<>("Error occurred while Getting Purchase History", HttpStatus.BAD_REQUEST);
        }
    }


    @Override
    public ResponseEntity<String> addToCart(String username, String token, int productId, String storeName, int quantity) {
        logger.info("Trying adding to cart  product with id: {}", productId);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(username,token))
                return invalidTokenResponse();
            userService.addToCart(username, productId, storeName, quantity);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed Trying adding to cart  product with id: {}", e.getMessage(), productId);
            return new ResponseEntity<>("Error occurred : {} , Failed Trying adding to cart  product with id: {}", HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished adding to cart product with id: {}", productId);
        return new ResponseEntity<>("Finished adding to cart product with id: {}", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> removeFromCart(String username, String token, int productId, String storeName, int quantity) {
        logger.info("Trying removing from cart product with id: {}", productId);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(username,token))
                return invalidTokenResponse();
            userService.removeFromCart(username, productId, storeName, quantity);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed Trying removing to cart  product with id: {}", e.getMessage(), productId);
            return new ResponseEntity<>("Error occurred : {} , Failed Trying removing to cart  product with id: {}", HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished removing from cart product with id: {}", productId);
        return new ResponseEntity<>("Finished removing from cart product with id: {}", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> openStore(String username, String token, String storeName, String description, StorePolicy policy) {
        logger.info("Trying opening store with name: {}", storeName);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(username,token))
                return invalidTokenResponse();
            userService.openStore(username, storeName, description, policy);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed opening store with name: {}", e.getMessage(), storeName);
            return new ResponseEntity<>("Error occurred in opening store", HttpStatus.BAD_REQUEST);
        }
        logger.info("Finished opening store with name: {}", storeName);
        return new ResponseEntity<>("Finished opening store with name: {}", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> viewCart(String username, String token) {
        logger.info("Trying registered : {} view cart ", username);
        try {
            if (!checkSystemOpen())
                return systemClosedResponse();
            if(!checkToken(username,token))
                return invalidTokenResponse();
            String result = userService.viewCart(username);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed registered view cart ", username);
            return new ResponseEntity<>("Finished registered view cart ", HttpStatus.OK);
        }
    }
}