package com.example.trading_system.domain.stores;

import com.example.trading_system.domain.users.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
public class MarketFacadeImp implements MarketFacade {
    private static final Logger logger = LoggerFactory.getLogger(MarketFacadeImp.class);
    private static MarketFacadeImp instance = null;
    private final ConcurrentHashMap<String, Lock> storeLocks = new ConcurrentHashMap<>();
    private StoreRepository storeRepository;
    private UserFacade userFacade;

    @Autowired
    public MarketFacadeImp(/*@Qualifier("storeDatabaseRepository")*/ StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }
    public static MarketFacadeImp getInstance(StoreRepository storeRepository) {
        if (instance == null) instance = new MarketFacadeImp(storeRepository);
        return instance;
    }

    @Override
    public void initialize(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @Override
    public void deleteInstance() {
        if (instance != null) {
            if (instance.storeRepository != null) {
                instance.storeRepository.deleteInstance();
                instance.storeRepository = null;
            }
            instance.userFacade = null;
            instance = null;
        }
    }

    public StoreRepository getStoreRepository() {
        return storeRepository;
    }

    public boolean isStoreExist(String store_name) {
        return storeRepository.isExist(store_name);
    }

    private void validateUserAndStore(String username, String storeName) {
        if (!storeRepository.isExist(storeName)) {
            logger.error("Store must exist: {}", storeName);
            throw new IllegalArgumentException("Store must exist");
        }
        if (!userFacade.isUserExist(username)) {
            logger.error("User must exist: {}", username);
            throw new IllegalArgumentException("User must exist");
        }
        if (userFacade.isSuspended(username)) {
            logger.error("User is suspended from the system: {}", username);
            throw new RuntimeException("User is suspended from the system");
        }
    }

    public void addStore(String storeName, String description, String founder, Double storeRating) {
        storeRepository.addStore(storeName, description, founder, storeRating);
    }

    public void deactivateStore(String storeName) {
        Store store = storeRepository.getStore(storeName);
        if (store != null) {
            if (store.isActive()) {
                store.setActive(false);
            } else {
                throw new RuntimeException("Can't deactivate store that already not active");
            }
        }
    }

    //For Tests
    public boolean isProductExist(String userName, int productId, String storeName) throws IllegalAccessException {
        if (!storeRepository.isExist(storeName)) {
            throw new IllegalArgumentException("Store must exist");
        }
        Store store = storeRepository.getStore(storeName);
        if (!store.isOpen() && !(store.isRoleHolder(userName) || userFacade.isAdmin(userName)))
            throw new IllegalAccessException("When store is closed just role holders can check if product exist");


        return storeRepository.getStore(storeName).isProductExist(productId);
    }

    @Override
    public String getAllStores(String userName) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("\"stores\":");
        if (userFacade.isSuspended(userName)) {
            throw new RuntimeException("User is suspended from the system");
        }
        for (Store store : storeRepository.getAllStoresByStores()) {
            if (!store.isOpen() && !(userFacade.isAdmin(userName) || store.isRoleHolder(userName))) continue;
            sb.append(store.getNameId());
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String getAllStoresInJSONFormat(String username){
        List<Map<String, Object>> storeList = new ArrayList<>();
        for (Store store : storeRepository.getAllStoresByStores()) {
            Map<String, Object> storeMap = new HashMap<>();
            storeMap.put("name", store.getNameId());
            if(store.isOwnerOfStore(username))
                storeMap.put("role", "Owner");
            else if(store.isManagerOfStore(username))
                storeMap.put("role", "Manager");
            else storeMap.put("role", "Viewer");
            storeMap.put("status",store.isActive());
            storeMap.put("description", store.getDescription());
            storeMap.put("founder", store.getFounder().substring(1));
            storeMap.put("isOpen", store.isOpen());
            storeMap.put("rating",store.getStoreRating());
            storeList.add(storeMap);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(storeList);
        } catch (JsonProcessingException e) {
            logger.error("Error converting stores to JSON", e);
            return "Error converting stores to JSON";
        }
    }

    @Override
    public String getOwnersOfStore(String username,String storeName){
        validateUserAndStore(username,storeName);
        List<Map<String, Object>> ownersList = new ArrayList<>();
        for(String user:storeRepository.getStore(storeName).getOwners()){
            Map<String, Object> ownerMap = new HashMap<>();
            if(storeRepository.getStore(storeName).getFounder().equals(user))
                ownerMap.put("founder",true);
            else
                ownerMap.put("founder",true);
            ownerMap.put("username",user.substring(1));
            ownersList.add(ownerMap);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(ownersList);
        } catch (JsonProcessingException e) {
            logger.error("Error converting stores to JSON", e);
            return "Error converting stores to JSON";
        }
    }

    @Override
    public String getCategories(String username){
        if (!userFacade.isUserExist(username)) {
            throw new IllegalArgumentException("User must exist");
        }
        if (userFacade.isSuspended(username)) {
            throw new RuntimeException("User is suspended from the system");
        }
        return Product.getAllCategories().toString();
    }

    @Override
    public String getProductsFromStoreJSONFormat(String storeName){
        if (!isStoreExist(storeName)) {
            throw new IllegalArgumentException("Store is not exist");
        }
        Store store=storeRepository.getStore(storeName);
        List<Map<String, Object>> productList = new ArrayList<>();
        for(Product product:store.getProducts().values()){
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("id",product.getProduct_id());
            productMap.put("name",product.getProduct_name());
            productMap.put("description",product.getProduct_description());
            productMap.put("price",product.getProduct_price());
            productMap.put("quantity",product.getProduct_quantity());
            productMap.put("rating",product.getRating());
            productMap.put("category",Category.getCategoryFromInt(product.getCategory().getIntValue()));
            productMap.put("keyWords", product.getKeyWords());
            productList.add(productMap);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(productList);
        } catch (JsonProcessingException e) {
            logger.error("Error converting stores to JSON", e);
            return "Error converting stores to JSON";
        }
    }

    @Override
    public String getStoresIOpened(String username){
        if (!userFacade.isUserExist(username)) {
            throw new IllegalArgumentException("User must exist");
        }
        if (userFacade.isSuspended(username)) {
            throw new RuntimeException("User is suspended from the system");
        }
        List stores=new ArrayList();
        for(Store store:storeRepository.getAllStoresByStores()){
            if(store.getFounder().equals(username)  && store.isOpen())
                stores.add(store.getNameId());
        }
        return stores.toString().substring(1,stores.toString().length()-1);
    }



    @Override
    public String getStoreProducts(String userName, String storeName) throws IllegalAccessException {
        validateUserAndStore(userName, storeName);
        Store store = storeRepository.getStore(storeName);
        if (!store.isOpen() && !(store.isRoleHolder(userName) || userFacade.isAdmin(userName)))
            throw new IllegalAccessException("When the store is closed only role holders can get products");

        return store.toString();

    }

    @Override
    public String getProductInfo(String userName, String storeName, int productId) throws IllegalAccessException {     //Change to Reop
        validateUserAndStore(userName, storeName);
        Store store = storeRepository.getStore(storeName);
        if (!store.isOpen() && !(store.isRoleHolder(userName) || userFacade.isAdmin(userName)))
            throw new IllegalAccessException("When the store is closed only role holder can get product info");
        if (store.getProduct(productId) == null) {
            throw new RuntimeException("Can't find product with id " + productId);
        }
        return store.getProduct(productId).toString();
    }

    @Override
    public String getPurchaseHistoryJSONFormatForStore(String userName,String storeName){
        validateUserAndStore(userName,storeName);
        return storeRepository.getStore(storeName).getPurchaseHistoryJSONFormat();
    }

    @Override
    public String getPurchaseHistoryJSONFormat(String userName) throws IllegalAccessException {
        if(!userFacade.isUserExist(userName)){
            throw new IllegalAccessException("Username is not exist");
        }
        List<Map<String, Object>> allStoresPurchases = new ArrayList<>();
        for(Store store:storeRepository.getAllStoresByStores()){
            Map<String, Object> storePurchaseMap = Map.of(
                    "purchaseHistory", getPurchaseHistoryJSONFormatForStore(userName,store.getNameId())
            );
            allStoresPurchases.add(storePurchaseMap);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(allStoresPurchases);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Error converting purchase history to JSON";
        }

    }


    @Override
    public String searchProductsInStores(String userName, String keyWord, double minPrice, double maxPrice, List<Integer> intCategories, Double rating) throws JsonProcessingException {
        if (!userFacade.isUserExist(userName)) {
            throw new IllegalArgumentException("User must exist");
        }

        List<Product> resultProductList = new LinkedList<>();

        StringBuilder sb = new StringBuilder();
        for (Store store : storeRepository.getAllStoresByStores()) {
            if (!store.isOpen()) continue;
            List<Product> products2 = store.searchProduct(keyWord, minPrice, maxPrice, intCategories, rating);
            if (!products2.isEmpty())//Change to Repo
            {
                sb.append(products2.toString());

                resultProductList.addAll(products2);
            }

        }
        if (sb.isEmpty()) return "{}";

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(resultProductList);


    }


    @Override
    public String searchNameInStore(String userName, String productName, String storeName, Double minPrice, Double maxPrice, Double minRating, int category) throws IllegalAccessException {
        if (productName == null) {
            logger.error("No name provided");
            throw new IllegalArgumentException("No name provided");
        }
        if (storeName == null) {
            logger.error("No store name provided");
            throw new IllegalArgumentException("No store name provided");
        }
        validateUserAndStore(userName, storeName);
        Store store = storeRepository.getStore(storeName);
        if (!store.isOpen()) {
            logger.error("Store is not open, can't search");
            throw new IllegalArgumentException("Store is not open, can't search");
        }
        if (store.getProducts().isEmpty()) {    //Change To Repo
            logger.warn("No products Available");
            return "{}";
        }
        return store.searchName(productName, minPrice, maxPrice, minRating, category).toString();
    }

    @Override
    public String searchCategoryInStore(String userName, int category, String storeName, Double minPrice, Double maxPrice, Double minRating) {
        if (category < 0) {                //TODO fix according to the logics of this function
            logger.error("No category provided");
            throw new IllegalArgumentException("No category provided");
        }
        if (storeName == null) {
            logger.error("No store name provided");
            throw new IllegalArgumentException("No store name provided");
        }
        validateUserAndStore(userName, storeName);
        Store store = storeRepository.getStore(storeName);
        if (!store.isOpen()) {
            logger.error("Store is not open, can't search");
            throw new IllegalArgumentException("Store is not open, can't search");
        }
        if (store.getProducts().isEmpty()) {   //Change to Repo
            logger.warn("No products Available");
            return "{}";
        }
        if (!EnumSet.allOf(Category.class).contains(Category.values()[category])) {
            logger.error("Category is not a valid category");
            throw new RuntimeException("Category is not a valid category");
        }
        return store.searchCategory(category, minPrice, maxPrice, minRating).toString();
    }

    @Override
    public String searchKeywordsInStore(String userName, String keyWords, String storeName, Double minPrice, Double maxPrice, Double minRating, int category) {
        if (keyWords == null) {
            logger.error("No keywords provided");
            throw new IllegalArgumentException("No keywords provided");
        }
        if (storeName == null) {
            logger.error("No store name provided");
            throw new IllegalArgumentException("No store name provided");
        }
        validateUserAndStore(userName, storeName);
        Store store = storeRepository.getStore(storeName);
        if (!store.isOpen()) {
            logger.error("Store is not open, can't search");
            throw new IllegalArgumentException("Store is not open, can't search");
        }
        if (store.getProducts().isEmpty()) {    //Change to Repo
            logger.warn("No products Available");
            return "{}";
        }
        return store.searchKeywords(keyWords, minPrice, maxPrice, minRating, category).toString();
    }

    @Override
    public String searchNameInStores(String userName, String productName, Double minPrice, Double maxPrice, Double minRating, int category, Double storeRating) {
        if (productName == null) {
            logger.error("No name provided");
            throw new IllegalArgumentException("No name provided");
        }
        if (!userFacade.getUsers().containsKey(userName)) {
            logger.error("User does not exist");
            throw new IllegalArgumentException("User does not exist");
        }
        if (userFacade.isSuspended(userName)) {
            throw new RuntimeException("User is suspended from the system");
        }
        StringBuilder sb = new StringBuilder();
        for (Store store : storeRepository.getAllStoresByStores()) {
            if (!store.isOpen()) continue;
            if (!store.searchName(productName, minPrice, maxPrice, minRating, category, storeRating).isEmpty())//Change to Repo
                sb.append(store.searchName(productName, minPrice, maxPrice, minRating, category, storeRating).toString());

        }
        if (sb.isEmpty()) return "{}";
        return sb.toString();
    }

    @Override
    public String searchCategoryInStores(String userName, int category, Double minPrice, Double maxPrice, Double minRating, Double storeRating) {
        if (category < 0) {        //TODO fix according to the logics of this function
            logger.error("No category provided");
            throw new IllegalArgumentException("No category provided");
        }
        if (!EnumSet.allOf(Category.class).contains(Category.values()[category])) {
            logger.error("Category is not a valid category");
            throw new RuntimeException("Category is not a valid category");
        }
        if (!userFacade.getUsers().containsKey(userName)) {
            logger.error("User does not exist");
            throw new IllegalArgumentException("User does not exist");
        }
        if (userFacade.isSuspended(userName)) {
            throw new RuntimeException("User is suspended from the system");
        }
        StringBuilder sb = new StringBuilder();
        for (Store store : storeRepository.getAllStoresByStores()) {    //Change to Repo
            if (!store.isOpen()) continue;
            if (!store.searchCategory(category, minPrice, maxPrice, minRating, storeRating).isEmpty())
                sb.append(store.searchCategory(category, minPrice, maxPrice, minRating, storeRating).toString());
        }
        if (sb.isEmpty()) return "{}";

        return sb.toString();
    }

    @Override
    public String searchKeywordsInStores(String userName, String keyWords, Double minPrice, Double maxPrice, Double minRating, int category, Double storeRating) {
        if (keyWords == null) {
            logger.error("No keywords provided");
            throw new IllegalArgumentException("No keywords provided");
        }
        if (!userFacade.getUsers().containsKey(userName)) {
            logger.error("User does not exist");
            throw new IllegalArgumentException("User does not exist");
        }
        if (userFacade.isSuspended(userName)) {
            throw new RuntimeException("User is suspended from the system");
        }
        StringBuilder sb = new StringBuilder();
        for (Store store : storeRepository.getAllStores().values()) {      //Change to Repo
            if (!store.isOpen()) continue;
            if (!store.searchKeywords(keyWords, minPrice, maxPrice, minRating, category, storeRating).isEmpty())
                sb.append(store.searchKeywords(keyWords, minPrice, maxPrice, minRating, category, storeRating).toString());
        }
        if (sb.isEmpty()) return "{}";
        return sb.toString();
    }

    @Override
    public void openStoreExist(String userName, String storeName) {
        validateUserAndStore(userName, storeName);
        Store store = storeRepository.getStore(storeName);
        if (!store.getFounder().equals(userName)) {
            throw new IllegalArgumentException("Only founder can open store exist");
        }
        if (store.isOpen()) {
            throw new IllegalArgumentException("Store is already active");
        }
        store.setOpen(true);
        for (String managerUserName : store.getManagers())
            userFacade.sendNotification(userName, managerUserName, "Store " + storeName + " has reopened");
        for (String ownerUserName : store.getOwners())
            userFacade.sendNotification(userName, ownerUserName, "Store " + storeName + " has reopened");
    }

    @Override
    public void closeStoreExist(String userName, String storeName) throws IllegalAccessException {
        validateUserAndStore(userName, storeName);
        Store store = storeRepository.getStore(storeName);    //Change to Repo
        if (!store.getFounder().equals(userName)) {
            throw new IllegalArgumentException("Only founder can close store exist");
        }
        if (!store.isOpen()) {
            throw new IllegalArgumentException("Store is not active");
        }
        store.setOpen(false);
        for (User user : userFacade.getUsers().values())
            user.getCart().removeShoppingBagFromCartByStore(storeName);
        for (String managerUserName : store.getManagers())
            userFacade.sendNotification(userName, managerUserName, storeName + " has closed");
        for (String ownerUserName : store.getOwners())
            userFacade.sendNotification(userName, ownerUserName, storeName + " has closed");
    }

    //Supply Management

    @Override
    public boolean addProduct(String username, int productId, String storeName, String productName, String productDescription, double productPrice, int productQuantity, double rating, int category, List<String> keyWords) throws IllegalAccessException {
        Lock lock = storeLocks.computeIfAbsent(storeName, k -> new ReentrantLock());
        lock.lock();
        try {
            validateUserAndStore(username, storeName);
            Store store = storeRepository.getStore(storeName);
            User user = userFacade.getUser(username);
            if (user.getRoleByStoreId(storeName) == null) {
                throw new RuntimeException("User with no permission for this store");
            }
            user.getRoleByStoreId(storeName).addProduct(username, productId, storeName, productName, productDescription, productPrice, productQuantity, rating, category, keyWords);
            store.addProduct(productId, productName, productDescription, productPrice, productQuantity, rating, category, keyWords);
            lock.unlock();
            storeLocks.remove(storeName, lock);
            return true;
        } catch (Exception e) {
            lock.unlock();
            storeLocks.remove(storeName, lock);
            throw e;
        }

    }

    @Override
    public boolean removeProduct(String username, String storeName, int productId) throws IllegalAccessException {
        Lock lock = storeLocks.computeIfAbsent(storeName, k -> new ReentrantLock());
        lock.lock();
        try {
            validateUserAndStore(username, storeName);
            Store store = storeRepository.getStore(storeName);
            User user = userFacade.getUser(username);
            if (user.getRoleByStoreId(storeName) == null) {
                throw new RuntimeException("User with no permission for this store");
            }
            user.getRoleByStoreId(storeName).removeProduct(username, storeName, productId);
            if (!storeRepository.getStore(storeName).getProducts().containsKey(productId)) {
                throw new IllegalArgumentException("Product must exist");
            }
            store.removeProduct(productId);
            lock.unlock();
            storeLocks.remove(storeName, lock);
            return true;
        } catch (Exception e) {
            lock.unlock();
            storeLocks.remove(storeName, lock);
            throw e;
        }
    }

    @Override
    public boolean setProductName(String username, String storeName, int productId, String productName) throws IllegalAccessException {
        Lock lock = storeLocks.computeIfAbsent(storeName, k -> new ReentrantLock());
        lock.lock();
        try {
            if (!storeRepository.isExist(storeName)) {
                throw new IllegalArgumentException("Store must exist");
            }
            Store store = storeRepository.getStore(storeName);
            if (!store.getProducts().containsKey(productId)) {
                throw new IllegalArgumentException("Product must exist");
            }
            if (!userFacade.isUserExist(username)) {
                throw new IllegalArgumentException("User must exist");
            }
            if (userFacade.isSuspended(username)) {
                throw new RuntimeException("User is suspended from the system");
            }
            User user = userFacade.getUser(username);
            if (user.getRoleByStoreId(storeName) == null) {
                throw new RuntimeException("User with no permission for this store");
            }
            user.getRoleByStoreId(storeName).setProduct_name(username, storeName, productId, productName);
            store.setProductName(productId, productName);
            lock.unlock();
            storeLocks.remove(storeName, lock);
            return true;
        } catch (Exception e) {
            lock.unlock();
            storeLocks.remove(storeName, lock);
            throw e;
        }
    }

    @Override
    public void editProduct(String username, String storeName, int productId, String productName, String productDescription, double productPrice, int productQuantity) throws Exception{
        if (!storeRepository.isExist(storeName)) {
            throw new IllegalArgumentException("Store must exist");
        }
        Store store = storeRepository.getStore(storeName);
        if (!store.getProducts().containsKey(productId)) {
            throw new IllegalArgumentException("Product must exist");
        }
        if (!userFacade.isUserExist(username)) {
            throw new IllegalArgumentException("User must exist");
        }
        if (userFacade.isSuspended(username)) {
            throw new RuntimeException("User is suspended from the system");
        }
        User user = userFacade.getUser(username);
        if (user.getRoleByStoreId(storeName) == null) {
            throw new RuntimeException("User with no permission for this store");
        }
        if (productPrice < 0) throw new IllegalArgumentException("Price can't be negative number");
        if (productQuantity <= 0) throw new IllegalArgumentException("Quantity must be natural number");

        store.editProduct(productId, productName, productDescription, productPrice, productQuantity);
    }

    @Override
    public boolean setProductDescription(String username, String storeName, int productId, String productDescription) throws IllegalAccessException {
        Lock lock = storeLocks.computeIfAbsent(storeName, k -> new ReentrantLock());
        lock.lock();
        try {
            if (!storeRepository.isExist(storeName)) {
                throw new IllegalArgumentException("Store must exist");
            }
            Store store = storeRepository.getStore(storeName);
            if (!store.getProducts().containsKey(productId)) {
                throw new IllegalArgumentException("Product must exist");
            }
            if (!userFacade.isUserExist(username)) {
                throw new IllegalArgumentException("User must exist");
            }
            if (userFacade.isSuspended(username)) {
                throw new RuntimeException("User is suspended from the system");
            }
            User user = userFacade.getUser(username);
            if (user.getRoleByStoreId(storeName) == null) {
                throw new RuntimeException("User with no permission for this store");
            }
            user.getRoleByStoreId(storeName).setProduct_description(username, storeName, productId, productDescription);
            store.setProductDescription(productId, productDescription);
            lock.unlock();
            storeLocks.remove(storeName, lock);
            return true;
        } catch (Exception e) {
            lock.unlock();
            storeLocks.remove(storeName, lock);
            throw e;
        }
    }

    @Override
    public boolean setProductPrice(String username, String storeName, int productId, double productPrice) throws IllegalAccessException {
        Lock lock = storeLocks.computeIfAbsent(storeName, k -> new ReentrantLock());
        lock.lock();
        try {
            if (!storeRepository.isExist(storeName)) {
                throw new IllegalArgumentException("Store must exist");
            }
            Store store = storeRepository.getStore(storeName);
            if (!store.getProducts().containsKey(productId)) {
                throw new IllegalArgumentException("Product must exist");
            }
            if (productPrice < 0) throw new IllegalArgumentException("Price can't be negative number");
            if (!userFacade.isUserExist(username)) {
                throw new IllegalArgumentException("User must exist");
            }
            if (userFacade.isSuspended(username)) {
                throw new RuntimeException("User is suspended from the system");
            }
            User user = userFacade.getUser(username);
            if (user.getRoleByStoreId(storeName) == null) {
                throw new RuntimeException("User with no permission for this store");
            }
            user.getRoleByStoreId(storeName).setProduct_price(username, storeName, productId, productPrice);
            store.setProductPrice(productId, productPrice);
            lock.unlock();
            storeLocks.remove(storeName, lock);
            return true;
        } catch (Exception e) {
            lock.unlock();
            storeLocks.remove(storeName, lock);
            throw e;
        }
    }

    @Override
    public boolean setProductQuantity(String username, String storeName, int productId, int productQuantity) throws IllegalAccessException {
        Lock lock = storeLocks.computeIfAbsent(storeName, k -> new ReentrantLock());
        lock.lock();
        try {
            if (!storeRepository.isExist(storeName)) {
                throw new IllegalArgumentException("Store must exist");
            }
            Store store = storeRepository.getStore(storeName);
            if (!store.getProducts().containsKey(productId)) {
                throw new IllegalArgumentException("Product must exist");
            }
            if (productQuantity <= 0) throw new IllegalArgumentException("Quantity must be natural number");
            if (!userFacade.isUserExist(username)) {
                throw new IllegalArgumentException("User must exist");
            }
            if (userFacade.isSuspended(username)) {
                throw new RuntimeException("User is suspended from the system");
            }
            User user = userFacade.getUser(username);
            if (user.getRoleByStoreId(storeName) == null) {
                throw new RuntimeException("User with no permission for this store");
            }
            user.getRoleByStoreId(storeName).setProduct_quantity(username, storeName, productId, productQuantity);
            store.setProductQuantity(productId, productQuantity);
            lock.unlock();
            storeLocks.remove(storeName, lock);
            return true;
        } catch (Exception e) {
            lock.unlock();
            storeLocks.remove(storeName, lock);
            throw e;
        }
    }

    @Override
    public boolean setRating(String username, String storeName, int productId, double rating) throws IllegalAccessException {
        Lock lock = storeLocks.computeIfAbsent(storeName, k -> new ReentrantLock());
        lock.lock();
        try {
            if (!storeRepository.isExist(storeName)) {
                throw new IllegalArgumentException("Store must exist");
            }
            Store store = storeRepository.getStore(storeName);
            if (!store.getProducts().containsKey(productId)) {
                throw new IllegalArgumentException("Product must exist");
            }
            if (rating < 0 || rating > 5) throw new IllegalArgumentException("Rating must be a number between 0 to 5");
            if (!userFacade.isUserExist(username)) {
                throw new IllegalArgumentException("User must exist");
            }
            if (userFacade.isSuspended(username)) {
                throw new RuntimeException("User is suspended from the system");
            }
            User user = userFacade.getUser(username);
            if (user.getRoleByStoreId(storeName) == null) {
                throw new RuntimeException("User with no permission for this store");
            }
            user.getRoleByStoreId(storeName).setRating(username, storeName, productId, rating);
            store.setRating(productId, rating);
            lock.unlock();
            storeLocks.remove(storeName, lock);
            return true;
        } catch (Exception e) {
            lock.unlock();
            storeLocks.remove(storeName, lock);
            throw e;
        }
    }


    @Override
    public boolean setCategory(String username, String storeName, int productId, int category) throws IllegalAccessException {
        Lock lock = storeLocks.computeIfAbsent(storeName, k -> new ReentrantLock());
        lock.lock();
        try {
            if (!storeRepository.isExist(storeName)) {
                throw new IllegalArgumentException("Store must exist");
            }
            Store store = storeRepository.getStore(storeName);
            if (!store.getProducts().containsKey(productId)) {
                throw new IllegalArgumentException("Product must exist");
            }
            if (!userFacade.isUserExist(username)) {
                throw new IllegalArgumentException("User must exist");
            }
            if (userFacade.isSuspended(username)) {
                throw new RuntimeException("User is suspended from the system");
            }
            User user = userFacade.getUser(username);
            if (user.getRoleByStoreId(storeName) == null) {
                throw new RuntimeException("User with no permission for this store");
            }
            user.getRoleByStoreId(storeName).setCategory(username, storeName, productId, category);
            store.setCategory(productId, category);
            lock.unlock();
            storeLocks.remove(storeName, lock);
            return true;
        } catch (Exception e) {
            lock.unlock();
            storeLocks.remove(storeName, lock);
            throw e;
        }
    }

    @Override
    public boolean addKeywordToProduct(String username, String storeName, int productId, String keyword) throws IllegalAccessException {
        Lock lock = storeLocks.computeIfAbsent(storeName, k -> new ReentrantLock());
        lock.lock();
        try {
            if (!storeRepository.isExist(storeName)) {
                throw new IllegalArgumentException("Store must exist");
            }
            Store store = storeRepository.getStore(storeName);
            if (!store.getProducts().containsKey(productId)) {
                throw new IllegalArgumentException("Product must exist");
            }
            if (!userFacade.isUserExist(username)) {
                throw new IllegalArgumentException("User must exist");
            }
            if (userFacade.isSuspended(username)) {
                throw new RuntimeException("User is suspended from the system");
            }
            User user = userFacade.getUser(username);
            if (user.getRoleByStoreId(storeName) == null) {
                throw new RuntimeException("User with no permission for this store");
            }
            user.getRoleByStoreId(storeName).addKeywordToProduct(username, storeName, productId, keyword);
            store.addKeyWordToProduct(productId, keyword);
            lock.unlock();
            storeLocks.remove(storeName, lock);
            return true;
            } catch (Exception e) {
              lock.unlock();
              storeLocks.remove(storeName, lock);
              throw e;
          }
       }


    @Override
    public boolean removeKeywordToProduct(String username, String storeName, int productId,String keyword) throws IllegalAccessException {
        Lock lock = storeLocks.computeIfAbsent(storeName, k -> new ReentrantLock());
        lock.lock();
        try {
            if (!storeRepository.isExist(storeName)) {
                throw new IllegalArgumentException("Store must exist");
            }
            Store store = storeRepository.getStore(storeName);
            if (!store.getProducts().containsKey(productId)) {
                throw new IllegalArgumentException("Product must exist");
            }
            if (!userFacade.isUserExist(username)) {
                throw new IllegalArgumentException("User must exist");
            }
            if (userFacade.isSuspended(username)) {
                throw new RuntimeException("User is suspended from the system");
            }
            User user = userFacade.getUser(username);
            if (user.getRoleByStoreId(storeName) == null) {
                throw new RuntimeException("User with no permission for this store");
            }
            user.getRoleByStoreId(storeName).removeKeywordFromProduct(username,storeName,productId,keyword);
            store.removeKeyWordFromProduct(productId,keyword);
            lock.unlock();
            storeLocks.remove(storeName, lock);
            return true;
        } catch (Exception e) {
            lock.unlock();
            storeLocks.remove(storeName, lock);
            throw e;
        }
    }

    @Override
    public String getHistoryPurchasesByCustomer(String userName, String storeName, String customerUserName) throws IllegalAccessException {
        if (!storeRepository.isExist(storeName)) {
            throw new IllegalArgumentException("Store must exist");
        }
        Store store = storeRepository.getStore(storeName);
        if (!userFacade.isUserExist(userName)) {
            throw new IllegalArgumentException("User must exist");
        }
        if (userFacade.isSuspended(userName)) {
            throw new RuntimeException("User is suspended from the system");
        }
        if (!userFacade.isUserExist(customerUserName)) {
            throw new IllegalArgumentException("Customer must exist");
        }
        User user = userFacade.getUser(userName);
        User customer = userFacade.getUsers().get(customerUserName);
        if (user.isAdmin())
            return store.getHistoryPurchasesByCustomer(customer.getUsername()).stream().map(Purchase::toString).collect(Collectors.joining("\n\n"));

        user.getRoleByStoreId(storeName).getRoleState().getHistoryPurchasesByCustomer();
        return store.getHistoryPurchasesByCustomer(customer.getUsername()).stream().map(Purchase::toString).collect(Collectors.joining("\n\n"));
    }

    @Override
    public String getAllHistoryPurchases(String userName, String storeName) throws IllegalAccessException {
        if (!storeRepository.isExist(storeName)) {
            throw new IllegalArgumentException("Store must exist");
        }
        if (!userFacade.getUsers().containsKey(userName)) {
            throw new IllegalArgumentException("User must exist");
        }
        if (userFacade.isSuspended(userName)) {
            throw new RuntimeException("User is suspended from the system");
        }
        Store store = storeRepository.getStore(storeName);
        User user = userFacade.getUsers().get(userName);
        if (user.isAdmin())
            return store.getAllHistoryPurchases().stream().map(Purchase::toString).collect(Collectors.joining("\n\n"));
        if (user.getRoleByStoreId(storeName) == null) throw new RuntimeException("Not allowed to view store history");
        user.getRoleByStoreId(storeName).getRoleState().getAllHistoryPurchases();
        return store.getAllHistoryPurchases().stream().map(Purchase::toString).collect(Collectors.joining("\n\n"));
    }

    @Override
    public String requestInformationAboutOfficialsInStore(String userName, String storeName) throws IllegalArgumentException, IllegalAccessException, JsonProcessingException {
        validateUserAndStore(userName, storeName);
        User user = userFacade.getUser(userName);
        Store store = storeRepository.getStore(storeName);
        user.getRoleByStoreId(storeName).getRoleState().requestInformationAboutOfficialsInStore();

        List<String> storeOwners = store.getOwners();
        List<String> storeManagers = store.getManagers();

        StringBuilder result = new StringBuilder();
        result.append(storeName).append("\n");
        result.append("Role id username address birthdate").append("\n");
        for (String owner : storeOwners) {
            User user2 = userFacade.getUser(owner);
            if (store.getFounder().equals(owner))
                result.append("Founder ").append(user2.getUsername()).append(" ").append(owner).append(" ").append(user2.getAddress()).append(" ").append(user2.getBirthdate()).append("\n");
            else
                result.append("Owner ").append(user2.getUsername()).append(" ").append(owner).append(" ").append(user2.getAddress()).append(" ").append(user2.getBirthdate()).append("\n");
        }
        for (String manager : storeManagers) {
            User user2 = userFacade.getUser(manager);
            result.append("Manager ").append(user2.getUsername()).append(" ").append(manager).append(" ").append(user2.getAddress()).append(" ").append(user2.getBirthdate()).append("\n");
        }

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(result);

    }

    /**
     * @param userName
     * @param storeName
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    @Override
    public String requestManagersPermissions(String userName, String storeName) throws IllegalAccessException {
        validateUserAndStore(userName, storeName);
        User user = userFacade.getUser(userName);
        Store store = storeRepository.getStore(storeName);

        user.getRoleByStoreId(storeName).getRoleState().requestManagersPermissions();

        List<String> storeManagers = store.getManagers();
        StringBuilder result = new StringBuilder();
        result.append(storeName).append("\n\n");
        result.append("Managers :").append("\n");
        result.append("id username watch editSupply editPurchasePolicy editDiscountPolicy").append("\n");
        for (String manager : storeManagers) {
            User user2 = userFacade.getUser(manager);
            RoleState managerRole = user2.getRoleByStoreId(storeName).getRoleState();
            result.append(user2.getUsername()).append(" ").append(manager).append(" ").append(managerRole.isWatch()).append(" ").append(managerRole.isEditSupply()).append(" ").append(managerRole.isEditPurchasePolicy()).append(" ").append(managerRole.isEditDiscountPolicy()).append(" ").append(managerRole.isAcceptBids()).append(" ").append(managerRole.isCreateLottery()).append('\n');
        }
        return result.toString();
    }

    /**
     * @param userName
     * @param storeName
     * @param officialUserName
     * @return
     * @throws IllegalArgumentException
     */
    @Override
    public String requestInformationAboutSpecificOfficialInStore(String userName, String storeName, String officialUserName) throws IllegalAccessException {
        validateUserAndStore(userName, storeName);
        if (!userFacade.isUserExist(officialUserName)) {
            throw new IllegalArgumentException("User must exist");
        }

        User user = userFacade.getUser(userName);
        user.getRoleByStoreId(storeName).getRoleState().requestInformationAboutSpecificOfficialInStore();


        Store store = storeRepository.getStore(storeName);
        List<String> storeOwners = store.getOwners();
        List<String> storeManagers = store.getManagers();

        StringBuilder result = new StringBuilder();
        result.append(storeName).append('\n');

        if (storeOwners.contains(officialUserName)) {
            User user2 = userFacade.getUser(officialUserName);
            result.append("Role id username address birthdate").append('\n');
            if (store.getFounder().equals(officialUserName))
                result.append("Founder ").append(user2.getUsername()).append(" ").append(officialUserName).append(" ").append(user2.getAddress()).append(" ").append(user2.getBirthdate()).append('\n');
            else
                result.append("Owner ").append(user2.getUsername()).append(" ").append(officialUserName).append(" ").append(user2.getAddress()).append(" ").append(user2.getBirthdate()).append('\n');
        } else if (storeManagers.contains(officialUserName)) {
            User user2 = userFacade.getUser(officialUserName);
            RoleState managerRole = user2.getRoleByStoreId(storeName).getRoleState();
            result.append("Role id username address birthdate watch editSupply editPurchasePolicy editDiscountPolicy").append("\n");
            result.append("Manager ").append(user2.getUsername()).append(" ").append(officialUserName).append(" ").append(user2.getAddress()).append(" ").append(user2.getBirthdate()).append(" ").append(managerRole.isWatch()).append(" ").append(managerRole.isEditSupply()).append(" ").append(managerRole.isEditPurchasePolicy()).append(" ").append(managerRole.isEditDiscountPolicy()).append(" ").append(managerRole.isAcceptBids()).append(" ").append(managerRole.isCreateLottery()).append("\n");
        } else throw new IllegalArgumentException("User is not employed in this store.");

        return result.toString();
    }


    @Override
    public HashMap<String, Store> getStores() {
        return storeRepository.getAllStores();
    }

    @Override
    public Store getStore(String storeName) {
        return storeRepository.getStore(storeName);
    }

    @Override
    public void releaseReservedProducts(int productId, int quantity, String storeName) {
        getStore(storeName).releaseReservedProducts(productId, quantity);

    }

    @Override
    public void removeReservedProducts(int productId, int quantity, String storeName) {
        getStore(storeName).removeReservedProducts(productId, quantity);

    }

    @Override
    public double calculateTotalPrice(String cartJSON) throws IOException {
        CartDTO cart = CartDTO.fromJson(cartJSON);
        double price = 0;
        for (ShoppingBagDTO bag : cart.getShoppingBags().values()) {
            price += storeRepository.getStore(bag.getStoreId()).calculatePrice(bag.getProducts_list().values());
        }
        return price;
    }

    @Override
    public boolean validatePurchasePolicies(String cartJSON, int age) throws IOException {
        CartDTO cart = CartDTO.fromJson(cartJSON);
        for (ShoppingBagDTO bag : cart.getShoppingBags().values()) {
            Store store = storeRepository.getStore(bag.getStoreId());
            if (storeRepository == null || store == null || !store.validatePurchasePolicies(bag.getProducts_list().values(), age)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void addPurchase(String customerUsername, String productInSaleList, double totalPrice, String storeName) throws IOException {
        Store store = storeRepository.getStore(storeName);
        store.addPurchase(new Purchase(customerUsername, ProductInSaleDTO.fromJsonList(productInSaleList), totalPrice, storeName));
        userFacade.sendNotification(customerUsername, store.getFounder(), "User: " + customerUsername + " bought the following items from your store: " + storeName + " " + productInSaleList);
    }

    //region Discount management
    @Override
    public String getDiscountPolicies(String username, String storeName) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        return storeRepository.getStore(storeName).getDiscountPoliciesInfo();
    }

    @Override
    public String getDiscountConditions(String username, String storeName) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        return storeRepository.getStore(storeName).getConditionInfo();
    }

    @Override
    public void addCategoryPercentageDiscount(String username, String storeName, int category, double discountPercent) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeRepository.getStore(storeName).addCategoryPercentageDiscount(category, discountPercent);
    }

    @Override
    public void addProductPercentageDiscount(String username, String storeName, int productId, double discountPercent) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeRepository.getStore(storeName).addProductPercentageDiscount(productId, discountPercent);
    }

    @Override
    public void addStoreDiscount(String username, String storeName, double discountPercent) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeRepository.getStore(storeName).addStoreDiscount(discountPercent);
    }

