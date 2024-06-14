package com.example.trading_system.AcceptanceTests.Market;

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

public class DiscountPolicyAcceptanceTests {
    private TradingSystem tradingSystem;
    private String username;
    private String token;

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
        userToken = tradingSystem.login(token, "0", "owner1", "password123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            username = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystem.openStore(username, token, "store1", "");
        tradingSystem.addProduct(username, token, 0, "store1", "product1", "", 1, 5, 1, 1, new LinkedList<>());
        tradingSystem.enter();
    }

    @AfterEach
    void setDown() {
        tradingSystem.deleteInstance();
    }

    @Test
    public void testEnterSystemClosed() {
    }
}
