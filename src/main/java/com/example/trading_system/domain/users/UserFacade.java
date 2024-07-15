package com.example.trading_system.domain.users;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public interface UserFacade {
    UserRepository getUserRepository();
    void setUserRepository(UserRepository userRepository);
    void deleteInstance();

    String getPendingUserNotifications(String admin, String username);

    void makeAdmin(String admin, String newAdmin);

    void enter(int id);

    void exit(String username) throws Exception;

    HashMap<String, User> getUsers();

    void register(String username, String password, LocalDate birthdate) throws Exception;

    void login(String usernameV, String username, String password);

    void sendPendingNotifications(String username);

    void sendNotification(String sender, String receiver, String content);

    void sendNotificationToStoreOwners(String sender, List<String> owners, String content);

    void logout(int id, String username);

    void suspendUser(String admin, String toSuspend, LocalDateTime endSuspension);

    void endSuspendUser(String admin, String toSuspend);

    void setAddress(String username, String address);

    String watchSuspensions(String admin);

    boolean isSuspended(String username);

    void saveUserCart(String username, int productId, String storeName, int quantity);

    void addToCart(String username, int productId, String storeName, int quantity, double price);

    void createStore(String username, String storeName, String description) throws IllegalAccessException;

    void suggestOwner(String appoint, String newOwner, String storeName) throws IllegalAccessException;

    void suggestManager(String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy, boolean acceptBids, boolean createLottery) throws IllegalAccessException, NoSuchElementException;

    void approveOwner(String newOwner, String storeName, String appoint) throws IllegalAccessException;

    void approveManager(String newManager, String store_name_id, String appoint, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy, boolean acceptBids, boolean createLottery) throws IllegalAccessException;

    void rejectToManageStore(String userName, String storeName, String appoint) throws IllegalAccessException;

    void rejectToOwnStore(String userName, String storeName, String appoint) throws IllegalAccessException;

    void waiverOnOwnership(String userName, String storeName) throws IllegalAccessException;

    void fireManager(String owner, String storeName, String manager) throws IllegalAccessException;

    void fireOwner(String ownerAppoint, String storeName, String owner) throws IllegalAccessException;

    /**
     * @param userId        is the current user that do the update
     * @param managerToEdit is the manager that the update will affect
     **/
    void editPermissionForManager(String userId, String managerToEdit, String storeNameId, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy, boolean acceptBids, boolean createLottery) throws IllegalAccessException;

    String getUserPassword(String username);

    boolean isAdminRegistered();

    boolean isAdmin(String username);

    String viewCart(String username) throws Exception;

    void removeFromCart(String username, int productId, String storeName, int quantity);

    boolean isUserExist(String username);

    User getUser(String username);

    String getPurchaseHistory(String username, String storeName);

    void bidPurchase(String userName, String storeName, int productID, double price, String address, String amount, String currency, String cardNumber, String month, String year, String holder, String ccv, String id) throws Exception;

    void purchaseCart(String username, String address, String amount, String currency, String cardNumber, String month, String year, String holder, String ccv, String id) throws Exception;

    String calculatePrice(String username) throws Exception;

    boolean getIsWatchPermission(String username,String storeName) throws IllegalAccessException;

    boolean getIsEditSupplyPermission(String username,String storeName) throws IllegalAccessException;

    boolean getIsEditDiscountPolicyPermission(String username,String storeName) throws IllegalAccessException;

    boolean getIsEditPurchasePolicyPermission(String username,String storeName) throws IllegalAccessException;

    void sendMessageUserToUser(String sender, String receiver, String content);

    String getUserMessagesJson(String admin, String username);

    String getStoresIOwn(String username);

    String getStoresIManage(String username);

    String getUserRequestsOwnership(String username);

    String getUserRequestsManagement(String username);

    String getPermissionsForUserJSONFormat(String username,String storeName);

    String getManagersOfStore(String username, String storeName);

}
