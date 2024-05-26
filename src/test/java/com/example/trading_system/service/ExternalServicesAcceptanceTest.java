package com.example.trading_system.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.*;


class ExternalServicesAcceptanceTest {
    private TradingSystemImp facade;


    @BeforeEach
    public void setUp() {
        facade=mock(TradingSystemImp.class);
    }

//    @Test
//    void addService_Success() throws InstanceAlreadyExistsException {
//        Service payment1=new PaymentService("Paypal");
//        when(facade.addService(payment1)).thenReturn(true);
//        boolean result=facade.addService(payment1);
//        assertEquals(result,true);
//    }
//
//    @Test
//    void addService_AlreadyExist() throws InstanceAlreadyExistsException {
//        Service payment1=new PaymentService("Paypal");
//        when(facade.addService(payment1)).thenReturn(false);
//        facade.addService(payment1);
//        boolean result=facade.addService(payment1);
//        assertEquals(result,false);
//    }
//
//    @Test
//    void replaceService_Success() throws InstanceAlreadyExistsException {
//        Service delivery1=new DeliveryService("Flex");
//        Service delivery2=new DeliveryService("Parcel");
//        facade.addService(delivery1);
//        when(facade.replaceService(delivery2,delivery1)).thenReturn(true);
//        boolean result=facade.replaceService(delivery2,delivery1);
//        assertEquals(result,true);
//    }
//
//    @Test
//    void replaceService_NotExistOld() {
//        Service delivery1=new DeliveryService("Flex");
//        Service delivery2=new DeliveryService("Parcel");
//        when(facade.replaceService(delivery2,delivery1)).thenReturn(false);
//        boolean result=facade.replaceService(delivery2,delivery1);
//        assertEquals(result,false);
//    }
//
//    @Test
//    void replaceService_AlreadyExistNew() throws InstanceAlreadyExistsException {
//        Service delivery1=new DeliveryService("Flex");
//        Service delivery2=new DeliveryService("Parcel");
//        facade.addService(delivery1);
//        facade.addService(delivery2);
//        when(facade.replaceService(delivery2,delivery1)).thenReturn(false);
//        boolean result=facade.replaceService(delivery2,delivery1);
//        assertEquals(result,false);
//    }
//
//    @Test
//    void changeServiceName_Success() throws InstanceAlreadyExistsException {
//        Service payment1=new PaymentService("Paypal");
//        facade.addService(payment1);
//        when(facade.changeServiceName(payment1,"Paypal1")).thenReturn(true);
//        boolean result=facade.changeServiceName(payment1,"Paypal1");
//        assertEquals(result,true);
//    }
//
//    @Test
//    void changeServiceName_NotExist() {
//        Service payment1=new PaymentService("Paypal");
//        when(facade.changeServiceName(payment1,"Paypal1")).thenReturn(false);
//        boolean result=facade.changeServiceName(payment1,"Paypal1");
//        assertEquals(result,false);
//    }
//
//    @Test
//    void makingPayment_Success() throws InstanceAlreadyExistsException {
//        Service payment1=new PaymentService("Paypal");
//        facade.addService(payment1);
//        when(facade.makePayment("Paypal",100)).thenReturn(true);
//        boolean result=facade.makePayment("Paypal",100);
//        assertEquals(result,true);
//    }
//
//    @Test
//    void makingPayment_ServiceNotExist() throws InstanceAlreadyExistsException {
//        Service payment1=new PaymentService("Paypal");
//        facade.addService(payment1);
//        when(facade.makePayment("Paypal",100)).thenReturn(false);
//        boolean result=facade.makePayment("Paypal",100);
//        assertEquals(result,false);
//    }
//
//    @Test
//    void makingPayment_InvalidAmount() {
//        Service payment1=new PaymentService("Paypal");
//        when(facade.makePayment("Paypal",-100)).thenReturn(false);
//        boolean result=facade.makePayment("Paypal",-100);
//        assertEquals(result,false);
//    }
//
//    @Test
//    void makingDelivery_Success() throws InstanceAlreadyExistsException {
//        Service delivery1=new DeliveryService("Parcel");
//        facade.addService(delivery1);
//        when(facade.makeDelivery("Parcel","123 Main St, Springfield, IL, 62704")).thenReturn(true);
//        boolean result=facade.makeDelivery("Parcel","123 Main St, Springfield, IL, 62704");
//        assertEquals(result,true);
//    }
//
//    @Test
//    void makingDelivery_ServiceNotExist() {
//        Service delivery1=new DeliveryService("Parcel");
//        when(facade.makeDelivery("Parcel","123 Main St, Springfield, IL, 62704")).thenReturn(false);
//        boolean result=facade.makeDelivery("Parcel","123 Main St, Springfield, IL, 62704");
//        assertEquals(result,false);
//    }
//
//    @Test
//    void makingPayment_InvalidAddress() throws InstanceAlreadyExistsException {
//        Service delivery1=new DeliveryService("Parcel");
//        facade.addService(delivery1);
//        when(facade.makeDelivery("Parcel","Invalid Address")).thenReturn(false);
//        boolean result=facade.makeDelivery("Parcel","Invalid Address");
//        assertEquals(result,false);
//    }




}