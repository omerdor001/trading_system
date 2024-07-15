package com.example.trading_system.UnitTests.Market;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.*;
import com.example.trading_system.domain.users.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import jakarta.transaction.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
public class GetPurchaseHistoryUnitTests {
    @Mock
    Purchase purchase;
    MarketFacadeImp marketFacade;
    UserFacadeImp userFacadeImp;
    @Autowired
    private UserDatabaseRepository userRepository;

    @Autowired
    private StoreDatabaseRepository storeRepository;
    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);

        marketFacade = MarketFacadeImp.getInstance(storeRepository);
        userFacadeImp = UserFacadeImp.getInstance(mock(PaymentService.class), mock(DeliveryService.class), mock(NotificationSender.class), userRepository, storeRepository);
    }

    @AfterEach
    public void tearDown() {
        userFacadeImp.deleteInstance();
        marketFacade.deleteInstance();
    }

    @Test
    public void givenValidPurchase_WhenAddPurchase_ThenAddedSuccessfully() {
        String username = "ValidUser";
        String storeName = "StoreName";
        ProductInSaleDTO product = new ProductInSaleDTO(storeName, 1, 100.0, 2, 3);
        Purchase purchase = new Purchase(username, List.of(product), 200.0, storeName);

        StoreSalesHistory salesHistory = new StoreSalesHistory();
        salesHistory.addPurchase(purchase);

        assertEquals(1, salesHistory.getAllPurchases().size());
        assertEquals(purchase, salesHistory.getAllPurchases().get(0));
    }

    @Test
    public void givenMultiplePurchases_WhenFilterByCustomer_ThenReturnFilteredResults() {
        String username1 = "User1";
        String username2 = "User2";
        String storeName = "StoreName";
        ProductInSaleDTO product1 = new ProductInSaleDTO(storeName, 1, 100.0, 2, 3);
        ProductInSaleDTO product2 = new ProductInSaleDTO(storeName, 2, 150.0, 1, 3);
        Purchase purchase1 = new Purchase(username1, List.of(product1), 200.0, storeName);
        Purchase purchase2 = new Purchase(username2, List.of(product2), 150.0, storeName);

        StoreSalesHistory salesHistory = new StoreSalesHistory();
        salesHistory.addPurchase(purchase1);
        salesHistory.addPurchase(purchase2);

        List<Purchase> user1Purchases = salesHistory.getPurchasesByCustomer(username1);

        assertEquals(1, user1Purchases.size());
        assertEquals(purchase1, user1Purchases.get(0));
    }

    @Test
    public void givenNoPurchases_WhenGetAllPurchases_ThenReturnEmptyList() {
        StoreSalesHistory salesHistory = new StoreSalesHistory();

        assertTrue(salesHistory.getAllPurchases().isEmpty());
    }

    @Test
    public void givenEmptyPurchaseHistory_WhenGetPurchaseHistory_ThenReturnEmptyString() {
        StoreSalesHistory salesHistory = new StoreSalesHistory();

        assertEquals("", salesHistory.getPurchaseHistory(null));
    }

    @Test
    public void givenInvalidCustomerUsername_WhenGetPurchasesByCustomer_ThenReturnEmptyList() {
        String username = "InvalidUser";
        String storeName = "StoreName";
        ProductInSaleDTO product = new ProductInSaleDTO(storeName, 1, 100.0, 2, 3);
        Purchase purchase = new Purchase("ValidUser", List.of(product), 200.0, storeName);

        StoreSalesHistory salesHistory = new StoreSalesHistory();
        salesHistory.addPurchase(purchase);

        List<Purchase> purchases = salesHistory.getPurchasesByCustomer(username);

        assertTrue(purchases.isEmpty());
    }


    @Test
    public void givenNonExistentUser_WhenGetPurchaseHistory_ThenThrowException() {
        String username = "rNonExistentUser";
        String storeName = "StoreName";
        marketFacade.addStore(storeName, "Description", "founder", 6.0);
        marketFacade.getStore(storeName).addPurchase(purchase);

        Assertions.assertThrows(RuntimeException.class, () -> userFacadeImp.getPurchaseHistory(username, storeName), "User not found");
    }

    @Test
    public void givenUserNotLoggedIn_WhenGetPurchaseHistory_ThenThrowException() {
        String username = "rValidUser";
        String storeName = "StoreName";
        marketFacade.addStore(storeName, "Description", "founder", 6.0);

        marketFacade.getStore(storeName).addPurchase(purchase);

        userRepository.addRegistered(username, "encrypted_password", LocalDate.of(2002,11,24));
        // Ensure user is added but not logged in
        Assertions.assertThrows(RuntimeException.class, () -> userFacadeImp.getPurchaseHistory(username, storeName), "User is not logged");
    }

    @Test
    public void givenUserNotCommercialManager_WhenGetPurchaseHistory_ThenThrowException() {
        String username = "rValidUser";
        String storeName = "StoreName";
        marketFacade.addStore(storeName, "Description", "founder", 6.0);
        marketFacade.getStore(storeName).addPurchase(purchase);

        userRepository.addRegistered(username, "encrypted_password", LocalDate.of(2002,11,24));
        // Ensure user is logged in
        User user = userRepository.getUser(username);
        user.login();

        Assertions.assertThrows(RuntimeException.class, () -> userFacadeImp.getPurchaseHistory(username, storeName), "User is not commercial manager");
    }

}