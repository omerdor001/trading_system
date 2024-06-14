package com.example.trading_system.AcceptanceTests.Market;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

class StockManagementAcceptanceTests {
    private TradingSystemImp tradingSystem;
    String token1;
    String username;

    @BeforeEach
    public void setUp() {
        tradingSystem = TradingSystemImp.getInstance();
        tradingSystem.register("owner1", "password123",LocalDate.now());
        tradingSystem.openSystem();
        //Enters
        String userToken = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            token1 = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
        userToken = tradingSystem.login(token1, "0", "owner1", "password123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            username = rootNode.get("username").asText();
            token1 = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystem.openStore(username, token1, "store1", "");
        tradingSystem.enter();
    }

    @AfterEach
    public void tearDown() {
        tradingSystem.deleteInstance();
    }

    @Test
    void addProduct_Success() {
        ResponseEntity<String> response = tradingSystem.addProduct(username, token1, 123,"store1", "product1","description", 10.0, 100, 4.5,1,new LinkedList<>());
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }

    @Test
    void addProduct_StoreNotExist() {
        ResponseEntity<String> response = tradingSystem.addProduct(username, token1, 123,"nonExistentStore",  "product1","description", 10.0, 100, 4.5,1,new LinkedList<>());
        assertEquals(HttpStatusCode.valueOf(500), response.getStatusCode());
    }

    @Test
    void addProduct_PriceLessThanZero() {
        ResponseEntity<String> response = tradingSystem.addProduct(username, token1, 123,"store1",  "product1","description", -10.0, 100, 4.5,1,new LinkedList<>());
        assertEquals(HttpStatusCode.valueOf(500), response.getStatusCode());
    }

    @Test
    void addProduct_QuantityLessEqualThanZero() {
        ResponseEntity<String> response = tradingSystem.addProduct(username, token1, 123,"store1", "product1", "description", 10, 0, 4.5,1,new LinkedList<>());
        assertEquals(HttpStatusCode.valueOf(500), response.getStatusCode());
    }

    @Test
    void addProduct_RatingLessThanZero() {
        ResponseEntity<String> response = tradingSystem.addProduct(username, token1, 123,"store1", "product1", "description", 10, 100, -1,1,new LinkedList<>());
        assertEquals(HttpStatusCode.valueOf(500), response.getStatusCode());
    }

    @Test
    void addProduct_EmptyUsername() {
        ResponseEntity<String> response = tradingSystem.addProduct("", token1, 123,"store1", "product1", "description", 10, 100, -1,1,new LinkedList<>());
        assertEquals(HttpStatusCode.valueOf(401), response.getStatusCode());
    }

    @Test
    void addProduct_EmptyStoreName() {
        ResponseEntity<String> response = tradingSystem.addProduct(username, token1, 123,"", "product1", "description", 10, 100, -1,1,new LinkedList<>());
        assertEquals(HttpStatusCode.valueOf(500), response.getStatusCode());
    }

    @Test
    void removeProduct_success() {
        tradingSystem.addProduct(username, token1,123, "store1", "product1", "description", 10, 100, 4.5,1,new LinkedList<>());
        ResponseEntity<String> response = tradingSystem.removeProduct(username, token1, "store1", 123);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }

    @Test
    void removeProduct_UserNotExist() {
        ResponseEntity<String> response = tradingSystem.removeProduct("nonExistentUser", token1, "store1", 123);
        assertEquals(HttpStatusCode.valueOf(401), response.getStatusCode());
    }

    @Test
    void removeProduct_StoreNotExist() {
        ResponseEntity<String> response = tradingSystem.removeProduct(username, token1, "nonExistentStore", 123);
        assertEquals(HttpStatusCode.valueOf(500), response.getStatusCode());
    }

    @Test
    void removeProduct_ProductNotExist() {
        ResponseEntity<String> response = tradingSystem.removeProduct(username, token1, "store1", 124);
        assertEquals(HttpStatusCode.valueOf(500), response.getStatusCode());
    }

    @Test
    void setProduct_name_success() {
        tradingSystem.addProduct(username, token1, 123,"store1", "product1", "description", 10, 100, 4.5,1,new LinkedList<>());
        ResponseEntity<String> response = tradingSystem.setProductName(username, token1, "store1", 123, "newProductName");
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }

    @Test
    void setProduct_Price_PriceLessThanZero() {
        tradingSystem.addProduct(username, token1, 123,"store1", "product1", "description", 10, 100, 4.5,1,new LinkedList<>());
        ResponseEntity<String> response = tradingSystem.setProductPrice(username, token1, "store1", 123, -10);
        assertEquals(HttpStatusCode.valueOf(500), response.getStatusCode());
    }

    @Test
    void setProduct_description_UserNotExist() {
        tradingSystem.addProduct(username, token1, 123,"store1", "product1", "description", 10, 100, 4.5,1,new LinkedList<>());
        ResponseEntity<String> response = tradingSystem.setProductDescription("nonExistentUser", token1, "store1", 123, "newDescription");
        assertEquals(HttpStatusCode.valueOf(401), response.getStatusCode());
    }






}