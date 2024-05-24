package com.example.trading_system.service;

import com.example.trading_system.domain.externalservices.Service;
import org.springframework.http.ResponseEntity;

public interface ExternalServices {
    ResponseEntity<String> addService(Service service);


    ResponseEntity<String> replaceService(Service newService, Service oldService);

    ResponseEntity<String> changeServiceName(Service serviceToChangeAt,String newName);

    ResponseEntity<String> makePayment(String serviceName,double amount);

    ResponseEntity<String> makeDelivery(String serviceName,String address);
}
