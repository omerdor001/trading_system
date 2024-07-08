package com.example.trading_system.UnitTests.Users;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
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
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class rejectsOrGiveUpOwnershipUnitTests {
    private UserFacade userFacade;
    private String username1;
    private String username2;
    private String username3;
    private UserRepository userRepository;
    private StoreRepository storeRepository;

    @BeforeEach
    public void setUp() {
        storeRepository= StoreMemoryRepository.getInstance();
        userRepository = UserMemoryRepository.getInstance();
        userFacade = UserFacadeImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class), mock(NotificationSender.class),userRepository,storeRepository);
        username1 = "testuser1";
        username2 = "testuser2";
        username3 = "testuser3";
        try {
            userFacade.register(username1, username1, LocalDate.now());
            userFacade.register(username2, username2, LocalDate.now());
            userFacade.register(username3, username3, LocalDate.now());
            userFacade.enter(0);
            userFacade.enter(1);
            userFacade.enter(2);
            userFacade.login("v0", username1, username1);
            userFacade.login("v1", username2, username2);
            userFacade.login("v2", username3, username3);
            userFacade.createStore("r" + username1, "Adidas", "");
            userFacade.createStore("r" + username2, "Nike", "");
        } catch (Exception e) {
        }
    }

    @AfterEach
    public void tearDown() {
        userFacade.logout(0, "r" + username1);
        userFacade.logout(1, "r" + username2);
        userFacade.logout(2, "r" + username3);
        try {
            userFacade.exit("v0");
            userFacade.exit("v1");
            userFacade.exit("v2");
            userFacade.deleteInstance();
        } catch (Exception e) {
        }
    }

    //RejectToManage

    @Test
    void rejectToManageStore_Success() {
        try {
            userFacade.suggestManager("r" + username1, "r" + username2, "Adidas", true, false, true, false);
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username2).getManagerSuggestions().size();
        assertDoesNotThrow(() -> userFacade.rejectToManageStore("r" + username2, "Adidas", "r" + username1), "rejectToManageStore should not throw any exceptions");
        int sizeA = userFacade.getUser("r" + username2).getManagerSuggestions().size();
        assertEquals(sizeB, sizeA + 1);
    }

    @Test
    void rejectToManageStore_StoreNotExist() {
        try {
            userFacade.suggestManager("r" + username1, "r" + username2, "Adidas", true, false, true, false);
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username2).getManagerSuggestions().size();
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> userFacade.rejectToManageStore("r" + username2, "Adidas1", "r" + username1));
        assertEquals("No store called Adidas1 exist", exception.getMessage());
        int sizeA = userFacade.getUser("r" + username2).getManagerSuggestions().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void rejectToManageStore_UserToOwnerExist() {
        try {
            userFacade.suggestManager("r" + username1, "r" + username2, "Adidas", true, false, true, false);
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username2).getManagerSuggestions().size();
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> userFacade.rejectToManageStore("r" + "username2", "Adidas", "r" + username1));
        assertEquals("No user called rusername2 exist", exception.getMessage());
        int sizeA = userFacade.getUser("r" + username2).getManagerSuggestions().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void rejectToManageStore_newManagerIsSuspended() {
        try {
            userFacade.suggestManager("r" + username3, "r" + username2, "Nike", true, false, true, false);
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username3).getManagerSuggestions().size();
        userFacade.suspendUser("r" + username1, "r" + username3, LocalDateTime.of(2025, 1, 1, 1, 1));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userFacade.rejectToManageStore("r" + username3, "Nike", "r" + username2));
        assertEquals("User is suspended from the system", exception.getMessage());
        userFacade.endSuspendUser("r" + username1, "r" + username3);
        int sizeA = userFacade.getUser("r" + username3).getManagerSuggestions().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void rejectToManageStore_UserAppointNotOwner() {
        try {
            userFacade.suggestManager("r" + username1, "r" + username2, "Adidas", true, false, true, false);
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username3).getManagerSuggestions().size();
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.rejectToManageStore("r" + username3, "Adidas", "r" + username2));
        assertEquals("User must be Owner", exception.getMessage());
        int sizeA = userFacade.getUser("r" + username3).getManagerSuggestions().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void rejectToManageStore_UserToManageNotLogged() {
        try {
            userFacade.suggestManager("r" + username1, "r" + username2, "Adidas", true, false, true, false);
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username2).getManagerSuggestions().size();
        userFacade.logout(1, "r" + username2);
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.rejectToManageStore("r" + username2, "Adidas", "r" + username1));
        assertEquals("New Manager user is not logged", exception.getMessage());
        userFacade.login("v1", username2, username2);
        int sizeA = userFacade.getUser("r" + username2).getManagerSuggestions().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void rejectToManageStore_UserNotForStoreList() {
        int sizeB = userFacade.getUser("r" + username3).getManagerSuggestions().size();
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.rejectToManageStore("r" + username3, "Nike", "r" + username2));
        assertEquals("No one suggest this user to be a manager", exception.getMessage());
        int sizeA = userFacade.getUser("r" + username3).getManagerSuggestions().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void rejectToManageStore_UserAppointIsManager() {
        try {
            userFacade.suggestManager("r" + username2, "r" + username3, "Nike", true, false, true, false);
            userFacade.getUser("r" + username3).addManagerRole("r" + username2, "Nike");
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username2).getManagerSuggestions().size();
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.rejectToManageStore("r" + username3, "Nike", "r" + username2));
        assertEquals("User already Manager of this store", exception.getMessage());
        int sizeA = userFacade.getUser("r" + username2).getManagerSuggestions().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void rejectToManageStore_isOwner() {
        try {
            userFacade.suggestManager("r" + username1, "r" + username2, "Adidas", true, false, true, false);
        } catch (Exception e) {
        }
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.rejectToManageStore("r" + username1, "Adidas", "r" + username1));
        assertEquals("User cannot be owner of this store", exception.getMessage());
    }

    //RejectToOwnStore

    @Test
    void rejectToOwnStore_Success() {
        try {
            userFacade.suggestOwner("r" + username1, "r" + username2, "Adidas");
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username2).getOwnerSuggestions().size();
        assertDoesNotThrow(() -> userFacade.rejectToOwnStore("r" + username2, "Adidas", "r" + username1), "rejectToManageStore should not throw any exceptions");
        int sizeA = userFacade.getUser("r" + username2).getOwnerSuggestions().size();
        assertEquals(sizeB, sizeA + 1);
    }

    @Test
    void rejectToOwnStore_StoreNotExist() {
        try {
            userFacade.suggestOwner("r" + username1, "r" + username2, "Adidas");
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username2).getOwnerSuggestions().size();
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> userFacade.rejectToOwnStore("r" + username2, "Adidas1", "r" + username1));
        assertEquals("No store called Adidas1 exist", exception.getMessage());
        int sizeA = userFacade.getUser("r" + username2).getOwnerSuggestions().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void rejectToOwnStore_UserToOwnerNotExist() {
        try {
            userFacade.suggestOwner("r" + username1, "r" + username2, "Adidas");
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username2).getOwnerSuggestions().size();
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> userFacade.rejectToOwnStore("r" + "username2", "Adidas", "r" + username1));
        assertEquals("No user called rusername2 exist", exception.getMessage());
        int sizeA = userFacade.getUser("r" + username2).getOwnerSuggestions().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void rejectToOwnStore_newManagerIsSuspended() {
        try {
            userFacade.suggestOwner("r" + username3, "r" + username2, "Nike");
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username3).getOwnerSuggestions().size();
        userFacade.suspendUser("r" + username1, "r" + username3, LocalDateTime.of(2025, 1, 1, 1, 1));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userFacade.rejectToOwnStore("r" + username3, "Nike", "r" + username2));
        assertEquals("User is suspended from the system", exception.getMessage());
        userFacade.endSuspendUser("r" + username1, "r" + username3);
        int sizeA = userFacade.getUser("r" + username3).getOwnerSuggestions().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void rejectToOwnStore_UserAppointNotOwner() {
        try {
            userFacade.suggestManager("r" + username1, "r" + username2, "Adidas", true, false, true, false);
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username3).getOwnerSuggestions().size();
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.rejectToOwnStore("r" + username3, "Adidas", "r" + username2));
        assertEquals("User must be Owner", exception.getMessage());
        int sizeA = userFacade.getUser("r" + username3).getOwnerSuggestions().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void rejectToOwnStore_UserNotForStoreList() {
        int sizeB = userFacade.getUser("r" + username3).getOwnerSuggestions().size();
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.rejectToOwnStore("r" + username3, "Nike", "r" + username2));
        assertEquals("No one suggest this user to be a owner", exception.getMessage());
        int sizeA = userFacade.getUser("r" + username3).getOwnerSuggestions().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void rejectToOwnStore_UserToManageNotLogged() {
        try {
            userFacade.suggestOwner("r" + username1, "r" + username2, "Adidas");
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username2).getOwnerSuggestions().size();
        userFacade.logout(1, "r" + username2);
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.rejectToOwnStore("r" + username2, "Adidas", "r" + username1));
        assertEquals("New owner user is not logged", exception.getMessage());
        userFacade.login("v1", username2, username2);
        int sizeA = userFacade.getUser("r" + username2).getOwnerSuggestions().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void rejectToOwnStore_UserAppointIsOwner() {
        try {
            userFacade.suggestOwner("r" + username1, "r" + username2, "Adidas");
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username2).getOwnerSuggestions().size();
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.rejectToManageStore("r" + username1, "Nike", "r" + username1));
        assertEquals("User must be Owner", exception.getMessage());
        int sizeA = userFacade.getUser("r" + username2).getOwnerSuggestions().size();
        assertEquals(sizeB, sizeA);
    }

    //GiveUpOwnership

    @Test
    void waiverOnOwnership_Success() {
        userFacade.getUser("r" + username2).addOwnerRole("r" + username1, "Adidas");    //For tests only!
        boolean isOwnerB = userFacade.getUser("r" + username2).isOwner("Adidas");
        assertDoesNotThrow(() -> userFacade.waiverOnOwnership("r" + username2, "Adidas"), "waiverOnOwnership should not throw any exceptions");
        boolean isOwnerA = userFacade.getUser("r" + username2).isOwner("Adidas");
        assertEquals(isOwnerB, !isOwnerA);
    }

    @Test
    void waiverOnOwnership_StoreNotExist() {
        userFacade.getUser("r" + username2).addOwnerRole("r" + username1, "Adidas");    //For tests only!
        boolean isOwnerB = userFacade.getUser("r" + username2).isOwner("Adidas");
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> userFacade.waiverOnOwnership("r" + username2, "Adidas1"));
        assertEquals("No store called Adidas1 exist", exception.getMessage());
        boolean isOwnerA = userFacade.getUser("r" + username2).isOwner("Adidas");
        assertEquals(isOwnerB, isOwnerA);
    }

    @Test
    void waiverOnOwnership_UserNotExist() {
        userFacade.getUser("r" + username2).addOwnerRole("r" + username1, "Adidas");    //For tests only!
        boolean isOwnerB = userFacade.getUser("r" + username2).isOwner("Adidas");
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> userFacade.waiverOnOwnership("r" + "username2", "Adidas"));
        assertEquals("No user called rusername2 exist", exception.getMessage());
        boolean isOwnerA = userFacade.getUser("r" + username2).isOwner("Adidas");
        assertEquals(isOwnerB, isOwnerA);
    }

    @Test
    void waiverOnOwnership_newManagerIsSuspended() {
        userFacade.getUser("r" + username2).addOwnerRole("r" + username1, "Adidas");    //For tests only!
        boolean isOwnerB = userFacade.getUser("r" + username2).isOwner("Adidas");
        userFacade.suspendUser("r" + username1, "r" + username2, LocalDateTime.of(2025, 1, 1, 1, 1));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userFacade.waiverOnOwnership("r" + username2, "Adidas"));
        assertEquals("User is suspended from the system", exception.getMessage());
        userFacade.endSuspendUser("r" + username1, "r" + username2);
        boolean isOwnerA = userFacade.getUser("r" + username2).isOwner("Adidas");
        assertEquals(isOwnerB, isOwnerA);
    }

    @Test
    void waiverOnOwnership_UserNotOwner() {
        boolean isOwnerB = userFacade.getUser("r" + username2).isOwner("Adidas");
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.waiverOnOwnership("r" + username2, "Adidas"));
        assertEquals("User is not owner of this store", exception.getMessage());
        boolean isOwnerA = userFacade.getUser("r" + username2).isOwner("Adidas");
        assertEquals(isOwnerB, isOwnerA);
    }

    @Test
    void waiverOnOwnership_IsFounder() {
        boolean isOwnerB = userFacade.getUser("r" + username1).isOwner("Adidas");
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.waiverOnOwnership("r" + username1, "Adidas"));
        assertEquals("Founder cant waive on ownership", exception.getMessage());
        boolean isOwnerA = userFacade.getUser("r" + username1).isOwner("Adidas");
        assertEquals(isOwnerB, isOwnerA);
    }
}