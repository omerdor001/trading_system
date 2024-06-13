package com.example.trading_system.AcceptanceTests.Market;

import com.example.trading_system.domain.stores.Category;
import com.example.trading_system.domain.stores.StorePolicy;
import com.example.trading_system.service.TradingSystem;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SearchProductStores {

    private static TradingSystem tradingSystem;
    private static String token;
    private static String username;

    @BeforeAll
    void setupOnce() {
        tradingSystem = TradingSystemImp.getInstance();
        tradingSystem.register( "owner1", "password123", LocalDate.now());
        tradingSystem.register( "manager", "password123", LocalDate.now());
        tradingSystem.openSystem();

        String userTokenResponse = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userTokenResponse);
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            Assertions.fail("Setup failed: Unable to extract token from JSON response");
        }

        String loginResponse = tradingSystem.login(token, "0", "owner1", "password123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(loginResponse);
            username = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            Assertions.fail("Setup failed: Unable to extract username and token from JSON response");
        }
    }
    @Test
    public void testSearchNameInStore_Successful() {
        tradingSystem.openStore(username,token,"store1", "General Store", new StorePolicy());
        tradingSystem.addProduct(username,token,1, "store1", "product1", "desc1", 10.0, 100, 4,0,new ArrayList<>());
        ResponseEntity<String> response = tradingSystem.searchNameInStores("product1", null, null, null, Category.Sport.getIntValue(),null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testSearchCategoryInStore_Successful() {
        tradingSystem.openStore(username,token,"store1", "General Store", new StorePolicy());
        tradingSystem.addProduct(username,token,1, "store1", "product1", "desc1", 10.0, 100, 4,0,new ArrayList<>());
        ResponseEntity<String> response = tradingSystem.searchCategoryInStores(Category.Sport.getIntValue(), null, null, null,null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testSearchKeywordsInStore_Successful() {
        tradingSystem.openStore(username,token,"store1", "General Store", new StorePolicy());
        tradingSystem.addProduct(username,token,1, "store1", "product1", "desc1", 10.0, 100, 4,0,new ArrayList<>());
        ResponseEntity<String> response = tradingSystem.searchKeywordsInStores("keyword",  null, null, null, Category.Sport.getIntValue(),null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testSearchNameInStore_NoNameProvided() {
        ResponseEntity<String> response = tradingSystem.searchNameInStores(null,  null, null, null, Category.Sport.getIntValue(),null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No name provided", response.getBody());
    }

    @Test
    public void testSearchCategoryInStore_NoCategoryProvided() {
        ResponseEntity<String> response = tradingSystem.searchCategoryInStores(-1,  null, null, null,null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No category provided", response.getBody());
    }


    @Test
    public void testSearchKeywordsInStore_NoKeywordsProvided() {
        ResponseEntity<String> response = tradingSystem.searchKeywordsInStores(null, null, null, null, Category.Sport.getIntValue(),null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No keywords provided", response.getBody());
    }

}