package com.example.trading_system.Service;

import com.example.trading_system.Domain.externalservices.Service;
import com.example.trading_system.Domain.externalservices.ServiceFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalServices {
    private ServiceFacade facade;
    private static final Logger logger= LoggerFactory.getLogger(ExternalServices.class);
    public ExternalServices(ServiceFacade facade){
        this.facade=facade;

    }
    public boolean addService(Service service) {//Add connection
        boolean result;
        logger.info("Trying adding external service: {}", service.getServiceName());
        try {
            boolean _result=facade.addService(service);
            result=_result;
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed trying adding external service: {}", e.getMessage(), service.getServiceName());
            return false;
        }
        logger.info("Finish adding external service: {}", service.getServiceName());
        return result;
    }

    public boolean replaceService(Service newService, Service oldService){
        boolean result;
        logger.info("Trying replacing external service: {} to {} ",oldService.getServiceName(),newService.getServiceName());
        try {
            boolean _result=facade.replaceService(newService,oldService);
            result=_result;
        }
        catch (Exception e){
            logger.error("Error occurred : {} ,Failed trying replacing external service: {} to {} ",e.getMessage(),oldService.getServiceName(),newService.getServiceName());
            return false;
        }
        logger.info("Finish replacing external service: {} to {} ",oldService.getServiceName(),newService.getServiceName());
        return result;
    }

    public boolean changeServiceName(Service serviceToChangeAt,String newName){
        boolean result;
        logger.info("Trying changing name to external service: {} to name : {} ",serviceToChangeAt.getServiceName(),newName);
        try {
            boolean _result=facade.changeServiceName(serviceToChangeAt,newName);
            result=_result;
        }
        catch (Exception e){
            logger.error("Error occurred : {} , Failed trying changing name to external service: {} to name : {} ",e.getMessage(),serviceToChangeAt.getServiceName(),newName);
            return false;
        }
        logger.info("Finish changing name to external service: {} to name : {} ",serviceToChangeAt.getServiceName(),newName);
        return result;
    }

    public boolean makePayment(String serviceName,double amount){
        boolean result;
        logger.info("Trying making Payment with service {} ",serviceName);
        try {
            boolean _result=facade.makePayment(serviceName,amount);
            result=_result;
        }
        catch (Exception e){
            logger.error("Error occurred : {} , Failed making payment with service: {}  ",e.getMessage(),serviceName);
            return false;
        }
        logger.info("Finish making payment with service: {} ",serviceName);
        return result;
    }
}
