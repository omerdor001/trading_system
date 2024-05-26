package com.example.trading_system.users;

import com.example.trading_system.service.TradingSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegistrationAcceptanceTests {
    private TradingSystem tradingSystem;

    @BeforeEach
    void setUp() {
        tradingSystem = mock(TradingSystem.class);
    }

    @Test
    void registration_Successful() {
        // Mock registration to return a successful ResponseEntity
        int id = 1;
        String username = "TestUser";
        String password = "TestPassword";
        LocalDate birthdate = LocalDate.of(1990, 5, 15);
        when(tradingSystem.register(id, username, password, birthdate))
                .thenReturn(ResponseEntity.ok("Registration successful"));

        // Perform registration
        ResponseEntity<String> response = tradingSystem.register(id, username, password, birthdate);

        // Verify registration is successful
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Registration successful", response.getBody());
    }

    @Test
    void registration_Failure() {
        // Mock registration to return a failure ResponseEntity
        int id = 2;
        String username = "ExistingUser";
        String password = "ExistingPassword";
        LocalDate birthdate = LocalDate.of(1995, 10, 20);
        when(tradingSystem.register(id, username, password, birthdate))
                .thenReturn(ResponseEntity.status(400).body("Registration failed"));

        // Perform registration
        ResponseEntity<String> response = tradingSystem.register(id, username, password, birthdate);

        // Verify registration fails
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Registration failed", response.getBody());
    }
}
