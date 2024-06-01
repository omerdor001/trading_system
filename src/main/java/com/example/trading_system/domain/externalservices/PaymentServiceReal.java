package com.example.trading_system.domain.externalservices;

public class PaymentServiceReal implements PaymentService{
    @Override
    public String getName() {
        return "";
    }

    @Override
    public void makePayment(double amount) {

    }

    @Override
    public void cancelPayment(int paymentId) {

    }

    @Override
    public boolean connect() {
        return false;
    }
}
