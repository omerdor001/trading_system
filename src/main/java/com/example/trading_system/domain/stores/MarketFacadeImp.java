package com.example.trading_system.domain.stores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class MarketFacadeImp implements MarketFacade {
    private HashMap<String, Store> stores;
    private static final Logger logger = LoggerFactory.getLogger(Store.class);


    public MarketFacadeImp() {
        stores = new HashMap<>();
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

    public String searchNameInStores(String name, Double minPrice, Double maxPrice, Double minRating, Category category) {
        StringBuilder sb = new StringBuilder();
        for (Store store : stores.values()) {
            try {
                sb.append(store.searchName(name, minPrice, maxPrice, minRating, category).toString());
            } catch (Exception e) {
                logger.error("Error occurred while searching products in store '{}': {}", store.getName_id(), e.getMessage());
            }
        }
        return sb.toString();
    }

    public String searchCategoryInStores(Category category, Double minPrice, Double maxPrice, Double minRating) {
        StringBuilder sb = new StringBuilder();
        for (Store store : stores.values()) {
            try {
                sb.append(store.searchCategory(category, minPrice, maxPrice, minRating).toString());
            } catch (Exception e) {
                logger.error("Error occurred while searching products in store '{}': {}", store.getName_id(), e.getMessage());
            }
        }
        return sb.toString();
    }

    public String searchKeywordsInStores(String keyWords, Double minPrice, Double maxPrice, Double minRating, Category category) {
        StringBuilder sb = new StringBuilder();
        for (Store store : stores.values()) {
            try {
                sb.append(store.searchKeywords(keyWords, minPrice, maxPrice, minRating, category).toString());
            } catch (Exception e) {
                logger.error("Error occurred while searching products in store '{}': {}", store.getName_id(), e.getMessage());
            }
        }

        return sb.toString();
    }

}
