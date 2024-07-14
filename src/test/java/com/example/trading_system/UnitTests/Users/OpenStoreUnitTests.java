package com.example.trading_system.UnitTests.Users;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.MarketFacadeImp;
import com.example.trading_system.domain.stores.StoreDatabaseRepository;
import com.example.trading_system.domain.stores.StoreMemoryRepository;
import com.example.trading_system.domain.stores.StoreRepository;
import com.example.trading_system.domain.users.UserDatabaseRepository;
import com.example.trading_system.domain.users.UserFacadeImp;
import com.example.trading_system.domain.users.UserMemoryRepository;
import com.example.trading_system.domain.users.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.mock;
@SpringBootTest
@Transactional
public class OpenStoreUnitTests {
    MarketFacadeImp marketFacade;
    UserFacadeImp userFacadeImp;
    @Autowired
    private UserDatabaseRepository userRepository;

    @Autowired
    private StoreDatabaseRepository storeRepository;
    @BeforeEach
    public void init() {
        // Re-instantiate singletons
        marketFacade = MarketFacadeImp.getInstance(storeRepository);

        userFacadeImp = UserFacadeImp.getInstance(mock(PaymentService.class), mock(DeliveryService.class), mock(NotificationSender.class), userRepository, storeRepository);
    }

    @AfterEach
    public void tearDown() {
        userFacadeImp.deleteInstance();
        marketFacade.deleteInstance();
    }

    @Test
    public void givenValidInputs_WhenOpenStore_ThenSuccess() {
        String username = "rValidUser";
        String storeName = "StoreName";
        String description = "StoreDescription";

        userRepository.addRegistered(username, "encrypted_password", null);
        userRepository.getUser(username).login();

        Assertions.assertDoesNotThrow(() -> userFacadeImp.createStore(username, storeName, description));

        // Check if the store was added successfully (assuming a method exists to get the store by name)
        Assertions.assertTrue(marketFacade.isStoreExist(storeName));
    }

    @Test
    public void givenNonExistentUser_WhenOpenStore_ThenThrowException() {
        String username = "rNonExistentUser";
        String storeName = "StoreName";
        String description = "StoreDescription";

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.createStore(username, storeName, description));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void givenNullStoreName_WhenOpenStore_ThenThrowException() {
        String username = "rValidUser";
        //String storeName = null;
        String description = "StoreDescription";

        userRepository.addRegistered(username, "encrypted_password", null);
        userRepository.getUser(username).login();

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.createStore(username, null, description));

        Assertions.assertEquals("Store name should not be null", exception.getMessage());
    }

    @Test
    public void givenEmptyStoreName_WhenOpenStore_ThenThrowException() {
        String username = "rValidUser";
        String storeName = "";
        String description = "StoreDescription";

        userRepository.addRegistered(username, "encrypted_password", null);
        userRepository.getUser(username).login();

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.createStore(username, storeName, description));

        Assertions.assertEquals("Store name should not be null", exception.getMessage());
    }

    @Test
    public void givenExistingStoreName_WhenOpenStore_ThenThrowException() {
        String username = "rValidUser";
        String storeName = "ExistingStoreName";
        String description = "StoreDescription";

        userRepository.addRegistered(username, "encrypted_password", null);
        userRepository.getUser(username).login();
        marketFacade.addStore(storeName, description, username, null); // Ensure store exists

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.createStore(username, storeName, description));

        Assertions.assertEquals("Store with name ExistingStoreName already exists", exception.getMessage());
    }

    @Test
    public void givenExceptionDuringStoreCreation_WhenOpenStore_ThenThrowException() {
        String username = "rValidUser";
        String description = "StoreDescription";

        userRepository.addRegistered(username, "encrypted_password", null);
        userRepository.getUser(username).login();

        // Simulate exception by invalid parameters
        IllegalArgumentException exception;
        exception = Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.createStore(username, "", description));
        Assertions.assertEquals("Store name should not be null", exception.getMessage());
    }
}
