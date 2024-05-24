package com.example.trading_system.service;

import com.example.trading_system.domain.externalservices.Service;
import com.example.trading_system.domain.externalservices.ServiceFacade;
import com.example.trading_system.domain.externalservices.ServiceFacadeImp;
import com.example.trading_system.domain.users.UserFacade;
import com.example.trading_system.domain.users.UserFacadeImp;

import javax.management.InstanceAlreadyExistsException;
import java.time.LocalDate;

public class Facade {
    public ServiceFacade serviceFacade;
    public UserFacade userFacade;
    public int counter_user=0;

    public ExternalServices externalServices;
    public UserService userService;

    public Facade(){
        serviceFacade=new ServiceFacadeImp();
        userFacade=new UserFacadeImp();

        externalServices=new ExternalServicesImp(serviceFacade);
        userService=new UserServiceImp(userFacade);
    }

    public String enter(){
        String token = userService.enter(counter_user);
        counter_user++;
        //TODO Show UI
        return token;
    }

    public void exit(String token, int id) throws Exception {
        userService.exit(id);
        Security.makeTokenExpire(token);
    }

    public void exit(String token, String username) throws Exception {
        userService.exit(username);
        Security.makeTokenExpire(token);
    }

    public boolean registration(String token, int id, String username, String password, LocalDate birthdate) throws Exception {
        if(Security.validateToken(token,"v"+id)){
            userFacade.registration(id,username,password,birthdate);
            return true;
        }
        else{
            return false;
        }
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