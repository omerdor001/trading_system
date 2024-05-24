package com.example.trading_system.domain.externalservices;

public class DeliveryService extends Service{

    public DeliveryService(String serviceName) {
        super(serviceName);
    }

    public void processDelivery(String address) {
        System.out.println("Processing delivery of $" + address);
        // Here would be the real delivery processing logic, e.g., calling an external delivery gateway API
    }

}
