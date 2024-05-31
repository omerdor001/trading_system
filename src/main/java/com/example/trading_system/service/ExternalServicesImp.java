package com.example.trading_system.service;

import com.example.trading_system.domain.externalservices.ServiceFacade;
import com.example.trading_system.domain.externalservices.ServiceFacadeImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.InstanceAlreadyExistsException;

public class ExternalServicesImp implements ExternalServices {
    private static final Logger logger = LoggerFactory.getLogger(ExternalServicesImp.class);
    private static ExternalServicesImp instance = null;
    private ServiceFacade serviceFacade;

    private ExternalServicesImp() {
        this.serviceFacade = ServiceFacadeImp.getInstance();
    }

    public static ExternalServicesImp getInstance() {
        if (instance == null)
            instance = new ExternalServicesImp();
        return instance;
    }

    @Override
    public void deleteInstance() {
        instance = null;
        this.serviceFacade.deleteInstance();
        serviceFacade = null;
    }

    @Override
    public void addPaymentService(String serviceName) throws InstanceAlreadyExistsException {
        logger.info("Trying adding external payment service: {}  ", serviceName);
        serviceFacade.addPaymentService(serviceName);
        logger.info("Finish adding external payment service: {}  ", serviceName);
    }

    @Override
    public void addPaymentProxyService(String serviceName) throws InstanceAlreadyExistsException {
        logger.info("Trying adding external payment proxy service: {}  ", serviceName);
        serviceFacade.addPaymentProxyService(serviceName);
        logger.info("Finish adding external payment  proxy service: {}  ", serviceName);
    }

    @Override
    public void addDeliveryService(String serviceName) throws InstanceAlreadyExistsException {
        logger.info("Trying adding external delivery service: {}  ", serviceName);
        serviceFacade.addPaymentService(serviceName);
        logger.info("Finish adding external delivery service: {}  ", serviceName);
    }

    @Override
    public void addDeliveryProxyService(String serviceName) throws InstanceAlreadyExistsException {
        logger.info("Trying adding external delivery proxy service: {}  ", serviceName);
        serviceFacade.addPaymentService(serviceName);
        logger.info("Finish adding external delivery proxy service: {}  ", serviceName);
    }

    @Override
    public void replaceService(String newService, String oldService) {
        logger.info("Trying replacing external service: {} to {} ", oldService, newService);
        serviceFacade.replaceService(newService, oldService);
        logger.info("Finish replacing external service: {} to {} ", oldService, newService);
    }

    @Override
    public void changeServiceName(String serviceToChangeAt, String newName) {
        logger.info("Trying changing name to external service: {} to name : {} ", serviceToChangeAt, newName);
        serviceFacade.changeServiceName(serviceToChangeAt, newName);
        logger.info("Finish changing name to external service: {} to name : {} ", serviceToChangeAt, newName);
    }

    @Override
    public void clearServices() {
        logger.info("Trying removing external services");
        serviceFacade.clearServices();
        logger.info("Finish adding external services");
    }

    @Override
    public void makePayment(String serviceName, double amount) {
        logger.info("Trying making Payment with service {} ", serviceName);
        serviceFacade.makePayment(serviceName, amount);
        logger.info("Finish making payment with service: {} ", serviceName);
    }

    @Override
    public void makeDelivery(String serviceName, String address) {
        logger.info("Trying making delivery with service {} ", serviceName);
        serviceFacade.makeDelivery(serviceName, address);
        logger.info("Finish making delivery with service: {} ", serviceName);
    }
}
