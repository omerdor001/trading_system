package com.example.trading_system.AcceptanceTests.Market;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.service.TradingSystem;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class GetProductsInfoAcceptanceTests {
    private TradingSystem tradingSystem;
    private String token;
    private String username;

    @BeforeEach
    void setup() {
        tradingSystem = TradingSystemImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class), mock(NotificationSender.class));
        tradingSystem.register("owner1", "password123", LocalDate.now());
        tradingSystem.register("manager", "password123", LocalDate.now());
        tradingSystem.openSystem();

        String userTokenResponse = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userTokenResponse);
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }

        String loginResponse = tradingSystem.login(token, "v0", "owner1", "password123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(loginResponse);
            username = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystem.enter();
    }

    @AfterEach
    public void tearDown() {
        tradingSystem.deleteInstance();
    }

    @Test
    public void testAddStoreWithInvalidToken() {
        ResponseEntity<String> response = tradingSystem.openStore(username, "invalidToken", "existingStore", "General Store");
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid token was supplied", response.getBody());
    }

    @Test
    public void testGetAllStoresSuccessfully() {
        tradingSystem.openStore(username, token, "existingStore", "General Store");
        tradingSystem.openStore(username, token, "existingStore2", "General Store");
        ResponseEntity<String> response = tradingSystem.getAllStores(username, token);
        assertEquals("[\"stores\":existingStore2,existingStore]",response.getBody());
    }

    @Test
    public void testGetStoreProductsSuccessfully() {
        tradingSystem.openStore(username, token, "store1", "General Store");
        tradingSystem.addProduct(username, token, 1, "store1", "product1", "desc1", 10.0, 100, 4, 1, new ArrayList<>());
        tradingSystem.addProduct(username, token, 2, "store1", "product1", "desc1", 10.0, 100, 4, 10, new ArrayList<>());
        ResponseEntity<String> response = tradingSystem.getStoreProducts(username, token,"store1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"name_id\":\"store1\", \"description\":\"General Store\", \"products\":[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"desc1\", \"product_price\":10.0, \"product_quantity\":100, \"rating\":4.0, \"category\":Sport, \"keyWords\":[]}, ]}",response.getBody());
    }



    @Test
    public void testGetStoreProductsNonExistentStore() {
        ResponseEntity<String> response = tradingSystem.getStoreProducts(username, token,"nonExistentStore");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Store must exist", response.getBody());
    }

    @Test
    public void testGetProductInfoNonExistentProduct() {
        tradingSystem.openStore(username, token, "store1", "General Store");
        ResponseEntity<String> response = tradingSystem.getProductInfo(username, token,"store1", 999);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Can't find product with id 999", response.getBody());
    }
}
