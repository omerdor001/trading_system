package com.example.trading_system.UnitTests.Users;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.StoreMemoryRepository;
import com.example.trading_system.domain.stores.StoreRepository;
import com.example.trading_system.domain.users.*;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;

public class ViewCartUnitTests {

    private UserRepository userRepository;
    private StoreRepository storeRepository;
    UserFacadeImp userFacadeImp;

    @BeforeEach
    public void init() {
        userRepository = UserMemoryRepository.getInstance();
        storeRepository= StoreMemoryRepository.getInstance();
        userFacadeImp = UserFacadeImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class), mock(NotificationSender.class),userRepository,storeRepository);
    }

    @AfterEach
    public void tearDown() {
        userFacadeImp.deleteInstance();
    }

    @Test
    public void givenValidLoggedInUser_WhenViewCart_ThenSuccess() {
        String username = "rValidUser";

        userRepository.addRegistered(username, "encrypted_password", LocalDate.now());
        userRepository.getUser(username).login();
        userRepository.getUser(username).setCart(new Cart());

        userRepository.getUser(username).getCart().addProductToCart(1, 1, "Store", 10.0, 1);
        String result = null;
        try {
             result = userFacadeImp.viewCart(username);
        }
        catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertNotNull(result);
    }

    @Test
    public void givenValidUserNotLoggedIn_WhenViewCart_ThenThrowException() {
        String username = "rValidUser";

        userRepository.addRegistered(username, "encrypted_password", LocalDate.now());

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> userFacadeImp.viewCart(username));

        Assertions.assertEquals("Registered user is not logged", exception.getMessage());
    }

    @Test
    public void givenUnregisteredUser_WhenViewCart_ThenThrowException() {
        String username = "unregisteredUser";

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> userFacadeImp.viewCart(username));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void givenNullUsername_WhenViewCart_ThenThrowException() {
        //String username = null;

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.viewCart(null));

        Assertions.assertEquals("Username cannot be null", exception.getMessage());
    }

    @Test
    public void givenEmptyUsername_WhenViewCart_ThenThrowException() {
        String username = "";

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.viewCart(username));

        Assertions.assertEquals("Username cannot be empty", exception.getMessage());
    }

    @Test
    public void givenNonExistentUser_WhenViewCart_ThenThrowException() {
        String username = "rNonExistentUser";

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> userFacadeImp.viewCart(username));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

}
