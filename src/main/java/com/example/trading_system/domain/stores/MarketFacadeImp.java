package com.example.trading_system.domain.stores;

import com.example.trading_system.domain.users.Registered;
import com.example.trading_system.domain.users.UserFacade;
import com.example.trading_system.domain.users.UserFacadeImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

public class MarketFacadeImp implements MarketFacade{
    private HashMap<String, Store> stores;
    private UserFacade userFacade;
    private static final Logger logger = LoggerFactory.getLogger(MarketFacadeImp.class);


    public MarketFacadeImp() {
        stores = new HashMap<>();
        userFacade=new UserFacadeImp();
    }
    private  static class Singleton  {
        private static final MarketFacadeImp INSTANCE = new MarketFacadeImp();
    }
    public static MarketFacadeImp getInstance() {
        return Singleton.INSTANCE;
    }

    public void addStore(Store store) {
        stores.put(store.getName_id(), store);
    }

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

    public String getStoreProducts(String store_name) {
        return stores.get(store_name).toString();
    }

    public String getProductInfo(String store_name, int product_id) {
        return stores.get(store_name).getProduct(product_id).toString();
    }

    public String searchNameInStore(String name, String store_name, Double minPrice, Double maxPrice, Double minRating, Category category) {
        if (name == null) {
            logger.error("No name provided");
            throw new IllegalArgumentException("No name provided");
        }
        if (stores.get(store_name).getProducts().isEmpty()) {
            logger.warn("No products Available");
            return "{}";
        }
        return stores.get(store_name).searchName(name, minPrice, maxPrice, minRating, category).toString();
    }

    public String searchCategoryInStore(Category category, String store_name, Double minPrice, Double maxPrice, Double minRating) {
        if (category == null) {
            logger.error("No category provided");
            throw new IllegalArgumentException("No category provided");
        }
        if (stores.get(store_name).getProducts().isEmpty()) {
            logger.warn("No products Available");
            return "{}";
        }
        if (!EnumSet.allOf(Category.class).contains(category)) {
            logger.error("Category is not a valid category");
            throw new RuntimeException("Category is not a valid category");
        }

        return stores.get(store_name).searchCategory(category, minPrice, maxPrice, minRating).toString();
    }

    public String searchKeywordsInStore(String keyWords, String store_name, Double minPrice, Double maxPrice, Double minRating, Category category) {
        if (keyWords == null) {
            logger.error("No keywords provided");
            throw new IllegalArgumentException("No keywords provided");
        }
        if (stores.get(store_name).getProducts().isEmpty()) {
            logger.warn("No products Available");
            return "{}";
        }
        return stores.get(store_name).searchKeywords(keyWords, minPrice, maxPrice, minRating, category).toString();
    }

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
    public boolean addProduct(String username, int product_id, String store_name, String product_name, String product_description,
                              double product_price, int product_quantity, double rating, Category category, List<String> keyWords) throws IllegalAccessException {
        if(!stores.containsKey(store_name)){
            throw new IllegalArgumentException("Store must exist");
        }
        if(product_price<0)
            throw new IllegalArgumentException("Price can't be negative number");
        if(product_quantity<=0)
            throw new IllegalArgumentException("Quantity must be natural number");
        if(rating<0)
            throw new IllegalArgumentException("Rating can't be negative number");
        if(!userFacade.getRegisters().containsKey(username)){
            throw new IllegalArgumentException("User must exist");
        }
        Registered registered =userFacade.getRegisters().get(username);
        registered.getRoleByStoreId(store_name).addProduct(username,product_id,store_name,product_name,product_description,product_price,product_quantity,rating,category,keyWords);
        stores.get(store_name).addProduct(product_id,store_name,product_name,product_description,product_price,product_quantity,rating,category,keyWords);
        return true;
    }

    @Override
    public boolean removeProduct(String username,  String store_name_id,int product_id) throws IllegalAccessException {
        if(!stores.containsKey(store_name_id)){
            throw new IllegalArgumentException("Store must exist");
        }
        if(!userFacade.getRegisters().containsKey(username)){
            throw new IllegalArgumentException("User must exist");
        }
        Registered registered =userFacade.getRegisters().get(username);
        registered.getRoleByStoreId(store_name_id).removeProduct(username,store_name_id,product_id);
        if(!stores.get(store_name_id).getProducts().containsKey(product_id)) {
            throw new IllegalArgumentException("Product must exist");
        }
        stores.get(store_name_id).removeProduct(product_id);
        return true;
    }

