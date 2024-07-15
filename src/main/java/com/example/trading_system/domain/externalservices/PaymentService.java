package com.example.trading_system.domain.externalservices;

public interface PaymentService {
    int makePayment(double amount, String currency, String cardNumber, String month, String year, String holder, String ccv, String id);
    void cancelPayment(int paymentId);
    boolean connect();
}
