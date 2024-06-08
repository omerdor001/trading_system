package com.example.trading_system.domain.stores;

import lombok.Getter;

import java.util.List;

@Getter

public class Purchase {
    private List<ProductInSale> productInSaleList;
    private String customerUsername;
    private double totalPrice;

    public Purchase(String customerUsername, List<ProductInSale> productInSaleList, double totalPrice) {
        this.customerUsername = customerUsername;
        this.productInSaleList = productInSaleList;
        this.totalPrice = totalPrice;
    }

    public String getCustomerUsername(){    //Added because Lombok does not work
        return customerUsername;
    }


    public void addProduct(ProductInSale product) {
        productInSaleList.add(product);
        totalPrice += product.getPrice();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Client Username: ").append(customerUsername).append(", Total Price: $").append(totalPrice).append("\n");
        builder.append("Products:\n");
        for (ProductInSale product : productInSaleList) {
            builder.append(product.toString()).append("\n");
        }
        return builder.toString();
    }

    public String getStoreName() {
        return null;
    }
}
