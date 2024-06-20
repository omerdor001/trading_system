package com.example.trading_system.UnitTests.Market;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.*;
import com.example.trading_system.domain.users.User;
import com.example.trading_system.domain.users.UserFacadeImp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class SearchUnitTests {
    private MarketFacadeImp marketFacade;
    private final String validUsername = "validName";
    User user;
    UserFacadeImp userFacade;


    ////////////////Check if more tests are needed.
    @BeforeEach
    void setUp() {
        marketFacade = MarketFacadeImp.getInstance();
        userFacade = UserFacadeImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class), mock(NotificationSender.class));
        Store store = new Store("store1", "description","robert",null);
        store.addProduct(1,"p1", "", 5, 5, 5, 3, new ArrayList<>());
        marketFacade.getStores().put(store.getNameId(), store);
        // Mock user object
        user = mock(User.class);

        // Add the mocked user to the facade
        userFacade.getUsers().put(validUsername, user);

        // Mock the user to return a valid role for the store
        //marketFacade.getStores().put("store2", new Store("store2","description"));
    }

    @AfterEach
    void setDown(){
        marketFacade.deleteInstance();
        userFacade.deleteInstance();
    }

    @Test
    void searchNameInStore_Success() {
        try {
            assertDoesNotThrow(() -> marketFacade.searchNameInStore(validUsername,"p1", "store1", 5.0, 5.0, 5.0, Category.Food.getIntValue()));
            assertTrue(marketFacade.searchNameInStore(validUsername,"p1", "store1", 5.0, 5.0, 5.0, Category.Food.getIntValue()).contains("p1"));
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    void searchName1InStore_Success() {
        try {
            assertDoesNotThrow(() -> marketFacade.searchNameInStore(validUsername,"p", "store1", 5.0, 5.0, 5.0, Category.Food.getIntValue()));
            assertFalse(marketFacade.searchNameInStore(validUsername,"p", "store1", 5.0, 5.0, 5.0, Category.Food.getIntValue()).contains("p1"));
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    void SearchNameInStore_nullName() {
        Exception exception = assertThrows(Exception.class, () -> marketFacade.searchNameInStore(validUsername,null, "store1", 5.0, 5.0, 5.0, Category.Food.getIntValue()));
        assertEquals(exception.getMessage(), "No name provided");
    }

    @Test
    void SearchCategoryInStore_nullCategory() {
        Exception exception = assertThrows(Exception.class, () -> marketFacade.searchCategoryInStore(validUsername,-1, "store1", 5.0, 5.0, 5.0));
        assertEquals(exception.getMessage(), "No category provided");
    }

    @Test
    void SearchkeywordInStore_nullkeyword() {
        Exception exception = assertThrows(Exception.class, () -> marketFacade.searchKeywordsInStore(validUsername,null, "store1", 5.0, 5.0, 5.0, Category.Food.getIntValue()));
        assertEquals(exception.getMessage(), "No keywords provided");
    }


    ///////////////////////////tests for searching in stores - without specific focus

    @Test
    void SearchNameInStores_Success() {
        assertDoesNotThrow(() -> marketFacade.searchNameInStores(validUsername,"p1", 5.0, 5.0, 5.0, Category.Food.getIntValue(),null));
        assertTrue(marketFacade.searchNameInStores(validUsername,"p1", 5.0, 5.0, 5.0, Category.Food.getIntValue(),null).contains("p1"));
    }

    @Test
    void SearchName1InStores_Success() {
        assertDoesNotThrow(() -> marketFacade.searchNameInStores(validUsername,"p", 5.0, 5.0, 5.0, Category.Food.getIntValue(),null));
        assertFalse(marketFacade.searchNameInStores(validUsername,"p", 5.0, 5.0, 5.0, Category.Food.getIntValue(),null).contains("p1"));
    }

    @Test
    void SearchNameInStores_nullName() {
        Exception exception = assertThrows(Exception.class, () -> marketFacade.searchNameInStores(validUsername,null, 5.0, 5.0, 5.0, Category.Food.getIntValue(),null));
        assertEquals(exception.getMessage(), "No name provided");
    }

    @Test
    void SearchCategoryInStores_nullCategory() {
        Exception exception = assertThrows(Exception.class, () -> marketFacade.searchCategoryInStores(validUsername,-1, 5.0, 5.0, 5.0,null));
        assertEquals(exception.getMessage(), "No category provided");
    }

    @Test
    void SearchkeywordInStores_nullkeyword() {
        Exception exception = assertThrows(Exception.class, () -> marketFacade.searchKeywordsInStores(validUsername,null, 5.0, 5.0, 5.0, Category.Food.getIntValue(),null));
        assertEquals(exception.getMessage(), "No keywords provided");
    }

}
