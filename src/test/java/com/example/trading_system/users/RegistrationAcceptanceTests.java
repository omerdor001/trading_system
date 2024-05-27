package com.example.trading_system.users;

import com.example.trading_system.service.TradingSystem;
import com.example.trading_system.service.TradingSystemImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationAcceptanceTests {
    private TradingSystem tradingSystem;


    @BeforeEach
    void setUp() {
        tradingSystem = TradingSystemImp.getInstance();
        // Open the system before each test
        tradingSystem.openSystem();
    }

    @Test
    void registration_Successful() {
        int id = 1;
        String username = "TestUser";
        String password = "TestPassword";
        LocalDate birthdate = LocalDate.of(1990, 5, 15);

        ResponseEntity<String> response = tradingSystem.register(id, username, password, birthdate);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User registered successfully.", response.getBody());
    }

    @Test
    void registration_Failure() {
        int id = 2;
        String username = "ExistingUser";
        String password = "ExistingPassword";
        LocalDate birthdate = LocalDate.of(1995, 10, 20);

        tradingSystem.register(id, username, password, birthdate);
        ResponseEntity<String> response = tradingSystem.register(id, username, password, birthdate);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("username already exists - ExistingUser", response.getBody());
    }
}