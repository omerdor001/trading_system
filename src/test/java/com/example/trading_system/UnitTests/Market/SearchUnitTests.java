package com.example.trading_system.UnitTests.Market;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.*;
import com.example.trading_system.domain.users.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class SearchUnitTests {
    private MarketFacadeImp marketFacade;
    private final String validUsername = "vvalidName"; // Prefix with 'v' for visitor
    private User user;
    private UserFacadeImp userFacade;

    @Autowired
    private UserDatabaseRepository userRepository;

    @Autowired
    private StoreDatabaseRepository storeRepository;

    @BeforeEach
    void setUp() {
        userFacade = UserFacadeImp.getInstance(
                mock(PaymentService.class),
                mock(DeliveryService.class),
                mock(NotificationSender.class),
                userRepository,
                storeRepository
        );
        marketFacade = MarketFacadeImp.getInstance(storeRepository);

        // Create and persist a store
        Store store = new Store("store1", "description", "robert", null);
        store.addProduct(1, "p1", "", 5, 5, 5, 3, new ArrayList<>());
        storeRepository.save(store);

        // Create and persist a user
        user = new Visitor(validUsername.substring(1)); // Remove the 'v' prefix when creating the Visitor
        userRepository.addVisitor(validUsername); // Add visitor with 'v' prefix

        // Adding the user to the facade's internal storage
        userFacade.getUsers().put(validUsername, user);
    }

    @AfterEach
    void tearDown() {
        marketFacade.deleteInstance();
        userFacade.deleteInstance();
        userRepository.deleteInstance();
        storeRepository.deleteInstance();
    }

    @Test
    void searchNameInStore_Success() throws IllegalAccessException {
        assertDoesNotThrow(() -> marketFacade.searchNameInStore(validUsername, "p1", "store1", 5.0, 5.0, 5.0, Category.Food.getIntValue()));
        assertTrue(marketFacade.searchNameInStore(validUsername, "p1", "store1", 5.0, 5.0, 5.0, Category.Food.getIntValue()).contains("p1"));
    }

    @Test
    void searchName1InStore_Success() throws IllegalAccessException {
        assertDoesNotThrow(() -> marketFacade.searchNameInStore(validUsername, "p", "store1", 5.0, 5.0, 5.0, Category.Food.getIntValue()));
        assertFalse(marketFacade.searchNameInStore(validUsername, "p", "store1", 5.0, 5.0, 5.0, Category.Food.getIntValue()).contains("p1"));
    }

    @Test
    void SearchNameInStore_nullName() {
        Exception exception = assertThrows(Exception.class, () -> marketFacade.searchNameInStore(validUsername, null, "store1", 5.0, 5.0, 5.0, Category.Food.getIntValue()));
        assertEquals("No name provided", exception.getMessage());
    }

    @Test
    void SearchCategoryInStore_nullCategory() {
        Exception exception = assertThrows(Exception.class, () -> marketFacade.searchCategoryInStore(validUsername, -1, "store1", 5.0, 5.0, 5.0));
        assertEquals("No category provided", exception.getMessage());
    }

    @Test
    void SearchkeywordInStore_nullkeyword() {
        Exception exception = assertThrows(Exception.class, () -> marketFacade.searchKeywordsInStore(validUsername, null, "store1", 5.0, 5.0, 5.0, Category.Food.getIntValue()));
        assertEquals("No keywords provided", exception.getMessage());
    }

    @Test
    void SearchNameInStores_Success() {
        assertDoesNotThrow(() -> marketFacade.searchNameInStores(validUsername, "p1", 5.0, 5.0, 5.0, Category.Food.getIntValue(), null));
        assertTrue(marketFacade.searchNameInStores(validUsername, "p1", 5.0, 5.0, 5.0, Category.Food.getIntValue(), null).contains("p1"));
    }

    @Test
    void SearchName1InStores_Success() {
        assertDoesNotThrow(() -> marketFacade.searchNameInStores(validUsername, "p", 5.0, 5.0, 5.0, Category.Food.getIntValue(), null));
        assertFalse(marketFacade.searchNameInStores(validUsername, "p", 5.0, 5.0, 5.0, Category.Food.getIntValue(), null).contains("p1"));
    }

    @Test
    void SearchNameInStores_nullName() {
        Exception exception = assertThrows(Exception.class, () -> marketFacade.searchNameInStores(validUsername, null, 5.0, 5.0, 5.0, Category.Food.getIntValue(), null));
        assertEquals("No name provided", exception.getMessage());
    }

    @Test
    void SearchCategoryInStores_nullCategory() {
        Exception exception = assertThrows(Exception.class, () -> marketFacade.searchCategoryInStores(validUsername, -1, 5.0, 5.0, 5.0, null));
        assertEquals("No category provided", exception.getMessage());
    }

    @Test
    void SearchkeywordInStores_nullkeyword() {
        Exception exception = assertThrows(Exception.class, () -> marketFacade.searchKeywordsInStores(validUsername, null, 5.0, 5.0, 5.0, Category.Food.getIntValue(), null));
        assertEquals("No keywords provided", exception.getMessage());
    }
}
