package com.example.trading_system.domain.stores;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
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
