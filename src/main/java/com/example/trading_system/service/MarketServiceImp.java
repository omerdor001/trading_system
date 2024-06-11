package com.example.trading_system.service;
import com.example.trading_system.domain.stores.MarketFacade;
import com.example.trading_system.domain.stores.MarketFacadeImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MarketServiceImp implements MarketService {
    private static final Logger logger = LoggerFactory.getLogger(MarketServiceImp.class);
    private static MarketServiceImp instance = null;
    private MarketFacade marketFacade;

    private MarketServiceImp() {
        marketFacade = MarketFacadeImp.getInstance();
    }

    public static MarketServiceImp getInstance() {
        if (instance == null)
            instance = new MarketServiceImp();
        return instance;
    }

    @Override
    public void deleteInstance() {
        instance = null;
        this.marketFacade.deleteInstance();
        this.marketFacade = null;
    }

    @Override
    public String getAllStores() {
        logger.info("Trying to Gather All Stores");
        String result = marketFacade.getAllStores();
        logger.info("FINISHED Gather All Stores Info");
        return result;
    }

    @Override
    public void openStoreExist(String storeName) {
        logger.info("Trying to open store with name : {}", storeName);
        try {
            marketFacade.openStoreExist(storeName);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed on opening Store with name : {}", e.getMessage(), storeName);
        }
    }

/*
    @Override
    public void closeStoreExist(String storeName) {
        logger.info("Trying to close store with name : {}", storeName);
        try {
            marketFacade.openStoreExist(storeName);

        }
        catch (Exception e) {
            logger.error("Error occurred : {} , Failed on closing Store with name : {}", e.getMessage(), storeName);
        }
    }
*/

    public String getStoreProducts(String store_name) {
        logger.info("Trying to Gather ALL Store Products");
        String result = marketFacade.getStoreProducts(store_name);
        logger.info("FINISHED Gather ALL Store Products Info");
        return result;
    }


    public String getProductInfo(String store_name, int product_Id) {
        logger.info("Trying to Gather Product Info with Store Id : {} and product ID: {}", store_name, product_Id);
        String result = marketFacade.getProductInfo(store_name, product_Id);
        logger.info("FINISHED Gather Product Info");
        return result;
    }

    public String searchNameInStore(String name, String store_name, Double minPrice, Double maxPrice, Double minRating, int category) {
        logger.info("Trying to search products in store : {} with name : {}", store_name, name);
        String result = marketFacade.searchNameInStore(name, store_name, minPrice, maxPrice, minRating, category);
        logger.info("FINISHED Searching products in store ");
        return result;
    }

    public String searchCategoryInStore(int category, String store_name, Double minPrice, Double maxPrice, Double minRating) {
        logger.info("Trying to search products in store : {} with category, : {}", store_name, category);
        String result = marketFacade.searchCategoryInStore(category, store_name, minPrice, maxPrice, minRating);
        logger.info("FINISHED Searching products in store ");
        return result;
    }

    public String searchKeywordsInStore(String keyWords, String store_name, Double minPrice, Double maxPrice, Double minRating, int category) {
        logger.info("Trying to search products in store : {} with keyWords,  : {}", store_name, keyWords);
        String result = marketFacade.searchKeywordsInStore(keyWords, store_name, minPrice, maxPrice, minRating, category);
        logger.info("FINISHED Searching products in store ");
        return result;
    }

    public String searchNameInStores(String name, Double minPrice, Double maxPrice, Double minRating, int category,Double storeRating) {
        logger.info("Trying to search products in stores with name : {}", name);
        String result = marketFacade.searchNameInStores(name, minPrice, maxPrice, minRating, category,storeRating);
        logger.info("FINISHED Searching products in stores ");
        return result;
    }

    public String searchCategoryInStores(int category, Double minPrice, Double maxPrice, Double minRating,Double storeRating) {
        logger.info("Trying to search products in stores with category, : {}", category);
        String result = marketFacade.searchCategoryInStores(category, minPrice, maxPrice, minRating,storeRating);
        logger.info("FINISHED Searching products in stores ");
        return result;
    }

    public String searchKeywordsInStores(String keyWords, Double minPrice, Double maxPrice, Double minRating, int category,Double storeRating) {
        logger.info("Trying to search products in stores with keyWords,  : {}", keyWords);
        String result = marketFacade.searchKeywordsInStores(keyWords, minPrice, maxPrice, minRating, category,storeRating);
        logger.info("FINISHED Searching products in stores ");
        return result;
    }

    public void addProduct(String username, int product_id, String store_name, String product_name, String product_description,
                           double product_price, int product_quantity, double rating, int category, List<String> keyWords) throws IllegalAccessException {
        marketFacade.addProduct(username, product_id, store_name, product_name, product_description, product_price, product_quantity, rating, category, keyWords);
    }

    public void removeProduct(String username, String store_name, int product_id) throws IllegalAccessException {
        marketFacade.removeProduct(username, store_name, product_id);
    }

    public void setProductName(String username, String store_name, int productId, String product_name) throws IllegalAccessException {
        marketFacade.setProductName(username, store_name, productId, product_name);
    }

    public void setProductDescription(String username, String store_name, int productId, String product_description) throws IllegalAccessException {
        marketFacade.setProductDescription(username, store_name, productId, product_description);
    }

    public void setProductPrice(String username, String store_name, int productId, double product_price) throws IllegalAccessException {
        marketFacade.setProductPrice(username, store_name, productId, product_price);
    }

    public void setProductQuantity(String username, String store_name, int productId, int product_quantity) throws IllegalAccessException {
        marketFacade.setProductQuantity(username, store_name, productId, product_quantity);
    }

    public void setRating(String username, String store_name, int productId, double rating) throws IllegalAccessException {
        marketFacade.setRating(username, store_name, productId, rating);
    }

    public void setCategory(String username, String store_name, int productId, int category) throws IllegalAccessException {
        marketFacade.setCategory(username, store_name, productId, category);
    }

    @Override
    public void closeStoreExist(String storeName) {
        logger.info("Trying to close store with name : {}", storeName);
        marketFacade.openStoreExist(storeName);
        logger.error("Failed on closing Store with name : {}", storeName);
    }
}




