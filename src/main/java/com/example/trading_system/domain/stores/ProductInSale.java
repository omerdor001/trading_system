package com.example.trading_system.domain.stores;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductInSale {
    @Getter
    @Setter
    private String storeId;
    @Getter
    @Setter
    private int id;
    @Getter
    @Setter
    private double price;
    @Getter
    @Setter
    private int quantity;
    @Getter
    @Setter
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
