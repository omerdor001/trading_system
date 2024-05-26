package com.example.trading_system.domain.users;

import com.example.trading_system.domain.stores.StorePolicy;

import java.time.LocalDate;
import java.util.HashMap;

public interface UserFacade {

    void createVisitor(int id);

    public HashMap<Integer, Visitor> getVisitors();
    public HashMap<String, Registered> getRegistered();
    void exit();
    void enter(int id);
    void exit(int id) throws Exception;
    void exit(String username) throws Exception;
    void registration(int id, String username, String token, LocalDate birthdate) throws Exception;    //Complete with what to register


    void login(String username);

    boolean sendNotification(User sender, User receiver, String content);


    void visitorAddToCart(int id, int productId, String storeName, int quantity);

    void visitorRemoveFromCart(int id, int productId, String storeName, int quantity);

    void registerdAddToCart(String username, int productId, String storeName, int quantity);

    void registerdRemoveFromCart(String username, int productId, String storeName, int quantity) throws Exception;
    void openStore(String username, String storeName, String description, StorePolicy policy);

    void suggestManage(String appoint, String newManager, String store_name_id,boolean watch,boolean editSupply,boolean editBuyPolicy,boolean editDiscountPolicy) throws IllegalAccessException, NoSuchElementException;
    void approveManage(String newManager,String store_name_id, String appoint) throws NoSuchElementException;
   // void appointManager(String appoint, String newManager, String store_name_id,boolean watch,boolean editSupply,boolean editBuyPolicy,boolean editDiscountPolicy) throws IllegalAccessException, NoSuchElementException;

    void suggestOwner(String appoint, String newOwner, String storeName) throws IllegalAccessException, NoSuchElementException;
    void approveOwner(String newOwner,String storeName, String appoint) throws NoSuchElementException;
   // void appointOwner(String appoint, String newOwner, String storeName) throws IllegalAccessException, NoSuchElementException;
    /**
     * @param userId  is the current user that do the update
     * @param managerToEdit  is the manager that the update will affect
     **/
    void editPermissionForManager(String userId, String managerToEdit,String storeNameId, boolean watch,boolean editSupply,boolean editBuyPolicy, boolean editDiscountPolicy) throws IllegalAccessException;
    String getUserPassword(String username);
    void removeVisitor(int id);

}
