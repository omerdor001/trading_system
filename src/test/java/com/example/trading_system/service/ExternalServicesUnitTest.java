package com.example.trading_system.service;

import com.example.trading_system.domain.externalservices.*;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.management.InstanceAlreadyExistsException;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;


class ExternalServicesUnitTest {
    private ServiceFacadeImp serviceFacade;

    @BeforeEach
    public void setUp() {
        serviceFacade= ServiceFacadeImp.getInstance();
    }

    @AfterEach
    void tearDown() {
       serviceFacade.clearServices();
    }

    @Test
    void addService_Success() {
        try {
            serviceFacade.addPaymentService("Paypal");
        } catch (InstanceAlreadyExistsException e) {
            fail("InstanceAlreadyExistsException was thrown");
        }
        assertTrue(serviceFacade.findServiceByName("Paypal"));
    }

    @Test
    void addService_AlreadyExist() {
        String errorMsg="";
        try {
            serviceFacade.addPaymentService("Paypal");
            serviceFacade.addPaymentProxyService("Paypal");
        } catch (InstanceAlreadyExistsException e) {
            errorMsg=e.getMessage();
        }
        assertEquals(errorMsg,"This service already exist");
    }

    @Test
    void replaceService_Success() {
//        Service delivery1=new DeliveryService("Flex");
//        Service delivery2=new DeliveryService("Parcel");
//        facade.addPaymentService(delivery1.getServiceName(),"testUser","111111");
//        when(facade.replaceService(delivery2.getServiceName(),delivery1.getServiceName(),"testUser","111111")).thenReturn(new ResponseEntity<>("Success adding external service", HttpStatus.OK));
//        ResponseEntity<String> result=facade.replaceService(delivery2.getServiceName(),delivery1.getServiceName(),"testUser","111111");
//        assertEquals(new ResponseEntity<>("Success adding external service", HttpStatus.OK),result);
    }

    @Test
    void replaceService_NotExistOld() {
//        Service delivery1=new DeliveryService("Flex");
//        Service delivery2=new DeliveryService("Parcel");
//        when(facade.replaceService(delivery2.getServiceName(),delivery1.getServiceName(),"testUser","111111")).thenReturn(new ResponseEntity<>("Service is not exist", HttpStatus.BAD_REQUEST));
//        ResponseEntity<String> result=facade.replaceService(delivery2.getServiceName(),delivery1.getServiceName(),"testUser","111111");
//        assertEquals(new ResponseEntity<>("Service is not exist", HttpStatus.BAD_REQUEST),result);
    }

    @Test
    void replaceService_AlreadyExistNew()  {
//        Service delivery1=new DeliveryService("Flex");
//        Service delivery2=new DeliveryService("Parcel");
//        facade.addDeliveryService(delivery1.getServiceName(),"testUser","111111");
//        facade.addDeliveryService(delivery2.getServiceName(),"testUser","111111");
//        when(facade.replaceService(delivery2.getServiceName(),delivery1.getServiceName(),"testUser","111111")).thenReturn(new ResponseEntity<>("Service is exist (no need to replace)", HttpStatus.BAD_REQUEST));
//        ResponseEntity<String> result=facade.replaceService(delivery2.getServiceName(),delivery1.getServiceName(),"testUser","111111");
//        assertEquals(new ResponseEntity<>("Service is exist (no need to replace)", HttpStatus.BAD_REQUEST),result);
    }

    @Test
    void changeServiceName_Success() {
        try {
            serviceFacade.addPaymentService("Paypal1");
            serviceFacade.changeServiceName("Paypal1","PayPal");
        } catch (InstanceAlreadyExistsException e) {
            fail("InstanceAlreadyExistsException was thrown");
        }
        assertEquals(serviceFacade.findServiceByName("Paypal1"),false);
    }

    @Test
    void changeServiceName_NotExist() {
        String errorMsg="";
        try {
            serviceFacade.changeServiceName("Paypal1","PayPal");
            } catch (NoSuchElementException e) {
            errorMsg=e.getMessage();
        }
        assertEquals(errorMsg,"Service is not exist");
    }

    @Test
    void makingPayment_Success() {
        try {
            serviceFacade.addPaymentProxyService("PayPal");
            serviceFacade.makePayment("PayPal",100.0);
        } catch (InstanceAlreadyExistsException e) {
            fail("InstanceAlreadyExistsException was thrown");
        }
        assertDoesNotThrow(() -> serviceFacade.makePayment("PayPal", 100.0));
    }

    @Test
    void makingPayment_ServiceNotExist() {
        String errorMsg="";
        try {
            serviceFacade.makePayment("Paypal",100.0);
        } catch (NoSuchElementException e) {
            errorMsg=e.getMessage();
        }
        assertEquals(errorMsg,"Service is not exist");
    }

    @Test
    void makingPayment_InvalidAmount() {
        String errorMsg="";
        try {
            serviceFacade.addPaymentProxyService("PayPal");
            serviceFacade.makePayment("PayPal",-100.0);
        } catch (IllegalArgumentException e) {
            errorMsg=e.getMessage();
        } catch (InstanceAlreadyExistsException e) {
            errorMsg=e.getMessage();
        }
        assertEquals(errorMsg,"Payment authorization failed");
    }

    @Test
    void makingDelivery_Success() {
        try {
            serviceFacade.addDeliveryProxyService("PayPal");
            serviceFacade.makeDelivery("PayPal","123 Main St, Springfield, IL, 62704");
        } catch (InstanceAlreadyExistsException e) {
            fail("InstanceAlreadyExistsException was thrown");
        }
        assertDoesNotThrow(() -> serviceFacade.makeDelivery("PayPal", "123 Main St, Springfield, IL, 62704"));
    }

    @Test
    void makingDelivery_ServiceNotExist() {
        String errorMsg="";
        try {
            serviceFacade.makeDelivery("PayPal","123 Main St, Springfield, IL, 62704");
        }
        catch (NoSuchElementException e) {
            errorMsg=e.getMessage();
        }
        assertEquals(errorMsg,"Service is not exist");
    }

    @Test
    void makingPayment_InvalidAddress() {
        String errorMsg="";
        try {
            serviceFacade.addDeliveryProxyService("PayPal");
            serviceFacade.makeDelivery("PayPal","Invalid Address");
        } catch (IllegalArgumentException e) {
            errorMsg=e.getMessage();
        } catch (InstanceAlreadyExistsException e) {
            errorMsg=e.getMessage();
        }
        assertEquals(errorMsg,"Delivery authorization failed");
    }






}