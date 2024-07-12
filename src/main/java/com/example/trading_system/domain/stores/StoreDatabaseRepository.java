package com.example.trading_system.domain.stores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
@Primary
@Repository
public class StoreDatabaseRepository implements StoreRepository {

    private HashMap<String, Store> memoryStores = new HashMap<>();
    private static StoreDatabaseRepository instance = null;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public StoreDatabaseRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    public static StoreDatabaseRepository getInstance(EntityManager entityManagery) {
        if (instance == null) instance = new StoreDatabaseRepository(entityManagery);
        return instance;
    }
    @Override
    public void deleteInstance() {
        if (memoryStores != null) {
            this.memoryStores.clear();
            this.memoryStores = null;
        }
    }

    @Override
    public Store getStore(String storeName) {
        return entityManager.find(Store.class, storeName);
    }

    @Override
    public boolean isExist(String storeName) {
        return entityManager.find(Store.class, storeName) != null;
    }

    @Override
    public HashMap<String, Store> getAllStores() {
        HashMap<String, Store> allStores = new HashMap<>(memoryStores);
        List<Store> storeList = entityManager.createQuery("SELECT s FROM Store s", Store.class).getResultList();
        for (Store store : storeList) {
            allStores.put(store.getNameId(), store);
        }
        return allStores;
    }

    @Override
    public void deleteStore(String storeName) {
        Store store = entityManager.find(Store.class, storeName);
        if (store != null) {
            entityManager.remove(store);
        }
    }

    @Override
    public Collection<Store> getAllStoresByStores() {
        return entityManager.createQuery("SELECT s FROM Store s", Store.class).getResultList();
    }

    @Override
    public boolean isEmpty() {
        Long count = entityManager.createQuery("SELECT COUNT(s) FROM Store s", Long.class).getSingleResult();
        return memoryStores.isEmpty() && count == 0;
    }

    @Override
    public void addStore(String storeName, String description, String founder, Double storeRating) {
        Store store = new Store(storeName, description, founder, storeRating);
        store.addOwner(founder);
        entityManager.persist(store);
    }
}
