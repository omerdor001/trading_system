package com.example.trading_system.UnitTests.Payment;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.*;
import com.example.trading_system.domain.users.*;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PurchaseCartUnitTests {

    MarketFacadeImp marketFacade;
    UserFacade userFacade;
    @Mock
    DeliveryService deliveryService;
    @Mock
    PaymentService paymentService;
    private UserRepository userRepository;
    private StoreRepository storeRepository;

    // Define common test variables
    private final String validUsername = "ValidUser";
    private final String validUsername1 = "ValidUser1";
    private final String validUsername2 = "ValidUser2";
    private final String encryptedPassword = "encrypted_password";
    private final int productId = 1;
    private final String storeName = "StoreName";
    private final int quantity = 5;
    private final String address = "1234 El Street, Springfield, IL, 62704-5678";  // Valid address format

    // Payment details
    private final String amount = "100.00";
    private final String currency = "USD";
    private final String cardNumber = "4111111111111111";
    private final String month = "12";
    private final String year = "2025";
    private final String holder = "John Doe";
    private final String ccv = "123";
    private final String id = "123456789";

    @BeforeEach
    public void init() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        // Re-instantiate singletons
        storeRepository = StoreMemoryRepository.getInstance();
        userRepository = UserMemoryRepository.getInstance();
        marketFacade = spy(MarketFacadeImp.getInstance(storeRepository)); // Manually create the spy
        java.lang.reflect.Field instance = MarketFacadeImp.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, marketFacade);

        paymentService = mock(PaymentService.class);
        deliveryService = mock(DeliveryService.class);
        userFacade = UserFacadeImp.getInstance(paymentService, deliveryService, mock(NotificationSender.class), userRepository, storeRepository);
    }

    @AfterEach
    public void tearDown() throws NoSuchFieldException, IllegalAccessException {
        userFacade.deleteInstance();
        marketFacade.deleteInstance();
        storeRepository.deleteInstance();
        userRepository.deleteInstance();

        java.lang.reflect.Field instance = MarketFacadeImp.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void givenValidDetails_WhenPurchaseCart_ThenSuccess() throws IllegalAccessException {
        try {
            userFacade.register(validUsername, encryptedPassword, LocalDate.now());
            userFacade.enter(0);
            userFacade.login("v0", validUsername, encryptedPassword);
        } catch (Exception ignored) {
        }
        userFacade.createStore("r" + validUsername, storeName, "description");
        userFacade.setAddress("r" + validUsername, address);
        marketFacade.addProduct("r" + validUsername, productId, storeName, "ProductName", "ProductDescription", 10.0, 10, 4.5, 1, null);
        userFacade.addToCart("r" + validUsername, productId, storeName, quantity, 10.0);

        Assertions.assertDoesNotThrow(() -> userFacade.purchaseCart("r" + validUsername, address, amount, currency, cardNumber, month, year, holder, ccv, id));
        assertFalse(marketFacade.getAllHistoryPurchases("r" + validUsername, storeName).isEmpty());
        assertTrue(marketFacade.getAllHistoryPurchases("r" + validUsername, storeName).contains("\"storeId\":\"StoreName\",\"id\":1"));
        assertEquals(5, marketFacade.getStore(storeName).getProduct(productId).getProduct_quantity());
        assertTrue(userFacade.getUser("r" + validUsername).isTimerCancelled());
    }

    @Test
    @Disabled
    //TODO fix me
    public void givenTwoUsersWithInsufficientProductQuantity_WhenPurchaseCart_ThenThrowException() throws IllegalAccessException {
        try {
            userFacade.register(validUsername1, encryptedPassword, LocalDate.now());
            userFacade.register(validUsername2, encryptedPassword, LocalDate.now());
            userFacade.enter(0);
            userFacade.enter(1);
            userFacade.login("v0", validUsername1, encryptedPassword);
            userFacade.login("v1", validUsername2, encryptedPassword);
        } catch (Exception ignored) {
        }
        userFacade.setAddress("r" + validUsername1, address);
        userFacade.setAddress("r" + validUsername2, address);
        userFacade.createStore("r" + validUsername1, storeName, "description");

        marketFacade.addProduct("r" + validUsername1, productId, storeName, "ProductName", "ProductDescription", 10.0, 10, 4.5, 1, null);
        userFacade.addToCart("r" + validUsername1, productId, storeName, quantity, 10.0);
        userFacade.addToCart("r" + validUsername2, productId, storeName, quantity, 10.0);

        Assertions.assertDoesNotThrow(() -> userFacade.purchaseCart("r" + validUsername1, address, amount, currency, cardNumber, month, year, holder, ccv, id));
        Assertions.assertThrows(RuntimeException.class, () -> userFacade.purchaseCart("r" + validUsername2, address, amount, currency, cardNumber, month, year, holder, ccv, id));
        assertFalse(marketFacade.getAllHistoryPurchases("r" + validUsername1, storeName).isEmpty());
        assertTrue(marketFacade.getAllHistoryPurchases("r" + validUsername1, storeName).contains("\"storeId\":\"StoreName\",\"id\":1"));
        assertEquals(4, marketFacade.getStore(storeName).getProduct(productId).getProduct_quantity());
        assertTrue(userFacade.getUser("r" + validUsername1).isTimerCancelled());
        assertTrue(userFacade.getUser("r" + validUsername2).isTimerCancelled());
    }

    @Test
    @Disabled
    //TODO fix me
    public void givenTwoProcessesWithDelayedDelivery_WhenPurchaseCart_ThenThrowException() throws IllegalAccessException, InterruptedException {
        try {
            userFacade.register(validUsername1, encryptedPassword, LocalDate.now());
            userFacade.register(validUsername2, encryptedPassword, LocalDate.now());
            userFacade.enter(0);
            userFacade.enter(1);
            userFacade.login("v0", validUsername1, encryptedPassword);
            userFacade.login("v1", validUsername2, encryptedPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        userFacade.setAddress("r" + validUsername1, address);
        userFacade.setAddress("r" + validUsername2, address);
        userFacade.createStore("r" + validUsername1, storeName, "description");

        marketFacade.addProduct("r" + validUsername1, productId, storeName, "ProductName", "ProductDescription", 10.0, 10, 4.5, 1, null);
        userFacade.addToCart("r" + validUsername1, productId, storeName, quantity, 10.0);
        userFacade.addToCart("r" + validUsername2, productId, storeName, quantity, 10.0);

        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        final boolean[] purchase1Success = {false};
        final boolean[] purchase2Success = {false};

        executorService.execute(() -> {
            try {
                latch.await();
                userFacade.purchaseCart("r" + validUsername1, address, amount, currency, cardNumber, month, year, holder, ccv, id);
                purchase1Success[0] = true;
                System.out.println("User1 purchase succeeded.");
            } catch (Exception e) {
                System.out.println("User1 purchase failed.");
            }
        });

        executorService.execute(() -> {
            try {
                latch.await();
                Thread.sleep(100); // Introduce a small delay for the second user
                userFacade.purchaseCart("r" + validUsername2, address, amount, currency, cardNumber, month, year, holder, ccv, id);
                purchase2Success[0] = true;
                System.out.println("User2 purchase succeeded.");
            } catch (Exception e) {
                System.out.println("User2 purchase failed.");
            }
        });

        latch.countDown(); // Signal both threads to proceed

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        assertFalse(marketFacade.getAllHistoryPurchases("r" + validUsername1, storeName).isEmpty());
        assertEquals(4, marketFacade.getStore(storeName).getProduct(productId).getProduct_quantity());
        assertTrue(purchase1Success[0] ^ purchase2Success[0]); // One should succeed, one should fail
        assertTrue(userFacade.getUser("r" + validUsername1).isTimerCancelled());
        assertTrue(userFacade.getUser("r" + validUsername2).isTimerCancelled());
    }

    @Test
    @Disabled
    //TODO fix me
    public void givenTwoProcessesWithInsufficientProductQuantity_WhenPurchaseCart_ThenOneThrowsException() throws Exception {
        try {
            userFacade.register(validUsername1, encryptedPassword, LocalDate.now());
            userFacade.register(validUsername2, encryptedPassword, LocalDate.now());
            userFacade.enter(0);
            userFacade.enter(1);
            userFacade.login("v0", validUsername1, encryptedPassword);
            userFacade.login("v1", validUsername2, encryptedPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        userFacade.setAddress("r" + validUsername1, address);
        userFacade.setAddress("r" + validUsername2, address);
        userFacade.createStore("r" + validUsername1, storeName, "description");

        marketFacade.addProduct("r" + validUsername1, productId, storeName, "ProductName", "ProductDescription", 10.0, 10, 4.5, 1, null);
        userFacade.addToCart("r" + validUsername1, productId, storeName, quantity, 10.0);
        userFacade.addToCart("r" + validUsername2, productId, storeName, quantity, 10.0);

        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        final boolean[] purchase1Success = {false};
        final boolean[] purchase2Success = {false};

        executorService.execute(() -> {
            try {
                latch.await();
                userFacade.purchaseCart("r" + validUsername1, address, amount, currency, cardNumber, month, year, holder, ccv, id);
                purchase1Success[0] = true;
            } catch (Exception e) {
                System.out.println("User1 purchase failed.");
            }
        });

        executorService.execute(() -> {
            try {
                latch.await();
                userFacade.purchaseCart("r" + validUsername2, address, amount, currency, cardNumber, month, year, holder, ccv, id);
                purchase2Success[0] = true;
            } catch (Exception e) {
                System.out.println("User2 purchase failed.");
            }
        });

        latch.countDown(); // Signal both threads to proceed

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
        assertFalse(marketFacade.getAllHistoryPurchases("r" + validUsername1, storeName).isEmpty());
        assertEquals(4, marketFacade.getStore(storeName).getProduct(productId).getProduct_quantity());
        assertTrue(purchase1Success[0] ^ purchase2Success[0]); // One should succeed, one should fail
        assertTrue(userFacade.getUser("r" + validUsername1).isTimerCancelled());
        assertTrue(userFacade.getUser("r" + validUsername2).isTimerCancelled());
    }

    @Test
    public void givenNullUsername_WhenPurchaseCart_ThenThrowException() {
        Assertions.assertThrows(RuntimeException.class, () -> userFacade.purchaseCart(null, address, amount, currency, cardNumber, month, year, holder, ccv, id));
    }

    @Test
    public void givenEmptyUsername_WhenPurchaseCart_ThenThrowException() {
        String emptyUsername = "";
        Assertions.assertThrows(RuntimeException.class, () -> userFacade.purchaseCart(emptyUsername, address, amount, currency, cardNumber, month, year, holder, ccv, id));
    }

    @Test
    public void givenNonExistentUser_WhenPurchaseCart_ThenThrowException() {
        String nonExistentUsername = "rNonExistentUser";
        Assertions.assertThrows(RuntimeException.class, () -> userFacade.purchaseCart(nonExistentUsername, address, amount, currency, cardNumber, month, year, holder, ccv, id));
    }

    @Test
    public void givenUserNotLoggedIn_WhenPurchaseCart_ThenThrowException() {
        try {
            userFacade.register(validUsername, encryptedPassword, LocalDate.now());
        } catch (Exception ignored) {
        }
        Assertions.assertThrows(RuntimeException.class, () -> userFacade.purchaseCart(validUsername, address, amount, currency, cardNumber, month, year, holder, ccv, id));
    }

    @Test
    public void givenEmptyCart_WhenPurchaseCart_ThenThrowException() {
        try {
            userFacade.register(validUsername, encryptedPassword, LocalDate.now());
            userFacade.enter(0);
            userFacade.login("v0", validUsername, encryptedPassword);
        } catch (Exception ignored) {
        }
        Assertions.assertThrows(RuntimeException.class, () -> userFacade.purchaseCart(validUsername, address, amount, currency, cardNumber, month, year, holder, ccv, id));
    }

    @Test
    public void givenInsufficientProductQuantity_WhenPurchaseCart_ThenThrowException() throws IllegalAccessException {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 10;
        try {
            userFacade.register(username, "encrypted_password", LocalDate.now());
            userFacade.enter(0);
            userFacade.login("v0", username, "encrypted_password");
        } catch (Exception ignored) {
        }
        marketFacade.addStore(storeName, "description", username, 4.5);
        Store store = marketFacade.getStore(storeName);
        store.addProduct(productId, "ProductName", "ProductDescription", 10.0, 5, 4.5, 1, null);
        Cart shoppingCart = new Cart();
        userFacade.getUser("r" + username).setCart(shoppingCart);
        userFacade.getUser("r" + username).addProductToCart(productId, quantity, storeName, marketFacade.getStore(storeName).getProduct(productId).getProduct_price(), 3);
        Assertions.assertThrows(RuntimeException.class, () -> userFacade.purchaseCart(validUsername, address, amount, currency, cardNumber, month, year, holder, ccv, id));
        assertTrue(marketFacade.getAllHistoryPurchases("r" + username,storeName).isEmpty());
        assertEquals(5, marketFacade.getStore(storeName).getProduct(productId).getProduct_quantity());
        assertTrue(userFacade.getUser("r" + username).isTimerCancelled());

    }

    @Test
    public void givenNotValidatedPolicy_WhenPurchaseCart_ThenThrowException() throws IOException, IllegalAccessException {
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
            fail("unexpected error: " + e.getMessage());
        }
        userFacade.getUser("r" + username).setAddress(address);
        marketFacade.addStore(storeName, "description", username, 4.5);
        Store store = marketFacade.getStore(storeName);
        store.addProduct(productId, "ProductName", "ProductDescription", 10.0, 10, 4.5, 1, null);
        Cart shoppingCart = new Cart();
        User user = userFacade.getUser("r" + username);
        user.setCart(shoppingCart);
        user.addProductToCart(productId, quantity, storeName, marketFacade.getStore(storeName).getProduct(productId).getProduct_price(), marketFacade.getStore(storeName).getProduct(productId).getCategory().getIntValue());

        doThrow(new RuntimeException()).when(marketFacade).validatePurchasePolicies(any(), anyInt());
        Assertions.assertThrows(RuntimeException.class, () -> userFacade.purchaseCart(validUsername, address, amount, currency, cardNumber, month, year, holder, ccv, id));
        assertTrue(marketFacade.getAllHistoryPurchases("r" + username,storeName).isEmpty());
        assertEquals(10, marketFacade.getStore(storeName).getProduct(productId).getProduct_quantity());
        assertTrue(userFacade.getUser("r" + username).isTimerCancelled());

    }

    @Test
    public void givenDeliveryServiceFails_WhenPurchaseCart_ThenThrowException() throws IllegalAccessException {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;
        String address = "SomeAddress";
        try {
            userFacade.enter(0);
            userFacade.register(username, "encrypted_password", LocalDate.now());
            userFacade.login("v0", username, "encrypted_password");
        } catch (Exception ignored) {
        }
        userFacade.getUser("r" + username).setAddress(address);
        marketFacade.addStore(storeName, "description", username, 4.5);
        Store store = marketFacade.getStore(storeName);
        store.addProduct(productId, "ProductName", "ProductDescription", 10.0, 5, 4.5, 1, null);
        Cart shoppingCart = new Cart();
        userFacade.getUser("r" + username).setCart(shoppingCart);
        userFacade.getUser("r" + username).addProductToCart(productId, quantity, storeName, marketFacade.getStore(storeName).getProduct(productId).getProduct_price(), marketFacade.getStore(storeName).getProduct(productId).getCategory().getIntValue());
        when(deliveryService.makeDelivery(address)).thenReturn(-1);
        Assertions.assertThrows(Exception.class, () -> userFacade.purchaseCart(validUsername, address, amount, currency, cardNumber, month, year, holder, ccv, id));
        assertTrue(marketFacade.getAllHistoryPurchases("r" + username,storeName).isEmpty());
        assertEquals(5, marketFacade.getStore(storeName).getProduct(productId).getProduct_quantity());
        assertTrue(userFacade.getUser("r" + username).isTimerCancelled());

    }


    //Cannot fail PaymentService
    @Test
    public void givenPaymentServiceFail1_WhenPurchaseCart_ThenThrowException() throws IllegalAccessException {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;
        String address = "SomeAddress";
        try {
            userFacade.register(username, "encrypted_password", LocalDate.now());
            userFacade.enter(0);
            userFacade.login("v0", username, "encrypted_password");
            userFacade.createStore("r" +username, storeName, "description");
            userFacade.setAddress("r" + username,address);
            marketFacade.addProduct("r" + username,productId,storeName, "ProductName", "ProductDescription", 10.0, 10, 4.5, 1, null);
            userFacade.addToCart("r" + username, productId, storeName, quantity, 10.0);
        } catch (Exception e) {
            fail("unexpected error: " + e.getMessage());
        }

        Class<Double> number = Double.class;
        when(paymentService.makePayment(anyDouble(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(-1);
        Assertions.assertThrows(Exception.class,() -> userFacade.purchaseCart(validUsername, address, amount, currency, cardNumber, month, year, holder, ccv, id));
        assertTrue(marketFacade.getAllHistoryPurchases("r" + username,storeName).isEmpty());
        assertEquals(10, marketFacade.getStore(storeName).getProduct(productId).getProduct_quantity());
        assertTrue(userFacade.getUser("r" + username).isTimerCancelled());

    }

    @Test
    public void givenPaymentServiceFail2_WhenPurchaseCart_ThenThrowException() throws IllegalAccessException {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;
        String address = "SomeAddress";
        try {
            userFacade.register(username, "encrypted_password", LocalDate.now());
            userFacade.enter(0);
            userFacade.login("v0", username, "encrypted_password");
            userFacade.createStore("r" +username, storeName, "description");
            userFacade.setAddress("r" + username,address);
            marketFacade.addProduct("r" + username,productId,storeName, "ProductName", "ProductDescription", 10.0, 10, 4.5, 1, null);
            userFacade.addToCart("r" + username, productId, storeName, quantity, 10.0);
        } catch (Exception e) {
            fail("unexpected error: " + e.getMessage());
        }

        Class<Double> number = Double.class;
        when(paymentService.makePayment(anyDouble(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenThrow(RuntimeException.class);
        Assertions.assertThrows(Exception.class,() -> userFacade.purchaseCart(validUsername, address, amount, currency, cardNumber, month, year, holder, ccv, id));
        assertTrue(marketFacade.getAllHistoryPurchases("r" + username,storeName).isEmpty());
        assertEquals(10, marketFacade.getStore(storeName).getProduct(productId).getProduct_quantity());
        assertTrue(userFacade.getUser("r" + username).isTimerCancelled());

    }
    @Test
    @Disabled
    //TODO fix me
    public void givenTwoProcessesWithSufficientProductQuantity_WhenPurchaseCart_ThenBothSucceed() throws IllegalAccessException, InterruptedException {
        String username1 = "ValidUser1";
        String username2 = "ValidUser2";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 4;
        String address = "1234 El Street, Springfield, IL, 62704-5678";  // Valid address format

        try {
            userFacade.register(username1, "encrypted_password", LocalDate.now());
            userFacade.register(username2, "encrypted_password", LocalDate.now());
            userFacade.enter(0);
            userFacade.enter(1);
            userFacade.login("v0", username1, "encrypted_password");
            userFacade.login("v1", username2, "encrypted_password");
        } catch (Exception e) {
            e.printStackTrace();
        }

        userFacade.setAddress("r" + username1, address);
        userFacade.setAddress("r" + username2, address);
        userFacade.createStore("r" + username1, storeName, "description");
        marketFacade.addProduct("r" + username1, productId, storeName, "ProductName", "ProductDescription", 10.0, 10, 4.5, 1, null);
        userFacade.addToCart("r" + username1, productId, storeName, quantity, 10.0);
        userFacade.addToCart("r" + username2, productId, storeName, quantity, 10.0);

        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        final boolean[] purchase1Success = {false};
        final boolean[] purchase2Success = {false};

        executorService.execute(() -> {
            try {
                latch.await();
                userFacade.purchaseCart(validUsername, address, amount, currency, cardNumber, month, year, holder, ccv, id);
                purchase1Success[0] = true;
                System.out.println("User1 purchase succeeded.");
            } catch (Exception e) {
                System.out.println("User1 purchase failed.");
            }
        });

        executorService.execute(() -> {
            try {
                latch.await();
                userFacade.purchaseCart(validUsername, address, amount, currency, cardNumber, month, year, holder, ccv, id);
                purchase2Success[0] = true;
                System.out.println("User2 purchase succeeded.");
            } catch (Exception e) {
                System.out.println("User2 purchase failed.");
            }
        });

        latch.countDown(); // Signal both threads to proceed

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        assertTrue(purchase1Success[0]);
        assertTrue(purchase2Success[0]);
        assertEquals(2, marketFacade.getStore(storeName).getProduct(productId).getProduct_quantity());

        // Check that timer is stopped
        assertFalse(marketFacade.getAllHistoryPurchases("r" + username1,storeName).isEmpty());
        assertTrue(marketFacade.getAllHistoryPurchases("r" + username1,storeName).contains("\"storeId\":\"StoreName\",\"id\":1"));
        assertTrue(marketFacade.getAllHistoryPurchases("r" + username1,storeName).contains("\"customerUsername\":\"rValidUser1\""));
        assertTrue(marketFacade.getAllHistoryPurchases("r" + username1,storeName).contains("\"customerUsername\":\"rValidUser2\""));
        assertTrue(userFacade.getUser("r" + username1).isTimerCancelled());
        assertTrue(userFacade.getUser("r" + username2).isTimerCancelled());

    }

}
