package com.example.trading_system.service;

import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.users.UserFacadeImp;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserServiceImp implements UserService {

    private static UserServiceImp instance = null;
    private UserFacadeImp userFacade;

    private UserServiceImp(PaymentService paymentService, DeliveryService deliveryService) {
        userFacade = UserFacadeImp.getInstance(paymentService,deliveryService);
    }

    public static UserServiceImp getInstance(PaymentService paymentService, DeliveryService deliveryService) {
        if (instance == null) instance = new UserServiceImp(paymentService,deliveryService);
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
        userFacade.login(usernameV, username, password);
        return true;
    }

    @Override
    public boolean logout(int id, String username) {
        userFacade.logout(id, username);
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
    public void addToCart(String username, int productId, String storeName, int quantity) {
        userFacade.addToCart(username, productId, storeName, quantity);
    }

    @Override
    public void removeFromCart(String username, int productId, String storeName, int quantity) {
        userFacade.removeFromCart(username, productId, storeName, quantity);
    }

    @Override
    public boolean openStore(String username, String storeName, String description) {
        userFacade.createStore(username, storeName, description);
        return true;
    }

    @Override
    public String viewCart(String username) {
        return userFacade.viewCart(username);
    }

    @Override
    public void suggestManage(String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) throws IllegalAccessException {
        userFacade.suggestManage(appoint, newManager, store_name_id, watch, editSupply, editBuyPolicy, editDiscountPolicy);
    }

    @Override
    public void suggestOwner(String appoint, String newOwner, String storeName) throws IllegalAccessException {
        userFacade.suggestOwner(appoint, newOwner, storeName);
    }


    @Override
    public void approveManage(String newManager, String store_name_id, String appoint) throws IllegalAccessException {
        userFacade.approveManage(newManager, store_name_id, appoint);
    }

    @Override
    public void rejectToManageStore(String userName, String storeName, String appoint) throws IllegalAccessException {
        userFacade.rejectToManageStore(userName, storeName, appoint);
    }

    @Override
    public void approveOwner(String newOwner, String storeName, String appoint) throws IllegalAccessException {
        userFacade.approveOwner(newOwner, storeName, appoint);
    }

    @Override
    public void rejectToOwnStore(String userName, String storeName, String appoint) throws IllegalAccessException {
        userFacade.rejectToOwnStore(userName, storeName, appoint);
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
    public void appointManager(String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) throws IllegalAccessException {
        userFacade.appointManager(appoint, newManager, store_name_id, watch, editSupply, editBuyPolicy, editDiscountPolicy);
    }

    @Override
    public void appointOwner(String appoint, String newOwner, String storeName) throws IllegalAccessException {
        userFacade.appointOwner(appoint, newOwner, storeName);
    }

    @Override
    public void editPermissionForManager(String userId, String managerToEdit, String storeNameId, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) throws IllegalAccessException {
        userFacade.editPermissionForManager(userId, managerToEdit, storeNameId, watch, editSupply, editBuyPolicy, editDiscountPolicy);
    }

    @Override
    public boolean isAdminRegistered() {
        return userFacade.isAdminRegistered();
    }

    @Override
    public void approvePurchase(String registeredId) throws Exception {
        userFacade.purchaseCart(registeredId);
    }

    @Override
    public String getPurchaseHistory(String username, String storeName) {
        return userFacade.getPurchaseHistory(username, storeName);
    }

    @Override
    public String calculatePrice(String username) throws Exception {
        return userFacade.calculatePrice(username);
    }
}
