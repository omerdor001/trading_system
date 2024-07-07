package com.example.trading_system.domain.stores;

import com.example.trading_system.domain.users.UserFacade;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public interface MarketFacade {
    StoreRepository getStoreRepository();

    void deleteInstance();

    void initialize(UserFacade userFacade);

    String getAllStores(String userName);

    String getAllStoresInJSONFormat(String username);

    String getProductsFromStoreJSONFormat(String storeName);

    void openStoreExist(String userName, String storeId) throws IllegalAccessException;

    void closeStoreExist(String userName, String storeId) throws IllegalAccessException;

    void deactivateStore(String storeId);

    String getStoresIOpened(String username);

    String getStoreProducts(String userName, String storeName) throws IllegalAccessException;

    String getProductInfo(String userName, String storeName, int productId) throws IllegalAccessException;

    String searchNameInStore(String userName, String productName, String storeName, Double minPrice, Double maxPrice, Double minRating, int category) throws IllegalAccessException;

    String searchCategoryInStore(String userName, int category, String storeName, Double minPrice, Double maxPrice, Double minRating) throws IllegalAccessException;

    String searchKeywordsInStore(String userName, String keyWords, String storeName, Double minPrice, Double maxPrice, Double minRating, int category) throws IllegalAccessException;

    String searchNameInStores(String userName, String productName, Double minPrice, Double maxPrice, Double minRating, int category, Double storeRating);

    String searchCategoryInStores(String userName, int category, Double minPrice, Double maxPrice, Double minRating, Double storeRating);

    String searchKeywordsInStores(String userName, String keyWords, Double minPrice, Double maxPrice, Double minRating, int category, Double storeRating);

    boolean addProduct(String username, int productId, String storeName, String productName, String productDescription, double productPrice, int productQuantity, double rating, int category, List<String> keyWords) throws IllegalAccessException;

    boolean removeProduct(String username, String storeName, int productId) throws IllegalAccessException;

    boolean setProductName(String username, String storeName, int productId, String productName) throws IllegalAccessException;

    boolean setProductDescription(String username, String storeName, int productId, String productDescription) throws IllegalAccessException;

    boolean setProductPrice(String username, String storeName, int productId, double productPrice) throws IllegalAccessException;

    boolean setProductQuantity(String username, String storeName, int productId, int productQuantity) throws IllegalAccessException;

    boolean setRating(String username, String storeName, int productId, double rating) throws IllegalAccessException;

    boolean setCategory(String username, String storeName, int productId, int category) throws IllegalAccessException;

    void addKeywordToProduct(String username, String storeName, int productId,String keyword) throws IllegalAccessException;

    void removeKeywordToProduct(String username, String storeName, int productId,String keyword) throws IllegalAccessException;

    String getAllHistoryPurchases(String userName, String storeName) throws IllegalAccessException;

    String getHistoryPurchasesByCustomer(String userName, String storeName, String customerUserName) throws IllegalAccessException;

    String requestInformationAboutOfficialsInStore(String userName, String storeName) throws IllegalArgumentException, IllegalAccessException;

    String requestManagersPermissions(String userName, String storeName) throws IllegalArgumentException, IllegalAccessException;

    String requestInformationAboutSpecificOfficialInStore(String userName, String storeName, String officialUserName) throws IllegalArgumentException, IllegalAccessException;

    void addStore(String storeName, String description, String founder, Double storeRating);

    boolean isStoreExist(String store_name);

    HashMap<String, Store> getStores();

    Store getStore(String storeName);

    void releaseReservedProducts(int productId, int quantity, String storeName);

    void removeReservedProducts(int productId, int quantity, String storeName);

    double calculateTotalPrice(String cart) throws IOException;

    void addPurchase(String customerUsername, String productInSaleList, double totalPrice, String storeName) throws IOException;

    //region Discount creation
    String getDiscountPolicies(String username, String storeName) throws IllegalAccessException;

    String getDiscountConditions(String username, String storeName) throws IllegalAccessException;

    void addCategoryPercentageDiscount(String username, String storeName, int category, double discountPercent) throws IllegalAccessException;

    void addProductPercentageDiscount(String username, String storeName, int productId, double discountPercent) throws IllegalAccessException;

    void addStoreDiscount(String username, String storeName, double discountPercent) throws IllegalAccessException;

    void addConditionalDiscount(String username, String storeName) throws IllegalAccessException;

    void addAdditiveDiscount(String username, String storeName) throws IllegalAccessException;

    void addMaxDiscount(String username, String storeName) throws IllegalAccessException;

    void addCategoryCountCondition(String username, String storeName, int category, int count) throws IllegalAccessException;

    void addTotalSumCondition(String username, String storeName, double requiredSum) throws IllegalAccessException;

    void addProductCountCondition(String username, String storeName, int productId, int count) throws IllegalAccessException;

    void addAndDiscount(String username, String storeName) throws IllegalAccessException;

    void addOrDiscount(String username, String storeName) throws IllegalAccessException;

    void addXorDiscount(String username, String storeName) throws IllegalAccessException;

    void removeDiscount(String username, String storeName, int selectedIndex) throws IllegalAccessException;

    //endregion

    //region Discount/Condition editing/manipulation

    void setFirstDiscount(String username, String storeName, int selectedDiscountIndex, int selectedFirstIndex) throws IllegalAccessException;

    void setSecondDiscount(String username, String storeName, int selectedDiscountIndex, int selectedSecondIndex) throws IllegalAccessException;

    void setFirstCondition(String username, String storeName, int selectedDiscountIndex, int selectedSecondIndex) throws IllegalAccessException;

    void setSecondCondition(String username, String storeName, int selectedDiscountIndex, int selectedSecondIndex) throws IllegalAccessException;

    void setThenDiscount(String username, String storeName, int selectedDiscountIndex, int selectedThenIndex) throws IllegalAccessException;

    void setCategoryDiscount(String username, String storeName, int selectedDiscountIndex, int category) throws IllegalAccessException;

    void setProductIdDiscount(String username, String storeName, int selectedDiscountIndex, int productId) throws IllegalAccessException;

    void setPercentDiscount(String username, String storeName, int selectedDiscountIndex, double discountPercent) throws IllegalAccessException;

    void setDeciderDiscount(String username, String storeName, int selectedDiscountIndex, int selectedDeciderIndex) throws IllegalAccessException;

    void setTotalSum(String username, String storeName, int selectedConditionIndex, double newSum) throws IllegalAccessException;

    void setCountCondition(String username, String storeName, int selectedConditionIndex, int newCount) throws IllegalAccessException;

    void setCategoryCondition(String username, String storeName, int selectedConditionIndex, int newCategory) throws IllegalAccessException;
    //endregion

    //purchase_policy
    boolean validatePurchasePolicies(String cart, int age) throws IOException;

    String getPurchasePoliciesInfo(String username, String storeName) throws IllegalAccessException;

    void addPurchasePolicyByAge(String username, String storeName, int ageToCheck, int category) throws IllegalAccessException;

    void addPurchasePolicyByCategoryAndDate(String username, String storeName, int category, LocalDateTime dateTime) throws IllegalAccessException;

    void addPurchasePolicyByDate(String username, String storeName, LocalDateTime dateTime) throws IllegalAccessException;

    void addPurchasePolicyByProductAndDate(String username, String storeName, int productId, LocalDateTime dateTime) throws IllegalAccessException;

    void addPurchasePolicyByShoppingCartMaxProductsUnit(String username, String storeName, int productId, int numOfQuantity) throws IllegalAccessException;

    void addPurchasePolicyByShoppingCartMinProducts(String username, String storeName, int numOfQuantity) throws IllegalAccessException;

    void addPurchasePolicyByShoppingCartMinProductsUnit(String username, String storeName, int productId, int numOfQuantity) throws IllegalAccessException;

    void addAndPurchasePolicy(String username, String storeName) throws IllegalAccessException;

    void addOrPurchasePolicy(String username, String storeName) throws IllegalAccessException;

    void addConditioningPurchasePolicy(String username, String storeName) throws IllegalAccessException;

    void setPurchasePolicyProductId(String username, String storeName, int selectedIndex, int productId) throws IllegalAccessException;

    void setPurchasePolicyNumOfQuantity(String username, String storeName, int selectedIndex, int numOfQuantity) throws IllegalAccessException;

    void setPurchasePolicyDateTime(String username, String storeName, int selectedIndex, LocalDateTime dateTime) throws IllegalAccessException;

    void setPurchasePolicyAge(String username, String storeName, int selectedIndex, int age) throws IllegalAccessException;

    void setFirstPurchasePolicy(String username, String storeName, int selectedDiscountIndex, int selectedFirstIndex) throws IllegalAccessException;

    void setSecondPurchasePolicy(String username, String storeName, int selectedDiscountIndex, int selectedSecondIndex) throws IllegalAccessException;

    void removePurchasePolicy(String username, String storeName, int selectedIndex) throws IllegalAccessException;

    void sendMessageUserToStore(String sender, String storeName, String content);

    void sendMessageStoreToUser(String owner, String receiver, String storeName, String content);

    String getStoreMessagesJson(String admin, String storeName);
}
