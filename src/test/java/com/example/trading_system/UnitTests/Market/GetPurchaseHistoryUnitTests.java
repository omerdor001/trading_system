package com.example.trading_system.UnitTests.Market;

import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.*;
import com.example.trading_system.domain.users.User;
import com.example.trading_system.domain.users.UserFacadeImp;
import com.example.trading_system.domain.users.UserMemoryRepository;
import com.example.trading_system.domain.users.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class GetPurchaseHistoryUnitTests {
    @Mock
    Purchase purchase;
    MarketFacadeImp marketFacade;
    UserFacadeImp userFacadeImp;
    private UserRepository userRepository;
    private StoreRepository storeRepository;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);

        // Re-instantiate singletons
        storeRepository=StoreMemoryRepository.getInstance();
        userRepository = UserMemoryRepository.getInstance();
        marketFacade = MarketFacadeImp.getInstance(storeRepository);
        userFacadeImp = UserFacadeImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class),userRepository,storeRepository);
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
        marketFacade.addStore(storeName,"Description","founder",6.0);
        marketFacade.getStore(storeName).addPurchase(purchase);

        Assertions.assertThrows(RuntimeException.class, () -> userFacadeImp.getPurchaseHistory(username, storeName), "User not found");
    }

    @Test
    public void givenUserNotLoggedIn_WhenGetPurchaseHistory_ThenThrowException() {
        String username = "rValidUser";
        String storeName = "StoreName";
        marketFacade.addStore(storeName,"Description","founder",6.0);

        marketFacade.getStore(storeName).addPurchase(purchase);

        userRepository.addRegistered(username, "encrypted_password", null);
        // Ensure user is added but not logged in
        Assertions.assertThrows(RuntimeException.class, () -> userFacadeImp.getPurchaseHistory(username, storeName), "User is not logged");
    }

    @Test
    public void givenUserNotCommercialManager_WhenGetPurchaseHistory_ThenThrowException() {
        String username = "rValidUser";
        String storeName = "StoreName";
        marketFacade.addStore(storeName,"Description","founder",6.0);
        marketFacade.getStore(storeName).addPurchase(purchase);

        userRepository.addRegistered(username, "encrypted_password", null);
        // Ensure user is logged in
        User user = userRepository.getUser(username);
        user.login();

        Assertions.assertThrows(RuntimeException.class, () -> userFacadeImp.getPurchaseHistory(username, storeName), "User is not commercial manager");
    }

    //TODO fix this test as soon as possible
//@Test
//    public void givenValidInputs_WhenGetPurchaseHistory_ThenReturnHistory() {
//        String username = "ValidUser";
//        String storeName = "StoreName";
//        String expectedHistory = """
//                Client Username: rValidUser, Total Price: $100.0
//                Products:
//                Product: 1, Quantity: 2, Price: $100.0, Store: StoreName
//
//                Client Username: rValidUser, Total Price: $100.0
//                Products:
//                Product: 2, Quantity: 2, Price: $100.0, Store: StoreName
//                """;
//        try{
//            userFacadeImp.register(username, "encrypted_password", LocalDate.now());
//            userFacadeImp.enter(0);
//            userFacadeImp.login("v0",username,"encrypted_password");
//        }
//        catch (Exception _) {}
//        userFacadeImp.createStore("r"+username,storeName,"");
//        ProductInSale productInSale = new ProductInSale(storeName,1, 100.0, 2,3);
//        ProductInSale productInSale2 = new ProductInSale(storeName,2, 100.0, 2,3);
//        marketFacade.addPurchase(username, List.of(productInSale), 100.0, storeName);
//        marketFacade.addPurchase(username, List.of(productInSale2), 100.0, storeName);
//        String purchaseHistory = userFacadeImp.getPurchaseHistory("r"+username, storeName);
//        Assertions.assertEquals(expectedHistory, purchaseHistory);
//    }
}
