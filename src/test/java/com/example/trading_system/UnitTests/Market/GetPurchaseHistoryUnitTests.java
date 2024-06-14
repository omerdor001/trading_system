package com.example.trading_system.UnitTests.Market;

import com.example.trading_system.domain.stores.*;
import com.example.trading_system.domain.users.User;
import com.example.trading_system.domain.users.UserFacadeImp;
import com.example.trading_system.domain.users.UserMemoryRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetPurchaseHistoryUnitTests {

    @Mock
    User user;
    @Mock
    Purchase purchase;

    UserMemoryRepository userMemoryRepository;
    MarketFacadeImp marketFacade;
    UserFacadeImp userFacadeImp;
    StoreSalesHistory storeSalesHistory;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);

        // Clear singleton instances
//        UserMemoryRepository.getInstance().deleteInstance();
        MarketFacadeImp.getInstance().deleteInstance();
        UserFacadeImp.getInstance().deleteInstance();
        StoreSalesHistory.getInstance().deleteInstance();

        // Re-instantiate singletons
        userMemoryRepository = UserMemoryRepository.getInstance();
        marketFacade = MarketFacadeImp.getInstance();
        userFacadeImp = UserFacadeImp.getInstance();
        storeSalesHistory = StoreSalesHistory.getInstance();

        storeSalesHistory.setPurchases(new ArrayList<>());
    }

    @AfterEach
    public void tearDown() {
        userFacadeImp.deleteInstance();
        marketFacade.deleteInstance();
    }

    @Test
    public void givenNonExistentUser_WhenGetPurchaseHistory_ThenThrowException() {
        String username = "rNonExistentUser";
        String storeName = "StoreName";
        storeSalesHistory.addPurchase(purchase);

        Assertions.assertThrows(RuntimeException.class, () -> userFacadeImp.getPurchaseHistory(username, storeName), "User not found");
    }

    @Test
    public void givenUserNotLoggedIn_WhenGetPurchaseHistory_ThenThrowException() {
        String username = "rValidUser";
        String storeName = "StoreName";
        storeSalesHistory.addPurchase(purchase);

        userMemoryRepository.addRegistered(username, "encrypted_password", null);
        // Ensure user is added but not logged in
        Assertions.assertThrows(RuntimeException.class, () -> userFacadeImp.getPurchaseHistory(username, storeName), "User is not logged");
    }

    @Test
    public void givenUserNotCommercialManager_WhenGetPurchaseHistory_ThenThrowException() {
        String username = "rValidUser";
        String storeName = "StoreName";
        storeSalesHistory.addPurchase(purchase);

        userMemoryRepository.addRegistered(username, "encrypted_password", null);
        // Ensure user is logged in
        User user = userMemoryRepository.getUser(username);
        user.login();

        Assertions.assertThrows(RuntimeException.class, () -> userFacadeImp.getPurchaseHistory(username, storeName), "User is not commercial manager");
    }

    @Test
    public void givenValidInputs_WhenGetPurchaseHistory_ThenReturnHistory() {
        String username = "rValidUser";
        String storeName = "StoreName";
        String expectedHistory = "Client Username: rValidUser, Total Price: $100.0\n" +
                "Products:\n" +
                "Product: 1, Quantity: 2, Price: $100.0, Store: StoreName\n" +
                "\n" +
                "Client Username: rValidUser, Total Price: $100.0\n" +
                "Products:\n" +
                "Product: 2, Quantity: 2, Price: $100.0, Store: StoreName\n";

        userMemoryRepository.addRegistered(username, "encrypted_password", null);
        // Ensure user is logged in and is an admin
        User user = userMemoryRepository.getUser(username);
        user.login();
        user.setAdmin(true);

        // Add a purchase to the storeSalesHistory
        ProductInSale productInSale = new ProductInSale(storeName,1, 100.0, 2);
        ProductInSale productInSale2 = new ProductInSale(storeName,2, 100.0, 2);

        Purchase purchase1 = new Purchase(username, List.of(productInSale), 100.0, storeName);
        Purchase purchase2 = new Purchase(username, List.of(productInSale2), 100.0, storeName);

        storeSalesHistory.addPurchase(purchase1);
        storeSalesHistory.addPurchase(purchase2);

        String purchaseHistory = userFacadeImp.getPurchaseHistory(username, storeName);

        Assertions.assertEquals(expectedHistory, purchaseHistory);
    }
}
