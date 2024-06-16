package com.example.trading_system.AcceptanceTests.Users;

import com.example.trading_system.service.TradingSystem;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class AddProductAcceptanceTests {
    private TradingSystem tradingSystem;
    private String token;
    private String username;

    @BeforeEach
    void setUp() {
        tradingSystem = TradingSystemImp.getInstance();
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
        tradingSystem.openStore(username, token, "existingStore", "General Store");
    }

    @AfterEach
    void setDown(){
        tradingSystem.deleteInstance();
    }

    @Test
    void addProduct_Success() {
        ResponseEntity<String> result = tradingSystem.addProduct(username, token, 0, "existingStore", "product1", "", 1, 5, 1, 1, new LinkedList<>());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Product was added successfully.", result.getBody());
    }


    @Test
    void addProduct_NonExistentStore() {
        ResponseEntity<String> result = tradingSystem.addProduct(username, token, 0, "non-existingStore", "product1", "", 1, 5, 1, 1, new LinkedList<>());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Store must exist", result.getBody());
    }

    @Test
    void addProduct_NegativePrice() {
        ResponseEntity<String> result = tradingSystem.addProduct(username, token, 0, "existingStore", "product1", "", -1, 5, 1, 1, new LinkedList<>());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Price can't be a negative number", result.getBody());
    }

    @Test
    void addProduct_NegativeQuantity() {
        ResponseEntity<String> result = tradingSystem.addProduct(username, token, 0, "existingStore", "product1", "", 1, -1, 1, 1, new LinkedList<>());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Quantity must be a natural number", result.getBody());
    }

    @Test
    void addProduct_NegativeRating() {
        ResponseEntity<String> result = tradingSystem.addProduct(username, token, 0, "existingStore", "product1", "", 1, 5, -1, 1, new LinkedList<>());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Rating can't be a negative number", result.getBody());
    }

    @Test
    void addProduct_NonExistentUser() {
        ResponseEntity<String> result = tradingSystem.addProduct("owner", token, 0, "existingStore", "product1", "", 1, 5, 1, 1, new LinkedList<>());
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertEquals("Invalid token was supplied", result.getBody());
    }
}