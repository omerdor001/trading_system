package com.example.trading_system.domain.externalservices;

import com.example.trading_system.service.TradingSystemImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentServiceProxy implements PaymentService{
    int id=1;
    private static final Logger logger = LoggerFactory.getLogger(TradingSystemImp.class);
    public PaymentServiceProxy() {
    }

    @Override
    public int makePayment(double amount) {
        if (amount>0) {
            System.out.println("Processing payment of $" + amount);
        }
        else {
            logger.error("Delivery authorization failed to make payment of amount : {}",amount);
            throw new IllegalArgumentException("Payment authorization failed");
        }
        logger.info("Succeed making payment in pay of {} ",amount);
        int paymentId=id;
        id++;
        return paymentId;
    }

    @Override
    public void cancelPayment(int paymentId) {
        logger.info("Cancel payment with id {}",paymentId);
        System.out.println("Payment cancelled successfully");
    }

    @Override
    public boolean connect() {
        return true;
    }
}
