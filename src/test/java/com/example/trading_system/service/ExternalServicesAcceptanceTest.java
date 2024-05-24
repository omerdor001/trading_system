package com.example.trading_system.service;

import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.externalservices.Service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;

import javax.management.InstanceAlreadyExistsException;


class ExternalServicesAcceptanceTest {
    private Facade facade;


    @BeforeEach
    public void setUp() {
        facade=mock(Facade.class);
    }

    @Test
    void addService_Success() {
        Service payment1=new PaymentService("Paypal");
        when(facade.addService(payment1)).thenReturn(new ResponseEntity<>("Success adding external service", HttpStatus.OK));
        ResponseEntity<String> result=facade.addService(payment1);
        assertEquals(new ResponseEntity<>("Success adding external service", HttpStatus.OK),result);
    }

    @Test
    void addService_AlreadyExist() {
        Service payment1=new PaymentService("Paypal");
        when(facade.addService(payment1)).thenReturn(new ResponseEntity<>("This service already exist", HttpStatus.BAD_REQUEST));
        facade.addService(payment1);
        ResponseEntity<String> result=facade.addService(payment1);
        assertEquals(new ResponseEntity<>("This service already exist", HttpStatus.BAD_REQUEST),result);
    }

    @Test
    void replaceService_Success() {
        Service delivery1=new DeliveryService("Flex");
        Service delivery2=new DeliveryService("Parcel");
        facade.addService(delivery1);
        when(facade.replaceService(delivery2,delivery1)).thenReturn(new ResponseEntity<>("Success adding external service", HttpStatus.OK));
        ResponseEntity<String> result=facade.replaceService(delivery2,delivery1);
        assertEquals(new ResponseEntity<>("Success adding external service", HttpStatus.OK),result);
    }

    @Test
    void replaceService_NotExistOld() {
        Service delivery1=new DeliveryService("Flex");
        Service delivery2=new DeliveryService("Parcel");
        when(facade.replaceService(delivery2,delivery1)).thenReturn(new ResponseEntity<>("Service is not exist", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> result=facade.replaceService(delivery2,delivery1);
        assertEquals(new ResponseEntity<>("Service is not exist", HttpStatus.BAD_REQUEST),result);
    }

    @Test
    void replaceService_AlreadyExistNew()  {
        Service delivery1=new DeliveryService("Flex");
        Service delivery2=new DeliveryService("Parcel");
        facade.addService(delivery1);
        facade.addService(delivery2);
        when(facade.replaceService(delivery2,delivery1)).thenReturn(new ResponseEntity<>("Service is exist (no need to replace)", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> result=facade.replaceService(delivery2,delivery1);
        assertEquals(new ResponseEntity<>("Service is exist (no need to replace)", HttpStatus.BAD_REQUEST),result);
    }

    @Test
    void changeServiceName_Success() {
        Service payment1=new PaymentService("Paypal");
        facade.addService(payment1);
        when(facade.changeServiceName(payment1,"Paypal1")).thenReturn(new ResponseEntity<>("Success changing external service name", HttpStatus.OK));
        ResponseEntity<String> result=facade.changeServiceName(payment1,"Paypal1");
        assertEquals(new ResponseEntity<>("Success changing external service name", HttpStatus.OK),result);
    }

    @Test
    void changeServiceName_NotExist() {
        Service payment1=new PaymentService("Paypal");
        when(facade.changeServiceName(payment1,"Paypal1")).thenReturn(new ResponseEntity<>("Service is not exist", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> result=facade.changeServiceName(payment1,"Paypal1");
        assertEquals(new ResponseEntity<>("Service is not exist", HttpStatus.BAD_REQUEST),result);
    }

    @Test
    void makingPayment_Success() {
        Service payment1=new PaymentService("Paypal");
        facade.addService(payment1);
        when(facade.makePayment("Paypal",100)).thenReturn(new ResponseEntity<>("Success making payment", HttpStatus.OK));
        ResponseEntity<String> result=facade.makePayment("Paypal",100);
        assertEquals(new ResponseEntity<>("Success making payment", HttpStatus.OK),result);
    }

    @Test
    void makingPayment_ServiceNotExist() {
        Service payment1=new PaymentService("Paypal");
        facade.addService(payment1);
        when(facade.makePayment("Paypal",100)).thenReturn(new ResponseEntity<>("Service is not exist", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> result=facade.makePayment("Paypal",100);
        assertEquals(new ResponseEntity<>("Service is not exist", HttpStatus.BAD_REQUEST),result);
    }

    @Test
    void makingPayment_InvalidAmount() {
        Service payment1=new PaymentService("Paypal");
        when(facade.makePayment("Paypal",-100)).thenReturn(new ResponseEntity<>("Payment authorization failed", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> result=facade.makePayment("Paypal",-100);
        assertEquals(new ResponseEntity<>("Payment authorization failed", HttpStatus.BAD_REQUEST),result);
    }

    @Test
    void makingDelivery_Success() {
        Service delivery1=new DeliveryService("Parcel");
        facade.addService(delivery1);
        when(facade.makeDelivery("Parcel","123 Main St, Springfield, IL, 62704")).thenReturn(new ResponseEntity<>("Success making delivery", HttpStatus.OK));
        ResponseEntity<String> result=facade.makeDelivery("Parcel","123 Main St, Springfield, IL, 62704");
        assertEquals(new ResponseEntity<>("Success making delivery", HttpStatus.OK),result);
    }

    @Test
    void makingDelivery_ServiceNotExist() {
        Service delivery1=new DeliveryService("Parcel");
        when(facade.makeDelivery("Parcel","123 Main St, Springfield, IL, 62704")).thenReturn(new ResponseEntity<>("Service is not exist", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> result=facade.makeDelivery("Parcel","123 Main St, Springfield, IL, 62704");
        assertEquals(new ResponseEntity<>("Service is not exist", HttpStatus.BAD_REQUEST),result);
    }

    @Test
    void makingPayment_InvalidAddress() {
        Service delivery1=new DeliveryService("Parcel");
        facade.addService(delivery1);
        when(facade.makeDelivery("Parcel","Invalid Address")).thenReturn(new ResponseEntity<>("Delivery authorization failed", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> result=facade.makeDelivery("Parcel","Invalid Address");
        assertEquals(new ResponseEntity<>("Delivery authorization failed", HttpStatus.BAD_REQUEST),result);
    }




}