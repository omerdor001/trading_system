package com.example.trading_system.service;

import java.util.List;

public interface MarketService {

    void deleteInstance();

    String getAllStores(String userName);

    void openStoreExist(String userName, String storeName) throws IllegalAccessException;

    void closeStoreExist(String userName, String storeName) throws IllegalAccessException;

    String getStoreProducts(String userName, String storeName) throws IllegalAccessException;

    String getProductInfo(String userName, String storeName, int productId) throws IllegalAccessException;

    String searchNameInStore(String userName, String productName, String storeName, Double minPrice, Double maxPrice, Double minRating, int category);

    String searchCategoryInStore(String userName, int category, String storeName, Double minPrice, Double maxPrice, Double minRating);

    String searchKeywordsInStore(String userName, String keyWords, String storeName, Double minPrice, Double maxPrice, Double minRating, int category);

    String searchNameInStores(String userName, String productName, Double minPrice, Double maxPrice, Double minRating, int category,Double storeRating);

    String searchCategoryInStores(String userName, int category, Double minPrice, Double maxPrice, Double minRating,Double storeRating);

    String searchKeywordsInStores(String userName, String keyWords, Double minPrice, Double maxPrice, Double minRating, int category,Double storeRating);

    void addProduct(String username, int product_id, String store_name, String product_name, String product_description,
                    double product_price, int product_quantity, double rating, int category, List<String> keyWords) throws IllegalAccessException;


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
}
