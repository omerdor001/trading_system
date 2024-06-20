package com.example.trading_system.AcceptanceTests.Users;

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

public class EnterAcceptanceTests {

    private static TradingSystem tradingSystem;
    private String token;
    private String username;

    @BeforeEach
    public void openSystemAndRegisterAdmin() {
        tradingSystem = TradingSystemImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class));
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
    }

    @AfterEach
    void setDown() {
        tradingSystem.deleteInstance();
    }

    @Test
    public void testEnterSuccessfully() {
        ResponseEntity<String> enterResponse = tradingSystem.enter();
        assertEquals(HttpStatus.OK, enterResponse.getStatusCode());
        String token = enterResponse.getBody();
        assertNotNull(token);
    }

    @Test
    public void testEnterSystemClosed() {
        tradingSystem.closeSystem(username, token);
        ResponseEntity<String> enterResponse = tradingSystem.enter();
        assertEquals(HttpStatus.FORBIDDEN, enterResponse.getStatusCode());
        assertEquals("", enterResponse.getBody());
    }

    @Test
    public void testEnterTwoSessions() {
        ResponseEntity<String> enterResponse = tradingSystem.enter();
        assertEquals(HttpStatus.OK, enterResponse.getStatusCode());
        String token = enterResponse.getBody();
        ResponseEntity<String> reEnterResponse = tradingSystem.enter();
        assertEquals(HttpStatus.OK, reEnterResponse.getStatusCode());
        String newToken = reEnterResponse.getBody();
        assertNotEquals(newToken, token);
    }
}
