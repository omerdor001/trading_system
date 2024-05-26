package com.example.trading_system.users;
import com.example.trading_system.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class LogoutAcceptanceTests {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
    }

    // Existing test for failed logout
    @Test
    void logout_Failed_Descriptive() {
        int userId = 0;
        String username = "testuser";
        boolean result = userService.logout(userId, username);
        assertFalse(result);
    }

    // Test successful logout
    @Test
    void logout_Success() {
        int userId = 1;
        String username = "validuser";
        when(userService.logout(userId, username)).thenReturn(true);
        assertTrue(userService.logout(userId, username));
    }

    // Test logout with null username
    @Test
    void logout_NullUsername() {
        int userId = 1;
        assertFalse( userService.logout(userId, null));
    }

    // Test logout with non-existing user
    @Test
    void logout_NonExistingUser() {
        int userId = 1;
        String username = "nonexisting";
        when(userService.logout(userId, username)).thenReturn(false);
        assertFalse(userService.logout(userId, username));
    }

    // Test logout when user already logged out
    @Test
    void logout_AlreadyLoggedOut() {
        int userId = 1;
        String username = "alreadyLoggedOutUser";
        when(userService.logout(userId, username)).thenThrow(new RuntimeException("User already logged out"));
        Exception exception = assertThrows(RuntimeException.class, () -> userService.logout(userId, username));
        assertEquals("User already logged out", exception.getMessage());
    }
}
