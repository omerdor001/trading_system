package com.example.trading_system.service;

import com.example.trading_system.domain.stores.StorePolicy;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

public interface UserService {
    String enter(int id);

    boolean registration(int id, String username, String password, LocalDate birthdate);


    boolean visitorAddToCart(int id, int productId, String storeName, int quantity);

    boolean visitorRemoveFromCart(int id, int productId, String storeName, int quantity);

    boolean registeredAddToCart(String username, int productId, String storeName, int quantity);

    boolean registeredRemoveFromCart(String username, int productId, String storeName, int quantity) throws Exception;

    boolean openStore(String username, String storeName, String description, StorePolicy policy);
    boolean register(int id, String username, String password, LocalDate birthdate);

    boolean login(int id, String username, String password);

    ResponseEntity<String> suggestManage(String appoint, String newManager, String store_name_id,boolean watch,boolean editSupply,boolean editBuyPolicy,boolean editDiscountPolicy);
    ResponseEntity<String> approveManage(String newManager,String store_name_id, String appoint);
   // ResponseEntity<String> appointManager(String appoint, String newManager, String store_name_id,boolean watch,boolean editSupply,boolean editBuyPolicy,boolean editDiscountPolicy) ;

    ResponseEntity<String> suggestOwner(String appoint, String newOwner, String storeName);
    ResponseEntity<String> approveOwner(String newOwner, String storeName, String appoint);
   // ResponseEntity<String> appointOwner(String appoint, String newOwner, String storeName);

    void exit(int id) throws Exception;
    void exit(String username) throws Exception;

    ResponseEntity<String> editPermissionForManager(String userId, String managerToEdit, String storeNameId, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy);
}
