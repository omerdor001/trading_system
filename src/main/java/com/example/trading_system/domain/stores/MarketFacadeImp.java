package com.example.trading_system.domain.stores;

import com.example.trading_system.domain.users.*;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class MarketFacadeImp implements MarketFacade {
    private static final Logger logger = LoggerFactory.getLogger(MarketFacadeImp.class);
    private static MarketFacadeImp instance = null;
    private final ConcurrentHashMap<String, Lock> storeLocks = new ConcurrentHashMap<>();
    @Getter
    private StoreRepository storeMemoryRepository;
    private UserFacade userFacade;


    private MarketFacadeImp() {
        this.storeMemoryRepository = StoreMemoryRepository.getInstance();
    }

    public static MarketFacadeImp getInstance() {
        if (instance == null) instance = new MarketFacadeImp();
        return instance;
    }

    @Override
    public void initialize(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @Override
    public void deleteInstance() {
        if (instance != null) {
            instance.storeMemoryRepository.deleteInstance();
            instance.storeMemoryRepository = null;
            instance.userFacade = null;
            instance = null;
        }
    }


    public boolean isStoreExist(String store_name) {
        return storeMemoryRepository.isExist(store_name);
    }

    public void addStore(String storeName, String description, String founder, Double storeRating) {
        storeMemoryRepository.addStore(storeName, description, founder, storeRating);
    }

    public void deactivateStore(String storeName) {
        Store store = storeMemoryRepository.getStore(storeName);
        if (store != null) {
            if (store.isActive()) {
                store.setActive(false);
            } else {
                throw new RuntimeException("Can't deactivate store that already not active");
            }
        }
    }

    //For Tests
    public boolean isProductExist(String userName, int productId, String storeName) throws IllegalAccessException{
        if (!storeMemoryRepository.isExist(storeName)) {
            throw new IllegalArgumentException("Store must exist");
        }
        Store store = storeMemoryRepository.getStore(storeName);
        if(!store.isOpen() && ! (store.isRoleHolder(userName) || userFacade.isAdmin(userName)))
            throw new IllegalAccessException("When store is closed just role holders can check if product exist");


        return storeMemoryRepository.getStore(storeName).isProductExist(productId);
    }

    @Override
    public String getAllStores(String userName) {           //For UI ?
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("\"stores\":");
        if(userFacade.isSuspended(userName)){
            throw new RuntimeException("User is suspended from the system");
        }
        for (Store store : storeMemoryRepository.getAllStoresByStores()) {
            if(!store.isOpen() && !(userFacade.isAdmin(userName)||store.isRoleHolder(userName)))
                continue;
            sb.append(store.getNameId());
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String getStoreProducts(String userName, String storeName) throws IllegalAccessException {
        validateUserAndStore(userName,storeName);
        Store store = storeMemoryRepository.getStore(storeName);
        if(!store.isOpen() && !(store.isRoleHolder(userName)|| userFacade.isAdmin(userName)))
            throw new IllegalAccessException("When the store is closed only role holders can get products");

        return store.toString();

    }

    @Override
    public String getProductInfo(String userName, String storeName, int productId) throws IllegalAccessException{     //Change to Reop
        validateUserAndStore(userName,storeName);
        Store store = storeMemoryRepository.getStore(storeName);
        if(!store.isOpen() && ! (store.isRoleHolder(userName) || userFacade.isAdmin(userName)))
            throw new IllegalAccessException("When the store is closed only role holder can get product info");
        if (store.getProduct(productId) == null) {
            throw new RuntimeException("Can't find product with id " + productId);
        }
        return store.getProduct(productId).toString();

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
        validateUserAndStore(userName,storeName);
        Store store = storeMemoryRepository.getStore(storeName);
        if (!store.isOpen())
        {
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
    public String searchCategoryInStore(String userName, int category, String storeName, Double minPrice, Double maxPrice, Double minRating) throws IllegalAccessException {
        if (category < 0) {                //TODO fix according to the logics of this function
            logger.error("No category provided");
            throw new IllegalArgumentException("No category provided");
        }
        if (storeName == null) {
            logger.error("No store name provided");
            throw new IllegalArgumentException("No store name provided");
        }
        validateUserAndStore(userName,storeName);
        Store store = storeMemoryRepository.getStore(storeName);
        if (!store.isOpen())
        {
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
    public String searchKeywordsInStore(String userName, String keyWords, String storeName, Double minPrice, Double maxPrice, Double minRating, int category) throws IllegalAccessException {
        if (keyWords == null) {
            logger.error("No keywords provided");
            throw new IllegalArgumentException("No keywords provided");
        }
        if (storeName == null) {
            logger.error("No store name provided");
            throw new IllegalArgumentException("No store name provided");
        }
        validateUserAndStore(userName,storeName);
        Store store = storeMemoryRepository.getStore(storeName);
        if (!store.isOpen())
        {
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
        if(!userFacade.getUsers().containsKey(userName))
        {
            logger.error("User does not exist");
            throw new IllegalArgumentException("User does not exist");
        }
        if(userFacade.isSuspended(userName)){
            throw new RuntimeException("User is suspended from the system");
        }
        StringBuilder sb = new StringBuilder();
        for (Store store : storeMemoryRepository.getAllStoresByStores()) {
            if (!store.isOpen())
                continue;
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
        if(!userFacade.getUsers().containsKey(userName))
        {
            logger.error("User does not exist");
            throw new IllegalArgumentException("User does not exist");
        }
        if(userFacade.isSuspended(userName)){
            throw new RuntimeException("User is suspended from the system");
        }
        StringBuilder sb = new StringBuilder();
        for (Store store : storeMemoryRepository.getAllStoresByStores()) {    //Change to Repo
            if (!store.isOpen())
                continue;
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
        if(!userFacade.getUsers().containsKey(userName))
        {
            logger.error("User does not exist");
            throw new IllegalArgumentException("User does not exist");
        }
        if(userFacade.isSuspended(userName)){
            throw new RuntimeException("User is suspended from the system");
        }
        StringBuilder sb = new StringBuilder();
        for (Store store : storeMemoryRepository.getAllStores().values()) {      //Change to Repo
            if (!store.isOpen())
                continue;
            if (!store.searchKeywords(keyWords, minPrice, maxPrice, minRating, category, storeRating).isEmpty())
                sb.append(store.searchKeywords(keyWords, minPrice, maxPrice, minRating, category, storeRating).toString());
        }
        if (sb.isEmpty()) return "{}";
        return sb.toString();
    }

    @Override
    public void openStoreExist(String userName, String storeName) throws IllegalAccessException {
        validateUserAndStore(userName,storeName);
        Store store = storeMemoryRepository.getStore(storeName);
        if (!store.getFounder().equals(userName)) {
            throw new IllegalArgumentException("Only founder can open store exist");
        }
        if(store.isOpen()){
            throw new IllegalArgumentException("Store is already active");
        }
        store.setOpen(true);
        for (String managerUserName : store.getManagers())
            userFacade.getUser(managerUserName).receiveNotification(storeName + " has reopened.");
        for (String ownerUserName : store.getOwners())
            userFacade.getUser(ownerUserName).receiveNotification(storeName + " has reopened.");
    }

    @Override
    public void closeStoreExist(String userName, String storeName) throws IllegalAccessException {
        validateUserAndStore(userName,storeName);
        Store store = storeMemoryRepository.getStore(storeName);    //Change to Repo
        if (!store.getFounder().equals(userName)) {
            throw new IllegalArgumentException("Only founder can close store exist");
        }
        if(!store.isOpen()){
            throw new IllegalArgumentException("Store is not active");
        }
        store.setOpen(false);
        for (User user : userFacade.getUsers().values())
            user.getCart().removeShoppingBagFromCartByStore(storeName);
        for (String managerUserName : store.getManagers())
            userFacade.getUser(managerUserName).receiveNotification(storeName + " has been closed.");
        for (String ownerUserName : store.getOwners())
            userFacade.getUser(ownerUserName).receiveNotification(storeName + " has been closed.");
    }

    //Supply Management

    @Override
    public boolean addProduct(String username, int productId, String storeName, String productName, String productDescription, double productPrice, int productQuantity, double rating, int category, List<String> keyWords) throws IllegalAccessException {
        Lock lock = storeLocks.computeIfAbsent(storeName, k -> new ReentrantLock());
        lock.lock();
        try {
            validateUserAndStore(username,storeName);
            Store store = storeMemoryRepository.getStore(storeName);
            User user=userFacade.getUser(username);
            if(user.getRoleByStoreId(storeName)==null){
                throw new RuntimeException("User with no permission for this store");
            }
            user.getRoleByStoreId(storeName).addProduct(username, productId, storeName, productName, productDescription, productPrice, productQuantity,rating,category,keyWords);
            store.addProduct(productId, productName, productDescription, productPrice, productQuantity,rating,category,keyWords);
            lock.unlock();
            storeLocks.remove(storeName, lock);
            return true;
        }
        catch (Exception e){
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
            validateUserAndStore(username,storeName);
            Store store=storeMemoryRepository.getStore(storeName);
            User user=userFacade.getUser(username);
            if(user.getRoleByStoreId(storeName)==null){
                throw new RuntimeException("User with no permission for this store");
            }
            user.getRoleByStoreId(storeName).removeProduct(username, storeName, productId);
            if(!storeMemoryRepository.getStore(storeName).getProducts().containsKey(productId)) {
                throw new IllegalArgumentException("Product must exist");
            }
            store.removeProduct(productId);
            lock.unlock();
            storeLocks.remove(storeName, lock);
            return true;
        }
        catch (Exception e){
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
            if (!storeMemoryRepository.isExist(storeName)) {
                throw new IllegalArgumentException("Store must exist");
            }
            Store store = storeMemoryRepository.getStore(storeName);
            if (!store.getProducts().containsKey(productId)) {
                throw new IllegalArgumentException("Product must exist");
            }
            if (!userFacade.isUserExist(username)) {
                throw new IllegalArgumentException("User must exist");
            }
            if(userFacade.isSuspended(username)){
                throw new RuntimeException("User is suspended from the system");
            }
            User user=userFacade.getUser(username);
            if(user.getRoleByStoreId(storeName)==null){
                throw new RuntimeException("User with no permission for this store");
            }
            user.getRoleByStoreId(storeName).setProduct_name(username, storeName,productId, productName);
            store.setProductName(productId, productName);
            lock.unlock();
            storeLocks.remove(storeName, lock);
            return true;
        }
        catch (Exception e){
            lock.unlock();
            storeLocks.remove(storeName, lock);
            throw e;
        }
    }

    @Override
    public boolean setProductDescription(String username, String storeName, int productId, String productDescription) throws IllegalAccessException {
        Lock lock = storeLocks.computeIfAbsent(storeName, k -> new ReentrantLock());
        lock.lock();
        try {
            if (!storeMemoryRepository.isExist(storeName)) {
                throw new IllegalArgumentException("Store must exist");
            }
            Store store = storeMemoryRepository.getStore(storeName);
            if (!store.getProducts().containsKey(productId)) {
                throw new IllegalArgumentException("Product must exist");
            }
            if (!userFacade.isUserExist(username)) {
                throw new IllegalArgumentException("User must exist");
            }
            if(userFacade.isSuspended(username)){
                throw new RuntimeException("User is suspended from the system");
            }
            User user=userFacade.getUser(username);
            if(user.getRoleByStoreId(storeName)==null){
                throw new RuntimeException("User with no permission for this store");
            }
            user.getRoleByStoreId(storeName).setProduct_description(username, storeName,productId, productDescription);
            store.setProductDescription(productId, productDescription);
            lock.unlock();
            storeLocks.remove(storeName, lock);
            return true;
        }
        catch (Exception e){
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
            if (!storeMemoryRepository.isExist(storeName)) {
                throw new IllegalArgumentException("Store must exist");
            }
            Store store = storeMemoryRepository.getStore(storeName);
            if (!store.getProducts().containsKey(productId)) {
                throw new IllegalArgumentException("Product must exist");
            }
            if (productPrice < 0) throw new IllegalArgumentException("Price can't be negative number");
            if (!userFacade.isUserExist(username)) {
                throw new IllegalArgumentException("User must exist");
            }
            if(userFacade.isSuspended(username)){
                throw new RuntimeException("User is suspended from the system");
            }
            User user=userFacade.getUser(username);
            if(user.getRoleByStoreId(storeName)==null){
                throw new RuntimeException("User with no permission for this store");
            }
            user.getRoleByStoreId(storeName).setProduct_price(username, storeName,productId, productPrice);
            store.setProductPrice(productId, productPrice);
            lock.unlock();
            storeLocks.remove(storeName, lock);
            return true;
        }
        catch (Exception e){
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
            if (!storeMemoryRepository.isExist(storeName)) {
                throw new IllegalArgumentException("Store must exist");
            }
            Store store = storeMemoryRepository.getStore(storeName);
            if (!store.getProducts().containsKey(productId)) {
                throw new IllegalArgumentException("Product must exist");
            }
            if (productQuantity <= 0) throw new IllegalArgumentException("Quantity must be natural number");
            if (!userFacade.isUserExist(username)) {
                throw new IllegalArgumentException("User must exist");
            }
            if(userFacade.isSuspended(username)){
                throw new RuntimeException("User is suspended from the system");
            }
            User user=userFacade.getUser(username);
            if(user.getRoleByStoreId(storeName)==null){
                throw new RuntimeException("User with no permission for this store");
            }
            user.getRoleByStoreId(storeName).setProduct_quantity(username, storeName,productId, productQuantity);
            store.setProductQuantity(productId, productQuantity);
            lock.unlock();
            storeLocks.remove(storeName, lock);
            return true;
        }
        catch (Exception e){
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
            if (!storeMemoryRepository.isExist(storeName)) {
                throw new IllegalArgumentException("Store must exist");
            }
            Store store = storeMemoryRepository.getStore(storeName);
            if (!store.getProducts().containsKey(productId)) {
                throw new IllegalArgumentException("Product must exist");
            }
            if (rating < 0) throw new IllegalArgumentException("Rating can't be negative number");
            if (!userFacade.isUserExist(username)) {
                throw new IllegalArgumentException("User must exist");
            }
            if(userFacade.isSuspended(username)){
                throw new RuntimeException("User is suspended from the system");
            }
            User user=userFacade.getUser(username);
            if(user.getRoleByStoreId(storeName)==null){
                throw new RuntimeException("User with no permission for this store");
            }
            user.getRoleByStoreId(storeName).setRating(username, storeName,productId,rating);
            store.setRating(productId,rating);
            lock.unlock();
            storeLocks.remove(storeName, lock);
            return true;
        }
        catch (Exception e){
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
            if (!storeMemoryRepository.isExist(storeName)) {
                throw new IllegalArgumentException("Store must exist");
            }
            Store store = storeMemoryRepository.getStore(storeName);
            if (!store.getProducts().containsKey(productId)) {
                throw new IllegalArgumentException("Product must exist");
            }
            if (!userFacade.isUserExist(username)) {
                throw new IllegalArgumentException("User must exist");
            }
            if(userFacade.isSuspended(username)){
                throw new RuntimeException("User is suspended from the system");
            }
            User user=userFacade.getUser(username);
            if(user.getRoleByStoreId(storeName)==null){
                throw new RuntimeException("User with no permission for this store");
            }
            user.getRoleByStoreId(storeName).setCategory(username, storeName,productId,category);
            store.setCategory(productId,category);
            lock.unlock();
            storeLocks.remove(storeName, lock);
            return true;
        }
        catch (Exception e){
            lock.unlock();
            storeLocks.remove(storeName, lock);
            throw e;
        }
    }

    @Override
    public String getHistoryPurchasesByCustomer(String userName, String storeName, String customerUserName) throws IllegalAccessException {
        if (!storeMemoryRepository.isExist(storeName)) {
            throw new IllegalArgumentException("Store must exist");
        }
        Store store = storeMemoryRepository.getStore(storeName);
        if (!userFacade.isUserExist(userName)) {
            throw new IllegalArgumentException("User must exist");
        }
        if(userFacade.isSuspended(userName)){
            throw new RuntimeException("User is suspended from the system");
        }
        if (!userFacade.isUserExist(customerUserName)) {
            throw new IllegalArgumentException("Customer must exist");
        }
        User user = userFacade.getUser(userName);
        User customer = userFacade.getUsers().get(customerUserName);
        if(user.isAdmin())
            return store.getHistoryPurchasesByCustomer(customer.getUsername()).stream().map(Purchase::toString).collect(Collectors.joining("\n\n"));

        user.getRoleByStoreId(storeName).getRoleState().getHistoryPurchasesByCustomer();
        return store.getHistoryPurchasesByCustomer(customer.getUsername()).stream().map(Purchase::toString).collect(Collectors.joining("\n\n"));
    }

    @Override
    public String getAllHistoryPurchases(String userName, String storeName) throws IllegalAccessException {
        if (!storeMemoryRepository.isExist(storeName)) {
            throw new IllegalArgumentException("Store must exist");
        }
        if (!userFacade.getUsers().containsKey(userName)) {
            throw new IllegalArgumentException("User must exist");
        }
        if(userFacade.isSuspended(userName)){
            throw new RuntimeException("User is suspended from the system");
        }
        Store store = storeMemoryRepository.getStore(storeName);
        User user = userFacade.getUsers().get(userName);
        if(user.isAdmin())
            return store.getAllHistoryPurchases().stream().map(Purchase::toString).collect(Collectors.joining("\n\n"));
        if(user.getRoleByStoreId(storeName) == null)
            throw new RuntimeException("Not allowed to view store history");
        user.getRoleByStoreId(storeName).getRoleState().getAllHistoryPurchases();
        return store.getAllHistoryPurchases().stream().map(Purchase::toString).collect(Collectors.joining("\n\n"));
    }

    @Override
    public String requestInformationAboutOfficialsInStore(String userName, String storeName) throws IllegalArgumentException, IllegalAccessException {
        validateUserAndStore(userName, storeName);
        User user = userFacade.getUser(userName);
        Store store = storeMemoryRepository.getStore(storeName);
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
        return result.toString();
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
        Store store = storeMemoryRepository.getStore(storeName);

        user.getRoleByStoreId(storeName).getRoleState().requestManagersPermissions();

        List<String> storeManagers = store.getManagers();
        StringBuilder result = new StringBuilder();
        result.append(storeName).append("\n\n");
        result.append("Managers :").append("\n");
        result.append("id username watch editSupply editPurchasePolicy editDiscountPolicy").append("\n");
        for (String manager : storeManagers) {
            User user2 = userFacade.getUser(manager);
            RoleState managerRole = user2.getRoleByStoreId(storeName).getRoleState();
            result.append(user2.getUsername()).append(" ").append(manager).append(" ").append(managerRole.isWatch()).append(" ").append(managerRole.isEditSupply()).append(" ").append(managerRole.isEditPurchasePolicy()).append(" ").append(managerRole.isEditDiscountPolicy()).append('\n');
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


        Store store = storeMemoryRepository.getStore(storeName);
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
                result.append("Manager ").append(user2.getUsername()).append(" ").append(officialUserName).append(" ").append(user2.getAddress()).append(" ").append(user2.getBirthdate()).append(" ").append(managerRole.isWatch()).append(" ").append(managerRole.isEditSupply()).append(" ").append(managerRole.isEditPurchasePolicy()).append(" ").append(managerRole.isEditDiscountPolicy()).append("\n");
            } else
                throw new IllegalArgumentException("User is not employed in this store.");

        return result.toString();
    }


    @Override
    public HashMap<String, Store> getStores() {
        return storeMemoryRepository.getAllStores();
    }

    @Override
    public Store getStore(String storeName) {
        return storeMemoryRepository.getStore(storeName);
    }

    @Override
    public synchronized void releaseReservedProducts(int productId, int quantity, String storeName) {
        getStore(storeName).releaseReservedProducts(productId,quantity);

    }

    @Override
    public synchronized void removeReservedProducts(int productId, int quantity, String storeName) {
        getStore(storeName).removeReservedProducts(productId,quantity);

    }

    @Override
    public double calculateTotalPrice(String cartJSON) throws IOException {
        CartDTO cart = CartDTO.fromJson(cartJSON);
        double price = 0;
        for (ShoppingBagDTO bag : cart.getShoppingBags().values()) {
            price += storeMemoryRepository.getStore(bag.getStoreId()).calculatePrice(bag.getProducts_list().values());
        }
        return price;
    }

    @Override
    public boolean validatePurchasePolicies(String cartJSON, int age) throws IOException{
        CartDTO cart = CartDTO.fromJson(cartJSON);
        for (ShoppingBagDTO bag : cart.getShoppingBags().values()) {
            if(!storeMemoryRepository.getStore(bag.getStoreId()).validatePurchasePolicies(bag.getProducts_list().values(),age)){
                return false;
            }
        }
        return true;
    }

    @Override
    public void addPurchase(String customerUsername, List<ProductInSale> productInSaleList, double totalPrice, String storeName){
        storeMemoryRepository.getStore(storeName).addPurchase(new Purchase(customerUsername,productInSaleList,totalPrice,storeName));
    }

    //region Discount management
    @Override
    public String getDiscountPolicies(String username, String storeName) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        return storeMemoryRepository.getStore(storeName).getDiscountPoliciesInfo();
    }

    @Override
    public String getDiscountConditions(String username, String storeName) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        return storeMemoryRepository.getStore(storeName).getConditionInfo();
    }

    @Override
    public void addCategoryPercentageDiscount(String username, String storeName, int category, double discountPercent) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeMemoryRepository.getStore(storeName).addCategoryPercentageDiscount(category, discountPercent);
    }

    @Override
    public void addProductPercentageDiscount(String username, String storeName, int productId, double discountPercent) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeMemoryRepository.getStore(storeName).addProductPercentageDiscount(productId, discountPercent);
    }

    @Override
    public void addStoreDiscount(String username, String storeName, double discountPercent) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeMemoryRepository.getStore(storeName).addStoreDiscount(discountPercent);
    }

    @Override
    public void addConditionalDiscount(String username, String storeName) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeMemoryRepository.getStore(storeName).addConditionalDiscount();
    }

    @Override
    public void addAdditiveDiscount(String username, String storeName) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeMemoryRepository.getStore(storeName).addAdditiveDiscount();
    }

    @Override
    public void addMaxDiscount(String username, String storeName) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeMemoryRepository.getStore(storeName).addMaxDiscount();
    }

    @Override
    public void addCategoryCountCondition(String username, String storeName, int category, int count) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeMemoryRepository.getStore(storeName).addCategoryCountCondition(category, count);
    }

    @Override
    public void addTotalSumCondition(String username, String storeName, double requiredSum) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeMemoryRepository.getStore(storeName).addTotalSumCondition(requiredSum);
    }

    @Override
    public void addProductCountCondition(String username, String storeName, int productId, int count) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeMemoryRepository.getStore(storeName).addProductCountCondition(productId, count);
    }

    @Override
    public void addAndDiscount(String username, String storeName) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeMemoryRepository.getStore(storeName).addAndDiscount();
    }

    @Override
    public void addOrDiscount(String username, String storeName) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeMemoryRepository.getStore(storeName).addOrDiscount();
    }

    @Override
    public void addXorDiscount(String username, String storeName) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeMemoryRepository.getStore(storeName).addXorDiscount();
    }

    @Override
    public void removeDiscount(String username, String storeName, int selectedIndex) throws IllegalAccessException{
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeMemoryRepository.getStore(storeName).removeDiscount(selectedIndex);
    }

    @Override
    public void setFirstDiscount(String username, String storeName, int selectedDiscountIndex, int selectedFirstIndex) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeMemoryRepository.getStore(storeName).setFirstDiscount(selectedDiscountIndex, selectedFirstIndex);
    }

    @Override
    public void setSecondDiscount(String username, String storeName, int selectedDiscountIndex, int selectedSecondIndex) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeMemoryRepository.getStore(storeName).setSecondDiscount(selectedDiscountIndex, selectedSecondIndex);
    }

    @Override
    public void setFirstCondition(String username, String storeName, int selectedDiscountIndex, int selectedSecondIndex) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeMemoryRepository.getStore(storeName).setFirstCondition(selectedDiscountIndex, selectedSecondIndex);
    }

    @Override
    public void setSecondCondition(String username, String storeName, int selectedDiscountIndex, int selectedSecondIndex) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeMemoryRepository.getStore(storeName).setSecondCondition(selectedDiscountIndex, selectedSecondIndex);
    }

    @Override
    public void setThenDiscount(String username, String storeName, int selectedDiscountIndex, int selectedThenIndex) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeMemoryRepository.getStore(storeName).setThenDiscount(selectedDiscountIndex, selectedThenIndex);
    }

    @Override
    public void setCategoryDiscount(String username, String storeName, int selectedDiscountIndex, int category) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeMemoryRepository.getStore(storeName).setCategoryDiscount(selectedDiscountIndex, category);
    }

    @Override
    public void setProductIdDiscount(String username, String storeName, int selectedDiscountIndex, int productId) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeMemoryRepository.getStore(storeName).setProductIdDiscount(selectedDiscountIndex, productId);
    }

    @Override
    public void setPercentDiscount(String username, String storeName, int selectedDiscountIndex, double discountPercent) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeMemoryRepository.getStore(storeName).setPercentDiscount(selectedDiscountIndex, discountPercent);
    }

    @Override
    public void setDeciderDiscount(String username, String storeName, int selectedDiscountIndex, int selectedDeciderIndex) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeMemoryRepository.getStore(storeName).setDeciderDiscount(selectedDiscountIndex, selectedDeciderIndex);
    }

    @Override
    public void setTotalSum(String username, String storeName, int selectedConditionIndex, double newSum) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeMemoryRepository.getStore(storeName).setTotalSum(selectedConditionIndex, newSum);
    }

    @Override
    public void setCountCondition(String username, String storeName, int selectedConditionIndex, int newCount) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeMemoryRepository.getStore(storeName).setCountCondition(selectedConditionIndex, newCount);
    }

    @Override
    public void setCategoryCondition(String username, String storeName, int selectedConditionIndex, int newCategory) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editDiscounts();
        storeMemoryRepository.getStore(storeName).setCategoryCondition(selectedConditionIndex, newCategory);
    }
