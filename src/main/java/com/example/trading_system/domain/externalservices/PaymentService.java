package com.example.trading_system.Domain.externalservices;

public class PaymentService extends Service{

    public PaymentService(String serviceName) {
        super(serviceName);
    }

    public void processPayment(double amount) {
        System.out.println("Processing payment of $" + amount);
        // Here would be the real payment processing logic, e.g., calling an external payment gateway API
    }




}


