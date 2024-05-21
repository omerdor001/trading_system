package com.example.trading_system.Domain.externalservices;

public class PaymentServiceProxy {
    private PaymentService realPaymentService;

    public PaymentServiceProxy(String serviceName) {
        this.realPaymentService = new PaymentService(serviceName);
    }

    public void processPayment(double amount) {
        // Additional logic before delegating to the real payment service
        if (amount>0) {
            realPaymentService.processPayment(amount);
        } else {
            System.out.println("Payment authorization failed.");
        }
    }
}
