package com.example.trading_system.domain.users;

import com.example.trading_system.domain.stores.StorePolicy;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.NoSuchElementException;

public interface UserFacade {
    void deleteInstance();

    HashMap<Integer, Visitor> getVisitors();

    HashMap<String, Registered> getRegistered();

    void enter(String username);

    void exit(int id) throws Exception;

    void exit(String username) throws Exception;

    void register(String username, String token, LocalDate birthdate) throws Exception;    //Complete with what to register

    void login(String usernameV,String username, String password);

    void logout(String username,String usernameV);

    boolean sendNotification(User sender, User receiver, String content);

    void saveUserCart(String username, int productId, String storeName, int quantity);

    void addToCart(String username, int productId, String storeName, int quantity);

    void visitorRemoveFromCart(String username,int productId, String storeName, int quantity);

    void registeredAddToCart(String username, int productId, String storeName, int quantity);

    void registeredRemoveFromCart(String username, int productId, String storeName, int quantity) throws Exception;

    void openStore(String username, String storeName, String description, StorePolicy policy) throws IllegalAccessException;

    void suggestManage(String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) throws IllegalAccessException, NoSuchElementException;

    void approveManage(String newManager, String store_name_id, String appoint) throws IllegalAccessException;

    void appointManager(String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) throws IllegalAccessException, NoSuchElementException;

    void suggestOwner(String appoint, String newOwner, String storeName) throws IllegalAccessException;

    void approveOwner(String newOwner, String storeName, String appoint) throws IllegalAccessException;

    void appointOwner(String appoint, String newOwner, String storeName) throws IllegalAccessException, NoSuchElementException;

    /**
     * @param userId        is the current user that do the update
     * @param managerToEdit is the manager that the update will affect
     **/
    void editPermissionForManager(String userId, String managerToEdit, String storeNameId, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) throws IllegalAccessException;

    String getUserPassword(String username);

    void removeVisitor(int id);

    boolean isAdminRegistered();

    String visitorViewCart(int id);

    String registeredViewCart(String username);

    boolean isAdmin(String username);



    String viewCart(String username);

    void removeFromCart(String username,int productId, String storeName, int quantity);
}
