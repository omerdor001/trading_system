package com.example.trading_system.Market;

import com.example.trading_system.domain.stores.Category;
import com.example.trading_system.domain.stores.Store;
import com.example.trading_system.domain.stores.StorePolicy;
import com.example.trading_system.service.MarketService;
import com.example.trading_system.service.TradingSystemImp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class MarketFacadeAcceptanceTests {

    private TradingSystemImp facade;
    private MarketService marketServiceMock;

    @BeforeEach
    void setUp() {
        facade = TradingSystemImp.getInstance();
        marketServiceMock = mock(MarketService.class);

        facade.register(1, "admin", "adminPass", LocalDate.of(1990, 1, 1));
        facade.openSystem();
        ResponseEntity<String> response = facade.enter();
    }

    @AfterEach
    void tearDown() {
        facade.logout(1, "admin");
    }

    @Test
    void testGetAllStores_Success() {
        // Mock the expected response
        String expectedResponse = "{, \"stores\":[{\"store_name\":\"store1\"}]}";
        when(marketServiceMock.getAllStores()).thenReturn(expectedResponse);

        ResponseEntity<String> response = facade.getAllStores();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        //assertTrue(Objects.requireNonNull(response.getBody()).contains("store1"));
    }

    @Test
    void testGetStoreProducts_StoreNotActive() {
        // Mock the expected exception
        when(marketServiceMock.getStoreProducts("store1")).thenThrow(new RuntimeException("Store not active"));

        ResponseEntity<String> response = facade.getStoreProducts("store1");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}

