package com.example.trading_system.AcceptanceTests.Users;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

public class FireManagerAcceptanceTests {
    private TradingSystemImp tradingSystemImp;
    private String userName = "";
    private String token = "";
    private final String storeName = "Store1";
    private final String[] keyWords = {"CarPlay", "iPhone"};
    private String ownerUserName = "";
    private String ownerToken = "";
    private String userNameManager = "";
    private String tokenManager = "";

    @BeforeEach
    public void setUp() {
        tradingSystemImp = TradingSystemImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class), mock(NotificationSender.class));
        String password = "123456";
        tradingSystemImp.register("admin", password, LocalDate.now());
        tradingSystemImp.openSystem();
        ResponseEntity<String> response = tradingSystemImp.enter();
        String userToken = response.getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            userName = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken = tradingSystemImp.login(token, "v0", "admin", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            userName = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystemImp.register("owner", password, LocalDate.now());
        tradingSystemImp.openSystem();
        ResponseEntity<String> response2 = tradingSystemImp.enter();
        String userToken2 = response2.getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken2);
            ownerUserName = rootNode.get("username").asText();
            ownerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken2 = tradingSystemImp.login(ownerToken, "v1", "owner", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken2);
            ownerUserName = rootNode.get("username").asText();
            ownerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }

        tradingSystemImp.register("manager", password, LocalDate.now());
        tradingSystemImp.openSystem();
        ResponseEntity<String> response3 = tradingSystemImp.enter();
        String userToken3 = response3.getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken3);
            userNameManager = rootNode.get("username").asText();
            tokenManager = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken3 = tradingSystemImp.login(tokenManager, "v2", "manager", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken3);
            userNameManager = rootNode.get("username").asText();
            tokenManager = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }

        tradingSystemImp.openStore(userName, token, storeName, "My Store is the best");
        tradingSystemImp.suggestOwner(userName, token, ownerUserName, storeName);
        tradingSystemImp.approveOwner(ownerUserName, ownerToken, storeName, userName);
        tradingSystemImp.appointOwner(userName, token, userName, ownerUserName, storeName);
        tradingSystemImp.suggestManage(ownerUserName, ownerToken, userNameManager, storeName, false, false, false, false);
        tradingSystemImp.approveManage(userNameManager, tokenManager, storeName, ownerUserName);
        tradingSystemImp.appointManager(ownerUserName, ownerToken, ownerUserName, userNameManager, storeName, false, false, false, false);
        String productName = "Product1";
        int productID = 111;
        tradingSystemImp.addProduct(ownerUserName, ownerToken, productID, storeName, productName, "oldDescription", 15.0, 6, 1, 1, new ArrayList<>(Arrays.asList(keyWords)));
    }

    @AfterEach
    public void tearDown() {
        tradingSystemImp.deleteInstance();
    }

    @Test
    public void GivenNotExistStore_WhenFireManager_ThenThrowException() {
        ResponseEntity<String> res = tradingSystemImp.fireManager(ownerUserName, ownerToken, "BadStoreName", userNameManager);
        assertEquals("No store called BadStoreName exist", res.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void GivenManagerNotExistUser_WhenFireManager_ThenThrowException() {
        ResponseEntity<String> res = tradingSystemImp.fireManager(ownerUserName, ownerToken, storeName, "BadUserName");
        assertEquals("No user called BadUserName exist", res.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void GivenNotOwnerUser_WhenFireManager_ThenThrowException() {
        ResponseEntity<String> res = tradingSystemImp.fireManager(userNameManager, tokenManager, storeName, userNameManager);
        assertEquals("User must be Owner", res.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void GivenNotManagerFired_WhenFireManager_ThenThrowException() {
        ResponseEntity<String> res = tradingSystemImp.fireManager(userName, token, storeName, ownerUserName);
        assertEquals("Fired user must be Manager", res.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void GivenManagerNotAppointedByThisOwner_WhenFireManager_ThenThrowException() {
        ResponseEntity<String> res = tradingSystemImp.fireManager(userName, token, storeName, userNameManager);
        assertEquals("Owner cant fire manager that he/she didn't appointed", res.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void GivenValidInput_WhenFireManager_ThenSuccess() {
        ResponseEntity<String> res = tradingSystemImp.fireManager(ownerUserName, ownerToken, storeName, userNameManager);
        assertEquals("Success fire manager", res.getBody());
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals("User is not employed in this store.", tradingSystemImp.requestInformationAboutSpecificOfficialInStore(ownerUserName, ownerToken, storeName, userNameManager).getBody());
    }
}
