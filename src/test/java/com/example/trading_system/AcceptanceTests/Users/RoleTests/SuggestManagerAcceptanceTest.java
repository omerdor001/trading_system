package com.example.trading_system.AcceptanceTests.Users.RoleTests;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.StoreMemoryRepository;
import com.example.trading_system.domain.stores.StoreRepository;
import com.example.trading_system.domain.users.UserMemoryRepository;
import com.example.trading_system.domain.users.UserRepository;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

public class SuggestManagerAcceptanceTest {
    TradingSystemImp tradingSystemImp;
    private UserRepository userRepository;
    private StoreRepository storeRepository;

    @BeforeEach
    void setUp() {
        userRepository= UserMemoryRepository.getInstance();    //May be change later
        storeRepository= StoreMemoryRepository.getInstance();  //May be change later
        tradingSystemImp = TradingSystemImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class), mock(NotificationSender.class),userRepository,storeRepository);
    }

    @AfterEach
    void tearDown(){
        tradingSystemImp.deleteInstance();
    }

    @Test
    void testSuggestManager_WhenSystemIsClosed_GetError() {
        ResponseEntity<String> response = tradingSystemImp.suggestManage("appointer", "123", "newManager", "Store1", true, true, true, true);
        Assertions.assertEquals("System is not open. Only registration is allowed.", response.getBody());
    }

    @Test
    void testSuggestManager_WhenInvalidToken_GetError() {
        tradingSystemImp.register("admin", "123456", LocalDate.now());
        tradingSystemImp.openSystem(storeRepository);
        tradingSystemImp.suggestManage("admin", "123", "newManager", "Store1", true, true, true, true);
    }

    @Test
    void testSuggestManager_WhenStoreNotExist_GetError() {
        tradingSystemImp.register("admin", "123456", LocalDate.now());
        tradingSystemImp.openSystem(storeRepository);
        ResponseEntity<String> response = tradingSystemImp.enter();
        String userToken = response.getBody();
        String userName = "";
        String token = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            userName = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystemImp.suggestManage(userName, token, "newManager", "Store1", true, true, true, true);
    }

    @Test
    void testSuggestManager_Success() {
        tradingSystemImp.register("admin", "123456", LocalDate.now());
        tradingSystemImp.register("user1", "123456", LocalDate.now());
        tradingSystemImp.openSystem(storeRepository);
        ResponseEntity<String> response = tradingSystemImp.enter();
        String userToken = response.getBody();
        String userName = "";
        String token = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            userName = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken = tradingSystemImp.login(token, "v0", "admin", "123456").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            userName = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystemImp.openStore(userName, token, "Store1", "myBestStore");
    }
}
