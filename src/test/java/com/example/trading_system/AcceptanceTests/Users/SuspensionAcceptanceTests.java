package com.example.trading_system.AcceptanceTests.Users;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.StoreMemoryRepository;
import com.example.trading_system.domain.stores.StoreRepository;
import com.example.trading_system.domain.users.UserMemoryRepository;
import com.example.trading_system.domain.users.UserRepository;
import com.example.trading_system.service.TradingSystemImp;
import com.example.trading_system.service.UserServiceImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

class SuspensionAcceptanceTests {
    String token1;
    String username;
    String token2;
    String username1;
    private TradingSystemImp tradingSystem;
    private final String storeName = "store1";
    private final String address = "1234 Main Street, Springfield, IL, 62704-1234";
    private final String amount = "100.00";
    private final String currency = "USD";
    private final String cardNumber = "4111111111111111";
    private final String month = "12";
    private final String year = "2025";
    private final String holder = "John Doe";
    private final String ccv = "123";
    private final String id = "123456789";
    @BeforeEach
    public void setUp() {
        UserRepository userRepository = UserMemoryRepository.getInstance();
        StoreRepository storeRepository = StoreMemoryRepository.getInstance();
        tradingSystem = TradingSystemImp.getInstance(mock(PaymentService.class), mock(DeliveryService.class), mock(NotificationSender.class), userRepository, storeRepository);
        tradingSystem.userService.getUserFacade().setUserRepository(userRepository);
        tradingSystem.userService = UserServiceImp.getInstance(mock(PaymentService.class), mock(DeliveryService.class), mock(NotificationSender.class), userRepository, storeRepository);
        tradingSystem.register("owner1", "password123", LocalDate.now());
        tradingSystem.register("emp1", "password123", LocalDate.now());
        tradingSystem.openSystem(storeRepository);
        //Enters
        String userToken = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            token1 = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response: " + userToken);
        }
        userToken = tradingSystem.login(token1, "v0", "owner1", "password123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            username = rootNode.get("username").asText();
            token1 = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystem.openStore(username, token1, "store1", "");

        String userToken1 = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken1);
            token2 = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
        userToken = tradingSystem.login(token2, "v1", "emp1", "password123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            username1 = rootNode.get("username").asText();
            token2 = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystem.openStore(username, token1, "store1", "");

    }

    @AfterEach
    public void tearDown() {
        tradingSystem.setSystemOpen(true);
        tradingSystem.deleteInstance();
    }

    @Test
    void suspendUser_Success() {
        ResponseEntity<String> response = tradingSystem.suspendUser(token1, username, username1, LocalDateTime.of(2024, 8, 1, 10, 0));
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }

    @Test
    void suspendUser_SuccessNotMakeAction() {

        tradingSystem.suspendUser(token1, username, username1, LocalDateTime.of(2024, 8, 1, 10, 0));
        ResponseEntity<String> response = tradingSystem.approvePurchase(username, token1, address, amount, currency, cardNumber, month, year, holder, ccv, id);
        assertEquals(HttpStatusCode.valueOf(401), response.getStatusCode());
    }

    @Test
    void suspendUser_AdminNotExist() {
        ResponseEntity<String> response = tradingSystem.suspendUser(token1, "", username1, LocalDateTime.of(2024, 8, 1, 10, 0));
        assertEquals(HttpStatusCode.valueOf(401), response.getStatusCode());
    }

    @Test
    void suspendUser_ToSuspendNotExist() {
        ResponseEntity<String> response = tradingSystem.suspendUser(token1, username, "", LocalDateTime.of(2024, 8, 1, 10, 0));
        assertEquals(HttpStatusCode.valueOf(500), response.getStatusCode());
    }

    @Test
    void suspendUser_DateNull() {
        ResponseEntity<String> response = tradingSystem.suspendUser(token1, username, username1, null);
        assertEquals(HttpStatusCode.valueOf(500), response.getStatusCode());
    }

    @Test
    void endSuspendUser_Success() {
        tradingSystem.suspendUser(token1, username, username1, LocalDateTime.now().plusDays(1));
        ResponseEntity<String> response = tradingSystem.endSuspendUser(token1, username, username1);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }

    @Test
    void endSuspendUser_AdminNotExist() {
        ResponseEntity<String> response = tradingSystem.endSuspendUser(token1, "", username1);
        assertEquals(HttpStatusCode.valueOf(401), response.getStatusCode());
    }

    @Test
    void endSuspendUser_ToSuspendNotExist() {
        tearDown();
        setUp();
        ResponseEntity<String> response = tradingSystem.endSuspendUser(token1, username, "");
        assertEquals(HttpStatusCode.valueOf(500), response.getStatusCode());
    }

    @Test
    void watchSuspensions_Success() {
        ResponseEntity<String> response = tradingSystem.watchSuspensions(token1, username);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }

    @Test
    void watchSuspensions_AdminNotExist() {
        ResponseEntity<String> response = tradingSystem.watchSuspensions(token1, "");
        assertEquals(HttpStatusCode.valueOf(401), response.getStatusCode());
    }
}
