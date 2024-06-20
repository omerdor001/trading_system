package com.example.trading_system.UnitTests.users;

import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.users.Registered;
import com.example.trading_system.domain.users.UserFacade;
import com.example.trading_system.domain.users.UserFacadeImp;
import com.example.trading_system.domain.users.Visitor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class LoginUnitTests {

    private UserFacade userFacade;

    @BeforeEach
    void setUp() {
        userFacade = UserFacadeImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class));
        try {
            userFacade.register("testvisitor", "password123", LocalDate.now());
        } catch (Exception e) {

        }
    }

    @AfterEach
    void tearDown() {
        userFacade.deleteInstance();
    }

    @Test
    void login_Success() {
        userFacade.enter(0);
        boolean isLoggedB = userFacade.getUser("rtestvisitor").getLogged();
        assertDoesNotThrow(() -> userFacade.login("v0", "testvisitor", "password123"), "Login should not throw any exceptions");
        boolean isLoggedA = userFacade.getUser("rtestvisitor").getLogged();
        assertEquals(isLoggedB, !isLoggedA);
    }

    @Test
    void login_Wrong_User() {
        userFacade.enter(0);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userFacade.login("v0", "nonExistingUser", "password123"));
        assertEquals(exception.getMessage(), "No such user nonExistingUser");
    }

    @Test
    void login_Wrong_Password() {
        userFacade.enter(0);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userFacade.login("v0", "testvisitor", "wrongPassword"));
        assertEquals(exception.getMessage(), "Wrong password");
    }

    @Test
    void registeredLogin_Success() {
        String username = "testuser";
        Registered registered = new Registered(username, "encryptedPassword", LocalDate.now());
        registered.login();
        assertTrue(registered.getLogged(), "User should be logged in");
    }

    @Test
    void visitorLogin() {
        Visitor visitor = new Visitor("testuser");
        assertThrows(RuntimeException.class, visitor::login);
    }
}