    @Override
    public void addConditionalDiscount(String username, String storeName) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeRepository.getStore(storeName).addConditionalDiscount();
    }

    @Override
    public void addAdditiveDiscount(String username, String storeName) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeRepository.getStore(storeName).addAdditiveDiscount();
    }

    @Override
    public void addMaxDiscount(String username, String storeName) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeRepository.getStore(storeName).addMaxDiscount();
    }

    @Override
    public void addCategoryCountCondition(String username, String storeName, int category, int count) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeRepository.getStore(storeName).addCategoryCountCondition(category, count);
    }

    @Override
    public void addTotalSumCondition(String username, String storeName, double requiredSum) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeRepository.getStore(storeName).addTotalSumCondition(requiredSum);
    }

    @Override
    public void addProductCountCondition(String username, String storeName, int productId, int count) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeRepository.getStore(storeName).addProductCountCondition(productId, count);
    }

    @Override
    public void addAndDiscount(String username, String storeName) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeRepository.getStore(storeName).addAndDiscount();
    }

    @Override
    public void addOrDiscount(String username, String storeName) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeRepository.getStore(storeName).addOrDiscount();
    }

    @Override
    public void addXorDiscount(String username, String storeName) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeRepository.getStore(storeName).addXorDiscount();
    }

    @Override
    public void removeDiscount(String username, String storeName, int selectedIndex) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeRepository.getStore(storeName).removeDiscount(selectedIndex);
    }

    @Override
    public void setFirstDiscount(String username, String storeName, int selectedDiscountIndex, int selectedFirstIndex) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeRepository.getStore(storeName).setFirstDiscount(selectedDiscountIndex, selectedFirstIndex);
    }

    @Override
    public void setSecondDiscount(String username, String storeName, int selectedDiscountIndex, int selectedSecondIndex) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeRepository.getStore(storeName).setSecondDiscount(selectedDiscountIndex, selectedSecondIndex);
    }

    @Override
    public void setFirstCondition(String username, String storeName, int selectedDiscountIndex, int selectedSecondIndex) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeRepository.getStore(storeName).setFirstCondition(selectedDiscountIndex, selectedSecondIndex);
    }

    @Override
    public void setSecondCondition(String username, String storeName, int selectedDiscountIndex, int selectedSecondIndex) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeRepository.getStore(storeName).setSecondCondition(selectedDiscountIndex, selectedSecondIndex);
    }

    @Override
    public void setThenDiscount(String username, String storeName, int selectedDiscountIndex, int selectedThenIndex) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeRepository.getStore(storeName).setThenDiscount(selectedDiscountIndex, selectedThenIndex);
    }

    @Override
    public void setCategoryDiscount(String username, String storeName, int selectedDiscountIndex, int category) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeRepository.getStore(storeName).setCategoryDiscount(selectedDiscountIndex, category);
    }

    @Override
    public void setProductIdDiscount(String username, String storeName, int selectedDiscountIndex, int productId) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeRepository.getStore(storeName).setProductIdDiscount(selectedDiscountIndex, productId);
    }

    @Override
    public void setPercentDiscount(String username, String storeName, int selectedDiscountIndex, double discountPercent) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeRepository.getStore(storeName).setPercentDiscount(selectedDiscountIndex, discountPercent);
    }

    @Override
    public void setDeciderDiscount(String username, String storeName, int selectedDiscountIndex, int selectedDeciderIndex) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeRepository.getStore(storeName).setDeciderDiscount(selectedDiscountIndex, selectedDeciderIndex);
    }

    @Override
    public void setTotalSum(String username, String storeName, int selectedConditionIndex, double newSum) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeRepository.getStore(storeName).setTotalSum(selectedConditionIndex, newSum);
    }

    @Override
    public void setCountCondition(String username, String storeName, int selectedConditionIndex, int newCount) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeRepository.getStore(storeName).setCountCondition(selectedConditionIndex, newCount);
    }

    @Override
    public void setCategoryCondition(String username, String storeName, int selectedConditionIndex, int newCategory) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeRepository.getStore(storeName).setCategoryCondition(selectedConditionIndex, newCategory);
    }

    @Override
    public void setProductIdCondition(String username, String storeName, int selectedConditionIndex, int newId) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeRepository.getStore(storeName).setProductIdCondition(selectedConditionIndex, newId);
    }

    @Override
    public void removeCondition(String username, String storeName, int selectedIndex) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeRepository.getStore(storeName).removeCondition(selectedIndex);
    }
    //endregion
    //region Purchase Policy Management
    @Override
    public String getPurchasePoliciesInfo(String username, String storeName) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        return storeRepository.getStore(storeName).getPurchasePoliciesInfo();
    }

    @Override
    public void addPurchasePolicyByAge(String username, String storeName, int ageToCheck, int category) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeRepository.getStore(storeName).addPurchasePolicyByAge(ageToCheck, category);
    }

    @Override
    public void addPurchasePolicyByCategoryAndDate(String username, String storeName, int category, LocalDateTime dateTime) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeRepository.getStore(storeName).addPurchasePolicyByCategoryAndDate(category, dateTime);
    }

    @Override
    public void addPurchasePolicyByDate(String username, String storeName, LocalDateTime dateTime) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeRepository.getStore(storeName).addPurchasePolicyByDate(dateTime);
    }

    @Override
    public void addPurchasePolicyByProductAndDate(String username, String storeName, int productId, LocalDateTime dateTime) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeRepository.getStore(storeName).addPurchasePolicyByProductAndDate(productId, dateTime);
    }

    @Override
    public void addPurchasePolicyByShoppingCartMaxProductsUnit(String username, String storeName, int productId, int numOfQuantity) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeRepository.getStore(storeName).addPurchasePolicyByShoppingCartMaxProductsUnit(productId, numOfQuantity);
    }

    @Override
    public void addPurchasePolicyByShoppingCartMinProducts(String username, String storeName, int numOfQuantity) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeRepository.getStore(storeName).addPurchasePolicyByShoppingCartMinProducts(numOfQuantity);
    }

    @Override
    public void addPurchasePolicyByShoppingCartMinProductsUnit(String username, String storeName, int productId, int numOfQuantity) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeRepository.getStore(storeName).addPurchasePolicyByShoppingCartMinProductsUnit(productId, numOfQuantity);
    }

    @Override
    public void addAndPurchasePolicy(String username, String storeName) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeRepository.getStore(storeName).addAndPurchasePolicy();
    }

    @Override
    public void addOrPurchasePolicy(String username, String storeName) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeRepository.getStore(storeName).addOrPurchasePolicy();
    }

    @Override
    public void addConditioningPurchasePolicy(String username, String storeName) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeRepository.getStore(storeName).addConditioningPurchasePolicy();
    }

    @Override
    public void setPurchasePolicyProductId(String username, String storeName, int selectedIndex, int productId) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeRepository.getStore(storeName).setPurchasePolicyProductId(selectedIndex, productId);
    }

    @Override
    public void setPurchasePolicyNumOfQuantity(String username, String storeName, int selectedIndex, int numOfQuantity) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeRepository.getStore(storeName).setPurchasePolicyNumOfQuantity(selectedIndex, numOfQuantity);
    }

    @Override
    public void setPurchasePolicyDateTime(String username, String storeName, int selectedIndex, LocalDateTime dateTime) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeRepository.getStore(storeName).setPurchasePolicyDateTime(selectedIndex, dateTime);
    }

    @Override
    public void setPurchasePolicyAge(String username, String storeName, int selectedIndex, int age) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeRepository.getStore(storeName).setPurchasePolicyAge(selectedIndex, age);
    }

    @Override
    public void setFirstPurchasePolicy(String username, String storeName, int selectedDiscountIndex, int selectedFirstIndex) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeRepository.getStore(storeName).setFirstPurchasePolicy(selectedDiscountIndex, selectedFirstIndex);
    }

    @Override
    public void setSecondPurchasePolicy(String username, String storeName, int selectedDiscountIndex, int selectedSecondIndex) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeRepository.getStore(storeName).setSecondPurchasePolicy(selectedDiscountIndex, selectedSecondIndex);
    }

    @Override
    public void removePurchasePolicy(String username, String storeName, int selectedIndex) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeRepository.getStore(storeName).removePurchasePolicy(selectedIndex);
    }

    public void checkAvailabilityAndConditions(int id, int quantity, String storeId) {
        if (getStore(storeId) == null) throw new RuntimeException("store not exist");
        getStore(storeId).checkAvailabilityAndConditions(id, quantity);
    }
