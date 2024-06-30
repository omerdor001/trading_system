package com.example.trading_system.AcceptanceTests.Users;

import com.example.trading_system.domain.Message;
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
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

public class SendMessageAcceptanceTests {
    private final String storeName = "store1";
    private TradingSystem tradingSystem;
    private NotificationSender mockNotificationSender;
    private String token;
    private String username = "rowner1";
    private String owner2Username = "rowner2";
    private String owner2Token;
    private UserRepository userRepository;
    private StoreRepository storeRepository;

    @BeforeEach
    public void setUp() {
        userRepository = UserMemoryRepository.getInstance();
        storeRepository = StoreMemoryRepository.getInstance();
        mockNotificationSender = mock(NotificationSender.class);
        tradingSystem = TradingSystemImp.getInstance(mock(PaymentService.class), mock(DeliveryService.class), mockNotificationSender, userRepository, storeRepository);
        tradingSystem.register("owner1", "password123", LocalDate.now());
        tradingSystem.register("owner2", "password123", LocalDate.now());
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
        tradingSystem.openStore(username, token, storeName, "");
        tradingSystem.addProduct(username, token, 0, storeName, "product1", "", 1, 5, 1, 1, new LinkedList<>());
    }

    @AfterEach
    public void setDown() {
        tradingSystem.deleteInstance();
        userRepository.deleteInstance();
        storeRepository.deleteInstance();
    }

    private void loginOwner2() {
        String userToken = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            owner2Token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
        userToken = tradingSystem.login(owner2Token, "v1", "owner2", "password123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            owner2Username = rootNode.get("username").asText();
            owner2Token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
    }

    @Test
    public void sendMessage_UserToUser_Success() {
        tradingSystem.sendMessageUserToUser(username, token, owner2Username, "test");
        ResponseEntity<String> result = tradingSystem.getUserMessagesJson(username, token, owner2Username);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[{\"senderId\":\"rowner1\",\"senderUsername\":\"owner1\",\"content\":\"test\"}]", result.getBody());
    }

    @Test
    public void sendMessage_UserToUser_InvalidSender() {
        ResponseEntity<String> result = tradingSystem.sendMessageUserToUser("username", token, owner2Username, "test");
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertEquals("Invalid token was supplied", result.getBody());
    }

    @Test
    public void sendMessage_UserToUser_InvalidReceiver() {
        ResponseEntity<String> result = tradingSystem.sendMessageUserToUser(username, token, "owner2Username", "test");
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Message receiver user must exist", result.getBody());
    }

    @Test
    public void sendMessage_UserToUser_VisitorReceiver() {
        tradingSystem.enter();
        ResponseEntity<String> result = tradingSystem.sendMessageUserToUser(username, token, "v1", "test");
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Visitors cannot receive messages from users", result.getBody());
    }

    @Test
    public void sendMessage_UserToUser_EmptyMessage() {
        tradingSystem.enter();
        ResponseEntity<String> result = tradingSystem.sendMessageUserToUser(username, token, owner2Username, "");
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Message content cannot be empty", result.getBody());
    }

    @Test
    public void sendMessage_UserToStore_Success() {
        loginOwner2();
        tradingSystem.sendMessageUserToStore(owner2Username, owner2Token, storeName, "test");
        ResponseEntity<String> result = tradingSystem.getStoreMessagesJson(username, token, storeName);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[{\"senderId\":\"rowner2\",\"senderUsername\":\"owner2\",\"content\":\"test\"}]", result.getBody());
    }

    @Test
    public void sendMessage_UserToStore_InvalidSender() {
        ResponseEntity<String> result = tradingSystem.sendMessageUserToStore("owner2Username", owner2Token, storeName, "test");
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertEquals("Invalid token was supplied", result.getBody());
    }

    @Test
    public void sendMessage_UserToStore_InvalidStore() {
        loginOwner2();
        ResponseEntity<String> result = tradingSystem.sendMessageUserToStore(owner2Username, owner2Token, "storeName", "test");
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Message receiver store must exist", result.getBody());
    }

    @Test
    public void sendMessage_UserToStore_EmptyMessage() {
        loginOwner2();
        ResponseEntity<String> result = tradingSystem.sendMessageUserToStore(owner2Username, owner2Token, storeName, "");
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Message content cannot be empty", result.getBody());
    }

    @Test
    public void sendMessage_StoreToUser_Success() {
        tradingSystem.sendMessageStoreToUser(username, token, owner2Username, storeName, "test");
        ResponseEntity<String> result = tradingSystem.getUserMessagesJson(username, token, owner2Username);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[{\"senderId\":\"store1\",\"senderUsername\":\"store1\",\"content\":\"test\"}]", result.getBody());
    }

    @Test
    public void sendMessage_StoreToUser_Success_InvalidSender() {
        ResponseEntity<String> result = tradingSystem.sendMessageStoreToUser("username", token, owner2Username, storeName, "test");
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertEquals("Invalid token was supplied", result.getBody());
    }

    @Test
    public void sendMessage_StoreToUser_NonOwnerSender() {
        loginOwner2();
        ResponseEntity<String> result = tradingSystem.sendMessageStoreToUser(owner2Username, owner2Token, owner2Username, storeName, "test");
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Message sender must be an owner of the store", result.getBody());
    }

    @Test
    public void sendMessage_StoreToUser_InvalidStore() {
        ResponseEntity<String> result = tradingSystem.sendMessageStoreToUser(username, token, owner2Username, "storeName", "test");
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Message sender store must exist", result.getBody());
    }

    @Test
    public void sendMessage_StoreToUser_EmptyMessage() {
        ResponseEntity<String> result = tradingSystem.sendMessageStoreToUser(username, token, owner2Username, storeName, "");
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Message content cannot be empty", result.getBody());
    }
}
