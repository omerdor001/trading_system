package com.example.trading_system.AcceptanceTests.Market;

import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.service.TradingSystem;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class SearchProductStoresAcceptanceTests {
    private TradingSystem tradingSystem;
    private String token;
    private String username;

    @BeforeEach
    void setUp() {
        tradingSystem = TradingSystemImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class));
        tradingSystem.register("owner1", "password123", LocalDate.now());
        tradingSystem.register("manager", "password123", LocalDate.now());
        tradingSystem.openSystem();

        String userTokenResponse = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userTokenResponse);
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            Assertions.fail("Setup failed: Unable to extract token from JSON response");
        }

        String loginResponse = tradingSystem.login(token, "v0", "owner1", "password123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(loginResponse);
            username = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            Assertions.fail("Setup failed: Unable to extract username and token from JSON response");
        }
    }

    @AfterEach
    void setDown(){
        tradingSystem.deleteInstance();
    }

    @Test
    public void testSearchNameInStore_Successful() {
        tradingSystem.openStore(username, token, "store1", "General Store");
        tradingSystem.addProduct(username, token, 1, "store1", "product1", "desc1", 10.0, 100, 4, 1, new ArrayList<>());
        ResponseEntity<String> response = tradingSystem.searchNameInStores(username, token, "product1", null, null, null, 1, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"desc1\", \"product_price\":10.0, \"product_quantity\":100, \"rating\":4.0, \"category\":Sport, \"keyWords\":[]}]",response.getBody());
    }

    @Test
    public void testSearchCategoryInStore_Successful() {
        tradingSystem.openStore(username, token, "store1", "General Store");
        tradingSystem.addProduct(username, token, 1, "store1", "product1", "desc1", 10.0, 100, 4, 1, new ArrayList<>());
        ResponseEntity<String> response = tradingSystem.searchCategoryInStores(username, token, 1, null, null, null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"desc1\", \"product_price\":10.0, \"product_quantity\":100, \"rating\":4.0, \"category\":Sport, \"keyWords\":[]}]",response.getBody());
    }

    @Test
    public void testSearchKeywordsInStore_Successful() {
        ArrayList<String> keywords = new ArrayList<>();
        keywords.add("keyword");
        tradingSystem.openStore(username, token, "store1", "General Store");
        tradingSystem.addProduct(username, token, 1, "store1", "product1", "desc1", 10.0, 100, 4, 1, keywords);
        ResponseEntity<String> response = tradingSystem.searchKeywordsInStores(username, token, "keyword", null, null, null, 1, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"desc1\", \"product_price\":10.0, \"product_quantity\":100, \"rating\":4.0, \"category\":Sport, \"keyWords\":[keyword]}]",response.getBody());
    }

    @Test
    public void testSearchNameInStore_NoNameProvided() {
        ResponseEntity<String> response = tradingSystem.searchNameInStores(username, token, null, null, null, null, 1, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No name provided", response.getBody());
    }

    @Test
    public void testSearchCategoryInStore_NoCategoryProvided() {
        ResponseEntity<String> response = tradingSystem.searchCategoryInStores(username, token, -1, null, null, null, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No category provided", response.getBody());
    }

    @Test
    public void testSearchKeywordsInStore_NoKeywordsProvided() {
        ResponseEntity<String> response = tradingSystem.searchKeywordsInStores(username, token, null, null, null, null, 1, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No keywords provided", response.getBody());
    }
}