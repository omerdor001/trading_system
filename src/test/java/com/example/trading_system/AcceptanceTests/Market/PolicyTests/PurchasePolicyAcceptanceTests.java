package com.example.trading_system.AcceptanceTests.Market.PolicyTests;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.StoreMemoryRepository;
import com.example.trading_system.domain.stores.StoreRepository;
import com.example.trading_system.domain.users.UserMemoryRepository;
import com.example.trading_system.domain.users.UserRepository;
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
import java.time.LocalDateTime;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class PurchasePolicyAcceptanceTests {
    private TradingSystem tradingSystem;
    private String username;
    private String token;
    private final String storeName = "store1";
    private final String address = "1234 Main Street, Springfield, IL, 62704-1234";
    private UserRepository userRepository;
    private StoreRepository storeRepository;

    @BeforeEach
    void setUp() {
        userRepository= UserMemoryRepository.getInstance();    //May be change later
        storeRepository= StoreMemoryRepository.getInstance();  //May be change later
        tradingSystem = TradingSystemImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class),userRepository,storeRepository);
        tradingSystem.register("owner1", "password123", LocalDate.of(1960,1,1));
        tradingSystem.openSystem(storeRepository);
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
        tradingSystem.setAddress(username, token, address);
        tradingSystem.openStore(username, token, storeName, "");
        tradingSystem.addProduct(username, token, 0, storeName, "product1", "", 1, 5, 1, 5, new LinkedList<>());
    }

    @AfterEach
    void setDown() {
        tradingSystem.deleteInstance();
        userRepository.deleteInstance();
        storeRepository.deleteInstance();
    }

    @Test
    public void testAddPurchasePolicyByAge_success() {
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 18, 5);
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.approvePurchase(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Purchase approved", result.getBody());
    }

    @Test
    public void testAddPurchasePolicyByAge_fail() {
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 70, 5);
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.approvePurchase(username,token);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Products do not meet purchase policies conditions.", result.getBody());
    }

    @Test
    public void testAddPurchasePolicyByCategory_success() {
        tradingSystem.addPurchasePolicyByCategory(username, token, storeName, 5, 0);
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.approvePurchase(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Purchase approved", result.getBody());
    }

    @Test
    public void testAddPurchasePolicyByCategory_fail() {
        tradingSystem.addPurchasePolicyByCategory(username, token, storeName, 4, 0);
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.approvePurchase(username,token);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Products do not meet purchase policies conditions.", result.getBody());
    }

    @Test
    public void testAddPurchasePolicyByCategoryAndDate_success() {
        tradingSystem.addPurchasePolicyByCategoryAndDate(username, token, storeName, 5, LocalDateTime.of(3000,1,1,15,0));
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.approvePurchase(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Purchase approved", result.getBody());
    }

    @Test
    public void testAddPurchasePolicyByCategoryAndDate_fail() {
        tradingSystem.addPurchasePolicyByCategoryAndDate(username, token, storeName, 5, LocalDateTime.of(2000,1,1,23,0));
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.approvePurchase(username,token);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Products do not meet purchase policies conditions.", result.getBody());
    }

    @Test
    public void testAddPurchasePolicyByDate_success() {
        tradingSystem.addPurchasePolicyByDate(username, token, storeName,  LocalDateTime.of(2000,1,1,23,0) );
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.approvePurchase(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Purchase approved", result.getBody());
    }

    @Test
    public void testAddPurchasePolicyByDate_fail() {
        tradingSystem.addPurchasePolicyByDate(username, token, storeName,  LocalDateTime.of(3000,1,1,23,0));
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.approvePurchase(username,token);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Products do not meet purchase policies conditions.", result.getBody());
    }

    @Test
    public void addPurchasePolicyByProductAndDate_success() {
        tradingSystem.addPurchasePolicyByProductAndDate(username, token, storeName, 0, LocalDateTime.of(2000,1,1,23,0) );
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.approvePurchase(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Purchase approved", result.getBody());
    }

    @Test
    public void addPurchasePolicyByProductAndDate_fail() {
        tradingSystem.addPurchasePolicyByProductAndDate(username, token, storeName, 0, LocalDateTime.of(3000,1,1,23,0));
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.approvePurchase(username,token);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Products do not meet purchase policies conditions.", result.getBody());
    }

    @Test
    public void testAddPurchasePolicyByShoppingCartMaxProductsUnit_success() {
        tradingSystem.addPurchasePolicyByShoppingCartMaxProductsUnit(username, token, storeName, 0,3);
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.approvePurchase(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Purchase approved", result.getBody());
    }

    @Test
    public void testAddPurchasePolicyByShoppingCartMaxProducts_fail() {
        tradingSystem.addPurchasePolicyByShoppingCartMaxProductsUnit(username, token, storeName, 0, 3);
        tradingSystem.addToCart(username,token,0,storeName,4);
        ResponseEntity<String> result = tradingSystem.approvePurchase(username,token);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Products do not meet purchase policies conditions.", result.getBody());
    }

    @Test
    public void testAddPurchasePolicyByShoppingCartMinProducts_success() {
        tradingSystem.addPurchasePolicyByShoppingCartMinProducts(username, token, storeName, 2);
        tradingSystem.addToCart(username,token,0,storeName,3);
        ResponseEntity<String> result = tradingSystem.approvePurchase(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Purchase approved", result.getBody());
    }

    @Test
    public void testAddPurchasePolicyByShoppingCartMinProducts_fail() {
        tradingSystem.addPurchasePolicyByShoppingCartMinProducts(username, token, storeName,  3);
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.approvePurchase(username,token);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Products do not meet purchase policies conditions.", result.getBody());
    }

    @Test
    public void testAddPurchasePolicyByShoppingCartMinProductsUnit_success() {
        tradingSystem.addPurchasePolicyByShoppingCartMinProductsUnit(username, token, storeName, 0,2);
        tradingSystem.addToCart(username,token,0,storeName,3);
        ResponseEntity<String> result = tradingSystem.approvePurchase(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Purchase approved", result.getBody());
    }

    @Test
    public void testAddPurchasePolicyByShoppingCartMinProductsUnit_fail() {
        tradingSystem.addPurchasePolicyByShoppingCartMinProductsUnit(username, token, storeName, 0, 3);
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.approvePurchase(username,token);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Products do not meet purchase policies conditions.", result.getBody());
    }

    @Test
    public void testAddAndPurchasePolicy_success() {
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 18, 5);
        tradingSystem.addPurchasePolicyByShoppingCartMinProducts(username, token, storeName, 2);
        tradingSystem.addAndPurchasePolicy(username,token,storeName);
        tradingSystem.setFirstPurchasePolicy(username,token,storeName,2,0);
        tradingSystem.setSecondPurchasePolicy(username,token,storeName,1,0);
        tradingSystem.addToCart(username,token,0,storeName,3);
        ResponseEntity<String> result = tradingSystem.approvePurchase(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Purchase approved", result.getBody());
    }

    @Test
    public void testAddAndPurchasePolicy_fail() {
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 18, 5);
        tradingSystem.addPurchasePolicyByShoppingCartMinProducts(username, token, storeName, 2);
        tradingSystem.addAndPurchasePolicy(username,token,storeName);
        tradingSystem.setFirstPurchasePolicy(username,token,storeName,2,0);
        tradingSystem.setSecondPurchasePolicy(username,token,storeName,1,0);
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.approvePurchase(username,token);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Products do not meet purchase policies conditions.", result.getBody());
    }

    @Test
    public void testOrAndPurchasePolicy_success() {
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 18, 5);
        tradingSystem.addPurchasePolicyByShoppingCartMinProducts(username, token, storeName, 2);
        tradingSystem.addOrPurchasePolicy(username,token,storeName);
        tradingSystem.setFirstPurchasePolicy(username,token,storeName,2,0);
        tradingSystem.setSecondPurchasePolicy(username,token,storeName,1,0);
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.approvePurchase(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Purchase approved", result.getBody());
    }

    @Test
    public void testAddOrPurchasePolicy_fail() {
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 70, 5);
        tradingSystem.addPurchasePolicyByShoppingCartMinProducts(username, token, storeName, 2);
        tradingSystem.addOrPurchasePolicy(username,token,storeName);
        tradingSystem.setFirstPurchasePolicy(username,token,storeName,2,0);
        tradingSystem.setSecondPurchasePolicy(username,token,storeName,1,0);
        tradingSystem.addToCart(username,token,0,storeName,1);
        ResponseEntity<String> result = tradingSystem.approvePurchase(username,token);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Products do not meet purchase policies conditions.", result.getBody());
    }

    @Test
    public void testAddConditioningPurchasePolicy_success() {
        tradingSystem.addPurchasePolicyByShoppingCartMinProducts(username, token, storeName, 2);
        tradingSystem.addPurchasePolicyByShoppingCartMinProducts(username, token, storeName, 5);
        tradingSystem.addConditioningPurchasePolicy(username,token,storeName);
        tradingSystem.setFirstPurchasePolicy(username,token,storeName,2,0);
        tradingSystem.setSecondPurchasePolicy(username,token,storeName,1,0);
        tradingSystem.addToCart(username,token,0,storeName,5);
        ResponseEntity<String> result = tradingSystem.approvePurchase(username,token);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Purchase approved", result.getBody());
    }

    @Test
    public void testAddConditioningPurchasePolicy_fail() {
        tradingSystem.addPurchasePolicyByShoppingCartMinProducts(username, token, storeName, 5);
        tradingSystem.addPurchasePolicyByShoppingCartMinProducts(username, token, storeName, 2);
        tradingSystem.addConditioningPurchasePolicy(username,token,storeName);
        tradingSystem.setFirstPurchasePolicy(username,token,storeName,2,0);
        tradingSystem.setSecondPurchasePolicy(username,token,storeName,1,0);
        tradingSystem.addToCart(username,token,0,storeName,3);
        ResponseEntity<String> result = tradingSystem.approvePurchase(username,token);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Products do not meet purchase policies conditions.", result.getBody());
    }

    @Test
    public void testGetPurchasePolicy() {
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 18,5);
        ResponseEntity<String> result = tradingSystem.getPurchasePoliciesInfo(username, token, storeName);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[ { \"type\": \"ShoppingCart age and category\", \"category\": 5, \"ageToCheck\": 18 } ]", result.getBody());
    }

}

