package com.example.trading_system.AcceptanceTests.TradingSystemSetupAndEntry;

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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import jakarta.transaction.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
class OpenSystemAcceptanceTests {
    private TradingSystemImp tradingSystem;
    @Autowired
    private UserDatabaseRepository userRepository;

    @Autowired
    private StoreDatabaseRepository storeRepository;
    @BeforeEach
    void setUp() {

        tradingSystem = TradingSystemImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class), mock(NotificationSender.class),userRepository,storeRepository);
    }

    @AfterEach
    void setDown(){
        tradingSystem.deleteInstance();
        userRepository.deleteInstance();
        storeRepository.deleteInstance();
    }

    @Test
    void openSystem_Success() {
        String username = "admin";
        String password = "adminPass";
        LocalDate birthdate = LocalDate.of(1980, 1, 1);
        tradingSystem.register(username, password, birthdate);
        ResponseEntity<String> response = tradingSystem.openSystem(storeRepository);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("System opened successfully.", response.getBody());
    }

    @Test
    void openSystem_NoAdmin() {
        ResponseEntity<String> response = tradingSystem.openSystem(storeRepository);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("System cannot be opened without at least one admin registered.", response.getBody());
    }

    @Test
    void openSystem_AlreadyOpen() {
        String username = "admin";
        String password = "adminPass";
        LocalDate birthdate = LocalDate.of(1980, 1, 1);
        tradingSystem.register(username, password, birthdate);
        tradingSystem.openSystem(storeRepository);
        ResponseEntity<String> response = tradingSystem.openSystem(storeRepository);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("System is already open.", response.getBody());
    }
}
