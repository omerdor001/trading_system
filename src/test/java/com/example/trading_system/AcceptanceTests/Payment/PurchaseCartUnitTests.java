package com.example.trading_system.AcceptanceTests.Payment;

import com.example.trading_system.domain.stores.*;
import com.example.trading_system.domain.users.*;
import com.example.trading_system.domain.externalservices.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PurchaseCartUnitTests {

    @Mock
    DeliveryService deliveryService;
    @Mock
    PaymentService paymentService;
    @Mock
    Timer timer;

    UserMemoryRepository userMemoryRepository;
    MarketFacadeImp marketFacade;
    UserFacadeImp userFacadeImp;
    StoreSalesHistory storeSalesHistory;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);

        // Clear singleton instances
        UserMemoryRepository.getInstance().deleteInstance();
        MarketFacadeImp.getInstance().deleteInstance();
        UserFacadeImp.getInstance().deleteInstance();
        StoreSalesHistory.getInstance().deleteInstance();

        // Re-instantiate singletons
        userMemoryRepository = UserMemoryRepository.getInstance();
        marketFacade = MarketFacadeImp.getInstance();
        storeSalesHistory = StoreSalesHistory.getInstance();
        userFacadeImp = UserFacadeImp.getInstance();
    }

    @AfterEach
    public void tearDown() {
        userFacadeImp.deleteInstance();
        userMemoryRepository.deleteInstance();
        marketFacade.deleteInstance();
        storeSalesHistory.deleteInstance();
    }

    @Test
    public void givenValidDetails_WhenPurchaseCart_ThenSuccess() throws Exception {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;
        String address = "SomeAddress";
        double totalPrice = 50.0;

        userMemoryRepository.addRegistered(username, "encrypted_password", LocalDate.now());
        userMemoryRepository.getUser(username).login();
        marketFacade.addStore(storeName, "description", new StorePolicy(), username, 4.5);
        Store store = marketFacade.getStore(storeName);
        store.addProduct(productId, storeName, "ProductName", "ProductDescription", 10.0, 10, 4.5, 1, null);
        Cart shoppingCart = new Cart();
        userMemoryRepository.getUser(username).setCart(shoppingCart);
        userFacadeImp.addToCart(username, productId, storeName, quantity);


        Assertions.assertEquals(marketFacade.calculateTotalPrice(shoppingCart),(totalPrice));
        when(deliveryService.makeDelivery(address)).thenReturn(1);
        when(paymentService.makePayment(totalPrice)).thenReturn(1);

        Assertions.assertDoesNotThrow(() -> userFacadeImp.purchaseCart(username));
    }

    @Test
    public void givenNullUsername_WhenPurchaseCart_ThenThrowException() {
        String username = null;

        Assertions.assertThrows(RuntimeException.class, () -> userFacadeImp.purchaseCart(username));
    }

    @Test
    public void givenEmptyUsername_WhenPurchaseCart_ThenThrowException() {
        String username = "";

        Assertions.assertThrows(RuntimeException.class, () -> userFacadeImp.purchaseCart(username));
    }

    @Test
    public void givenNonExistentUser_WhenPurchaseCart_ThenThrowException() {
        String username = "rNonExistentUser";

        Assertions.assertThrows(RuntimeException.class, () -> userFacadeImp.purchaseCart(username));
    }

    @Test
    public void givenUserNotLoggedIn_WhenPurchaseCart_ThenThrowException() {
        String username = "rValidUser";

        userMemoryRepository.addRegistered(username, "encrypted_password", LocalDate.now());

        Assertions.assertThrows(RuntimeException.class, () -> userFacadeImp.purchaseCart(username));
    }

    @Test
    public void givenEmptyCart_WhenPurchaseCart_ThenThrowException() {
        String username = "rValidUser";

        userMemoryRepository.addRegistered(username, "encrypted_password", LocalDate.now());
        userMemoryRepository.getUser(username).login();

        Assertions.assertThrows(RuntimeException.class, () -> userFacadeImp.purchaseCart(username));
    }

    @Test
    public void givenInsufficientProductQuantity_WhenPurchaseCart_ThenThrowException() {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 10;

        userMemoryRepository.addRegistered(username, "encrypted_password", LocalDate.now());
        userMemoryRepository.getUser(username).login();
        marketFacade.addStore(storeName, "description", new StorePolicy(), username, 4.5);
        Store store = marketFacade.getStore(storeName);
        store.addProduct(productId, storeName, "ProductName", "ProductDescription", 10.0, 5, 4.5, 1, null);
        Cart shoppingCart = new Cart();
        userMemoryRepository.getUser(username).setCart(shoppingCart);
        userFacadeImp.addToCart(username, productId, storeName, quantity); //TODO: yes this should Happen with error,
        //TODO but make sure how to remove quantity after it to make the test correctly

        Assertions.assertThrows(RuntimeException.class, () -> userFacadeImp.purchaseCart(username));
    }

    @Test
    public void givenDeliveryServiceFails_WhenPurchaseCart_ThenThrowException() throws Exception {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;
        String address = "SomeAddress";
        double totalPrice = 50.0;

        userMemoryRepository.addRegistered(username, "encrypted_password", LocalDate.now());
        userMemoryRepository.getUser(username).login();
        marketFacade.addStore(storeName, "description", new StorePolicy(), username, 4.5);
        Store store = marketFacade.getStore(storeName);
        store.addProduct(productId, storeName, "ProductName", "ProductDescription", 10.0, 10, 4.5, 1, null);
        Cart shoppingCart = new Cart();
        userMemoryRepository.getUser(username).setCart(shoppingCart);
        userFacadeImp.addToCart(username, productId, storeName, quantity);

//        when(marketFacade.calculateTotalPrice(shoppingCart)).thenReturn(totalPrice);
        Assertions.assertEquals(marketFacade.calculateTotalPrice(shoppingCart),(totalPrice));
        doThrow(new Exception("Delivery Service Error")).when(deliveryService).makeDelivery(address);

        Assertions.assertThrows(Exception.class, () -> userFacadeImp.purchaseCart(username));
    }

    @Test
    public void givenPaymentServiceFails_WhenPurchaseCart_ThenThrowException() throws Exception {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;
        String address = "SomeAddress";
        double totalPrice = 50.0;

        userMemoryRepository.addRegistered(username, "encrypted_password", LocalDate.now());
        userMemoryRepository.getUser(username).login();
        marketFacade.addStore(storeName, "description", new StorePolicy(), username, 4.5);
        Store store = marketFacade.getStore(storeName);
        store.addProduct(productId, storeName, "ProductName", "ProductDescription", 10.0, 10, 4.5, 1, null);
        Cart shoppingCart = new Cart();
        userMemoryRepository.getUser(username).setCart(shoppingCart);
        userFacadeImp.addToCart(username, productId, storeName, quantity);

//        when(marketFacade.calculateTotalPrice(shoppingCart)).thenReturn(totalPrice);
        Assertions.assertEquals(marketFacade.calculateTotalPrice(shoppingCart),(totalPrice));

        when(deliveryService.makeDelivery(address)).thenReturn(1);
        doThrow(new Exception("Payment Service Error")).when(paymentService).makePayment(totalPrice);

        Assertions.assertThrows(Exception.class, () -> userFacadeImp.purchaseCart(username));
    }
}
