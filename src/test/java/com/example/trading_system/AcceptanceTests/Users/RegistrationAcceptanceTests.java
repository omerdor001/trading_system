package com.example.trading_system.AcceptanceTests.Users;

import com.example.trading_system.TradingSystemApplication;
import com.example.trading_system.service.TradingSystem;
import com.example.trading_system.service.TradingSystemImp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TradingSystemApplication.class)
public class RegistrationAcceptanceTests {
    @Autowired
    private TradingSystem tradingSystem;

    @BeforeEach
    void setUp() {
        tradingSystem = TradingSystemImp.getInstance();
    }

    @AfterEach
    void setDown(){
        tradingSystem.deleteInstance();
    }

    @Test
    void registration_Successful() {
        ResponseEntity<String> response = tradingSystem.register("owner1", "password123", LocalDate.now());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully.", response.getBody());
    }

    @Test
    void registration_Failure_User_Exists() {
        tradingSystem.register("owner1", "password123", LocalDate.now());
        ResponseEntity<String> response = tradingSystem.register("owner1", "password123", LocalDate.now());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("username already exists - owner1", response.getBody());
    }
}