package com.example.trading_system.AcceptanceTests.Market.ProductTests;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.StoreDatabaseRepository;
import com.example.trading_system.domain.stores.StoreMemoryRepository;
import com.example.trading_system.domain.stores.StoreRepository;
import com.example.trading_system.domain.users.UserDatabaseRepository;
import com.example.trading_system.domain.users.UserMemoryRepository;
import com.example.trading_system.domain.users.UserRepository;
import com.example.trading_system.service.TradingSystem;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import jakarta.transaction.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
public class SearchProductSpecificStoreAcceptanceTests {
    private TradingSystem tradingSystem;
    private String token;
    private String username;
    @Autowired
    private UserDatabaseRepository userRepository;

    @Autowired
    private StoreDatabaseRepository storeRepository;

    @BeforeEach
    void setup() {

        tradingSystem = TradingSystemImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class), mock(NotificationSender.class),userRepository,storeRepository);
        tradingSystem.register("owner1", "password123", LocalDate.now());
        tradingSystem.register("manager", "password123", LocalDate.now());
        tradingSystem.openSystem(storeRepository);

        String userTokenResponse = tradingSystem.enter().getBody();
        extractToken(userTokenResponse);

        String loginResponse = tradingSystem.login(token, "v0", "owner1", "password123").getBody();
        extractUsernameAndToken(loginResponse);
    }

    @AfterEach
    void tearDown() {
        tradingSystem.deleteInstance();
        userRepository.deleteInstance();
        storeRepository.deleteInstance();
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
        tradingSystem.openStore(username, token, "store1", "General Store");
        tradingSystem.addProduct(username, token, 1, "store1", "product1", "desc1", 10.0, 100, 4, 1, "");
        ResponseEntity<String> response = tradingSystem.searchNameInStore(username,"product1",token, "store1", null, null, null, 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("[{\"product_id\":1, \"store_name\":\"store1\", \"product_name\":\"product1\", \"product_description\":\"desc1\", \"product_price\":10.0, \"product_quantity\":100, \"rating\":4.0, \"category\":\"Sport\", \"keyWords\":[\"\"]}]",response.getBody());
    }

    @Test
    public void testSearchCategoryInStore_Successful() {
        tradingSystem.openStore(username, token, "store1", "General Store");
        tradingSystem.addProduct(username, token, 1, "store1", "product1", "desc1", 10.0, 100, 4, 1, "");
        ResponseEntity<String> response = tradingSystem.searchCategoryInStore(username,token,1, "store1", null, null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("[{\"product_id\":1, \"store_name\":\"store1\", \"product_name\":\"product1\", \"product_description\":\"desc1\", \"product_price\":10.0, \"product_quantity\":100, \"rating\":4.0, \"category\":\"Sport\", \"keyWords\":[\"\"]}]",response.getBody());
    }

    @Test
    public void testSearchKeywordsInStore_Successful() {
        tradingSystem.openStore(username, token, "store1", "General Store");
        tradingSystem.addProduct(username, token, 1, "store1", "product1", "desc1", 10.0, 100, 4, 1, "keyword");
        ResponseEntity<String> response = tradingSystem.searchKeywordsInStore(username,token,"keyword", "store1", null, null, null, 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("[{\"product_id\":1, \"store_name\":\"store1\", \"product_name\":\"product1\", \"product_description\":\"desc1\", \"product_price\":10.0, \"product_quantity\":100, \"rating\":4.0, \"category\":\"Sport\", \"keyWords\":[\"keyword\"]}]",response.getBody());
    }

    @Test
    public void testSearchNameInStore_NoNameProvided() {
        ResponseEntity<String> response = tradingSystem.searchNameInStore(username,null,token, "store1", null, null, null, 1);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No name provided", response.getBody());
    }

    @Test
    public void testSearchNameInStore_NoStoreNameProvided() {
        ResponseEntity<String> response = tradingSystem.searchNameInStore(username,"product1",token, null, null, null, null, 1);
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
        ResponseEntity<String> response = tradingSystem.searchCategoryInStore(username,token,1, null, null, null, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No store name provided", response.getBody());
    }

    @Test
    public void testSearchKeywordsInStore_NoKeywordsProvided() {
        ResponseEntity<String> response = tradingSystem.searchKeywordsInStore(username,token,null, "store1", null, null, null, 1);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No keywords provided", response.getBody());
    }

    @Test
    public void testSearchKeywordsInStore_NoStoreNameProvided() {
        ResponseEntity<String> response = tradingSystem.searchKeywordsInStore(username,token,"keyword", null, null, null, null, 1);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No store name provided", response.getBody());
    }
}
