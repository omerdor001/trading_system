package com.example.trading_system.AcceptanceTests.Users;

import com.example.trading_system.service.TradingSystem;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class EnterAcceptanceTests {

    private static TradingSystem tradingSystem;
    private String token;
    private String username;

    @BeforeAll
    public static void setup() {
        tradingSystem = TradingSystemImp.getInstance();
    }

    @BeforeEach
    public void openSystemAndRegisterAdmin() {
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
//TODO FIX ME
/*
    @Test
    public void testEnterSuccessfully() {
        // Enter the system
        ResponseEntity<String> enterResponse = tradingSystem.enter();
        assertEquals(HttpStatus.OK, enterResponse.getStatusCode());
        String token = enterResponse.getBody();
        assertEquals(true, token.length() > 0);
    }
*/

//    @Test
//    public void testEnterSystemClosed() {
//        // Close the system
//        tradingSystem.closeSystem();
//
//        // Attempt to enter the system
//        ResponseEntity<String> enterResponse = tradingSystem.enter();
//        assertEquals(HttpStatus.FORBIDDEN, enterResponse.getStatusCode());
//        assertEquals("", enterResponse.getBody());
//    }

//TODO FIX ME
/*    @Test
    public void testEnterWithExistingToken() {
        // Enter the system
        ResponseEntity<String> enterResponse = tradingSystem.enter();
        assertEquals(HttpStatus.OK, enterResponse.getStatusCode());
        String token = enterResponse.getBody();

        // Attempt to enter the system again with the existing token
        ResponseEntity<String> reEnterResponse = tradingSystem.enter();
        assertEquals(HttpStatus.OK, reEnterResponse.getStatusCode());
        String newToken = reEnterResponse.getBody();
        assertEquals(false, newToken.equals(token));
    }*/
}
