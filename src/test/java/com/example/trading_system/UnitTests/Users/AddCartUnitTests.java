package com.example.trading_system.UnitTests.Users;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.Store;
import com.example.trading_system.domain.stores.StoreDatabaseRepository;
import com.example.trading_system.domain.users.Cart;
import com.example.trading_system.domain.users.UserDatabaseRepository;
import com.example.trading_system.domain.users.UserFacadeImp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SpringBootTest
@Transactional
public class AddCartUnitTests {

    @Autowired
    private UserDatabaseRepository userRepository;

    @Autowired
    private StoreDatabaseRepository storeRepository;

    private UserFacadeImp userFacadeImp;

    @BeforeEach
    public void init() {
        userFacadeImp = UserFacadeImp.getInstance(mock(PaymentService.class), mock(DeliveryService.class), mock(NotificationSender.class), userRepository, storeRepository);
    }

    @AfterEach
    public void tearDown() {
        userFacadeImp.deleteInstance();
        userRepository.deleteInstance();
        storeRepository.deleteInstance();
    }

    @Test
    public void givenValidDetails_WhenAddToCart_ThenSuccess() {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;

        userRepository.addRegistered(username, "encrypted_password", LocalDate.now());
        userRepository.getUser(username).login();
        storeRepository.addStore(storeName, "description", username, 4.5);

        Store store = storeRepository.getStore(storeName);
        store.addProduct(productId, "ProductName", "ProductDescription", 10.0, 10, 4.5, 1, null);

        Cart shoppingCart = new Cart();
        userRepository.getUser(username).setCart(shoppingCart);

        assertDoesNotThrow(() -> userFacadeImp.addToCart(username, productId, storeName, quantity, 10.0));
//        assertEquals(quantity, shoppingCart.getShoppingBags().get(storeName).getProducts_list().get(productId).getQuantity());
    }

    @Test
    public void givenNullUsername_WhenAddToCart_ThenThrowException() {
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;

        assertThrows(IllegalArgumentException.class, () -> userFacadeImp.addToCart(null, productId, storeName, quantity, 1));
    }

    @Test
    public void givenEmptyUsername_WhenAddToCart_ThenThrowException() {
        String username = "";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;

        assertThrows(IllegalArgumentException.class, () -> userFacadeImp.addToCart(username, productId, storeName, quantity, 1));
    }

    @Test
    public void givenNonExistentUser_WhenAddToCart_ThenThrowException() {
        String username = "rNonExistentUser";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;

        assertThrows(NoSuchElementException.class, () -> userFacadeImp.addToCart(username, productId, storeName, quantity, 1));
    }

    @Test
    public void givenNullStoreName_WhenAddToCart_ThenThrowException() {
        String username = "rValidUser";
        int productId = 1;
        int quantity = 5;

        userRepository.addRegistered(username, "encrypted_password", LocalDate.now());

        assertThrows(IllegalArgumentException.class, () -> userFacadeImp.addToCart(username, productId, null, quantity, 1));
    }

    @Test
    public void givenEmptyStoreName_WhenAddToCart_ThenThrowException() {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "";
        int quantity = 5;

        userRepository.addRegistered(username, "encrypted_password", LocalDate.now());

        assertThrows(IllegalArgumentException.class, () -> userFacadeImp.addToCart(username, productId, storeName, quantity, 1));
    }

    @Test
    public void givenNonExistentStore_WhenAddToCart_ThenThrowException() {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "NonExistentStore";
        int quantity = 5;

        userRepository.addRegistered(username, "encrypted_password", LocalDate.now());

        assertThrows(NoSuchElementException.class, () -> userFacadeImp.addToCart(username, productId, storeName, quantity, 1));
    }

    @Test
    public void givenUserNotLoggedIn_WhenAddToCart_ThenThrowException() {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;

        userRepository.addRegistered(username, "encrypted_password", LocalDate.now());
        storeRepository.addStore(storeName, "description", username, 4.5);

        assertThrows(RuntimeException.class, () -> userFacadeImp.addToCart(username, productId, storeName, quantity, 1));
    }

    @Test
    public void givenInsufficientProductQuantity_WhenAddToCart_ThenThrowException() {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 10;

        userRepository.addRegistered(username, "encrypted_password", LocalDate.now());
        userRepository.getUser(username).login();
        storeRepository.addStore(storeName, "description", username, 4.5);

        Store store = storeRepository.getStore(storeName);
        store.addProduct(productId, "ProductName", "ProductDescription", 10.0, 5, 4.5, 1, null);

        Cart shoppingCart = new Cart();
        userRepository.getUser(username).setCart(shoppingCart);

        assertThrows(RuntimeException.class, () -> userFacadeImp.addToCart(username, productId, storeName, quantity, 10.0));
    }
}
