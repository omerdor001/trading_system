package com.example.trading_system.AcceptanceTests.Users;

import com.example.trading_system.domain.stores.*;

import com.example.trading_system.service.TradingSystem;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AddProductAcceptanceTests {
    private TradingSystem tradingSystem;
    private String token;
    private String username;

    @BeforeEach
    void setUp() {
        tradingSystem = TradingSystemImp.getInstance();
        tradingSystem.register(0, "owner1", "password123", LocalDate.now());
        tradingSystem.openSystem();
        String userToken = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
        userToken = tradingSystem.login(token, 0, "owner1", "password123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            username = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystem.openStore(username,token,"existingStore", "General Store", new StorePolicy());
    }
    //TODO fix tests
/*
    @Test
    void addProduct_Success() throws IllegalAccessException {
        assertNotNull(stores.get("existingStore"), "Store should exist in the map before calling addProduct");
        assertTrue(storeManagementFacade.addProduct("validUser", 101, "existingStore", "New Product", "Description",
                29.99, 10, 4.5, 1, Arrays.asList("electronics", "gadget")));
        verify(role).addProduct(eq("validUser"), eq(101), eq("existingStore"), eq("New Product"), eq("Description"),
                eq(29.99), eq(10), eq(4.5), eq(1), anyList());
    }
*/
//
//    @Test
//    void addProduct_NonExistentStore() {
//        assertThrows(IllegalArgumentException.class, () ->
//                storeManagementFacade.addProduct("validUser", 102, "nonExistentStore", "Product", "Description", 19.99, 5, 3.0, 1, Arrays.asList("toy")));
//    }
//
//    @Test
//    void addProduct_NegativePrice() {
//        assertThrows(IllegalArgumentException.class, () ->
//                storeManagementFacade.addProduct("validUser", 103, "existingStore", "Product", "Description", -1, 5, 3.0, 1, Arrays.asList("toy")));
//    }
//
//    @Test
//    void addProduct_ZeroQuantity() {
//        assertThrows(IllegalArgumentException.class, () ->
//                storeManagementFacade.addProduct("validUser", 104, "existingStore", "Product", "Description", 20, 0, 3.0, 1, Arrays.asList("toy")));
//    }
//
//    @Test
//    void addProduct_NegativeRating() {
//        assertThrows(IllegalArgumentException.class, () ->
//                storeManagementFacade.addProduct("validUser", 105, "existingStore", "Product", "Description", 20, 1, -1, 1, Arrays.asList("toy")));
//    }
//
//    @Test
//    void addProduct_NonExistentUser() {
//        assertThrows(IllegalArgumentException.class, () ->
//                storeManagementFacade.addProduct("nonExistentUser", 106, "existingStore", "Product", "Description", 20, 1, 3.0, 1, Arrays.asList("toy")));
//    }
}