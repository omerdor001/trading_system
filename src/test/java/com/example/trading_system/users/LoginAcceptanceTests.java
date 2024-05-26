package com.example.trading_system.users;

import com.example.trading_system.service.Facade;
import com.example.trading_system.service.Security;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;

class LoginAcceptanceTests {

    String token1;
    private Facade facade;

    @BeforeEach
    void setUp() throws Exception {
        facade = new Facade();
        token1 = facade.enter();
        facade.register(0, "testuser", "password123", LocalDate.now());
    }

    @Test
    void login_Success() {
        int userId = 0;
        String username = "testuser";
        String password = "password123";
        String token = facade.login(token1, userId, username, password);
        assertTrue(!token.isEmpty(), "Token should not be empty");
    }

    @Test
    void login_Wrong_Password() {
        int userId = 0;
        String username = "testuser";
        String password = "wrongPassword";
        String token = facade.login(token1, userId, username, password);
        assertTrue(token.isEmpty(), "Token should be empty");
    }

    @Test
    void login_Wrong_Username() {
        int userId = 0;
        String username = "wronguser";
        String password = "password123";
        String token = facade.login(token1, userId, username, password);
        assertTrue(token.isEmpty(), "Token should be empty");
    }

    @Test
    void login_User_Logged_In() {
        int userId = 0;
        String username = "testuser";
        String password = "password123";
        String token = facade.login(token1, userId, username, password);
        token = facade.login(token, userId, username, password);
        assertTrue(token.isEmpty(), "Token should be empty");
    }
}
