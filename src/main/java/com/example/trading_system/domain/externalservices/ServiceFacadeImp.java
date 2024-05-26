package com.example.trading_system.domain.externalservices;

import javax.management.InstanceAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ServiceFacadeImp implements ServiceFacade {
    private List<Service> services;
    public ServiceFacadeImp(){
        services=new ArrayList<>();
    }

    @Override
    public void addService(String serviceName) throws InstanceAlreadyExistsException {
        if (findServiceByName(serviceName)){
            throw new InstanceAlreadyExistsException("This service already exist");
        }
       // services.add();
    }

    @Override
    public void replaceService(String newServiceName, String oldServiceName) {
        if (!findServiceByName(oldServiceName)){
            throw new NoSuchElementException("Service is not exist");
        }
        if (findServiceByName(newServiceName)){
            throw new IllegalArgumentException("Service is exist (no need to replace)");
        }
        services.remove(getServiceByName(oldServiceName));
        services.add(getServiceByName(newServiceName));
    }

    @Override
    public void changeServiceName(String serviceToChangeAtName,String newName) {
        if (!findServiceByName(serviceToChangeAtName)){
            throw new NoSuchElementException("Service is not exist");
        }
        getServiceByName(serviceToChangeAtName).setServiceName(newName);
    }

    public boolean findServiceByName(String serviceName){
        for (Service service:services){
            if (service.getServiceName().equals(serviceName))
                return true;
        }
        return false;
    }

    public Service getServiceByName(String serviceName){
        for (Service service:services){
            if (service.getServiceName().equals(serviceName))
                return service;
        }
        throw new NoSuchElementException("No service exist");
    }

    @Override
    public void makePayment(String serviceName, double amount) {
        if (!findServiceByName(serviceName)){
            throw new NoSuchElementException("Service is not exist");
        }
        Service paymentService = new PaymentServiceProxy(serviceName);
        paymentService.makePayment(serviceName,amount);
    }

    @Override
    public void makeDelivery(String serviceName, String address) {
        if (!findServiceByName(serviceName)){
            throw new NoSuchElementException("Service is not exist");
        }
        Service deliveryService = new DeliveryServiceProxy(serviceName);
        deliveryService.makeDelivery(serviceName,address);
    }

}
