package com.example.trading_system.service;

import com.example.trading_system.domain.externalservices.Service;
import org.springframework.http.ResponseEntity;

import javax.management.InstanceAlreadyExistsException;

public interface ExternalServices {
    void addService(Service service) throws InstanceAlreadyExistsException;
    void addServiceNew(Service service) throws InstanceAlreadyExistsException;

    public void replaceService(Service newService, Service oldService);

    public void changeServiceName(Service serviceToChangeAt,String newName);

    public void makePayment(String serviceName,double amount);

    public void makeDelivery(String serviceName,String address);
}
