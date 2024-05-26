package com.example.trading_system.domain.stores;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import java.util.stream.Collectors;

@Setter
@Getter
public class Store {

    private String nameId;//this will be the ID for the store
    private HashMap<Integer, Product> products;
    private List<String> managers;
    private List<String> owners;
    private String founder;

    private String name_id; // this will be the ID for the store
    private String description;
    private ConcurrentHashMap<Integer, Product> products;
    private StorePolicy storePolicy;
    private boolean isActive;
    private boolean isOpen;
    private StoreSalesHistory salesHistory;
    private static final Logger logger = LoggerFactory.getLogger(Store.class);

    public Store(String nameId, String description, StorePolicy storePolicy, String founder) {
        this.nameId = nameId;
        this.description = description;
        this.storePolicy = storePolicy;

        this.products = new HashMap<>();
        this.isActive = true;
        this.salesHistory = new StoreSalesHistory();
        this.founder = founder;
        this.managers = new LinkedList<>();
        this.owners = new LinkedList<>();

        this.products = new ConcurrentHashMap<>();

    }

    public List<Product> filterProducts(List<Product> productList, Double minPrice, Double maxPrice, Double minRating, Category category) {
        return productList.stream()
                .filter(p -> minPrice == null || p.getProduct_price() >= minPrice)
                .filter(p -> maxPrice == null || p.getProduct_price() <= maxPrice)
                .filter(p -> minRating == null || p.getRating() >= minRating)
                .filter(p -> category == null || p.getCategory() == category)
                .collect(Collectors.toList());
    }

    public List<Product> searchName(String name, Double minPrice, Double maxPrice, Double minRating, Category category) {
        List<Product> list_products = new ArrayList<>();
        for (Product p : products.values()) {
            if (p.getProduct_name().equals(name))
                list_products.add(p);
        }
        return filterProducts(list_products, minPrice, maxPrice, minRating, category);
    }

    public List<Product> searchCategory(Category category, Double minPrice, Double maxPrice, Double minRating) {
        List<Product> list_products = new ArrayList<>();
        for (Product p : products.values()) {
            if (p.getCategory().equals(category))
                list_products.add(p);
        }
        return filterProducts(list_products, minPrice, maxPrice, minRating, category);
    }

    public List<Product> searchKeywords(String keyWords, Double minPrice, Double maxPrice, Double minRating, Category category) {
        List<Product> list_products = new ArrayList<>();
        for (Product p : products.values()) {
            if (p.getKeyWords().contains(keyWords))
                list_products.add(p);
        }
        return filterProducts(list_products, minPrice, maxPrice, minRating, category);
    }

    @Override
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

    public synchronized void addProductToStore(Product product) {
        products.put(product.getProduct_id(), product);
    }

    public synchronized void addProduct(int product_id, String store_name, String product_name, String product_description,
                                        double product_price, int product_quantity, double rating, Category category, List<String> keyWords) {
        Product product = new Product(product_id, product_name, product_description, product_price, product_quantity, rating, category, keyWords);
        products.put(product.getProduct_id(), product);
    }

    public synchronized void removeProduct(int productId) {
        products.remove(productId);
    }

    public synchronized void setProduct_name(int productId, String product_name) {
        Product product = getProduct(productId);
        if (product != null) {
            product.setProduct_name(product_name);
        }
    }

    public synchronized void setProduct_description(int productId, String product_description) {
        Product product = getProduct(productId);
        if (product != null) {
            product.setProduct_description(product_description);
        }
    }

    public synchronized void setProduct_price(int productId, int product_price) {
        Product product = getProduct(productId);
        if (product != null) {
            product.setProduct_price(product_price);
        }

    }

    public synchronized void setProduct_quantity(int productId, int product_quantity) {
        Product product = getProduct(productId);
        if (product != null) {
            product.setProduct_quantity(product_quantity);
        }
    }

    public synchronized void setRating(int productId, int rating) {
        Product product = getProduct(productId);
        if (product != null) {
            product.setRating(rating);
        }
    }
    List<Purchase> getHistoryPurchasesByCustomer(int customerId){
        return salesHistory.getPurchasesByCustomer(customerId);
    }

    List<Purchase> getAllHistoryPurchases(){
        return salesHistory.getAllPurchases();
    }



    public List<String> getManagers() {
        return managers;
    }

    public List<String> getOwners() {
        return owners;

    public synchronized void setCategory(int productId, Category category) {
        Product product = getProduct(productId);
        if (product != null) {
            product.setCategory(category);
        }
    }
}
