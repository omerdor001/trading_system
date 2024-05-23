package com.example.trading_system.service;

import com.example.trading_system.domain.externalservices.Service;
import com.example.trading_system.domain.externalservices.ServiceFacade;
import com.example.trading_system.domain.externalservices.ServiceFacadeImp;
import com.example.trading_system.domain.stores.Category;
import com.example.trading_system.domain.stores.MarketFacade;
import com.example.trading_system.domain.users.UserFacade;
import com.example.trading_system.domain.users.UserFacadeImp;
import org.springframework.http.ResponseEntity;

import javax.management.InstanceAlreadyExistsException;
import java.util.List;

public class Facade {
    public ServiceFacade serviceFacade;
    public UserFacade userFacade;
    public MarketFacade marketFacade;

    public ExternalServices externalServices;
    public UserService userService;
    public MarketService marketService;

    public Facade(){
        serviceFacade=new ServiceFacadeImp();
        userFacade=new UserFacadeImp();
        externalServices=new ExternalServicesImp(serviceFacade);
        userService=new UserServiceImp(userFacade);
        marketService=new MarketServiceImp(marketFacade);
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

    public ResponseEntity<String> addProduct(String username, int product_id, String store_name, String product_name, String product_description,
                                             double product_price, int product_quantity, double rating, Category category, List<String> keyWords){
        return marketService.addProduct(username,product_id,store_name,product_name,product_description,product_price,product_quantity,rating,category,keyWords);
    }




}