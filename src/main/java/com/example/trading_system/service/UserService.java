package com.example.trading_system.service;

import com.example.trading_system.domain.users.UserFacadeImp;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface UserService {
    UserFacadeImp getUserFacade();

    void deleteInstance();

    String getPendingUserNotifications(String admin, String username);

    void makeAdmin(String admin, String newAdmin);

    String enter(int id);

    boolean isAdmin(String username);

    void addToCart(String username, int productId, String storeName, int quantity, double price);


    void removeFromCart(String username, int productId, String storeName, int quantity);

    boolean openStore(String username, String storeName, String description);

    boolean register(String username, String password, LocalDate birthdate) throws Exception;

    boolean login(String usernameV, String username, String password);

    void sendPendingNotifications(String username);

    boolean logout(int id, String username);

    void suspendUser(String admin, String toSuspend, LocalDateTime endSuspention);

    void endSuspendUser(String admin, String toSuspend);

    void setAddress(String username, String address);

    String watchSuspensions(String admin);

    void suggestOwner(String appoint, String newOwner, String storeName) throws IllegalAccessException;

    void suggestManage(String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy, boolean acceptBids) throws IllegalAccessException;

    void approveOwner(String newOwner, String storeName, String appoint) throws Exception;

    void approveManage(String newManager, String store_name_id, String appoint, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy, boolean acceptBids) throws Exception;

    void rejectToManageStore(String userName, String storeName, String appoint) throws IllegalAccessException;

    void rejectToOwnStore(String userName, String storeName, String appoint) throws IllegalAccessException;

    void waiverOnOwnership(String userName, String storeName) throws IllegalAccessException;

    void fireManager(String owner, String storeName, String manager) throws IllegalAccessException;

    void fireOwner(String ownerAppoint, String storeName, String owner) throws IllegalAccessException;

    void exit(String username) throws Exception;

    String viewCart(String username) throws Exception;

    void editPermissionForManager(String userId, String managerToEdit, String storeNameId, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy, boolean acceptBids) throws IllegalAccessException;

    boolean isAdminRegistered();

    void approvePurchase(String registeredId, String address, String amount, String currency, String cardNumber, String month, String year, String holder, String ccv, String id) throws Exception;

    String getPurchaseHistory(String username, String storeName);

    String calculatePrice(String username) throws Exception;

    void sendMessageUserToUser(String sender, String receiver, String content);

    String getIsWatchPermission(String username,String storeName) throws IllegalAccessException;

    String getIsEditSupplyPermission(String username,String storeName) throws IllegalAccessException;

    String getIsEditDiscountPolicyPermission(String username,String storeName) throws IllegalAccessException;

    String getIsEditPurchasePolicyPermission(String username,String storeName) throws IllegalAccessException;

    String getUserMessagesJson(String admin, String username);

    String getStoresIOwn(String username);

    String getStoresIManage(String username);

    String getUserRequestsOwnership(String username);

    String getUserRequestsManagement(String username);

    String getPermissionsForUserJSONFormat(String username,String storeName);

    String getManagersOfStore(String username, String storeName);
}
