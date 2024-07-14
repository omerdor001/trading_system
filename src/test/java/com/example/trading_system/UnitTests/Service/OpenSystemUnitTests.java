package com.example.trading_system.UnitTests.Service;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.StoreDatabaseRepository;
import com.example.trading_system.domain.stores.StoreMemoryRepository;
import com.example.trading_system.domain.stores.StoreRepository;
import com.example.trading_system.domain.users.UserDatabaseRepository;
import com.example.trading_system.domain.users.UserMemoryRepository;
import com.example.trading_system.domain.users.UserRepository;
import com.example.trading_system.service.TradingSystemImp;
import com.example.trading_system.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
@SpringBootTest
@Transactional
class OpenSystemUnitTests {
    private TradingSystemImp facade;
    private UserService userService;
    @Autowired
    private UserDatabaseRepository userRepository;

    @Autowired
    private StoreDatabaseRepository storeRepository;
    @BeforeEach
    public void setUp() {
        userService = mock(UserService.class);
        facade = TradingSystemImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class), mock(NotificationSender.class), userRepository,storeRepository);
        facade.userService = userService;
    }

    @AfterEach
    void setDown(){
        facade.setSystemOpen(false);
        facade.deleteInstance();
    }

    @Test
    void openSystem_Success() {
        when(userService.isAdminRegistered()).thenReturn(true);
        ResponseEntity<String> response = facade.openSystem(storeRepository);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("System opened successfully.", response.getBody());
        verify(userService, times(1)).isAdminRegistered();
    }

    @Test
    void openSystem_No_Admin() {
        when(userService.isAdminRegistered()).thenReturn(false);
        ResponseEntity<String> response = facade.openSystem(storeRepository);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("System cannot be opened without at least one admin registered.", response.getBody());
//        verify(userService, times(1)).isAdminRegistered();
    }

}
