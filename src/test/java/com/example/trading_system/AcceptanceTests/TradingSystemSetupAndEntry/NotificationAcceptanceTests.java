package com.example.trading_system.AcceptanceTests.TradingSystemSetupAndEntry;

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

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class NotificationAcceptanceTests {
    private final String storeName = "store1";
    private TradingSystem tradingSystem;
    private NotificationSender mockNotificationSender;
    private String token;
    private String username;
    private String owner2Username;
    private String owner2Token;
    private String managerUsername;
    private String managerToken;
    private UserRepository userRepository;
    private StoreRepository storeRepository;

    @BeforeEach
    public void setUp() {
        userRepository = UserMemoryRepository.getInstance();    //May be change later
        storeRepository = StoreMemoryRepository.getInstance();  //May be change later
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
        tradingSystem.addProduct(username, token, 0, storeName, "product1", "", 1, 5, 1, 1, "[]");
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

    private void loginManager() {
        String userToken = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            userToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
        userToken = tradingSystem.login(userToken, "v2", "manager", "password123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            managerUsername = rootNode.get("username").asText();
            managerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
    }

    @Test
    public void testNotification_SuggestOwner_NotLogged() {
        tradingSystem.suggestOwner(username, token, "rowner2", storeName);
        ResponseEntity<String> result = tradingSystem.getPendingUserNotifications(username, token, "rowner2");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"owner1 suggests you to become a store owner at store1\"}]", result.getBody());
    }

    @Test
    public void testNotification_SuggestOwner_Logged() {
        loginOwner2();
        tradingSystem.suggestOwner(username, token, "rowner2", storeName);
        ResponseEntity<String> result = tradingSystem.getPendingUserNotifications(username, token, "rowner2");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[]", result.getBody());
        verify(mockNotificationSender).sendNotification(eq("rowner2"), eq("{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"owner1 suggests you to become a store owner at store1\"}"));
    }

    @Test
    public void testNotification_SuggestManager_NotLogged() {
        tradingSystem.suggestManage(username, token, "rowner2", storeName, true, true, true, true);
        ResponseEntity<String> result = tradingSystem.getPendingUserNotifications(username, token, "rowner2");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"owner1 suggests you to become a store manager at store1\"}]", result.getBody());
    }

    @Test
    public void testNotification_SuggestManager_Logged() {
        loginOwner2();
        tradingSystem.suggestManage(username, token, "rowner2", storeName, true, true, true, true);
        ResponseEntity<String> result = tradingSystem.getPendingUserNotifications(username, token, "rowner2");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[]", result.getBody());
        verify(mockNotificationSender).sendNotification(eq("rowner2"), eq("{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"owner1 suggests you to become a store manager at store1\"}"));
    }

    @Test
    public void testNotification_ApproveOwner_NotLogged() {
        tradingSystem.suggestOwner(username, token, "rowner2", storeName);
        tradingSystem.makeAdmin(username, token, "rowner2");
        tradingSystem.logout(token, username);
        loginOwner2();
        tradingSystem.approveOwner(owner2Username, owner2Token, storeName, username);
        ResponseEntity<String> result = tradingSystem.getPendingUserNotifications(owner2Username, owner2Token, "rowner1");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rowner1\",\"textContent\":\"owner2 accepted your suggestion to become an owner at store: store1\"}]", result.getBody());
    }

    @Test
    public void testNotification_ApproveOwner_Logged() {
        tradingSystem.suggestOwner(username, token, "rowner2", storeName);
        loginOwner2();
        tradingSystem.approveOwner(owner2Username, owner2Token, storeName, username);
        ResponseEntity<String> result = tradingSystem.getPendingUserNotifications(username, token, "rowner1");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[]", result.getBody());
        verify(mockNotificationSender).sendNotification(eq("rowner1"), eq("{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rowner1\",\"textContent\":\"owner2 accepted your suggestion to become an owner at store: store1\"}"));
    }

    @Test
    public void testNotification_ApproveManager_NotLogged() {
        tradingSystem.suggestManage(username, token, "rowner2", storeName, true, true, true, true);
        tradingSystem.makeAdmin(username, token, "rowner2");
        tradingSystem.logout(token, username);
        loginOwner2();
        tradingSystem.approveManage(owner2Username, owner2Token, storeName, username, true, true, true, true);
        ResponseEntity<String> result = tradingSystem.getPendingUserNotifications(owner2Username, owner2Token, "rowner1");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rowner1\",\"textContent\":\"owner2 accepted your suggestion to become a manager at store: store1\"}]", result.getBody());
    }

    @Test
    public void testNotification_ApproveManager_Logged() {
        tradingSystem.suggestManage(username, token, "rowner2", storeName, true, true, true, true);
        loginOwner2();
        tradingSystem.approveManage(owner2Username, owner2Token, storeName, username, true, true, true, true);
        ResponseEntity<String> result = tradingSystem.getPendingUserNotifications(username, token, "rowner1");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[]", result.getBody());
        verify(mockNotificationSender).sendNotification(eq("rowner1"), eq("{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rowner1\",\"textContent\":\"owner2 accepted your suggestion to become a manager at store: store1\"}"));
    }

    @Test
    public void testNotification_RejectOwner_NotLogged() {
        tradingSystem.suggestOwner(username, token, "rowner2", storeName);
        tradingSystem.makeAdmin(username, token, "rowner2");
        tradingSystem.logout(token, username);
        loginOwner2();
        tradingSystem.rejectToOwnStore(owner2Username, owner2Token, storeName, username);
        ResponseEntity<String> result = tradingSystem.getPendingUserNotifications(owner2Username, owner2Token, "rowner1");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rowner1\",\"textContent\":\"owner2 rejected your suggestion to become an owner at store: store1\"}]", result.getBody());
    }

    @Test
    public void testNotification_RejectOwner_Logged() {
        tradingSystem.suggestOwner(username, token, "rowner2", storeName);
        loginOwner2();
        tradingSystem.rejectToOwnStore(owner2Username, owner2Token, storeName, username);
        ResponseEntity<String> result = tradingSystem.getPendingUserNotifications(username, token, "rowner1");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[]", result.getBody());
        verify(mockNotificationSender).sendNotification(eq("rowner1"), eq("{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rowner1\",\"textContent\":\"owner2 rejected your suggestion to become an owner at store: store1\"}"));
    }

    @Test
    public void testNotification_RejectManager_NotLogged() {
        tradingSystem.suggestManage(username, token, "rowner2", storeName, true, true, true, true);
        tradingSystem.makeAdmin(username, token, "rowner2");
        tradingSystem.logout(token, username);
        loginOwner2();
        tradingSystem.rejectToManageStore(owner2Username, owner2Token, storeName, username);
        ResponseEntity<String> result = tradingSystem.getPendingUserNotifications(owner2Username, owner2Token, "rowner1");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rowner1\",\"textContent\":\"owner2 rejected your suggestion to become a manager at store: store1\"}]", result.getBody());
    }

    @Test
    public void testNotification_RejectManager_Logged() {
        tradingSystem.suggestManage(username, token, "rowner2", storeName, true, true, true, true);
        loginOwner2();
        tradingSystem.rejectToManageStore(owner2Username, owner2Token, storeName, username);
        ResponseEntity<String> result = tradingSystem.getPendingUserNotifications(username, token, "rowner1");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[]", result.getBody());
        verify(mockNotificationSender).sendNotification(eq("rowner1"), eq("{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rowner1\",\"textContent\":\"owner2 rejected your suggestion to become a manager at store: store1\"}"));
    }

    @Test
    public void testNotification_WaiverOwner_NotLogged() {
        tradingSystem.register("manager", "password123", LocalDate.now());
        tradingSystem.suggestOwner(username, token, "rowner2", storeName);
        loginOwner2();
        tradingSystem.approveOwner(owner2Username, owner2Token, storeName, username);
        tradingSystem.suggestOwner(owner2Username, owner2Token, "rmanager", storeName);
        loginManager();
        tradingSystem.approveOwner(managerUsername, managerToken, storeName, owner2Username);
        tradingSystem.logout(managerToken, managerUsername);
        tradingSystem.waiverOnOwnership(owner2Username, owner2Token, storeName);
        ResponseEntity<String> result = tradingSystem.getPendingUserNotifications(username, token, "rmanager");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rmanager\",\"textContent\":\"owner2 suggests you to become a store owner at store1\"},{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rmanager\",\"textContent\":\"You are no longer an owner at store: store1 due to user: owner2 is fired/waiving his ownership\"}]", result.getBody());
    }

    @Test
    public void testNotification_WaiverOwner_Logged() {
        tradingSystem.register("manager", "password123", LocalDate.now());
        tradingSystem.suggestOwner(username, token, "rowner2", storeName);
        loginOwner2();
        tradingSystem.approveOwner(owner2Username, owner2Token, storeName, username);
        tradingSystem.suggestOwner(owner2Username, owner2Token, "rmanager", storeName);
        loginManager();
        tradingSystem.approveOwner(managerUsername, managerToken, storeName, owner2Username);
        tradingSystem.waiverOnOwnership(owner2Username, owner2Token, storeName);
        ResponseEntity<String> result = tradingSystem.getPendingUserNotifications(username, token, "rmanager");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rmanager\",\"textContent\":\"owner2 suggests you to become a store owner at store1\"}]", result.getBody());
        verify(mockNotificationSender).sendNotification(eq("rmanager"), eq("{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rmanager\",\"textContent\":\"You are no longer an owner at store: store1 due to user: owner2 is fired/waiving his ownership\"}"));
    }

    @Test
    public void testNotification_FireManager_NotLogged() {
        tradingSystem.suggestManage(username, token, "rowner2", storeName, true, true, true, true);
        loginOwner2();
        tradingSystem.approveManage(owner2Username, owner2Token, storeName, username, true, true, true, true);
        tradingSystem.logout(owner2Token, owner2Username);
        tradingSystem.fireManager(username, token, storeName, "rowner2");
        ResponseEntity<String> result = tradingSystem.getPendingUserNotifications(username, token, "rowner2");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"owner1 suggests you to become a store manager at store1\"},{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"You are no longer a manager at store: store1 due to being fired by owner1\"}]", result.getBody());
    }

    @Test
    public void testNotification_FireManager_Logged() {
        tradingSystem.suggestManage(username, token, "rowner2", storeName, true, true, true, true);
        loginOwner2();
        tradingSystem.approveManage(owner2Username, owner2Token, storeName, username, true, true, true, true);
        tradingSystem.fireManager(username, token, storeName, "rowner2");
        ResponseEntity<String> result = tradingSystem.getPendingUserNotifications(username, token, owner2Username);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"owner1 suggests you to become a store manager at store1\"}]", result.getBody());
        verify(mockNotificationSender).sendNotification(eq(owner2Username), eq("{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"You are no longer a manager at store: store1 due to being fired by owner1\"}"));
    }

    @Test
    public void testNotification_FireOwner_NotLogged() {
        tradingSystem.suggestOwner(username, token, "rowner2", storeName);
        loginOwner2();
        tradingSystem.approveOwner(owner2Username, owner2Token, storeName, username);
        tradingSystem.logout(owner2Token, owner2Username);
        tradingSystem.fireOwner(username, token, storeName, "rowner2");
        ResponseEntity<String> result = tradingSystem.getPendingUserNotifications(username, token, "rowner2");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"owner1 suggests you to become a store owner at store1\"},{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"You are no longer an owner at store: store1 due to being fired by user: owner1\"}]", result.getBody());
    }

    @Test
    public void testNotification_FireOwner_Logged() {
        tradingSystem.suggestOwner(username, token, "rowner2", storeName);
        loginOwner2();
        tradingSystem.approveOwner(owner2Username, owner2Token, storeName, username);
        tradingSystem.fireOwner(username, token, storeName, "rowner2");
        ResponseEntity<String> result = tradingSystem.getPendingUserNotifications(username, token, "rowner2");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"owner1 suggests you to become a store owner at store1\"}]", result.getBody());
        verify(mockNotificationSender).sendNotification(eq(owner2Username), eq("{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"You are no longer an owner at store: store1 due to being fired by user: owner1\"}"));
    }

    @Test
    public void testNotification_EditPermission_NotLogged() {
        tradingSystem.suggestManage(username, token, "rowner2", storeName, true, true, true, true);
        loginOwner2();
        tradingSystem.approveManage(owner2Username, owner2Token, storeName, username, true, true, true, true);
        tradingSystem.logout(owner2Token, owner2Username);
        tradingSystem.editPermissionForManager(username, token, "rowner2", storeName, true, true, true, false);
        ResponseEntity<String> result = tradingSystem.getPendingUserNotifications(username, token, "rowner2");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"owner1 suggests you to become a store manager at store1\"},{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"Your permissions for store: store1 were changed by user: owner1\"}]", result.getBody());
    }

    @Test
    public void testNotification_EditPermission_Logged() {
        tradingSystem.suggestManage(username, token, "rowner2", storeName, true, true, true, true);
        loginOwner2();
        tradingSystem.approveManage(owner2Username, owner2Token, storeName, username, true, true, true, true);
        tradingSystem.editPermissionForManager(username, token, "rowner2", storeName, true, true, true, false);
        ResponseEntity<String> result = tradingSystem.getPendingUserNotifications(username, token, "rowner2");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"owner1 suggests you to become a store manager at store1\"}]", result.getBody());
        verify(mockNotificationSender).sendNotification(eq(owner2Username), eq("{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"Your permissions for store: store1 were changed by user: owner1\"}"));
    }


    @Test
    public void testNotification_MessageSentToUser_NotLogged() {
        try {
            tradingSystem.sendMessageUserToUser(username, token, "rowner2", "test");
            ResponseEntity<String> result = tradingSystem.getPendingUserNotifications(username, token, "rowner2");
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals("[{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"You have received a message from user: owner1\"}]", result.getBody());
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_MessageSentToUser_Logged() {
        try {
            loginOwner2();
            tradingSystem.sendMessageUserToUser(username, token, "rowner2", "test");
            ResponseEntity<String> result = tradingSystem.getPendingUserNotifications(username, token, "rowner2");
            assertEquals("[]", result.getBody());
            assertEquals(HttpStatus.OK, result.getStatusCode());
            verify(mockNotificationSender).sendNotification(eq(owner2Username), eq("{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"You have received a message from user: owner1\"}"));
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_MessageSentToStore_NotLogged() {
        try {
            loginOwner2();
            tradingSystem.makeAdmin(username, token, owner2Username);
            tradingSystem.logout(token, username);
            tradingSystem.sendMessageUserToStore(owner2Username, owner2Token, storeName, "test");
            ResponseEntity<String> result = tradingSystem.getPendingUserNotifications(owner2Username, owner2Token, username);
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals("[{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rowner1\",\"textContent\":\"Store: store1 received a message from user: rowner2\"}]", result.getBody());
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_MessageSentToStore_Logged() {
        try {
            loginOwner2();
            tradingSystem.makeAdmin(username, token, owner2Username);
            tradingSystem.sendMessageUserToStore(owner2Username, owner2Token, storeName, "test");
            ResponseEntity<String> result = tradingSystem.getPendingUserNotifications(owner2Username, owner2Token, username);
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals("[]", result.getBody());
            verify(mockNotificationSender).sendNotification(eq(username), eq("{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rowner1\",\"textContent\":\"Store: store1 received a message from user: rowner2\"}"));
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_MessageSentFromStore_NotLogged() {
        try {
            tradingSystem.sendMessageStoreToUser(username, token, "rowner2", storeName, "test");
            ResponseEntity<String> result = tradingSystem.getPendingUserNotifications(username, token, "rowner2");
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals("[{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"Owner: owner1 from store: store1 has replied to your message\"}]", result.getBody());
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_MessageSentFromStore_Logged() {
        try {
            loginOwner2();
            tradingSystem.sendMessageStoreToUser(username, token, "rowner2", storeName, "test");
            ResponseEntity<String> result = tradingSystem.getPendingUserNotifications(username, token, "rowner2");
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals("[]", result.getBody());
            verify(mockNotificationSender).sendNotification(eq(owner2Username), eq("{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"Owner: owner1 from store: store1 has replied to your message\"}"));
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_SendPendingNotifications() {
        tradingSystem.suggestManage(username, token, "rowner2", storeName, true, true, true, true);
        loginOwner2();
        tradingSystem.approveManage(owner2Username, owner2Token, storeName, username, true, true, true, true);
        tradingSystem.editPermissionForManager(username, token, "rowner2", storeName, true, true, true, false);
        tradingSystem.sendPendingNotifications(owner2Username, owner2Token);
        ResponseEntity<String> result = tradingSystem.getPendingUserNotifications(username, token, "rowner2");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("[]", result.getBody());
        verify(mockNotificationSender).sendNotification(eq(owner2Username), eq("{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"owner1 suggests you to become a store manager at store1\"}"));
        verify(mockNotificationSender).sendNotification(eq(owner2Username), eq("{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"Your permissions for store: store1 were changed by user: owner1\"}"));
    }
}
