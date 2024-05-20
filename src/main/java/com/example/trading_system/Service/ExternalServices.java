package com.example.trading_system.Service;

import com.example.trading_system.Domain.externalservices.Service;
import com.example.trading_system.Domain.externalservices.ServiceFacade;
import com.example.trading_system.Domain.externalservices.ServiceFacadeImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

public class ExternalServices {
    private ServiceFacade facade;
    private static final Logger logger= LoggerFactory.getLogger(ExternalServices.class);
    public ExternalServices(){
        facade=new ServiceFacadeImp();

    }
    public void addService(String serviceName, RestTemplate restTemplate){//Add connection
        logger.info("Trying adding external service: {}",serviceName);
        try {
            facade.addService(serviceName,restTemplate);
        }
        catch (Exception e){
            logger.error("Error occurred : {} , Failed trying adding external service: {}",e.getMessage(),serviceName);
        }
        logger.info("Finish adding external service: {}",serviceName);

    }
    public void replaceService(Service newService, Service oldService){
        logger.info("Trying replacing external service: {} to {} ",oldService.getServiceName(),newService.getServiceName());
        try {
            facade.replaceService(newService,oldService);
        }
        catch (Exception e){
            logger.error("Error occurred : {} ,Failed trying replacing external service: {} to {} ",e.getMessage(),oldService.getServiceName(),newService.getServiceName());
        }
        logger.info("Finish replacing external service: {} to {} ",oldService.getServiceName(),newService.getServiceName());
    }
    public void changeServiceName(Service serviceToChangeAt,String newName){
        logger.info("Trying changing name to external service: {} to name : {} ",serviceToChangeAt.getServiceName(),newName);
        try {
            facade.changeServiceName(serviceToChangeAt,newName);
        }
        catch (Exception e){
            logger.error("Error occurred : {} , Failed trying changing name to external service: {} to name : {} ",e.getMessage(),serviceToChangeAt.getServiceName(),newName);
        }
        logger.info("Finish changing name to external service: {} to name : {} ",serviceToChangeAt.getServiceName(),newName);
    }
}
