package com.example.trading_system.users;

import com.example.trading_system.service.TradingSystemImp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;

class LoginAcceptanceTests {

    String token1;
    private TradingSystemImp facade;

    @BeforeEach
    void setUp(){
        facade = TradingSystemImp.getInstance();
        facade.register(0, "testuser", "password123", LocalDate.now());
        facade.openSystem();
        ResponseEntity<String> response = facade.enter();
        token1 = response.getBody();
    }

    @AfterEach
    void setDown(){
        facade.logout(0, "testuser");
    }

    @Test
    void login_Success() {
        int userId = 0;
        String username = "testuser";
        String password = "password123";
        ResponseEntity<String> response = facade.login(token1, userId, username, password);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() != null && !response.getBody().isEmpty(), "Token should not be empty");
    }

    @Test
    void login_Wrong_Password() {
        int userId = 0;
        String username = "testuser";
        String password = "wrongPassword";
        ResponseEntity<String> response = facade.login(token1, userId, username, password);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void login_Wrong_Username() {
        int userId = 0;
        String username = "wronguser";
        String password = "password123";
        ResponseEntity<String> response = facade.login(token1, userId, username, password);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void login_User_Logged_In() {
        int userId = 0;
        String username = "testuser";
        String password = "password123";
        ResponseEntity<String> response1 = facade.login(token1, userId, username, password);
        ResponseEntity<String> response2 = facade.login(response1.getBody(), userId, username, password);

        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }
}
