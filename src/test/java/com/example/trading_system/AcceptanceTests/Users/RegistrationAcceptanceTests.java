package com.example.trading_system.AcceptanceTests.Users;

import com.example.trading_system.service.TradingSystem;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationAcceptanceTests {
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
        userToken = tradingSystem.login(token, "0", "owner1", "password123").getBody();
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
    void registration_Successful() {
        int id = 1;
        String username = "TestUser";
        String password = "TestPassword";
        LocalDate birthdate = LocalDate.of(1990, 5, 15);

        ResponseEntity<String> response = tradingSystem.register(id, username, password, birthdate);

        assertEquals(200, response.getStatusCode());
        assertEquals("User registered successfully.", response.getBody());
    }

    @Test
    void registration_Failure() {
        int id = 2;
        String username = "ExistingUser";
        String password = "ExistingPassword";
        LocalDate birthdate = LocalDate.of(1995, 10, 20);

        tradingSystem.register(id, username, password, birthdate);
        ResponseEntity<String> response = tradingSystem.register(id, username, password, birthdate);

        assertEquals(400, response.getStatusCode());
        assertEquals("username already exists - ExistingUser", response.getBody());
    }
}