package com.example.trading_system.service;

import com.example.trading_system.domain.stores.Category;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MarketService {

    String getAllStores();
    String getStoreProducts(String store_name);
    String getProductInfo(String store_name, int product_Id);
    String searchNameInStore(String name, String store_name, Double minPrice, Double maxPrice, Double minRating, Category category);
    String searchCategoryInStore(Category category, String store_name, Double minPrice, Double maxPrice, Double minRating);
    String searchKeywordsInStore(String keyWords, String store_name, Double minPrice, Double maxPrice, Double minRating, Category category);
    String searchNameInStores(String name, Double minPrice, Double maxPrice, Double minRating, Category category);
    String searchCategoryInStores(Category category, Double minPrice, Double maxPrice, Double minRating);
    String searchKeywordsInStores(String keyWords, Double minPrice, Double maxPrice, Double minRating, Category category);
    ResponseEntity<String> addProduct(String username, int product_id, String store_name, String product_name, String product_description,
                                             double product_price, int product_quantity, double rating, Category category, List<String> keyWords);

}
