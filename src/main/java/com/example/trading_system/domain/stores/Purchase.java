package com.example.trading_system.domain.stores;

import lombok.Getter;

import java.util.List;

@Getter
public class Purchase {
    private final List<ProductInSale> productInSaleList;
    private int customerId;
    private double totalPrice;

    public Purchase(int customerId, List<ProductInSale> productInSaleList) {
        this.customerId = customerId;
        this.productInSaleList = productInSaleList;
        this.totalPrice = productInSaleList.stream().mapToDouble(ProductInSale::getSumPrice).sum();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Client ID: ").append(customerId).append(", Total Price: $").append(totalPrice).append("\n");
        builder.append("Products:\n");
        for (ProductInSale product : productInSaleList) {
            builder.append(product.toString()).append("\n");
        }
        return builder.toString();
    }
}
