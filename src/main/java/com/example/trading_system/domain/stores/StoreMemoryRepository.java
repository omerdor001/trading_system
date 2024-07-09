package com.example.trading_system.domain.stores;

import com.example.trading_system.domain.users.UserMemoryRepository;
import com.example.trading_system.service.MarketServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;

@Service
public class StoreMemoryRepository implements StoreRepository {
    private static StoreMemoryRepository instance = null;

    private final StoreDatabaseRepository storeDatabaseRepository;

    public StoreMemoryRepository(StoreDatabaseRepository storeDatabaseRepository) {
        this.storeDatabaseRepository = storeDatabaseRepository;
    }

    public static StoreMemoryRepository getInstance(StoreDatabaseRepository storeDatabaseRepository) {
        if (instance == null) instance = new StoreMemoryRepository(storeDatabaseRepository);
        return instance;
    }


    @Override
    public void deleteInstance() {
        storeDatabaseRepository.deleteInstance();
    }

    @Override
    public Store getStore(String storeName) {
        return storeDatabaseRepository.getStore(storeName);
    }

    @Override
    public boolean isExist(String storeName) {
        return storeDatabaseRepository.isExist(storeName);
    }

    @Override
    public HashMap<String, Store> getAllStores() {
        return storeDatabaseRepository.getAllStores();
    }

    @Override
    public void deleteStore(String storeName) {
        storeDatabaseRepository.deleteStore(storeName);
    }

    @Override
    public Collection<Store> getAllStoresByStores() {
        return storeDatabaseRepository.getAllStoresByStores();
    }

    @Override
    public boolean isEmpty() {
        return storeDatabaseRepository.isEmpty();
    }

    @Override
    public void addStore(String storeName, String description, String founder, Double storeRating) {
        storeDatabaseRepository.addStore(storeName, description, founder, storeRating);
    }
}
