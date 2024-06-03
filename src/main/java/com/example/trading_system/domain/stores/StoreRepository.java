package com.example.trading_system.domain.stores;
import java.util.HashMap;

public interface StoreRepository {
    Store getStore(String storeName);
    boolean isExist(String storeName);
    HashMap<String,Store> getAllStores();
    void deleteStore(String storeName);
    boolean isEmpty();
    void addStore(String storeName, String description, StorePolicy storePolicy, String founder);
}
