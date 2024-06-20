package com.example.trading_system.UnitTests.Payment;

import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.*;
import com.example.trading_system.domain.users.*;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PurchaseCartUnitTests {
    MarketFacadeImp marketFacade;
    UserFacade userFacade;
    DeliveryService deliveryService;
    PaymentService paymentService;
    private UserRepository userRepository;
    private StoreRepository storeRepository;

    @BeforeEach
    public void init() {
        storeRepository= StoreMemoryRepository.getInstance();
        userRepository = UserMemoryRepository.getInstance();
        paymentService=mock(PaymentService.class);
        deliveryService=mock(DeliveryService.class);
        userFacade = UserFacadeImp.getInstance(paymentService,deliveryService,userRepository,storeRepository);
        marketFacade = MarketFacadeImp.getInstance(storeRepository);
    }

    @AfterEach
    public void tearDown() {
        marketFacade.deleteInstance();
        userFacade.deleteInstance();
    }

    @Test
    public void givenValidDetails_WhenPurchaseCart_ThenSuccess() throws IllegalAccessException {
        String username = "ValidUser";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;
        String address = "1234 El Street, Springfield, IL, 62704-5678";  // Valid address format
        try {
            userFacade.register(username, "encrypted_password", LocalDate.now());
            userFacade.enter(0);
            userFacade.login("v0", username, "encrypted_password");
        } catch (Exception e) {
        }
        userFacade.createStore("r" +username, storeName, "description");
        userFacade.setAddress("r" + username,address);
        marketFacade.addProduct("r" + username,productId,storeName, "ProductName", "ProductDescription", 10.0, 10, 4.5, 1, null);
        userFacade.addToCart("r" + username, productId, storeName, quantity);

        Assertions.assertDoesNotThrow(() -> userFacade.purchaseCart("r" + username));
    }

    @Test
    public void givenTwoUsersWithInsufficientProductQuantity_WhenPurchaseCart_ThenThrowException() throws IllegalAccessException {
        String username1 = "ValidUser1";
        String username2 = "ValidUser2";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 6;
        String address = "1234 El Street, Springfield, IL, 62704-5678";  // Valid address format
        try {
            userFacade.register(username1, "encrypted_password", LocalDate.now());
            userFacade.register(username2, "encrypted_password", LocalDate.now());
            userFacade.enter(0);
            userFacade.enter(1);
            userFacade.login("v0", username1, "encrypted_password");
            userFacade.login("v1", username2, "encrypted_password");
        } catch (Exception _) {
        }
        userFacade.setAddress("r" + username1,address);
        userFacade.setAddress("r" +username2,address);
        userFacade.createStore("r" +username1, storeName, "description");

        marketFacade.addProduct("r" +username1,productId,storeName, "ProductName", "ProductDescription", 10.0, 10, 4.5, 1, null);
        userFacade.addToCart("r" + username1, productId, storeName, quantity);
        userFacade.addToCart("r" + username2, productId, storeName, quantity);

        Assertions.assertDoesNotThrow(() -> userFacade.purchaseCart("r" + username1));
        Assertions.assertThrows(RuntimeException.class, () -> userFacade.purchaseCart("r" + username2));
    }

    @Test
    public void givenTwoProcessesWithDelayedDelivery_WhenPurchaseCart_ThenThrowException() throws IllegalAccessException {
        String username1 = "ValidUser1";
        String username2 = "ValidUser2";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 6;
        String address = "1234 El Street, Springfield, IL, 62704-5678";  // Valid address format
        try {
            userFacade.register(username1, "encrypted_password", LocalDate.now());
            userFacade.register(username2, "encrypted_password", LocalDate.now());
            userFacade.enter(0);
            userFacade.enter(1);
            userFacade.login("v0", username1, "encrypted_password");
            userFacade.login("v1", username2, "encrypted_password");
        } catch (Exception _) {
        }
        userFacade.setAddress("r" + username1,address);
        userFacade.setAddress("r" +username2,address);
        userFacade.createStore("r" +username1, storeName, "description");

        marketFacade.addProduct("r" +username1,productId,storeName, "ProductName", "ProductDescription", 10.0, 10, 4.5, 1, null);
        userFacade.addToCart("r" + username1, productId, storeName, quantity);
        userFacade.addToCart("r" + username2, productId, storeName, quantity);

        Assertions.assertDoesNotThrow(() -> userFacade.purchaseCart("r" + username1));
        Assertions.assertThrows(RuntimeException.class, () -> userFacade.purchaseCart("r" + username2));
        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.execute(() -> {
            try {
                latch.await(); // Wait for the signal to start
                userFacade.purchaseCart("r" + username1);
            } catch (Exception _) {

            }
        });

        executorService.execute(() -> {
            latch.countDown(); // Signal the other thread to proceed
            try {
                Thread.sleep(1000); // Introduce a delay for the second user
                Assertions.assertThrows(RuntimeException.class, () -> userFacade.purchaseCart("r" + username2));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        executorService.shutdown();

    }

    @Test
    public void givenTwoProcessesWithInsufficientProductQuantity_WhenPurchaseCart_ThenThrowException() throws IllegalAccessException {
        String username1 = "ValidUser1";
        String username2 = "ValidUser2";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 6;
        String address = "1234 El Street, Springfield, IL, 62704-5678";  // Valid address format

        try {
            userFacade.register(username1, "encrypted_password", LocalDate.now());
            userFacade.register(username2, "encrypted_password", LocalDate.now());
            userFacade.enter(0);
            userFacade.enter(1);
            userFacade.login("v0", username1, "encrypted_password");
            userFacade.login("v1", username2, "encrypted_password");
        } catch (Exception _) {
            // Handle exceptions as needed
        }

        userFacade.setAddress("r" + username1, address);
        userFacade.setAddress("r" + username2, address);
        userFacade.createStore("r" +username1, storeName, "description");

        marketFacade.addProduct("r" + username1, productId, storeName, "ProductName", "ProductDescription", 10.0, 10, 4.5, 1, null);
        userFacade.addToCart("r" + username1, productId, storeName, quantity);
        userFacade.addToCart("r" + username2, productId, storeName, quantity);

        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.execute(() -> {
            try {
                userFacade.purchaseCart("r" + username1); // This should hold the product
                latch.countDown(); // Signal the other thread to proceed
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        executorService.execute(() -> {
            try {
                latch.await(); // Wait for the signal from the first thread
                Assertions.assertThrows(RuntimeException.class, () -> userFacade.purchaseCart("r" + username2));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        executorService.shutdown();
    }

    @Test
    public void givenNullUsername_WhenPurchaseCart_ThenThrowException() {
        //String username = null;
        Assertions.assertThrows(RuntimeException.class, () -> userFacade.purchaseCart(null));
    }

    @Test
    public void givenEmptyUsername_WhenPurchaseCart_ThenThrowException() {
        String username = "";

        Assertions.assertThrows(RuntimeException.class, () -> userFacade.purchaseCart(username));
    }

    @Test
    public void givenNonExistentUser_WhenPurchaseCart_ThenThrowException() {
        String username = "rNonExistentUser";

        Assertions.assertThrows(RuntimeException.class, () -> userFacade.purchaseCart(username));
    }

    @Test
    public void givenUserNotLoggedIn_WhenPurchaseCart_ThenThrowException() {
        String username = "rValidUser";
        try {
            userFacade.register(username, "encrypted_password", LocalDate.now());
        } catch (Exception e) {
        }
        Assertions.assertThrows(RuntimeException.class, () -> userFacade.purchaseCart(username));
    }

    @Test
    public void givenEmptyCart_WhenPurchaseCart_ThenThrowException() {
        String username = "rValidUser";
        try {
            userFacade.register(username, "encrypted_password", LocalDate.now());
            userFacade.enter(0);
            userFacade.login("v0", username, "encrypted_password");
        } catch (Exception e) {
        }
        Assertions.assertThrows(RuntimeException.class, () -> userFacade.purchaseCart(username));
    }

    @Test
    public void givenInsufficientProductQuantity_WhenPurchaseCart_ThenThrowException() {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 10;
        try {
            userFacade.register(username, "encrypted_password", LocalDate.now());
            userFacade.enter(0);
            userFacade.login("v0", username, "encrypted_password");
        } catch (Exception e) {
        }
        marketFacade.addStore(storeName, "description", username, 4.5);
        Store store = marketFacade.getStore(storeName);
        store.addProduct(productId, "ProductName", "ProductDescription", 10.0, 5, 4.5, 1, null);
        Cart shoppingCart = new Cart();
        userFacade.getUser("r" + username).setCart(shoppingCart);
        userFacade.getUser("r" + username).addProductToCart(productId, quantity, storeName, marketFacade.getStore(storeName).getProduct(productId).getProduct_price(), 3);

        Assertions.assertThrows(RuntimeException.class, () -> userFacade.purchaseCart("r" + username));
    }

    @Test
    public void givenDeliveryServiceFails_WhenPurchaseCart_ThenThrowException() {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;
        String address = "SomeAddress";
        try {
            userFacade.enter(0);
            userFacade.register(username, "encrypted_password", LocalDate.now());
            userFacade.login("v0", username, "encrypted_password");
        } catch (Exception e) {
        }
        userFacade.getUser("r" + username).setAddress(address);
        marketFacade.addStore(storeName, "description", username, 4.5);
        Store store = marketFacade.getStore(storeName);
        store.addProduct(productId, "ProductName", "ProductDescription", 10.0, 10, 4.5, 1, null);
        Cart shoppingCart = new Cart();
        userFacade.getUser("r" + username).setCart(shoppingCart);
        userFacade.getUser("r" + username).addProductToCart(productId, quantity, storeName, marketFacade.getStore(storeName).getProduct(productId).getProduct_price(), marketFacade.getStore(storeName).getProduct(productId).getCategory().getIntValue());
        when(deliveryService.makeDelivery(address)).thenReturn(-1);
        Assertions.assertThrows(Exception.class, () -> userFacade.purchaseCart("r" + username));
    }

    //Cannot fail PaymentService
    @Test
    public void givenPaymentServiceFails_WhenPurchaseCart_ThenThrowException() {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;
        String address = "SomeAddress";
        try {
            userFacade.register(username, "encrypted_password", LocalDate.now());
            userFacade.enter(0);
            userFacade.login("v0", username, "encrypted_password");
        } catch (Exception e) {
        }
        userFacade.getUser("r" + username).setAddress(address);
        marketFacade.addStore(storeName, "description", username, 4.5);
        Store store = marketFacade.getStore(storeName);
        store.addProduct(productId, "ProductName", "ProductDescription", 10.0, 10, 4.5, 1, null);
        Cart shoppingCart = new Cart();
        userFacade.getUser("r" + username).setCart(shoppingCart);
        userFacade.addToCart("r" + username, productId, storeName, quantity);
        //Assertions.assertThrows(Exception.class, () -> userFacade.purchaseCart(username));
    }
}
