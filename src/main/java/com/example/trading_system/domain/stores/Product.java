package com.example.trading_system.domain.stores;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
@Getter
@Entity
@Table(name = "products")
public class Product {
    private static final Logger logger = LoggerFactory.getLogger(Product.class);

    @Id
    @Column(nullable = false, unique = true)
    private int product_id;

    @Column(name = "store_name")
    private String store_name;

    @Column
    private String product_name;

    @Column
    private String product_description;

    @Column
    private double product_price;

    @Column
    private volatile int product_quantity;

    @Column
    private double rating;

    @Enumerated(EnumType.STRING)
    private Category category;

   // @ManyToOne
   // @JoinColumn(name = "store_id")
   // private Store store;

    @ElementCollection
    @CollectionTable(name = "product_keywords", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "keyword")
    private List<String> keyWords;


    public Product(int product_id, String product_name, String product_description, double product_price, int product_quantity, double rating, Category category, List<String> keyWords) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.product_description = product_description;
        this.product_price = product_price;
        this.product_quantity = product_quantity;
        this.rating = rating;
        this.store_name = "";
        this.category = category;
        this.keyWords = keyWords;
    }

    public Product() {

    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public void setProduct_price(double product_price) {
        this.product_price = product_price;
    }

    public void setProduct_quantity(int product_quantity) {
        this.product_quantity = product_quantity;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public void setCategory(int category) {
        this.category = Category.values()[category];
    }

    public void addKeyWord(String keyWord) {
        this.keyWords.add(keyWord);
    }

    public void removeKeyWord(String keyWord) {
        this.keyWords.remove(keyWord);
    }

    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        String keyWordsJson;
        try {
            keyWordsJson = objectMapper.writeValueAsString(keyWords);
        } catch (JsonProcessingException e) {
            keyWordsJson = "[]"; // default to empty array in case of error
        }

        return "{" +
                "\"product_id\":" + product_id +
                ", \"store_name\":\"" + store_name + "\"" +
                ", \"product_name\":\"" + product_name + "\"" +
                ", \"product_description\":\"" + product_description + "\"" +
                ", \"product_price\":" + product_price +
                ", \"product_quantity\":" + product_quantity +
                ", \"rating\":" + rating +
                ", \"category\":\"" + (category != null ? category.toString() : "null") + "\"" +
                ", \"keyWords\":" + keyWordsJson +
                "}";
    }

    public synchronized void releaseReservedProducts(int quantity) {
        this.product_quantity += quantity;
        logger.info("Released {} products. New quantity: {}", quantity, this.product_quantity);
    }

    public synchronized void removeReservedProducts(int quantity) {
        if (this.product_quantity < quantity) {
            logger.error("Insufficient product quantity in store for product ID: {}", product_id);
            throw new RuntimeException("Product quantity is too low");
        }
        this.product_quantity -= quantity;
        logger.info("Removed {} products. New quantity: {}", quantity, this.product_quantity);
    }

    public synchronized void checkAvailabilityAndConditions(int quantity) {
        if (this.product_quantity < quantity) {
            logger.error("Insufficient product quantity in store for product ID: {}", product_id);
            throw new RuntimeException("Product quantity is too low");
        }
    }

    public void editProduct(String productName, String productDescription, double productPrice, int productQuantity) {
        this.product_name = productName;
        this.product_description = productDescription;
        this.product_price = productPrice;
        this.product_quantity = productQuantity;
    }
    public static List<String> getAllCategories() {
        return Category.getCategoriesString();
    }

    public static String getCategoryStringFromInt(int category) {
        return Category.getCategoryFromInt(category).toString();
    }


}