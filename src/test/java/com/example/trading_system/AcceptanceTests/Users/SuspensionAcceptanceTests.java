package com.example.trading_system.AcceptanceTests.Users;

import com.example.trading_system.domain.stores.StorePolicy;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

class SuspensionAcceptanceTests {
    private TradingSystemImp tradingSystem;
    String token1;
    String username;

    @BeforeEach
    public void setUp() {
        tradingSystem = TradingSystemImp.getInstance();
        tradingSystem.register(0,"owner1", "password123", LocalDate.now());
        tradingSystem.openSystem();
        //Enters
        String userToken = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            token1 = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
        userToken = tradingSystem.login(token1, "0", "owner1", "password123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            username = rootNode.get("username").asText();
            token1 = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystem.openStore(username, token1, "store1", "", mock(StorePolicy.class));
        tradingSystem.enter();
    }

    @AfterEach
    public void tearDown() {
        tradingSystem.deleteInstance();
    }

    @Test
    void suspendUser() {
    }

    @Test
    void endSuspendUser() {
    }

    @Test
    void checkForEndingSuspension() {
    }

    @Test
    void watchSuspensions() {
    }
}