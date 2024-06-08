package com.example.trading_system.domain.stores;
import com.example.trading_system.domain.users.RoleState;
import com.example.trading_system.domain.users.User;
import com.example.trading_system.domain.users.UserFacade;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
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
    public boolean isProductExist(String userName, int productId, String storeName) {
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
        sb.append("}");
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
        if(storeName == null){
            logger.error("No store name provided");
            throw new IllegalArgumentException("No store name provided");
        }
        if(!userFacade.getUsers().containsKey(name))
        {
            logger.error("User does not exist");
            throw new IllegalArgumentException("User does not exist");
        }
        if (!storeMemoryRepository.isExist(storeName))
        {
            logger.error("Store does not exist");
            throw new IllegalArgumentException("Store does not exist");
        }
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
        return store.searchName(name, minPrice, maxPrice, minRating, category).toString();
    }

    @Override
    public String searchCategoryInStore(String userName, Category category, String storeName, Double minPrice, Double maxPrice, Double minRating) {
        if (category == null) {
            logger.error("No category provided");
            throw new IllegalArgumentException("No category provided");
        }
        if(storeName == null){
            logger.error("No store name provided");
            throw new IllegalArgumentException("No store name provided");
        }
        if(!userFacade.getUsers().containsKey(userName))
        {
            logger.error("User does not exist");
            throw new IllegalArgumentException("User does not exist");
        }
        if (!storeMemoryRepository.isExist(storeName))
        {
            logger.error("Store does not exist");
            throw new IllegalArgumentException("Store does not exist");
        }
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
        if (!EnumSet.allOf(Category.class).contains(category)) {
            logger.error("Category is not a valid category");
            throw new RuntimeException("Category is not a valid category");
        }
        return store.searchCategory(category, minPrice, maxPrice, minRating).toString();
    }

    @Override
    public String searchKeywordsInStore(String userName, String keyWords, String storeName, Double minPrice, Double maxPrice, Double minRating, Category category) {
        if (keyWords == null) {
            logger.error("No keywords provided");
            throw new IllegalArgumentException("No keywords provided");
        }
        if(storeName == null){
            logger.error("No store name provided");
            throw new IllegalArgumentException("No store name provided");
        }
        if(!userFacade.getUsers().containsKey(userName))
        {
            logger.error("User does not exist");
            throw new IllegalArgumentException("User does not exist");
        }
        if (!storeMemoryRepository.isExist(storeName))
        {
            logger.error("Store does not exist");
            throw new IllegalArgumentException("Store does not exist");
        }
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
    public String searchNameInStores(String name, Double minPrice, Double maxPrice, Double minRating, Category category, Double storeRating) {
        if (name == null) {
            logger.error("No name provided");
            throw new IllegalArgumentException("No name provided");
        }
        if(!userFacade.getUsers().containsKey(name))
        {
            logger.error("User does not exist");
            throw new IllegalArgumentException("User does not exist");
        }
        StringBuilder sb = new StringBuilder();
        for (Store store : storeMemoryRepository.getAllStores().values()) {
            if (!store.isOpen())
                continue;
            if (!store.searchName(name, minPrice, maxPrice, minRating, category, storeRating).isEmpty())//Change to Repo
                sb.append(store.searchName(name, minPrice, maxPrice, minRating, category, storeRating).toString());

        }
        if (sb.isEmpty())
            return "{}";
        return sb.toString();
    }

    @Override
    public String searchCategoryInStores(String userName, Category category, Double minPrice, Double maxPrice, Double minRating, Double storeRating) {
        if (category == null) {
            logger.error("No category provided");
            throw new IllegalArgumentException("No category provided");
        }
        if(!userFacade.getUsers().containsKey(userName))
        {
            logger.error("User does not exist");
            throw new IllegalArgumentException("User does not exist");
        }
        if (!EnumSet.allOf(Category.class).contains(category)) {
            logger.error("Category is not a valid category");
            throw new RuntimeException("Category is not a valid category");
        }
        StringBuilder sb = new StringBuilder();
        for (Store store : storeMemoryRepository.getAllStores().values()) {    //Change to Repo
            if (!store.isOpen())
                continue;
            if (!store.searchCategory(category, minPrice, maxPrice, minRating, storeRating).isEmpty())//Change to Repo

                sb.append(store.searchCategory(category, minPrice, maxPrice, minRating, storeRating).toString());
        }
        if (sb.isEmpty())
            return "{}";

        return sb.toString();
    }

    @Override
    public String searchKeywordsInStores(String userName, String keyWords, Double minPrice, Double maxPrice, Double minRating, Category category, Double storeRating) {
        if (keyWords == null) {
            logger.error("No keywords provided");
            throw new IllegalArgumentException("No keywords provided");
        }
        if(!userFacade.getUsers().containsKey(userName))
        {
            logger.error("User does not exist");
            throw new IllegalArgumentException("User does not exist");
        }
        StringBuilder sb = new StringBuilder();
        for (Store store : storeMemoryRepository.getAllStores().values()) {      //Change to Repo
            if (!store.isOpen())
                continue;
            if (!store.searchKeywords(keyWords, minPrice, maxPrice, minRating, category, storeRating).isEmpty())
                sb.append(store.searchKeywords(keyWords, minPrice, maxPrice, minRating, category, storeRating).toString());
        }
        if (sb.isEmpty())
            return "{}";
        return sb.toString();
    }

    @Override
    public void openStoreExist(String userName, String storeName) {
        if(!userFacade.getUsers().containsKey(userName))
        {
            throw new IllegalArgumentException("User does not exist");
        }
        if (!storeMemoryRepository.isExist(storeName)) {
            throw new IllegalArgumentException("Store must exist to close");
        }
        Store store = storeMemoryRepository.getStore(storeName);    //Change to Repo
        if (!store.getFounder().equals(userName)) {
            throw new IllegalArgumentException("Only founder can close store exist");
        }
        if(store.isOpen()){
            throw new IllegalArgumentException("Store is already active");
        }
        store.setOpen(true);
    }

    @Override
    public void closeStoreExist(String userName, String storeName) throws IllegalArgumentException{
        if(!userFacade.getUsers().containsKey(userName))
        {
            throw new IllegalArgumentException("User does not exist");
        }
        if (!storeMemoryRepository.isExist(storeName)) {
            throw new IllegalArgumentException("Store must exist to close");
        }
        Store store = storeMemoryRepository.getStore(storeName);    //Change to Repo
        if (!store.getFounder().equals(userName)) {
            throw new IllegalArgumentException("Only founder can close store exist");
        }
        if(!store.isOpen()){
            throw new IllegalArgumentException("Store is not active");
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
        Store store = storeMemoryRepository.getStore(storeName);
        User user=userFacade.getUsers().get(username);
        if (!store.isOpen()  &&  !store.getFounder().equals(username))
            throw new IllegalAccessException("only founder can add product in case that the store closed");
        user.getRoleByStoreId(storeName).addProduct(username, productId, storeName, productName, productDescription, productPrice, productQuantity,rating,category,keyWords);
        store.addProduct(productId, storeName, productName, productDescription, productPrice, productQuantity,rating,category,keyWords);
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
        Store store=storeMemoryRepository.getStore(storeName);
        User user=userFacade.getUsers().get(username);
        if (!store.isOpen() &&  !store.getFounder().equals(username))
            throw new IllegalAccessException("Only founder can remove product in case that the store closed");
        user.getRoleByStoreId(storeName).removeProduct(username, storeName, productId);
        if(!storeMemoryRepository.getStore(storeName).getProducts().containsKey(productId)) {
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
        Store store = storeMemoryRepository.getStore(storeName);
        if (!store.getProducts().containsKey(productId)) {
            throw new IllegalArgumentException("Product must exist");
        }
        if (!userFacade.isUserExist(username)) {
            throw new IllegalArgumentException("User must exist");
        }
        User user=userFacade.getUsers().get(username);
        if (!store.isOpen() &&  !store.getFounder().equals(username))
            throw new IllegalAccessException("Only founder can set product name in case that the store closed");
        user.getRoleByStoreId(storeName).setProduct_name(username, storeName,productId, productName);
        store.setProductName(productId, productName);
        return true;
    }

    @Override
    public boolean setProductDescription(String username, String storeName, int productId, String productDescription) throws IllegalAccessException {
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
        User user=userFacade.getUsers().get(username);
        if (!store.isOpen() &&  !store.getFounder().equals(username))
            throw new IllegalAccessException("Only founder can set product description in case that the store closed");
        user.getRoleByStoreId(storeName).setProduct_description(username, storeName,productId, productDescription);
        store.setProductDescription(productId, productDescription);
        return true;
    }

    @Override
    public boolean setProductPrice(String username, String storeName, int productId, double productPrice) throws IllegalAccessException {
        if (!storeMemoryRepository.isExist(storeName)) {
            throw new IllegalArgumentException("Store must exist");
        }
        Store store = storeMemoryRepository.getStore(storeName);
        if (!store.getProducts().containsKey(productId)) {
            throw new IllegalArgumentException("Product must exist");
        }
        if (productPrice < 0)
            throw new IllegalArgumentException("Price can't be negative number");
        if (!userFacade.isUserExist(username)) {
            throw new IllegalArgumentException("User must exist");
        }
        User user=userFacade.getUsers().get(username);
        if (!store.isOpen() &&  ! store.getFounder().equals(username))
            throw new IllegalAccessException("Only founder can set product price in case that the store closed");
        user.getRoleByStoreId(storeName).setProduct_price(username, storeName,productId, productPrice);
        store.setProductPrice(productId, productPrice);
        return true;
    }

    @Override
    public boolean setProductQuantity(String username, String storeName, int productId, int productQuantity) throws IllegalAccessException {
        if (!storeMemoryRepository.isExist(storeName)) {
            throw new IllegalArgumentException("Store must exist");
        }
        Store store = storeMemoryRepository.getStore(storeName);
        if (!store.getProducts().containsKey(productId)) {
            throw new IllegalArgumentException("Product must exist");
        }
        if (productQuantity <= 0)
            throw new IllegalArgumentException("Quantity must be natural number");
        if (!userFacade.isUserExist(username)) {
            throw new IllegalArgumentException("User must exist");
        }
        User user=userFacade.getUsers().get(username);
        if (!store.isOpen() &&  ! store.getOwners().contains(username))
            throw new IllegalAccessException("Only founder can set product quantity in case that the store closed");
        user.getRoleByStoreId(storeName).setProduct_quantity(username, storeName,productId, productQuantity);
        store.setProductQuantity(productId, productQuantity);
        return true;
    }

    @Override
    public boolean setRating(String username, String storeName, int productId, double rating) throws IllegalAccessException {
        if (!storeMemoryRepository.isExist(storeName)) {
            throw new IllegalArgumentException("Store must exist");
        }
        Store store = storeMemoryRepository.getStore(storeName);
        if (!store.getProducts().containsKey(productId)) {
            throw new IllegalArgumentException("Product must exist");
        }
        if (rating < 0)
            throw new IllegalArgumentException("Rating can't be negative number");
        if (!userFacade.isUserExist(username)) {
            throw new IllegalArgumentException("User must exist");
        }
        User user=userFacade.getUsers().get(username);
        if (!store.isOpen() &&  ! store.getFounder().equals(username))
            throw new IllegalAccessException("Only founder can set product rating in case that the store closed");
        user.getRoleByStoreId(storeName).setRating(username, storeName,productId,rating);
        store.setRating(productId,rating);
        return true;
    }

    @Override
    public boolean setCategory(String username, String storeName, int productId, int category) throws IllegalAccessException {
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
        User user=userFacade.getUsers().get(username);
        if (!store.isOpen() &&  ! store.getFounder().equals(username))
            throw new IllegalAccessException("Only founder can set product category in case that the store closed");
        user.getRoleByStoreId(storeName).setCategory(username, storeName,productId,category);
        store.setCategory(productId,category);
        return true;
    }

    //todo : check what getRoleByStore do on clients and support that systemAdministrator can do this func
    @Override
    public String getHistoryPurchasesByCustomer(String userName, String storeName, String customerUserName) throws IllegalAccessException {
        if (!storeMemoryRepository.isExist(storeName)) {   //Change to Repo
            throw new IllegalArgumentException("Store must exist");
        }
        Store store = storeMemoryRepository.getStore(storeName);
        if (!userFacade.isUserExist(userName)) {
            throw new IllegalArgumentException("User must exist");
        }
        if (!userFacade.isUserExist(customerUserName)) {
            throw new IllegalArgumentException("Customer must exist");
        }
        User user=userFacade.getUsers().get(userName);
        if (!store.isOpen() &&  !(user.isAdmin()  || store.getOwners().contains(userName))) // founder must be also owner
            throw new IllegalAccessException("Only owners and trading system manager can get history purchases in case that the store closed");
        User customer = userFacade.getUsers().get(customerUserName);
        user.getRoleByStoreId(storeName).getRoleState().getHistoryPurchasesByCustomer();
        return store.getHistoryPurchasesByCustomer(customer.getUsername()).stream().map(Purchase::toString).collect(Collectors.joining("\n\n"));
    }

    //todo : check what getRoleByStore do on clients and support that systemAdministrator can do this func
    @Override
    public String getAllHistoryPurchases(String userName, String storeName) throws IllegalAccessException {
        if (!storeMemoryRepository.isExist(storeName)) {     //Change to Repo
            throw new IllegalArgumentException("Store must exist");
        }
        if (!userFacade.getUsers().containsKey(userName)) {
            throw new IllegalArgumentException("User must exist");
        }
        Store store = storeMemoryRepository.getStore(storeName);
        User user=userFacade.getUsers().get(userName);
        user.getRoleByStoreId(storeName).getRoleState().getAllHistoryPurchases();
        return store.getAllHistoryPurchases().stream().map(Purchase::toString).collect(Collectors.joining("\n\n"));

    }

    @Override
    public String requestInformationAboutOfficialsInStore(String userName, String storeName) throws IllegalArgumentException, IllegalAccessException {
        if (!storeMemoryRepository.isExist(storeName)) {
            throw new IllegalArgumentException("Store must exist");
        }
        if (!userFacade.isUserExist(userName)) {
            throw new IllegalArgumentException("User must exist");
        }
        User user=userFacade.getUsers().get(userName);
        Store store=storeMemoryRepository.getStore(storeName);
        user.getRoleByStoreId(storeName).getRoleState().requestInformationAboutOfficialsInStore();

        List<String> storeOwners = store.getOwners();
        List<String> storeManagers = store.getManagers();

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


        Store store = storeMemoryRepository.getStore(storeName);
        List<String> storeOwners = store.getOwners();

        StringBuilder result = new StringBuilder();
        result.append(storeName).append('\n');

        if (storeOwners.contains(officialUserName)) {
            User user2 = userFacade.getUser(officialUserName);
            result.append("Role id username address birthdate").append('\n');
            result.append("Owner ").append(user2.getUsername()).append(officialUserName).append(user2.getAddress()).append(user2.getBirthdate()).append('\n');
        }
        else
        {
            List<String> storeManagers = store.getManagers();
            if(storeManagers.contains(officialUserName))
            {
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


}