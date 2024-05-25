package com.example.trading_system.domain.stores;

import lombok.Getter;

import java.util.List;

@Getter
public class Purchase {
    private int customerId;

    public Purchase(int customerId, List<ProductInSale> productInSaleList) {
        double totalPrice = productInSaleList.stream().mapToDouble(ProductInSale::getSumPrice).sum();
    }
}
