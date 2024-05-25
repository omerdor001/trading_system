package com.example.trading_system.domain.stores;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ProductInSale {
    private String productName;
    private double price;
    private int quantity;

    double getSumPrice() {
        return price * quantity;
    }

}
