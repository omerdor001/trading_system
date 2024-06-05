package com.example.trading_system.domain.stores;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ProductInSale {
    private int id;
    private double price;
    private int quantity;

    @Override
    public String toString() {
        return "Product: " + id + ", Quantity: " + quantity + ", Price: $" + price;
    }

}
