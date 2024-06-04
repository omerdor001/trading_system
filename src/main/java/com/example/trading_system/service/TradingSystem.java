package com.example.trading_system.service;

import com.example.trading_system.domain.stores.Category;
import com.example.trading_system.domain.stores.StorePolicy;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface TradingSystem {
    void deleteInstance();

    ResponseEntity<String> openSystem();

    ResponseEntity<String> closeSystem(String username, String token);

    ResponseEntity<String> enter();

    //TODO might be removed because visitor has username v+id

    ResponseEntity<String> exit(String token, String username);

    ResponseEntity<String> register(int id, String username, String password, LocalDate birthdate);

    //TODO check if user is admin is external service functions?

    ResponseEntity<String> addProduct(String username, String token, int product_id, String store_name, String product_name, String product_description, double product_price, int product_quantity, double rating, int category, List<String> keyWords);

    ResponseEntity<String> removeProduct(String username, String token, String storeName, int productId);

    ResponseEntity<String> setProductName(String username, String token, String storeName, int productId, String productName);

    ResponseEntity<String> setProductDescription(String username, String token, String storeName, int productId, String productDescription);

    ResponseEntity<String> setProductPrice(String username, String token, String storeName, int productId, double productPrice);

    ResponseEntity<String> setProductQuantity(String username, String token, String storeName, int productId, int productQuantity);

    ResponseEntity<String> setRating(String username, String token, String storeName, int productId, double rating);

    ResponseEntity<String> setCategory(String username, String token, String storeName, int productId, int category);

    ResponseEntity<String> login(String token, String usernameV, String username, String password);

    //TODO why id? also create new visitor and return new token + username (maybe return result of enter?)
    ResponseEntity<String> logout(String token, int id, String username);

    ResponseEntity<String> suggestManage(String username, String token, String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy);

    ResponseEntity<String> suggestOwner(String username, String token, String appoint, String newOwner, String storeName);

    ResponseEntity<String> approveManage(String username, String token, String newManager, String store_name_id, String appoint);

    ResponseEntity<String> approveOwner(String username, String token, String newOwner, String storeName, String appoint);

    //TODO Same as suggestManager/approveManager?
    ResponseEntity<String> appointManager(String username, String token, String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy);

    //TODO same as suggestOwner/approveOwner?
    ResponseEntity<String> appointOwner(String username, String token, String appoint, String newOwner, String storeName);

    ResponseEntity<String> editPermissionForManager(String username, String token, String userId, String managerToEdit, String storeNameId, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy);

    ResponseEntity<String> getAllStores();

    ResponseEntity<String> getStoreProducts(String store_name);

    ResponseEntity<String> getProductInfo(String store_name, int product_Id);

    //search in specific store
    ResponseEntity<String> searchNameInStore(String name, String store_name, Double minPrice, Double maxPrice, Double minRating, Category category);

    ResponseEntity<String> searchCategoryInStore(Category category, String store_name, Double minPrice, Double maxPrice, Double minRating);

    ResponseEntity<String> searchKeywordsInStore(String keyWords, String store_name, Double minPrice, Double maxPrice, Double minRating, Category category);

    //search in stores
    ResponseEntity<String> searchNameInStores(String name, Double minPrice, Double maxPrice, Double minRating, Category category,Double storeRating);

    ResponseEntity<String> searchCategoryInStores(Category category, Double minPrice, Double maxPrice, Double minRating,Double storeRating);

    ResponseEntity<String> searchKeywordsInStores(String keyWords, Double minPrice, Double maxPrice, Double minRating, Category category,Double storeRating);

    //TODO edit all visitor/registered functions to single function (all visitors have username of "v+id")
    ResponseEntity<String> VisitorCheckAvailabilityAndConditions(String username, String token, int visitorId);

    ResponseEntity<String> registeredCheckAvailabilityAndConditions(String username, String token, String registeredId);

    ResponseEntity<String> VisitorApprovePurchase(String username, String token, int visitorId, String paymentservice);

    ResponseEntity<String> RegisteredApprovePurchase(String username, String token, String registeredId, String paymentservice);

    ResponseEntity<String> getPurchaseHistory(String username, String token, String storeName, Integer id, Integer productBarcode);

    ResponseEntity<String> getStoresPurchaseHistory(String username, String token, String storeName, Integer id, Integer productBarcode);

    ResponseEntity<String> addToCart(String username, String token, int productId, String storeName, int quantity);

    ResponseEntity<String> removeFromCart(String username, String token, int productId, String storeName, int quantity);

    ResponseEntity<String> openStore(String username, String token, String storeName, String description, StorePolicy policy);

    ResponseEntity<String> viewCart(String username, String token);
}
