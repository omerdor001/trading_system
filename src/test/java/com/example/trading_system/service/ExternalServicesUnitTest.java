package com.example.trading_system.service;
import com.example.trading_system.domain.externalservices.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.management.InstanceAlreadyExistsException;

class ExternalServicesUnitTest {
    private ExternalServices externalServices;
    @BeforeEach
    public void setUp() {
        ServiceFacade serviceFacade=new ServiceFacadeImp();
        externalServices=new ExternalServices(serviceFacade);
    }

    @Test
    void addService_Success() throws InstanceAlreadyExistsException {
        Service payment1=new PaymentService("Paypal");
        boolean result=externalServices.addService(payment1);
        assertEquals(result,true);
    }

    @Test
    void addService_AlreadyExist(){
        Service payment1=new PaymentService("Paypal");
        externalServices.addService(payment1);
        boolean result=externalServices.addService(payment1);
        assertEquals(result,false);
    }

    @Test
    void replaceService_Success() {
        Service delivery1=new DeliveryService("Flex");
        Service delivery2=new DeliveryService("Parcel");
        externalServices.addService(delivery1);
        boolean result=externalServices.replaceService(delivery2,delivery1);
        assertEquals(result,true);
    }

    @Test
    void replaceService_NotExistOld() {
        Service delivery1=new DeliveryService("Flex");
        Service delivery2=new DeliveryService("Parcel");
        boolean result=externalServices.replaceService(delivery2,delivery1);
        assertEquals(result,false);
    }

    @Test
    void replaceService_AlreadyExistNew() {
        Service delivery1=new DeliveryService("Flex");
        Service delivery2=new DeliveryService("Parcel");
        externalServices.addService(delivery1);
        externalServices.addService(delivery2);
        boolean result=externalServices.replaceService(delivery2,delivery1);
        assertEquals(result,false);
    }

    @Test
    void changeServiceName_Success() {
        Service payment1=new PaymentService("Paypal");
        externalServices.addService(payment1);
        boolean result=externalServices.changeServiceName(payment1,"Paypal1");
        assertEquals(result,true);
    }

    @Test
    void changeServiceName_NotExist() {
        Service payment1=new PaymentService("Paypal");
        boolean result=externalServices.changeServiceName(payment1,"Paypal1");
        assertEquals(result,false);
    }

    @Test
    void makingPayment_Success() {
        Service payment1=new PaymentService("Paypal");
        externalServices.addService(payment1);
        boolean result=externalServices.makePayment("Paypal",100);
        assertEquals(result,true);
    }

    @Test
    void makingPayment_ServiceNotExist() {
        Service payment1=new PaymentService("Paypal");
        boolean result=externalServices.makePayment("Paypal",100);
        assertEquals(result,false);
    }

    @Test
    void makingPayment_InvalidAmount() {
        Service payment1=new PaymentService("Paypal");
        boolean result=externalServices.makePayment("Paypal",-100);
        assertEquals(result,false);
    }

    @Test
    void makingDelivery_Success() {
        Service delivery1=new DeliveryService("Parcel");
        externalServices.addService(delivery1);
        boolean result=externalServices.makeDelivery("Parcel","123 Main St, Springfield, IL, 62704");
        assertEquals(result,true);
    }

    @Test
    void makingDelivery_ServiceNotExist() {
        Service delivery1=new DeliveryService("Parcel");
        boolean result=externalServices.makeDelivery("Parcel","123 Main St, Springfield, IL, 62704");
        assertEquals(result,false);
    }

    @Test
    void makingDelivery_InvalidAddress() {
        Service delivery1=new DeliveryService("Parcel");
        externalServices.addService(delivery1);
        boolean result=externalServices.makeDelivery("Parcel","Invalid Address");
        assertEquals(result,false);
    }



}