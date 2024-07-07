package com.example.trading_system.service;

import com.example.trading_system.domain.stores.MarketFacade;

import java.time.LocalDateTime;
import java.util.List;

public interface MarketService {
    MarketFacade getMarketFacade();

    void deleteInstance();

    String getAllStores(String userName);

    String getAllStoresInJSONFormat(String username);

    String getStoresIOpened(String username);

    void openStoreExist(String userName, String storeName) throws IllegalAccessException;

    void closeStoreExist(String userName, String storeName) throws IllegalAccessException;

    String getStoreProducts(String userName, String storeName) throws IllegalAccessException;

    String getProductInfo(String userName, String storeName, int productId) throws IllegalAccessException;

    String searchNameInStore(String userName, String productName, String storeName, Double minPrice, Double maxPrice, Double minRating, int category) throws IllegalAccessException;

    String searchCategoryInStore(String userName, int category, String storeName, Double minPrice, Double maxPrice, Double minRating) throws IllegalAccessException;

    String searchKeywordsInStore(String userName, String keyWords, String storeName, Double minPrice, Double maxPrice, Double minRating, int category) throws IllegalAccessException;

    String searchNameInStores(String userName, String productName, Double minPrice, Double maxPrice, Double minRating, int category, Double storeRating);

    String searchCategoryInStores(String userName, int category, Double minPrice, Double maxPrice, Double minRating, Double storeRating);

    String searchKeywordsInStores(String userName, String keyWords, Double minPrice, Double maxPrice, Double minRating, int category, Double storeRating);

    void addProduct(String username, int product_id, String store_name, String product_name, String product_description, double product_price, int product_quantity, double rating, int category, List<String> keyWords) throws IllegalAccessException;

    void removeProduct(String username, String store_name, int product_id) throws IllegalAccessException;

    void setProductName(String username, String store_name_id, int productId, String product_name) throws IllegalAccessException;

    void setProductDescription(String username, String store_name_id, int productId, String product_description) throws IllegalAccessException;

    void setProductPrice(String username, String store_name_id, int productId, double product_price) throws IllegalAccessException;

    void setProductQuantity(String username, String store_name_id, int productId, int product_quantity) throws IllegalAccessException;

    void setRating(String username, String store_name_id, int productId, double rating) throws IllegalAccessException;

    void setCategory(String username, String store_name_id, int productId, int category) throws IllegalAccessException;

    String getAllHistoryPurchases(String userName, String storeName) throws IllegalAccessException;

    String getHistoryPurchasesByCustomer(String userName, String storeName, String customerUserName) throws IllegalAccessException;

    String requestInformationAboutOfficialsInStore(String userName, String storeName) throws IllegalArgumentException, IllegalAccessException;

    String requestManagersPermissions(String userName, String storeName) throws IllegalArgumentException, IllegalAccessException;

    String requestInformationAboutSpecificOfficialInStore(String userName, String storeName, String officialUserName) throws IllegalArgumentException, IllegalAccessException;

    void sendMessageUserToStore(String sender, String storeName, String content);

    void sendMessageStoreToUser(String owner, String receiver, String storeName, String content);

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

    void removeCondition(String username, String storeName, int selectedIndex) throws IllegalAccessException;
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

    //region purchase policy
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
    //endregion

    String getStoreMessagesJson(String admin, String storeName);
}
