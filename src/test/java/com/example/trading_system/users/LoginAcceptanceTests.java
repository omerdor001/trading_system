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

    private Facade facade;
    @BeforeEach
    void setUp() throws Exception {
        facade = new Facade();
// TODO wait for registration to work
        // Mocking static method Security.validateToken in the setup
        try (MockedStatic<Security> mockedSecurity = Mockito.mockStatic(Security.class)) {
            mockedSecurity.when(() -> Security.validateToken(anyString(), anyString())).thenReturn(true);
            String token = ""; //facade.enter();
            //facade.registration("", 1, "testuser", "password123", LocalDate.now());
        }
    }

    @Test
    void givenValidCredentials_whenLogin_thenUserLogged() {
//        // Given
//        int userId = -1;
//        String username = "testuser";
//        String password = "password123";
//        LocalDate birthdate = LocalDate.of(1990, 5, 15);
//
//        // When
//        facade.registration("valid_token", userId, username, password, birthdate);
//        String token = facade.login(userId, username, password);
//
//        // Then
//        assertTrue(!token.isEmpty(), "Token should not be empty");
//        // You can add more assertions here to verify the state after successful login
    }

    @Test
    void givenInvalidCredentials_whenLogin_thenUserNotLogged(){
//        // Given
//        int userId = -1;
//        String username = "testuser";
//        String password = "wrongPassword";
//        LocalDate birthdate = LocalDate.of(1990, 5, 15);
//
//        // When
//        facade.registration("valid_token", userId, username, "correctPassword", birthdate);
//        String token = facade.login(userId, username, password);
//
//        // Then
//        assertTrue(token.isEmpty(), "Token should be empty");
//        // You can add more assertions here to verify the state after unsuccessful login
    }
}
