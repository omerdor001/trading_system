package com.example.trading_system.service;

import com.example.trading_system.domain.externalservices.Service;
import org.springframework.http.ResponseEntity;

import javax.management.InstanceAlreadyExistsException;

public interface ExternalServices {
    void addService(String service) throws InstanceAlreadyExistsException;
    void addServiceNew(String service) throws InstanceAlreadyExistsException;

    public void replaceService(String newService, String oldService);

    public void changeServiceName(String serviceToChangeAt,String newName);

    public void makePayment(String serviceName,double amount);

    public void makeDelivery(String serviceName,String address);
}
