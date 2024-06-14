package com.example.trading_system.UnitTests.service;

import com.example.trading_system.service.TradingSystemImp;
import com.example.trading_system.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OpenSystemUnitTests {
    private TradingSystemImp facade;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = mock(UserService.class);
        facade = TradingSystemImp.getInstance();
        facade.userService = userService;
    }

    @Test
    void openSystem_Success() {
        when(userService.isAdminRegistered()).thenReturn(true);
        ResponseEntity<String> response = facade.openSystem();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("System opened successfully.", response.getBody());
        verify(userService, times(1)).isAdminRegistered();
    }

    @Test
    void openSystem_No_Admin() {
        when(userService.isAdminRegistered()).thenReturn(false);
        ResponseEntity<String> response = facade.openSystem();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("System is already open.", response.getBody());
//        verify(userService, times(1)).isAdminRegistered();
    }
}
