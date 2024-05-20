package com.example.trading_system.Domain.externalservices;

import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ServiceFacadeImp implements ServiceFacade {
    public List<Service> services;
    public ServiceFacadeImp(){
        services=new ArrayList<>();
    }
    @Override
    public void addService(String serviceName, RestTemplate restTemplate) {
        services.add(new Service(serviceName,restTemplate));
    }

    @Override
    public void replaceService(Service newService, Service oldService) {
        if (!services.contains(oldService)){
            throw new NoSuchElementException("Service is not exist");
        }
        if (services.contains(newService)){
            throw new IllegalArgumentException("Service is exist (no need to replace)");
        }
        services.remove(oldService);
        services.add(newService);
    }

    @Override
    public void changeServiceName(Service serviceToChangeAt,String newName) {
       serviceToChangeAt.setServiceName(newName);
    }
}
