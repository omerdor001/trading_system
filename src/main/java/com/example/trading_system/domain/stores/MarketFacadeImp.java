package com.example.trading_system.domain.stores;

import com.example.trading_system.domain.users.Registered;
import com.example.trading_system.domain.users.RoleState;
import com.example.trading_system.domain.users.UserFacade;
import com.example.trading_system.domain.users.UserFacadeImp;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MarketFacadeImp implements MarketFacade{
    @Getter
    private HashMap<String, Store> stores;
    private UserFacade userFacade = UserFacadeImp.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(MarketFacadeImp.class);

    private MarketFacadeImp() {
        stores = new HashMap<>();
        userFacade= UserFacadeImp.getInstance();
    }
    private  static class Singleton  {
        private static final MarketFacadeImp INSTANCE = new MarketFacadeImp();
    }
    public static MarketFacadeImp getInstance() {
        return Singleton.INSTANCE;
    }

    public void addStore(Store store) {
        stores.put(store.getNameId(), store);
    }
    public void deactivateStore(String storeId){
        Store store = stores.get(storeId);
        if(store!=null){
            if(store.isActive()){
                store.setActive(false);
            }
            else {
                throw new RuntimeException("Can't deactivate store that already not active");
            }
        }
    }

    @Override
    public String getAllStores() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(", \"stores\":[");

        for (Store store : stores.values()) {
            sb.append(store.toString());
            sb.append(", ");
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String getStoreProducts(String storeName) {
        if (stores.get(storeName).isActive()) {
            return stores.get(storeName).toString();
        } else {
            logger.error("Can't find store with name {}", storeName);
            return null;
        }
    }


    @Override
    public String getProductInfo(String storeName, int productId) {
        return stores.get(storeName).getProduct(productId).toString();
    }

    @Override
    public String searchNameInStore(String name, String storeName, Double minPrice, Double maxPrice, Double minRating, Category category) {
        if (name == null) {
            logger.error("No name provided");
            throw new IllegalArgumentException("No name provided");
        }
        if (stores.get(storeName).getProducts().isEmpty()) {
            logger.warn("No products Available");
            return "{}";
        }
        return stores.get(storeName).searchName(name, minPrice, maxPrice, minRating, category).toString();
    }

    @Override
    public String searchCategoryInStore(Category category, String storeName, Double minPrice, Double maxPrice, Double minRating) {
        if (category == null) {
            logger.error("No category provided");
            throw new IllegalArgumentException("No category provided");
        }
        if (stores.get(storeName).getProducts().isEmpty()) {
            logger.warn("No products Available");
            return "{}";
        }
        if (!EnumSet.allOf(Category.class).contains(category)) {
            logger.error("Category is not a valid category");
            throw new RuntimeException("Category is not a valid category");
        }

        return stores.get(storeName).searchCategory(category, minPrice, maxPrice, minRating).toString();
    }

    @Override
    public String searchKeywordsInStore(String keyWords, String storeName, Double minPrice, Double maxPrice, Double minRating, Category category) {
        if (keyWords == null) {
            logger.error("No keywords provided");
            throw new IllegalArgumentException("No keywords provided");
        }
        if (stores.get(storeName).getProducts().isEmpty()) {
            logger.warn("No products Available");
            return "{}";
        }
        return stores.get(storeName).searchKeywords(keyWords, minPrice, maxPrice, minRating, category).toString();
    }

    @Override
    public String searchNameInStores(String name, Double minPrice, Double maxPrice, Double minRating, Category category) {
        if (name == null) {
            logger.error("No name provided");
            throw new IllegalArgumentException("No name provided");
        }
        StringBuilder sb = new StringBuilder();
        for (Store store : stores.values()) {
            sb.append(store.searchName(name, minPrice, maxPrice, minRating, category).toString());
        }
        return sb.toString();
    }

    @Override
    public String searchCategoryInStores(Category category, Double minPrice, Double maxPrice, Double minRating) {
        if (category == null) {
            logger.error("No category provided");
            throw new IllegalArgumentException("No category provided");
        }
        if (!EnumSet.allOf(Category.class).contains(category)) {
            logger.error("Category is not a valid category");
            throw new RuntimeException("Category is not a valid category");
        }
        StringBuilder sb = new StringBuilder();
        for (Store store : stores.values()) {
            sb.append(store.searchCategory(category, minPrice, maxPrice, minRating).toString());
        }
        return sb.toString();
    }

    @Override
    public String searchKeywordsInStores(String keyWords, Double minPrice, Double maxPrice, Double minRating, Category category) {
        if (keyWords == null) {
            logger.error("No keywords provided");
            throw new IllegalArgumentException("No keywords provided");
        }
        StringBuilder sb = new StringBuilder();
        for (Store store : stores.values()) {
            sb.append(store.searchKeywords(keyWords, minPrice, maxPrice, minRating, category).toString());
        }

        return sb.toString();
    }

    @Override
    public void openStoreExist(String storeId) {
        Store store = stores.get(storeId);
        if(store==null){
            logger.error("No store available - to open");
            throw new RuntimeException("No store available");
        }
        store.setOpen(true);
    }

    @Override
    public void closeStoreExist(String storeId) {
        Store store = stores.get(storeId);
        if(store==null){
            logger.error("No store available -  to close");
            throw new RuntimeException("No store available");
        }
        store.setOpen(false);
    }
    @Override
    public boolean addProduct(String username, int productId, String storeName, String productName, String productDescription,
                              double productPrice, int productQuantity, double rating, int category, List<String> keyWords) throws IllegalAccessException {
        if(!stores.containsKey(storeName)){
            throw new IllegalArgumentException("Store must exist");
        }
        if(productPrice <0)
            throw new IllegalArgumentException("Price can't be negative number");
        if(productQuantity <=0)
            throw new IllegalArgumentException("Quantity must be natural number");
        if(rating<0)
            throw new IllegalArgumentException("Rating can't be negative number");
        if(!userFacade.getRegistered().containsKey(username)){
            throw new IllegalArgumentException("User must exist");
        }
        Registered registered =userFacade.getRegistered().get(username);
        registered.getRoleByStoreId(storeName).addProduct(username, productId, storeName, productName, productDescription, productPrice, productQuantity,rating,category,keyWords);
        stores.get(storeName).addProduct(productId, storeName, productName, productDescription, productPrice, productQuantity,rating,category,keyWords);
        return true;
    }

    @Override
    public boolean removeProduct(String username, String storeName, int productId) throws IllegalAccessException {
        if(!stores.containsKey(storeName)){
            throw new IllegalArgumentException("Store must exist");
        }
        if(!userFacade.getRegistered().containsKey(username)){
            throw new IllegalArgumentException("User must exist");
        }
        Registered registered =userFacade.getRegistered().get(username);
        registered.getRoleByStoreId(storeName).removeProduct(username, storeName, productId);
        if(!stores.get(storeName).getProducts().containsKey(productId)) {
            throw new IllegalArgumentException("Product must exist");
        }
        stores.get(storeName).removeProduct(productId);
        return true;
    }

    @Override
    public boolean setProductName(String username, String storeName, int productId, String productName) throws IllegalAccessException {
        if(!stores.containsKey(storeName)){
            throw new IllegalArgumentException("Store must exist");
        }
        if(!stores.get(storeName).getProducts().containsKey(productId)) {
            throw new IllegalArgumentException("Product must exist");
        }
        if(!userFacade.getRegistered().containsKey(username)){
            throw new IllegalArgumentException("User must exist");
        }
        Registered registered =userFacade.getRegistered().get(username);
        registered.getRoleByStoreId(storeName).setProduct_name(username, storeName,productId, productName);
        stores.get(storeName).setProductName(productId, productName);
        return true;
    }

    @Override
    public boolean setProductDescription(String username, String storeName, int productId, String productDescription) throws IllegalAccessException {
        if(!stores.containsKey(storeName)){
            throw new IllegalArgumentException("Store must exist");
        }
        if(!stores.get(storeName).getProducts().containsKey(productId)) {
            throw new IllegalArgumentException("Product must exist");
        }
        if(!userFacade.getRegistered().containsKey(username)){
            throw new IllegalArgumentException("User must exist");
        }
        Registered registered =userFacade.getRegistered().get(username);
        registered.getRoleByStoreId(storeName).setProduct_description(username, storeName,productId, productDescription);
        stores.get(storeName).setProductDescription(productId, productDescription);
        return true;
    }

    @Override
    public boolean setProductPrice(String username, String storeName, int productId, int productPrice) throws IllegalAccessException {
        if(!stores.containsKey(storeName)){
            throw new IllegalArgumentException("Store must exist");
        }
        if(!stores.get(storeName).getProducts().containsKey(productId)) {
            throw new IllegalArgumentException("Product must exist");
        }
        if(productPrice <0)
            throw new IllegalArgumentException("Price can't be negative number");
        if(!userFacade.getRegistered().containsKey(username)){
            throw new IllegalArgumentException("User must exist");
        }
        Registered registered =userFacade.getRegistered().get(username);
        registered.getRoleByStoreId(storeName).setProduct_price(username, storeName,productId, productPrice);
        stores.get(storeName).setProductPrice(productId, productPrice);
        return true;
    }

    @Override
    public boolean setProductQuantity(String username, String storeName, int productId, int productQuantity) throws IllegalAccessException {
        if(!stores.containsKey(storeName)){
            throw new IllegalArgumentException("Store must exist");
        }
        if(!stores.get(storeName).getProducts().containsKey(productId)) {
            throw new IllegalArgumentException("Product must exist");
        }
        if(productQuantity <=0)
            throw new IllegalArgumentException("Quantity must be natural number");
        if(!userFacade.getRegistered().containsKey(username)){
            throw new IllegalArgumentException("User must exist");
        }
        Registered registered =userFacade.getRegistered().get(username);
        registered.getRoleByStoreId(storeName).setProduct_quantity(username, storeName,productId, productQuantity);
        stores.get(storeName).setProductQuantity(productId, productQuantity);
        return true;
    }

    @Override
    public boolean setRating(String username, String storeName, int productId, int rating) throws IllegalAccessException {
        if(!stores.containsKey(storeName)){
            throw new IllegalArgumentException("Store must exist");
        }
        if(!stores.get(storeName).getProducts().containsKey(productId)) {
            throw new IllegalArgumentException("Product must exist");
        }
        if(rating<0)
            throw new IllegalArgumentException("Rating can't be negative number");
        if(!userFacade.getRegistered().containsKey(username)){
            throw new IllegalArgumentException("User must exist");
        }
        Registered registered =userFacade.getRegistered().get(username);
        registered.getRoleByStoreId(storeName).setRating(username, storeName,productId,rating);
        stores.get(storeName).setRating(productId,rating);
        return true;
    }

    @Override
    public boolean setCategory(String username, String storeName, int productId, int category) throws IllegalAccessException {
        if(!stores.containsKey(storeName)){
            throw new IllegalArgumentException("Store must exist");
        }
        if(!stores.get(storeName).getProducts().containsKey(productId)) {
            throw new IllegalArgumentException("Product must exist");
        }
        if(!userFacade.getRegistered().containsKey(username)){
            throw new IllegalArgumentException("User must exist");
        }
        Registered registered =userFacade.getRegistered().get(username);
        registered.getRoleByStoreId(storeName).setCategory(username, storeName,productId,category);
        stores.get(storeName).setCategory(productId,category);
        return true;
    }


    @Override
    public String getHistoryPurchasesByCustomer(String userName, String storeName, String customerUserName) throws IllegalAccessException {
        if(!stores.containsKey(storeName)){
            throw new IllegalArgumentException("Store must exist");
        }
        if(!userFacade.getRegistered().containsKey(userName)){
            throw new IllegalArgumentException("User must exist");
        }
        if(!userFacade.getRegistered().containsKey(customerUserName)){
            throw new IllegalArgumentException("Customer must exist");
        }
        Registered registered =userFacade.getRegistered().get(userName);
        Registered customer =userFacade.getRegistered().get(customerUserName);
        registered.getRoleByStoreId(storeName).getRoleState().getHistoryPurchasesByCustomer();
        return stores.get(storeName).getHistoryPurchasesByCustomer(customer.getId()).stream().map(Purchase::toString).collect(Collectors.joining("\n\n"));
    }

    @Override
    public String getAllHistoryPurchases(String userName, String storeName) throws IllegalAccessException {
        if(!stores.containsKey(storeName)){
            throw new IllegalArgumentException("Store must exist");
        }
        if(!userFacade.getRegistered().containsKey(userName)){
            throw new IllegalArgumentException("User must exist");
        }
        Registered registered =userFacade.getRegistered().get(userName);
        registered.getRoleByStoreId(storeName).getRoleState().getAllHistoryPurchases();

        return stores.get(storeName).getAllHistoryPurchases().stream().map(Purchase::toString).collect(Collectors.joining("\n\n"));

    }

    @Override
    public String requestInformationAboutOfficialsInStore(String userName, String storeName) throws IllegalArgumentException, IllegalAccessException {
        if(!stores.containsKey(storeName)){
            throw new IllegalArgumentException("Store must exist");
        }
        if(!userFacade.getRegistered().containsKey(userName)){
            throw new IllegalArgumentException("User must exist");
        }
        Registered registered =userFacade.getRegistered().get(userName);
        registered.getRoleByStoreId(storeName).getRoleState().requestInformationAboutOfficialsInStore();

        List<String> storeOwners = stores.get(storeName).getOwners();
        List<String> storeManagers = stores.get(storeName).getManagers();


        StringBuilder result = new StringBuilder();
        result.append(storeName).append("\n");
        result.append("Role id username address birthdate").append("\n");
        for (String owner : storeOwners) {
            Registered registered2 =userFacade.getRegistered().get(owner);

            result.append("Owner ").append(registered2.getId()).append(owner).append(registered2.getAddress()).append(registered2.getBirthdate()).append("\n");
        }
        for (String manager : storeManagers) {
            Registered registered2 =userFacade.getRegistered().get(manager);

            result.append("Manager ").append(registered2.getId()).append(manager).append(registered2.getAddress()).append(registered2.getBirthdate()).append("\n");

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
        if(!stores.containsKey(storeName)){
            throw new IllegalArgumentException("Store must exist");
        }
        if(!userFacade.getRegistered().containsKey(userName)){
            throw new IllegalArgumentException("User must exist");
        }
        Registered registered =userFacade.getRegistered().get(userName);
        registered.getRoleByStoreId(storeName).getRoleState().requestManagersPermissions();

        List<String> storeManagers = stores.get(storeName).getManagers();

        StringBuilder result = new StringBuilder();
        result.append(storeName).append("\n\n");
        result.append("Managers :").append("\n");
        result.append("id username watch editSupply editBuyPolicy editDiscountPolicy").append("\n");
        for (String manager : storeManagers) {
            Registered registered2 =userFacade.getRegistered().get(manager);
            RoleState managerRole = registered2.getRoleByStoreId(storeName).getRoleState();
            result.append(registered2.getId()).append(manager).append(managerRole.isWatch()).append(managerRole.isEditSupply()).append(managerRole.isEditBuyPolicy()).append(managerRole.isEditDiscountPolicy()).append('\n');
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
        if(!stores.containsKey(storeName)){
            throw new IllegalArgumentException("Store must exist");
        }
        if(!userFacade.getRegistered().containsKey(userName)){
            throw new IllegalArgumentException("User must exist");
        }
        if(!userFacade.getRegistered().containsKey(officialUserName)){
            throw new IllegalArgumentException("User must exist");
        }

        Registered registered =userFacade.getRegistered().get(userName);
        registered.getRoleByStoreId(storeName).getRoleState().requestInformationAboutSpecificOfficialInStore();


        List<String> storeOwners = stores.get(storeName).getOwners();

        StringBuilder result = new StringBuilder();
        result.append(storeName).append('\n');

        if(storeOwners.contains(officialUserName))
        {
            Registered registered2 =userFacade.getRegistered().get(officialUserName);
            result.append("Role id username address birthdate").append('\n');
            result.append("Owner ").append(registered2.getId()).append(officialUserName).append(registered2.getAddress()).append(registered2.getBirthdate()).append('\n');
        }
        else
        {
            List<String> storeManagers = stores.get(storeName).getManagers();
            if(storeManagers.contains(officialUserName))
            {
                Registered registered2 =userFacade.getRegistered().get(officialUserName);
                RoleState managerRole = registered2.getRoleByStoreId(storeName).getRoleState();
                result.append("Role id username address birthdate watch editSupply editBuyPolicy editDiscountPolicy").append("\n");
                result.append("Manager ").append(registered2.getId()).append(officialUserName).append(registered2.getAddress()).append(registered2.getBirthdate()).append(managerRole.isWatch()).append(managerRole.isEditSupply()).append(managerRole.isEditBuyPolicy()).append(managerRole.isEditDiscountPolicy()).append("\n");
            }
            else
                throw new IllegalArgumentException("User is not employeed in this store.");
        }

        return result.toString();

    }

    @Override
    public HashMap<String, Store> getStores() {
        return stores;
    }


}
