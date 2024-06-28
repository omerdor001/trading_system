package com.example.trading_system.UnitTests.Service;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.MarketFacade;
import com.example.trading_system.domain.stores.MarketFacadeImp;
import com.example.trading_system.domain.stores.StoreMemoryRepository;
import com.example.trading_system.domain.stores.StoreRepository;
import com.example.trading_system.domain.users.UserFacade;
import com.example.trading_system.domain.users.UserFacadeImp;
import com.example.trading_system.domain.users.UserMemoryRepository;
import com.example.trading_system.domain.users.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class NotificationUnitTests {
    private final String owner1 = "rowner1";
    private final String owner2 = "rowner2";
    private final String manager = "rmanager";
    private final String storeName = "store1";
    private NotificationSender mockNotificationSender;
    private UserRepository userRepository;
    private StoreRepository storeRepository;
    private UserFacade userFacade;
    private MarketFacade marketFacade;

    @BeforeEach
    public void setUp() {
        userRepository = UserMemoryRepository.getInstance();
        storeRepository = StoreMemoryRepository.getInstance();
        marketFacade = MarketFacadeImp.getInstance(storeRepository);
        mockNotificationSender = mock(NotificationSender.class);
        userFacade = UserFacadeImp.getInstance(mock(PaymentService.class), mock(DeliveryService.class), mockNotificationSender, userRepository, storeRepository);
        userRepository.addRegistered(owner1, "password123", LocalDate.now());
        userRepository.addRegistered(owner2, "password123", LocalDate.now());
        userRepository.getUser(owner1).login();
        userRepository.getUser(owner1).setAdmin(true);
        userRepository.getUser(owner1).addOwnerRole("", storeName);
        storeRepository.addStore(storeName, "", owner1, 5.0);
        storeRepository.getStore(storeName).addProduct(0, "product1", "", 1, 5, 1, 1, new LinkedList<>());
    }

    @AfterEach
    public void setDown() {
        userFacade.deleteInstance();
        marketFacade.deleteInstance();
        userRepository.deleteInstance();
        storeRepository.deleteInstance();
    }

    @Test
    public void testNotification_SuggestOwner_NotLogged() {
        try {
            userFacade.suggestOwner(owner1, owner2, storeName);
            String notifications = userFacade.getPendingUserNotifications(owner1, owner2);
            assertEquals("[{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"owner1 suggests you to become a store owner at store1\"}]", notifications);
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_SuggestOwner_Logged() {
        try {
            userFacade.getUser(owner2).login();
            userFacade.suggestOwner(owner1, owner2, storeName);
            String notifications = userFacade.getPendingUserNotifications(owner1, owner2);
            assertEquals("[]", notifications);
            verify(mockNotificationSender).sendNotification(eq(owner2), eq("{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"owner1 suggests you to become a store owner at store1\"}"));
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_SuggestManager_NotLogged() {
        try {
            userFacade.suggestManager(owner1, owner2, storeName, true, true, true, true);
            String notifications = userFacade.getPendingUserNotifications(owner1, owner2);
            assertEquals("[{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"owner1 suggests you to become a store manager at store1\"}]", notifications);
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_SuggestManager_Logged() {
        try {
            userFacade.getUser(owner2).login();
            userFacade.suggestManager(owner1, owner2, storeName, true, true, true, true);
            String notifications = userFacade.getPendingUserNotifications(owner1, owner2);
            assertEquals("[]", notifications);
            verify(mockNotificationSender).sendNotification(eq(owner2), eq("{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"owner1 suggests you to become a store manager at store1\"}"));
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_ApproveOwner_NotLogged() {
        try {
            userFacade.suggestOwner(owner1, owner2, storeName);
            userFacade.getUser(owner2).setAdmin(true);
            userFacade.getUser(owner1).logout();
            userFacade.getUser(owner2).login();
            userFacade.approveOwner(owner2, storeName, owner1);
            String notifications = userFacade.getPendingUserNotifications(owner2, owner1);
            assertEquals("[{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rowner1\",\"textContent\":\"owner2 accepted your suggestion to become an owner at store: store1\"}]", notifications);
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_ApproveOwner_Logged() {
        try {
            userFacade.suggestOwner(owner1, owner2, storeName);
            userFacade.getUser(owner2).login();
            userFacade.approveOwner(owner2, storeName, owner1);
            String notifications = userFacade.getPendingUserNotifications(owner1, owner1);
            assertEquals("[]", notifications);
            verify(mockNotificationSender).sendNotification(eq(owner1), eq("{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rowner1\",\"textContent\":\"owner2 accepted your suggestion to become an owner at store: store1\"}"));
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_ApproveManager_NotLogged() {
        try {
            userFacade.suggestManager(owner1, owner2, storeName, true, true, true, true);
            userFacade.getUser(owner2).setAdmin(true);
            userFacade.getUser(owner1).logout();
            userFacade.getUser(owner2).login();
            userFacade.approveManager(owner2, storeName, owner1, true, true, true, true);
            String notifications = userFacade.getPendingUserNotifications(owner2, owner1);
            assertEquals("[{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rowner1\",\"textContent\":\"owner2 accepted your suggestion to become a manager at store: store1\"}]", notifications);
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_ApproveManager_Logged() {
        try {
            userFacade.suggestManager(owner1, owner2, storeName, true, true, true, true);
            userFacade.getUser(owner2).login();
            userFacade.approveManager(owner2, storeName, owner1, true, true, true, true);
            String notifications = userFacade.getPendingUserNotifications(owner1, owner1);
            assertEquals("[]", notifications);
            verify(mockNotificationSender).sendNotification(eq(owner1), eq("{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rowner1\",\"textContent\":\"owner2 accepted your suggestion to become a manager at store: store1\"}"));
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_RejectOwner_NotLogged() {
        try {
            userFacade.suggestOwner(owner1, owner2, storeName);
            userFacade.getUser(owner2).setAdmin(true);
            userFacade.getUser(owner1).logout();
            userFacade.getUser(owner2).login();
            userFacade.rejectToOwnStore(owner2, storeName, owner1);
            String notifications = userFacade.getPendingUserNotifications(owner2, owner1);
            assertEquals("[{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rowner1\",\"textContent\":\"owner2 rejected your suggestion to become an owner at store: store1\"}]", notifications);
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_RejectOwner_Logged() {
        try {
            userFacade.suggestOwner(owner1, owner2, storeName);
            userFacade.getUser(owner2).login();
            userFacade.rejectToOwnStore(owner2, storeName, owner1);
            String notifications = userFacade.getPendingUserNotifications(owner1, owner1);
            assertEquals("[]", notifications);
            verify(mockNotificationSender).sendNotification(eq(owner1), eq("{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rowner1\",\"textContent\":\"owner2 rejected your suggestion to become an owner at store: store1\"}"));
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_RejectManager_NotLogged() {
        try {
            userFacade.suggestManager(owner1, owner2, storeName, true, true, true, true);
            userFacade.getUser(owner2).setAdmin(true);
            userFacade.getUser(owner1).logout();
            userFacade.getUser(owner2).login();
            userFacade.rejectToManageStore(owner2, storeName, owner1);
            String notifications = userFacade.getPendingUserNotifications(owner2, owner1);
            assertEquals("[{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rowner1\",\"textContent\":\"owner2 rejected your suggestion to become a manager at store: store1\"}]", notifications);
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_RejectManager_Logged() {
        try {
            userFacade.suggestManager(owner1, owner2, storeName, true, true, true, true);
            userFacade.getUser(owner2).login();
            userFacade.rejectToManageStore(owner2, storeName, owner1);
            String notifications = userFacade.getPendingUserNotifications(owner1, owner1);
            assertEquals("[]", notifications);
            verify(mockNotificationSender).sendNotification(eq(owner1), eq("{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rowner1\",\"textContent\":\"owner2 rejected your suggestion to become a manager at store: store1\"}"));

        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_WaiverOwner_NotLogged() {
        try {
            userRepository.addRegistered(manager, "password123", LocalDate.now());
            userFacade.suggestOwner(owner1, owner2, storeName);
            userRepository.getUser(owner2).login();
            userFacade.approveOwner(owner2, storeName, owner1);
            userFacade.getUser(owner2).login();
            userFacade.suggestOwner(owner2, manager, storeName);
            userFacade.getUser(manager).login();
            userFacade.approveOwner(manager, storeName, owner2);
            userFacade.getUser(manager).logout();
            userFacade.waiverOnOwnership(owner2, storeName);
            String notifications = userFacade.getPendingUserNotifications(owner1, owner2);
            assertEquals("[{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"owner1 suggests you to become a store owner at store1\"}]", notifications);
            notifications = userFacade.getPendingUserNotifications(owner1, manager);
            assertEquals("[{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rmanager\",\"textContent\":\"owner2 suggests you to become a store owner at store1\"},{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rmanager\",\"textContent\":\"You are no longer an owner at store: store1 due to user: owner2 waiving his ownership\"}]", notifications);
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_WaiverOwner_Logged() {
        try {
            setDown();
            setUp();
            userRepository.addRegistered(manager, "password123", LocalDate.now());
            userFacade.suggestOwner(owner1, owner2, storeName);
            userRepository.getUser(owner2).login();
            userFacade.approveOwner(owner2, storeName, owner1);
            userFacade.getUser(owner2).login();
            userFacade.suggestOwner(owner2, manager, storeName);
            userFacade.getUser(manager).login();
            userFacade.approveOwner(manager, storeName, owner2);
            userFacade.waiverOnOwnership(owner2, storeName);
            String notifications = userFacade.getPendingUserNotifications(owner1, owner2);
            assertEquals("[{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"owner1 suggests you to become a store owner at store1\"}]", notifications);
            notifications = userFacade.getPendingUserNotifications(owner1, manager);
            assertEquals("[{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rmanager\",\"textContent\":\"owner2 suggests you to become a store owner at store1\"}]", notifications);
            verify(mockNotificationSender).sendNotification(eq(manager), eq("{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rmanager\",\"textContent\":\"You are no longer an owner at store: store1 due to user: owner2 waiving his ownership\"}"));
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_FireManager_NotLogged() {
        try {
            userFacade.suggestManager(owner1, owner2, storeName, true, true, true, true);
            userFacade.getUser(owner2).login();
            userFacade.approveManager(owner2, storeName, owner1, true, true, true, true);
            userRepository.getUser(owner2).logout();
            userFacade.fireManager(owner1, storeName, owner2);
            String notifications = userFacade.getPendingUserNotifications(owner1, owner2);
            assertEquals("[{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"owner1 suggests you to become a store manager at store1\"},{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"You are no longer a manager at store: store1 due to being fired by owner1\"}]", notifications);
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_FireManager_Logged() {
        try {
            userFacade.suggestManager(owner1, owner2, storeName, true, true, true, true);
            userFacade.getUser(owner2).login();
            userFacade.approveManager(owner2, storeName, owner1, true, true, true, true);
            userFacade.fireManager(owner1, storeName, owner2);
            String notifications = userFacade.getPendingUserNotifications(owner1, owner2);
            assertEquals("[{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"owner1 suggests you to become a store manager at store1\"}]", notifications);
            verify(mockNotificationSender).sendNotification(eq(owner2), eq("{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"You are no longer a manager at store: store1 due to being fired by owner1\"}"));
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_FireOwner_NotLogged() {
        try {
            userFacade.suggestOwner(owner1, owner2, storeName);
            userRepository.getUser(owner2).login();
            userFacade.approveOwner(owner2, storeName, owner1);
            userRepository.getUser(owner2).logout();
            userFacade.fireOwner(owner1, storeName, owner2);
            String notifications = userFacade.getPendingUserNotifications(owner1, owner2);
            assertEquals("[{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"owner1 suggests you to become a store owner at store1\"},{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"You are no longer an owner at store: store1 due to being fired by user: owner1\"}]", notifications);
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_FireOwner_Logged() {
        try {
            userFacade.suggestOwner(owner1, owner2, storeName);
            userRepository.getUser(owner2).login();
            userFacade.approveOwner(owner2, storeName, owner1);
            userFacade.fireOwner(owner1, storeName, owner2);
            String notifications = userFacade.getPendingUserNotifications(owner1, owner2);
            assertEquals("[{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"owner1 suggests you to become a store owner at store1\"}]", notifications);
            verify(mockNotificationSender).sendNotification(eq(owner2), eq("{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"You are no longer an owner at store: store1 due to being fired by user: owner1\"}"));
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_EditPermission_NotLogged() {
        try {
            userFacade.suggestManager(owner1, owner2, storeName, true, true, true, true);
            userRepository.getUser(owner2).login();
            userFacade.approveManager(owner2, storeName, owner1, true, true, true, true);
            userRepository.getUser(owner2).logout();
            userFacade.editPermissionForManager(owner1, owner2, storeName, true, true, true, false);
            String notifications = userFacade.getPendingUserNotifications(owner1, owner2);
            assertEquals("[{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"owner1 suggests you to become a store manager at store1\"},{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"Your permissions for store: store1 were changed by user: owner1\"}]", notifications);
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_EditPermission_Logged() {
        try {
            userFacade.suggestManager(owner1, owner2, storeName, true, true, true, true);
            userRepository.getUser(owner2).login();
            userFacade.approveManager(owner2, storeName, owner1, true, true, true, true);
            userFacade.editPermissionForManager(owner1, owner2, storeName, true, true, true, false);
            String notifications = userFacade.getPendingUserNotifications(owner1, owner2);
            assertEquals("[{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"owner1 suggests you to become a store manager at store1\"}]", notifications);
            verify(mockNotificationSender).sendNotification(eq(owner2), eq("{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"Your permissions for store: store1 were changed by user: owner1\"}"));
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_MessageSentToUser_NotLogged() {
        try {
            userFacade.sendMessageUserToUser(owner1, owner2, "test");
            String notifications = userFacade.getPendingUserNotifications(owner1, owner2);
            assertEquals("[{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"You have received a message from user: owner1\"}]", notifications);
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_MessageSentToUser_Logged() {
        try {
            userFacade.getUser(owner2).login();
            userFacade.sendMessageUserToUser(owner1, owner2, "test");
            String notifications = userFacade.getPendingUserNotifications(owner1, owner2);
            assertEquals("[]", notifications);
            verify(mockNotificationSender).sendNotification(eq(owner2), eq("{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"You have received a message from user: owner1\"}"));
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_MessageSentToStore_NotLogged() {
        try {
            userFacade.getUser(owner2).login();
            userFacade.getUser(owner1).logout();
            marketFacade.sendMessageUserToStore(owner2, storeName, "test");
            userFacade.getUser(owner1).login();
            String notifications = userFacade.getPendingUserNotifications(owner1, owner1);
            assertEquals("[{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rowner1\",\"textContent\":\"Store: store1 received a message from user: rowner2\"}]", notifications);
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_MessageSentToStore_Logged() {
        try {
            userFacade.getUser(owner2).login();
            marketFacade.sendMessageUserToStore(owner2, storeName, "test");
            String notifications = userFacade.getPendingUserNotifications(owner1, owner1);
            assertEquals("[]", notifications);
            verify(mockNotificationSender).sendNotification(eq(owner1), eq("{\"senderUsername\":\"rowner2\",\"receiverUsername\":\"rowner1\",\"textContent\":\"Store: store1 received a message from user: rowner2\"}"));
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_MessageSentFromStore_NotLogged() {
        try {
            marketFacade.sendMessageStoreToUser(owner1, owner2, storeName, "test");
            String notifications = userFacade.getPendingUserNotifications(owner1, owner2);
            assertEquals("[{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"Owner: owner1 from store: store1 has replied to your message\"}]", notifications);
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_MessageSentFromStore_Logged() {
        try {
            userFacade.getUser(owner2).login();
            marketFacade.sendMessageStoreToUser(owner1, owner2, storeName, "test");
            String notifications = userFacade.getPendingUserNotifications(owner1, owner2);
            assertEquals("[]", notifications);
            verify(mockNotificationSender).sendNotification(eq(owner2), eq("{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"Owner: owner1 from store: store1 has replied to your message\"}"));
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_SendPendingNotifications() {
        try {
            userFacade.suggestManager(owner1, owner2, storeName, true, true, true, true);
            userRepository.getUser(owner2).login();
            userFacade.sendPendingNotifications(owner2);
            String notifications = userFacade.getPendingUserNotifications(owner1, owner2);
            assertEquals("[]", notifications);
            verify(mockNotificationSender).sendNotification(eq(owner2), eq("{\"senderUsername\":\"rowner1\",\"receiverUsername\":\"rowner2\",\"textContent\":\"owner1 suggests you to become a store manager at store1\"}"));
        } catch (Exception e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testNotification_InvalidReceiver() {
        try {
            userFacade.sendNotification(owner1, "user", "Should fail");
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals("Not a valid receiver: user", e.getMessage());
        }
    }

    @Test
    public void testNotification_InvalidSender() {
        try {
            userFacade.sendNotification("user", owner1, "Should fail");
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals("Not a valid sender: user", e.getMessage());
        }
    }

    @Test
    public void testNotification_EmptyContent() {
        try {
            userFacade.sendNotification(owner1, owner2, "");
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals("Empty notification content", e.getMessage());
        }
    }
}
