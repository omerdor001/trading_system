package com.example.trading_system.Service;

import com.example.trading_system.Domain.externalservices.DeliveryService;
import com.example.trading_system.Domain.externalservices.PaymentService;
import com.example.trading_system.Domain.externalservices.Service;
import com.example.trading_system.Domain.externalservices.ServiceFacade;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import javax.management.InstanceAlreadyExistsException;


class ExternalServicesAcceptanceTest {
    private ExternalServices externalServices;
    private ServiceFacade serviceFacade;


    @BeforeEach
    public void setUp() {
        serviceFacade=mock(ServiceFacade.class);
        externalServices=new ExternalServices(serviceFacade);
    }

    @Test
    void addService_Success() throws InstanceAlreadyExistsException {
        Service payment1=new PaymentService("Paypal");
        when(serviceFacade.addService(payment1)).thenReturn(true);
        boolean result=externalServices.addService(payment1);
        assertEquals(result,true);
    }

    @Test
    void addService_AlreadyExist() throws InstanceAlreadyExistsException {
        Service payment1=new PaymentService("Paypal");
        when(serviceFacade.addService(payment1)).thenReturn(false);
        externalServices.addService(payment1);
        boolean result=externalServices.addService(payment1);
        assertEquals(result,false);
    }

    @Test
    void replaceService_Success() {
        Service delivery1=new DeliveryService("Flex");
        Service delivery2=new DeliveryService("Parcel");
        externalServices.addService(delivery1);
        when(serviceFacade.replaceService(delivery2,delivery1)).thenReturn(true);
        boolean result=externalServices.replaceService(delivery2,delivery1);
        assertEquals(result,true);
    }

    @Test
    void replaceService_NotExistOld() {
        Service delivery1=new DeliveryService("Flex");
        Service delivery2=new DeliveryService("Parcel");
        when(serviceFacade.replaceService(delivery2,delivery1)).thenReturn(false);
        boolean result=externalServices.replaceService(delivery2,delivery1);
        assertEquals(result,false);
    }

    @Test
    void replaceService_AlreadyExistNew() {
        Service delivery1=new DeliveryService("Flex");
        Service delivery2=new DeliveryService("Parcel");
        externalServices.addService(delivery1);
        externalServices.addService(delivery2);
        when(serviceFacade.replaceService(delivery2,delivery1)).thenReturn(false);
        boolean result=externalServices.replaceService(delivery2,delivery1);
        assertEquals(result,false);
    }

    @Test
    void changeServiceName_Success() {
        Service payment1=new PaymentService("Paypal");
        externalServices.addService(payment1);
        when(serviceFacade.changeServiceName(payment1,"Paypal1")).thenReturn(true);
        boolean result=externalServices.changeServiceName(payment1,"Paypal1");
        assertEquals(result,true);
    }

    @Test
    void changeServiceName_NotExist() {
        Service payment1=new PaymentService("Paypal");
        when(serviceFacade.changeServiceName(payment1,"Paypal1")).thenReturn(false);
        boolean result=externalServices.changeServiceName(payment1,"Paypal1");
        assertEquals(result,false);
    }

    @Test
    void makingPayment_Success() {
        Service payment1=new PaymentService("Paypal");
        externalServices.addService(payment1);
        when(serviceFacade.makePayment("Paypal",100)).thenReturn(true);
        boolean result=externalServices.makePayment("Paypal",100);
        assertEquals(result,true);
    }

    @Test
    void makingPayment_ServiceNotExist() {
        Service payment1=new PaymentService("Paypal");
        when(serviceFacade.makePayment("Paypal",100)).thenReturn(false);
        boolean result=externalServices.makePayment("Paypal",100);
        assertEquals(result,false);
    }

    @Test
    void makingPayment_InvalidAmount() {
        Service payment1=new PaymentService("Paypal");
        when(serviceFacade.makePayment("Paypal",-100)).thenReturn(false);
        boolean result=externalServices.makePayment("Paypal",-100);
        assertEquals(result,false);
    }


}