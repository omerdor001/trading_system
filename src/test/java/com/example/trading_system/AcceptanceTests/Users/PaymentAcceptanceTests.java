package com.example.trading_system.AcceptanceTests.Users;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.service.TradingSystem;
import com.example.trading_system.service.TradingSystemImp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class PaymentAcceptanceTests {
    private TradingSystem tradingSystem;
    private String username;
    private String token;
    private final String storeName = "Store1";
    private final String address = "1234 Main Street, Springfield, IL, 62704-1234";

    @BeforeEach
    void setUp() {
        tradingSystem = TradingSystemImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class), mock(NotificationSender.class));
        tradingSystem.register("owner1", "password123", LocalDate.now());
        tradingSystem.openSystem();
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
        tradingSystem.setAddress(username, token, address);
        tradingSystem.openStore(username, token, storeName, "");
        tradingSystem.addProduct(username, token, 0, storeName, "product1", "", 1, 5, 1, 1, new LinkedList<>());
    }

    @AfterEach
    void setDown() {
        tradingSystem.deleteInstance();
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
        tradingSystem.addToCart(username, token, 0, storeName, 2);
        tradingSystem.setProductQuantity(username, token, storeName, 0, 1);
        ResponseEntity<String> result = tradingSystem.approvePurchase(username, token);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Product quantity is too low", result.getBody());
    }
}
