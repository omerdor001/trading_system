/*
package com.example.trading_system.AcceptanceTests.Users;


import com.example.trading_system.domain.stores.StorePolicy;
import com.example.trading_system.service.TradingSystemImp;
import org.junit.jupiter.api.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//TODO FIX ME
public class OpenStoreAcceptanceTests {
    private TradingSystemImp tradingSystem;

    @BeforeEach
    public void setUp() {
        tradingSystem = TradingSystemImp.getInstance();
        tradingSystem.openSystem();
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
        String token = extractToken(enterResponse.getBody());

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
        String token = extractToken(enterResponse.getBody());

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

    private String extractToken(String responseBody) {
        // Assuming the response body contains a JSON with "token"
        int startIndex = responseBody.indexOf("token") + 8;
        int endIndex = responseBody.indexOf("\"", startIndex);
        return responseBody.substring(startIndex, endIndex);
    }
}
*/
