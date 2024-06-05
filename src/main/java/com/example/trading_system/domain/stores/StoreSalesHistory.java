package com.example.trading_system.domain.stores;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StoreSalesHistory {
    private List<Purchase> purchases;

    public void addPurchase(Purchase purchase) {
        purchases.add(purchase);
    }

    public List<Purchase> getPurchasesByCustomer(String customerUsername) {
        return purchases.stream().filter(purchase -> customerUsername == purchase.getCustomerUsername()).collect(Collectors.toList());
    }

    public List<Purchase> getAllPurchases() {
        return purchases;
    }
}

