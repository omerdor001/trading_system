package com.example.trading_system.UnitTests.Users;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.*;
import com.example.trading_system.domain.users.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.mock;
@SpringBootTest
@Transactional
public class RemoveFromCartUnitTests {
    MarketFacadeImp marketFacade;
    UserFacadeImp userFacadeImp;
    @Autowired
    private UserDatabaseRepository userRepository;

    @Autowired
    private StoreDatabaseRepository storeRepository;

    @BeforeEach
    public void init() {

        marketFacade = MarketFacadeImp.getInstance(storeRepository);
        userFacadeImp = UserFacadeImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class), mock(NotificationSender.class),userRepository,storeRepository);
    }

    @AfterEach
    public void tearDown() {
        marketFacade.deleteInstance();
        userFacadeImp.deleteInstance();
    }

    @Test
    public void givenValidDetails_WhenRemoveFromCart_ThenSuccess() {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;


        userRepository.addRegistered(username, "encrypted_password", LocalDate.now());
        userRepository.getUser(username).login();
        marketFacade.addStore(storeName, "description", username, 4.5);

        Store store = marketFacade.getStore(storeName);
        store.addProduct(productId, "ProductName", "ProductDescription", 10.0, 10, 4.5, 1, null);

        Cart shoppingCart = new Cart();
        userRepository.getUser(username).setCart(shoppingCart);
        shoppingCart.addProductToCart(productId, quantity, storeName, 10.0, 1);

        Assertions.assertDoesNotThrow(() -> userFacadeImp.removeFromCart(username, productId, storeName, quantity));

    }

    @Test
    public void givenNullUsername_WhenRemoveFromCart_ThenThrowException() {
        //String username = null;
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;

        Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.removeFromCart(null, productId, storeName, quantity));
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
        //String storeName = null;
        int quantity = 5;

        userRepository.addRegistered(username, "encrypted_password", LocalDate.now());

        Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.removeFromCart(username, productId, null, quantity));
    }

    @Test
    public void givenEmptyStoreName_WhenRemoveFromCart_ThenThrowException() {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "";
        int quantity = 5;

        userRepository.addRegistered(username, "encrypted_password", LocalDate.now());

        Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.removeFromCart(username, productId, storeName, quantity));
    }

    @Test
    public void givenNonExistentStore_WhenRemoveFromCart_ThenThrowException() {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "NonExistentStore";
        int quantity = 5;

        userRepository.addRegistered(username, "encrypted_password", LocalDate.now());

        Assertions.assertThrows(NoSuchElementException.class, () -> userFacadeImp.removeFromCart(username, productId, storeName, quantity));
    }

    @Test
    public void givenUserNotLoggedIn_WhenRemoveFromCart_ThenThrowException() {
        String username = "rValidUser";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;

        userRepository.addRegistered(username, "encrypted_password", LocalDate.now());
        marketFacade.addStore(storeName, "description", username, 4.5);

        Assertions.assertThrows(RuntimeException.class, () -> userFacadeImp.removeFromCart(username, productId, storeName, quantity));
    }

}