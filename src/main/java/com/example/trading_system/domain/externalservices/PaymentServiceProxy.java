package com.example.trading_system.domain.externalservices;

public class PaymentServiceProxy extends Service {

    public PaymentServiceProxy(String serviceName) {
        super(serviceName);
    }

    @Override
    public void makePayment(String serviceName, double amount) {
        if (amount>0) {
            System.out.println("Processing payment of $" + amount);
        }
        else {
            throw new IllegalArgumentException("Payment authorization failed");
        }
        // Here would be the real payment processing logic, e.g., calling an external payment gateway API
    }

    @Override
    public void cancelPayment(String serviceName) {}

    @Override
    public void makeDelivery(String serviceName, String address) {}

    @Override
    public void cancelDelivery(String serviceName, String address) {}

    @Override
    public boolean connect() {
        return true;
    }
}
