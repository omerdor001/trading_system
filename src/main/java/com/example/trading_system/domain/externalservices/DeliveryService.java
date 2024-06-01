package com.example.trading_system.domain.externalservices;

public interface DeliveryService{
    String getName();
    int makeDelivery(String address);
    void cancelDelivery(int deliveryId);
    boolean connect();
}
