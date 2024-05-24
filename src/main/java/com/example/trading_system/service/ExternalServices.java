package com.example.trading_system.service;

import com.example.trading_system.domain.externalservices.Service;
import org.springframework.http.ResponseEntity;

public interface ExternalServices {
    boolean addService(Service service);
    ResponseEntity<String> addServiceNew(Service service);

    public boolean replaceService(Service newService, Service oldService);

    public boolean changeServiceName(Service serviceToChangeAt,String newName);

    public boolean makePayment(String serviceName,double amount);

    public boolean makeDelivery(String serviceName,String address);
}
