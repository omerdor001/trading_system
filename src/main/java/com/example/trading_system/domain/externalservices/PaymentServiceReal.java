package com.example.trading_system.domain.externalservices;

public class PaymentServiceReal implements PaymentService {

    @Override
    public int makePayment(double amount) {
        return 0;
    }

    @Override
    public void cancelPayment(int paymentId) {
    }

    @Override
    public boolean connect() {
        return false;
    }
}
