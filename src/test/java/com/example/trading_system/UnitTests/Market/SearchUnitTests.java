package com.example.trading_system.UnitTests.Market;

import com.example.trading_system.domain.stores.*;
import com.example.trading_system.domain.users.User;
import com.example.trading_system.domain.users.UserFacadeImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SearchUnitTests {
    private MarketFacadeImp marketFacade;
    private String validUsername = "validName";
    User user;
    UserFacadeImp userFacade;


    ////////////////Check if more tests are needed.
    @BeforeEach
    void setUp() {
        marketFacade = MarketFacadeImp.getInstance();
        userFacade = UserFacadeImp.getInstance();
        Store store = new Store("store1", "description",new StorePolicy(),"robert",null);
        Product product = new Product(1, "p1", "", 5, 5, 5, Category.Food, new ArrayList<>());
        store.addProduct(1, "smartwatch","p1", "", 5, 5, 5, 3, new ArrayList<>());
        marketFacade.getStores().put(store.getNameId(), store);
        // Mock user object
        user = mock(User.class);

        // Add the mocked user to the facade
        userFacade.getUsers().put(validUsername, user);

        // Mock the user to return a valid role for the store
        //marketFacade.getStores().put("store2", new Store("store2","description"));
    }

    @Test
    void SearchNameInStore_Success() throws Exception {
        assertDoesNotThrow(() -> marketFacade.searchNameInStore(validUsername,"p1", "store1", 5.0, 5.0, 5.0, Category.Food.getIntValue()));
        assertTrue(marketFacade.searchNameInStore(validUsername,"p1", "store1", 5.0, 5.0, 5.0, Category.Food.getIntValue()).contains("p1"));
    }

    @Test
    void SearchName1InStore_Success() throws Exception {
        assertDoesNotThrow(() -> marketFacade.searchNameInStore(validUsername,"p", "store1", 5.0, 5.0, 5.0, Category.Food.getIntValue()));
        assertFalse(marketFacade.searchNameInStore(validUsername,"p", "store1", 5.0, 5.0, 5.0, Category.Food.getIntValue()).contains("p1"));
    }

    @Test
    void SearchNameInStore_nullName() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> marketFacade.searchNameInStore(validUsername,null, "store1", 5.0, 5.0, 5.0, Category.Food.getIntValue()));
        assertEquals(exception.getMessage(), "No name provided");
    }

    @Test
    void SearchCategoryInStore_nullCategory() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> marketFacade.searchCategoryInStore(validUsername,-1, "store1", 5.0, 5.0, 5.0));
        assertEquals(exception.getMessage(), "No category provided");
    }

    @Test
    void SearchkeywordInStore_nullkeyword() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> marketFacade.searchKeywordsInStore(validUsername,null, "store1", 5.0, 5.0, 5.0, Category.Food.getIntValue()));
        assertEquals(exception.getMessage(), "No keywords provided");
    }


    ///////////////////////////tests for searching in stores - without specific focus


    void SearchNameInStores_Success() throws Exception {
        assertDoesNotThrow(() -> marketFacade.searchNameInStores(validUsername,"p1", 5.0, 5.0, 5.0, Category.Food.getIntValue(),null));
        assertTrue(marketFacade.searchNameInStores(validUsername,"p1", 5.0, 5.0, 5.0, Category.Food.getIntValue(),null).contains("p1"));
    }

    @Test
    void SearchName1InStores_Success() throws Exception {
        assertDoesNotThrow(() -> marketFacade.searchNameInStores(validUsername,"p", 5.0, 5.0, 5.0, Category.Food.getIntValue(),null));
        assertFalse(marketFacade.searchNameInStores(validUsername,"p", 5.0, 5.0, 5.0, Category.Food.getIntValue(),null).contains("p1"));
    }

    @Test
    void SearchNameInStores_nullName() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> marketFacade.searchNameInStores(validUsername,null, 5.0, 5.0, 5.0, Category.Food.getIntValue(),null));
        assertEquals(exception.getMessage(), "No name provided");
    }

    @Test
    void SearchCategoryInStores_nullCategory() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> marketFacade.searchCategoryInStores(validUsername,-1, 5.0, 5.0, 5.0,null));
        assertEquals(exception.getMessage(), "No category provided");
    }

    @Test
    void SearchkeywordInStores_nullkeyword() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> marketFacade.searchKeywordsInStores(validUsername,null, 5.0, 5.0, 5.0, Category.Food.getIntValue(),null));
        assertEquals(exception.getMessage(), "No keywords provided");
    }

}