//region Purchase Policy Management

    @Override
    public String getPurchasePoliciesInfo(String username, String storeName) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        return storeMemoryRepository.getStore(storeName).getPurchasePoliciesInfo();
    }

    @Override
    public void addPurchasePolicyByAge(String username, String storeName, int ageToCheck, int category) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeMemoryRepository.getStore(storeName).addPurchasePolicyByAge(ageToCheck, category);
    }

    @Override
    public void addPurchasePolicyByCategory(String username, String storeName, int category, int productId) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeMemoryRepository.getStore(storeName).addPurchasePolicyByCategory(category,productId);
    }

    @Override
    public void addPurchasePolicyByCategoryAndDate(String username, String storeName, int category, LocalDateTime dateTime) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeMemoryRepository.getStore(storeName).addPurchasePolicyByCategoryAndDate(category, dateTime);
    }

    @Override
    public void addPurchasePolicyByDate(String username, String storeName, LocalDateTime dateTime) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeMemoryRepository.getStore(storeName).addPurchasePolicyByDate(dateTime);
    }

    @Override
    public void addPurchasePolicyByProductAndDate(String username, String storeName, int productId, LocalDateTime dateTime) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeMemoryRepository.getStore(storeName).addPurchasePolicyByProductAndDate(productId, dateTime);
    }

    @Override
    public void addPurchasePolicyByShoppingCartMaxProductsUnit(String username, String storeName, int productId, int numOfQuantity) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeMemoryRepository.getStore(storeName).addPurchasePolicyByShoppingCartMaxProductsUnit(productId, numOfQuantity);
    }

    @Override
    public void addPurchasePolicyByShoppingCartMinProducts(String username, String storeName, int numOfQuantity) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeMemoryRepository.getStore(storeName).addPurchasePolicyByShoppingCartMinProducts(numOfQuantity);
    }

    @Override
    public void addPurchasePolicyByShoppingCartMinProductsUnit(String username, String storeName, int productId, int numOfQuantity) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeMemoryRepository.getStore(storeName).addPurchasePolicyByShoppingCartMinProductsUnit(productId, numOfQuantity);
    }

    @Override
    public void addAndPurchasePolicy(String username, String storeName) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeMemoryRepository.getStore(storeName).addAndPurchasePolicy();
    }

    @Override
    public void addOrPurchasePolicy(String username, String storeName) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeMemoryRepository.getStore(storeName).addOrPurchasePolicy();
    }

    @Override
    public void addConditioningPurchasePolicy(String username, String storeName) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeMemoryRepository.getStore(storeName).addConditioningPurchasePolicy();
    }

    @Override
    public void setPurchasePolicyProductId(String username, String storeName, int selectedIndex, int productId) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeMemoryRepository.getStore(storeName).setPurchasePolicyProductId(selectedIndex, productId);
    }

    @Override
    public void setPurchasePolicyNumOfQuantity(String username, String storeName, int selectedIndex, int numOfQuantity) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeMemoryRepository.getStore(storeName).setPurchasePolicyNumOfQuantity(selectedIndex, numOfQuantity);
    }

    @Override
    public void setPurchasePolicyDateTime(String username, String storeName, int selectedIndex, LocalDateTime dateTime) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeMemoryRepository.getStore(storeName).setPurchasePolicyDateTime(selectedIndex, dateTime);
    }

    @Override
    public void setPurchasePolicyAge(String username, String storeName, int selectedIndex, int age) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeMemoryRepository.getStore(storeName).setPurchasePolicyAge(selectedIndex, age);
    }

    @Override
    public void setFirstPurchasePolicy(String username, String storeName, int selectedDiscountIndex, int selectedFirstIndex) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeMemoryRepository.getStore(storeName).setFirstPurchasePolicy(selectedDiscountIndex, selectedFirstIndex);
    }

    @Override
    public void setSecondPurchasePolicy(String username, String storeName, int selectedDiscountIndex, int selectedSecondIndex) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeMemoryRepository.getStore(storeName).setSecondPurchasePolicy(selectedDiscountIndex, selectedSecondIndex);
    }

    @Override
    public void removePurchasePolicy(String username, String storeName, int selectedIndex) throws IllegalAccessException {
        validateUserAndStore(username, storeName);
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).editPurchasePolicies();
        storeMemoryRepository.getStore(storeName).removePurchasePolicy(selectedIndex);
    }

    private void validateUserAndStore(String username, String storeName) throws IllegalAccessException {
        if (!storeMemoryRepository.isExist(storeName)) {
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

    public void checkAvailabilityAndConditions(int id, int quantity, String storeId) {
        if (getStore(storeId) == null)
            throw new RuntimeException("store not exist");
        getStore(storeId).checkAvailabilityAndConditions(id,quantity);
    }
//endregion

}