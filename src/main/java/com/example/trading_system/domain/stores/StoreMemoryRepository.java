package com.example.trading_system.domain.stores;

import java.util.Collection;
import java.util.HashMap;

public class StoreMemoryRepository implements StoreRepository {
    private static StoreMemoryRepository instance = null;
    private HashMap<String, Store> stores;

    private StoreMemoryRepository() {
        stores = new HashMap<>();
    }

    public static StoreMemoryRepository getInstance() {
        if (instance == null) instance = new StoreMemoryRepository();
        return instance;
    }

    public void deleteInstance() {
        for (Store store : stores.values()) {
            store.getProducts().clear();
        }
        this.stores.clear();
        instance = null;
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
    public Collection<Store> getAllStoresByStores() {
        return stores.values();
    }

    @Override
    public boolean isEmpty() {
        return stores.isEmpty();
    }


    @Override
    public void addStore(String storeName, String description, String founder,Double storeRating) {
        Store store=new Store(storeName,description,founder,storeRating);
        stores.put(storeName, store);
        store.addOwner(founder);
    }

}
