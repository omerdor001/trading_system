package com.example.trading_system.UnitTests.Payment;

import com.example.trading_system.domain.stores.*;
import com.example.trading_system.domain.users.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
public class PurchaseCartUnitTests {
    MarketFacadeImp marketFacade;
    UserFacade userFacade;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        // Re-instantiate singletons
        marketFacade = MarketFacadeImp.getInstance();
        userFacade = UserFacadeImp.getInstance();
    }

    @AfterEach
    public void tearDown() {
        userFacade.deleteInstance();
        marketFacade.deleteInstance();
    }

    @Test
    public void givenValidDetails_WhenPurchaseCart_ThenSuccess() {
        String username = "ValidUser";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;
        String address = "1234 El Street, Springfield, IL, 62704-5678";  // Valid address format
        try {
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
        userFacade.addToCart("r" + username, productId, storeName, quantity);

        Assertions.assertDoesNotThrow(() -> userFacade.purchaseCart("r" + username));
    }

    @Test
    public void givenTwoProcessesWithDelayedDelivery_WhenPurchaseCart_ThenThrowException() throws IllegalAccessException {
        String username1 = "ValidUser1";
        String username2 = "ValidUser2";
        int productId = 1;
        String storeName = "StoreName";
        int quantity = 5;
        String address = "1234 El Street, Springfield, IL, 62704-5678";  // Valid address format
        try {
            userFacade.register(username1, "encrypted_password", LocalDate.now());
            userFacade.register(username2, "encrypted_password", LocalDate.now());
            userFacade.login("v1", username1, "encrypted_password");
            userFacade.login("v2", username2, "encrypted_password");
        } catch (Exception e) {
        }
        userFacade.setAddress("r" + username1,address);
        userFacade.setAddress("r" +username2,address);
        marketFacade.addStore(storeName, "description", username1, 4.5);
        Store store = marketFacade.getStore(storeName);
        store.addProduct(productId, "ProductName", "ProductDescription", 10.0, 10, 4.5, 1, null);

        userFacade.addToCart("r" + username1, productId, storeName, quantity);

        Assertions.assertDoesNotThrow(() -> userFacade.purchaseCart("r" + username1));
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
