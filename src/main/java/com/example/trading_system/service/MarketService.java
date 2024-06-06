package com.example.trading_system.service;

import com.example.trading_system.domain.stores.Category;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MarketService {

    void deleteInstance();

    String getAllStores();

    void openStoreExist(String userName, String storeName) throws IllegalArgumentException;

    void closeStoreExist(String userName, String storeName) throws IllegalArgumentException;

    String getStoreProducts(String storeName);

    String getProductInfo(String storeName, int productId);

    String searchNameInStore(String name, String storeName, Double minPrice, Double maxPrice, Double minRating, Category category);

    String searchCategoryInStore(String userName, Category category, String storeName, Double minPrice, Double maxPrice, Double minRating);

    String searchKeywordsInStore(String userName, String keyWords, String storeName, Double minPrice, Double maxPrice, Double minRating, Category category);

    String searchNameInStores(String name, Double minPrice, Double maxPrice, Double minRating, Category category,Double storeRating);

    String searchCategoryInStores(String userName, Category category, Double minPrice, Double maxPrice, Double minRating,Double storeRating);

    String searchKeywordsInStores(String userName, String keyWords, Double minPrice, Double maxPrice, Double minRating, Category category,Double storeRating);

    void addProduct(String username, int product_id, String store_name, String product_name, String product_description,
                    double product_price, int product_quantity, double rating, int category, List<String> keyWords) throws IllegalAccessException;


    void removeProduct(String username, String store_name, int product_id) throws IllegalAccessException;

    void setProductName(String username, String store_name_id, int productId, String product_name) throws IllegalAccessException;

    void setProductDescription(String username, String store_name_id, int productId, String product_description) throws IllegalAccessException;

    void setProductPrice(String username, String store_name_id, int productId, double product_price) throws IllegalAccessException;

    void setProductQuantity(String username, String store_name_id, int productId, int product_quantity) throws IllegalAccessException;

    void setRating(String username, String store_name_id, int productId, double rating) throws IllegalAccessException;

    void setCategory(String username, String store_name_id, int productId, int category) throws IllegalAccessException;

}
