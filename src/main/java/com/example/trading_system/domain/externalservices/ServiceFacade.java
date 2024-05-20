package com.example.trading_system.Domain.externalservices;

import org.springframework.web.client.RestTemplate;

public interface ServiceFacade {
    public void addService(String serviceName, RestTemplate restTemplate);
    public void replaceService(Service newService, Service oldService);
    public void changeServiceName(Service serviceToChangeAt, String newName);

}
