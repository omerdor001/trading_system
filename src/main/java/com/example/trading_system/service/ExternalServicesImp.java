package com.example.trading_system.service;

import com.example.trading_system.domain.externalservices.Service;
import com.example.trading_system.domain.externalservices.ServiceFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ExternalServicesImp implements ExternalServices{
    private ServiceFacade facade;
    private static final Logger logger= LoggerFactory.getLogger(ExternalServices.class);
    public ExternalServicesImp(ServiceFacade facade){
        this.facade=facade;

    }
    public ResponseEntity<String> addService(String serviceName) {//Add connection
        logger.info("Trying adding external service: {}", serviceName);
        try {
            facade.addService(serviceName);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed trying adding external service: {}", e.getMessage(), serviceName);
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);

        }
        logger.info("Finish adding external service: {}", serviceName);
        return new ResponseEntity<String>("Success adding external service", HttpStatus.OK);
    }

    public ResponseEntity<String> replaceService(String newServiceName, String oldServiceName){
        logger.info("Trying replacing external service: {} to {} ",oldServiceName,newServiceName);
        try {
            facade.replaceService(newServiceName,oldServiceName);
        }
        catch (Exception e){
            logger.error("Error occurred : {} ,Failed trying replacing external service: {} to {} ",e.getMessage(),oldServiceName,newServiceName);
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finish replacing external service: {} to {} ",oldServiceName,newServiceName);
        return new ResponseEntity<String>("Success replacing external service", HttpStatus.OK);
    }

    public ResponseEntity<String> changeServiceName(String serviceToChangeAtName,String newName){
        logger.info("Trying changing name to external service: {} to name : {} ",serviceToChangeAtName,newName);
        try {
            facade.changeServiceName(serviceToChangeAtName,newName);
        }
        catch (Exception e){
            logger.error("Error occurred : {} , Failed trying changing name to external service: {} to name : {} ",e.getMessage(),serviceToChangeAtName,newName);
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finish changing name to external service: {} to name : {} ",serviceToChangeAtName,newName);
        return new ResponseEntity<String>("Success changing external service name", HttpStatus.OK);
    }

    public ResponseEntity<String> makePayment(String serviceName,double amount){
        logger.info("Trying making Payment with service {} ",serviceName);
        try {
            facade.makePayment(serviceName,amount);
        }
        catch (Exception e){
            logger.error("Error occurred : {} , Failed making payment with service: {}  ",e.getMessage(),serviceName);
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finish making payment with service: {} ",serviceName);
        return new ResponseEntity<String>("Success making payment", HttpStatus.OK);
    }

    public ResponseEntity<String> makeDelivery(String serviceName,String address){
        boolean result;
        logger.info("Trying making delivery with service {} ",serviceName);
        try {
            facade.makeDelivery(serviceName,address);
        }
        catch (Exception e){
            logger.error("Error occurred : {} , Failed making delivery with service: {}  ",e.getMessage(),serviceName);
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        logger.info("Finish making delivery with service: {} ",serviceName);
        return new ResponseEntity<String>("Success making delivery", HttpStatus.OK);
    }
}
