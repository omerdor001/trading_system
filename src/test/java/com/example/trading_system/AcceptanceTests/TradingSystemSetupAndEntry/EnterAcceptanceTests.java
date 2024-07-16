package com.example.trading_system.AcceptanceTests.TradingSystemSetupAndEntry;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.StoreMemoryRepository;
import com.example.trading_system.domain.stores.StoreRepository;
import com.example.trading_system.domain.users.UserMemoryRepository;
import com.example.trading_system.domain.users.UserRepository;
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
    private UserRepository userRepository;
    private StoreRepository storeRepository;

    @BeforeEach
    public void openSystemAndRegisterAdmin() {
        userRepository= UserMemoryRepository.getInstance();    //May be change later
        storeRepository= StoreMemoryRepository.getInstance();  //May be change later
        tradingSystem = TradingSystemImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class), mock(NotificationSender.class),userRepository,storeRepository);
        tradingSystem.register("owner1", "password123", LocalDate.now());
        tradingSystem.openSystem(storeRepository);
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
        userRepository.deleteInstance();
        storeRepository.deleteInstance();
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
        assertEquals("System is not open, entry forbidden", enterResponse.getBody());
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