//endregion

//    @Override
//    public void sendMessageUserToStore(String sender, String storeName, String content) {
//        if (!userFacade.isUserExist(sender))
//            throw new RuntimeException("Message sender user must exist");
//        if (!isStoreExist(storeName))
//            throw new RuntimeException("Message receiver store must exist");
//        if (content.isEmpty())
//            throw new RuntimeException("Message content cannot be empty");
//        Store store = storeRepository.getStore(storeName);
//        String username = "";
//        if (sender.charAt(0) == 'v')
//            username = "visitor " + sender;
//        else if (sender.charAt(0) == 'r')
//            username = sender.substring(1);
//        store.receiveMessage(sender, username, content);
//        if (sender.charAt(0) == 'r')
//            userFacade.sendNotificationToStoreOwners(sender, store.getOwners(), "Store: " + storeName + " received a message from user: " + sender);
//        else
//            userFacade.sendNotificationToStoreOwners(sender, store.getOwners(), "Store: " + storeName + " received a message from a visitor");
//    }

//    @Override
//    public void sendMessageStoreToUser(String owner, String receiver, String storeName, String content) {
//        if (!userFacade.isUserExist(receiver)) {
//            if (receiver.charAt(0) == 'r')
//                throw new RuntimeException("Message receiver user must exist");
//            else if (receiver.charAt(0) == 'v')
//                throw new RuntimeException("Visitor no longer exists, no need to reply");
//        }
//        if (!userFacade.isUserExist(owner)) {
//            throw new RuntimeException("Message sender user must exist");
//        }
//        if (!isStoreExist(storeName))
//            throw new RuntimeException("Message sender store must exist");
//        if (content.isEmpty())
//            throw new RuntimeException("Message content cannot be empty");
//        User receiverUser = userFacade.getUser(receiver);
//        User ownerUser = userFacade.getUser(owner);
//        if (!ownerUser.isOwner(storeName)){
//            throw new RuntimeException("Message sender must be an owner of the store");
//        }
//        receiverUser.receiveMessage(storeName, storeName, content);
//        userFacade.sendNotification(owner, receiver, "Owner: " + ownerUser.getUsername() + " from store: " + storeName + " has replied to your message");
//    }

