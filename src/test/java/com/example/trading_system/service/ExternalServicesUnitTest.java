package com.example.trading_system.service;
import com.example.trading_system.domain.externalservices.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.management.InstanceAlreadyExistsException;

class ExternalServicesUnitTest {
    private ExternalServices externalServices;
    @BeforeEach
    public void setUp() {
        externalServices=new ExternalServicesImp(new ServiceFacadeImp());
    }

    @Test
    void addService_Success(){
        Service payment1=new PaymentService("Paypal");
        ResponseEntity<String> result=externalServices.addService(payment1);
        assertEquals(new ResponseEntity<>("Success adding external service", HttpStatus.OK),result);
    }

    @Test
    void addService_AlreadyExist() {
        Service payment1=new PaymentService("Paypal");
        externalServices.addService(payment1);
        ResponseEntity<String> result=externalServices.addService(payment1);
        assertEquals(new ResponseEntity<>("This service already exist", HttpStatus.BAD_REQUEST),result);
    }

    @Test
    void replaceService_Success() {
        Service delivery1=new DeliveryService("Flex");
        Service delivery2=new DeliveryService("Parcel");
        externalServices.addService(delivery1);
        ResponseEntity<String> result=externalServices.replaceService(delivery2,delivery1);
        assertEquals(new ResponseEntity<>("Success replacing external service", HttpStatus.OK),result);
    }

    @Test
    void replaceService_NotExistOld() {
        Service delivery1=new DeliveryService("Flex");
        Service delivery2=new DeliveryService("Parcel");
        ResponseEntity<String> result=externalServices.replaceService(delivery2,delivery1);
        assertEquals(new ResponseEntity<>("Service is not exist", HttpStatus.BAD_REQUEST),result);
    }

    @Test
    void replaceService_AlreadyExistNew() {
        Service delivery1=new DeliveryService("Flex");
        Service delivery2=new DeliveryService("Parcel");
        externalServices.addService(delivery1);
        externalServices.addService(delivery2);
        ResponseEntity<String> result=externalServices.replaceService(delivery2,delivery1);
        assertEquals(new ResponseEntity<>("Service is exist (no need to replace)", HttpStatus.BAD_REQUEST),result);
    }

    @Test
    void changeServiceName_Success() {
        Service payment1=new PaymentService("Paypal");
        externalServices.addService(payment1);
        ResponseEntity<String> result=externalServices.changeServiceName(payment1,"Paypal1");
        assertEquals(new ResponseEntity<>("Success changing external service name", HttpStatus.OK),result);
    }

    @Test
    void changeServiceName_NotExist() {
        Service payment1=new PaymentService("Paypal");
        ResponseEntity<String> result=externalServices.changeServiceName(payment1,"Paypal1");
        assertEquals(new ResponseEntity<>("Service is not exist", HttpStatus.BAD_REQUEST),result);
    }

    @Test
    void makingPayment_Success() {
        Service payment1=new PaymentService("Paypal");
        externalServices.addService(payment1);
        ResponseEntity<String> result=externalServices.makePayment("Paypal",100);
        assertEquals(new ResponseEntity<>("Success making payment", HttpStatus.OK),result);
    }

    @Test
    void makingPayment_ServiceNotExist() {
        Service payment1=new PaymentService("Paypal");
        ResponseEntity<String> result=externalServices.makePayment("Paypal",100);
        assertEquals(new ResponseEntity<>("Service is not exist", HttpStatus.BAD_REQUEST),result);
    }

    @Test
    void makingPayment_InvalidAmount() {
        Service payment1=new PaymentService("Paypal");
        externalServices.addService(payment1);
        ResponseEntity<String> result=externalServices.makePayment("Paypal",-100);
        assertEquals(new ResponseEntity<>("Payment authorization failed", HttpStatus.BAD_REQUEST),result);
    }

    @Test
    void makingDelivery_Success() {
        Service delivery1=new DeliveryService("Parcel");
        externalServices.addService(delivery1);
        ResponseEntity<String> result=externalServices.makeDelivery("Parcel","123 Main St, Springfield, IL, 62704");
        assertEquals(new ResponseEntity<>("Success making delivery", HttpStatus.OK),result);
    }

    @Test
    void makingDelivery_ServiceNotExist() {
        Service delivery1=new DeliveryService("Parcel");
        ResponseEntity<String> result=externalServices.makeDelivery("Parcel","123 Main St, Springfield, IL, 62704");
        assertEquals(new ResponseEntity<>("Service is not exist", HttpStatus.BAD_REQUEST),result);
    }

    @Test
    void makingDelivery_InvalidAddress() {
        Service delivery1=new DeliveryService("Parcel");
        externalServices.addService(delivery1);
        ResponseEntity<String> result=externalServices.makeDelivery("Parcel","Invalid Address");
        assertEquals(new ResponseEntity<>("Delivery authorization failed", HttpStatus.BAD_REQUEST),result);
    }



}