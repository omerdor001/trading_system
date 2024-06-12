package com.example.trading_system.AcceptanceTests.Market;

import com.example.trading_system.domain.stores.StorePolicy;
import com.example.trading_system.service.TradingSystem;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class GetProductsInfo {
    private static TradingSystem tradingSystem;
    private static String token;
    private static String username;
    @BeforeAll
    void setupOnce() {
        tradingSystem = TradingSystemImp.getInstance();
        tradingSystem.register(0, "owner1", "password123", LocalDate.now());
        tradingSystem.register(1, "manager", "password123", LocalDate.now());
        tradingSystem.openSystem();

        String userTokenResponse = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userTokenResponse);
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            Assertions.fail("Setup failed: Unable to extract token from JSON response");
        }

        String loginResponse = tradingSystem.login(token, "0", "owner1", "password123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(loginResponse);
            username = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            Assertions.fail("Setup failed: Unable to extract username and token from JSON response");
        }
    }
    @Test
    public void testAddStoreWithInvalidToken() {
        // Attempt to add a store with an invalid token
        ResponseEntity<String> response = tradingSystem.openStore(username, "invalidToken","existingStore", "General Store", new StorePolicy());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error occurred in opening store", response.getBody());
    }

    @Test
    public void testGetAllStoresSuccessfully() {
        tradingSystem.openStore(username,token,"existingStore", "General Store", new StorePolicy());
        tradingSystem.openStore(username,token,"existingStore2", "General Store", new StorePolicy());
        ResponseEntity<String> response = tradingSystem.getAllStores();
        assertNotNull(response.getBody());
    }
    @Test
    public void testGetStoreProductsSuccessfully() {
        tradingSystem.openStore(username,token,"store1", "General Store", new StorePolicy());
        tradingSystem.addProduct(username,token,1, "store1", "product1", "desc1", 10.0, 100, 4,0,new ArrayList<>());
        tradingSystem.addProduct(username,token,2, "store1", "product1", "desc1", 10.0, 100, 4,0,new ArrayList<>());
        ResponseEntity<String> response = tradingSystem.getStoreProducts("store1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

/*    @Test //TODO FIX ME
    public void testGetProductInfoSuccessfully() {
        tradingSystem.openStore(username,token,"store1", "General Store", new StorePolicy());
        tradingSystem.addProduct(username,token,1, "store1", "product1", "desc1", 10.0, 100, 4,0,new ArrayList<>());
        ResponseEntity<String> response = tradingSystem.getProductInfo("store1", 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }*/

    @Test
    public void testGetStoreProductsNonExistentStore() {
        ResponseEntity<String> response = tradingSystem.getStoreProducts("nonExistentStore");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Can't find store with name nonExistentStore", response.getBody());
    }

    @Test
    public void testGetProductInfoNonExistentProduct() {
        // Add store
        tradingSystem.openStore(username,token,"store1", "General Store", new StorePolicy());
        ResponseEntity<String> response = tradingSystem.getProductInfo("store1", 999);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Can't find product with id 999", response.getBody());
    }
}