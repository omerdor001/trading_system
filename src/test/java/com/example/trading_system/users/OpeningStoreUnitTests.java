package com.example.trading_system.users;

import com.example.trading_system.domain.stores.MarketFacadeImp;
import com.example.trading_system.domain.stores.StorePolicy;
import com.example.trading_system.domain.stores.Store;
import com.example.trading_system.domain.users.Registered;
import com.example.trading_system.domain.users.UserFacadeImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class OpeningStoreUnitTests {
    private UserFacadeImp userFacade;
    private MarketFacadeImp marketFacade;

    @BeforeEach
    void setUp() {
        marketFacade = MarketFacadeImp.getInstance();
        userFacade = UserFacadeImp.getInstance();
    }

    @Test
    void testOpenStoreWithValidInputs() {
        String username = "user1";
        String storeName = "store1";
        String description = "A nice store";
        StorePolicy policy = new StorePolicy();

        Registered registered = new Registered(1, username, "address", LocalDate.of(1990, 1, 1));
        userFacade.getRegistered().put(username, registered);

        userFacade.openStore(username, storeName, description, policy);

        Store store = marketFacade.getStores().get(storeName);

        assertNotNull(store);
        assertEquals(storeName, store.getName_id());
        assertEquals(description, store.getDescription());
    }

    @Test
    void testOpenStoreWithNullStoreName() {
        String username = "user1";
        String description = "A nice store";
        StorePolicy policy = new StorePolicy();

        Registered registered = new Registered(1, username, "address", LocalDate.of(1990, 1, 1));
        userFacade.getRegistered().put(username, registered);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userFacade.openStore(username, null, description, policy);
        });

        assertEquals("Store name is null", exception.getMessage());
    }

    @Test
    void testOpenStoreWithExistingStoreName() {
        String username = "user1";
        String storeName = "store1";
        String description = "A nice store";
        StorePolicy policy = new StorePolicy();

        Registered registered = new Registered(1, username, "address", LocalDate.of(1990, 1, 1));
        userFacade.getRegistered().put(username, registered);

        Store existingStore = new Store(storeName, description, new StorePolicy());
        marketFacade.addStore(existingStore);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userFacade.openStore(username, storeName, description, policy);
        });

        assertEquals("Store with name " + storeName + " already exists", exception.getMessage());
    }

    @Test
    void testOpenStoreWithNonExistingUser() {
        //TODO: every test should be atomic and separated from the rest of the tests
        String storeName = "store2";
        String description = "A nice store";
        StorePolicy policy = new StorePolicy();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userFacade.openStore("nonexistent_user", storeName, description, policy);
        });

        assertEquals("User not found", exception.getMessage());
    }
}