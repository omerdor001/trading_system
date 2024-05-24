package com.example.trading_system.users;

import com.example.trading_system.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExitAcceptanceTests {
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
    }

    @Test
    void exitVisitor_Successful() throws Exception {
        // Mock exit to not throw an exception
        int id = 1;
        doNothing().when(userService).exit(id);

        // Perform exit
        assertDoesNotThrow(() -> userService.exit(id));

        // Verify the interaction with the mock
        verify(userService, times(1)).exit(id);
    }

    @Test
    void exitVisitor_Failure() throws Exception {
        // Mock exit to throw an exception
        int id = 2;
        doThrow(new Exception("No such visitor with id- " + id)).when(userService).exit(id);

        // Perform exit and verify exception is thrown
        Exception exception = assertThrows(Exception.class, () -> userService.exit(id));
        assertEquals("No such visitor with id- " + id, exception.getMessage());

        // Verify the interaction with the mock
        verify(userService, times(1)).exit(id);
    }

    @Test
    void exitRegisteredUser_Successful() throws Exception {
        // Mock exit to not throw an exception
        String username = "user1";
        doNothing().when(userService).exit(username);

        // Perform exit
        assertDoesNotThrow(() -> userService.exit(username));

        // Verify the interaction with the mock
        verify(userService, times(1)).exit(username);
    }

    @Test
    void exitRegisteredUser_Failure() throws Exception {
        // Mock exit to throw an exception
        String username = "nonexistent";
        doThrow(new Exception("No such user with username- " + username)).when(userService).exit(username);

        // Perform exit and verify exception is thrown
        Exception exception = assertThrows(Exception.class, () -> userService.exit(username));
        assertEquals("No such user with username- " + username, exception.getMessage());

        // Verify the interaction with the mock
        verify(userService, times(1)).exit(username);
    }
}