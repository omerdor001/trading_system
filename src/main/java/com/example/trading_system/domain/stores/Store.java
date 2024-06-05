package com.example.trading_system.domain.stores;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@Setter
@Getter
public class Store {
    private static final Logger logger = LoggerFactory.getLogger(Store.class);
    private String nameId; // this will be the ID for the store
    private String description;
    private HashMap<Integer, Product> products;
    @Getter
    private List<String> managers;
    @Getter
    private List<String> owners;
    private String founder;
    private StorePolicy storePolicy;
    @Getter
    @Setter
    private boolean isActive;
    private boolean isOpen;
    private StoreSalesHistory salesHistory;
    @Getter
    @Setter
    private Double storeRating;

    public Store(String nameId, String description, StorePolicy storePolicy, String founder,Double storeRating) {
        this.nameId = nameId;
        this.description = description;
        this.storePolicy = storePolicy;
        this.products = new HashMap<>();
        this.isActive = true;
        this.salesHistory = new StoreSalesHistory();
        this.founder = founder;
        this.managers = new LinkedList<>();
        this.owners = new LinkedList<>();
        this.storeRating=storeRating;
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
    public List<Product> filterProducts(List<Product> productList, Double minPrice, Double maxPrice, Double minRating, Category category,Double storeRating) {
        return productList.stream()
                .filter(p -> minPrice == null || p.getProduct_price() >= minPrice)
                .filter(p -> maxPrice == null || p.getProduct_price() <= maxPrice)
                .filter(p -> minRating == null || p.getRating() >= minRating)
                .filter(p -> storeRating == null || p.getRating() >= storeRating)
                .filter(p -> category == null || p.getCategory() == category)
                .collect(Collectors.toList());
    }

    public List<Product> searchName(String name, Double minPrice, Double maxPrice, Double minRating, Category category,Double storeRating) {
        List<Product> list_products = new ArrayList<>();
        for (Product p : products.values()) {
            if (p.getProduct_name().equals(name))
                list_products.add(p);
        }
        return filterProducts(list_products, minPrice, maxPrice, minRating, category,storeRating);
    }

    public List<Product> searchCategory(Category category, Double minPrice, Double maxPrice, Double minRating,Double storeRating) {
        List<Product> list_products = new ArrayList<>();
        for (Product p : products.values()) {
            if (p.getCategory().equals(category))
                list_products.add(p);
        }
        return filterProducts(list_products, minPrice, maxPrice, minRating, category,storeRating);
    }

    public List<Product> searchKeywords(String keyWords, Double minPrice, Double maxPrice, Double minRating, Category category,Double storeRating) {
        List<Product> list_products = new ArrayList<>();
        for (Product p : products.values()) {
            if (p.getKeyWords().contains(keyWords))
                list_products.add(p);
        }
        return filterProducts(list_products, minPrice, maxPrice, minRating, category,storeRating);
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

    public boolean isProductExist(int productId) {
        return products.containsKey(productId);
    }

    public Product getProduct(int productId) {
        return products.get(productId);
    }

    public synchronized void addProduct(int product_id, String store_name, String product_name, String product_description,
                                        double product_price, int product_quantity, double rating, int category, List<String> keyWords) {
        if(isProductExist(product_id))
            throw new IllegalArgumentException("Product with id " + product_id + " already exists");
        if (product_price < 0)
            throw new IllegalArgumentException("Price can't be negative number");
        if (product_quantity <= 0)
            throw new IllegalArgumentException("Quantity must be natural number");
        if (rating < 0)
            throw new IllegalArgumentException("Rating can't be negative number");
        Product product = new Product(product_id, product_name, product_description, product_price, product_quantity, rating, Category.getCategoryFromInt(category), keyWords);
        products.put(product.getProduct_id(), product);
    }

    public synchronized void removeProduct(int productId) {
        if(!isProductExist(productId))
            throw new IllegalArgumentException("Product with id " + productId + " does not exist");
        products.remove(productId);
    }

    public synchronized void setProductName(int productId, String product_name) {
        Product product = getProduct(productId);
        if (product != null) {
            product.setProduct_name(product_name);
        }
        else
            throw new IllegalArgumentException("Product with id " + productId + " does not exist");
    }

    public synchronized void setProductDescription(int productId, String product_description) {
        Product product = getProduct(productId);
        if (product != null) {
            product.setProduct_description(product_description);
        }
        else
            throw new IllegalArgumentException("Product with id " + productId + " does not exist");
    }

    public synchronized void setProductPrice(int productId, double product_price) {
        if (product_price < 0)
            throw new IllegalArgumentException("Price can't be negative number");
        Product product = getProduct(productId);
        if (product != null) {
            product.setProduct_price(product_price);
        }
        else
            throw new IllegalArgumentException("Product with id " + productId + " does not exist");

    }

    public synchronized void setProductQuantity(int productId, int product_quantity) {
        if (product_quantity <= 0)
            throw new IllegalArgumentException("Quantity must be natural number");
        Product product = getProduct(productId);
        if (product != null) {
            product.setProduct_quantity(product_quantity);
        }
        else
            throw new IllegalArgumentException("Product with id " + productId + " does not exist");
    }

    public synchronized void setRating(int productId, double rating) {
        if (rating < 0)
            throw new IllegalArgumentException("Rating can't be negative number");
        Product product = getProduct(productId);
        if (product != null) {
            product.setRating(rating);
        }
        else
            throw new IllegalArgumentException("Product with id " + productId + " does not exist");

    }

    public synchronized void setCategory(int productId, int category) {
        Product product = getProduct(productId);
        if (product != null) {
            product.setCategory(category);
        }
        else
            throw new IllegalArgumentException("Product with id " + productId + " does not exist");

    }

    List<Purchase> getHistoryPurchasesByCustomer(String customerUsername) {
        return salesHistory.getPurchasesByCustomer(customerUsername);
    }

    List<Purchase> getAllHistoryPurchases() {
        return salesHistory.getAllPurchases();
    }


    public String getNameId() {
        return nameId;
    }

    public HashMap<Integer,Product> getProducts(){     //Added because missing
        return products;
    }

    public void setActive(Boolean active){             //Added because missing
        this.isActive=active;
    }

    public Boolean isActive(){                      //Added because missing
        return isActive;
    }

    public Boolean setOpen(Boolean open){            //Added because missing
        return isOpen;
    }

    public List<String> getOwners(){                //Added because missing
        return owners;
    }

    public List<String> getManagers(){                //Added because missing
        return managers;
    }

    public void addPurchase(Purchase purchase){
        salesHistory.addPurchase(purchase);
    }






}
