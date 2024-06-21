package com.example.trading_system.AcceptanceTests.Users.PurchaseTests;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.StoreMemoryRepository;
import com.example.trading_system.domain.stores.StoreRepository;
import com.example.trading_system.domain.users.UserMemoryRepository;
import com.example.trading_system.domain.users.UserRepository;
import com.example.trading_system.service.TradingSystem;
import com.example.trading_system.service.TradingSystemImp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class PaymentAcceptanceTests {
    private final String storeName = "Store1";
    private final String address = "1234 Main Street, Springfield, IL, 62704-1234";
    private TradingSystem tradingSystem;
    private String username;
    private String token;
    private UserRepository userRepository;
    private StoreRepository storeRepository;
    private static final Logger logger = LoggerFactory.getLogger(PaymentAcceptanceTests.class);
    @BeforeEach
    void setUp() {
        userRepository = UserMemoryRepository.getInstance();    //May be change later
        storeRepository = StoreMemoryRepository.getInstance();  //May be change later
        tradingSystem = TradingSystemImp.getInstance(mock(PaymentService.class), mock(DeliveryService.class), mock(NotificationSender.class), userRepository, storeRepository);
        tradingSystem.register("owner1", "password123", LocalDate.now());
        tradingSystem.openSystem(storeRepository);
        String userToken = tradingSystem.enter().getBody();
        try {
            if(userToken == null)
                userToken = tradingSystem.enter().getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response, " + e.getMessage() + " userToken: " + userToken);
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
        tradingSystem.setAddress(username, token, address);
        tradingSystem.openStore(username, token, storeName, "");
        tradingSystem.addProduct(username, token, 0, storeName, "product1", "", 1, 5, 1, 1, new LinkedList<>());
    }

    @AfterEach
    void setDown() {
        tradingSystem.deleteInstance();
        userRepository.deleteInstance();
        storeRepository.deleteInstance();
    }

    @Test
    void testRegistered_Success() {
        tradingSystem.addToCart(username, token, 0, storeName, 1);
        ResponseEntity<String> result = tradingSystem.approvePurchase(username, token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Purchase approved", result.getBody());
    }

    @Test
    void testVisitor_Success() {
        String userToken = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            username = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystem.setAddress(username, token, address);
        tradingSystem.addToCart(username, token, 0, storeName, 1);
        ResponseEntity<String> result = tradingSystem.approvePurchase(username, token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Purchase approved", result.getBody());
    }

    @Test
    void testVisitor_ProductNotAvailable() {
        String visitorToken = "";
        String visitorUsername = "";
        String userToken = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            visitorUsername = rootNode.get("username").asText();
            visitorToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystem.addToCart(visitorUsername, visitorToken, 0, storeName, 2);
        tradingSystem.setProductQuantity(username, token, storeName, 0, 1);
        ResponseEntity<String> result = tradingSystem.approvePurchase(visitorUsername, visitorToken);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Product quantity is too low", result.getBody());
    }

    @Test
    void testRegistered_ProductNotAvailable() {
        setDown();
        setUp();
        tradingSystem.addToCart(username, token, 0, storeName, 2);
        tradingSystem.setProductQuantity(username, token, storeName, 0, 1);
        ResponseEntity<String> result = tradingSystem.approvePurchase(username, token);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Product quantity is too low", result.getBody());
    }
}
