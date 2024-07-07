package com.example.trading_system.AcceptanceTests.Market.BidAcceptanceTests;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.Store;
import com.example.trading_system.domain.stores.StoreMemoryRepository;
import com.example.trading_system.domain.stores.StoreRepository;
import com.example.trading_system.domain.users.User;
import com.example.trading_system.domain.users.UserMemoryRepository;
import com.example.trading_system.domain.users.UserRepository;
import com.example.trading_system.service.TradingSystem;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class PlaceBidAcceptanceTests {

    private NotificationSender mockNotificationSender;
    private TradingSystem tradingSystem;
    private String ownerToken;
    private String ownerUserName;
    private String regularUserToken;
    private String regularUserName;
    private String secondOwnerUserName = "owner2";
    private String secondOwnerToken;
    private final String password = "123456";
    private final String productName = "Product1";

    private final String storeName = "Store1";
    private UserRepository userRepository;
    private StoreRepository storeRepository;
    private int productID = 111 ;
    private String productDescription = "Product1 Description";

    @BeforeEach
    void setUp() {
        userRepository= UserMemoryRepository.getInstance();    //May be change later
        storeRepository= StoreMemoryRepository.getInstance();  //May be change later
        mockNotificationSender = mock(NotificationSender.class);
        tradingSystem = TradingSystemImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class),mockNotificationSender,userRepository,storeRepository);
        tradingSystem.register("owner1", "password123", LocalDate.now());
        tradingSystem.openSystem(storeRepository);
        String userToken = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            ownerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
        userToken = tradingSystem.login(ownerToken, "v0", "owner1", "password123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            ownerUserName = rootNode.get("username").asText();
            ownerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystem.openStore(ownerUserName, ownerToken, storeName, "General Store");
        tradingSystem.addProduct(ownerUserName, ownerToken, productID, storeName, productName, productDescription, 15, 5, 6, 1, "[\"CarPlay\", \"iPhone\"]");

        tradingSystem.register("regularUser", "password123", LocalDate.now());
        String userToken2 = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken2);
            regularUserToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
        userToken2 = tradingSystem.login(regularUserToken, "v1", "regularUser", "password123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken2);
            regularUserName = rootNode.get("username").asText();
            regularUserToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }


        tradingSystem.register(secondOwnerUserName, "password123", LocalDate.now());
        String userToken3 = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken3);
            secondOwnerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
        userToken3 = tradingSystem.login(regularUserToken, "v2", secondOwnerUserName, "password123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken3);
            secondOwnerUserName = rootNode.get("username").asText();
            secondOwnerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }


    }


    @Test
    void placeOneBid_Success() {

        Assertions.assertEquals(HttpStatus.OK,tradingSystem.suggestOwner(ownerUserName,ownerToken,secondOwnerUserName,storeName).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.approveOwner(secondOwnerUserName,secondOwnerToken,storeName,ownerUserName).getStatusCode());

        Assertions.assertEquals(HttpStatus.OK,tradingSystem.logout(secondOwnerToken,secondOwnerUserName).getStatusCode());
        ResponseEntity<String> result = tradingSystem.placeBid(regularUserName, regularUserToken, storeName, productID, 10);
        Assertions.assertEquals(HttpStatus.OK,result.getStatusCode());
        Assertions.assertEquals("Placed bid successfully.", result.getBody());

        ResponseEntity<String> result2 = tradingSystem.getStoreBids(ownerUserName,ownerToken,storeName);

        Assertions.assertEquals("{\n  \"storeName\" : \"Store1\",\n  \"bids\" : [\n{\n  \"userName\" : \"rregularUser\",\n  \"productID\" : 111,\n  \"price\" : 10.0,\n  \"allOwnersApproved\" : false,\n  \"approvedBy\" : []\n}\n]\n}",result2.getBody());
        Assertions.assertEquals("{\n  \"storeName\" : \"Store1\",\n  \"bids\" : [\n{\n  \"userName\" : \"rregularUser\",\n  \"productID\" : 111,\n  \"price\" : 10.0,\n  \"allOwnersApproved\" : false,\n  \"approvedBy\" : []\n}\n]\n}",tradingSystem.getMyBids(regularUserName,regularUserToken,storeName).getBody());

        verify(mockNotificationSender).sendNotification(eq(ownerUserName), eq("{\"senderUsername\":\"rregularUser\",\"receiverUsername\":\"rowner1\",\"textContent\":\"rregularUser is placed a bid for product 111 in store Store1 with price 10.0\"}"));
        //verify(mockNotificationSender).sendNotification(eq(secondOwnerUserName), eq("{\"senderUsername\":\"rregularUser\",\"receiverUsername\":\"rowner2\",\"textContent\":\"rregularUser is placed a bid for product 111 in store Store1 with price 10.0\"}"));

        Assertions.assertEquals("[{\"senderUsername\":\"rregularUser\",\"receiverUsername\":\"rowner2\",\"textContent\":\"rregularUser is placed a bid for product 111 in store Store1 with price 10.0\"}]",tradingSystem.getPendingUserNotifications(ownerUserName,ownerToken, "rowner2").getBody());

    }

    @Test
    void placeTwoBids_Sucess(){
        ResponseEntity<String> result = tradingSystem.placeBid(regularUserName, regularUserToken, storeName, productID, 10);
        Assertions.assertEquals(HttpStatus.OK,result.getStatusCode());
        Assertions.assertEquals("Placed bid successfully.", result.getBody());

        Assertions.assertEquals(HttpStatus.OK,        tradingSystem.addProduct(ownerUserName,ownerToken,222,storeName,"product2","product2description", 20, 5,6,4,"[\"CarPlay\", \"iPhone\"]").getStatusCode());
        ResponseEntity<String> result2 = tradingSystem.placeBid(regularUserName, regularUserToken, storeName, 222, 10);
        Assertions.assertEquals(HttpStatus.OK,result2.getStatusCode());
        Assertions.assertEquals("Placed bid successfully.", result2.getBody());

        Assertions.assertEquals("{\n  \"storeName\" : \"Store1\",\n  \"bids\" : [\n{\n  \"userName\" : \"rregularUser\",\n  \"productID\" : 111,\n  \"price\" : 10.0,\n  \"allOwnersApproved\" : false,\n  \"approvedBy\" : []\n},\n{\n  \"userName\" : \"rregularUser\",\n  \"productID\" : 222,\n  \"price\" : 10.0,\n  \"allOwnersApproved\" : false,\n  \"approvedBy\" : []\n}\n]\n}", tradingSystem.getStoreBids(ownerUserName,ownerToken,storeName).getBody());
        Assertions.assertEquals("{\n  \"storeName\" : \"Store1\",\n  \"bids\" : [\n{\n  \"userName\" : \"rregularUser\",\n  \"productID\" : 111,\n  \"price\" : 10.0,\n  \"allOwnersApproved\" : false,\n  \"approvedBy\" : []\n},\n{\n  \"userName\" : \"rregularUser\",\n  \"productID\" : 222,\n  \"price\" : 10.0,\n  \"allOwnersApproved\" : false,\n  \"approvedBy\" : []\n}\n]\n}",tradingSystem.getMyBids(regularUserName,regularUserToken,storeName).getBody());

        verify(mockNotificationSender).sendNotification(eq(ownerUserName), eq("{\"senderUsername\":\"rregularUser\",\"receiverUsername\":\"rowner1\",\"textContent\":\"rregularUser is placed a bid for product 111 in store Store1 with price 10.0\"}"));
        verify(mockNotificationSender).sendNotification(eq(ownerUserName), eq("{\"senderUsername\":\"rregularUser\",\"receiverUsername\":\"rowner1\",\"textContent\":\"rregularUser is placed a bid for product 222 in store Store1 with price 10.0\"}"));
    }

    @Test
    void placeBid_whenStoreNotExist_ThenThrowException(){
        ResponseEntity<String> res = tradingSystem.placeBid(regularUserName, regularUserToken, "badStoreName", productID, 10);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST,res.getStatusCode());
        Assertions.assertEquals("Store must exist", res.getBody());
    }

    @Test
    void placeBid_whenProductNotExist_ThenThrowException(){
        ResponseEntity<String> res = tradingSystem.placeBid(regularUserName, regularUserToken, storeName, 555, 10);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST,res.getStatusCode());
        Assertions.assertEquals("Product must exist", res.getBody());
    }

    @Test
    void approveBidOneOwner_Success(){

        // place bid, approve bid, verify notication, verify getBids, verify getMyBids
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.placeBid(regularUserName, regularUserToken, storeName, productID, 10).getStatusCode());

        ResponseEntity<String> res = tradingSystem.approveBid(ownerUserName, ownerToken, storeName, productID, regularUserName);
        Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
        Assertions.assertEquals("Approved bid successfully.", res.getBody());

        verify(mockNotificationSender).sendNotification(eq(ownerUserName), eq("{\"senderUsername\":\"rregularUser\",\"receiverUsername\":\"rowner1\",\"textContent\":\"rregularUser is placed a bid for product 111 in store Store1 with price 10.0\"}"));
        verify(mockNotificationSender).sendNotification(eq(regularUserName), eq("{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rregularUser\",\"textContent\":\"Your bid on product Product1 in store Store1 is approved\"}"));

        Assertions.assertEquals("{\n  \"storeName\" : \"Store1\",\n  \"bids\" : [\n{\n  \"userName\" : \"rregularUser\",\n  \"productID\" : 111,\n  \"price\" : 10.0,\n  \"allOwnersApproved\" : true,\n  \"approvedBy\" : [\"rowner1\"]\n}\n]\n}",tradingSystem.getMyBids(regularUserName,regularUserToken,storeName).getBody());
        Assertions.assertEquals("{\n  \"storeName\" : \"Store1\",\n  \"bids\" : [\n{\n  \"userName\" : \"rregularUser\",\n  \"productID\" : 111,\n  \"price\" : 10.0,\n  \"allOwnersApproved\" : true,\n  \"approvedBy\" : [\"rowner1\"]\n}\n]\n}",tradingSystem.getStoreBids(ownerUserName,ownerToken,storeName).getBody());



    }

    @Test
    void approveBidOneOwnerButHaveTwoOwners_Success(){
        // place bid, approve bid, verify notication, verify getBids, verify getMyBids
        // place bid, approve bid, verify notication, verify getBids, verify getMyBids

        Assertions.assertEquals(HttpStatus.OK,tradingSystem.suggestOwner(ownerUserName,ownerToken,secondOwnerUserName,storeName).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.approveOwner(secondOwnerUserName,secondOwnerToken,storeName,ownerUserName).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.placeBid(regularUserName, regularUserToken, storeName, productID, 10).getStatusCode());

        ResponseEntity<String> res = tradingSystem.approveBid(ownerUserName, ownerToken, storeName, productID, regularUserName);
        Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
        Assertions.assertEquals("Approved bid successfully.", res.getBody());

        verify(mockNotificationSender).sendNotification(eq(ownerUserName), eq("{\"senderUsername\":\"rregularUser\",\"receiverUsername\":\"rowner1\",\"textContent\":\"rregularUser is placed a bid for product 111 in store Store1 with price 10.0\"}"));
        verify(mockNotificationSender).sendNotification(eq(secondOwnerUserName), eq("{\"senderUsername\":\"rregularUser\",\"receiverUsername\":\"rowner2\",\"textContent\":\"rregularUser is placed a bid for product 111 in store Store1 with price 10.0\"}"));


        Assertions.assertEquals("{\n  \"storeName\" : \"Store1\",\n  \"bids\" : [\n{\n  \"userName\" : \"rregularUser\",\n  \"productID\" : 111,\n  \"price\" : 10.0,\n  \"allOwnersApproved\" : false,\n  \"approvedBy\" : [\"rowner1\"]\n}\n]\n}",tradingSystem.getMyBids(regularUserName,regularUserToken,storeName).getBody());
        Assertions.assertEquals("{\n  \"storeName\" : \"Store1\",\n  \"bids\" : [\n{\n  \"userName\" : \"rregularUser\",\n  \"productID\" : 111,\n  \"price\" : 10.0,\n  \"allOwnersApproved\" : false,\n  \"approvedBy\" : [\"rowner1\"]\n}\n]\n}",tradingSystem.getStoreBids(ownerUserName,ownerToken,storeName).getBody());

    }

    @Test
    void approveTwoOwners(){


        Assertions.assertEquals(HttpStatus.OK,tradingSystem.suggestOwner(ownerUserName,ownerToken,secondOwnerUserName,storeName).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.approveOwner(secondOwnerUserName,secondOwnerToken,storeName,ownerUserName).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.placeBid(regularUserName, regularUserToken, storeName, productID, 10).getStatusCode());

        ResponseEntity<String> res = tradingSystem.approveBid(ownerUserName, ownerToken, storeName, productID, regularUserName);
        Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
        Assertions.assertEquals("Approved bid successfully.", res.getBody());
        verify(mockNotificationSender,never()).sendNotification(eq(regularUserName),anyString());
        verify(mockNotificationSender).sendNotification(eq(ownerUserName), eq("{\"senderUsername\":\"rregularUser\",\"receiverUsername\":\"rowner1\",\"textContent\":\"rregularUser is placed a bid for product 111 in store Store1 with price 10.0\"}"));
        verify(mockNotificationSender).sendNotification(eq(secondOwnerUserName), eq("{\"senderUsername\":\"rregularUser\",\"receiverUsername\":\"rowner2\",\"textContent\":\"rregularUser is placed a bid for product 111 in store Store1 with price 10.0\"}"));
//
//
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.approveBid(secondOwnerUserName,secondOwnerToken,storeName,productID,regularUserName).getStatusCode());
        verify(mockNotificationSender).sendNotification(eq(regularUserName), eq("{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rregularUser\",\"textContent\":\"Your bid on product Product1 in store Store1 is approved\"}"));


        Assertions.assertEquals("{\n  \"storeName\" : \"Store1\",\n  \"bids\" : [\n{\n  \"userName\" : \"rregularUser\",\n  \"productID\" : 111,\n  \"price\" : 10.0,\n  \"allOwnersApproved\" : true,\n  \"approvedBy\" : [\"rowner1\", \"rowner2\"]\n}\n]\n}",tradingSystem.getMyBids(regularUserName,regularUserToken,storeName).getBody());
        Assertions.assertEquals("{\n  \"storeName\" : \"Store1\",\n  \"bids\" : [\n{\n  \"userName\" : \"rregularUser\",\n  \"productID\" : 111,\n  \"price\" : 10.0,\n  \"allOwnersApproved\" : true,\n  \"approvedBy\" : [\"rowner1\", \"rowner2\"]\n}\n]\n}",tradingSystem.getStoreBids(ownerUserName,ownerToken,storeName).getBody());

    }

    @Test
    void managerWithPermissionApproved() {
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.suggestManage(ownerUserName,ownerToken,secondOwnerUserName,storeName, true, true, true, true, true, true).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.approveManage(secondOwnerUserName,secondOwnerToken,storeName,ownerUserName, true, true,true,true,true, true).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.placeBid(regularUserName, regularUserToken, storeName, productID, 10).getStatusCode());

        ResponseEntity<String> res = tradingSystem.approveBid(secondOwnerUserName, secondOwnerToken, storeName, productID, regularUserName);
        Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
        Assertions.assertEquals("Approved bid successfully.", res.getBody());
    }

    @Test
    void managerWithoutPermissionsApproved(){

        Assertions.assertEquals(HttpStatus.OK,tradingSystem.suggestManage(ownerUserName,ownerToken,secondOwnerUserName,storeName, true, true, true, true, false, false).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.approveManage(secondOwnerUserName,secondOwnerToken,storeName,ownerUserName, true, true,true,true,false, false).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.placeBid(regularUserName, regularUserToken, storeName, productID, 10).getStatusCode());

        ResponseEntity<String> res = tradingSystem.approveBid(secondOwnerUserName, secondOwnerToken, storeName, productID, regularUserName);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        Assertions.assertEquals("Manager cannot approve bid", res.getBody());
    }


    @Test
    void testRejectBid_Success(){

        Assertions.assertEquals(HttpStatus.OK,tradingSystem.placeBid(regularUserName, regularUserToken, storeName, productID, 10).getStatusCode());

        ResponseEntity<String> res = tradingSystem.rejectBid(ownerUserName, ownerToken, storeName, productID, regularUserName);
        Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
        Assertions.assertEquals("Reject bid successfully.", res.getBody());

        verify(mockNotificationSender).sendNotification(eq(regularUserName),eq("{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rregularUser\",\"textContent\":\"Your bid on product Product1 in store Store1 is rejected\"}"));
        verify(mockNotificationSender).sendNotification(eq(ownerUserName), eq("{\"senderUsername\":\"rregularUser\",\"receiverUsername\":\"rowner1\",\"textContent\":\"rregularUser is placed a bid for product 111 in store Store1 with price 10.0\"}"));


        Assertions.assertEquals("{}",tradingSystem.getMyBids(regularUserName,regularUserToken,storeName).getBody());
        Assertions.assertEquals("{}",tradingSystem.getStoreBids(ownerUserName,ownerToken,storeName).getBody());

    }

    @Test
    void testManagerWithoutPermissionReject_ThenThrowException(){

        Assertions.assertEquals(HttpStatus.OK,tradingSystem.suggestManage(ownerUserName,ownerToken,secondOwnerUserName,storeName, true, true, true, true, false, false).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.approveManage(secondOwnerUserName,secondOwnerToken,storeName,ownerUserName, true, true,true,true,false, false).getStatusCode());

        Assertions.assertEquals(HttpStatus.OK,tradingSystem.placeBid(regularUserName, regularUserToken, storeName, productID, 10).getStatusCode());

        ResponseEntity<String> result = tradingSystem.rejectBid(secondOwnerUserName, secondOwnerToken, storeName, productID, regularUserName);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST,result.getStatusCode());
        Assertions.assertEquals("Manager cannot reject bid", result.getBody());

    }

    @Test
    void testManagerWithPermissionReject_ThenSuccess(){
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.suggestManage(ownerUserName,ownerToken,secondOwnerUserName,storeName, true, true, true, true, true, true).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.approveManage(secondOwnerUserName,secondOwnerToken,storeName,ownerUserName, true, true,true,true,true, true).getStatusCode());

        Assertions.assertEquals(HttpStatus.OK,tradingSystem.placeBid(regularUserName, regularUserToken, storeName, productID, 10).getStatusCode());

        ResponseEntity<String> result = tradingSystem.rejectBid(secondOwnerUserName, secondOwnerToken, storeName, productID, regularUserName);
        Assertions.assertEquals(HttpStatus.OK,result.getStatusCode());
        Assertions.assertEquals("Reject bid successfully.", result.getBody());
    }

    @Test
    void testRejectBid_WhenStoreNotExist(){
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.placeBid(regularUserName, regularUserToken, storeName, productID, 10).getStatusCode());
        ResponseEntity<String> result = tradingSystem.rejectBid(ownerUserName, ownerToken, "badStoreName", productID, regularUserName);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST,result.getStatusCode());
        Assertions.assertEquals("Store must exist", result.getBody());

    }

    @Test
    void testRejectBid_WhenProductNotExist(){
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.placeBid(regularUserName, regularUserToken, storeName, productID, 10).getStatusCode());
        ResponseEntity<String> result = tradingSystem.rejectBid(ownerUserName, ownerToken, storeName, 555, regularUserName);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST,result.getStatusCode());
        Assertions.assertEquals("Product must exist", result.getBody());
    }

    @Test
    void testRejectBid_WhenBidNotExist(){
        ResponseEntity<String> result = tradingSystem.rejectBid(ownerUserName, ownerToken, storeName, 111, regularUserName);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST,result.getStatusCode());
        Assertions.assertEquals("Bid must exist", result.getBody());
    }

    @Test
    void testApproveBid_WhenBidNotExist()
    {
        ResponseEntity<String> result = tradingSystem.approveBid(ownerUserName, ownerToken, storeName, 111, regularUserName);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST,result.getStatusCode());
        Assertions.assertEquals("Bid must exist", result.getBody());
    }

