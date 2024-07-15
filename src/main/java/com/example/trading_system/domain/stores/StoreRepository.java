package com.example.trading_system.domain.stores;

import java.util.Collection;
import java.util.HashMap;

public interface StoreRepository {
    Store getStore(String storeName);

    boolean isExist(String storeName);

    HashMap<String, Store> getAllStores();

    void deleteStore(String storeName);

    Collection<Store> getAllStoresByStores();

    boolean isEmpty();

    void addStore(String storeName, String description, String founder, Double storeRating);

    void deleteInstance();
}
