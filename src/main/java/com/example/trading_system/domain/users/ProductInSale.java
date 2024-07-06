package com.example.trading_system.domain.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ProductInSale {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;

    @Getter
    @Setter
    @Column(nullable = false)
    private String storeId;

    @Getter
    @Setter
    @Column(nullable = false)
    private int id;

    @Getter
    @Setter
    @Column(nullable = false)
    private double price;

    @Getter
    @Setter
    @Column(nullable = false)
    private int quantity;

    @Getter
    @Setter
    @Column(nullable = false)
    private int category;

    public ProductInSale(String storeId, int productId, double price, int quantity, int category) {
        this.storeId=storeId;
        this.id=productId;
        this.price=price;
        this.quantity=quantity;
        this.category=category;
    }

    @Override
    public String toString() {
        return "Product: " + id + ", Category: " + category + ", Quantity: " + quantity + ", Price: $" + price + ", Store: " + storeId;
    }

    public void addQuantity(int newQuantity) {
        this.quantity += newQuantity;
    }

    public void reduceQuantity(int newQuantity) {
        this.quantity -= newQuantity;
    }

    public double sumTotalPrice() {
        return quantity * price;
    }

}
