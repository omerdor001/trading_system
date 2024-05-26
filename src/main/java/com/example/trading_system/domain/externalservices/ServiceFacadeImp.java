package com.example.trading_system.domain.externalservices;

import com.example.trading_system.domain.stores.MarketFacadeImp;

import javax.management.InstanceAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ServiceFacadeImp implements ServiceFacade {
    private List<Service> services;
    private ServiceFacadeImp(){
        services=new ArrayList<>();
    }

    private  static class Singleton  {
        private static final ServiceFacadeImp INSTANCE = new ServiceFacadeImp();
    }
    public static ServiceFacadeImp getInstance() {
        return ServiceFacadeImp.Singleton.INSTANCE;
    }

    @Override
    public void addService(String serviceName) throws InstanceAlreadyExistsException {
        if (findServiceByName(serviceName)){
            throw new InstanceAlreadyExistsException("This service already exist");
        }
        services.add(new Service(serviceName));
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

    private boolean findServiceByName(String serviceName){
        for (Service service:services){
            if (service.getServiceName().equals(serviceName))
                return true;
        }
        return false;
    }

    private Service getServiceByName(String serviceName){
        for (Service service:services){
            if (service.getServiceName().equals(serviceName))
                return service;
        }
        return null;
    }

    @Override
    public void makePayment(String serviceName, double amount) {
        if (!findServiceByName(serviceName)){
            throw new NoSuchElementException("Service is not exist");
        }
        PaymentServiceProxy paymentService = new PaymentServiceProxy(serviceName);
        paymentService.processPayment(amount);
    }

    @Override
    public void makeDelivery(String serviceName, String address) {
        if (!findServiceByName(serviceName)){
            throw new NoSuchElementException("Service is not exist");
        }
        DeliveryServiceProxy deliveryServiceProxy = new DeliveryServiceProxy(serviceName);
        deliveryServiceProxy.processDelivery(address);
    }
}
