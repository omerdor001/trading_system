package com.example.trading_system.service;

import com.example.trading_system.domain.users.PaymentFacade;
import com.example.trading_system.domain.users.PaymentFacadeImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentServiceImp implements PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImp.class);
    private PaymentFacade paymentFacade;

    private static PaymentServiceImp instance = null;

    private PaymentServiceImp() {
        paymentFacade = PaymentFacadeImp.getInstance();
    }

    public static PaymentServiceImp getInstance() {
        if(instance == null)
            instance = new PaymentServiceImp();
        return instance;
    }

    @Override
    public void deleteInstance(){
        instance = null;
        paymentFacade.deleteInstance();
        paymentFacade = null;
    }

    @Override
    public boolean checkAvailabilityAndConditions(String registeredId) {
        logger.info("Checking availability and conditions for registered user with ID: {}", registeredId);
        return paymentFacade.checkAvailabilityAndConditions(registeredId);
    }

    @Override
    public void approvePurchase(String registeredId) {
        logger.info("Approving purchase for registered user with ID: {} ", registeredId);
        paymentFacade.approvePurchase(registeredId);
        logger.info("Purchase approved for registered user with ID: {}", registeredId);

    }

    @Override
    public String getPurchaseHistory(String username, String storeName,  Integer productBarcode) {
        String result = "";
        logger.info("Get Purchase History");
        result = paymentFacade.getPurchaseHistory(username, storeName,  productBarcode);
        return result;
    }

    @Override
    public String getStoresPurchaseHistory(String username, String storeName,  Integer productBarcode) {
        String result = "";
        logger.info("Get Purchase Stores History");
        result = paymentFacade.getStoresPurchaseHistory(username, storeName, productBarcode);
        return result;
    }
}
