package com.example.trading_system.domain.stores;

import java.util.List;

public interface MarketFacade {

    String getAllStores();

    String getStoreProducts(String store_name);

    String getProductInfo(String store_name, int product_id);

    String searchNameInStore(String name, String store_name, Double minPrice, Double maxPrice, Double minRating, Category category);

    String searchCategoryInStore(Category category, String store_name, Double minPrice, Double maxPrice, Double minRating);

    String searchKeywordsInStore(String keyWords, String store_name, Double minPrice, Double maxPrice, Double minRating, Category category);
}
