package com.example.trading_system.service;

public interface PaymentService {
    void deleteInstance();

    boolean checkAvailabilityAndConditions(String username);

    void approvePurchase(String username);

    String getPurchaseHistory(String username, String storeName, Integer productBarcode);

    String getStoresPurchaseHistory(String username, String storeName, Integer productBarcode);
}

