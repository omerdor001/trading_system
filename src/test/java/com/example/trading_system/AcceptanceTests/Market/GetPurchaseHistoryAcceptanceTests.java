package com.example.trading_system.AcceptanceTests.Market;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.StoreMemoryRepository;
import com.example.trading_system.domain.stores.StoreRepository;
import com.example.trading_system.domain.users.UserMemoryRepository;
import com.example.trading_system.domain.users.UserRepository;
import com.example.trading_system.service.TradingSystem;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GetPurchaseHistoryAcceptanceTests {
    private TradingSystem tradingSystem;
    private String username;
    private String token;
    private final String storeName = "Store1";
    private UserRepository userRepository;
    private StoreRepository storeRepository;
    private final String amount = "100.00";
    private final String currency = "USD";
    private final String cardNumber = "4111111111111111";
    private final String month = "12";
    private final String year = "2025";
    private final String holder = "John Doe";
    private final String ccv = "123";
    private final String id = "123456789";

    @BeforeEach
    void setUp() {
        userRepository= UserMemoryRepository.getInstance();    //May be change later
        storeRepository= StoreMemoryRepository.getInstance();  //May be change later
        tradingSystem = TradingSystemImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class), mock(NotificationSender.class),userRepository,storeRepository);
        tradingSystem.register("owner1", "password123", LocalDate.now());
        tradingSystem.openSystem(storeRepository);
        String userToken = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
        userToken = tradingSystem.login(token, "v0", "owner1", "password123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            username = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        String address = "1234 Main Street, Springfield, IL, 62704-1234";
        tradingSystem.setAddress(username, token, address);
        tradingSystem.openStore(username, token, storeName, "");
        tradingSystem.addProduct(username, token, 0, storeName, "product1", "", 1, 5, 1, 1, "[]");
        tradingSystem.addToCart(username, token, 0, storeName, 1,1);
        tradingSystem.approvePurchase(username, token, address, amount, currency, cardNumber, month, year, holder, ccv, id);
    }

    @AfterEach
    void setDown(){
        tradingSystem.deleteInstance();
        userRepository.deleteInstance();
        storeRepository.deleteInstance();
    }

    @Test
    void testGetPurchaseHistory_Success() {
        ResponseEntity<String> result = tradingSystem.getPurchaseHistory(username, token, storeName);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("{\"productInSaleList\":[{\"storeId\":\"Store1\",\"id\":0,\"price\":1.0,\"quantity\":1,\"category\":1}],\"customerUsername\":\"rowner1\",\"totalPrice\":1.0,\"storeName\":\"Store1\"}", result.getBody());
    }

    @Test
    void testGetStoresPurchaseHistory_ValidInput() {
        ResponseEntity<String> result = tradingSystem.getAllHistoryPurchases(username, token, storeName);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("{\"productInSaleList\":[{\"storeId\":\"Store1\",\"id\":0,\"price\":1.0,\"quantity\":1,\"category\":1}],\"customerUsername\":\"rowner1\",\"totalPrice\":1.0,\"storeName\":\"Store1\"}", result.getBody());
    }

    @Test
    void testGetPurchaseHistory_NotAdmin() {
        String userToken = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            username = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        ResponseEntity<String> result = tradingSystem.getPurchaseHistory(username, token, storeName);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("User is not commercial manager", result.getBody());
    }

    @Test
    void testGetPurchaseHistory_UserNotLogged() {
        tradingSystem.logout(token,username);
        ResponseEntity<String> result = tradingSystem.getPurchaseHistory(username, token, storeName);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("User is not logged", result.getBody());
    }

    @Test
    void testGetPurchaseHistory_UserNotFound() {
        ResponseEntity<String> result = tradingSystem.getPurchaseHistory("owner2", token, storeName);
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertEquals("Invalid token was supplied", result.getBody());
    }

    @Test
    void testGetPurchaseHistory_InvalidStore() {
        ResponseEntity<String> result = tradingSystem.getAllHistoryPurchases(username, token, "store2");
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Store must exist", result.getBody());
    }

    @Test
    void testGetStoresPurchaseHistory_InvalidStore() {
        ResponseEntity<String> result = tradingSystem.getPurchaseHistory(username, token, "store2");
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Store does not exist", result.getBody());
    }

    @Test
    void testGetPurchaseHistory_ValidStoreButNoPermission() {
        String userToken = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            username = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        ResponseEntity<String> result = tradingSystem.getAllHistoryPurchases(username, token, storeName);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Not allowed to view store history", result.getBody());
    }
}

