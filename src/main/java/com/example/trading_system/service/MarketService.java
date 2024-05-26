package com.example.trading_system.service;

import com.example.trading_system.domain.stores.Category;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MarketService {

    String getAllStores();
    void openStoreExist    (String storeName);
    void closeStoreExist   (String storeName);
    String getStoreProducts(String storeName);
    String getProductInfo  (String storeName, int productId);
    String searchNameInStore(String name, String storeName, Double minPrice, Double maxPrice, Double minRating, Category category);
    String searchCategoryInStore(Category category, String storeName, Double minPrice, Double maxPrice, Double minRating);
    String searchKeywordsInStore(String keyWords, String storeName, Double minPrice, Double maxPrice, Double minRating, Category category);
    String searchNameInStores(String name, Double minPrice, Double maxPrice, Double minRating, Category category);

    String searchCategoryInStores(Category category, Double minPrice, Double maxPrice, Double minRating);

    String searchKeywordsInStores(String keyWords, Double minPrice, Double maxPrice, Double minRating, Category category);
    ResponseEntity<String> addProduct(String username, int product_id, String store_name, String product_name, String product_description,
                                      double product_price, int product_quantity, double rating, Category category, List<String> keyWords);
    ResponseEntity<String> removeProduct(String username, String store_name, int product_id);
    ResponseEntity<String> setProduct_name(String username, String store_name_id, int productId, String product_name);
    ResponseEntity<String> setProduct_description(String username, String store_name_id, int productId, String product_description);
    ResponseEntity<String> setProduct_price(String username, String store_name_id, int productId, int product_price);
    ResponseEntity<String> setProduct_quantity(String username, String store_name_id, int productId, int product_quantity);
    ResponseEntity<String> setRating(String username, String store_name_id, int productId, int rating);
    ResponseEntity<String> setCategory(String username, String store_name_id, int productId, Category category);

}
