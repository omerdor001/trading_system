package com.example.trading_system.domain.stores;

public interface MarketFacade {

    String getAllStores();
    String getStoreProducts(String store_name);
    String getProductInfo(String store_name, int product_id);

}
