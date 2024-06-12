package com.example.trading_system.service;

import com.example.trading_system.domain.stores.StorePolicy;
import com.example.trading_system.domain.users.UserFacadeImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class UserServiceImp implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);
    private static UserServiceImp instance = null;
    private UserFacadeImp userFacade;

    private UserServiceImp() {
        userFacade = UserFacadeImp.getInstance();
    }

    public static UserServiceImp getInstance() {
        if (instance == null)
            instance = new UserServiceImp();
        return instance;
    }

    @Override
    public void deleteInstance() {
        instance = null;
        userFacade.deleteInstance();
        userFacade = null;
    }

    @Override
    public String enter(int id) {
        userFacade.enter(id);
        return "v" + id;
    }

    @Override
    public void exit(String username) throws Exception {
        userFacade.exit(username);
    }

    @Override
    public boolean isAdmin(String username) {
        return userFacade.isAdmin(username);
    }

    @Override
    public boolean login(String usernameV, String username, String password) {
        logger.info("Trying to login user: {}", username);
        userFacade.login(usernameV, username, password);
        logger.info("User: {} logged in", username);
        return true;
    }

    @Override
    public boolean logout(int id, String username) {
        logger.info("Trying to logout user: {}", username);
        userFacade.logout(id, username);
        logger.info("User: {} logged out", username);
        return true;
    }


    @Override
    public boolean register(String username, String password, LocalDate birthdate) throws Exception {
        userFacade.register(username, password, birthdate);
        return true;
    }

    @Override
    public boolean addToCart(String username, int productId, String storeName, int quantity) {
        logger.info("Trying adding to cart  product with id: {}", productId);
        userFacade.addToCart(username, productId, storeName, quantity);
        logger.info("Finished adding to cart product with id: {}", productId);
        return true;
    }

    @Override
    public boolean removeFromCart(String username, int productId, String storeName, int quantity) {
        logger.info("Trying removing from cart product with id: {}", productId);
        userFacade.removeFromCart(username, productId, storeName, quantity);
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
    public String viewCart(String username) {
        logger.info("Trying registered : {} view cart ", username);
        String result = userFacade.viewCart(username);
        logger.info("Finished registered view cart: {} ", username);
        return result;
    }

    @Override
    public void suggestManage(String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) throws IllegalAccessException {
        logger.info("Trying to suggest user : {} to be a manager in store : {}", newManager, store_name_id);
        userFacade.suggestManage(appoint, newManager, store_name_id, watch, editSupply, editBuyPolicy, editDiscountPolicy);
        logger.info("Finished suggesting manager : {} to be a manager in store : {}", newManager, store_name_id);
    }

    @Override
    public void suggestOwner(String appoint, String newOwner, String storeName) throws IllegalAccessException {
        logger.info("{} trying to suggest user : {} to be a owner in store : {}", appoint, newOwner, storeName);
        userFacade.suggestOwner(appoint, newOwner, storeName);
        logger.info("Finished suggesting  : {} to be a owner in store : {}", newOwner, storeName);
    }


    @Override
    public void approveManage(String newManager, String store_name_id, String appoint) throws IllegalAccessException {
        logger.info("Trying to approve manage to store : {}", store_name_id);
        userFacade.approveManage(newManager, store_name_id, appoint);
        logger.info("Finished approving manage to store : {}", store_name_id);
    }

    @Override
    public void rejectToManageStore(String userName, String storeName, String appoint) throws IllegalAccessException {
        logger.info("{} trying to approve owner to store : {}", userName, storeName);
        userFacade.rejectToManageStore(userName, storeName, appoint);
        logger.info("Finished approving owner to store : {}", storeName);
    }

    @Override
    public void approveOwner(String newOwner, String storeName, String appoint) throws IllegalAccessException {
        logger.info("{} trying to approve owner to store : {}", newOwner, storeName);
        userFacade.approveOwner(newOwner, storeName, appoint);
        logger.info("Finished approving owner to store : {}", storeName);
    }

    @Override
    public void rejectToOwnStore(String userName, String storeName, String appoint) throws IllegalAccessException {
        logger.info("{} trying to approve owner to store : {}", userName, storeName);
        userFacade.rejectToOwnStore(userName, storeName, appoint);
        logger.info("Finished approving owner to store : {}", storeName);
    }

    @Override
    public void appointManager(String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) throws IllegalAccessException {
        logger.info("Trying to appoint manager : {} to store : {}", newManager, store_name_id);
        userFacade.appointManager(appoint, newManager, store_name_id, watch, editSupply, editBuyPolicy, editDiscountPolicy);
        logger.info("Finished appointing manager : {} to store : {}", newManager, store_name_id);
    }

    @Override
    public void appointOwner(String appoint, String newOwner, String storeName) throws IllegalAccessException {
        logger.info("Trying to appoint owner : {} to store : {}", newOwner, storeName);
        userFacade.appointOwner(appoint, newOwner, storeName);
        logger.info("Finished appointing owner : {} to store : {}", newOwner, storeName);
    }

    @Override
    public void editPermissionForManager(String userId, String managerToEdit, String storeNameId, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) throws IllegalAccessException {
        logger.info("{} is Trying to edit permission for manager : {} in store : {}", userId, managerToEdit, storeNameId);
        userFacade.editPermissionForManager(userId, managerToEdit, storeNameId, watch, editSupply, editBuyPolicy, editDiscountPolicy);
        logger.info("Finished edit permission to manager : {}  in store : {}", managerToEdit, storeNameId);
    }

    @Override
    public boolean isAdminRegistered() {
        return userFacade.isAdminRegistered();
    }

    @Override
    public void approvePurchase(String registeredId) throws Exception {
        logger.info("Approving purchase for registered user with ID: {} ", registeredId);
        userFacade.approvePurchase(registeredId);
        logger.info("Purchase approved for registered user with ID: {}", registeredId);

    }

    @Override
    public String getPurchaseHistory(String username, String storeName) {
        String result = "";
        logger.info("Get Purchase History");
        result = userFacade.getPurchaseHistory(username, storeName);
        return result;
    }


}
