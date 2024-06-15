package com.example.trading_system.domain.stores;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@Setter
public class StoreSalesHistory {
    private List<Purchase> purchases;

    public StoreSalesHistory(){
        purchases = new ArrayList<>();
    }

    public void addPurchase(List<Purchase> purchases1) {
        purchases.addAll(purchases1);
    }

    public void addPurchase(Purchase purchase) {
        purchases.add(purchase);
    }


    public List<Purchase> getPurchasesByCustomer(String customerUsername) {
        return purchases.stream().filter(purchase -> customerUsername.equals(purchase.getCustomerUsername())).collect(Collectors.toList());
    }

    public List<Purchase> getAllPurchases() {
        return purchases;
    }

    public String getPurchaseHistory(String username){
        List<Purchase> filteredPurchases = purchases;
        if (username != null) {
            filteredPurchases = filteredPurchases.stream()
                    .filter(p -> p.getCustomerUsername().equals(username))
                    .collect(Collectors.toList());
        }
        return filteredPurchases.stream()
                .map(Purchase::toString)
                .collect(Collectors.joining("\n"));
    }


}

