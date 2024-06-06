package com.example.trading_system.service;

import com.example.trading_system.domain.stores.StorePolicy;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface UserService {
    void deleteInstance();

    String enter(int id);

    boolean isAdmin(String username);

    boolean addToCart(String username,int productId, String storeName, int quantity);

    boolean removeFromCart(String username, int productId, String storeName, int quantity);

    boolean openStore(String username, String storeName, String description, StorePolicy policy);

    boolean register(int id, String username, String password, LocalDate birthdate) throws Exception;

    boolean login(String usernameV, String username, String password);

    boolean logout(int id, String username);

    void suspendUser(String admin, String toSuspend, LocalDateTime endSuspention);

    void endSuspendUser(String admin, String toSuspend);

    void checkForEndingSuspension(String toSuspend);

    void suggestManage(String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) throws IllegalAccessException;

    void approveManage(String newManager, String store_name_id, String appoint) throws IllegalAccessException;

    void rejectToManageStore(String userName, String storeName, String appoint) throws IllegalAccessException;

    void appointManager(String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) throws IllegalAccessException;

    void suggestOwner(String appoint, String newOwner, String storeName) throws IllegalAccessException;

    void approveOwner(String newOwner, String storeName, String appoint) throws IllegalAccessException;

    void rejectToOwnStore(String userName, String storeName, String appoint) throws IllegalAccessException;

    void appointOwner(String appoint, String newOwner, String storeName) throws IllegalAccessException;

    void exit(String username) throws Exception;

    String viewCart(String username);

    void editPermissionForManager(String userId, String managerToEdit, String storeNameId, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) throws IllegalAccessException;

    boolean isAdminRegistered();
}
