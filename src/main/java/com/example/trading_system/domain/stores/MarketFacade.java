package com.example.trading_system.domain.stores;

import com.example.trading_system.domain.users.UserFacade;

import java.util.HashMap;
import java.util.List;

public interface MarketFacade {
    void deleteInstance();

    void initialize(UserFacade userFacade);

    String getAllStores();

    void openStoreExist(String userName, String storeName) throws IllegalArgumentException;

    void closeStoreExist(String userName, String storeName) throws IllegalArgumentException;

    void deactivateStore(String storeId);

    String getStoreProducts(String storeName);

    String getProductInfo(String storeName, int productId);

    String searchNameInStore(String name, String storeName, Double minPrice, Double maxPrice, Double minRating, Category category);

    String searchCategoryInStore(String userName, Category category, String storeName, Double minPrice, Double maxPrice, Double minRating);

    String searchKeywordsInStore(String userName, String keyWords, String storeName, Double minPrice, Double maxPrice, Double minRating, Category category);

    String searchNameInStores(String name, Double minPrice, Double maxPrice, Double minRating, Category category,Double storeRating);

    String searchCategoryInStores(String userName, Category category, Double minPrice, Double maxPrice, Double minRating,Double storeRating);

    String searchKeywordsInStores(String userName, String keyWords, Double minPrice, Double maxPrice, Double minRating, Category category,Double storeRating);

    boolean addProduct(String username,
                       int productId,
                       String storeName,
                       String productName,
                       String productDescription,
                       double productPrice,
                       int productQuantity,
                       double rating,
                       int category,
                       List<String> keyWords) throws IllegalAccessException;

    boolean removeProduct(String username, String storeName, int productId) throws IllegalAccessException;

    boolean setProductName(String username, String storeName, int productId, String productName) throws IllegalAccessException;

    boolean setProductDescription(String username, String storeName, int productId, String productDescription) throws IllegalAccessException;

    boolean setProductPrice(String username, String storeName, int productId, double productPrice) throws IllegalAccessException;

    boolean setProductQuantity(String username, String storeName, int productId, int productQuantity) throws IllegalAccessException;

    boolean setRating(String username, String storeName, int productId, double rating) throws IllegalAccessException;

    boolean setCategory(String username, String storeName, int productId, int category) throws IllegalAccessException;

    String getAllHistoryPurchases(String userName, String storeName) throws IllegalAccessException;

    String getHistoryPurchasesByCustomer(String userName, String storeName, String customerUserName) throws IllegalAccessException;

    String requestInformationAboutOfficialsInStore(String userName, String storeName) throws IllegalArgumentException, IllegalAccessException;

    String requestManagersPermissions(String userName, String storeName) throws IllegalArgumentException, IllegalAccessException;

    String requestInformationAboutSpecificOfficialInStore(String userName, String storeName, String officialUserName) throws IllegalArgumentException, IllegalAccessException;

    void addStore(String storeName, String description, StorePolicy storePolicy, String founder,Double storeRating);

    boolean isStoreExist(String store_name);

    HashMap<String, Store> getStores();

    Store getStore(String storeName);
}
