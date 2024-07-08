package com.example.trading_system.AcceptanceTests.Users;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

public class PermissionsAcceptanceTests {
    String token1;
    String username;
    String token2;
    String username1;
    private TradingSystemImp tradingSystem;
    private UserRepository userRepository;
    private StoreRepository storeRepository;

    @BeforeEach
    public void setUp() {
        userRepository= UserMemoryRepository.getInstance();    //May be change later
        storeRepository= StoreMemoryRepository.getInstance();  //May be change later
        tradingSystem = TradingSystemImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class), mock(NotificationSender.class),userRepository,storeRepository);
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
            fail("Setup failed: Unable to extract token from JSON response");
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
        tradingSystem.suggestManage(username,token1,username1,"store1",true,true,true,true, true, true);
        tradingSystem.approveManage(username1,token2,"store1",username,true,true,true,true, true, true);
    }

    @AfterEach
    public void tearDown() {
        tradingSystem.deleteInstance();
    }

    @Test
    void addProduct_Success() {
        ResponseEntity<String> response = tradingSystem.addProduct(username1, token2, 123, "store1", "product1", "description", 10.0, 100, 4.5, 1, "[]");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void addProductManager_NoPermission() {
        tradingSystem.editPermissionForManager(username,token1,username1,"store1",true,false,true,true, true, true);
        ResponseEntity<String> response = tradingSystem.addProduct(username1, token2, 123, "store1", "product1", "description", 10.0, 100, 4.5, 1, "");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void addDiscountPolicyManager_Success() {
        ResponseEntity<String> response = tradingSystem.addAdditiveDiscount(username1,token2,"store1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void addDiscountPolicyManager_NoPermission() {
        tradingSystem.editPermissionForManager(username,token1,username1,"store1",true,true,true,false, true, true);
        ResponseEntity<String> response = tradingSystem.addAdditiveDiscount(username1,token2,"store1");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void addPurchasePolicyManager_Success() {
        tradingSystem.addProduct(username1, token2, 123, "store1", "product1", "description", 10.0, 100, 4.5, 1, "");
        ResponseEntity<String> response = tradingSystem.addPurchasePolicyByAge(username1,token2,"store1",20,1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void addPurchasePolicyManager_NoPermission() {
        tradingSystem.editPermissionForManager(username,token1,username1,"store1",true,true,false,true, true, true);
        tradingSystem.addProduct(username1, token2, 123, "store1", "product1", "description", 10.0, 100, 4.5, 1, "");
        ResponseEntity<String> response = tradingSystem.addPurchasePolicyByAge(username1,token2,"store1",20,1);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }




}
