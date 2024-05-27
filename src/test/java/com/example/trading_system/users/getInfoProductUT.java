package com.example.trading_system.users;

import com.example.trading_system.domain.stores.Category;
import com.example.trading_system.domain.stores.MarketFacadeImp;
import com.example.trading_system.domain.stores.Store;
import com.example.trading_system.domain.stores.StorePolicy;
import com.example.trading_system.domain.users.UserFacadeImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class getInfoProductUT {
    private MarketFacadeImp marketFacade;
    private UserFacadeImp userFacade;

    @BeforeEach
    void setUp() {
        marketFacade = MarketFacadeImp.getInstance();
        userFacade = UserFacadeImp.getInstance();
        marketFacade.getStores().clear();
    }

    @Test
    void getAllStores_Success() {
        Store store1 = new Store("store1", "desc1",new StorePolicy(),"FOUNDER");
        Store store2 = new Store("store2", "desc2",new StorePolicy(),"FOUNDER");
        marketFacade.addStore(store1);
        marketFacade.addStore(store2);

        String expected = "{, \"stores\":[{\"name_id\":\"store1\", \"description\":\"desc1\", \"products\":[]}, {\"name_id\":\"store2\", \"description\":\"desc2\", \"products\":[]}, ]}";
        String result = marketFacade.getAllStores();

        assertEquals(expected, result);
    }

    @Test
    void getStoreProducts_Success() {
        Store store = new Store("store1", "desc1",new StorePolicy(),"FOUNDER");
        store.setActive(true);
        marketFacade.addStore(store);

        String expected = store.toString();
        String result = marketFacade.getStoreProducts("store1");

        assertEquals(expected, result);
    }

    @Test
    void getStoreProducts_StoreNotActive() {
        Store store = new Store("store1", "desc1",new StorePolicy(),"FOUNDER");
        store.setActive(false);
        marketFacade.addStore(store);

        String result = marketFacade.getStoreProducts("store1");

        assertNull(result);
    }

    @Test
    void getProductInfo_Success() {
        Store store = new Store("store1", "desc1",new StorePolicy(),"FOUNDER");
        store.addProduct(1, "store1", "product1", "desc", 10.0, 5, 4.5, Category.Food.ordinal(), List.of("food"));
        marketFacade.addStore(store);

        String expected = store.getProduct(1).toString();
        String result = marketFacade.getProductInfo("store1", 1);

        assertEquals(expected, result);
}
}