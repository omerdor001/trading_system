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
import static org.mockito.Mockito.mock;


class AppointmentManagerUnitTests {
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

    //SuggestManage

    @Test
    void suggestManager_Success() {
        int sizeB = userFacade.getUser("r" + username2).getManagerToApprove().size();
        assertDoesNotThrow(() -> userFacade.suggestManager("r" + username1, "r" + username2, "Adidas", true, false, true, false, true, true), "suggestManage should not throw any exceptions");
        int sizeA = userFacade.getUser("r" + username2).getManagerToApprove().size();
        assertEquals(sizeB, sizeA - 1);
    }

    @Test
    void suggestManager_StoreNotExist() {
        int sizeB = userFacade.getUser("r" + username2).getManagerToApprove().size();
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> userFacade.suggestManager("r" + username1, "r" + username2, "Adidas1", true, false, true, false, true, true));
        assertEquals("No store called Adidas1 exist", exception.getMessage());
        int sizeA = userFacade.getUser("r" + username2).getManagerSuggestions().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void suggestManager_UserToOwnerExist() {
        int sizeB = userFacade.getUser("r" + username2).getManagerToApprove().size();
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> userFacade.suggestManager("r" + username1, "r" + "username2", "Adidas", true, false, true, false, true, true));
        assertEquals("No user called rusername2 exist", exception.getMessage());
        int sizeA = userFacade.getUser("r" + username2).getManagerSuggestions().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void suggestManager_AppointIsSuspended() {
        int sizeB = userFacade.getUser("r" + username3).getManagerSuggestions().size();
        userFacade.suspendUser("r" + username1, "r" + username2, LocalDateTime.of(2025, 1, 1, 1, 1));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userFacade.suggestManager("r" + username2, "r" + username3, "Nike", true, false, true, false, true, true));
        assertEquals("User is suspended from the system", exception.getMessage());
        userFacade.endSuspendUser("r" + username1, "r" + username2);
        int sizeA = userFacade.getUser("r" + username3).getManagerSuggestions().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void suggestManager_UserAppointNotOwner() {
        int sizeB = userFacade.getUser("r" + username3).getManagerToApprove().size();
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.suggestManager("r" + username2, "r" + username3, "Adidas", true, false, true, false, true, true));
        assertEquals("Appoint user must be Owner", exception.getMessage());
        int sizeA = userFacade.getUser("r" + username3).getManagerSuggestions().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void suggestManager_UserAppointNotLogged() {
        int sizeB = userFacade.getUser("r" + username2).getManagerSuggestions().size();
        userFacade.logout(0, "r" + username1);
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.suggestManager("r" + username1, "r" + username2, "Adidas", true, false, true, false, true, true));
        assertEquals("Appoint user is not logged", exception.getMessage());
        userFacade.login("v0", username1, username1);
        int sizeA = userFacade.getUser("r" + username2).getManagerSuggestions().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void suggestManager_AlreadyManager() {
        int sizeB = userFacade.getUser("r" + username3).getManagerSuggestions().size();
        userFacade.getUser("r" + username3).addManagerRole("r" + username2, "Nike");    //For tests only!
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.suggestManager("r" + username2, "r" + username3, "Nike", true, false, true, false, true, true));
        assertEquals("User already Manager of this store", exception.getMessage());
        userFacade.getUser("r" + username2).removeManagerRole("Adidas");     //For tests only!
        int sizeA = userFacade.getUser("r" + username3).getManagerSuggestions().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void suggestManager_isOwner() {
        int sizeB = userFacade.getUser("r" + username2).getManagerToApprove().size();
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.suggestManager("r" + username1, "r" + username1, "Adidas", true, false, true, false, true, true));
        assertEquals("User is already owner of this store", exception.getMessage());
        int sizeA = userFacade.getUser("r" + username2).getManagerToApprove().size();
        assertEquals(sizeB, sizeA);
    }

    //ApproveManager

    @Test
    void approveManager_Success() {
        try {
            userFacade.suggestManager("r" + username1, "r" + username2, "Adidas", true, false, true, false, true, true);
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username2).getManagerToApprove().size();
        assertDoesNotThrow(() -> userFacade.approveManager("r" + username2, "Adidas", "r" + username1, true, false, true, false, true, true), "approveManage should not throw any exceptions");
        int sizeA = userFacade.getUser("r" + username2).getManagerToApprove().size();
        assertEquals(sizeB, sizeA + 1);
    }

    @Test
    void approveManager_StoreNotExist() {
        try {
            userFacade.suggestManager("r" + username1, "r" + username2, "Adidas", true, false, true, false, true, true);
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username2).getManagerToApprove().size();
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> userFacade.approveManager("r" + username2, "Adidas1", "r" + username1, true, false, true, false, true, true));
        assertEquals("No store called Adidas1 exist", exception.getMessage());
        int sizeA = userFacade.getUser("r" + username2).getManagerSuggestions().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void approveManager_UserToOwnerExist() {
        try {
            userFacade.suggestManager("r" + username1, "r" + username2, "Adidas", true, false, true, false, true, true);
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username2).getManagerToApprove().size();
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> userFacade.approveManager("r" + "username2", "Adidas", "r" + username1, true, false, true, false, true, true));
        assertEquals("No user called rusername2 exist", exception.getMessage());
        int sizeA = userFacade.getUser("r" + username2).getManagerSuggestions().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void approveManager_newManagerIsSuspended() {
        try {
            userFacade.suggestManager("r" + username3, "r" + username2, "Nike", true, false, true, false, true, true);
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username3).getManagerSuggestions().size();
        userFacade.suspendUser("r" + username1, "r" + username3, LocalDateTime.of(2025, 1, 1, 1, 1));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userFacade.approveManager("r" + username3, "Nike", "r" + username2,true, false, true, false, true, true));
        assertEquals("User is suspended from the system", exception.getMessage());
        userFacade.endSuspendUser("r" + username1, "r" + username3);
        int sizeA = userFacade.getUser("r" + username3).getManagerSuggestions().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void approveManager_UserAppointNotOwner() {
        try {
            userFacade.suggestManager("r" + username1, "r" + username2, "Adidas", true, false, true, false, true, true);
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username3).getManagerToApprove().size();
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.approveManager("r" + username3, "Adidas", "r" + username2, true, false, true, false, true, true));
        assertEquals("User must be Owner", exception.getMessage());
        int sizeA = userFacade.getUser("r" + username3).getManagerSuggestions().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void approveManager_UserToManageNotLogged() {
        try {
            userFacade.suggestManager("r" + username1, "r" + username2, "Adidas", true, false, true, false, true, true);
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username2).getManagerSuggestions().size();
        userFacade.logout(1, "r" + username2);
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.approveManager("r" + username2, "Adidas", "r" + username1, true, false, true, false, true, true));
        assertEquals("New Manager user is not logged", exception.getMessage());
        userFacade.login("v1", username2, username2);
        int sizeA = userFacade.getUser("r" + username2).getManagerSuggestions().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void approveOwner_UserAppointIsManager() {
        try {
            userFacade.suggestManager("r" + username2, "r" + username3, "Nike", true, false, true, false, true, true);
            userFacade.getUser("r" + username3).addManagerRole("r" + username2, "Nike");
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username2).getManagerToApprove().size();
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.approveManager("r" + username3, "Nike", "r" + username2, true, false, true, false, true, true));
        assertEquals("User already Manager of this store", exception.getMessage());
        int sizeA = userFacade.getUser("r" + username2).getManagerSuggestions().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void approveManager_isOwner() {
        try {
            userFacade.suggestManager("r" + username1, "r" + username2, "Adidas", true, false, true, false, true, true);
        } catch (Exception e) {
        }
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.approveManager("r" + username1, "Adidas", "r" + username1, true, false, true, false, true, true));
        assertEquals("User is already owner of this store", exception.getMessage());
    }
}