package com.example.trading_system.domain.users;

public interface PaymentFacade {

    void deleteInstance();

    boolean checkAvailabilityAndConditions(String username);

    void approvePurchase(String username);

    double calculateTotalPrice(Cart cart);

    String getPurchaseHistory(String username, String storeName, Integer productBarcode);

    String getStoresPurchaseHistory(String username, String storeName, Integer productBarcode);
}
