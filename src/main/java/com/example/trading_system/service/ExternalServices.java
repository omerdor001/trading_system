package com.example.trading_system.service;

import com.example.trading_system.domain.externalservices.Service;
import org.springframework.http.ResponseEntity;

import javax.management.InstanceAlreadyExistsException;

public interface ExternalServices {
    ResponseEntity<String> addPaymentService(String serviceName) ;
    ResponseEntity<String> addPaymentProxyService(String serviceName) ;
    ResponseEntity<String> addDeliveryService(String serviceName) ;
    ResponseEntity<String> addDeliveryProxyService(String serviceName) ;

    ResponseEntity<String> replaceService(String newServiceName, String oldServiceName);

    ResponseEntity<String> changeServiceName(String serviceToChangeAtName,String newName);

    ResponseEntity<String> makePayment(String serviceName,double amount);

    ResponseEntity<String> makeDelivery(String serviceName,String address);
}
