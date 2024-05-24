package com.example.trading_system.Market;

import com.example.trading_system.domain.stores.*;
import com.example.trading_system.domain.users.Visitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SearchUnitTests {
    private MarketFacadeImp marketFacade;

    ////////////////Check if more tests are needed.
    @BeforeEach
    void setUp() {
        marketFacade = new MarketFacadeImp();
        Store store = new Store("store1", "description",new StorePolicy());
        Product product = new Product(1, "p1", "", 5, 5, 5, Category.Food, new ArrayList<>());
        store.addProductToStore(product);
        marketFacade.getStores().put(store.getName_id(), store);
        //marketFacade.getStores().put("store2", new Store("store2","description"));
    }

    @Test
    void SearchNameInStore_Success() throws Exception {
        assertDoesNotThrow(() -> marketFacade.searchNameInStore("p1", "store1", 5.0, 5.0, 5.0, Category.Food));
        assertTrue(marketFacade.searchNameInStore("p1", "store1", 5.0, 5.0, 5.0, Category.Food).contains("p1"));
    }

    @Test
    void SearchName1InStore_Success() throws Exception {
        assertDoesNotThrow(() -> marketFacade.searchNameInStore("p", "store1", 5.0, 5.0, 5.0, Category.Food));
        assertFalse(marketFacade.searchNameInStore("p", "store1", 5.0, 5.0, 5.0, Category.Food).contains("p1"));
    }

    @Test
    void SearchNameInStore_nullName() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> marketFacade.searchNameInStore(null, "store1", 5.0, 5.0, 5.0, Category.Food));
        assertEquals(exception.getMessage(), "No name provided");
    }

    @Test
    void SearchCategoryInStore_nullCategory() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> marketFacade.searchCategoryInStore(null, "store1", 5.0, 5.0, 5.0));
        assertEquals(exception.getMessage(), "No category provided");
    }

    @Test
    void SearchkeywordInStore_nullkeyword() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> marketFacade.searchKeywordsInStore(null, "store1", 5.0, 5.0, 5.0, Category.Food));
        assertEquals(exception.getMessage(), "No keywords provided");
    }


    ///////////////////////////tests for searching in stores - without specific focus


    void SearchNameInStores_Success() throws Exception {
        assertDoesNotThrow(() -> marketFacade.searchNameInStores("p1", 5.0, 5.0, 5.0, Category.Food));
        assertTrue(marketFacade.searchNameInStores("p1", 5.0, 5.0, 5.0, Category.Food).contains("p1"));
    }

    @Test
    void SearchName1InStores_Success() throws Exception {
        assertDoesNotThrow(() -> marketFacade.searchNameInStores("p", 5.0, 5.0, 5.0, Category.Food));
        assertFalse(marketFacade.searchNameInStores("p", 5.0, 5.0, 5.0, Category.Food).contains("p1"));
    }

    @Test
    void SearchNameInStores_nullName() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> marketFacade.searchNameInStores(null, 5.0, 5.0, 5.0, Category.Food));
        assertEquals(exception.getMessage(), "No name provided");
    }

    @Test
    void SearchCategoryInStores_nullCategory() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> marketFacade.searchCategoryInStores(null, 5.0, 5.0, 5.0));
        assertEquals(exception.getMessage(), "No category provided");
    }

    @Test
    void SearchkeywordInStores_nullkeyword() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> marketFacade.searchKeywordsInStores(null, 5.0, 5.0, 5.0, Category.Food));
        assertEquals(exception.getMessage(), "No keywords provided");
    }

}
