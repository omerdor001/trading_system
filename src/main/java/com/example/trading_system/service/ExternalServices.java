package com.example.trading_system.service;

import com.example.trading_system.domain.externalservices.Service;
import org.springframework.http.ResponseEntity;

import javax.management.InstanceAlreadyExistsException;

public interface ExternalServices {
    boolean addService(Service service) throws InstanceAlreadyExistsException;
    boolean addServiceNew(Service service) throws InstanceAlreadyExistsException;

    public boolean replaceService(Service newService, Service oldService);

    public boolean changeServiceName(Service serviceToChangeAt,String newName);

    public boolean makePayment(String serviceName,double amount);

    public boolean makeDelivery(String serviceName,String address);
}
