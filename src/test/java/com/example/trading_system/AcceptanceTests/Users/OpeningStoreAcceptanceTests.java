package com.example.trading_system.AcceptanceTests.Users;

import com.example.trading_system.domain.stores.StorePolicy;
import com.example.trading_system.service.TradingSystem;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class OpeningStoreAcceptanceTests {

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
    }
    //TODO fix tests
    @Test
    public void testOpenStore() {
        String username = "user1";
        String storeName = "store1";
        String description = "A nice store";
//        StorePolicy policy = mock(StorePolicy.class);
//
//        when(userService.openStore(username, storeName, description, policy)).thenReturn(true);
//
//        boolean result = userService.openStore(username, storeName, description, policy);
//        assertTrue(result);
    }

    @Test
    public void testOpenStoreFailsDueToExistingStoreName() {
        String username = "user1";
        String storeName = "existingStore";
        String description = "A nice store";
//        StorePolicy policy = mock(StorePolicy.class);
//        when(userService.openStore(username, storeName, description, policy))
//                .thenThrow(new RuntimeException("Store with name " + storeName + " already exists"));
//        assertThrows(RuntimeException.class, () -> {
//            userService.openStore(username, storeName, description, policy);
//        });
    }
}