package com.example.trading_system.service;

import com.example.trading_system.domain.externalservices.Service;
import com.example.trading_system.domain.externalservices.ServiceFacade;
import com.example.trading_system.domain.externalservices.ServiceFacadeImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.InstanceAlreadyExistsException;

public class ExternalServicesImp implements ExternalServices {
    private ServiceFacade facade = ServiceFacadeImp.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(ExternalServicesImp.class);

    private ExternalServicesImp() {
    }

    private static class Singleton {
        private static final ExternalServicesImp INSTANCE = new ExternalServicesImp();
    }

    public static ExternalServicesImp getInstance() {
        return ExternalServicesImp.Singleton.INSTANCE;
    }

    public ResponseEntity<String> addService(String serviceName) {//Add connection
        logger.info("Trying adding external service: {}", serviceName);
        try {
            facade.addService(serviceName);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed trying adding external service: {}", e.getMessage(), serviceName);
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);

        }
        logger.info("Finish adding external payment service: {}", serviceName);
        return new ResponseEntity<String>("Success adding external payment service", HttpStatus.OK);
    }
    //TODO:
/*
    @Override
    public ResponseEntity<String> addPaymentService(String serviceName) {
        logger.info("Trying adding external payment service: {}", serviceName);
        try {
            facade.addPaymentService(serviceName);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed trying adding external payment service: {}", e.getMessage(), serviceName);
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);

        }
        logger.info("Finish adding external payment service: {}", serviceName);
        return new ResponseEntity<String>("Success adding external payment service", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> addPaymentProxyService(String serviceName) {
        logger.info("Trying adding external payment proxy service: {}", serviceName);
        try {
            facade.addPaymentProxyService(serviceName);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed trying adding external payment proxy service: {}", e.getMessage(), serviceName);
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);

        }
        logger.info("Finish adding external payment proxy service: {}", serviceName);
        return new ResponseEntity<String>("Success adding external payment proxy service", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> addDeliveryService(String serviceName) {
        logger.info("Trying adding external delivery service: {}", serviceName);
        try {
            facade.addDeliveryService(serviceName);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed trying adding external delivery service: {}", e.getMessage(), serviceName);
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);

        }
        logger.info("Finish adding external delivery service: {}", serviceName);
        return new ResponseEntity<String>("Success adding external delivery service", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> addDeliveryProxyService(String serviceName) {
        logger.info("Trying adding external delivery proxy service: {}", serviceName);
        try {
            facade.addDeliveryProxyService(serviceName);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed trying adding external delivery proxy service: {}", e.getMessage(), serviceName);
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);

        }
        logger.info("Finish adding external delivery proxy service: {}", serviceName);
        return new ResponseEntity<String>("Success adding external delivery proxy service", HttpStatus.OK);
    }*/

    public void addService(String service) throws InstanceAlreadyExistsException {//Add connection
        logger.info("Trying adding external service: {}", service);
        facade.addService(service);
        logger.info("Finish adding external service: {}", service);
    }

    @Override
    public void addServiceNew(String service) throws InstanceAlreadyExistsException {
        logger.info("Trying add external service: {}", service);
        facade.addService(service);
        logger.info("Finish add external service: {}", service);
    }

    public void replaceService(String newService, String oldService) {
        logger.info("Trying replacing external service: {} to {} ", oldService, newService);
        facade.replaceService(newService, oldService);
        logger.info("Finish replacing external service: {} to {} ", oldService, newService);
    }

    public void changeServiceName(String serviceToChangeAt, String newName) {
        logger.info("Trying changing name to external service: {} to name : {} ", serviceToChangeAt, newName);
        facade.changeServiceName(serviceToChangeAt, newName);
        logger.info("Finish changing name to external service: {} to name : {} ", serviceToChangeAt, newName);
    }

    public void makePayment(String serviceName, double amount) {
        logger.info("Trying making Payment with service {} ", serviceName);
        facade.makePayment(serviceName, amount);
        logger.info("Finish making payment with service: {} ", serviceName);
    }

    public void makeDelivery(String serviceName, String address) {
        logger.info("Trying making delivery with service {} ", serviceName);
        facade.makeDelivery(serviceName, address);
        logger.info("Finish making delivery with service: {} ", serviceName);
    }
}
