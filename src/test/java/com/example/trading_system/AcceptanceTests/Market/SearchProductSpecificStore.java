package com.example.trading_system.AcceptanceTests.Market;

import com.example.trading_system.domain.stores.Category;
import com.example.trading_system.domain.stores.StorePolicy;
import com.example.trading_system.service.TradingSystem;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SearchProductSpecificStore {
    private TradingSystem tradingSystem;
    private String token;
    private String username;

    @BeforeEach
    void setup() {
        tradingSystem = TradingSystemImp.getInstance();

        tradingSystem.register("owner1", "password123", LocalDate.now());
        tradingSystem.register("manager", "password123", LocalDate.now());
        tradingSystem.openSystem();

        String userTokenResponse = tradingSystem.enter().getBody();
        extractToken(userTokenResponse);

        String loginResponse = tradingSystem.login(token, "0", "owner1", "password123").getBody();
        extractUsernameAndToken(loginResponse);
    }

    @AfterEach
    void tearDown() {
        tradingSystem.deleteInstance();
    }

    private void extractToken(String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            token = rootNode.get("token").asText();
            if (token == null || token.isEmpty()) {
                Assertions.fail("Setup failed: Token is null or empty");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Setup failed: Unable to extract token from JSON response. Response: " + jsonResponse);
        }
    }

    private void extractUsernameAndToken(String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            username = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
            if (username == null || username.isEmpty() || token == null || token.isEmpty()) {
                Assertions.fail("Setup failed: Username or token is null or empty");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Setup failed: Unable to extract username and token from JSON response. Response: " + jsonResponse);
        }
    }

    @Test
    public void testSearchNameInStore_Successful() {
        tradingSystem.openStore(username, token, "store1", "General Store", new StorePolicy());
        tradingSystem.addProduct(username, token, 1, "store1", "product1", "desc1", 10.0, 100, 4, 0, new ArrayList<>());
        ResponseEntity<String> response = tradingSystem.searchNameInStore(username,"product1",token, "store1", null, null, null, Category.Sport.getIntValue());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testSearchCategoryInStore_Successful() {
        tradingSystem.openStore(username, token, "store1", "General Store", new StorePolicy());
        tradingSystem.addProduct(username, token, 1, "store1", "product1", "desc1", 10.0, 100, 4, 0, new ArrayList<>());
        ResponseEntity<String> response = tradingSystem.searchCategoryInStore(username,token,Category.Sport.getIntValue(), "store1", null, null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testSearchKeywordsInStore_Successful() {
        tradingSystem.openStore(username, token, "store1", "General Store", new StorePolicy());
        tradingSystem.addProduct(username, token, 1, "store1", "product1", "desc1", 10.0, 100, 4, 0, new ArrayList<>());
        ResponseEntity<String> response = tradingSystem.searchKeywordsInStore(username,token,"keyword", "store1", null, null, null, Category.Sport.getIntValue());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testSearchNameInStore_NoNameProvided() {
        ResponseEntity<String> response = tradingSystem.searchNameInStore(username,null,token, "store1", null, null, null, Category.Sport.getIntValue());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No name provided", response.getBody());
    }

    @Test
    public void testSearchNameInStore_NoStoreNameProvided() {
        ResponseEntity<String> response = tradingSystem.searchNameInStore(username,"product1",token, null, null, null, null, Category.Sport.getIntValue());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No store name provided", response.getBody());
    }

    @Test
    public void testSearchCategoryInStore_NoCategoryProvided() {
        ResponseEntity<String> response = tradingSystem.searchCategoryInStore(username,token,-1, "store1", null, null, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No category provided", response.getBody());
    }

    @Test
    public void testSearchCategoryInStore_NoStoreNameProvided() {
        ResponseEntity<String> response = tradingSystem.searchCategoryInStore(username,token,Category.Sport.getIntValue(), null, null, null, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No store name provided", response.getBody());
    }

    @Test
    public void testSearchKeywordsInStore_NoKeywordsProvided() {
        ResponseEntity<String> response = tradingSystem.searchKeywordsInStore(username,token,null, "store1", null, null, null, Category.Sport.getIntValue());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No keywords provided", response.getBody());
    }

    @Test
    public void testSearchKeywordsInStore_NoStoreNameProvided() {
        ResponseEntity<String> response = tradingSystem.searchKeywordsInStore(username,token,"keyword", null, null, null, null, Category.Sport.getIntValue());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No store name provided", response.getBody());
    }
}
