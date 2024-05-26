package com.example.trading_system.service;

import com.example.trading_system.domain.externalservices.Service;
import org.springframework.http.ResponseEntity;

public interface ExternalServices {
    boolean addService(Service service);
    ResponseEntity<String> addServiceNew(Service service);

    boolean replaceService(Service newService, Service oldService);

    boolean changeServiceName(Service serviceToChangeAt,String newName);

    boolean makePayment(String serviceName,double amount);

    boolean makeDelivery(String serviceName,String address);
}
