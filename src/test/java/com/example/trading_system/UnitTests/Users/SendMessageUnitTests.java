package com.example.trading_system.UnitTests.Users;

import com.example.trading_system.domain.Message;
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

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.LinkedList;

import static org.mockito.Mockito.mock;

public class SendMessageUnitTests {
    private final String owner1 = "rowner1";
    private final String owner2 = "rowner2";
    private final String storeName = "store1";
    private UserRepository userRepository;
    private StoreRepository storeRepository;
    private UserFacade userFacade;
    private MarketFacade marketFacade;

    @BeforeEach
    public void setUp() {
        userRepository = UserMemoryRepository.getInstance();
        storeRepository = StoreMemoryRepository.getInstance();
        marketFacade = MarketFacadeImp.getInstance(storeRepository);
        userFacade = UserFacadeImp.getInstance(mock(PaymentService.class), mock(DeliveryService.class), mock(NotificationSender.class), userRepository, storeRepository);
        userRepository.addRegistered(owner1, "password123", LocalDate.now());
        userRepository.addRegistered(owner2, "password123", LocalDate.now());
        userRepository.getUser(owner1).login();
        userRepository.getUser(owner1).setAdmin(true);
        userRepository.getUser(owner1).addOwnerRole("", storeName);
        storeRepository.addStore(storeName, "", owner1, 5.0);
    }

    @AfterEach
    public void setDown() {
        userFacade.deleteInstance();
        marketFacade.deleteInstance();
        userRepository.deleteInstance();
        storeRepository.deleteInstance();
    }

    @Test
    public void sendMessage_UserToUser_Success() {
        userFacade.sendMessageUserToUser(owner1, owner2, "test");
        LinkedList<Message> messages = userFacade.getUser(owner2).getMessages();
        assertEquals(1, messages.size());
        assertEquals(owner1, messages.getFirst().getSenderId());
        assertEquals("owner1", messages.getFirst().getSenderUsername());
        assertEquals("test", messages.getFirst().getContent());
    }

    @Test
    public void sendMessage_UserToUser_InvalidSender() {
        try {
            userFacade.sendMessageUserToUser("owner1", owner2, "test");
            fail("Expected an error");
        } catch (Exception e) {
            assertEquals("Message sender user must exist", e.getMessage());
        }
    }

    @Test
    public void sendMessage_UserToUser_InvalidReceiver() {
        try {
            userFacade.sendMessageUserToUser(owner1, "owner2", "test");
            fail("Expected an error");
        } catch (Exception e) {
            assertEquals("Message receiver user must exist", e.getMessage());
        }
    }

    @Test
    public void sendMessage_UserToUser_VisitorReceiver() {
        try {
            userFacade.enter(0);
            userFacade.sendMessageUserToUser(owner1, "v0", "test");
            fail("Expected an error");
        } catch (Exception e) {
            assertEquals("Visitors cannot receive messages from users", e.getMessage());
        }
    }

    @Test
    public void sendMessage_UserToUser_EmptyMessage() {
        try {
            userFacade.sendMessageUserToUser(owner1, owner2, "");
            fail("Expected an error");
        } catch (Exception e) {
            assertEquals("Message content cannot be empty", e.getMessage());
        }
    }

    @Test
    public void sendMessage_UserToStore_Success() {
        userFacade.getUser(owner2).login();
        marketFacade.sendMessageUserToStore(owner2, storeName, "test");
        LinkedList<Message> messages = marketFacade.getStore(storeName).getMessages();
        assertEquals(1, messages.size());
        assertEquals(owner2, messages.getFirst().getSenderId());
        assertEquals("owner2", messages.getFirst().getSenderUsername());
        assertEquals("test", messages.getFirst().getContent());
    }

    @Test
    public void sendMessage_UserToStore_InvalidSender() {
        try {
            userFacade.getUser(owner2).login();
            marketFacade.sendMessageUserToStore("owner2", storeName, "test");
            fail("Expected an error");
        } catch (Exception e) {
            assertEquals("Message sender user must exist", e.getMessage());
        }
    }

    @Test
    public void sendMessage_UserToStore_InvalidStore() {
        try {
            userFacade.getUser(owner2).login();
            marketFacade.sendMessageUserToStore(owner2, "storeName", "test");
            fail("Expected an error");
        } catch (Exception e) {
            assertEquals("Message receiver store must exist", e.getMessage());
        }
    }

    @Test
    public void sendMessage_UserToStore_EmptyMessage() {
        try {
            userFacade.getUser(owner2).login();
            marketFacade.sendMessageUserToStore(owner2, storeName, "");
            fail("Expected an error");
        } catch (Exception e) {
            assertEquals("Message content cannot be empty", e.getMessage());
        }
    }

    @Test
    public void sendMessage_StoreToUser_Success() {
        marketFacade.sendMessageStoreToUser(owner1, owner2, storeName, "test");
        LinkedList<Message> messages = userFacade.getUser(owner2).getMessages();
        assertEquals(1, messages.size());
        assertEquals(storeName, messages.getFirst().getSenderId());
        assertEquals(storeName, messages.getFirst().getSenderUsername());
        assertEquals("test", messages.getFirst().getContent());
    }

    @Test
    public void sendMessage_StoreToUser_Success_InvalidSender() {
        try {
            marketFacade.sendMessageStoreToUser("owner1", owner2, storeName, "test");
            fail("Expected an error");
        } catch (Exception e) {
            assertEquals("Message sender user must exist", e.getMessage());
        }
    }

    @Test
    public void sendMessage_StoreToUser_NonOwnerSender() {
        try {
            userFacade.register("user", "password123", LocalDate.now());
            userFacade.getUser("ruser").login();
            marketFacade.sendMessageStoreToUser("ruser", owner2, storeName, "test");
            fail("Expected an error");
        } catch (Exception e) {
            assertEquals("Message sender must be an owner of the store", e.getMessage());
        }
    }

    @Test
    public void sendMessage_StoreToUser_InvalidStore() {
        try {
            marketFacade.sendMessageStoreToUser(owner1, owner2, "storeName", "test");
            fail("Expected an error");
        } catch (Exception e) {
            assertEquals("Message sender store must exist", e.getMessage());
        }
    }

    @Test
    public void sendMessage_StoreToUser_EmptyMessage() {
        try {
            marketFacade.sendMessageStoreToUser(owner1, owner2, storeName, "");
            fail("Expected an error");
        } catch (Exception e) {
            assertEquals("Message content cannot be empty", e.getMessage());
        }
    }

    @Test
    public void sendMessage_MessageToJSON() {
        userFacade.sendMessageUserToUser(owner1, owner2, "test");
        String messages = userFacade.getUserMessagesJson(owner1, owner2);
        assertEquals("[{\"senderId\":\"rowner1\",\"senderUsername\":\"owner1\",\"content\":\"test\"}]",messages);
    }
}
