package com.example.trading_system.AcceptanceTests.Users;

import com.example.trading_system.domain.stores.*;
import com.example.trading_system.domain.users.Registered;
import com.example.trading_system.domain.users.Role;
import com.example.trading_system.domain.users.UserFacadeImp;
import com.example.trading_system.service.MarketServiceImp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddProductAcceptanceTests {

    private MarketFacadeImp storeManagementFacade;

    @Mock
    private UserFacadeImp userFacade;

    @Mock
    private StorePolicy mockStorePolicy;

    private Map<String, Store> stores;

    @Mock
    private Registered registered;

    @Mock
    private Role role;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        stores = new HashMap<>();
        storeManagementFacade =  MarketFacadeImp.getInstance();

        Store store = new Store("existingStore", "General Store", mockStorePolicy, "storeFounder");
        stores.put("existingStore", store);

        when(userFacade.getRegistered()).thenReturn(new HashMap<String, Registered>() {{
            put("validUser", registered);
        }});
        when(registered.getRoleByStoreId(anyString())).thenReturn(role);
    }

/*
    @Test
    void addProduct_Success() throws IllegalAccessException {
        assertNotNull(stores.get("existingStore"), "Store should exist in the map before calling addProduct");
        assertTrue(storeManagementFacade.addProduct("validUser", 101, "existingStore", "New Product", "Description",
                29.99, 10, 4.5, 1, Arrays.asList("electronics", "gadget")));
        verify(role).addProduct(eq("validUser"), eq(101), eq("existingStore"), eq("New Product"), eq("Description"),
                eq(29.99), eq(10), eq(4.5), eq(1), anyList());
    }
*/

    @Test
    void addProduct_NonExistentStore() {
        assertThrows(IllegalArgumentException.class, () ->
                storeManagementFacade.addProduct("validUser", 102, "nonExistentStore", "Product", "Description", 19.99, 5, 3.0, 1, Arrays.asList("toy")));
    }

    @Test
    void addProduct_NegativePrice() {
        assertThrows(IllegalArgumentException.class, () ->
                storeManagementFacade.addProduct("validUser", 103, "existingStore", "Product", "Description", -1, 5, 3.0, 1, Arrays.asList("toy")));
    }

    @Test
    void addProduct_ZeroQuantity() {
        assertThrows(IllegalArgumentException.class, () ->
                storeManagementFacade.addProduct("validUser", 104, "existingStore", "Product", "Description", 20, 0, 3.0, 1, Arrays.asList("toy")));
    }

    @Test
    void addProduct_NegativeRating() {
        assertThrows(IllegalArgumentException.class, () ->
                storeManagementFacade.addProduct("validUser", 105, "existingStore", "Product", "Description", 20, 1, -1, 1, Arrays.asList("toy")));
    }

    @Test
    void addProduct_NonExistentUser() {
        assertThrows(IllegalArgumentException.class, () ->
                storeManagementFacade.addProduct("nonExistentUser", 106, "existingStore", "Product", "Description", 20, 1, 3.0, 1, Arrays.asList("toy")));
    }
}