package com.example.trading_system.AcceptanceTests.Services;

import com.example.trading_system.service.TradingSystemImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OpenSystemAcceptanceTests {
    private TradingSystemImp facade;

    @BeforeEach
    void setUp() {
        facade = TradingSystemImp.getInstance();
    }
//TODO FIX ME
/*    @Test
    void openSystem_Success() {
        int userId = 0;
        String username = "admin";
        String password = "adminPass";
        LocalDate birthdate = LocalDate.of(1980, 1, 1);
        facade.register(userId, username, password, birthdate);
        ResponseEntity<String> response = facade.openSystem();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("System opened successfully.", response.getBody());
    }*/

    @Test
    void openSystem_NoAdmin() {
        ResponseEntity<String> response = facade.openSystem();

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("System cannot be opened without at least one admin registered.", response.getBody());
    }
}
