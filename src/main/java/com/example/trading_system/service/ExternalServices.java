package com.example.trading_system.service;

import com.example.trading_system.domain.externalservices.Service;
import org.springframework.http.ResponseEntity;

public interface ExternalServices {
    ResponseEntity<String> addService(String serviceName);


    ResponseEntity<String> replaceService(String newServiceName, String oldServiceName);

    ResponseEntity<String> changeServiceName(String serviceToChangeAtName,String newName);

    ResponseEntity<String> makePayment(String serviceName,double amount);

    ResponseEntity<String> makeDelivery(String serviceName,String address);
}
