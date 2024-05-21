package com.example.trading_system.domain.externalservices;

import org.springframework.web.client.RestTemplate;

import javax.management.InstanceAlreadyExistsException;

public interface ServiceFacade {
    public boolean addService(Service service) throws InstanceAlreadyExistsException;
    public boolean replaceService(Service newService, Service oldService);
    public boolean changeServiceName(Service serviceToChangeAt, String newName);

}
