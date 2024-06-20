package com.example.trading_system.AcceptanceTests.Services;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.service.TradingSystemImp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class OpenSystemAcceptanceTests {
    private TradingSystemImp tradingSystem;

    @BeforeEach
    void setUp() {
        tradingSystem = TradingSystemImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class), mock(NotificationSender.class));
    }

    @AfterEach
    void setDown(){
        tradingSystem.deleteInstance();
    }

    @Test
    void openSystem_Success() {
        String username = "admin";
        String password = "adminPass";
        LocalDate birthdate = LocalDate.of(1980, 1, 1);
        tradingSystem.register(username, password, birthdate);
        ResponseEntity<String> response = tradingSystem.openSystem();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("System opened successfully.", response.getBody());
    }

    @Test
    void openSystem_NoAdmin() {
        ResponseEntity<String> response = tradingSystem.openSystem();
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("System cannot be opened without at least one admin registered.", response.getBody());
    }

    @Test
    void openSystem_AlreadyOpen() {
        String username = "admin";
        String password = "adminPass";
        LocalDate birthdate = LocalDate.of(1980, 1, 1);
        tradingSystem.register(username, password, birthdate);
        tradingSystem.openSystem();
        ResponseEntity<String> response = tradingSystem.openSystem();
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("System is already open.", response.getBody());
    }
}
