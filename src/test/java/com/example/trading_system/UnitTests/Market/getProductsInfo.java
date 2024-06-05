package com.example.trading_system.UnitTests.Market;

import com.example.trading_system.domain.stores.*;
import com.example.trading_system.domain.users.UserFacadeImp;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

class getProductsInfo {

    private MarketFacadeImp marketFacade;
    private UserFacadeImp userFacadeImp;
    LocalDate birthdate = LocalDate.of(1990, 5, 15);

    @BeforeAll
    void setUpOnce() throws Exception {
        userFacadeImp = UserFacadeImp.getInstance();
        marketFacade = MarketFacadeImp.getInstance();
        userFacadeImp.enter(1);
        userFacadeImp.register(1, "testuser", "testpassword", birthdate);
        userFacadeImp.login(1, "testuser", "testpassword");
        Store store1 = mock(Store.class);
        Store store2 = mock(Store.class);
        userFacadeImp.openStore("testuser", "store1", "description", new StorePolicy());
        userFacadeImp.openStore("testuser", "store2", "description", new StorePolicy());
        when(store1.toString()).thenReturn("Store 1");
        when(store2.toString()).thenReturn("Store 2");
        marketFacade.addProduct("testuser", 1, "store1", "product1", "", 5, 5, 5, 1, new ArrayList<>());
    }

    @Test
    void testGetAllStores() throws Exception {

        String expected = "[\"stores\":store1,store2]";
        String actual = marketFacade.getAllStores();
        assertEquals(expected, actual);
    }

    @Test
    void testGetStoreProducts() throws Exception {
        String expected = "{\"name_id\":\"store1\", \"description\":\"description\", \"products\":[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Art, \"keyWords\":[]}, ]}";
        String actual = marketFacade.getStoreProducts("store1");
        assertEquals(expected, actual);
    }
@Test
    void testGetProductInfo() throws Exception {
        String expected = "{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Art, \"keyWords\":[]}";
        String actual = marketFacade.getProductInfo("store1",1);
        assertEquals(expected, actual);
    }
}
