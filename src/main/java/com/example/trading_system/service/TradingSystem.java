package com.example.trading_system.service;

import com.example.trading_system.domain.stores.StoreRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public interface TradingSystem {
    void setSystemOpen(boolean systemOpen); //For tests

    void deleteInstance();

    ResponseEntity<String> getPendingUserNotifications(String admin, String token, String username);

    ResponseEntity<String> openSystem(StoreRepository storeRepository);

    ResponseEntity<String> closeSystem(String username, String token);

    ResponseEntity<String> makeAdmin(String admin, String token, String newAdmin);

    ResponseEntity<String> enter();

    ResponseEntity<String> exit(String token, String username);

    ResponseEntity<String> register(String username, String password, LocalDate birthdate);

    ResponseEntity<String> closeStoreExist(String userName, String token, String storeName);

    ResponseEntity<String> openStoreExist(String userName, String token, String storeName);

    ResponseEntity<String> addProduct(String username, String token, int product_id, String store_name, String product_name, String product_description, double product_price, int product_quantity, double rating, int category, List<String> keyWords);

    ResponseEntity<String> removeProduct(String username, String token, String storeName, int productId);

    ResponseEntity<String> setProductName(String username, String token, String storeName, int productId, String productName);

    ResponseEntity<String> setProductDescription(String username, String token, String storeName, int productId, String productDescription);

    ResponseEntity<String> setProductPrice(String username, String token, String storeName, int productId, double productPrice);

    ResponseEntity<String> setProductQuantity(String username, String token, String storeName, int productId, int productQuantity);

    ResponseEntity<String> setRating(String username, String token, String storeName, int productId, double rating);

    ResponseEntity<String> setCategory(String username, String token, String storeName, int productId, int category);

    ResponseEntity<String> login(String token, String usernameV, String username, String password);

    ResponseEntity<String> sendPendingNotifications(String username, String token);

    ResponseEntity<String> logout(String token, String username);

    ResponseEntity<String> suspendUser(String token, String admin, String toSuspend, LocalDateTime endSuspention);

    ResponseEntity<String> endSuspendUser(String token, String admin, String toSuspend);

    ResponseEntity<String> setAddress(String username, String token, String address);

    ResponseEntity<String> watchSuspensions(String token, String admin);

    ResponseEntity<String> suggestOwner(String appoint, String token, String newOwner, String storeName);

    ResponseEntity<String> suggestManage(String appoint, String token, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy);

    ResponseEntity<String> approveOwner(String newOwner, String token, String storeName, String appoint);

    ResponseEntity<String> approveManage(String newManager, String token, String store_name_id, String appoint, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy);

    ResponseEntity<String> rejectToOwnStore(String username, String token, String storeName, String appoint);

    ResponseEntity<String> rejectToManageStore(String userName, String token, String store_name_id, String appoint);

    ResponseEntity<String> waiverOnOwnership(String userName, String token, String storeName);

    ResponseEntity<String> fireManager(String owner, String token, String storeName, String manager);

    ResponseEntity<String> fireOwner(String ownerAppoint, String token, String storeName, String ownerToFire);

    ResponseEntity<String> editPermissionForManager(String username, String token, String managerToEdit, String storeNameId, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy);

    ResponseEntity<String> getAllStores(String userName, String token);

    ResponseEntity<String> getAllStoresInJSONFormat(String username, String token);

    ResponseEntity<String> getStoresIOpened(String username, String token);

    ResponseEntity<String> getStoresIOwn(String username, String token);

    ResponseEntity<String> getUserRequestsOwnership(String username, String token);

    ResponseEntity<String> getUserRequestsManagement(String username, String token);

    ResponseEntity<String> getStoresIManage(String username, String token);

    ResponseEntity<String> getStoreProducts(String userName, String token, String store_name);

    ResponseEntity<String> getProductInfo(String userName, String token, String store_name, int product_Id);

    //search in specific store
    ResponseEntity<String> searchNameInStore(String userName, String productName, String token, String store_name, Double minPrice, Double maxPrice, Double minRating, int category);

    ResponseEntity<String> searchCategoryInStore(String userName, String token, int category, String store_name, Double minPrice, Double maxPrice, Double minRating);

    ResponseEntity<String> searchKeywordsInStore(String userName, String token, String keyWords, String store_name, Double minPrice, Double maxPrice, Double minRating, int category);

    //search in stores
    ResponseEntity<String> searchNameInStores(String userName, String token, String productName, Double minPrice, Double maxPrice, Double minRating, int category, Double storeRating);

    ResponseEntity<String> searchCategoryInStores(String userName, String token, int category, Double minPrice, Double maxPrice, Double minRating, Double storeRating);

    ResponseEntity<String> searchKeywordsInStores(String userName, String token, String keyWords, Double minPrice, Double maxPrice, Double minRating, int category, Double storeRating);


    ResponseEntity<String> approvePurchase(String username, String token);

    ResponseEntity<String> getPurchaseHistory(String username, String token, String storeName);

    ResponseEntity<String> getStoresPurchaseHistory(String username, String token, String storeName, Integer productBarcode);

    ResponseEntity<String> addToCart(String username, String token, int productId, String storeName, int quantity);

    ResponseEntity<String> removeFromCart(String username, String token, int productId, String storeName, int quantity);

    ResponseEntity<String> openStore(String username, String token, String storeName, String description);

    ResponseEntity<String> viewCart(String username, String token);

    ResponseEntity<String> getAllHistoryPurchases(String userName, String token, String storeName);

    ResponseEntity<String> getHistoryPurchasesByCustomer(String userName, String token, String storeName, String customerUserName);

    ResponseEntity<String> requestInformationAboutOfficialsInStore(String userName, String token, String storeName);

    ResponseEntity<String> requestManagersPermissions(String userName, String token, String storeName);

    ResponseEntity<String> requestInformationAboutSpecificOfficialInStore(String userName, String token, String storeName, String officialUserName);

    ResponseEntity<String> calculatePrice(String username, String token);

    ResponseEntity<String> sendMessageUserToUser(String sender, String token, String receiver, String content);

    ResponseEntity<String> sendMessageUserToStore(String sender, String token, String storeName, String content);

    ResponseEntity<String> sendMessageStoreToUser(String owner, String token, String receiver, String storeName, String content);

    ResponseEntity<String> getUserMessagesJson(String admin, String token, String username);

    ResponseEntity<String> getStoreMessagesJson(String admin, String token, String storeName);

    //region Discount creation
    ResponseEntity<String> getDiscountPolicies(String username, String token, String storeName);

    ResponseEntity<String> getDiscountConditions(String username, String token, String storeName);

    ResponseEntity<String> addCategoryPercentageDiscount(String username, String token, String storeName, int category, double discountPercent);

    ResponseEntity<String> addProductPercentageDiscount(String username, String token, String storeName, int productId, double discountPercent);

    ResponseEntity<String> addStoreDiscount(String username, String token, String storeName, double discountPercent);

    ResponseEntity<String> addConditionalDiscount(String username, String token, String storeName);

    ResponseEntity<String> addAdditiveDiscount(String username, String token, String storeName);

    ResponseEntity<String> addMaxDiscount(String username, String token, String storeName);

    ResponseEntity<String> addCategoryCountCondition(String username, String token, String storeName, int category, int count);

    ResponseEntity<String> addTotalSumCondition(String username, String token, String storeName, double requiredSum);

    ResponseEntity<String> addProductCountCondition(String username, String token, String storeName, int productId, int count);

    ResponseEntity<String> addAndDiscount(String username, String token, String storeName);

    ResponseEntity<String> addOrDiscount(String username, String token, String storeName);

    ResponseEntity<String> addXorDiscount(String username, String token, String storeName);

    ResponseEntity<String> removeDiscount(String username, String token, String storeName, int selectedIndex);

    //endregion

    //region Discount/Condition editing/manipulation
    ResponseEntity<String> setFirstDiscount(String username, String token, String storeName, int selectedDiscountIndex, int selectedFirstIndex);

    ResponseEntity<String> setSecondDiscount(String username, String token, String storeName, int selectedDiscountIndex, int selectedSecondIndex);

    ResponseEntity<String> setFirstCondition(String username, String token, String storeName, int selectedDiscountIndex, int selectedSecondIndex);

    ResponseEntity<String> setSecondCondition(String username, String token, String storeName, int selectedDiscountIndex, int selectedSecondIndex);

    ResponseEntity<String> setThenDiscount(String username, String token, String storeName, int selectedDiscountIndex, int selectedThenIndex);

    ResponseEntity<String> setCategoryDiscount(String username, String token, String storeName, int selectedDiscountIndex, int category);

    ResponseEntity<String> setProductIdDiscount(String username, String token, String storeName, int selectedDiscountIndex, int productId);

    ResponseEntity<String> setPercentDiscount(String username, String token, String storeName, int selectedDiscountIndex, double discountPercent);

    ResponseEntity<String> setDeciderDiscount(String username, String token, String storeName, int selectedDiscountIndex, int selectedDeciderIndex);

    ResponseEntity<String> setTotalSum(String username, String token, String storeName, int selectedConditionIndex, double newSum);

    ResponseEntity<String> setCountCondition(String username, String token, String storeName, int selectedConditionIndex, int newCount);

    ResponseEntity<String> setCategoryCondition(String username, String token, String storeName, int selectedConditionIndex, int newCategory);
    //endregion

    //region purchase policy
    ResponseEntity<String> getPurchasePoliciesInfo(String username, String token, String storeName);

    ResponseEntity<String> addPurchasePolicyByAge(String username, String token, String storeName, int ageToCheck, int category);

    ResponseEntity<String> addPurchasePolicyByCategoryAndDate(String username, String token, String storeName, int category, LocalDateTime dateTime);

    ResponseEntity<String> addPurchasePolicyByDate(String username, String token, String storeName, LocalDateTime dateTime);

    ResponseEntity<String> addPurchasePolicyByProductAndDate(String username, String token, String storeName, int productId, LocalDateTime dateTime);

    ResponseEntity<String> addPurchasePolicyByShoppingCartMaxProductsUnit(String username, String token, String storeName, int productId, int numOfQuantity);

    ResponseEntity<String> addPurchasePolicyByShoppingCartMinProducts(String username, String token, String storeName, int numOfQuantity);

    ResponseEntity<String> addPurchasePolicyByShoppingCartMinProductsUnit(String username, String token, String storeName, int productId, int numOfQuantity);

    ResponseEntity<String> addAndPurchasePolicy(String username, String token, String storeName);

    ResponseEntity<String> addOrPurchasePolicy(String username, String token, String storeName);

    ResponseEntity<String> addConditioningPurchasePolicy(String username, String token, String storeName);

    ResponseEntity<String> setPurchasePolicyProductId(String username, String token, String storeName, int selectedIndex, int productId);

    ResponseEntity<String> setPurchasePolicyNumOfQuantity(String username, String token, String storeName, int selectedIndex, int numOfQuantity);

    ResponseEntity<String> setPurchasePolicyDateTime(String username, String token, String storeName, int selectedIndex, LocalDateTime dateTime);

    ResponseEntity<String> setPurchasePolicyAge(String username, String token, String storeName, int selectedIndex, int age);

    ResponseEntity<String> setFirstPurchasePolicy(String username, String token, String storeName, int selectedDiscountIndex, int selectedFirstIndex);

    ResponseEntity<String> setSecondPurchasePolicy(String username, String token, String storeName, int selectedDiscountIndex, int selectedSecondIndex);

    ResponseEntity<String> removePurchasePolicy(String username, String token, String storeName, int selectedIndex);
    //endregion

    ResponseEntity<String> getIsWatchPermission(String username, String token, String manager, String storeName);

    ResponseEntity<String> getIsEditSupplyPermission(String username, String token, String manager, String storeName);

    ResponseEntity<String> getIsEditDiscountPolicyPermission(String username, String token, String manager, String storeName);

    ResponseEntity<String> getIsEditPurchasePolicyPermission(String username, String token, String manager, String storeName);
}
