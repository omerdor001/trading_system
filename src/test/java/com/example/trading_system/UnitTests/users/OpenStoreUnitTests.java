package com.example.trading_system.UnitTests.users;

import com.example.trading_system.domain.stores.MarketFacadeImp;
import com.example.trading_system.domain.stores.StorePolicy;
import com.example.trading_system.domain.users.User;
import com.example.trading_system.domain.users.UserFacadeImp;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OpenStoreUnitTests {

    @Mock
    User user;
    @Mock
    StorePolicy policy;

    UserFacadeImp userFacade;
    MarketFacadeImp marketFacade;
    String validUsername = "validUser";
    String validStoreName = "ValidStore";
    String validDescription = "This is a valid description.";

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        marketFacade = MarketFacadeImp.getInstance();
        userFacade = UserFacadeImp.getInstance();
        userFacade.getUsers().put(validUsername, user);
    }

    @AfterEach
    public void resetMocks() {
        userFacade.deleteInstance();
        marketFacade.deleteInstance();
        reset(user);
    }

    @Test
    public void givenNonExistentUser_WhenOpenStore_ThenThrowRuntimeException() {
        Assertions.assertThrows(RuntimeException.class, () -> userFacade.openStore("nonExistentUser", validStoreName, validDescription, policy));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {""})
    public void givenInvalidStoreName_WhenOpenStore_ThenThrowRuntimeException(String storeName) {
        Assertions.assertThrows(RuntimeException.class, () -> userFacade.openStore(validUsername, storeName, validDescription, policy));
    }

    @Test
    public void givenExistingStoreName_WhenOpenStore_ThenThrowRuntimeException() {
        marketFacade.addStore(validStoreName, validDescription, policy, validUsername, null); // Ensure store exists
        Assertions.assertThrows(RuntimeException.class, () -> userFacade.openStore(validUsername, validStoreName, validDescription, policy));
    }

    @Test
    public void givenValidDetails_WhenOpenStore_ThenSuccess() {
        Assertions.assertDoesNotThrow(() -> userFacade.openStore(validUsername, validStoreName, validDescription, policy));
    }

}
