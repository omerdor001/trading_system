package com.example.trading_system.AcceptanceTests.TradingSystemSetupAndEntry;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class LoginAcceptanceTests {

    private static TradingSystem tradingSystem;
    private String token;

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
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
    }

    @AfterEach
    void setDown() {
        tradingSystem.deleteInstance();
    }

    @Test
    void login_Success() {
        ResponseEntity<String> response = tradingSystem.login(token, "v0", "owner1", "password123");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody(), "Token should not be empty");
    }

    @Test
    void login_User_Logged_In() {
        tradingSystem.login(token, "v0", "owner1", "password123");
        String userToken = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        ResponseEntity<String> response = tradingSystem.login(token, "v1", "owner1", "password123");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void login_Wrong_Password() {
        ResponseEntity<String> response = tradingSystem.login(token, "v0", "owner1", "password12");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(response.getBody(), "Wrong password");
    }

    @Test
    void login_Wrong_Username() {
        ResponseEntity<String> response = tradingSystem.login(token, "v0", "owner", "password123");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
