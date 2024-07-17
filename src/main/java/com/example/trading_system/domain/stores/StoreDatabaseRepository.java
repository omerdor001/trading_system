package com.example.trading_system.domain.stores;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Repository
@Transactional
public class StoreDatabaseRepository implements StoreRepository {

    private static StoreDatabaseRepository instance = null;

    @PersistenceContext
    private EntityManager entityManager;

    public StoreDatabaseRepository() {
    }

    public static StoreDatabaseRepository getInstance(EntityManager entityManager) {
        if (instance == null) {
            instance = new StoreDatabaseRepository();
            instance.entityManager = entityManager;
        }
        return instance;
    }

    @Override
    public void deleteData(){
        entityManager.createQuery("DELETE FROM Store").executeUpdate();
    }

    @Override
    public void deleteInstance() {
        instance = null;
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
        List<Store> storeList = entityManager.createQuery("SELECT s FROM Store s", Store.class).getResultList();
        HashMap<String, Store> allStores = new HashMap<>();
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
        return count == 0;
    }
    @Transactional
    @Override
    public void addStore(String storeName, String description, String founder, Double storeRating) {
        Store store = new Store(storeName, description, founder, storeRating);
        store.addOwner(founder);
        entityManager.persist(store);
    }
    @Transactional
    public void save(Store store) {
        if (store != null) {
            if (entityManager.contains(store) || store.getNameId() != null && entityManager.find(Store.class, store.getNameId()) != null) {
                entityManager.merge(store);
            } else {
                entityManager.persist(store);
            }
        }
    }
}
