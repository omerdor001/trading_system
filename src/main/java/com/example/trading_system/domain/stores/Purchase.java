package com.example.trading_system.domain.stores;

import lombok.Getter;

import java.util.List;

@Getter

public class Purchase {
    private List<ProductInSale> productInSaleList;
    @Getter
    private int customerId;
    private double totalPrice;
    @Getter
    private String storeName;

    public Purchase(int customerId, List<ProductInSale> productInSaleList, String storeName,double totalPrice) {
        this.customerId = customerId;
        this.productInSaleList = productInSaleList;
        this.storeName = storeName;
        this.totalPrice = totalPrice;
    }


    public void addProduct(ProductInSale product) {
        productInSaleList.add(product);
        totalPrice += product.getPrice();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Store Name: ").append(storeName).append("\n");
        builder.append("Client ID: ").append(customerId).append(", Total Price: $").append(totalPrice).append("\n");
        builder.append("Products:\n");
        for (ProductInSale product : productInSaleList) {
            builder.append(product.toString()).append("\n");
        }
        return builder.toString();
    }
}
