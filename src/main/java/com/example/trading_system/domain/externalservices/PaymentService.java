package com.example.trading_system.domain.externalservices;

public interface PaymentService{
    int makePayment(double amount);
    void cancelPayment(int paymentId);
    boolean connect();
}
