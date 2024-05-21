package com.example.trading_system.domain.stores;

import java.util.List;

public interface MarketFacade {

    String getAllStores();
    String getStoreProducts(String store_name);
    String getProductInfo(String store_name, int product_id);
    List<Product> searchNameInStore(String name, String store_name);
    List<Product> searchCategoryInStore(Category category,String store_name);
    List<Product> searchKeywordsInStore(String keyWords,String store_name);
}
