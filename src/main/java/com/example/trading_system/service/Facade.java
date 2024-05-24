package com.example.trading_system.service;

import com.example.trading_system.domain.externalservices.Service;
import com.example.trading_system.domain.externalservices.ServiceFacade;
import com.example.trading_system.domain.externalservices.ServiceFacadeImp;
import com.example.trading_system.domain.users.UserFacade;
import com.example.trading_system.domain.users.UserFacadeImp;

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

    public boolean addService(Service service) throws InstanceAlreadyExistsException {
        return serviceFacade.addService(service);
    }

    public boolean replaceService(Service newService, Service oldService){
        return serviceFacade.replaceService(newService,oldService);
    }

    public boolean changeServiceName(Service serviceToChangeAt,String newName){
        return serviceFacade.changeServiceName(serviceToChangeAt,newName);
    }

    public boolean makePayment(String serviceName,double amount){
        return serviceFacade.makePayment(serviceName,amount);
    }

    public boolean makeDelivery(String serviceName,String address){
        return serviceFacade.makeDelivery(serviceName,address);
    }

}