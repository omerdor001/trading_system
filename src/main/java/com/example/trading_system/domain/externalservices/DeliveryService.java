package com.example.trading_system.domain.externalservices;

public interface DeliveryService {
    int makeDelivery(String address);

    void cancelDelivery(int deliveryId);

    boolean connect();
}
