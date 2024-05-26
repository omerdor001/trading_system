package com.example.trading_system.service;
import com.example.trading_system.domain.externalservices.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;

import java.time.LocalDate;

class ExternalServicesAcceptanceTest {
    private Facade facade;
    String token1;
    String token2;

    @BeforeEach
    public void setUp() throws Exception {
        facade= new Facade();
        token1 = facade.enter();
        token2 = facade.enter();
        facade.register(0, "testuser1", "password123", LocalDate.now());
        facade.register(1, "testuser2", "password124", LocalDate.now());
        facade.openSystem();
        facade.login(token1,1,"testuser2","password124");
    }

    @Test
    void addService_Success(){
        Service payment1=new PaymentService("Paypal");
        ResponseEntity<String> result=facade.addService(payment1.getServiceName(),"v1",token2);
        assertEquals(new ResponseEntity<>("Success adding external service", HttpStatus.OK),result);
    }

    @Test
    void addService_AlreadyExist() {
        Service payment1=new PaymentService("Paypal");
        facade.addService(payment1.getServiceName(),"v0",token1);
        ResponseEntity<String> result=facade.addService(payment1.getServiceName(),"v1",token2);
        assertEquals(new ResponseEntity<>("This service already exist", HttpStatus.BAD_REQUEST),result);
    }

    @Test
    void replaceService_Success() {
        Service delivery1=new DeliveryService("Flex");
        Service delivery2=new DeliveryService("Parcel");
        facade.addService(delivery1.getServiceName(),"v1",token2);
        ResponseEntity<String> result=facade.replaceService(delivery2.getServiceName(),delivery1.getServiceName(),"v1",token2);
        assertEquals(new ResponseEntity<>("Success replacing external service", HttpStatus.OK),result);
    }

    @Test
    void replaceService_NotExistOld() {
        Service delivery1=new DeliveryService("Flex");
        Service delivery2=new DeliveryService("Parcel");
        ResponseEntity<String> result=facade.replaceService(delivery2.getServiceName(),delivery1.getServiceName(),"v1",token2);
        assertEquals(new ResponseEntity<>("Service is not exist", HttpStatus.BAD_REQUEST),result);
    }

    @Test
    void replaceService_AlreadyExistNew() {
        Service delivery1=new DeliveryService("Flex");
        Service delivery2=new DeliveryService("Parcel");
        facade.addService(delivery1.getServiceName(),"v1",token2);
        facade.addService(delivery2.getServiceName(),"v1",token2);
        ResponseEntity<String> result=facade.replaceService(delivery2.getServiceName(),delivery1.getServiceName(),"v1",token2);
        assertEquals(new ResponseEntity<>("Service is exist (no need to replace)", HttpStatus.BAD_REQUEST),result);
    }

    @Test
    void changeServiceName_Success() {
        Service payment1=new PaymentService("Paypal");
        facade.addService(payment1.getServiceName(),"v1",token2);
        ResponseEntity<String> result=facade.changeServiceName(payment1.getServiceName(),"Paypal1","v1",token2);
        assertEquals(new ResponseEntity<>("Success changing external service name", HttpStatus.OK),result);
    }

    @Test
    void changeServiceName_NotExist() {
        Service payment1=new PaymentService("Paypal");
        ResponseEntity<String> result=facade.changeServiceName(payment1.getServiceName(),"Paypal1","v1",token2);
        assertEquals(new ResponseEntity<>("Service is not exist", HttpStatus.BAD_REQUEST),result);
    }

    @Test
    void makingPayment_Success() {
        Service payment1=new PaymentService("Paypal");
        facade.addService(payment1.getServiceName(),"v1",token2);
        ResponseEntity<String> result=facade.makePayment("Paypal",100,"v0",token1);
        assertEquals(new ResponseEntity<>("Success making payment", HttpStatus.OK),result);
    }

    @Test
    void makingPayment_ServiceNotExist() {
        Service payment1=new PaymentService("Paypal");
        ResponseEntity<String> result=facade.makePayment("Paypal",100,"v0",token1);
        assertEquals(new ResponseEntity<>("Service is not exist", HttpStatus.BAD_REQUEST),result);
    }

    @Test
    void makingPayment_InvalidAmount() {
        Service payment1=new PaymentService("Paypal");
        facade.addService(payment1.getServiceName(),"v1",token2);
        ResponseEntity<String> result=facade.makePayment("Paypal",-100,"v0",token1);
        assertEquals(new ResponseEntity<>("Payment authorization failed", HttpStatus.BAD_REQUEST),result);
    }

    @Test
    void makingDelivery_Success() {
        Service delivery1=new DeliveryService("Parcel");
        facade.addService(delivery1.getServiceName(),"v1",token2);
        ResponseEntity<String> result=facade.makeDelivery("Parcel","123 Main St, Springfield, IL, 62704","v0",token1);
        assertEquals(new ResponseEntity<>("Success making delivery", HttpStatus.OK),result);
    }

    @Test
    void makingDelivery_ServiceNotExist() {
        Service delivery1=new DeliveryService("Parcel");
        ResponseEntity<String> result=facade.makeDelivery("Parcel","123 Main St, Springfield, IL, 62704","v0",token1);
        assertEquals(new ResponseEntity<>("Service is not exist", HttpStatus.BAD_REQUEST),result);
    }

    @Test
    void makingDelivery_InvalidAddress() {
        Service delivery1=new DeliveryService("Parcel");
        facade.addService(delivery1.getServiceName(),"v1",token2);
        ResponseEntity<String> result=facade.makeDelivery("Parcel","Invalid Address","v0",token1);
        assertEquals(new ResponseEntity<>("Delivery authorization failed", HttpStatus.BAD_REQUEST),result);
    }



}