//    validateUserAndStore(userName, storeName);
//    Store store = storeRepository.getStore(storeName);
//        if(!store.getProducts().containsKey(productID))
//            throw new IllegalArgumentException("Product must exist");
//        if(!store.isBidExist(productID, bidUserName))
//            throw new IllegalArgumentException("Bid must exist");
//    User user = userFacade.getUser(userName);
//        user.getRoleByStoreId(storeName).placeCounterOffer();
//
//        store.counterOffer(userName,productID,bidUserName,newPrice);
//
//        userFacade.sendNotification(userName, bidUserName, "Your bid on product " + store.getProducts().get(productID).getProduct_name() + " in store " + storeName + " is got counter offer by storeOwner of " + newPrice);
//}

    @Test
    void placeCounterOffer_whenStoreNotExist_ThenThrowException(){
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.placeBid(regularUserName, regularUserToken, storeName, productID, 10).getStatusCode());
        ResponseEntity<String> response = tradingSystem.placeCounterOffer(ownerUserName, ownerToken, "badStoreName", productID, regularUserName, 11);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Store must exist", response.getBody());
    }

    @Test
    void placeCounterOffer_whenProductNotExist_ThenThrowException(){
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.placeBid(regularUserName, regularUserToken, storeName, productID, 10).getStatusCode());
        ResponseEntity<String> response = tradingSystem.placeCounterOffer(ownerUserName, ownerToken, storeName, 555, regularUserName, 11);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Product must exist", response.getBody());
    }

    @Test
    void placeCounterOffer_whenBidNotExist_ThenThrowException(){
        ResponseEntity<String> response = tradingSystem.placeCounterOffer(ownerUserName, ownerToken, storeName, productID, regularUserName, 11);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Bid must exist", response.getBody());
    }

    @Test
    void placeCounterOffer_managerWithPermissions_Success(){
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.suggestManage(ownerUserName,ownerToken,secondOwnerUserName,storeName, true, true, true, true, true, true).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.approveManage(secondOwnerUserName,secondOwnerToken,storeName,ownerUserName, true, true,true,true,true, true).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.placeBid(regularUserName, regularUserToken, storeName, productID, 10).getStatusCode());
        ResponseEntity<String> response = tradingSystem.placeCounterOffer(secondOwnerUserName, secondOwnerToken, storeName, productID, regularUserName, 11);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Placed counter offer successfully.", response.getBody());
    }

    @Test
    void placeCounterOffer_managerWithoutPermission_ThenThrowException(){
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.suggestManage(ownerUserName,ownerToken,secondOwnerUserName,storeName, true, true, true, true, false, false).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.approveManage(secondOwnerUserName,secondOwnerToken,storeName,ownerUserName, true, true,true,true,false, false).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.placeBid(regularUserName, regularUserToken, storeName, productID, 10).getStatusCode());
        ResponseEntity<String> response = tradingSystem.placeCounterOffer(secondOwnerUserName, secondOwnerToken, storeName, productID, regularUserName, 11);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Manager cannot place counter offer", response.getBody());
    }

    @Test
    void placeCounterOffer_success(){
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.placeBid(regularUserName, regularUserToken, storeName, productID, 10).getStatusCode());
        Assertions.assertEquals("{\n  \"storeName\" : \"Store1\",\n  \"bids\" : [\n{\n  \"userName\" : \"rregularUser\",\n  \"productID\" : 111,\n  \"price\" : 10.0,\n  \"allOwnersApproved\" : false,\n  \"approvedBy\" : []\n}\n]\n}",tradingSystem.getStoreBids(ownerUserName,ownerToken,storeName).getBody());
        Assertions.assertEquals("{\n  \"storeName\" : \"Store1\",\n  \"bids\" : [\n{\n  \"userName\" : \"rregularUser\",\n  \"productID\" : 111,\n  \"price\" : 10.0,\n  \"allOwnersApproved\" : false,\n  \"approvedBy\" : []\n}\n]\n}",tradingSystem.getMyBids(regularUserName,regularUserToken,storeName).getBody());


        ResponseEntity<String> response = tradingSystem.placeCounterOffer(ownerUserName, ownerToken, storeName, productID, regularUserName, 11);
        Assertions.assertEquals("{\n  \"storeName\" : \"Store1\",\n  \"bids\" : [\n{\n  \"userName\" : \"rregularUser\",\n  \"productID\" : 111,\n  \"price\" : 11.0,\n  \"allOwnersApproved\" : false,\n  \"approvedBy\" : []\n}\n]\n}",tradingSystem.getMyBids(regularUserName,regularUserToken,storeName).getBody());
        Assertions.assertEquals("{\n  \"storeName\" : \"Store1\",\n  \"bids\" : [\n{\n  \"userName\" : \"rregularUser\",\n  \"productID\" : 111,\n  \"price\" : 11.0,\n  \"allOwnersApproved\" : false,\n  \"approvedBy\" : []\n}\n]\n}",tradingSystem.getStoreBids(ownerUserName,ownerToken,storeName).getBody());

        verify(mockNotificationSender).sendNotification(eq(regularUserName), eq("{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rregularUser\",\"textContent\":\"Your bid on product Product1 in store Store1 is got counter offer by rowner1 of 11.0\"}"));
    }


    @Test
    void getStoreBids_Success(){
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.placeBid(regularUserName, regularUserToken, storeName, productID, 10).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.getStoreBids(ownerUserName, ownerToken, storeName).getStatusCode());

    }

    @Test
    void getStoreBids_managerWithPermission_ThenSuccess(){
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.suggestManage(ownerUserName,ownerToken,secondOwnerUserName,storeName, true, true, true, true, true, true).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.approveManage(secondOwnerUserName,secondOwnerToken,storeName,ownerUserName, true, true,true,true,true, true).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.placeBid(regularUserName, regularUserToken, storeName, productID, 10).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.getStoreBids(secondOwnerUserName, secondOwnerToken, storeName).getStatusCode());
    }

    @Test
    void getStoreBids_managerWithoutPermission_ThenThrowException(){
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.suggestManage(ownerUserName,ownerToken,secondOwnerUserName,storeName, true, true, true, true, false,false).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.approveManage(secondOwnerUserName,secondOwnerToken,storeName,ownerUserName, true, true,true,true,false,false).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.placeBid(regularUserName, regularUserToken, storeName, productID, 10).getStatusCode());

        ResponseEntity<String> response = tradingSystem.getStoreBids(secondOwnerUserName, secondOwnerToken, storeName);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
        Assertions.assertEquals("Manager cannot view store bids",response.getBody());
    }

    @Test
    void getStoreBids_byRegularUser_ThenThrowException(){
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.placeBid(regularUserName, regularUserToken, storeName, productID, 10).getStatusCode());
        ResponseEntity<String> response = tradingSystem.getStoreBids(regularUserName,regularUserToken, storeName);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
        Assertions.assertEquals("User doesn't have roles",response.getBody());
    }

    @Test
    void getMyBids_byUserWithBid_ThenSuccess(){
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.placeBid(regularUserName, regularUserToken, storeName, productID, 10).getStatusCode());
        ResponseEntity<String> response = tradingSystem.getMyBids(regularUserName,regularUserToken,storeName);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("{\n  \"storeName\" : \"Store1\",\n  \"bids\" : [\n{\n  \"userName\" : \"rregularUser\",\n  \"productID\" : 111,\n  \"price\" : 10.0,\n  \"allOwnersApproved\" : false,\n  \"approvedBy\" : []\n}\n]\n}", response.getBody());
    }

    @Test
    void getMyBids_byUserWithoutBids_ThenSuccess(){
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.placeBid(regularUserName, regularUserToken, storeName, productID, 10).getStatusCode());
        ResponseEntity<String> response = tradingSystem.getMyBids(ownerUserName,ownerToken,storeName);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("{}",response.getBody());
    }


    //check getMyBids is just for my bid

    @AfterEach
    public void tearDown() {
        tradingSystem.deleteInstance();
        userRepository.deleteInstance();
        storeRepository.deleteInstance();
    }


}
