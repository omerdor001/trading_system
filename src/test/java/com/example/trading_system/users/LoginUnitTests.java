package com.example.trading_system.users;

import com.example.trading_system.domain.users.UserFacade;
import com.example.trading_system.service.Security;
import com.example.trading_system.service.UserServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginUnitTests {

    private UserFacade userFacade;
    private UserServiceImp userService;

    @BeforeEach
    void setUp() {
        userFacade = Mockito.mock(UserFacade.class);
        userService = new UserServiceImp(userFacade);
    }

    @Test
    void login_Success() {
        // Given
        String username = "testvisitor";
        String password = "password123";
        int visitorId = 1;

        // Mocking static method Security.checkPassword
        try (MockedStatic<Security> mockedSecurity = mockStatic(Security.class)) {
            mockedSecurity.when(() -> Security.checkPassword(password, null)).thenReturn(true);

            // When
            boolean loginResult = userService.login(visitorId, username, password);

            // Then
            assertTrue(loginResult, "Login should be successful");
            verify(userFacade, times(1)).login(username);
            verify(userFacade, times(1)).removeVisitor(visitorId);
        }
    }

    @Test
    void login_Wrong_User() {
        // Given
        String username = "nonExistingUser";
        String password = "password123";
        when(userFacade.getUserPassword(username)).thenThrow(new RuntimeException("No such registered user " + username));

        // When
        boolean loginResult = userService.login(1, username, password);

        // Then
        assertFalse(loginResult, "Login should fail for non-existing username");
        verify(userFacade, times(1)).getUserPassword(username);
        verify(userFacade, never()).login(username);
    }

    @Test
    void login_Wrong_Password() {
        // Given
        String username = "testuser";
        String password = "wrongPassword";
        String encryptedPassword = "encryptedPassword";

        // Mocking static method Security.checkPassword
        try (MockedStatic<Security> mockedSecurity = mockStatic(Security.class)) {
            mockedSecurity.when(() -> Security.checkPassword(password, encryptedPassword)).thenReturn(false);

            // When
            boolean loginResult = userService.login(1, username, password);

            // Then
            assertFalse(loginResult, "Login should fail for wrong password");
            verify(userFacade, times(1)).getUserPassword(username);
            verify(userFacade, never()).login(username);
        }
    }
}
