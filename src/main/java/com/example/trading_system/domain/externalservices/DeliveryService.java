package com.example.trading_system.domain.externalservices;

public class DeliveryService extends Service{

    public DeliveryService(String serviceName) {
        super(serviceName);
    }

    @Override
    public void makePayment(String serviceName, double amount) {}

    @Override
    public void cancelPayment(String serviceName) {}

    @Override
    public void makeDelivery(String serviceName, String address) {}

    @Override
    public void cancelDelivery(String serviceName, String address) {}

    @Override
    public boolean connect() {
        return false;
    }
}
