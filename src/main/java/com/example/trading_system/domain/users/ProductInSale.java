package com.example.trading_system.domain.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
@Entity
@Getter
@Setter
public class ProductInSale {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;

    @Column(nullable = false)
    private String storeId;

    @Column(nullable = false)
    private int id;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int category;

    @ManyToOne
    @JoinColumn(name = "shopping_bag_id", referencedColumnName = "id")
    private ShoppingBag shoppingBag;


    public ProductInSale(String storeId, int productId, double price, int quantity, int category) {
        this.storeId=storeId;
        this.id=productId;
        this.price=price;
        this.quantity=quantity;
        this.category=category;
    }

    public ProductInSale() {

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


    public int getQuantity() {
        return quantity;
    }

    public String getStoreId() {
        return storeId;
    }

    public int getId() {
        return id;
    }
}
