package com.example.trading_system.domain.stores;

import com.example.trading_system.domain.users.UserFacade;

import java.util.HashMap;

public class StoreMemoryRepository implements StoreRepository{
    private HashMap<String, Store> stores;
    private static StoreMemoryRepository instance = null;

    private StoreMemoryRepository(){
        stores=new HashMap<>();
    }

    public static StoreMemoryRepository getInstance() {
        if (instance == null)
            instance = new StoreMemoryRepository();
        return instance;
    }

    public void deleteInstance() {
        instance = null;
        this.stores = null;
    }

    @Override
    public Store getStore(String storeName) {
        return stores.get(storeName);
    }

    @Override
    public boolean isExist(String storeName) {
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
    public boolean isEmpty() {
        return stores.isEmpty();
    }


    @Override
    public void addStore(String storeName, String description, StorePolicy storePolicy, String founder,Double storeRating) {
        stores.put(storeName,new Store(storeName,description,storePolicy,founder,storeRating));
    }


}
