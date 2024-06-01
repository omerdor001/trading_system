package com.example.trading_system.users;

import com.example.trading_system.domain.users.Registered;
import com.example.trading_system.domain.users.UserFacade;
import com.example.trading_system.domain.users.Visitor;
import com.example.trading_system.service.Security;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginUnitTests {

    private UserFacade userFacade;

    @BeforeEach
    void setUp() {
        userFacade = Mockito.mock(UserFacade.class);
    }

//    @Test
//    void login_Success() {
//        // Given
//        String username = "testvisitor";
//        String password = "password123";
//        String encryptedPassword = "encryptedPassword";
//
//        // Mocking userFacade behavior
//        when(userFacade.getUserPassword(username)).thenReturn(encryptedPassword);
//        when(Security.checkPassword(password, encryptedPassword)).thenReturn(true);
//
//        // When
//        userFacade.login(username);
//
//        verify(userFacade, times(1)).login(username);
//    }
//
//    @Test
//    void login_Wrong_User() {
//        // Given
//        String username = "nonExistingUser";
//
//        // Mocking userFacade behavior
//        when(userFacade.getUserPassword(username)).thenThrow(new RuntimeException("No such registered user " + username));
//
//        // When & Then
//        assertThrows(RuntimeException.class, () -> userFacade.login(username));
//        verify(userFacade, never()).login(username);
//    }
//
//    @Test
//    void login_Wrong_Password() {
//        // Given
//        String username = "testuser";
//        String password = "wrongPassword";
//        String encryptedPassword = "encryptedPassword";
//
//        // Mocking userFacade behavior
//        when(userFacade.getUserPassword(username)).thenReturn(encryptedPassword);
//        when(Security.checkPassword(password, encryptedPassword)).thenReturn(false);
//
//        userFacade.login(username);
//        verify(userFacade, never()).login(username);
//    }

    @Test
    void registeredLogin_Success() {
        String username = "testuser";
        Registered registered = new Registered(1, username, "encryptedPassword", LocalDate.now());
        registered.login();
        assertTrue(registered.getLogged(), "User should be logged in");
    }

    @Test
    void visitorLogin() {
        Visitor visitor = new Visitor(1);
        assertThrows(RuntimeException.class, visitor::login);
    }
}
