package com.example.trading_system.Market;

import com.example.trading_system.domain.stores.Category;
import com.example.trading_system.domain.stores.MarketFacadeImp;
import com.example.trading_system.domain.stores.Product;
import com.example.trading_system.domain.stores.Store;
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
        Store store = new Store("store1", "description");
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
    void SearchName_nullName() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> marketFacade.searchNameInStore(null, "store1", 5.0, 5.0, 5.0, Category.Food));
        assertEquals(exception.getMessage(), "No name provided");
    }

    @Test
    void SearchCategory_nullCategory() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> marketFacade.searchCategoryInStore(null, "store1", 5.0, 5.0, 5.0));
        assertEquals(exception.getMessage(), "No category provided");
    }

    @Test
    void Searchkeyword_nullkeyword() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> marketFacade.searchKeywordsInStore(null, "store1", 5.0, 5.0, 5.0, Category.Food));
        assertEquals(exception.getMessage(), "No keywords provided");
    }


}
