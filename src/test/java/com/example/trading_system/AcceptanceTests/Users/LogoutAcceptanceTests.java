//package com.example.trading_system.AcceptanceTests.Users;
//
//import com.example.trading_system.service.TradingSystemImp;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.time.LocalDate;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//class LogoutAcceptanceTests {
//
//    String token1;
//    private TradingSystemImp facade;
//
//    @BeforeEach
//    void setUp(){
//        facade = TradingSystemImp.getInstance();
//        facade.register(0, "testuser", "password123", LocalDate.now());
//        facade.openSystem();
//        ResponseEntity<String> response = facade.enter();
//        token1 = response.getBody();
//        facade.login(token1, 0, "testuser", "password123");
//    }
//
//    @AfterEach
//    void setDown(){
//        facade.logout(0, "testuser");
//    }
//
//    @Test
//    void logout_Success() {
//        int userId = 0;
//        String username = "testuser";
//        ResponseEntity<String> response = facade.logout(userId, username);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Logout successful.", response.getBody(), "Logout message should be 'Logout successful.'");
//    }
//
//    @Test
//    void logout_User_Not_Logged_In() {
//        int userId = 0;
//        String username = "nonexistentuser";
//        ResponseEntity<String> response = facade.logout(userId, username);
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Expected an internal server error for a non-logged-in user.");
//    }
//}