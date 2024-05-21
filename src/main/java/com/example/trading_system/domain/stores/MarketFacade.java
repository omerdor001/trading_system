package com.example.trading_system.domain.stores;

public interface MarketFacade {

    String getAllStores();

    String getStoreProducts(String store_name);

    String getProductInfo(String store_name, int product_id);

    String searchNameInStores(String name, Double minPrice, Double maxPrice, Double minRating, Category category);

    String searchCategoryInStores(Category category, Double minPrice, Double maxPrice, Double minRating);

    String searchKeywordsInStores(String keyWords, Double minPrice, Double maxPrice, Double minRating, Category category);
}
