package com.example.trading_system.domain.stores;

import java.util.HashMap;

public class StoreMemoryRepository implements StoreRepository{
    private HashMap<String, Store> stores;

    public StoreMemoryRepository(){
        stores=new HashMap<>();
    }
    @Override
    public Store getStore(String storeName) {
        return stores.get(storeName);
    }

    @Override
    public Boolean isExist(String storeName) {
        return stores.containsKey(storeName);
    }

    @Override
    public HashMap<String, Store> getAllStores() {
        return stores;
    }

    @Override
    public void deleteStore(String storeName) {
        stores.remove(storeName);
    }

    @Override
    public void addStore(String storeName, String description, StorePolicy storePolicy, String founder) {
        stores.put(storeName,new Store(storeName,description,storePolicy,founder));
    }


}
