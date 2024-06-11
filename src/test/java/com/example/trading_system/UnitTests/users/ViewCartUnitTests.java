package com.example.trading_system.UnitTests.users;

import com.example.trading_system.domain.users.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
public class ViewCartUnitTests {

    UserMemoryRepository userMemoryRepository;
    UserFacadeImp userFacadeImp;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);

        // Clear singleton instances
        UserMemoryRepository.getInstance().deleteInstance();
        UserFacadeImp.getInstance().deleteInstance();

        // Re-instantiate singletons
        userMemoryRepository = UserMemoryRepository.getInstance();
        userFacadeImp = UserFacadeImp.getInstance();
    }

    @AfterEach
    public void tearDown() {
        userFacadeImp.deleteInstance();
        userMemoryRepository.deleteInstance();
    }

    @Test
    public void givenValidLoggedInUser_WhenViewCart_ThenSuccess() {
        String username = "rValidUser";

        userMemoryRepository.addRegistered(username, "encrypted_password", LocalDate.now());
        userMemoryRepository.getUser(username).login();
        userMemoryRepository.getUser(username).setCart(new Cart());

        userMemoryRepository.getUser(username).getCart().addProductToCart(1, 1, "Store", 10.0);

        String result = userFacadeImp.viewCart(username);

        Assertions.assertNotNull(result);
    }

    @Test
    public void givenValidUserNotLoggedIn_WhenViewCart_ThenThrowException() {
        String username = "rValidUser";

        userMemoryRepository.addRegistered(username, "encrypted_password", LocalDate.now());

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
        String username = null;

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.viewCart(username));

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
