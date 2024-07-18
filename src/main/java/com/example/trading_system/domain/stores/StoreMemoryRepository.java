package com.example.trading_system.domain.stores;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;

@Repository
public class StoreMemoryRepository implements StoreRepository {
    private static StoreMemoryRepository instance = null;
    private HashMap<String, Store> stores;

    public StoreMemoryRepository() {
        stores = new HashMap<>();
    }

    public static StoreMemoryRepository getInstance() {
        if (instance == null) instance = new StoreMemoryRepository();
        return instance;
    }

    public void deleteInstance() {
        if(stores!=null){
            this.stores.clear();
        }
        instance = null;
    }

    @Override
    public void save(Store store) {

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