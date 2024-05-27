package com.example.trading_system.service;

import com.example.trading_system.domain.externalservices.Service;
import com.example.trading_system.domain.stores.Category;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface TradingSystem {
    ResponseEntity<String> openSystem();
    ResponseEntity<String> enter();
    ResponseEntity<String> exit(String token, int id);
    ResponseEntity<String> exit(String token, String username);
    ResponseEntity<String> register(int id, String username, String password, LocalDate birthdate);
    ResponseEntity<String> addPaymentService(String serviceName);
    ResponseEntity<String> addPaymentProxyService(String serviceName);
    ResponseEntity<String> addDeliveryService(String serviceName);
    ResponseEntity<String> addDeliveryProxyService(String serviceName);
    ResponseEntity<String> clearServices();

    ResponseEntity<String> replaceService(String newServiceName, String oldServiceName);
    ResponseEntity<String> changeServiceName(String serviceToChangeAt, String newName);
    ResponseEntity<String> makePayment(String serviceName, double amount);
    ResponseEntity<String> makeDelivery(String serviceName, String address);
    ResponseEntity<String> addProduct(String username, int product_id, String store_name, String product_name, String product_description,
                                      double product_price, int product_quantity, double rating, int category, List<String> keyWords);
    ResponseEntity<String> removeProduct(String username, String store_name, int product_id);
    ResponseEntity<String> setProductName(String username, String store_name_id, int productId, String product_name);
    ResponseEntity<String> setProductDescription(String username, String store_name_id, int productId, String product_description);
    ResponseEntity<String> setProductPrice(String username, String store_name_id, int productId, int product_price);
    ResponseEntity<String> setProductQuantity(String username, String store_name_id, int productId, int product_quantity);
    ResponseEntity<String> setRating(String username, String store_name_id, int productId, int rating);
    ResponseEntity<String> setCategory(String username, String store_name_id, int productId, int category);
    ResponseEntity<String> login(String token, int id, String username, String password);
    ResponseEntity<String> logout(int id, String userName);

    ResponseEntity<String> suggestManage(String appoint, String newManager, String store_name_id,boolean watch,boolean editSupply,boolean editBuyPolicy,boolean editDiscountPolicy) throws IllegalAccessException;
    ResponseEntity<String> approveManage(String newManager,String store_name_id, String appoint) throws IllegalAccessException;
    ResponseEntity<String> appointManager(String appoint, String newManager, String store_name_id,boolean watch,boolean editSupply,boolean editBuyPolicy,boolean editDiscountPolicy) throws IllegalAccessException;

    ResponseEntity<String> suggestOwner(String appoint, String newOwner, String storeName) throws IllegalAccessException;
    ResponseEntity<String> approveOwner(String newOwner, String storeName, String appoint) throws IllegalAccessException;
    ResponseEntity<String> appointOwner(String appoint, String newOwner, String storeName) throws IllegalAccessException;
    ResponseEntity<String> editPermissionForManager(String userId, String managerToEdit, String storeNameId, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy);


}
