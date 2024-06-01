package com.example.trading_system.AcceptanceTests.Users;

import com.example.trading_system.domain.stores.StorePolicy;
import com.example.trading_system.domain.users.UserFacade;
import com.example.trading_system.service.UserService;
import com.example.trading_system.service.UserServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OpeningStoreAccepatanceTests {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
    }

    @Test
    public void testOpenStore() {
        String username = "user1";
        String storeName = "store1";
        String description = "A nice store";
        StorePolicy policy = mock(StorePolicy.class);

        when(userService.openStore(username, storeName, description, policy)).thenReturn(true);

        boolean result = userService.openStore(username, storeName, description, policy);
        assertTrue(result);
    }

    @Test
    public void testOpenStoreFailsDueToExistingStoreName() {
        String username = "user1";
        String storeName = "existingStore";
        String description = "A nice store";
        StorePolicy policy = mock(StorePolicy.class);
        when(userService.openStore(username, storeName, description, policy))
                .thenThrow(new RuntimeException("Store with name " + storeName + " already exists"));
        assertThrows(RuntimeException.class, () -> {
            userService.openStore(username, storeName, description, policy);
        });
    }
    }

