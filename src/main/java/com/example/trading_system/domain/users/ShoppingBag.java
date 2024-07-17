package com.example.trading_system.domain.users;

import com.example.trading_system.domain.stores.MarketFacadeImp;
import com.example.trading_system.domain.stores.StoreRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "shopping_bags")
public class ShoppingBag {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    @Column(nullable = false)
    private String storeId;

    @Setter
    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;
    @Getter
    @OneToMany(mappedBy = "shoppingBag", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Map<Integer, ProductInSale> products_list = new HashMap<>();


    public ShoppingBag(String storeId, Cart cart) {
        this.storeId = storeId;
        products_list = new HashMap<>();
        this.cart=cart;
    }

    public ShoppingBag() {

    }


    public synchronized void addProduct(int productId, int quantity, double price, int category) {
        if (products_list.containsKey(productId)) {
            products_list.get(productId).addQuantity(quantity);
        } else products_list.put(productId, new ProductInSale(storeId, productId, price, quantity, category));
    }

    public synchronized void removeProduct(int productId, int quantity) {
        if (!products_list.containsKey(productId)) {
            throw new IllegalArgumentException("Product not found in shopping bag.");
        }
        if (products_list.get(productId).getQuantity() - quantity <= 0) {
            products_list.remove(productId);
        } else products_list.get(productId).reduceQuantity(quantity);
    }

    public double calculateTotalPrice() {
        double total = 0;
        for (ProductInSale product : products_list.values()) {
            total += product.sumTotalPrice();
        }
        return total;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ShoppingBag: {storeId: ").append(storeId).append(", products_list: [");
        for (ProductInSale product : products_list.values()) {
            sb.append("\n    ").append(product.toString());
        }
        sb.append("\n  ]");
        sb.append(", Total Price: $").append(calculateTotalPrice());
        sb.append("}");
        return sb.toString();
    }

    public int checkProductQuantity(int productId) {
        ProductInSale productInSale = products_list.get(productId);
        if(productInSale == null)
            return 0;
        return products_list.get(productId).getQuantity();
    }

    public void removeReservedProducts(StoreRepository storeRepository) {
        for (ProductInSale product : products_list.values()) {
            MarketFacadeImp.getInstance(storeRepository).removeReservedProducts(product.getId(), product.getQuantity(), product.getStoreId());
        }
    }

    public void releaseReservedProducts(StoreRepository storeRepository) {
        for (ProductInSale product : products_list.values()) {
            MarketFacadeImp.getInstance(storeRepository).releaseReservedProducts(product.getId(), product.getQuantity(), product.getStoreId());
        }
    }

    public void checkAvailabilityAndConditions(StoreRepository storeRepository) {
        for (ProductInSale product : products_list.values()) {
            MarketFacadeImp.getInstance(storeRepository).checkAvailabilityAndConditions(product.getId(), product.getQuantity(), product.getStoreId());
        }
    }

    public void addPurchase(StoreRepository storeRepository, String username) {
        try {
            MarketFacadeImp.getInstance(storeRepository).addPurchase(username, productsListToJson(), calculateTotalPrice(), storeId);
        } catch (IOException e) {
            throw new RuntimeException("error converting shopping bag to json");
        }
    }


    public String productsListToJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(products_list.values());
    }

}
