/*
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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ExitAcceptanceTests {
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

    @Test
    public void testExitSuccess() {
        ResponseEntity<String> enterResponse = tradingSystem.enter();
        String userToken = enterResponse.getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            username = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        ResponseEntity<String> exitResponse = tradingSystem.exit(token, username);
        assertEquals(HttpStatus.OK, exitResponse.getStatusCode());
        assertEquals("User exited successfully.", exitResponse.getBody());
    }

    @Test
    public void testExitSystemClosed() {
        ResponseEntity<String> enterResponse = tradingSystem.enter();
        String userToken = enterResponse.getBody();
        tradingSystem.closeSystem(username,token);
        ResponseEntity<String> exitResponse = tradingSystem.exit(userToken, "owner1");
        assertEquals(HttpStatus.FORBIDDEN, exitResponse.getStatusCode());
        assertEquals("System is not open. Only registration is allowed.", exitResponse.getBody());
    }

   @Test
    public void testExitInvalidToken() {
        ResponseEntity<String> exitResponse = tradingSystem.exit(token, "owner1");
        assertEquals(HttpStatus.UNAUTHORIZED, exitResponse.getStatusCode());
        assertEquals("Invalid token was supplied", exitResponse.getBody());
    }

    @Test
    public void testExitNonExistentUser() {
        ResponseEntity<String> exitResponse = tradingSystem.exit(token, "nonExistentUser");
        assertEquals(HttpStatus.UNAUTHORIZED, exitResponse.getStatusCode());
        assertEquals("Invalid token was supplied", exitResponse.getBody());
    }
}
*/
