package com.example.trading_system.domain.stores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

public class MarketFacadeImp {
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
}
