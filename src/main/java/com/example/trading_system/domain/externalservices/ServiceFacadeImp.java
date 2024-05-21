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
    public boolean addService(Service service) throws InstanceAlreadyExistsException {
        if (services.contains(service)){
            throw new InstanceAlreadyExistsException("This service already exist");
        }
        services.add(service);
        return true;
    }

    @Override
    public boolean replaceService(Service newService, Service oldService) {
        if (!services.contains(oldService)){
            throw new NoSuchElementException("Service is not exist");
        }
        if (services.contains(newService)){
            throw new IllegalArgumentException("Service is exist (no need to replace)");
        }
        services.remove(oldService);
        services.add(newService);
        return true;
    }

    @Override
    public boolean changeServiceName(Service serviceToChangeAt,String newName) {
        if (!services.contains(serviceToChangeAt)){
            throw new NoSuchElementException("Service is not exist");
        }
        serviceToChangeAt.setServiceName(newName);
        return true;
    }

    public boolean findServiceByName(String serviceName){
        for (Service service:services){
            if (service.getServiceName().equals(serviceName))
                return true;
        }
        return false;
    }

    @Override
    public boolean makePayment(String serviceName, double amount) {
        if (!findServiceByName(serviceName)){
            throw new NoSuchElementException("Service is not exist");
        }
        PaymentServiceProxy paymentService = new PaymentServiceProxy(serviceName);
        paymentService.processPayment(amount);
        return true;
    }

    @Override
    public boolean makeDelivery(String serviceName, String address) {
        if (!findServiceByName(serviceName)){
            throw new NoSuchElementException("Service is not exist");
        }
        DeliveryServiceProxy deliveryServiceProxy = new DeliveryServiceProxy(serviceName);
        deliveryServiceProxy.processDelivery(address);
        return true;
    }
}
