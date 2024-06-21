package com.example.trading_system.AcceptanceTests.Users;

import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.StoreMemoryRepository;
import com.example.trading_system.domain.stores.StoreRepository;
import com.example.trading_system.domain.users.UserMemoryRepository;
import com.example.trading_system.domain.users.UserRepository;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class OpenStoreAcceptanceTests {
    private TradingSystemImp tradingSystem;
    private String token;
    private String username;
    private UserRepository userRepository;
    private StoreRepository storeRepository;

    @BeforeEach
    public void setUp() {
        userRepository= UserMemoryRepository.getInstance();    //May be change later
        storeRepository= StoreMemoryRepository.getInstance();  //May be change later
        tradingSystem = TradingSystemImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class),userRepository,storeRepository);
        tradingSystem.register("owner1", "password123", LocalDate.now());
        tradingSystem.register("manager", "password123", LocalDate.now());
        tradingSystem.openSystem(storeRepository);
        String userTokenResponse = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userTokenResponse);
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
        String loginResponse = tradingSystem.login(token, "v0", "owner1", "password123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(loginResponse);
            username = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
    }

    @AfterEach
    public void tearDown() {
        tradingSystem.deleteInstance();
    }

    @Test
    public void testOpenStoreWithValidDetails() {
        // Open a store with valid details
        String storeName = "Valid Store";
        String description = "This is a valid store description.";
        ResponseEntity<String> openStoreResponse = tradingSystem.openStore(username, token, storeName, description);
        assertEquals(HttpStatus.OK, openStoreResponse.getStatusCode());
        assertEquals("Store opened successfully", openStoreResponse.getBody());
    }

    @Test
    public void testOpenStoreWithExistingStoreName() {
        // Open a store with valid details
        String storeName = "Valid Store";
        String description = "This is a valid store description.";
        ResponseEntity<String> openStoreResponse = tradingSystem.openStore(username, token, storeName, description);
        assertEquals(HttpStatus.OK, openStoreResponse.getStatusCode());
        assertEquals("Store opened successfully", openStoreResponse.getBody());
        // Attempt to open another store with the same name
        ResponseEntity<String> duplicateStoreResponse = tradingSystem.openStore(username, token, storeName, description);
        assertEquals(HttpStatus.BAD_REQUEST, duplicateStoreResponse.getStatusCode());
        assertEquals("Store with name Valid Store already exists", duplicateStoreResponse.getBody());
    }

    @Test
    public void testOpenStoreWithInvalidToken() {
        // Register a user
        String username = "validUser";
        String password = "password";
        LocalDate birthdate = LocalDate.of(1990, 1, 1);
        ResponseEntity<String> registerResponse = tradingSystem.register(username, password, birthdate);
        assertEquals(HttpStatus.OK, registerResponse.getStatusCode());
        // Attempt to open a store with an invalid token
        String invalidToken = "invalidToken";
        String storeName = "Invalid Store";
        String description = "This store should not open.";
        ResponseEntity<String> openStoreResponse = tradingSystem.openStore(username, invalidToken, storeName, description);
        assertEquals(HttpStatus.UNAUTHORIZED, openStoreResponse.getStatusCode());
        assertEquals("Invalid token was supplied", openStoreResponse.getBody());
    }

    @Test
    public void testOpenStoreWithSystemClosed() {
        tradingSystem.closeSystem(username, token);
        String storeName = "StoreWhileClosed";
        String description = "This store should not open.";
        ResponseEntity<String> openStoreResponse = tradingSystem.openStore(username, token, storeName, description);
        assertEquals(HttpStatus.FORBIDDEN, openStoreResponse.getStatusCode());
        assertEquals("System is not open. Only registration is allowed.", openStoreResponse.getBody());
    }
}

