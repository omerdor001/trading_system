package com.example.trading_system.UnitTests.Market;

import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.*;
import com.example.trading_system.domain.users.PaymentFacade;
import com.example.trading_system.domain.users.PaymentFacadeImp;
import com.example.trading_system.domain.users.UserFacadeImp;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentFacadeImpTests {

    private PaymentFacadeImp paymentFacade;
    private UserFacadeImp userFacadeImp;
    LocalDate birthdate = LocalDate.of(1990, 5, 15);

    @BeforeAll
    void setUpOnce() throws Exception {
        PaymentService paymentServiceMock = mock(PaymentService.class);
        DeliveryService deliveryServiceMock = mock(DeliveryService.class);
        paymentFacade = new PaymentFacadeImp(paymentServiceMock, deliveryServiceMock);
        userFacadeImp = UserFacadeImp.getInstance();

        userFacadeImp.enter(1);
        userFacadeImp.enter(2);
        userFacadeImp.register(1, "testuser", "testpassword", birthdate);
        userFacadeImp.register(2, "testuser2", "testpassword2", birthdate);
        userFacadeImp.login(1, "testuser", "testpassword");
        userFacadeImp.login(2, "testuser2", "testpassword2");

        Store store1 = mock(Store.class);
        userFacadeImp.openStore("testuser", "store1", "description", new StorePolicy());
        when(store1.toString()).thenReturn("Store 1");

        MarketFacadeImp marketFacade = MarketFacadeImp.getInstance();
        marketFacade.addProduct("testuser", 1, "store1", "product1", "", 5, 50, 5, 1, new ArrayList<>());
        marketFacade.addProduct("testuser", 2, "store1", "product2", "", 5, 50, 5, 1, new ArrayList<>());
        ProductInSale productInSale = new ProductInSale(1, 100.0, 2, "store1");
        ProductInSale productInSale2 = new ProductInSale(2, 100.0, 2, "store1");
        Purchase purchase1 = new Purchase(1, List.of(productInSale), "store1", 100.0);
        Purchase purchase2 = new Purchase(2, List.of(productInSale2), "store1", 100.0);
        paymentFacade.getPurchases().add(purchase1);
        paymentFacade.getPurchases().add(purchase2);
    }

    @BeforeEach
    void setUp() {
        if (!userFacadeImp.getRegistered().get("testuser").getLogged())
            userFacadeImp.login(1, "testuser", "testpassword");
        userFacadeImp.getRegistered().get("testuser").setAdmin(true);
    }

    @Test
    void testGetPurchaseHistory_ValidInput() {
        String result = paymentFacade.getPurchaseHistory("testuser", "store1", 1, null);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("store1"));
        assertFalse(result.contains("store2"));
    }

    @Test
    void testGetPurchaseHistory_ValidInput2() {
        String result = paymentFacade.getPurchaseHistory("testuser", "store1", 2, null);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("store1"));
        assertFalse(result.contains("store2"));
    }


    @Test
    void testGetStoresPurchaseHistory_ValidInput() {
        String result = paymentFacade.getStoresPurchaseHistory("testuser", "store1", 1, null);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }


    @Test
    void testGetPurchaseHistory_NotAdmin() {
        userFacadeImp.getRegistered().get("testuser").setAdmin(false);
        assertThrows(RuntimeException.class, () -> {
            paymentFacade.getPurchaseHistory("testuser", "store1", 1, null);
        });
    }


    @Test
    void testGetStoresPurchaseHistory_NotAdmin() {
        userFacadeImp.getRegistered().get("testuser").setAdmin(false);
        assertThrows(RuntimeException.class, () -> {
            paymentFacade.getStoresPurchaseHistory("testuser", "store1", 1, null);
        });
    }

    @Test
    void testGetPurchaseHistory_UserNotLogged() {
        userFacadeImp.getRegistered().get("testuser").logout();
        assertThrows(RuntimeException.class, () -> {
            paymentFacade.getPurchaseHistory("testuser", "store1", 1, null);
        });
    }

    @Test
    void testGetPurchaseHistory_UserNotFound() {
        assertThrows(RuntimeException.class, () -> {
            paymentFacade.getPurchaseHistory("invalidUser", null, null, null);
        });
    }

    @Test
    void testGetStoresPurchaseHistory_UserNotFound() {
        assertThrows(RuntimeException.class, () -> {
            paymentFacade.getStoresPurchaseHistory("invalidUser", null, null, null);
        });
    }

    @Test
    void testGetPurchaseHistory_InvalidStore() {
        assertThrows(RuntimeException.class, () -> {
            paymentFacade.getStoresPurchaseHistory("testuser", "invalidStore", 1, null);
        });
    }

    @Test
    void testGetPurchaseHistory_NullInputs() {
        assertThrows(RuntimeException.class, () -> {
            paymentFacade.getPurchaseHistory(null, null, null, null);
        });
    }

    @Test
    void testGetStoresPurchaseHistory_InvalidStore() {
        assertThrows(RuntimeException.class, () -> {
            paymentFacade.getStoresPurchaseHistory("testuser", "invalidStore", 1, null);
        });
    }

    @Test
    void testGetStoresPurchaseHistory_NullInputs() {
        assertThrows(RuntimeException.class, () -> {
            paymentFacade.getStoresPurchaseHistory(null, null, null, null);
        });
    }

    @Test
    void testGetPurchaseHistory_ValidStoreButNoPurchases() {
        // Arrange
        paymentFacade.getPurchases().clear(); // Clear purchases to simulate no purchases

        // Act
        String result = paymentFacade.getPurchaseHistory("testuser", "store1", 1, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }



}

