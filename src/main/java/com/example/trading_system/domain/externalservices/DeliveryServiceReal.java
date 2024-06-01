package com.example.trading_system.domain.externalservices;

public class DeliveryServiceReal implements DeliveryService{
    @Override
    public String getName() {
        return "";
    }

    @Override
    public int makeDelivery(String address) {

        return 0;
    }

    @Override
    public void cancelDelivery(int deliveryId) {

    }

    @Override
    public boolean connect() {
        return false;
    }
}
