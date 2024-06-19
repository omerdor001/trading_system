package com.example.trading_system.AcceptanceTests.Users;

import com.example.trading_system.TradingSystemApplication;
import com.example.trading_system.service.TradingSystem;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TradingSystemApplication.class)
class AddProductAcceptanceTests {
    private TradingSystem tradingSystem;
    private String token;
    private String username;
    private final String password = "123456";
    private final String productName = "Product1";
    private final String[] keyWords = {"CarPlay", "iPhone"};
    private final String storeName = "Store1";

    @BeforeEach
    void setUp() {
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
        tradingSystem.openStore(username, token, "existingStore", "General Store");
        tradingSystem.openStore(username, token, storeName, "My Store is the best");
    }

    @AfterEach
    void setDown() {
        tradingSystem.deleteInstance();
    }

    @Test
    void addProduct_Success() {
        ResponseEntity<String> result = tradingSystem.addProduct(username, token, 0, "existingStore", "product1", "", 1, 5, 1, 1, new LinkedList<>());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Product was added successfully.", result.getBody());
    }


    @Test
    void addProduct_NonExistentStore() {
        ResponseEntity<String> result = tradingSystem.addProduct(username, token, 0, "non-existingStore", "product1", "", 1, 5, 1, 1, new LinkedList<>());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Store must exist", result.getBody());
    }

    @Test
    void addProduct_NegativePrice() {
        ResponseEntity<String> result = tradingSystem.addProduct(username, token, 0, "existingStore", "product1", "", -1, 5, 1, 1, new LinkedList<>());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Price can't be a negative number", result.getBody());
    }

    @Test
    void addProduct_NegativeQuantity() {
        ResponseEntity<String> result = tradingSystem.addProduct(username, token, 0, "existingStore", "product1", "", 1, -1, 1, 1, new LinkedList<>());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Quantity must be a natural number", result.getBody());
    }

    @Test
    void addProduct_NegativeRating() {
        ResponseEntity<String> result = tradingSystem.addProduct(username, token, 0, "existingStore", "product1", "", 1, 5, -1, 1, new LinkedList<>());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Rating can't be a negative number", result.getBody());
    }

    @Test
    void addProduct_NonExistentUser() {
        ResponseEntity<String> result = tradingSystem.addProduct("owner", token, 0, "existingStore", "product1", "", 1, 5, 1, 1, new LinkedList<>());
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertEquals("Invalid token was supplied", result.getBody());
    }


    @Test
    public void givenManagerWithoutPermission_WhenAddProduct_ThenThrowException() {
        tradingSystem.register("managerWithoutPermissions", password, LocalDate.now());
        ResponseEntity<String> response = tradingSystem.enter();
        String userToken = response.getBody();
        String usernameManager = "";
        String tokenManager = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            usernameManager = rootNode.get("username").asText();
            tokenManager = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken = tradingSystem.login(tokenManager, "v1", "managerWithoutPermissions", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            usernameManager = rootNode.get("username").asText();
            tokenManager = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystem.suggestManage(username, token, usernameManager, storeName, true, false, true, true);
        tradingSystem.approveManage(usernameManager, tokenManager, storeName, username);
        tradingSystem.appointManager(username, token, username, usernameManager, storeName, true, false, true, true);
        ResponseEntity<String> response2 = tradingSystem.addProduct(usernameManager, tokenManager, 111, storeName, productName, "newDescription", 15.0, 6, 1, 1, new ArrayList<>(Arrays.asList(keyWords)));
        Assertions.assertEquals("Manager cannot add products", response2.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    public void givenManagerWithPermission_WhenAddProduct_Success() {
        tradingSystem.register("managerWithPermissions", password, LocalDate.now());
        ResponseEntity<String> response = tradingSystem.enter();
        String userToken = response.getBody();
        String usernameManager = "";
        String tokenManager = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            usernameManager = rootNode.get("username").asText();
            tokenManager = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken = tradingSystem.login(tokenManager, "v1", "managerWithPermissions", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            usernameManager = rootNode.get("username").asText();
            tokenManager = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystem.suggestManage(username, token, usernameManager, storeName, true, true, true, true);
        tradingSystem.approveManage(usernameManager, tokenManager, storeName, username);
        tradingSystem.appointManager(username, token, username, usernameManager, storeName, true, true, true, true);
        ResponseEntity<String> response2 = tradingSystem.addProduct(usernameManager, tokenManager, 111, storeName, productName, "newDescription", 15.0, 6, 1, 1, new ArrayList<>(Arrays.asList(keyWords)));
        Assertions.assertEquals("Product was added successfully.", response2.getBody());
        Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());

    }

    @Test
    public void givenRegularUser_WhenAddProduct_ThenThrowException() {

        tradingSystem.register("regularUser", password, LocalDate.now());
        ResponseEntity<String> response = tradingSystem.enter();
        String userToken = response.getBody();
        String regularUser = "";
        String regularToken = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            regularUser = rootNode.get("username").asText();
            regularToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken = tradingSystem.login(regularToken, "v1", "regularUser", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            regularUser = rootNode.get("username").asText();
            regularToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        ResponseEntity<String> response2 = tradingSystem.addProduct(regularUser, regularToken, 111, storeName, productName, "newDescription", 15.0, 6, 1, 1, new ArrayList<>(Arrays.asList(keyWords)));
        Assertions.assertEquals("User doesn't have roles", response2.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());

    }

    @Test
    public void givenProductIDExist_WhenAddProduct_ThenThrowException() {
        ResponseEntity<String> response = tradingSystem.addProduct(username, token, 111, storeName, productName, "newDescription", 15.0, 6, 1, 1, new ArrayList<>(Arrays.asList(keyWords)));
        Assertions.assertEquals("Product was added successfully.", response.getBody());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseEntity<String> response2 = tradingSystem.addProduct(username, token, 111, storeName, "anotherProduct", "newDescription", 15.0, 6, 1, 1, new ArrayList<>(Arrays.asList(keyWords)));
        Assertions.assertEquals("Product with id 111 already exists", response2.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }
}