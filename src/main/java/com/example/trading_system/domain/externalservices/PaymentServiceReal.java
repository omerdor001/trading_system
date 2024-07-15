package com.example.trading_system.domain.externalservices;

public class PaymentServiceReal implements PaymentService {


    @Override
    public int makePayment(double amount, String currency, String cardNumber, String month, String year, String holder, String ccv, String id) {
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
