package com.example.trading_system.AcceptanceTests.Users;

import com.example.trading_system.TradingSystemApplication;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(classes = TradingSystemApplication.class)
public class EditManagerPermissionsAcceptanceTests {
    @Autowired
    private TradingSystemImp tradingSystemImp;
    private final String password = "123456";
    private String userName = "";
    private String token = "";
    private final String storeName = "Store1";
    private final String[] keyWords = {"CarPlay", "iPhone"};
    private String tokenManager = "";
    private String userNameManager = "";
    private String regularUser = "";
    private String regularUserToken = "";

    @BeforeEach
    public void setUp() {
        tradingSystemImp = TradingSystemImp.getInstance();
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
        tradingSystemImp.openStore(userName, token, storeName, "My Store is the best");

        tradingSystemImp.register("manager", password, LocalDate.now());
        ResponseEntity<String> response2 = tradingSystemImp.enter();
        String userToken2 = response2.getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken2);
            userNameManager = rootNode.get("username").asText();
            tokenManager = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken2 = tradingSystemImp.login(tokenManager, "v1", "manager", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken2);
            userNameManager = rootNode.get("username").asText();
            tokenManager = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }

        tradingSystemImp.register("regularUser", password, LocalDate.now());
        ResponseEntity<String> response3 = tradingSystemImp.enter();
        String userToken3 = response3.getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken3);
            regularUser = rootNode.get("username").asText();
            regularUserToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken3 = tradingSystemImp.login(regularUserToken, "v2", "regularUser", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken3);
            regularUser = rootNode.get("username").asText();
            regularUserToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystemImp.suggestManage(userName, token, userNameManager, storeName, true, true, true, true);
        tradingSystemImp.approveManage(userNameManager, tokenManager, storeName, userName);
        tradingSystemImp.appointManager(userName, token, userName, userNameManager, storeName, true, true, true, true);

    }

    @AfterEach
    public void tearDown() {
        tradingSystemImp.deleteInstance();
    }

    @Test
    public void GivenStoreNotExist_WhenEditPermissionsForManager_ThenThrowException() {
        ResponseEntity<String> response = tradingSystemImp.editPermissionForManager(userName, token, userNameManager, "BadStoreName", true, true, true, true);
        Assertions.assertEquals("No store called BadStoreName exist", response.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void GivenBadUserName_WhenEditPermissionsForManager_ThenThrowException() {
        ResponseEntity<String> response = tradingSystemImp.editPermissionForManager(userName, token, "BadUserName", storeName, true, true, true, true);
        Assertions.assertEquals("No user called BadUserName exist", response.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void GivenManagerAppointer_WhenEditPermissionsForManager_ThenThrowException() {
        ResponseEntity<String> response = tradingSystemImp.editPermissionForManager(userNameManager, tokenManager, userNameManager, storeName, true, true, true, true);
        Assertions.assertEquals("User must be owner of this store", response.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void GivenRegularUserAsManagerToEdit_WhenEditPermissionForManager_ThenThrowException() {
        ResponseEntity<String> response = tradingSystemImp.editPermissionForManager(userName, token, regularUser, storeName, true, true, true, true);
        Assertions.assertEquals("User must be a  Manager of this store", response.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void GivenNotAppointedByOwner_WhenEditPermissionForManager_ThenThrowException() {
        tradingSystemImp.register("secondOwner", password, LocalDate.now());
        ResponseEntity<String> response3 = tradingSystemImp.enter();
        String userToken3 = response3.getBody();
        String ownerToken = "";
        String ownerUserName = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken3);
            ownerUserName = rootNode.get("username").asText();
            ownerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken3 = tradingSystemImp.login(tokenManager, "v3", "secondOwner", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken3);
            ownerUserName = rootNode.get("username").asText();
            ownerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestOwner(userName, token, ownerUserName, storeName).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.approveOwner(ownerUserName, ownerToken, storeName, userName).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.appointOwner(userName, token, userName, ownerUserName, storeName).getStatusCode());
        ResponseEntity<String> resp = tradingSystemImp.editPermissionForManager(ownerUserName, ownerToken, userNameManager, storeName, true, true, true, true);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertEquals("Owner cant edit permissions to manager that he/she didn't appointed", resp.getBody());
    }

    @Test
    public void GivenGoodManagerAndOwner_WhenEditPermissionToManager_ThenSuccesss() //testing EditSupply Permission
    {
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.addProduct(userNameManager, tokenManager, 111, storeName, "Product1", "ProductDescription", 5, 5, 5, 1, new ArrayList<>(Arrays.asList(keyWords))).getStatusCode());
        ResponseEntity<String> resp = tradingSystemImp.editPermissionForManager(userName, token, userNameManager, storeName, true, false, true, true);
        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
        Assertions.assertEquals("Success edit permission for manager ", resp.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, tradingSystemImp.addProduct(userNameManager, tokenManager, 222, storeName, "Product1", "ProductDescription", 5, 5, 5, 1, new ArrayList<>(Arrays.asList(keyWords))).getStatusCode());
    } // Test Permission Before and then Test Permission After

    @Test
    public void GivenGoodManagerAndOwner_WhenEditPermissionToManager_ThenSuccesss2() //testing Watch Permission
    {
        //TODO why store sales is Singelton
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.getAllHistoryPurchases(userNameManager, tokenManager, storeName).getStatusCode());
        ResponseEntity<String> resp = tradingSystemImp.editPermissionForManager(userName, token, userNameManager, storeName, false, true, true, true);
        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
        Assertions.assertEquals("Success edit permission for manager ", resp.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, tradingSystemImp.getAllHistoryPurchases(userNameManager, tokenManager, storeName).getStatusCode());
    }
}
