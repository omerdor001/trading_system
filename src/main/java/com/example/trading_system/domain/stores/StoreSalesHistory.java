package com.example.trading_system.domain.stores;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StoreSalesHistory {
    private static StoreSalesHistory instance = null;
    private List<Purchase> purchases;

    public static StoreSalesHistory getInstance() {
        if (instance == null)
            instance = new StoreSalesHistory();
        return instance;
    }
    public void deleteInstance() {
        this.purchases = null;
        instance = null;
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

    public String getPurchaseHistory(String username, String storeName) {
        List<Purchase> filteredPurchases = purchases;
        if (username != null) {
            filteredPurchases = filteredPurchases.stream()
                    .filter(p -> p.getCustomerUsername().equals(username))
                    .collect(Collectors.toList());
        }

        if (storeName != null) {
            filteredPurchases = filteredPurchases.stream()
                    .filter(p -> p.getStoreName().equals(storeName)) //TODO: getStoreName added - make sure it works properly.
                    .collect(Collectors.toList());
        }
        return filteredPurchases.stream()
                .map(Purchase::toString)
                .collect(Collectors.joining("\n"));
    }


}

