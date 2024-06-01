package com.example.trading_system.AcceptanceTests.Users;

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

public class ExitAcceptanceTests {

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
    public void testExitUserByIdSuccessfully() {
        // Register and enter a user
        tradingSystem.register(2, "user1", "password1", LocalDate.of(1995, 5, 5));
        ResponseEntity<String> enterResponse = tradingSystem.enter();
        String userToken = enterResponse.getBody();

        // Exit the user by ID
        ResponseEntity<String> exitResponse = tradingSystem.exit(userToken, 2);
        assertEquals(HttpStatus.OK, exitResponse.getStatusCode());
        assertEquals("User exited successfully.", exitResponse.getBody());
    }

    @Test
    public void testExitUserByUsernameSuccessfully() {
        // Register and enter a user
        tradingSystem.register(3, "user2", "password2", LocalDate.of(1996, 6, 6));
        ResponseEntity<String> enterResponse = tradingSystem.enter();
        String userToken = enterResponse.getBody();

        // Exit the user by username
        ResponseEntity<String> exitResponse = tradingSystem.exit(userToken, "user2");
        assertEquals(HttpStatus.OK, exitResponse.getStatusCode());
        assertEquals("User exited successfully.", exitResponse.getBody());
    }
//
//    @Test
//    public void testExitUserByIdSystemClosed() {
//        // Register and enter a user
//        tradingSystem.register(4, "user3", "password3", LocalDate.of(1997, 7, 7));
//        ResponseEntity<String> enterResponse = tradingSystem.enter();
//        String userToken = enterResponse.getBody();
//
//        // Close the system
//        tradingSystem.closeSystem();
//
//        // Attempt to exit the user by ID
//        ResponseEntity<String> exitResponse = tradingSystem.exit(userToken, 4);
//        assertEquals(HttpStatus.FORBIDDEN, exitResponse.getStatusCode());
//        assertEquals("System is not open. Only registration is allowed.", exitResponse.getBody());
//    }
//
//    @Test
//    public void testExitUserByUsernameSystemClosed() {
//        // Register and enter a user
//        tradingSystem.register(5, "user4", "password4", LocalDate.of(1998, 8, 8));
//        ResponseEntity<String> enterResponse = tradingSystem.enter();
//        String userToken = enterResponse.getBody();
//
//        // Close the system
//        tradingSystem.closeSystem();
//
//        // Attempt to exit the user by username
//        ResponseEntity<String> exitResponse = tradingSystem.exit(userToken, "user4");
//        assertEquals(HttpStatus.FORBIDDEN, exitResponse.getStatusCode());
//        assertEquals("System is not open. Only registration is allowed.", exitResponse.getBody());
//    }

    @Test
    public void testExitNonExistentUserById() {
        // Attempt to exit a non-existent user by ID
        ResponseEntity<String> exitResponse = tradingSystem.exit(adminToken, 999);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exitResponse.getStatusCode());
        assertEquals("No such visitor with id- 999", exitResponse.getBody());
    }

    @Test
    public void testExitNonExistentUserByUsername() {
        // Attempt to exit a non-existent user by username
        ResponseEntity<String> exitResponse = tradingSystem.exit(adminToken, "nonExistentUser");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exitResponse.getStatusCode());
        assertEquals("No such user with username- nonExistentUser", exitResponse.getBody());
    }
}
