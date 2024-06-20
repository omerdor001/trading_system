package com.example.trading_system.AcceptanceTests.Market.PolicyTests;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.service.TradingSystem;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class DiscountPolicyAcceptanceTests {
    private TradingSystem tradingSystem;
    private String username;
    private String token;
    private final String storeName = "store1";

    @BeforeEach
    void setUp() {
        tradingSystem = TradingSystemImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class), mock(NotificationSender.class));
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
        tradingSystem.openStore(username, token, storeName, "");
        tradingSystem.addProduct(username, token, 0, storeName, "product1", "", 1, 5, 1, 1, new LinkedList<>());
    }

    @AfterEach
    void setDown() {
        tradingSystem.deleteInstance();
    }

    @Test
    public void testPercentageByCategory() {
        tradingSystem.addCategoryPercentageDiscount(username, token, storeName, 1, 0.5);
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.calculatePrice(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("0.5", result.getBody());
    }

    @Test
    public void testPercentageByCategory_NoMatchingItem() {
        tradingSystem.addCategoryPercentageDiscount(username, token, storeName, 2, 0.5);
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.calculatePrice(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("1.0", result.getBody());
    }

    @Test
    public void testPercentageByProduct() {
        tradingSystem.addProductPercentageDiscount(username,token,storeName,0,0.5);
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.calculatePrice(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("0.5", result.getBody());
    }

    @Test
    public void testPercentageByProduct_NoMatchingItem() {
        tradingSystem.addProductPercentageDiscount(username,token,storeName,1,0.5);
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.calculatePrice(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("1.0", result.getBody());
    }

    @Test
    public void testPercentageByStore() {
        tradingSystem.addStoreDiscount(username,token,storeName,0.5);
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.calculatePrice(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("0.5", result.getBody());
    }

    @Test
    public void testAdditiveDiscount() {
        tradingSystem.addStoreDiscount(username,token,storeName,0.5);
        tradingSystem.addStoreDiscount(username,token,storeName,0.25);
        tradingSystem.addAdditiveDiscount(username,token,storeName);
        tradingSystem.setFirstDiscount(username,token,storeName,2,0);
        tradingSystem.setSecondDiscount(username,token,storeName,1,0);
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.calculatePrice(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("0.25", result.getBody());
    }


    @Test
    public void testMaxDiscount_FirstMax() {
        tradingSystem.addStoreDiscount(username,token,storeName,0.5);
        tradingSystem.addStoreDiscount(username,token,storeName,0.25);
        tradingSystem.addMaxDiscount(username,token,storeName);
        tradingSystem.setFirstDiscount(username,token,storeName,2,0);
        tradingSystem.setSecondDiscount(username,token,storeName,1,0);
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.calculatePrice(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("0.5", result.getBody());
    }

    @Test
    public void testMaxDiscount_SecondMax() {
        tradingSystem.addStoreDiscount(username,token,storeName,0.5);
        tradingSystem.addStoreDiscount(username,token,storeName,0.25);
        tradingSystem.addMaxDiscount(username,token,storeName);
        tradingSystem.setFirstDiscount(username,token,storeName,2,0);
        tradingSystem.setSecondDiscount(username,token,storeName,1,0);
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.calculatePrice(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("0.5", result.getBody());
    }

    @Test
    public void testConditionalDiscount_CategoryCount() {
        tradingSystem.addStoreDiscount(username,token,storeName,0.5);
        tradingSystem.addCategoryCountCondition(username,token,storeName,1,1);
        tradingSystem.addConditionalDiscount(username,token,storeName);
        tradingSystem.setFirstCondition(username,token,storeName,1,2);
        tradingSystem.setThenDiscount(username,token,storeName,1,0);
        tradingSystem.addToCart(username,token,0,storeName,2);
        ResponseEntity<String> result = tradingSystem.calculatePrice(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("1.0", result.getBody());
    }

    @Test
    public void testConditionalDiscount_CategoryCount_NotSatisfied() {
        tradingSystem.addStoreDiscount(username,token,storeName,0.5);
        tradingSystem.addCategoryCountCondition(username,token,storeName,1,2);
        tradingSystem.addConditionalDiscount(username,token,storeName);
        tradingSystem.setFirstCondition(username,token,storeName,1,2);
        tradingSystem.setThenDiscount(username,token,storeName,1,0);
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.calculatePrice(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("1.0", result.getBody());
    }

    @Test
    public void testConditionalDiscount_ProductCount() {
        tradingSystem.addStoreDiscount(username,token,storeName,0.5);
        tradingSystem.addProductCountCondition(username,token,storeName,0,2);
        tradingSystem.addConditionalDiscount(username,token,storeName);
        tradingSystem.setFirstCondition(username,token,storeName,1,2);
        tradingSystem.setThenDiscount(username,token,storeName,1,0);
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.calculatePrice(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("1.0", result.getBody());
    }

    @Test
    public void testConditionalDiscount_ProductCount_NotSatisfied() {
        tradingSystem.addStoreDiscount(username,token,storeName,0.5);
        tradingSystem.addProductCountCondition(username,token,storeName,0,2);
        tradingSystem.addConditionalDiscount(username,token,storeName);
        tradingSystem.setFirstCondition(username,token,storeName,1,2);
        tradingSystem.setThenDiscount(username,token,storeName,1,0);
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.calculatePrice(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("1.0", result.getBody());
    }

    @Test
    public void testConditionalDiscount_TotalSum() {
        tradingSystem.addStoreDiscount(username,token,storeName,0.5);
        tradingSystem.addTotalSumCondition(username,token,storeName,1);
        tradingSystem.addConditionalDiscount(username,token,storeName);
        tradingSystem.setFirstCondition(username,token,storeName,1,2);
        tradingSystem.setThenDiscount(username,token,storeName,1,0);
        tradingSystem.addToCart(username,token,0,storeName,2);
        ResponseEntity<String> result = tradingSystem.calculatePrice(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("1.0", result.getBody());
    }

    @Test
    public void testConditionalDiscount_TotalSum_NotSatisfied() {
        tradingSystem.addStoreDiscount(username,token,storeName,0.5);
        tradingSystem.addTotalSumCondition(username,token,storeName,2);
        tradingSystem.addConditionalDiscount(username,token,storeName);
        tradingSystem.setFirstCondition(username,token,storeName,1,2);
        tradingSystem.setThenDiscount(username,token,storeName,1,0);
        tradingSystem.addToCart(username,token,0,storeName,2);
        ResponseEntity<String> result = tradingSystem.calculatePrice(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("2.0", result.getBody());
    }

    @Test
    public void testAndDiscount_BothSatisfied() {
        tradingSystem.addStoreDiscount(username,token,storeName,0.5);
        tradingSystem.addTotalSumCondition(username,token,storeName,1);
        tradingSystem.addTotalSumCondition(username,token,storeName,2);
        tradingSystem.addAndDiscount(username,token,storeName);
        tradingSystem.setFirstCondition(username,token,storeName,1,2);
        tradingSystem.setSecondCondition(username,token,storeName,1,2);
        tradingSystem.setThenDiscount(username,token,storeName,1,0);
        tradingSystem.addToCart(username,token,0,storeName,3);
        ResponseEntity<String> result = tradingSystem.calculatePrice(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("1.5", result.getBody());
    }

    @Test
    public void testAndDiscount_OnlyFirstSatisfied() {
        tradingSystem.addStoreDiscount(username,token,storeName,0.5);
        tradingSystem.addTotalSumCondition(username,token,storeName,1);
        tradingSystem.addTotalSumCondition(username,token,storeName,3);
        tradingSystem.addAndDiscount(username,token,storeName);
        tradingSystem.setFirstCondition(username,token,storeName,1,2);
        tradingSystem.setSecondCondition(username,token,storeName,1,2);
        tradingSystem.setThenDiscount(username,token,storeName,1,0);
        tradingSystem.addToCart(username,token,0,storeName,3);
        ResponseEntity<String> result = tradingSystem.calculatePrice(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("3.0", result.getBody());
    }

    @Test
    public void testAndDiscount_OnlySecondSatisfied() {
        tradingSystem.addStoreDiscount(username,token,storeName,0.5);
        tradingSystem.addTotalSumCondition(username,token,storeName,3);
        tradingSystem.addTotalSumCondition(username,token,storeName,2);
        tradingSystem.addAndDiscount(username,token,storeName);
        tradingSystem.setFirstCondition(username,token,storeName,1,2);
        tradingSystem.setSecondCondition(username,token,storeName,1,2);
        tradingSystem.setThenDiscount(username,token,storeName,1,0);
        tradingSystem.addToCart(username,token,0,storeName,3);
        ResponseEntity<String> result = tradingSystem.calculatePrice(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("3.0", result.getBody());
    }

    @Test
    public void testOrDiscount_BothSatisfied() {
        tradingSystem.addStoreDiscount(username,token,storeName,0.5);
        tradingSystem.addTotalSumCondition(username,token,storeName,1);
        tradingSystem.addTotalSumCondition(username,token,storeName,2);
        tradingSystem.addOrDiscount(username,token,storeName);
        tradingSystem.setFirstCondition(username,token,storeName,1,2);
        tradingSystem.setSecondCondition(username,token,storeName,1,2);
        tradingSystem.setThenDiscount(username,token,storeName,1,0);
        tradingSystem.addToCart(username,token,0,storeName,3);
        ResponseEntity<String> result = tradingSystem.calculatePrice(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("1.5", result.getBody());
    }

    @Test
    public void testOrDiscount_FirstSatisfied() {
        tradingSystem.addStoreDiscount(username,token,storeName,0.5);
        tradingSystem.addTotalSumCondition(username,token,storeName,1);
        tradingSystem.addTotalSumCondition(username,token,storeName,3);
        tradingSystem.addOrDiscount(username,token,storeName);
        tradingSystem.setFirstCondition(username,token,storeName,1,2);
        tradingSystem.setSecondCondition(username,token,storeName,1,2);
        tradingSystem.setThenDiscount(username,token,storeName,1,0);
        tradingSystem.addToCart(username,token,0,storeName,3);
        ResponseEntity<String> result = tradingSystem.calculatePrice(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("1.5", result.getBody());
    }

    @Test
    public void testOrDiscount_SecondSatisfied() {
        tradingSystem.addStoreDiscount(username,token,storeName,0.5);
        tradingSystem.addTotalSumCondition(username,token,storeName,3);
        tradingSystem.addTotalSumCondition(username,token,storeName,2);
        tradingSystem.addOrDiscount(username,token,storeName);
        tradingSystem.setFirstCondition(username,token,storeName,1,2);
        tradingSystem.setSecondCondition(username,token,storeName,1,2);
        tradingSystem.setThenDiscount(username,token,storeName,1,0);
        tradingSystem.addToCart(username,token,0,storeName,3);
        ResponseEntity<String> result = tradingSystem.calculatePrice(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("1.5", result.getBody());
    }

    @Test
    public void testXorDiscount_FirstSatisfied() {
        tradingSystem.addStoreDiscount(username,token,storeName,0.5);
        tradingSystem.addProductPercentageDiscount(username, token, storeName, 1, 0.25);
        tradingSystem.addProductCountCondition(username,token,storeName,1,1);
        tradingSystem.addXorDiscount(username,token,storeName);
        tradingSystem.setFirstDiscount(username,token,storeName,2,0);
        tradingSystem.setSecondDiscount(username,token,storeName,1,0);
        tradingSystem.setDeciderDiscount(username,token,storeName,0,1);
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.calculatePrice(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("0.5", result.getBody());
    }

    @Test
    public void testXorDiscount_SecondSatisfied() {
        tradingSystem.addStoreDiscount(username,token,storeName,0.5);
        tradingSystem.addProductPercentageDiscount(username, token, storeName, 1, 0.25);
        tradingSystem.addProductCountCondition(username,token,storeName,1,1);
        tradingSystem.addXorDiscount(username,token,storeName);
        tradingSystem.setSecondDiscount(username,token,storeName,2,0);
        tradingSystem.setFirstDiscount(username,token,storeName,1,0);
        tradingSystem.setDeciderDiscount(username,token,storeName,0,1);
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.calculatePrice(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("0.5", result.getBody());
    }

    @Test
    public void testXorDiscount_FirstDecider() {
        tradingSystem.addStoreDiscount(username,token,storeName,0.5);
        tradingSystem.addProductPercentageDiscount(username, token, storeName, 0, 0.25);
        tradingSystem.addProductCountCondition(username,token,storeName,0,1);
        tradingSystem.addXorDiscount(username,token,storeName);
        tradingSystem.setFirstDiscount(username,token,storeName,2,0);
        tradingSystem.setSecondDiscount(username,token,storeName,1,0);
        tradingSystem.setDeciderDiscount(username,token,storeName,0,1);
        tradingSystem.addToCart(username,token,0,storeName,2);
        ResponseEntity<String> result = tradingSystem.calculatePrice(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("1.0", result.getBody());
    }

    @Test
    public void testXorDiscount_SecondDecider() {
        tradingSystem.addStoreDiscount(username,token,storeName,0.5);
        tradingSystem.addProductPercentageDiscount(username, token, storeName, 1, 0.25);
        tradingSystem.addProductCountCondition(username,token,storeName,0,1);
        tradingSystem.addXorDiscount(username,token,storeName);
        tradingSystem.setFirstDiscount(username,token,storeName,2,0);
        tradingSystem.setSecondDiscount(username,token,storeName,1,0);
        tradingSystem.setDeciderDiscount(username,token,storeName,0,1);
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.calculatePrice(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("0.5", result.getBody());
    }

    @Test
    public void testGetDiscounts() {
        tradingSystem.addStoreDiscount(username, token, storeName, 0.5);
        ResponseEntity<String> result = tradingSystem.getDiscountPolicies(username, token, storeName);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[ { \"type\": \"percentageStore\", \"percent\": 0.5 } ]", result.getBody());
    }

    @Test
    public void testGetConditions() {
        tradingSystem.addTotalSumCondition(username, token, storeName, 100);
        ResponseEntity<String> result = tradingSystem.getDiscountConditions(username, token, storeName);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[ { \"type\": \"totalSum\", \"requiredSum\": 100.0 } ]", result.getBody());
    }
}
