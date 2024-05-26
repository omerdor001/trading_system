package com.example.trading_system.service;
import com.example.trading_system.domain.externalservices.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.management.InstanceAlreadyExistsException;

class ExternalServicesUnitTest {
    private ServiceFacade serviceFacade;
    @BeforeEach
    public void setUp() {
        serviceFacade= ServiceFacadeImp.getInstance();
    }

    @Test
    void addService_Success() throws InstanceAlreadyExistsException {
        Service payment1=new PaymentService("Paypal");
        boolean result=serviceFacade.addService(payment1);
        assertEquals(result,true);
    }

    @Test
    void addService_AlreadyExist() throws InstanceAlreadyExistsException {
        Service payment1=new PaymentService("Paypal");
        serviceFacade.addService(payment1);
        Exception exception = assertThrows(Exception.class, () -> serviceFacade.addService(payment1));
        assertEquals("This service already exist", exception.getMessage());
    }

    @Test
    void replaceService_Success() throws InstanceAlreadyExistsException {
        Service delivery1=new DeliveryService("Flex");
        Service delivery2=new DeliveryService("Parcel");
        serviceFacade.addService(delivery1);
        boolean result=serviceFacade.replaceService(delivery2,delivery1);
        assertEquals(result,true);
    }

    @Test
    void replaceService_NotExistOld() {
        Service delivery1=new DeliveryService("Flex");
        Service delivery2=new DeliveryService("Parcel");
        Exception exception = assertThrows(Exception.class, () -> serviceFacade.replaceService(delivery2,delivery1));
        assertEquals("Service is not exist", exception.getMessage());
    }

    @Test
    void replaceService_AlreadyExistNew() throws InstanceAlreadyExistsException {
        Service delivery1=new DeliveryService("Flex");
        Service delivery2=new DeliveryService("Parcel");
        serviceFacade.addService(delivery1);
        serviceFacade.addService(delivery2);
        Exception exception = assertThrows(Exception.class, () -> serviceFacade.replaceService(delivery2,delivery1));
        assertEquals("Service is exist (no need to replace)", exception.getMessage());
    }

    @Test
    void changeServiceName_Success() throws InstanceAlreadyExistsException {
        Service payment1=new PaymentService("Paypal");
        serviceFacade.addService(payment1);
        boolean result=serviceFacade.changeServiceName(payment1,"Paypal1");
        assertEquals(result,true);
    }

    @Test
    void changeServiceName_NotExist() {
        Service payment1=new PaymentService("Paypal");
        Exception exception = assertThrows(Exception.class, () -> serviceFacade.changeServiceName(payment1,"Paypal1"));
        assertEquals("Service is not exist", exception.getMessage());
    }

    @Test
    void makingPayment_Success() throws InstanceAlreadyExistsException {
        Service payment1=new PaymentService("Paypal");
        serviceFacade.addService(payment1);
        boolean result=serviceFacade.makePayment("Paypal",100);
        assertEquals(result,true);
    }

    @Test
    void makingPayment_ServiceNotExist() {
        Service payment1=new PaymentService("Paypal");
        Exception exception = assertThrows(Exception.class, () -> serviceFacade.makePayment("Paypal",100));
        assertEquals("Service is not exist", exception.getMessage());
    }

    @Test
    void makingPayment_InvalidAmount() throws InstanceAlreadyExistsException {
        Service payment1=new PaymentService("Paypal");
        serviceFacade.addService(payment1);
        Exception exception = assertThrows(Exception.class, () -> serviceFacade.makePayment("Paypal",-100));
        assertEquals("Payment authorization failed", exception.getMessage());
    }

    @Test
    void makingDelivery_Success() throws InstanceAlreadyExistsException {
        Service delivery1=new DeliveryService("Parcel");
        serviceFacade.addService(delivery1);
        boolean result=serviceFacade.makeDelivery("Parcel","123 Main St, Springfield, IL, 62704");
        assertEquals(result,true);
    }

    @Test
    void makingDelivery_ServiceNotExist() {
        Service delivery1=new DeliveryService("Parcel");
        Exception exception = assertThrows(Exception.class, () -> serviceFacade.makeDelivery("Parcel","123 Main St, Springfield, IL, 62704"));
        assertEquals("Service is not exist", exception.getMessage());
    }

    @Test
    void makingDelivery_InvalidAddress() throws InstanceAlreadyExistsException {
        Service delivery1=new DeliveryService("Parcel");
        serviceFacade.addService(delivery1);
        Exception exception = assertThrows(Exception.class, () -> serviceFacade.makeDelivery("Parcel","Invalid Address"));
        assertEquals("Delivery authorization failed", exception.getMessage());
    }



}