    @Override
    public boolean setProduct_name(String username,String store_name_id,int productId,String product_name) throws IllegalAccessException {
        if(!stores.containsKey(store_name_id)){
            throw new IllegalArgumentException("Store must exist");
        }
        if(!stores.get(store_name_id).getProducts().containsKey(productId)) {
            throw new IllegalArgumentException("Product must exist");
        }
        if(!userFacade.getRegisters().containsKey(username)){
            throw new IllegalArgumentException("User must exist");
        }
        Registered registered =userFacade.getRegisters().get(username);
        registered.getRoleByStoreId(store_name_id).setProduct_name(username,store_name_id,productId,product_name);
        stores.get(store_name_id).setProduct_name(productId,product_name);
        return true;
    }

    @Override
    public boolean setProduct_description(String username,String store_name_id,int productId,String product_description) throws IllegalAccessException {
        if(!stores.containsKey(store_name_id)){
            throw new IllegalArgumentException("Store must exist");
        }
        if(!stores.get(store_name_id).getProducts().containsKey(productId)) {
            throw new IllegalArgumentException("Product must exist");
        }
        if(!userFacade.getRegisters().containsKey(username)){
            throw new IllegalArgumentException("User must exist");
        }
        Registered registered =userFacade.getRegisters().get(username);
        registered.getRoleByStoreId(store_name_id).setProduct_description(username,store_name_id,productId,product_description);
        stores.get(store_name_id).setProduct_description(productId,product_description);
        return true;
    }

    @Override
    public boolean setProduct_price(String username,String store_name_id,int productId,int product_price) throws IllegalAccessException {
        if(!stores.containsKey(store_name_id)){
            throw new IllegalArgumentException("Store must exist");
        }
        if(!stores.get(store_name_id).getProducts().containsKey(productId)) {
            throw new IllegalArgumentException("Product must exist");
        }
        if(product_price<0)
            throw new IllegalArgumentException("Price can't be negative number");
        if(!userFacade.getRegisters().containsKey(username)){
            throw new IllegalArgumentException("User must exist");
        }
        Registered registered =userFacade.getRegisters().get(username);
        registered.getRoleByStoreId(store_name_id).setProduct_price(username,store_name_id,productId,product_price);
        stores.get(store_name_id).setProduct_price(productId,product_price);
        return true;
    }

    @Override
    public boolean setProduct_quantity(String username,String store_name_id,int productId,int product_quantity) throws IllegalAccessException {
        if(!stores.containsKey(store_name_id)){
            throw new IllegalArgumentException("Store must exist");
        }
        if(!stores.get(store_name_id).getProducts().containsKey(productId)) {
            throw new IllegalArgumentException("Product must exist");
        }
        if(product_quantity<=0)
            throw new IllegalArgumentException("Quantity must be natural number");
        if(!userFacade.getRegisters().containsKey(username)){
            throw new IllegalArgumentException("User must exist");
        }
        Registered registered =userFacade.getRegisters().get(username);
        registered.getRoleByStoreId(store_name_id).setProduct_quantity(username,store_name_id,productId,product_quantity);
        stores.get(store_name_id).setProduct_quantity(productId,product_quantity);
        return true;
    }

    @Override
    public boolean setRating(String username,String store_name_id,int productId,int rating) throws IllegalAccessException {
        if(!stores.containsKey(store_name_id)){
            throw new IllegalArgumentException("Store must exist");
        }
        if(!stores.get(store_name_id).getProducts().containsKey(productId)) {
            throw new IllegalArgumentException("Product must exist");
        }
        if(rating<0)
            throw new IllegalArgumentException("Rating can't be negative number");
        if(!userFacade.getRegisters().containsKey(username)){
            throw new IllegalArgumentException("User must exist");
        }
        Registered registered =userFacade.getRegisters().get(username);
        registered.getRoleByStoreId(store_name_id).setRating(username,store_name_id,productId,rating);
        stores.get(store_name_id).setRating(productId,rating);
        return true;
    }

    @Override
    public boolean setCategory(String username,String store_name_id,int productId,Category category) throws IllegalAccessException {
        if(!stores.containsKey(store_name_id)){
            throw new IllegalArgumentException("Store must exist");
        }
        if(!stores.get(store_name_id).getProducts().containsKey(productId)) {
            throw new IllegalArgumentException("Product must exist");
        }
        if(!userFacade.getRegisters().containsKey(username)){
            throw new IllegalArgumentException("User must exist");
        }
        Registered registered =userFacade.getRegisters().get(username);
        registered.getRoleByStoreId(store_name_id).setCategory(username,store_name_id,productId,category);
        stores.get(store_name_id).setCategory(productId,category);
        return true;
    }

    public HashMap<String, Store> getStores() {
        return stores;
    }




}
