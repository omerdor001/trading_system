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
import org.junit.jupiter.api.Disabled;
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
    private final String amount = "100.00";
    private final String currency = "USD";
    private final String cardNumber = "4111111111111111";
    private final String month = "12";
    private final String year = "2025";
    private final String holder = "John Doe";
    private final String ccv = "123";
    private final String id = "123456789";
    private UserRepository userRepository;
    private StoreRepository storeRepository;

    @BeforeEach
    void setUp() {
        userRepository = UserMemoryRepository.getInstance();    // May be changed later
        storeRepository = StoreMemoryRepository.getInstance();  // May be changed later
        tradingSystem = TradingSystemImp.getInstance(mock(PaymentService.class), mock(DeliveryService.class), mock(NotificationSender.class), userRepository, storeRepository);
        tradingSystem.register("owner1", "password123", LocalDate.of(1960, 1, 1));
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
        tradingSystem.addProduct(username, token, 0, storeName, "product1", "", 1, 5, 1, 5, "[]");
    }

    @AfterEach
    void tearDown() {
        tradingSystem.deleteInstance();
        userRepository.deleteInstance();
        storeRepository.deleteInstance();
    }

    private ResponseEntity<String> approvePurchase() {
        return tradingSystem.approvePurchase(username, token, address, amount, currency, cardNumber, month, year, holder, ccv, id);
    }

    @Test
    public void testAddPurchasePolicyByAge_success() {
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 18, 5);
        tradingSystem.addToCart(username, token, 0, storeName, 1, 1);
        ResponseEntity<String> result = approvePurchase();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Purchase approved", result.getBody());
    }

    @Test
    public void testAddPurchasePolicyByAge_fail() {
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 70, 5);
        tradingSystem.addToCart(username, token, 0, storeName, 1, 1);
        ResponseEntity<String> result = approvePurchase();
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Products do not meet purchase policies conditions.", result.getBody());
    }

    @Test
    public void testAddPurchasePolicyByCategoryAndDate_success() {
        tradingSystem.addPurchasePolicyByCategoryAndDate(username, token, storeName, 5, LocalDateTime.of(3000, 1, 1, 15, 0));
        tradingSystem.addToCart(username, token, 0, storeName, 1, 1);
        ResponseEntity<String> result = approvePurchase();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Purchase approved", result.getBody());
    }

    @Test
    public void testAddPurchasePolicyByCategoryAndDate_fail() {
        tradingSystem.addPurchasePolicyByCategoryAndDate(username, token, storeName, 5, LocalDateTime.of(2000, 1, 1, 23, 0));
        tradingSystem.addToCart(username, token, 0, storeName, 1, 1);
        ResponseEntity<String> result = approvePurchase();
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Products do not meet purchase policies conditions.", result.getBody());
    }

    @Test
    public void testAddPurchasePolicyByDate_success() {
        tradingSystem.addPurchasePolicyByDate(username, token, storeName, LocalDateTime.of(2000, 1, 1, 23, 0));
        tradingSystem.addToCart(username, token, 0, storeName, 1, 1);
        ResponseEntity<String> result = approvePurchase();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Purchase approved", result.getBody());
    }

    @Test
    public void testAddPurchasePolicyByDate_fail() {
        tradingSystem.addPurchasePolicyByDate(username, token, storeName, LocalDateTime.of(3000, 1, 1, 23, 0));
        tradingSystem.addToCart(username, token, 0, storeName, 1, 1);
        ResponseEntity<String> result = approvePurchase();
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Products do not meet purchase policies conditions.", result.getBody());
    }

    @Test
    public void addPurchasePolicyByProductAndDate_success() {
        tradingSystem.addPurchasePolicyByProductAndDate(username, token, storeName, 0, LocalDateTime.of(2000, 1, 1, 23, 0));
        tradingSystem.addToCart(username, token, 0, storeName, 1, 1);
        ResponseEntity<String> result = approvePurchase();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Purchase approved", result.getBody());
    }

    @Test
    public void addPurchasePolicyByProductAndDate_fail() {
        tradingSystem.addPurchasePolicyByProductAndDate(username, token, storeName, 0, LocalDateTime.of(3000, 1, 1, 23, 0));
        tradingSystem.addToCart(username, token, 0, storeName, 1, 1);
        ResponseEntity<String> result = approvePurchase();
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Products do not meet purchase policies conditions.", result.getBody());
    }

    @Test
    public void testAddPurchasePolicyByShoppingCartMaxProductsUnit_success() {
        tradingSystem.addPurchasePolicyByShoppingCartMaxProductsUnit(username, token, storeName, 0, 3);
        tradingSystem.addToCart(username, token, 0, storeName, 1, 1);
        ResponseEntity<String> result = approvePurchase();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Purchase approved", result.getBody());
    }

    @Test
    public void testAddPurchasePolicyByShoppingCartMaxProducts_fail() {
        tradingSystem.addPurchasePolicyByShoppingCartMaxProductsUnit(username, token, storeName, 0, 3);
        tradingSystem.addToCart(username, token, 0, storeName, 4, 1);
        ResponseEntity<String> result = approvePurchase();
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Products do not meet purchase policies conditions.", result.getBody());
    }

    @Test
    public void testAddPurchasePolicyByShoppingCartMinProducts_success() {
        tradingSystem.addPurchasePolicyByShoppingCartMinProducts(username, token, storeName, 2);
        tradingSystem.addToCart(username, token, 0, storeName, 3, 1);
        ResponseEntity<String> result = approvePurchase();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Purchase approved", result.getBody());
    }

    @Test
    public void testAddPurchasePolicyByShoppingCartMinProducts_fail() {
        tradingSystem.addPurchasePolicyByShoppingCartMinProducts(username, token, storeName, 3);
        tradingSystem.addToCart(username, token, 0, storeName, 1, 1);
        ResponseEntity<String> result = approvePurchase();
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Products do not meet purchase policies conditions.", result.getBody());
    }

    @Test
    public void testAddPurchasePolicyByShoppingCartMinProductsUnit_success() {
        tradingSystem.addPurchasePolicyByShoppingCartMinProductsUnit(username, token, storeName, 0, 2);
        tradingSystem.addToCart(username, token, 0, storeName, 3, 1);
        ResponseEntity<String> result = approvePurchase();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Purchase approved", result.getBody());
    }

    @Test
    public void testAddPurchasePolicyByShoppingCartMinProductsUnit_fail() {
        tradingSystem.addPurchasePolicyByShoppingCartMinProductsUnit(username, token, storeName, 0, 3);
        tradingSystem.addToCart(username, token, 0, storeName, 1, 1);
        ResponseEntity<String> result = approvePurchase();
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Products do not meet purchase policies conditions.", result.getBody());
    }

    @Test
    public void testAddAndPurchasePolicy_success() {
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 18, 5);
        tradingSystem.addPurchasePolicyByShoppingCartMinProducts(username, token, storeName, 2);
        tradingSystem.addAndPurchasePolicy(username, token, storeName);
        tradingSystem.setFirstPurchasePolicy(username, token, storeName, 2, 0);
        tradingSystem.setSecondPurchasePolicy(username, token, storeName, 1, 0);
        tradingSystem.addToCart(username, token, 0, storeName, 3, 1);
        ResponseEntity<String> result = approvePurchase();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Purchase approved", result.getBody());
    }

    @Test
    public void testAddAndPurchasePolicy_fail() {
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 18, 5);
        tradingSystem.addPurchasePolicyByShoppingCartMinProducts(username, token, storeName, 2);
        tradingSystem.addAndPurchasePolicy(username, token, storeName);
        tradingSystem.setFirstPurchasePolicy(username, token, storeName, 2, 0);
        tradingSystem.setSecondPurchasePolicy(username, token, storeName, 1, 0);
        tradingSystem.addToCart(username, token, 0, storeName, 1, 1);
        ResponseEntity<String> result = approvePurchase();
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Products do not meet purchase policies conditions.", result.getBody());
    }

    @Test
    public void testOrAndPurchasePolicy_success() {
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 18, 5);
        tradingSystem.addPurchasePolicyByShoppingCartMinProducts(username, token, storeName, 2);
        tradingSystem.addOrPurchasePolicy(username, token, storeName);
        tradingSystem.setFirstPurchasePolicy(username, token, storeName, 2, 0);
        tradingSystem.setSecondPurchasePolicy(username, token, storeName, 1, 0);
        tradingSystem.addToCart(username, token, 0, storeName, 1, 1);
        ResponseEntity<String> result = approvePurchase();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Purchase approved", result.getBody());
    }

    @Test
    public void testAddOrPurchasePolicy_fail() {
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 70, 5);
        tradingSystem.addPurchasePolicyByShoppingCartMinProducts(username, token, storeName, 2);
        tradingSystem.addOrPurchasePolicy(username, token, storeName);
        tradingSystem.setFirstPurchasePolicy(username, token, storeName, 2, 0);
        tradingSystem.setSecondPurchasePolicy(username, token, storeName, 1, 0);
        tradingSystem.addToCart(username, token, 0, storeName, 1, 1);
        ResponseEntity<String> result = approvePurchase();
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Products do not meet purchase policies conditions.", result.getBody());
    }

    @Test
    public void testAddConditioningPurchasePolicy_success() {
        tradingSystem.addPurchasePolicyByShoppingCartMinProducts(username, token, storeName, 2);
        tradingSystem.addPurchasePolicyByShoppingCartMinProducts(username, token, storeName, 5);
        tradingSystem.addConditioningPurchasePolicy(username, token, storeName);
        tradingSystem.setFirstPurchasePolicy(username, token, storeName, 2, 0);
        tradingSystem.setSecondPurchasePolicy(username, token, storeName, 1, 0);
        tradingSystem.addToCart(username, token, 0, storeName, 5, 1);
        ResponseEntity<String> result = approvePurchase();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Purchase approved", result.getBody());
    }

    @Test
    public void testAddConditioningPurchasePolicy_fail() {
        tradingSystem.addPurchasePolicyByShoppingCartMinProducts(username, token, storeName, 5);
        tradingSystem.addPurchasePolicyByShoppingCartMinProducts(username, token, storeName, 2);
        tradingSystem.addConditioningPurchasePolicy(username, token, storeName);
        tradingSystem.setFirstPurchasePolicy(username, token, storeName, 2, 0);
        tradingSystem.setSecondPurchasePolicy(username, token, storeName, 1, 0);
        tradingSystem.addToCart(username, token, 0, storeName, 3, 1);
        ResponseEntity<String> result = approvePurchase();
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Products do not meet purchase policies conditions.", result.getBody());
    }

    @Test
    public void testGetPurchasePolicy() {
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 18, 5);
        ResponseEntity<String> result = tradingSystem.getPurchasePoliciesInfo(username, token, storeName);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[ { \"index\": 0, \"policy\": { \"type\": \"Age and Category Policy\", \"category\": 5, \"age\": 18 } } ]", result.getBody());
    }

    @Test
    public void testSetPurchasePolicyProductId_success() {
        tradingSystem.addPurchasePolicyByShoppingCartMaxProductsUnit(username, token, storeName, 0, 5);
        tradingSystem.addProduct(username, token, 1, storeName, "product2", "", 1, 5, 1, 5, "[]");
        ResponseEntity<String> response = tradingSystem.setPurchasePolicyProductId(username, token, storeName, 0, 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product ID set for purchase policy successfully", response.getBody());
    }

    @Test
    public void testSetPurchasePolicyProductIdInvalidToken_fail() {
        tradingSystem.addPurchasePolicyByShoppingCartMaxProductsUnit(username, token, storeName, 0, 5);
        ResponseEntity<String> response = tradingSystem.setPurchasePolicyProductId(username, "invalidToken", storeName, 0, 1);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid token"));
    }

    @Test
    @Disabled
    //TODO fix me
    public void testSetPurchasePolicyProductIdNotExists_fail() {
        tradingSystem.addPurchasePolicyByShoppingCartMaxProductsUnit(username, token, storeName, 0, 5);
        ResponseEntity<String> response = tradingSystem.setPurchasePolicyProductId(username, token, storeName, 0, 3);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Product with id 3 does not exist", response.getBody());
    }

    @Test
    public void testSetPurchasePolicyNumOfQuantity_success() {
        tradingSystem.addPurchasePolicyByShoppingCartMaxProductsUnit(username, token, storeName, 0, 5);
        ResponseEntity<String> response = tradingSystem.setPurchasePolicyNumOfQuantity(username, token, storeName, 0, 10);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Number of quantity set for purchase policy successfully", response.getBody());
    }

    @Test
    public void testSetPurchasePolicyNumOfQuantityInvalidToken_fail() {
        tradingSystem.addPurchasePolicyByShoppingCartMaxProductsUnit(username, token, storeName, 0, 5);
        ResponseEntity<String> response = tradingSystem.setPurchasePolicyNumOfQuantity(username, "invalidToken", storeName, 0, 10);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid token"));
    }

    @Test
    public void testSetPurchasePolicyNumOfQuantityInvalidUnits_fail() {
        tradingSystem.addPurchasePolicyByShoppingCartMaxProductsUnit(username, token, storeName, 0, 5);
        ResponseEntity<String> response = tradingSystem.setPurchasePolicyNumOfQuantity(username, token, storeName, 0, 0);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Parameter units cannot be negative or zero", response.getBody());
    }

    @Test
    public void testSetPurchasePolicyDateTime_success() {
        tradingSystem.addPurchasePolicyByDate(username, token, storeName, LocalDateTime.now());
        ResponseEntity<String> response = tradingSystem.setPurchasePolicyDateTime(username, token, storeName, 0, LocalDateTime.now());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Date time set for purchase policy successfully", response.getBody());
    }

    @Test
    public void testSetPurchasePolicyDateTime_fail() {
        ResponseEntity<String> response = tradingSystem.setPurchasePolicyDateTime(username, "invalidToken", storeName, 0, LocalDateTime.now());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid token"));
    }

    @Test
    public void testSetPurchasePolicyAge_success() {
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 18, 5);
        ResponseEntity<String> response = tradingSystem.setPurchasePolicyAge(username, token, storeName, 0, 21);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Age set for purchase policy successfully", response.getBody());
    }

    @Test
    public void testSetPurchasePolicyAgeInvalidToken_fail() {
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 18, 5);
        ResponseEntity<String> response = tradingSystem.setPurchasePolicyAge(username, "invalidToken", storeName, 0, 18);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid token"));
    }

    @Test
    public void testSetPurchasePolicyAgeInvalidAge_fail() {
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 18, 5);
        ResponseEntity<String> response = tradingSystem.setPurchasePolicyAge(username, token, storeName, 0, 0);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Parameter age cannot be negative or zero", response.getBody());
    }

    @Test
    public void testSetPurchasePolicyCategory_success() {
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 18, 5);
        ResponseEntity<String> response = tradingSystem.setPurchasePolicyCategory(username, token, storeName, 0, 4);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Category set for purchase policy successfully", response.getBody());
    }

    @Test
    public void testSetPurchasePolicyCategoryInvalidToken_fail() {
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 18, 5);
        ResponseEntity<String> response = tradingSystem.setPurchasePolicyCategory(username, "invalidToken", storeName, 0, 5);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid token"));
    }

    @Test
    public void testSetPurchasePolicyCategoryInvalidCategory_fail() {
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 18, 5);
        ResponseEntity<String> response = tradingSystem.setPurchasePolicyCategory(username, token, storeName, 0, 6);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid category integer: 6", response.getBody());
    }

    @Test
    public void testSetFirstPurchasePolicy_success() {
        tradingSystem.addOrPurchasePolicy(username, token, storeName);
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 18, 5);
        ResponseEntity<String> response = tradingSystem.setFirstPurchasePolicy(username, token, storeName, 0, 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("First purchase policy set successfully", response.getBody());
    }

    @Test
    public void testSetFirstPurchasePolicy_fail() {
        tradingSystem.addOrPurchasePolicy(username, token, storeName);
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 18, 5);
        ResponseEntity<String> response = tradingSystem.setFirstPurchasePolicy(username, "invalidToken", storeName, 1, 0);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid token"));
    }

    @Test
    public void testSetFirstPurchasePolicyInvalidIndex_fail() {
        tradingSystem.addOrPurchasePolicy(username, token, storeName);
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 18, 5);
        ResponseEntity<String> response = tradingSystem.setFirstPurchasePolicy(username, token, storeName, 0, 3);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid index: 3", response.getBody());
    }

    @Test
    public void testSetSecondPurchasePolicy_success() {
        tradingSystem.addOrPurchasePolicy(username, token, storeName);
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 18, 5);
        ResponseEntity<String> response = tradingSystem.setSecondPurchasePolicy(username, token, storeName, 0, 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Second purchase policy set successfully", response.getBody());
    }
    @Test
    public void testSetSecondPurchasePolicy_fail() {
        tradingSystem.addOrPurchasePolicy(username, token, storeName);
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 18,5);
        ResponseEntity<String> response = tradingSystem.setSecondPurchasePolicy(username, "invalidToken", storeName, 1, 0);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid token"));
    }

    @Test
    public void testSetSecondPurchasePolicyInvalidIndex_fail() {
        tradingSystem.addOrPurchasePolicy(username, token, storeName);
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 18,5);
        ResponseEntity<String> response = tradingSystem.setSecondPurchasePolicy(username, token, storeName, 0, 3);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid index: 3", response.getBody());
    }

    @Test
    public void testRemovePurchasePolicy_success() {
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 18, 5);
        ResponseEntity<String> response = tradingSystem.removePurchasePolicy(username, token, storeName, 0);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Purchase policy removed successfully", response.getBody());
    }

    @Test
    public void testRemovePurchasePolicy_fail() {
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 18, 5);
        ResponseEntity<String> response = tradingSystem.removePurchasePolicy(username, "invalidToken", storeName, 0);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid token"));
    }

    @Test
    public void testRemovePurchasePolicyInvalidIndex_fail() {
        tradingSystem.addPurchasePolicyByAge(username, token, storeName, 18, 5);
        ResponseEntity<String> response = tradingSystem.removePurchasePolicy(username, token, storeName, 3);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid index: 3", response.getBody());
    }

}

