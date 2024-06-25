package com.example.trading_system.UnitTests.Users;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.*;
import com.example.trading_system.domain.users.*;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.mock;

public class AddCartUnitTests {
    MarketFacadeImp marketFacade;
    UserFacadeImp userFacadeImp;
    private UserRepository userRepository;
    private StoreRepository storeRepository;

    @BeforeEach
    public void init() {
        storeRepository= StoreMemoryRepository.getInstance();
        userRepository = UserMemoryRepository.getInstance();
        marketFacade = MarketFacadeImp.getInstance(storeRepository);
        userFacadeImp = UserFacadeImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class),mock(NotificationSender.class),userRepository,storeRepository);
    }

    @AfterEach
    public void tearDown() {
        marketFacade.deleteInstance();
        userFacadeImp.deleteInstance();
    }

    @Test
    public void givenValidDetails_WhenAddToCart_ThenSuccess() {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;

        // Setup data in singletons
        userRepository.addRegistered(username, "encrypted_password", LocalDate.now());
        userRepository.getUser(username).login();
        marketFacade.addStore(storeName, "description", username, 4.5);

        Store store = marketFacade.getStore(storeName);
        store.addProduct(productId, "ProductName", "ProductDescription", 10.0, 10, 4.5, 1, null);

        Cart shoppingCart = new Cart();
        userRepository.getUser(username).setCart(shoppingCart);

        Assertions.assertDoesNotThrow(() -> userFacadeImp.addToCart(username, productId, storeName, quantity));

        Assertions.assertEquals(quantity, shoppingCart.getShoppingBags().get(storeName).getProducts_list().get(productId).getQuantity());
    }

    @Test
    public void givenNullUsername_WhenAddToCart_ThenThrowException() {
        //String username = null;
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;

        Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.addToCart(null, productId, storeName, quantity));
    }

    @Test
    public void givenEmptyUsername_WhenAddToCart_ThenThrowException() {
        String username = "";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;

        Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.addToCart(username, productId, storeName, quantity));
    }

    @Test
    public void givenNonExistentUser_WhenAddToCart_ThenThrowException() {
        String username = "rNonExistentUser";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;

        Assertions.assertThrows(NoSuchElementException.class, () -> userFacadeImp.addToCart(username, productId, storeName, quantity));
    }

    @Test
    public void givenNullStoreName_WhenAddToCart_ThenThrowException() {
        String username = "rValidUser";
        int productId = 1;
        //String storeName = null;
        int quantity = 5;

        userRepository.addRegistered(username, "encrypted_password", LocalDate.now());

        Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.addToCart(username, productId, null, quantity));
    }

    @Test
    public void givenEmptyStoreName_WhenAddToCart_ThenThrowException() {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "";
        int quantity = 5;

        userRepository.addRegistered(username, "encrypted_password", LocalDate.now());

        Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.addToCart(username, productId, storeName, quantity));
    }

    @Test
    public void givenNonExistentStore_WhenAddToCart_ThenThrowException() {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "NonExistentStore";
        int quantity = 5;

        userRepository.addRegistered(username, "encrypted_password", LocalDate.now());

        Assertions.assertThrows(NoSuchElementException.class, () -> userFacadeImp.addToCart(username, productId, storeName, quantity));
    }

    @Test
    public void givenUserNotLoggedIn_WhenAddToCart_ThenThrowException() {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;

        userRepository.addRegistered(username, "encrypted_password", LocalDate.now());
        marketFacade.addStore(storeName, "description", username, 4.5);

        Assertions.assertThrows(RuntimeException.class, () -> userFacadeImp.addToCart(username, productId, storeName, quantity));
    }

    @Test
    public void givenInsufficientProductQuantity_WhenAddToCart_ThenThrowException() {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 10;

        userRepository.addRegistered(username, "encrypted_password", LocalDate.now());
        userRepository.getUser(username).login();
        marketFacade.addStore(storeName, "description", username, 4.5);

        Store store = marketFacade.getStore(storeName);
        store.addProduct(productId, "ProductName", "ProductDescription", 10.0, 5, 4.5, 1, null);

        Cart shoppingCart = new Cart();
        userRepository.getUser(username).setCart(shoppingCart);

        Assertions.assertThrows(RuntimeException.class, () -> userFacadeImp.addToCart(username, productId, storeName, quantity));
    }
}
