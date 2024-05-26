package com.example.trading_system.service;

import com.example.trading_system.domain.users.PaymentFacade;
import com.example.trading_system.domain.users.PaymentFacadeImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentServiceImp implements PaymentService{
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);
    PaymentFacadeImp paymentFacade = PaymentFacadeImp.getInstance();
    @Override
    public boolean VisitorCheckAvailabilityAndConditions(int visitorId) {
        logger.info("Checking availability and conditions for visitor with ID: {}", visitorId);
        try {
            return paymentFacade.VisitorCheckAvailabilityAndConditions(visitorId);
        } catch (Exception e) {
            logger.error("Error occurred while checking availability cart and conditions for visitor with ID: {}: {}", visitorId, e.getMessage());
            return false;
        }

    }

    @Override
    public boolean registeredCheckAvailabilityAndConditions(String registeredId) {
        logger.info("Checking availability and conditions for registered user with ID: {}", registeredId);
        try {
            return paymentFacade.registeredCheckAvailabilityAndConditions(registeredId);
        } catch (Exception e) {
            logger.error("Error occurred while checking availability cart and conditions for registered user with ID: {}: {}", registeredId, e.getMessage());
            return false;
        }
    }

    @Override
    public void VisitorApprovePurchase(int visitorId, String paymentService) {
        logger.info("Approving purchase for visitor with ID: {} using payment service: {}", visitorId, paymentService);
        try {
            paymentFacade.VisitorApprovePurchase(visitorId, paymentService);
        } catch (Exception e) {
            logger.error("Error occurred while approving purchase for visitor with ID: {} using payment service: {}: {}", visitorId, paymentService, e.getMessage());
        }
        logger.info("Purchase approved for visitor with ID: {} using payment service: {}", visitorId, paymentService);

    }

    @Override
    public void RegisteredApprovePurchase(String registeredId, String paymentService) {
        logger.info("Approving purchase for registered user with ID: {} using payment service: {}", registeredId, paymentService);
        try {
            paymentFacade.RegisteredApprovePurchase(registeredId,paymentService);
        } catch (Exception e) {
            logger.error("Error occurred while approving purchase for registered user with ID: {} using payment service: {}: {}", registeredId, paymentService, e.getMessage());
        }
        logger.info("Purchase approved for registered user with ID: {} using payment service: {}", registeredId, paymentService);

    }

    @Override
    public String getPurchaseHistory(String username, String storeName, Integer id, Integer productBarcode) {
        String result="";
        logger.info("Get Purchase History");
        try {
             result= paymentFacade.getPurchaseHistory(username,storeName,id,productBarcode);
        }catch (Exception e){
            logger.error("Error occurred while Getting Purchase History");
            return  "";
        }
        return result;
    }

    @Override
    public String getStoresPurchaseHistory(String username, String storeName, Integer id, Integer productBarcode) {
        String result="";
        logger.info("Get Purchase Stores History");
        try {
            result= paymentFacade.getStoresPurchaseHistory(username,storeName,id,productBarcode);
        }catch (Exception e){
            logger.error("Error occurred while Getting Purchase Stores History");
            return  "";
        }
        return result;
    }


}
