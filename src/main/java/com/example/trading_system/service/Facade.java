package com.example.trading_system.service;

import com.example.trading_system.domain.externalservices.Service;
import com.example.trading_system.domain.externalservices.ServiceFacade;
import com.example.trading_system.domain.externalservices.ServiceFacadeImp;
import com.example.trading_system.domain.users.RoleState;
import com.example.trading_system.domain.users.UserFacade;
import com.example.trading_system.domain.users.UserFacadeImp;
import org.springframework.http.ResponseEntity;

import javax.management.InstanceAlreadyExistsException;

public class Facade {
    public ServiceFacade serviceFacade;
    public UserFacade userFacade;

    public ExternalServices externalServices;
    public UserService userService;

    public Facade(){
        serviceFacade=new ServiceFacadeImp();
        userFacade=new UserFacadeImp();

        externalServices=new ExternalServicesImp(serviceFacade);
        userService=new UserServiceImp(userFacade);
    }

    public ResponseEntity<String> addService(Service service) {
        return externalServices.addService(service);
    }

    public ResponseEntity<String> replaceService(Service newService, Service oldService){
        return externalServices.replaceService(newService,oldService);
    }

    public ResponseEntity<String> changeServiceName(Service serviceToChangeAt,String newName){
        return externalServices.changeServiceName(serviceToChangeAt,newName);
    }

    public ResponseEntity<String> makePayment(String serviceName,double amount){
        return externalServices.makePayment(serviceName,amount);
    }

    public ResponseEntity<String> makeDelivery(String serviceName,String address){
        return externalServices.makeDelivery(serviceName,address);
    }

}