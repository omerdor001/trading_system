package com.example.trading_system.UnitTests.users;

import com.example.trading_system.domain.stores.MarketFacadeImp;
import com.example.trading_system.domain.stores.Product;
import com.example.trading_system.domain.stores.Store;
import com.example.trading_system.domain.users.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.NoSuchElementException;

@ExtendWith(MockitoExtension.class)
public class RemoveFromCartUnitTests {
    @Mock
    User user;
    @Mock
    Cart shoppingCart;
    @Mock
    Product product;

    UserMemoryRepository userMemoryRepository;
    MarketFacadeImp marketFacade;
    UserFacadeImp userFacadeImp;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);

        // Clear singleton instances
        UserFacadeImp.getInstance().deleteInstance();

        // Re-instantiate singletons
        userMemoryRepository = UserMemoryRepository.getInstance();
        marketFacade = MarketFacadeImp.getInstance();
        userFacadeImp = UserFacadeImp.getInstance();
    }

    @AfterEach
    public void tearDown() {
        userFacadeImp.deleteInstance();
    }

    @Test
    public void givenValidDetails_WhenRemoveFromCart_ThenSuccess() {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;


        userMemoryRepository.addRegistered(username, "encrypted_password", LocalDate.now());
        userMemoryRepository.getUser(username).login();
        marketFacade.addStore(storeName, "description", username, 4.5);

        Store store = marketFacade.getStore(storeName);
        store.addProduct(productId, "ProductName", "ProductDescription", 10.0, 10, 4.5, 1, null);

        Cart shoppingCart = new Cart();
        userMemoryRepository.getUser(username).setCart(shoppingCart);
        shoppingCart.addProductToCart(productId, quantity, storeName, 10.0, 1);

        Assertions.assertDoesNotThrow(() -> userFacadeImp.removeFromCart(username, productId, storeName, quantity));

    }

    @Test
    public void givenNullUsername_WhenRemoveFromCart_ThenThrowException() {
        String username = null;
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;

        Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.removeFromCart(username, productId, storeName, quantity));
    }

    @Test
    public void givenEmptyUsername_WhenRemoveFromCart_ThenThrowException() {
        String username = "";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;

        Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.removeFromCart(username, productId, storeName, quantity));
    }

    @Test
    public void givenNonExistentUser_WhenRemoveFromCart_ThenThrowException() {
        String username = "rNonExistentUser";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;

        Assertions.assertThrows(NoSuchElementException.class, () -> userFacadeImp.removeFromCart(username, productId, storeName, quantity));
    }

    @Test
    public void givenNullStoreName_WhenRemoveFromCart_ThenThrowException() {
        String username = "rValidUser";
        int productId = 1;
        String storeName = null;
        int quantity = 5;

        userMemoryRepository.addRegistered(username, "encrypted_password", LocalDate.now());

        Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.removeFromCart(username, productId, storeName, quantity));
    }

    @Test
    public void givenEmptyStoreName_WhenRemoveFromCart_ThenThrowException() {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "";
        int quantity = 5;

        userMemoryRepository.addRegistered(username, "encrypted_password", LocalDate.now());

        Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.removeFromCart(username, productId, storeName, quantity));
    }

    @Test
    public void givenNonExistentStore_WhenRemoveFromCart_ThenThrowException() {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "NonExistentStore";
        int quantity = 5;

        userMemoryRepository.addRegistered(username, "encrypted_password", LocalDate.now());

        Assertions.assertThrows(NoSuchElementException.class, () -> userFacadeImp.removeFromCart(username, productId, storeName, quantity));
    }

    @Test
    public void givenUserNotLoggedIn_WhenRemoveFromCart_ThenThrowException() {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;

        userMemoryRepository.addRegistered(username, "encrypted_password", LocalDate.now());
        marketFacade.addStore(storeName, "description", username, 4.5);

        Assertions.assertThrows(RuntimeException.class, () -> userFacadeImp.removeFromCart(username, productId, storeName, quantity));
    }

}