package com.example.trading_system.domain.stores;
import java.util.List;

public interface MarketFacade {

    String getAllStores();

    String getStoreProducts(String store_name);

    String getProductInfo(String store_name, int product_id);

    String searchNameInStore(String name, String store_name, Double minPrice, Double maxPrice, Double minRating, Category category);

    String searchCategoryInStore(Category category, String store_name, Double minPrice, Double maxPrice, Double minRating);

    String searchKeywordsInStore(String keyWords, String store_name, Double minPrice, Double maxPrice, Double minRating, Category category);

    String searchNameInStores(String name, Double minPrice, Double maxPrice, Double minRating, Category category);

    String searchCategoryInStores(Category category, Double minPrice, Double maxPrice, Double minRating);

    String searchKeywordsInStores(String keyWords, Double minPrice, Double maxPrice, Double minRating, Category category);

    boolean addProduct(String username, int product_id, String store_name, String product_name, String product_description,
                                     double product_price, int product_quantity, double rating, Category category, List<String> keyWords) throws IllegalAccessException;

    boolean removeProduct(String username,  String store_name_id,int product_id) throws IllegalAccessException;

    boolean setProduct_name(String username,String store_name_id,int productId,String product_name) throws IllegalAccessException;

    boolean setProduct_description(String username,String store_name_id,int productId,String product_description) throws IllegalAccessException;

    boolean setProduct_price(String username,String store_name_id,int productId,int product_price) throws IllegalAccessException;

    boolean setProduct_quantity(String username,String store_name_id,int productId,int product_quantity) throws IllegalAccessException;

    boolean setRating(String username,String store_name_id,int productId,int rating) throws IllegalAccessException;

    boolean setCategory(String username,String store_name_id,int productId,Category category) throws IllegalAccessException;

    String getAllHistoryPurchases(String userName, String storeName) throws IllegalArgumentException;

    String getHistoryPurchasesByCustomer(String userName, String storeName, String customerUserName) throws IllegalAccessException;


    }
