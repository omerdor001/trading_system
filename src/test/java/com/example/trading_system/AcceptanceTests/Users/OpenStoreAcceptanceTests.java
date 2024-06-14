/*
package com.example.trading_system.AcceptanceTests.Users;


import com.example.trading_system.domain.stores.StorePolicy;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//TODO FIX ME
public class OpenStoreAcceptanceTests {
    private TradingSystemImp tradingSystem;
    private String token;
    private String username;


    @BeforeEach
    public void setUp() {
        tradingSystem = TradingSystemImp.getInstance();
        tradingSystem.register("owner1", "password123", LocalDate.now());
        tradingSystem.register("manager", "password123", LocalDate.now());
        tradingSystem.openSystem();

        String userTokenResponse = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userTokenResponse);
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }

        String loginResponse = tradingSystem.login(token, "0", "owner1", "password123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(loginResponse);
            username = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystem.enter();
    }

    @AfterEach
    public void tearDown() {
        tradingSystem.deleteInstance();
    }

    @Test
    public void testOpenStoreWithValidDetails() {
        // Register a user
        String username = "validUser";
        String password = "password";
        LocalDate birthdate = LocalDate.of(1990, 1, 1);
        ResponseEntity<String> registerResponse = tradingSystem.register( username, password, birthdate);
        assertEquals(200, registerResponse.getStatusCodeValue());

        // Generate a token for the user
        ResponseEntity<String> enterResponse = tradingSystem.enter();
        assertEquals(403, enterResponse.getStatusCodeValue());

        // Open a store with valid details
        String storeName = "Valid Store";
        String description = "This is a valid store description.";
        StorePolicy policy = new StorePolicy();
        ResponseEntity<String> openStoreResponse = tradingSystem.openStore(username, token, storeName, description, policy);
        assertEquals(200, openStoreResponse.getStatusCodeValue());
        assertTrue(openStoreResponse.getBody().contains("Finished opening store with name:"));
    }

    @Test
    public void testOpenStoreWithExistingStoreName() {
        // Register a user
        String username = "validUser";
        String password = "password";
        LocalDate birthdate = LocalDate.of(1990, 1, 1);
        ResponseEntity<String> registerResponse = tradingSystem.register( username, password, birthdate);
        assertEquals(200, registerResponse.getStatusCodeValue());

        // Generate a token for the user
        ResponseEntity<String> enterResponse = tradingSystem.enter();
        assertEquals(403, enterResponse.getStatusCodeValue());

        // Open a store with valid details
        String storeName = "Valid Store";
        String description = "This is a valid store description.";
        StorePolicy policy = new StorePolicy();
        ResponseEntity<String> openStoreResponse = tradingSystem.openStore(username, token, storeName, description, policy);
        assertEquals(200, openStoreResponse.getStatusCodeValue());
        assertTrue(openStoreResponse.getBody().contains("Finished opening store with name:"));

        // Attempt to open another store with the same name
        ResponseEntity<String> duplicateStoreResponse = tradingSystem.openStore(username, token, storeName, description, policy);
        assertEquals(400, duplicateStoreResponse.getStatusCodeValue());
        assertTrue(duplicateStoreResponse.getBody().contains("Error occurred in opening store"));
    }

    @Test
    public void testOpenStoreWithInvalidToken() {
        // Register a user
        String username = "validUser";
        String password = "password";
        LocalDate birthdate = LocalDate.of(1990, 1, 1);
        ResponseEntity<String> registerResponse = tradingSystem.register( username, password, birthdate);
        assertEquals(200, registerResponse.getStatusCodeValue());

        // Attempt to open a store with an invalid token
        String invalidToken = "invalidToken";
        String storeName = "Invalid Store";
        String description = "This store should not open.";
        StorePolicy policy = new StorePolicy();
        ResponseEntity<String> openStoreResponse = tradingSystem.openStore(username, invalidToken, storeName, description, policy);
        assertEquals(403, openStoreResponse.getStatusCodeValue());
        assertTrue(openStoreResponse.getBody().contains("Invalid token was supplied"));
    }

    @Test
    public void testOpenStoreWithSystemClosed() {
        // Close the system
        tradingSystem.closeSystem("admin", "adminToken");

        // Attempt to open a store
        String username = "validUser";
        String token = "validToken";
        String storeName = "StoreWhileClosed";
        String description = "This store should not open.";
        StorePolicy policy = new StorePolicy();
        ResponseEntity<String> openStoreResponse = tradingSystem.openStore(username, token, storeName, description, policy);
        assertEquals(403, openStoreResponse.getStatusCodeValue());
        assertTrue(openStoreResponse.getBody().contains("System is not open. Only registration is allowed."));
    }

}
*/
