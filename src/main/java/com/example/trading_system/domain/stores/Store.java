package com.example.trading_system.domain.stores;

import com.example.trading_system.domain.users.Manager;
import com.example.trading_system.domain.users.ManagerPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.*;
@Getter
@Setter
@Builder
public class Store {
    private String nameId;//this will be the ID for the store
    private String description;
    @Getter
    private HashMap<Integer, Product> products;
    private StorePolicy storePolicy;

    private ArrayList<Manager> storeManagers;
    private ManagerPermission defaultManagerPermission;

    private static final Logger logger = LoggerFactory.getLogger(Store.class);

    public Store(String nameId, String description, StorePolicy storePolicy) {
        this.nameId = nameId;
        this.description = description;
        this.storePolicy = storePolicy;
        this.products = new HashMap<>();
    }

    private List<Product> filterProducts(List<Product> productList, Double minPrice, Double maxPrice, Double minRating, Category category) {
        return productList.stream()
                .filter(p -> minPrice == null || p.getPrice() >= minPrice)
                .filter(p -> maxPrice == null || p.getPrice() <= maxPrice)
                .filter(p -> minRating == null || p.getRating() >= minRating)
                .filter(p -> category == null || p.getCategory() == category)
                .collect(Collectors.toList());
    }

    List<Product> searchName(String name, Double minPrice, Double maxPrice, Double minRating, Category category) {
        List<Product> productList = products.values().stream().filter(p -> p.getName().equals(name)).collect(Collectors.toList());
        return filterProducts(productList, minPrice, maxPrice, minRating, category);
    }

    List<Product> searchCategory(Category category, Double minPrice, Double maxPrice, Double minRating) {
        List<Product> productsList = products.values().stream().filter(p -> p.getCategory().equals(category)).collect(Collectors.toList());
        return filterProducts(productsList, minPrice, maxPrice, minRating, category);
    }

    List<Product> searchKeywords(String keyWords, Double minPrice, Double maxPrice, Double minRating, Category category) {
        List<Product> productsList = products.values().stream().filter(p -> p.getKeyWords().contains(keyWords)).collect(Collectors.toList());
        return filterProducts(productsList, minPrice, maxPrice, minRating, category);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"name_id\":\"").append(nameId).append("\"");
        sb.append(", \"description\":\"").append(description).append("\"");
        sb.append(", \"products\":[");

        for (Product p : products.values()) {
            sb.append(p.toString());
            sb.append(", ");
        }

        sb.append("]");
        sb.append("}");
        return sb.toString();
    }

    public Product getProduct(int productId) {
        return products.get(productId);
    }

    public void addProductToStore(Product product) {
        products.put(product.getId(), product);
    }

}
