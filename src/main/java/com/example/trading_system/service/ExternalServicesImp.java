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

    public boolean addService(Service service) throws InstanceAlreadyExistsException {//Add connection
        logger.info("Trying adding external service: {}", service.getServiceName());
        boolean result = facade.addService(service);
        logger.info("Finish adding external service: {}", service.getServiceName());
        return result;
    }

    @Override
    public boolean addServiceNew(Service service) throws InstanceAlreadyExistsException {
        logger.info("Trying add external service: {}", service.getServiceName());
        boolean result = facade.addService(service);
        logger.info("Finish add external service: {}", service.getServiceName());
        return result;
    }

    public boolean replaceService(Service newService, Service oldService) {
        logger.info("Trying replacing external service: {} to {} ", oldService.getServiceName(), newService.getServiceName());
        boolean result = facade.replaceService(newService, oldService);
        logger.info("Finish replacing external service: {} to {} ", oldService.getServiceName(), newService.getServiceName());
        return result;
    }

    public boolean changeServiceName(Service serviceToChangeAt, String newName) {
        logger.info("Trying changing name to external service: {} to name : {} ", serviceToChangeAt.getServiceName(), newName);
        boolean result = facade.changeServiceName(serviceToChangeAt, newName);
        logger.info("Finish changing name to external service: {} to name : {} ", serviceToChangeAt.getServiceName(), newName);
        return result;
    }

    public boolean makePayment(String serviceName, double amount) {
        logger.info("Trying making Payment with service {} ", serviceName);
        boolean result = facade.makePayment(serviceName, amount);
        logger.info("Finish making payment with service: {} ", serviceName);
        return result;
    }

    public boolean makeDelivery(String serviceName, String address) {
        logger.info("Trying making delivery with service {} ", serviceName);
        boolean result = facade.makeDelivery(serviceName, address);
        logger.info("Finish making delivery with service: {} ", serviceName);
        return result;
    }
}
