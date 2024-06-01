//package com.example.trading_system.AcceptanceTests.Services;
//import com.example.trading_system.domain.externalservices.*;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import com.example.trading_system.domain.externalservices.PaymentService;
//
//import com.example.trading_system.service.TradingSystemImp;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatusCode;
//import org.springframework.http.ResponseEntity;
//
//import java.time.LocalDate;
//
//class ExternalServicesAcceptanceTest {
//    private TradingSystemImp facade;
//    String token1;
//    String token;
//
//    @BeforeEach
//    public void setUp()  {
//        facade= TradingSystemImp.getInstance();
//        facade.register(0, "testuser1", "password123", LocalDate.now());
//        facade.openSystem();
//        token1 = facade.enter().getBody();
//        token=facade.login(token1,0,"testuser1","password123").getBody();
//    }
//
//    @AfterEach
//    void tearDown() {
//        facade.clearServices();
//    }
//
//    @Test
//    void addService_Success(){
//        PaymentService payment1=new PaymentService("Paypal");
//        ResponseEntity<String> result=facade.addPaymentService(payment1.getServiceName());
//        assertEquals(new ResponseEntity<>("Success adding external payment service", HttpStatus.OK),result);
//    }
//
//    @Test
//    void addService_AlreadyExist() {
//        PaymentService payment1=new PaymentService("Paypal");
//        facade.addPaymentService(payment1.getServiceName());
//        ResponseEntity<String> result=facade.addPaymentService(payment1.getServiceName());
//        assertEquals(new ResponseEntity<>("This service already exist", HttpStatus.BAD_REQUEST),result);
//    }
//
//
//    @Test
//    void replaceService_Success() {
//        Service delivery1=new DeliveryService("Flex");
//        Service delivery2=new DeliveryService("Parcel");
//        facade.addPaymentService(delivery1.getServiceName());
//        ResponseEntity<String> result=facade.replaceService(delivery2.getServiceName(),delivery1.getServiceName());
//        assertEquals(new ResponseEntity<>("Service replaced successfully.", HttpStatus.OK),result);
//    }
//
//    @Test
//    void replaceService_NotExistOld() {
//        Service delivery1=new DeliveryService("Flex");
//        Service delivery2=new DeliveryService("Parcel");
//        ResponseEntity<String> result=facade.replaceService(delivery2.getServiceName(),delivery1.getServiceName());
//        assertEquals(new ResponseEntity<>("Service is not exist", HttpStatus.valueOf(500)),result);
//    }
//
//    @Test
//    void replaceService_AlreadyExistNew() {
//        Service delivery1=new DeliveryService("Flex");
//        Service delivery2=new DeliveryService("Parcel");
//        facade.addDeliveryService(delivery1.getServiceName());
//        facade.addDeliveryService(delivery2.getServiceName());
//        ResponseEntity<String> result=facade.replaceService(delivery2.getServiceName(),delivery1.getServiceName());
//        assertEquals(new ResponseEntity<>("Service is exist (no need to replace)", HttpStatus.valueOf(500)),result);
//    }
//
//    @Test
//    void changeServiceName_Success() {
//        Service payment1=new PaymentService("Paypal");
//        facade.addPaymentService(payment1.getServiceName());
//        ResponseEntity<String> result=facade.changeServiceName(payment1.getServiceName(),"Paypal1");
//        assertEquals(new ResponseEntity<>("Service name changed successfully.", HttpStatus.OK),result);
//    }
//
//
//    @Test
//    void changeServiceName_NotExist() {
//        Service payment1=new PaymentService("Paypal");
//        ResponseEntity<String> result=facade.changeServiceName(payment1.getServiceName(),"Paypal1");
//        assertEquals(new ResponseEntity<>("Service is not exist", HttpStatus.valueOf(500)),result);
//    }
//
//    @Test
//    void makingPayment_Success() {
//        Service payment1=new PaymentService("Paypal");
//        facade.addPaymentService(payment1.getServiceName());
//        ResponseEntity<String> result=facade.makePayment("Paypal",100);
//        assertEquals(new ResponseEntity<>("Payment made successfully.", HttpStatus.OK),result);
//    }
//
//    @Test
//    void makingPayment_ServiceNotExist() {
//        Service payment1=new PaymentService("Paypal");
//        ResponseEntity<String> result=facade.makePayment("Paypal",100);
//        assertEquals(new ResponseEntity<>("Service is not exist", HttpStatus.valueOf(500)),result);
//    }
//
//    @Test
//    void makingPayment_InvalidAmount() {
//        Service payment1=new PaymentService("Paypal");
//        facade.addPaymentService(payment1.getServiceName());
//        ResponseEntity<String> result=facade.makePayment("Paypal",-100);
//        assertEquals(new ResponseEntity<>("Payment authorization failed", HttpStatus.valueOf(500)),result);
//    }
//
//    @Test
//    void makingDelivery_Success() {
//        Service delivery1=new DeliveryService("Parcel");
//        facade.addDeliveryService(delivery1.getServiceName());
//        ResponseEntity<String> result=facade.makeDelivery("Parcel","123 Main St, Springfield, IL, 62704");
//        assertEquals(new ResponseEntity<>("Delivery made successfully.", HttpStatus.valueOf(200)),result);
//    }
//
//    @Test
//    void makingDelivery_ServiceNotExist() {
//        Service delivery1=new DeliveryService("Parcel");
//        ResponseEntity<String> result=facade.makeDelivery("Parcel","123 Main St, Springfield, IL, 62704");
//        assertEquals(new ResponseEntity<>("Service is not exist", HttpStatus.valueOf(500)),result);
//    }
//
//    @Test
//    void makingDelivery_InvalidAddress() {
//        Service delivery1=new DeliveryService("Parcel");
//        facade.addDeliveryService(delivery1.getServiceName());
//        ResponseEntity<String> result=facade.makeDelivery("Parcel","Invalid Address");
//        assertEquals(new ResponseEntity<>("Delivery authorization failed", HttpStatusCode.valueOf(500)),result);
//    }
//
//
//
//}