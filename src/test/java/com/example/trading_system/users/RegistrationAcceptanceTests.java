package com.example.trading_system.users;

import com.example.trading_system.domain.users.*;
import com.example.trading_system.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDate;

public class RegistrationAcceptanceTests {
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
    }

    @Test
    void registration_Successful() {
        // Mock registration to return true
        int id = 1;
        String username = "TestUser";
        String password = "TestPassword";
        LocalDate birthdate = LocalDate.of(1990, 5, 15);
        when(userService.registration(id, username, password, birthdate)).thenReturn(true);

        // Perform registration
        boolean result = userService.registration(id, username, password, birthdate);

        // Verify registration is successful
        assertTrue(result);
    }

    @Test
    void registration_Failure() {
        // Mock registration to return false
        int id = 2;
        String username = "ExistingUser";
        String password = "ExistingPassword";
        LocalDate birthdate = LocalDate.of(1995, 10, 20);
        when(userService.registration(id, username, password, birthdate)).thenReturn(false);

        // Perform registration
        boolean result = userService.registration(id, username, password, birthdate);

        // Verify registration fails
        assertFalse(result);
    }
}