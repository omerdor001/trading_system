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


    public void addPaymentService(String serviceName) throws InstanceAlreadyExistsException {
        logger.info("Trying adding external payment service: {}  ", serviceName);
        facade.addPaymentService(serviceName);
        logger.info("Finish adding external payment service: {}  ", serviceName);
    }

    public void addPaymentProxyService(String serviceName) throws InstanceAlreadyExistsException {
        logger.info("Trying adding external payment proxy service: {}  ", serviceName);
        facade.addPaymentProxyService(serviceName);
        logger.info("Finish adding external payment  proxy service: {}  ", serviceName);
    }

    public void addDeliveryService(String serviceName) throws InstanceAlreadyExistsException {
        logger.info("Trying adding external delivery service: {}  ", serviceName);
        facade.addPaymentService(serviceName);
        logger.info("Finish adding external delivery service: {}  ", serviceName);
    }

    public void addDeliveryProxyService(String serviceName) throws InstanceAlreadyExistsException {
        logger.info("Trying adding external delivery proxy service: {}  ", serviceName);
        facade.addPaymentService(serviceName);
        logger.info("Finish adding external delivery proxy service: {}  ", serviceName);
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

    @Override
    public void clearServices() {
        logger.info("Trying removing external services");
        facade.clearServices();
        logger.info("Finish adding external services");
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
