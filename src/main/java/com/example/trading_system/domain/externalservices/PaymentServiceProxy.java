package com.example.trading_system.domain.externalservices;

import com.example.trading_system.service.TradingSystemImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//@Service
public class PaymentServiceProxy implements PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(TradingSystemImp.class);
    int id = 1;

//    @Autowired
    public PaymentServiceProxy() {
    }

    @Override
    public int makePayment(double amount, String currency, String cardNumber, String month, String year, String holder, String ccv, String id) {
        if (amount > 0) {
            System.out.println("Processing payment of $" + amount + " " + currency);
        } else {
            logger.error("Payment authorization failed for amount: {}", amount);
            throw new IllegalArgumentException("Payment authorization failed");
        }
        logger.info("Succeed making payment of {} {}", amount, currency);
        int paymentId = this.id;
        this.id++;
        return paymentId;
    }

    @Override
    public void cancelPayment(int paymentId) {
        logger.info("Cancel payment with id {}", paymentId);
        System.out.println("Payment cancelled successfully");
    }

    @Override
    public boolean connect() {
        return true;
    }
}
