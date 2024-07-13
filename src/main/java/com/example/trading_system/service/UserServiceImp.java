package com.example.trading_system.service;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.StoreRepository;
import com.example.trading_system.domain.users.UserFacadeImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.example.trading_system.domain.users.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class UserServiceImp implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);
    private static UserServiceImp instance = null;
    private UserFacadeImp userFacade;

    private UserServiceImp(PaymentService paymentService, DeliveryService deliveryService, NotificationSender notificationSender, UserRepository userRepository, StoreRepository storeRepository) {
        userFacade = UserFacadeImp.getInstance(paymentService, deliveryService, notificationSender, userRepository, storeRepository);
    }

    public static UserServiceImp getInstance(PaymentService paymentService, DeliveryService deliveryService, NotificationSender notificationSender, UserRepository userRepository, StoreRepository storeRepository) {
        if (instance == null)
            instance = new UserServiceImp(paymentService, deliveryService, notificationSender, userRepository, storeRepository);
        return instance;
    }

    public UserFacadeImp getUserFacade() {
        return userFacade;
    }

    @Override
    public void deleteInstance() {
        instance = null;
        userFacade.deleteInstance();
        userFacade = null;
    }

    @Override
    public String getPendingUserNotifications(String admin, String username) {
        return userFacade.getPendingUserNotifications(admin, username);
    }

    @Override
    public void makeAdmin(String admin, String newAdmin) {
        userFacade.makeAdmin(admin, newAdmin);
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
    public void sendPendingNotifications(String username) {
        userFacade.sendPendingNotifications(username);
    }

    @Override
    public boolean logout(int id, String username) {
        logger.info("Trying to logout user: {}", username);
        userFacade.logout(id, username);
        logger.info("User: {} logged out", username);
        return true;
    }

    @Override
    public void suspendUser(String admin, String toSuspend, LocalDateTime endSuspension) {
        userFacade.suspendUser(admin, toSuspend, endSuspension);
    }

    @Override
    public void endSuspendUser(String admin, String toSuspend) {
        userFacade.endSuspendUser(admin, toSuspend);
    }

    @Override
    public String watchSuspensions(String admin) {
        return userFacade.watchSuspensions(admin);
    }

    public void setAddress(String username, String address) {
        userFacade.setAddress(username, address);
    }

    @Override
    public boolean register(String username, String password, LocalDate birthdate) throws Exception {
        userFacade.register(username, password, birthdate);
        return true;
    }

    @Override
    public void addToCart(String username, int productId, String storeName, int quantity, double price) {
        logger.info("Trying adding to cart  product with id: {}", productId);
        userFacade.addToCart(username, productId, storeName, quantity, price);
        logger.info("Finished adding to cart product with id: {}", productId);
    }


    @Override
    public void removeFromCart(String username, int productId, String storeName, int quantity) {
        logger.info("Trying removing from cart product with id: {}", productId);
        userFacade.removeFromCart(username, productId, storeName, quantity);
        logger.info("Finished removing from cart product with id: {}", productId);
    }

    @Override
    public boolean openStore(String username, String storeName, String description) {
        logger.info("Trying opening store with name: {}", storeName);
        userFacade.createStore(username, storeName, description);
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
    public void suggestOwner(String appoint, String newOwner, String storeName) throws IllegalAccessException {
        logger.info("{} trying to suggest user : {} to be a owner in store : {}", appoint, newOwner, storeName);
        userFacade.suggestOwner(appoint, newOwner, storeName);
        logger.info("Finished suggesting  : {} to be a owner in store : {}", newOwner, storeName);
    }

    @Override
    public void suggestManage(String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy, boolean acceptBids) throws IllegalAccessException {
        logger.info("Trying to suggest user : {} to be a manager in store : {}", newManager, store_name_id);
        userFacade.suggestManager(appoint, newManager, store_name_id, watch, editSupply, editBuyPolicy, editDiscountPolicy, acceptBids);
        logger.info("Finished suggesting manager : {} to be a manager in store : {}", newManager, store_name_id);
    }

    @Override
    public void approveOwner(String newOwner, String storeName, String appoint) throws IllegalAccessException {
        logger.info("{} trying to approve owner to store : {}", newOwner, storeName);
        userFacade.approveOwner(newOwner, storeName, appoint);
        logger.info("Finished approving owner to store : {}", storeName);
    }

    @Override
    public void approveManage(String newManager, String store_name_id, String appoint, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy, boolean acceptBids) throws IllegalAccessException {
        logger.info("Trying to approve manage to store : {}", store_name_id);
        userFacade.approveManager(newManager, store_name_id, appoint,watch, editSupply, editBuyPolicy,  editDiscountPolicy, acceptBids);
        logger.info("Finished approving manage to store : {}", store_name_id);
    }

    @Override
    public void rejectToManageStore(String userName, String storeName, String appoint) throws IllegalAccessException {
        logger.info("{} trying to approve owner to store : {}", userName, storeName);
        userFacade.rejectToManageStore(userName, storeName, appoint);
        logger.info("Finished approving owner to store : {}", storeName);
    }

    @Override
    public void rejectToOwnStore(String userName, String storeName, String appoint) throws IllegalAccessException {
        logger.info("{} trying to approve owner to store : {}", userName, storeName);
        userFacade.rejectToOwnStore(userName, storeName, appoint);
        logger.info("Finished approving owner to store : {}", storeName);
    }

    @Override
    public void waiverOnOwnership(String userName, String storeName) throws IllegalAccessException {
        userFacade.waiverOnOwnership(userName, storeName);
    }

    @Override
    public void fireManager(String owner, String storeName, String manager) throws IllegalAccessException {
        userFacade.fireManager(owner, storeName, manager);
    }

    @Override
    public void fireOwner(String ownerAppoint, String storeName, String owner) throws IllegalAccessException {
        userFacade.fireOwner(ownerAppoint, storeName, owner);
    }

    @Override
    public void editPermissionForManager(String userId, String managerToEdit, String storeNameId, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy, boolean acceptBids) throws IllegalAccessException {
        logger.info("{} is Trying to edit permission for manager : {} in store : {}", userId, managerToEdit, storeNameId);
        userFacade.editPermissionForManager(userId, managerToEdit, storeNameId, watch, editSupply, editBuyPolicy, editDiscountPolicy, acceptBids);
        logger.info("Finished edit permission to manager : {}  in store : {}", managerToEdit, storeNameId);
    }

    @Override
    public boolean isAdminRegistered() {
        return userFacade.isAdminRegistered();
    }

    @Override
    public void approvePurchase(String registeredId) throws Exception {
        logger.info("Approving purchase for registered user with ID: {} ", registeredId);
        userFacade.purchaseCart(registeredId);
        logger.info("Purchase approved for registered user with ID: {}", registeredId);
    }

    @Override
    public String getPurchaseHistory(String username, String storeName) {
        String result;
        logger.info("Get Purchase History");
        result = userFacade.getPurchaseHistory(username, storeName);
        return result;
    }

    @Override
    public String calculatePrice(String username) throws Exception {
        logger.info("Calculating price for user with ID: {} ", username);
        String result = userFacade.calculatePrice(username);
        logger.info("Finished calculating price for user with ID: {}", username);
        return result;
    }

    @Override
    public void sendMessageUserToUser(String sender, String receiver, String content) {
        userFacade.sendMessageUserToUser(sender, receiver, content);
    }

    @Override
    public String getIsWatchPermission(String username, String storeName) throws IllegalAccessException {
        return String.valueOf(userFacade.getIsWatchPermission(username, storeName));
    }

    @Override
    public String getIsEditSupplyPermission(String username, String storeName) throws IllegalAccessException {
        return String.valueOf(userFacade.getIsEditSupplyPermission(username, storeName));
    }

    @Override
    public String getIsEditDiscountPolicyPermission(String username, String storeName) throws IllegalAccessException {
        return String.valueOf(userFacade.getIsEditDiscountPolicyPermission(username, storeName));
    }

    @Override
    public String getIsEditPurchasePolicyPermission(String username, String storeName) throws IllegalAccessException {
        return String.valueOf(userFacade.getIsEditPurchasePolicyPermission(username, storeName));
    }

    @Override
    public String getUserMessagesJson(String admin, String username) {
        return userFacade.getUserMessagesJson(admin, username);
    }

    @Override
    public String getStoresIOwn(String username) {
        return userFacade.getStoresIOwn(username);
    }

    @Override
    public String getStoresIManage(String username) {
        return userFacade.getStoresIManage(username);
    }

    @Override
    public String getUserRequestsOwnership(String username) {
        return userFacade.getUserRequestsOwnership(username);
    }

    @Override
    public String getUserRequestsManagement(String username) {
        return userFacade.getUserRequestsManagement(username);
    }

    @Override
    public String getPermissionsForUserJSONFormat(String username, String storeName) {
        return userFacade.getPermissionsForUserJSONFormat(username,storeName);
    }



}
