package com.example.trading_system.UnitTests.users;

import com.example.trading_system.domain.stores.MarketFacadeImp;
import com.example.trading_system.domain.stores.StorePolicy;
import com.example.trading_system.domain.users.User;
import com.example.trading_system.domain.users.UserFacadeImp;
import com.example.trading_system.domain.users.UserMemoryRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OpenStoreUnitTests {

    @Mock
    User user;

    UserMemoryRepository userMemoryRepository;
    MarketFacadeImp marketFacade;
    UserFacadeImp userFacadeImp;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);

        // Clear singleton instances
        UserMemoryRepository.getInstance().deleteInstance();
        MarketFacadeImp.getInstance().deleteInstance();
        UserFacadeImp.getInstance().deleteInstance();

        // Re-instantiate singletons
        userMemoryRepository = UserMemoryRepository.getInstance();
        marketFacade = MarketFacadeImp.getInstance();
        userFacadeImp = UserFacadeImp.getInstance();
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
        StorePolicy policy = new StorePolicy();

        userMemoryRepository.addRegistered(username, "encrypted_password", null);
        userMemoryRepository.getUser(username).login();

        Assertions.assertDoesNotThrow(() -> userFacadeImp.createStore(username, storeName, description, policy));

        // Check if the store was added successfully (assuming a method exists to get the store by name)
        Assertions.assertTrue(marketFacade.isStoreExist(storeName));
    }

    @Test
    public void givenNonExistentUser_WhenOpenStore_ThenThrowException() {
        String username = "rNonExistentUser";
        String storeName = "StoreName";
        String description = "StoreDescription";
        StorePolicy policy = new StorePolicy();

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.createStore(username, storeName, description, policy));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void givenNullStoreName_WhenOpenStore_ThenThrowException() {
        String username = "rValidUser";
        String storeName = null;
        String description = "StoreDescription";
        StorePolicy policy = new StorePolicy();

        userMemoryRepository.addRegistered(username, "encrypted_password", null);
        userMemoryRepository.getUser(username).login();

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.createStore(username, storeName, description, policy));

        Assertions.assertEquals("Store name should not be null", exception.getMessage());
    }

    @Test
    public void givenEmptyStoreName_WhenOpenStore_ThenThrowException() {
        String username = "rValidUser";
        String storeName = "";
        String description = "StoreDescription";
        StorePolicy policy = new StorePolicy();

        userMemoryRepository.addRegistered(username, "encrypted_password", null);
        userMemoryRepository.getUser(username).login();

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.createStore(username, storeName, description, policy));

        Assertions.assertEquals("Store name should not be null", exception.getMessage());
    }

    @Test
    public void givenExistingStoreName_WhenOpenStore_ThenThrowException() {
        String username = "rValidUser";
        String storeName = "ExistingStoreName";
        String description = "StoreDescription";
        StorePolicy policy = new StorePolicy();

        userMemoryRepository.addRegistered(username, "encrypted_password", null);
        userMemoryRepository.getUser(username).login();
        marketFacade.addStore(storeName, description, policy, username, null); // Ensure store exists

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.createStore(username, storeName, description, policy));

        Assertions.assertEquals("Store with name ExistingStoreName already exists", exception.getMessage());
    }

    @Test
    public void givenExceptionDuringStoreCreation_WhenOpenStore_ThenThrowException() {
        String username = "rValidUser";
        String storeName = "StoreName";
        String description = "StoreDescription";
        StorePolicy policy = new StorePolicy();

        userMemoryRepository.addRegistered(username, "encrypted_password", null);
        userMemoryRepository.getUser(username).login();

        // Simulate exception by invalid parameters
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userFacadeImp.createStore(username, "", description, policy);
        });

        Assertions.assertEquals("Store name should not be null", exception.getMessage());
    }
}
