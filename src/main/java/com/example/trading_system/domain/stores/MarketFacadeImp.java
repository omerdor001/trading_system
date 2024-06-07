package com.example.trading_system.domain.stores;

import com.example.trading_system.domain.users.*;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MarketFacadeImp implements MarketFacade {
    @Getter
    private StoreRepository storeMemoryRepository;
    private UserFacade userFacade;
    private static final Logger logger = LoggerFactory.getLogger(MarketFacadeImp.class);
    private static MarketFacadeImp instance = null;


    private MarketFacadeImp() {
        this.storeMemoryRepository = StoreMemoryRepository.getInstance();
    }

    public static MarketFacadeImp getInstance() {
        if (instance == null)
            instance = new MarketFacadeImp();
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
/*        this.userFacade = null;
        this.storeMemoryRepository = null;
        instance = null;*/
    }


    public boolean isStoreExist(String store_name) {
        return storeMemoryRepository.isExist(store_name);
    }

    public void addStore(String storeName, String description, StorePolicy storePolicy, String founder, Double storeRating) {
        storeMemoryRepository.addStore(storeName, description, storePolicy, founder, storeRating);
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
    public boolean isProductExist(int productId, String storeName) {
        if (!storeMemoryRepository.isExist(storeName)) {
            throw new IllegalArgumentException("Store must exist");
        }
        return storeMemoryRepository.getStore(storeName).isProductExist(productId);
    }

    @Override
    public String getAllStores() {           //For UI ?
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("\"stores\":");

        for (Store store : storeMemoryRepository.getAllStores().values()) {
            sb.append(store.getNameId());
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String getStoreProducts(String storeName) {
        if (storeMemoryRepository.getStore(storeName) == null) {
            throw new RuntimeException("Can't find store with name " + storeName);
        }
        if (storeMemoryRepository.getStore(storeName).isActive()) {    //Change to Repo
            return storeMemoryRepository.getStore(storeName).toString();
        } else {
            logger.error("Can't find store with name {}", storeName);
            return null;
        }
    }

    @Override
    public String getProductInfo(String storeName, int productId) {     //Change to Reop
        if (storeMemoryRepository.getStore(storeName).getProduct(productId) == null) {
            throw new RuntimeException("Can't find product with id " + productId);
        }
        if (storeMemoryRepository.getStore(storeName) == null) {
            throw new RuntimeException("Can't find store with name " + storeName);
        }
        return storeMemoryRepository.getStore(storeName).getProduct(productId).toString();

    }

    @Override
    public String searchNameInStore(String name, String storeName, Double minPrice, Double maxPrice, Double minRating, Category category) {
        if (name == null) {
            logger.error("No name provided");
            throw new IllegalArgumentException("No name provided");
        }
        if (storeName == null) {
            logger.error("No store name provided");
            throw new IllegalArgumentException("No store name provided");
        }

        if (storeMemoryRepository.getStore(storeName).getProducts().isEmpty()) {    //Change To Repo
            logger.warn("No products Available");
            return "{}";
        }
        return storeMemoryRepository.getStore(storeName).searchName(name, minPrice, maxPrice, minRating, category).toString();
    }

    @Override
    public String searchCategoryInStore(Category category, String storeName, Double minPrice, Double maxPrice, Double minRating) {
        if (category == null) {
            logger.error("No category provided");
            throw new IllegalArgumentException("No category provided");
        }
        if (storeName == null) {
            logger.error("No store name provided");
            throw new IllegalArgumentException("No store name provided");
        }

        if (storeMemoryRepository.getStore(storeName).getProducts().isEmpty()) {   //Change to Repo
            logger.warn("No products Available");
            return "{}";
        }
        if (!EnumSet.allOf(Category.class).contains(category)) {
            logger.error("Category is not a valid category");
            throw new RuntimeException("Category is not a valid category");
        }
        return storeMemoryRepository.getStore(storeName).searchCategory(category, minPrice, maxPrice, minRating).toString();
    }

    @Override
    public String searchKeywordsInStore(String keyWords, String storeName, Double minPrice, Double maxPrice, Double minRating, Category category) {
        if (keyWords == null) {
            logger.error("No keywords provided");
            throw new IllegalArgumentException("No keywords provided");
        }
        if (storeName == null) {
            logger.error("No store name provided");
            throw new IllegalArgumentException("No store name provided");
        }

        if (storeMemoryRepository.getStore(storeName).getProducts().isEmpty()) {    //Change to Repo
            logger.warn("No products Available");
            return "{}";
        }
        return storeMemoryRepository.getStore(storeName).searchKeywords(keyWords, minPrice, maxPrice, minRating, category).toString();
    }

    @Override
    public String searchNameInStores(String name, Double minPrice, Double maxPrice, Double minRating, Category category, Double storeRating) {
        if (name == null) {
            logger.error("No name provided");
            throw new IllegalArgumentException("No name provided");
        }
        StringBuilder sb = new StringBuilder();
        for (Store store : storeMemoryRepository.getAllStores().values()) {
            if (!store.searchName(name, minPrice, maxPrice, minRating, category, storeRating).isEmpty())//Change to Repo
                sb.append(store.searchName(name, minPrice, maxPrice, minRating, category, storeRating).toString());

        }
        if (sb.isEmpty())
            return "{}";
        return sb.toString();
    }

    @Override
    public String searchCategoryInStores(Category category, Double minPrice, Double maxPrice, Double minRating, Double storeRating) {
        if (category == null) {
            logger.error("No category provided");
            throw new IllegalArgumentException("No category provided");
        }
        if (!EnumSet.allOf(Category.class).contains(category)) {
            logger.error("Category is not a valid category");
            throw new RuntimeException("Category is not a valid category");
        }
        StringBuilder sb = new StringBuilder();
        for (Store store : storeMemoryRepository.getAllStores().values()) {    //Change to Repo
            if (!store.searchCategory(category, minPrice, maxPrice, minRating, storeRating).isEmpty())//Change to Repo

                sb.append(store.searchCategory(category, minPrice, maxPrice, minRating, storeRating).toString());
        }
        if (sb.isEmpty())
            return "{}";

        return sb.toString();
    }

    @Override
    public String searchKeywordsInStores(String keyWords, Double minPrice, Double maxPrice, Double minRating, Category category, Double storeRating) {
        if (keyWords == null) {
            logger.error("No keywords provided");
            throw new IllegalArgumentException("No keywords provided");
        }
        StringBuilder sb = new StringBuilder();
        for (Store store : storeMemoryRepository.getAllStores().values()) {      //Change to Repo
            if (!store.searchKeywords(keyWords, minPrice, maxPrice, minRating, category, storeRating).isEmpty())
                sb.append(store.searchKeywords(keyWords, minPrice, maxPrice, minRating, category, storeRating).toString());
        }
        if (sb.isEmpty())
            return "{}";
        return sb.toString();
    }

    @Override
    public void openStoreExist(String storeName) {
        Store store = storeMemoryRepository.getStore(storeName);    //Change to Repo
        if (store == null) {
            logger.error("No store available - to open");
            throw new RuntimeException("No store available");
        }
        store.setOpen(true);
    }

    @Override
    public void closeStoreExist(String storeName) {
        Store store = storeMemoryRepository.getStore(storeName);    //Change to Repo
        if (store == null) {
            logger.error("No store available -  to close");
            throw new RuntimeException("No store available");
        }
        store.setOpen(false);
    }

    //Supply Management

    @Override
    public boolean addProduct(String username, int productId, String storeName, String productName, String productDescription,
                              double productPrice, int productQuantity, double rating, int category, List<String> keyWords) throws IllegalAccessException {
        if (!storeMemoryRepository.isExist(storeName)) {
            throw new IllegalArgumentException("Store must exist");
        }
        if (!userFacade.isUserExist(username)) {
            throw new IllegalArgumentException("User must exist");
        }
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).addProduct(username, productId, storeName, productName, productDescription, productPrice, productQuantity, rating, category, keyWords);
        storeMemoryRepository.getStore(storeName).addProduct(productId, storeName, productName, productDescription, productPrice, productQuantity, rating, category, keyWords);
        return true;
    }

    @Override
    public boolean removeProduct(String username, String storeName, int productId) throws IllegalAccessException {
        if (!storeMemoryRepository.isExist(storeName)) {
            throw new IllegalArgumentException("Store must exist");
        }
        if (!userFacade.isUserExist(username)) {
            throw new IllegalArgumentException("User must exist");
        }
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).removeProduct(username, storeName, productId);
        if (!storeMemoryRepository.getStore(storeName).getProducts().containsKey(productId)) {
            throw new IllegalArgumentException("Product must exist");
        }
        storeMemoryRepository.getStore(storeName).removeProduct(productId);
        return true;
    }

    @Override
    public boolean setProductName(String username, String storeName, int productId, String productName) throws IllegalAccessException {
        if (!storeMemoryRepository.isExist(storeName)) {
            throw new IllegalArgumentException("Store must exist");
        }
        if (!storeMemoryRepository.getStore(storeName).getProducts().containsKey(productId)) {
            throw new IllegalArgumentException("Product must exist");
        }
        if (!userFacade.isUserExist(username)) {
            throw new IllegalArgumentException("User must exist");
        }
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).setProduct_name(username, storeName, productId, productName);
        storeMemoryRepository.getStore(storeName).setProductName(productId, productName);
        return true;
    }

    @Override
    public boolean setProductDescription(String username, String storeName, int productId, String productDescription) throws IllegalAccessException {
        if (!storeMemoryRepository.isExist(storeName)) {
            throw new IllegalArgumentException("Store must exist");
        }
        if (!storeMemoryRepository.getStore(storeName).getProducts().containsKey(productId)) {
            throw new IllegalArgumentException("Product must exist");
        }
        if (!userFacade.isUserExist(username)) {
            throw new IllegalArgumentException("User must exist");
        }
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).setProduct_description(username, storeName, productId, productDescription);
        storeMemoryRepository.getStore(storeName).setProductDescription(productId, productDescription);
        return true;
    }

    @Override
    public boolean setProductPrice(String username, String storeName, int productId, double productPrice) throws IllegalAccessException {
        if (!storeMemoryRepository.isExist(storeName)) {
            throw new IllegalArgumentException("Store must exist");
        }
        if (!storeMemoryRepository.getStore(storeName).getProducts().containsKey(productId)) {
            throw new IllegalArgumentException("Product must exist");
        }
        if (productPrice < 0)
            throw new IllegalArgumentException("Price can't be negative number");
        if (!userFacade.isUserExist(username)) {
            throw new IllegalArgumentException("User must exist");
        }
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).setProduct_price(username, storeName, productId, productPrice);
        storeMemoryRepository.getStore(storeName).setProductPrice(productId, productPrice);
        return true;
    }

    @Override
    public boolean setProductQuantity(String username, String storeName, int productId, int productQuantity) throws IllegalAccessException {
        if (!storeMemoryRepository.isExist(storeName)) {
            throw new IllegalArgumentException("Store must exist");
        }
        if (!storeMemoryRepository.getStore(storeName).getProducts().containsKey(productId)) {
            throw new IllegalArgumentException("Product must exist");
        }
        if (productQuantity <= 0)
            throw new IllegalArgumentException("Quantity must be natural number");
        if (!userFacade.isUserExist(username)) {
            throw new IllegalArgumentException("User must exist");
        }
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).setProduct_quantity(username, storeName, productId, productQuantity);
        storeMemoryRepository.getStore(storeName).setProductQuantity(productId, productQuantity);
        return true;
    }

    @Override
    public boolean setRating(String username, String storeName, int productId, double rating) throws IllegalAccessException {
        if (!storeMemoryRepository.isExist(storeName)) {
            throw new IllegalArgumentException("Store must exist");
        }
        if (!storeMemoryRepository.getStore(storeName).getProducts().containsKey(productId)) {
            throw new IllegalArgumentException("Product must exist");
        }
        if (rating < 0)
            throw new IllegalArgumentException("Rating can't be negative number");
        if (!userFacade.isUserExist(username)) {
            throw new IllegalArgumentException("User must exist");
        }
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).setRating(username, storeName, productId, rating);
        storeMemoryRepository.getStore(storeName).setRating(productId, rating);
        return true;
    }

    @Override
    public boolean setCategory(String username, String storeName, int productId, int category) throws IllegalAccessException {
        if (!storeMemoryRepository.isExist(storeName)) {
            throw new IllegalArgumentException("Store must exist");
        }
        if (!storeMemoryRepository.getStore(storeName).getProducts().containsKey(productId)) {
            throw new IllegalArgumentException("Product must exist");
        }
        if (!userFacade.isUserExist(username)) {
            throw new IllegalArgumentException("User must exist");
        }
        User user = userFacade.getUser(username);
        user.getRoleByStoreId(storeName).setCategory(username, storeName, productId, category);
        storeMemoryRepository.getStore(storeName).setCategory(productId, category);
        return true;
    }

    @Override
    public String getHistoryPurchasesByCustomer(String userName, String storeName, String customerUserName) throws IllegalAccessException {
        if (!storeMemoryRepository.isExist(storeName)) {   //Change to Repo
            throw new IllegalArgumentException("Store must exist");
        }
        if (!userFacade.isUserExist(userName)) {
            throw new IllegalArgumentException("User must exist");
        }
        if (!userFacade.isUserExist(customerUserName)) {
            throw new IllegalArgumentException("Customer must exist");
        }
        User user = userFacade.getUser(userName);
        User customer = userFacade.getUser(customerUserName);
        user.getRoleByStoreId(storeName).getRoleState().getHistoryPurchasesByCustomer();
        return storeMemoryRepository.getStore(storeName).getHistoryPurchasesByCustomer(customer.getUsername()).stream().map(Purchase::toString).collect(Collectors.joining("\n\n"));
    }

    @Override
    public String getAllHistoryPurchases(String userName, String storeName) throws IllegalAccessException {
        if (!storeMemoryRepository.isExist(storeName)) {     //Change to Repo
            throw new IllegalArgumentException("Store must exist");
        }
        if (!userFacade.getUsers().containsKey(userName)) {
            throw new IllegalArgumentException("User must exist");
        }
        User user = userFacade.getUsers().get(userName);
        user.getRoleByStoreId(storeName).getRoleState().getAllHistoryPurchases();
        return storeMemoryRepository.getStore(storeName).getAllHistoryPurchases().stream().map(Purchase::toString).collect(Collectors.joining("\n\n"));

    }

    @Override
    public String requestInformationAboutOfficialsInStore(String userName, String storeName) throws IllegalArgumentException, IllegalAccessException {
        if (!storeMemoryRepository.isExist(storeName)) {
            throw new IllegalArgumentException("Store must exist");
        }
        if (!userFacade.isUserExist(userName)) {
            throw new IllegalArgumentException("User must exist");
        }
        User user = userFacade.getUser(userName);
        user.getRoleByStoreId(storeName).getRoleState().requestInformationAboutOfficialsInStore();

        List<String> storeOwners = storeMemoryRepository.getStore(storeName).getOwners();
        List<String> storeManagers = storeMemoryRepository.getStore(storeName).getManagers();

        StringBuilder result = new StringBuilder();
        result.append(storeName).append("\n");
        result.append("Role id username address birthdate").append("\n");
        for (String owner : storeOwners) {
            User user2 = userFacade.getUser(userName);
            result.append("Owner ").append(user2.getUsername()).append(owner).append(user2.getAddress()).append(user2.getBirthdate()).append("\n");
        }
        for (String manager : storeManagers) {
            User user2 = userFacade.getUser(manager);
            result.append("Manager ").append(user2.getUsername()).append(manager).append(user2.getAddress()).append(user2.getBirthdate()).append("\n");
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
        if (!storeMemoryRepository.isExist(storeName)) {
            throw new IllegalArgumentException("Store must exist");
        }
        if (!userFacade.isUserExist(userName)) {
            throw new IllegalArgumentException("User must exist");
        }
        User user = userFacade.getUser(userName);
        user.getRoleByStoreId(storeName).getRoleState().requestManagersPermissions();

        List<String> storeManagers = storeMemoryRepository.getStore(storeName).getManagers();

        StringBuilder result = new StringBuilder();
        result.append(storeName).append("\n\n");
        result.append("Managers :").append("\n");
        result.append("id username watch editSupply editBuyPolicy editDiscountPolicy").append("\n");
        for (String manager : storeManagers) {
            User user2 = userFacade.getUser(manager);
            RoleState managerRole = user2.getRoleByStoreId(storeName).getRoleState();
            result.append(user2.getUsername()).append(manager).append(managerRole.isWatch()).append(managerRole.isEditSupply()).append(managerRole.isEditBuyPolicy()).append(managerRole.isEditDiscountPolicy()).append('\n');
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
        if (!storeMemoryRepository.isExist(storeName)) {
            throw new IllegalArgumentException("Store must exist");
        }
        if (!userFacade.isUserExist(userName)) {
            throw new IllegalArgumentException("User must exist");
        }
        if (!userFacade.isUserExist(officialUserName)) {
            throw new IllegalArgumentException("User must exist");
        }

        User user = userFacade.getUser(userName);
        user.getRoleByStoreId(storeName).getRoleState().requestInformationAboutSpecificOfficialInStore();


        List<String> storeOwners = storeMemoryRepository.getStore(storeName).getOwners();

        StringBuilder result = new StringBuilder();
        result.append(storeName).append('\n');

        if (storeOwners.contains(officialUserName)) {
            User user2 = userFacade.getUser(officialUserName);
            result.append("Role id username address birthdate").append('\n');
            result.append("Owner ").append(user2.getUsername()).append(officialUserName).append(user2.getAddress()).append(user2.getBirthdate()).append('\n');
        } else {
            List<String> storeManagers = storeMemoryRepository.getStore(storeName).getManagers();
            if (storeManagers.contains(officialUserName)) {
                User user2 = userFacade.getUser(userName);
                RoleState managerRole = user2.getRoleByStoreId(storeName).getRoleState();
                result.append("Role id username address birthdate watch editSupply editBuyPolicy editDiscountPolicy").append("\n");
                result.append("Manager ").append(user2.getUsername()).append(officialUserName).append(user2.getAddress()).append(user2.getBirthdate()).append(managerRole.isWatch()).append(managerRole.isEditSupply()).append(managerRole.isEditBuyPolicy()).append(managerRole.isEditDiscountPolicy()).append("\n");
            } else
                throw new IllegalArgumentException("User is not employeed in this store.");
        }
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
        int productQuantity = getStore(storeName).getProduct(productId).getProduct_quantity();
        getStore(storeName).getProduct(productId).setProduct_quantity(productQuantity + quantity);

    }

    @Override
    public synchronized void removeReservedProducts(int productId, int quantity, String storeName) {
        int productQuantity = getStore(storeName).getProduct(productId).getProduct_quantity();
        getStore(storeName).getProduct(productId).setProduct_quantity(productQuantity - quantity);

    }

    @Override
    public double calculateTotalPrice(Cart cart) {
        double price = 0;
        for (Map.Entry<String, ShoppingBag> shoppingBagInStore : cart.getShoppingBags().entrySet()) {
            for (Map.Entry<Integer, ProductInSale> productEntry : shoppingBagInStore.getValue().getProducts_list().entrySet()) {
                price = price + productEntry.getValue().sumTotalPrice();
            }
        }
        return price;
    }


}