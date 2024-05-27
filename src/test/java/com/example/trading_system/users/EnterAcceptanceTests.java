package com.example.trading_system.users;

import com.example.trading_system.service.TradingSystem;
import com.example.trading_system.service.TradingSystemImp;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnterAcceptanceTests {

    private static TradingSystem tradingSystem;
    private String adminToken;

    @BeforeAll
    public static void setup() {
        tradingSystem = TradingSystemImp.getInstance();
    }

    @BeforeEach
    public void openSystemAndRegisterAdmin() {
        tradingSystem.register(1, "admin", "adminPass", LocalDate.of(1990, 1, 1));
        tradingSystem.openSystem();
        ResponseEntity<String> response = tradingSystem.enter();
        adminToken = response.getBody();
    }

    @Test
    public void testEnterSuccessfully() {
        // Enter the system
        ResponseEntity<String> enterResponse = tradingSystem.enter();
        assertEquals(HttpStatus.OK, enterResponse.getStatusCode());
        String token = enterResponse.getBody();
        assertEquals(true, token.length() > 0);
    }

//    @Test
//    public void testEnterSystemClosed() {
//        // Close the system
//        tradingSystem.closeSystem();
//
//        // Attempt to enter the system
//        ResponseEntity<String> enterResponse = tradingSystem.enter();
//        assertEquals(HttpStatus.FORBIDDEN, enterResponse.getStatusCode());
//        assertEquals("", enterResponse.getBody());
//    }


    @Test
    public void testEnterWithExistingToken() {
        // Enter the system
        ResponseEntity<String> enterResponse = tradingSystem.enter();
        assertEquals(HttpStatus.OK, enterResponse.getStatusCode());
        String token = enterResponse.getBody();

        // Attempt to enter the system again with the existing token
        ResponseEntity<String> reEnterResponse = tradingSystem.enter();
        assertEquals(HttpStatus.OK, reEnterResponse.getStatusCode());
        String newToken = reEnterResponse.getBody();
        assertEquals(false, newToken.equals(token));
    }
}
