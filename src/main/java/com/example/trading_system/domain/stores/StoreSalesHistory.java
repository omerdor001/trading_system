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

    public List<Purchase> getPurchasesByCustomer(int customerId) {
        return purchases.stream().filter(purchase -> customerId == purchase.getCustomerId()).collect(Collectors.toList());
    }

    public List<Purchase> getAllPurchases() {
        return purchases;
    }
}