//    @Override
//    public String  getStoreMessagesJson(String admin, String storeName){
//        if (!userFacade.isUserExist(admin)) {
//            throw new IllegalArgumentException("Admin user doesn't exist in the system");
//        }
//        if (!storeRepository.isExist(storeName)) {
//            throw new IllegalArgumentException("Store doesn't exist in the system");
//        }
//        if (!userFacade.isAdmin(admin)) {
//            throw new IllegalArgumentException("Only admin user can get user notifications");
//        }
//        Store store = storeRepository.getStore(storeName);
//        return store.getMessagesJSON();
//    }

    @Override
    public void placeBid(String userName, String storeName, int productID, double price) throws IllegalArgumentException {
        validateUserAndStore(userName, storeName);
        Store store = storeRepository.getStore(storeName);
        if(!store.getProducts().containsKey(productID))
            throw new IllegalArgumentException("Product must exist");
        store.placeBid(userName, productID, price);

        for (String owner : store.getOwners())
            userFacade.sendNotification(userName, owner, userName + " is placed a bid for product " + productID + " in store " + storeName + " with price " + price);
        for (String manager : store.getManagers())
            userFacade.sendNotification(userName, manager, userName + " is placed a bid for product " + productID + " in store " + storeName + " with price " + price);
    }

    @Override
    public void approveBid(String userName, String storeName, int productID, String bidUserName) throws Exception {
        validateUserAndStore(userName, storeName);
        Store store = storeRepository.getStore(storeName);
        if(!store.getProducts().containsKey(productID))
            throw new IllegalArgumentException("Product must exist");
        if(!store.isBidExist(productID, bidUserName))
            throw new IllegalArgumentException("Bid must exist");
        User user = userFacade.getUser(userName);
        user.getRoleByStoreId(storeName).approveBid();

        boolean allOwnersApproved = store.approveBid(userName, productID, bidUserName);
        if(allOwnersApproved)
        {
            userFacade.sendNotification(userName, bidUserName, "Your bid on product " + store.getProducts().get(productID).getProduct_name() + " in store " + storeName + " is approved");
            userFacade.bidPurchase(bidUserName, storeName, productID, store.getBidPrice(bidUserName, productID));
            store.removeBidAccepted(bidUserName, productID);
        }

    }

    @Override
    public void rejectBid(String userName, String storeName, int productID, String bidUserName) throws IllegalArgumentException, IllegalAccessException{
        validateUserAndStore(userName, storeName);
        Store store = storeRepository.getStore(storeName);
        if(!store.getProducts().containsKey(productID))
            throw new IllegalArgumentException("Product must exist");
        if(!store.isBidExist(productID, bidUserName))
            throw new IllegalArgumentException("Bid must exist");
        User user = userFacade.getUser(userName);
        user.getRoleByStoreId(storeName).rejectBid();

        store.rejectBid(userName, productID, bidUserName);

        userFacade.sendNotification(userName, bidUserName, "Your bid on product " + store.getProducts().get(productID).getProduct_name() + " in store " + storeName + " is rejected");
    }

    @Override
    public void placeCounterOffer(String userName, String storeName, int productID, String bidUserName, double newPrice) throws IllegalArgumentException, IllegalAccessException{
        validateUserAndStore(userName, storeName);
        Store store = storeRepository.getStore(storeName);
        if(!store.getProducts().containsKey(productID))
            throw new IllegalArgumentException("Product must exist");
        if(!store.isBidExist(productID, bidUserName))
            throw new IllegalArgumentException("Bid must exist");
        User user = userFacade.getUser(userName);
        user.getRoleByStoreId(storeName).placeCounterOffer();

        store.counterOffer(userName,productID,bidUserName,newPrice);

        userFacade.sendNotification(userName, bidUserName, "Your bid on product " + store.getProducts().get(productID).getProduct_name() + " in store " + storeName + " is got counter offer by " + userName + " of " + newPrice);
    }

    @Override
    public String getStoreBids(String userName, String storeName) throws IllegalArgumentException, IllegalAccessException {
        validateUserAndStore(userName, storeName);
        User user = userFacade.getUser(userName);
        user.getRoleByStoreId(storeName).getStoreBids();
        Store store = storeRepository.getStore(storeName);
        return store.getStoreBids();
    }

    @Override
    public String getMyBids(String userName, String storeName) throws IllegalArgumentException, IllegalAccessException {
        validateUserAndStore(userName, storeName);
        Store store = storeRepository.getStore(storeName);
        return store.getMyBids(userName);

    }

//    @Override
//    public void createProductLottery(String userName, String storeName, int productID, LocalDateTime localDateTime, double price) throws Exception{
//        validateUserAndStore(userName, storeName);
//        Store store = storeRepository.getStore(storeName);
//        if(!store.isProductExist(productID))
//            throw new IllegalArgumentException("Product must exist in store");
//        if(localDateTime.isBefore(LocalDateTime.now()) || localDateTime.isEqual(LocalDateTime.now()))
//            throw new Exception("Cant create lottery for past or present");
//        User user = userFacade.getUser(userName);
//        user.getRoleByStoreId(storeName).createProductLottery();
//      //  store.createProductLottery(productID,localDateTime,price);
//    }
//
//    @Override
//    public String buyLotteryProductTicket(String userName, String storeName, int productID, double price) throws Exception{
//        validateUserAndStore(userName, storeName);
//        Store store = storeRepository.getStore(storeName);
//        if(!store.isLotteryExist(productID))
//            throw new Exception("Lottery does not exist");
//        if(store.buyLotteryProductTicket(userName, productID, price)){
//            return store.makeLotteryOnProduct(productID) + " won the product " + productID;
//        }
//        else
//            return "Ticket Bought Successfully";
//
//    }






}