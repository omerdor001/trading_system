package com.example.trading_system.AcceptanceTests.Users;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.StoreDatabaseRepository;
import com.example.trading_system.domain.users.UserDatabaseRepository;
import com.example.trading_system.service.TradingSystem;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
public class PaymentAcceptanceTests {

    @Autowired
    private UserDatabaseRepository userRepository;

    @Autowired
    private StoreDatabaseRepository storeRepository;

    private NotificationSender mockNotificationSender;
    private TradingSystem tradingSystem;
    private String ownerToken;
    private String ownerUserName;
    private String regularUserToken;
    private String regularUserName;
    private String secondOwnerUserName = "owner2";
    private String secondOwnerToken;
    private final String password = "123456";
    private final String productName = "Product1";

    private final String storeName = "Store1";
    private int productID = 111;
    private String productDescription = "Product1 Description";

    @BeforeEach
    void setUp() {
        mockNotificationSender = mock(NotificationSender.class);
        tradingSystem = TradingSystemImp.getInstance(mock(PaymentService.class), mock(DeliveryService.class), mockNotificationSender, userRepository, storeRepository);
        tradingSystem.register("owner1", "password123", LocalDate.now());
        tradingSystem.openSystem(storeRepository);
        String userToken = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            ownerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
        userToken = tradingSystem.login(ownerToken, "v0", "owner1", "password123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            ownerUserName = rootNode.get("username").asText();
            ownerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystem.openStore(ownerUserName, ownerToken, storeName, "General Store");
        tradingSystem.addProduct(ownerUserName, ownerToken, productID, storeName, productName, productDescription, 15, 5, 6, 1, "[\"CarPlay\", \"iPhone\"]");

        tradingSystem.register("regularUser", "password123", LocalDate.now());
        String userToken2 = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken2);
            regularUserToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
        userToken2 = tradingSystem.login(regularUserToken, "v1", "regularUser", "password123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken2);
            regularUserName = rootNode.get("username").asText();
            regularUserToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }

        tradingSystem.register(secondOwnerUserName, "password123", LocalDate.now());
        String userToken3 = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken3);
            secondOwnerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
        userToken3 = tradingSystem.login(regularUserToken, "v2", secondOwnerUserName, "password123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken3);
            secondOwnerUserName = rootNode.get("username").asText();
            secondOwnerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
    }

    @AfterEach
    void tearDown() {
        tradingSystem.deleteInstance();
        userRepository.deleteInstance();
        storeRepository.deleteInstance();
    }

    @Test
    void placeOneBid_Success() {
        assertEquals(HttpStatus.OK, tradingSystem.suggestOwner(ownerUserName, ownerToken, secondOwnerUserName, storeName).getStatusCode());
        assertEquals(HttpStatus.OK, tradingSystem.approveOwner(secondOwnerUserName, secondOwnerToken, storeName, ownerUserName).getStatusCode());

        assertEquals(HttpStatus.OK, tradingSystem.logout(secondOwnerToken, secondOwnerUserName).getStatusCode());
        ResponseEntity<String> result = tradingSystem.placeBid(regularUserName, regularUserToken, storeName, productID, 10);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Placed bid successfully.", result.getBody());

        ResponseEntity<String> result2 = tradingSystem.getStoreBids(ownerUserName, ownerToken, storeName);
        assertEquals("{\n  \"storeName\" : \"Store1\",\n  \"bids\" : [\n{\n  \"userName\" : \"rregularUser\",\n  \"productID\" : 111,\n  \"price\" : 10.0,\n  \"allOwnersApproved\" : false,\n  \"approvedBy\" : []\n}\n]\n}", result2.getBody());
        assertEquals("{\n  \"storeName\" : \"Store1\",\n  \"bids\" : [\n{\n  \"userName\" : \"rregularUser\",\n  \"productID\" : 111,\n  \"price\" : 10.0,\n  \"allOwnersApproved\" : false,\n  \"approvedBy\" : []\n}\n]\n}", tradingSystem.getMyBids(regularUserName, regularUserToken, storeName).getBody());

        verify(mockNotificationSender).sendNotification(eq(ownerUserName), eq("{\"senderUsername\":\"rregularUser\",\"receiverUsername\":\"rowner1\",\"textContent\":\"rregularUser is placed a bid for product 111 in store Store1 with price 10.0\"}"));
        assertEquals("[{\"senderUsername\":\"rregularUser\",\"receiverUsername\":\"rowner2\",\"textContent\":\"rregularUser is placed a bid for product 111 in store Store1 with price 10.0\"}]", tradingSystem.getPendingUserNotifications(ownerUserName, ownerToken, "rowner2").getBody());
    }

    @Test
    void placeTwoBids_Success() {
        ResponseEntity<String> result = tradingSystem.placeBid(regularUserName, regularUserToken, storeName, productID, 10);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Placed bid successfully.", result.getBody());

        assertEquals(HttpStatus.OK, tradingSystem.addProduct(ownerUserName, ownerToken, 222, storeName, "product2", "product2description", 20, 5, 6, 4, "[\"CarPlay\", \"iPhone\"]").getStatusCode());
        ResponseEntity<String> result2 = tradingSystem.placeBid(regularUserName, regularUserToken, storeName, 222, 10);
        assertEquals(HttpStatus.OK, result2.getStatusCode());
        assertEquals("Placed bid successfully.", result2.getBody());

        assertEquals("{\n  \"storeName\" : \"Store1\",\n  \"bids\" : [\n{\n  \"userName\" : \"rregularUser\",\n  \"productID\" : 111,\n  \"price\" : 10.0,\n  \"allOwnersApproved\" : false,\n  \"approvedBy\" : []\n},\n{\n  \"userName\" : \"rregularUser\",\n  \"productID\" : 222,\n  \"price\" : 10.0,\n  \"allOwnersApproved\" : false,\n  \"approvedBy\" : []\n}\n]\n}", tradingSystem.getStoreBids(ownerUserName, ownerToken, storeName).getBody());
        assertEquals("{\n  \"storeName\" : \"Store1\",\n  \"bids\" : [\n{\n  \"userName\" : \"rregularUser\",\n  \"productID\" : 111,\n  \"price\" : 10.0,\n  \"allOwnersApproved\" : false,\n  \"approvedBy\" : []\n},\n{\n  \"userName\" : \"rregularUser\",\n  \"productID\" : 222,\n  \"price\" : 10.0,\n  \"allOwnersApproved\" : false,\n  \"approvedBy\" : []\n}\n]\n}", tradingSystem.getMyBids(regularUserName, regularUserToken, storeName).getBody());

        verify(mockNotificationSender).sendNotification(eq(ownerUserName), eq("{\"senderUsername\":\"rregularUser\",\"receiverUsername\":\"rowner1\",\"textContent\":\"rregularUser is placed a bid for product 111 in store Store1 with price 10.0\"}"));
        verify(mockNotificationSender).sendNotification(eq(ownerUserName), eq("{\"senderUsername\":\"rregularUser\",\"receiverUsername\":\"rowner1\",\"textContent\":\"rregularUser is placed a bid for product 222 in store Store1 with price 10.0\"}"));
    }
}