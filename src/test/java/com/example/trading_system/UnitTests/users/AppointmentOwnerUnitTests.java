package com.example.trading_system.UnitTests.users;

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


class AppointmentOwnerUnitTests {
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

    //SuggestOwner

    @Test
    void suggestOwner_Success() {
        int sizeB = userFacade.getUser("r" + username2).getOwnerToApprove().size();
        assertDoesNotThrow(() -> userFacade.suggestOwner("r" + username1, "r" + username2, "Adidas"), "suggestOwner should not throw any exceptions");
        int sizeA = userFacade.getUser("r" + username2).getOwnerToApprove().size();
        assertEquals(sizeB, sizeA - 1);
    }

    @Test
    void suggestOwner_StoreNotExist() {
        int sizeB = userFacade.getUser("r" + username2).getOwnerToApprove().size();
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> userFacade.suggestOwner("r" + username1, "r" + username2, "Adidas1"));
        assertEquals("No store called Adidas1 exist", exception.getMessage());
        int sizeA = userFacade.getUser("r" + username2).getOwnerToApprove().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void suggestOwner_UserToOwnerExist() {
        int sizeB = userFacade.getUser("r" + username2).getOwnerToApprove().size();
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> userFacade.suggestOwner("r" + username1, "r" + "username2", "Adidas"));
        assertEquals("No user called rusername2 exist", exception.getMessage());
        int sizeA = userFacade.getUser("r" + username2).getOwnerToApprove().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void suggestOwner_AppointIsSuspended() {
        int sizeB = userFacade.getUser("r" + username2).getOwnerToApprove().size();
        userFacade.suspendUser("r" + username1, "r" + username2, LocalDateTime.of(2025, 1, 1, 1, 1));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userFacade.suggestOwner("r" + username2, "r" + username3, "Nike"));
        assertEquals("User is suspended from the system", exception.getMessage());
        userFacade.endSuspendUser("r" + username1, "r" + username2);
        int sizeA = userFacade.getUser("r" + username2).getOwnerToApprove().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void suggestOwner_UserAppointNotOwner() {
        int sizeB = userFacade.getUser("r" + username3).getOwnerToApprove().size();
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.suggestOwner("r" + username2, "r" + username3, "Adidas"));
        assertEquals("Appoint user must be Owner", exception.getMessage());
        int sizeA = userFacade.getUser("r" + username3).getOwnerToApprove().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void suggestOwner_UserAppointNotLogged() {
        int sizeB = userFacade.getUser("r" + username2).getOwnerToApprove().size();
        userFacade.logout(0, "r" + username1);
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.suggestOwner("r" + username1, "r" + username2, "Adidas"));
        assertEquals("Appoint user is not logged", exception.getMessage());
        userFacade.login("v0", username1, username1);
        int sizeA = userFacade.getUser("r" + username2).getOwnerToApprove().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void suggestOwner_UserAppointIsOwner() {
        int sizeB = userFacade.getUser("r" + username2).getOwnerToApprove().size();
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.suggestOwner("r" + username1, "r" + username1, "Adidas"));
        assertEquals("User already Owner of this store", exception.getMessage());
        int sizeA = userFacade.getUser("r" + username2).getOwnerToApprove().size();
        assertEquals(sizeB, sizeA);
    }

    //ApproveOwner

    @Test
    void approveOwner_Success() {
        try {
            userFacade.suggestOwner("r" + username1, "r" + username2, "Adidas");
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username2).getOwnerToApprove().size();
        assertDoesNotThrow(() -> userFacade.approveOwner("r" + username2, "Adidas", "r" + username1), "approveOwner should not throw any exceptions");
        int sizeA = userFacade.getUser("r" + username2).getOwnerToApprove().size();
        assertEquals(sizeB, sizeA + 1);
    }

    @Test
    void approveOwner_StoreNotExist() {
        try {
            userFacade.suggestOwner("r" + username1, "r" + username2, "Adidas");
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username2).getOwnerToApprove().size();
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> userFacade.approveOwner("r" + username2, "Adidas1", "r" + username1));
        assertEquals("No store called Adidas1 exist", exception.getMessage());
        int sizeA = userFacade.getUser("r" + username2).getOwnerToApprove().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void approveOwner_UserToOwnerExist() {
        try {
            userFacade.suggestOwner("r" + username1, "r" + username2, "Adidas");
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username2).getOwnerToApprove().size();
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> userFacade.approveOwner("r" + "username2", "Adidas", "r" + username1));
        assertEquals("No user called rusername2 exist", exception.getMessage());
        int sizeA = userFacade.getUser("r" + username2).getOwnerToApprove().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void approveOwner_newOwnerIsSuspended() {
        try {
            userFacade.suggestOwner("r" + username3, "r" + username2, "Nike");
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username3).getOwnerToApprove().size();
        userFacade.suspendUser("r" + username1, "r" + username3, LocalDateTime.of(2025, 1, 1, 1, 1));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userFacade.approveOwner("r" + username3, "Nike", "r" + username2));
        assertEquals("User is suspended from the system", exception.getMessage());
        userFacade.endSuspendUser("r" + username1, "r" + username3);
        int sizeA = userFacade.getUser("r" + username3).getOwnerToApprove().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void approveOwner_UserAppointNotOwner() {
        try {
            userFacade.suggestOwner("r" + username1, "r" + username2, "Adidas");
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username3).getOwnerToApprove().size();
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.approveOwner("r" + username3, "Adidas", "r" + username2));
        assertEquals("User must be Owner", exception.getMessage());
        int sizeA = userFacade.getUser("r" + username3).getOwnerToApprove().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void approveOwner_UserAppointNotLogged() {
        try {
            userFacade.suggestOwner("r" + username1, "r" + username2, "Adidas");
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username2).getOwnerToApprove().size();
        userFacade.logout(1, "r" + username2);
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.approveOwner("r" + username2, "Adidas", "r" + username1));
        assertEquals("New owner user is not logged", exception.getMessage());
        userFacade.login("v1", username2, username2);
        int sizeA = userFacade.getUser("r" + username2).getOwnerToApprove().size();
        assertEquals(sizeB, sizeA);
    }

    @Test
    void approveOwner_UserAppointIsOwner() {
        try {
            userFacade.suggestOwner("r" + username1, "r" + username2, "Adidas");
        } catch (Exception e) {
        }
        int sizeB = userFacade.getUser("r" + username2).getOwnerToApprove().size();
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.approveOwner("r" + username1, "Adidas", "r" + username1));
        assertEquals("User already Owner of this store", exception.getMessage());
        int sizeA = userFacade.getUser("r" + username2).getOwnerToApprove().size();
        assertEquals(sizeB, sizeA);
    }

    //AppointOwner

    @Test
    void appointOwner_Success() {
        try {
            userFacade.suggestOwner("r" + username1, "r" + username2, "Adidas");
            userFacade.approveOwner("r" + username2, "Adidas", "r" + username1);
        } catch (Exception e) {
        }
        boolean isOwnerB = userFacade.getUser("r" + username2).isOwner("Adidas");
        assertDoesNotThrow(() -> userFacade.appointOwner("r" + username1, "r" + username2, "Adidas"), "appointOwner should not throw any exceptions");
        boolean isOwnerA = userFacade.getUser("r" + username2).isOwner("Adidas");
        assertEquals(isOwnerB, !isOwnerA);
    }

    @Test
    void appointOwner_StoreNotExist() {
        try {
            userFacade.suggestOwner("r" + username1, "r" + username2, "Adidas");
            userFacade.approveOwner("r" + username2, "Adidas", "r" + username1);
        } catch (Exception e) {
        }
        boolean isOwnerB = userFacade.getUser("r" + username2).isOwner("Adidas");
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> userFacade.appointOwner("r" + username1, "r" + username2, "Adidas1"));
        assertEquals("No store called Adidas1 exist", exception.getMessage());
        boolean isOwnerA = userFacade.getUser("r" + username2).isOwner("Adidas");
        assertEquals(isOwnerB, isOwnerA);
    }

    @Test
    void appointOwner_UserToOwnerExist() {
        try {
            userFacade.suggestOwner("r" + username1, "r" + username2, "Adidas");
            userFacade.approveOwner("r" + username2, "Adidas", "r" + username1);
        } catch (Exception e) {
        }
        boolean isOwnerB = userFacade.getUser("r" + username2).isOwner("Adidas");
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> userFacade.appointOwner("r" + username1, "r" + "username2", "Adidas"));
        assertEquals("No user called rusername2 exist", exception.getMessage());
        boolean isOwnerA = userFacade.getUser("r" + username2).isOwner("Adidas");
        assertEquals(isOwnerB, isOwnerA);
    }

    @Test
    void appointOwner_AppointIsSuspended() {
        try {
            userFacade.suggestOwner("r" + username2, "r" + username3, "Nike");
            userFacade.approveOwner("r" + username3, "Nike", "r" + username2);
        } catch (Exception e) {
        }
        boolean isOwnerB = userFacade.getUser("r" + username3).isOwner("Adidas");
        userFacade.suspendUser("r" + username1, "r" + username2, LocalDateTime.of(2025, 1, 1, 1, 1));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userFacade.appointOwner("r" + username2, "r" + username3, "Nike"));
        assertEquals("User is suspended from the system", exception.getMessage());
        userFacade.endSuspendUser("r" + username1, "r" + username2);
        boolean isOwnerA = userFacade.getUser("r" + username3).isOwner("Adidas");
        assertEquals(isOwnerB, isOwnerA);
    }

    @Test
    void appointOwner_UserAppointNotOwner() {
        try {
            userFacade.suggestOwner("r" + username2, "r" + username3, "Nike");
            userFacade.approveOwner("r" + username3, "Nike", "r" + username2);
        } catch (Exception e) {
        }
        boolean isOwnerB = userFacade.getUser("r" + username3).isOwner("Nike");
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.appointOwner("r" + username2, "r" + username3, "Adidas"));
        assertEquals("Appoint user must be Owner", exception.getMessage());
        boolean isOwnerA = userFacade.getUser("r" + username3).isOwner("Nike");
        assertEquals(isOwnerB, isOwnerA);
    }

    @Test
    void appointOwner_UserAppointNotLogged() {
        try {
            userFacade.suggestOwner("r" + username1, "r" + username2, "Adidas");
            userFacade.approveOwner("r" + username2, "Adidas", "r" + username1);
        } catch (Exception e) {
        }
        boolean isOwnerB = userFacade.getUser("r" + username2).isOwner("Adidas");
        userFacade.logout(0, "r" + username1);
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.appointOwner("r" + username1, "r" + username2, "Adidas"));
        assertEquals("Appoint user is not logged", exception.getMessage());
        userFacade.login("v0", username1, username1);
        boolean isOwnerA = userFacade.getUser("r" + username2).isOwner("Adidas");
        assertEquals(isOwnerB, isOwnerA);
    }

    @Test
    void appointOwner_UserAppointIsOwner() {
        try {
            userFacade.suggestOwner("r" + username1, "r" + username2, "Adidas");
            userFacade.approveOwner("r" + username2, "Adidas", "r" + username1);
        } catch (Exception e) {
        }
        boolean isOwnerB = userFacade.getUser("r" + username2).isOwner("Adidas");
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> userFacade.appointOwner("r" + username1, "r" + username1, "Adidas"));
        assertEquals("User already Owner of this store", exception.getMessage());
        boolean isOwnerA = userFacade.getUser("r" + username2).isOwner("Adidas");
        assertEquals(isOwnerB, isOwnerA);
    }
}