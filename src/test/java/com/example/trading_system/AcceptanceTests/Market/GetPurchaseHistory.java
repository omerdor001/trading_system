/*
package com.example.trading_system.AcceptanceTests.Market;

import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.*;

import com.example.trading_system.domain.users.UserFacadeImp;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetPurchaseHistory {

    private UserFacadeImp userFacadeImp;
    LocalDate birthdate = LocalDate.of(1990, 5, 15);

    @BeforeAll
    void setUpOnce() throws Exception {
        PaymentService paymentServiceMock = mock(PaymentService.class);
        DeliveryService deliveryServiceMock = mock(DeliveryService.class);
        userFacadeImp = UserFacadeImp.getInstance();

        userFacadeImp.enter(1);
        userFacadeImp.enter(2);
        userFacadeImp.register( "testuser", "testpassword", birthdate);
        userFacadeImp.register( "testuser2", "testpassword2", birthdate);
        userFacadeImp.login("v0", "testuser", "testpassword");
        userFacadeImp.login("v1", "testuser2", "testpassword2");

        Store store1 = mock(Store.class);
        userFacadeImp.createStore("testuser", "store1", "description", new StorePolicy());
        when(store1.toString()).thenReturn("Store 1");

        MarketFacadeImp marketFacade = MarketFacadeImp.getInstance();
        marketFacade.addProduct("testuser", 1, "store1", "product1", "", 5, 50, 5, 1, new ArrayList<>());
        marketFacade.addProduct("testuser", 2, "store1", "product2", "", 5, 50, 5, 1, new ArrayList<>());
        ProductInSale productInSale = new ProductInSale("store1",1, 100.0, 2);
        ProductInSale productInSale2 = new ProductInSale("store1",2, 100.0, 2);
        Purchase purchase1 = new Purchase("testuser", List.of(productInSale), 100.0, "store1");
        Purchase purchase2 = new Purchase("testuser", List.of(productInSale2), 100.0, "store1");
        userFacadeImp.purchaseCart("testuser");

//        userFacadeImp.getPurchases().add(purchase2);
    }

    @BeforeEach
    void setUp() {
        if (!userFacadeImp.getUsers().get("testuser").getLogged())
            userFacadeImp.login("v0", "testuser", "testpassword");
        userFacadeImp.getUsers().get("testuser").setAdmin(true);
    }

    @Test
    void testGetPurchaseHistory_ValidInput() {
        String result = userFacadeImp.getPurchaseHistory("testuser", "store1");
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("store1"));
        assertFalse(result.contains("store2"));
    }

    @Test
    void testGetPurchaseHistory_ValidInput2() {
        String result = userFacadeImp.getPurchaseHistory("testuser", "store1");
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("store1"));
        assertFalse(result.contains("store2"));
    }


    @Test
    void testGetStoresPurchaseHistory_ValidInput() {
        String result = userFacadeImp.getPurchaseHistory("testuser", "store1");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }


    @Test
    void testGetPurchaseHistory_NotAdmin() {
        userFacadeImp.getUsers().get("testuser").setAdmin(false);
        assertThrows(RuntimeException.class, () -> {
            userFacadeImp.getPurchaseHistory("testuser", "store1");
        });
    }


    @Test
    void testGetStoresPurchaseHistory_NotAdmin() {
        userFacadeImp.getUsers().get("testuser").setAdmin(false);
        assertThrows(RuntimeException.class, () -> {
            userFacadeImp.getPurchaseHistory("testuser", "store1");
        });
    }

    @Test
    void testGetPurchaseHistory_UserNotLogged() {
        userFacadeImp.getUsers().get("testuser").logout();
        assertThrows(RuntimeException.class, () -> {
            userFacadeImp.getPurchaseHistory("testuser", "store1");
        });
    }

    @Test
    void testGetPurchaseHistory_UserNotFound() {
        assertThrows(RuntimeException.class, () -> {
            userFacadeImp.getPurchaseHistory("invalidUser", null);
        });
    }

    @Test
    void testGetStoresPurchaseHistory_UserNotFound() {
        assertThrows(RuntimeException.class, () -> {
            userFacadeImp.getPurchaseHistory("invalidUser", null);
        });
    }

    @Test
    void testGetPurchaseHistory_InvalidStore() {
        assertThrows(RuntimeException.class, () -> {
            userFacadeImp.getPurchaseHistory("testuser", "invalidStore");
        });
    }

    @Test
    void testGetPurchaseHistory_NullInputs() {
        assertThrows(RuntimeException.class, () -> {
            userFacadeImp.getPurchaseHistory(null, null);
        });
    }

    @Test
    void testGetStoresPurchaseHistory_InvalidStore() {
        assertThrows(RuntimeException.class, () -> {
            userFacadeImp.getPurchaseHistory("testuser", "invalidStore");
        });
    }

    @Test
    void testGetStoresPurchaseHistory_NullInputs() {
        assertThrows(RuntimeException.class, () -> {
            userFacadeImp.getPurchaseHistory(null, null);
        });
    }

    @Test
    void testGetPurchaseHistory_ValidStoreButNoPurchases() {
        // Arrange
        userFacadeImp.deleteInstance(); // Clear purchases to simulate no purchases

        // Act
        String result = userFacadeImp.getPurchaseHistory("testuser", "store1");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }



}

*/
