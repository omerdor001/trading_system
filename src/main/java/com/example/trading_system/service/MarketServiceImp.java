package com.example.trading_system.service;

import com.example.trading_system.domain.stores.Category;
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

    public String searchNameInStore(String name, String store_name, Double minPrice, Double maxPrice, Double minRating, Category category) {
        logger.info("Trying to search products in store : {} with name : {}", store_name, name);
        String result = marketFacade.searchNameInStore(name, store_name, minPrice, maxPrice, minRating, category);
        logger.info("FINISHED Searching products in store ");
        return result;
    }

    public String searchCategoryInStore(Category category, String store_name, Double minPrice, Double maxPrice, Double minRating) {
        logger.info("Trying to search products in store : {} with category, : {}", store_name, category);
        String result = marketFacade.searchCategoryInStore(category, store_name, minPrice, maxPrice, minRating);
        logger.info("FINISHED Searching products in store ");
        return result;
    }

    public String searchKeywordsInStore(String keyWords, String store_name, Double minPrice, Double maxPrice, Double minRating, Category category) {
        logger.info("Trying to search products in store : {} with keyWords,  : {}", store_name, keyWords);
        String result = marketFacade.searchKeywordsInStore(keyWords, store_name, minPrice, maxPrice, minRating, category);
        logger.info("FINISHED Searching products in store ");
        return result;
    }

    public String searchNameInStores(String name, Double minPrice, Double maxPrice, Double minRating, Category category) {
        logger.info("Trying to search products in stores with name : {}", name);
        String result = marketFacade.searchNameInStores(name, minPrice, maxPrice, minRating, category);
        logger.info("FINISHED Searching products in stores ");
        return result;
    }

    public String searchCategoryInStores(Category category, Double minPrice, Double maxPrice, Double minRating) {
        logger.info("Trying to search products in stores with category, : {}", category);
        String result = marketFacade.searchCategoryInStores(category, minPrice, maxPrice, minRating);
        logger.info("FINISHED Searching products in stores ");
        return result;
    }

    public String searchKeywordsInStores(String keyWords, Double minPrice, Double maxPrice, Double minRating, Category category) {
        logger.info("Trying to search products in stores with keyWords,  : {}", keyWords);
        String result = marketFacade.searchKeywordsInStores(keyWords, minPrice, maxPrice, minRating, category);
        logger.info("FINISHED Searching products in stores ");
        return result;
    }

    public void addProduct(String username, int product_id, String store_name, String product_name, String product_description,
                           double product_price, int product_quantity, double rating, int category, List<String> keyWords) throws IllegalAccessException {
        logger.info("Trying to add products to store : {}", store_name);
        marketFacade.addProduct(username, product_id, store_name, product_name, product_description, product_price, product_quantity, rating, category, keyWords);
        logger.info("Finished add products to store : {}", store_name);
    }

    public void removeProduct(String username, String store_name, int product_id) throws IllegalAccessException {
        logger.info("Trying to remove products to store : {}", store_name);
        marketFacade.removeProduct(username, store_name, product_id);
        logger.info("Finished remove products to store : {}", store_name);
    }

    public void setProductName(String username, String store_name_id, int productId, String product_name) throws IllegalAccessException {
        logger.info("Trying to edit name to product : {}", productId);
        marketFacade.setProductName(username, store_name_id, productId, product_name);
        logger.info("Finished edit name of product : {}", productId);

    }

    public void setProductDescription(String username, String store_name_id, int productId, String product_description) throws IllegalAccessException {
        logger.info("Trying to edit description to product : {}", productId);
        marketFacade.setProductDescription(username, store_name_id, productId, product_description);
        logger.info("Finished edit description of product : {}", productId);
    }

    public void setProductPrice(String username, String store_name_id, int productId, int product_price) throws IllegalAccessException {
        logger.info("Trying to edit price to product : {}", productId);
        marketFacade.setProductPrice(username, store_name_id, productId, product_price);
        logger.info("Finished edit price of product : {}", productId);
    }

    public void setProductQuantity(String username, String store_name_id, int productId, int product_quantity) throws IllegalAccessException {
        logger.info("Trying to edit quantity to product : {}", productId);
        marketFacade.setProductQuantity(username, store_name_id, productId, product_quantity);
        logger.info("Finished edit quantity of product : {}", productId);
    }

    public void setRating(String username, String store_name_id, int productId, int rating) throws IllegalAccessException {
        logger.info("Trying to edit rating to product : {}", productId);
        marketFacade.setRating(username, store_name_id, productId, rating);
        logger.info("Finished edit rating of product : {}", productId);
    }

    public void setCategory(String username, String store_name_id, int productId, int category) throws IllegalAccessException {
        logger.info("Trying to edit category to product : {}", productId);
        marketFacade.setCategory(username, store_name_id, productId, category);
        logger.info("Finished edit category of product : {}", productId);
    }

    @Override
    public void closeStoreExist(String storeName) {
        logger.info("Trying to close store with name : {}", storeName);
        marketFacade.openStoreExist(storeName);
        logger.error("Failed on closing Store with name : {}", storeName);
    }
}




