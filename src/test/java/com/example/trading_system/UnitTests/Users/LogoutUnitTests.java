package com.example.trading_system.UnitTests.Users;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.users.Cart;
import com.example.trading_system.domain.users.Registered;
import com.example.trading_system.domain.users.User;
import com.example.trading_system.domain.users.UserDatabaseRepository;
import com.example.trading_system.domain.users.UserFacadeImp;
import com.example.trading_system.domain.stores.StoreDatabaseRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SpringBootTest
@Transactional
public class LogoutUnitTests {

    @Autowired
    private UserDatabaseRepository userRepository;

    @Autowired
    private StoreDatabaseRepository storeRepository;

    private UserFacadeImp userFacadeImp;

    @BeforeEach
    public void init() {
        userFacadeImp = UserFacadeImp.getInstance(mock(PaymentService.class), mock(DeliveryService.class), mock(NotificationSender.class), userRepository, storeRepository);
    }

    @AfterEach
    public void tearDown() {
        userFacadeImp.deleteInstance();
        userRepository.deleteInstance();
        storeRepository.deleteInstance();
    }

    @Test
    public void givenValidUser_WhenLogout_ThenSuccess() {
        String username = "rValidUser";
        int id = 123;

        Registered user = new Registered(username.substring(1), "encrypted_password", LocalDate.now());
        user.login();
        user.setCart(new Cart());
        userRepository.saveUser(user);

        assertDoesNotThrow(() -> userFacadeImp.logout(id, username));

        User userFromRepo = userRepository.getUser(username);
        assertNotNull(userFromRepo);
        assertFalse(userFromRepo.getLogged());
    }

    @Test
    public void givenNullUsername_WhenLogout_ThenThrowException() {
        int id = 123;
        assertThrows(IllegalArgumentException.class, () -> userFacadeImp.logout(id, null));
    }

    @Test
    public void givenEmptyUsername_WhenLogout_ThenThrowException() {
        int id = 123;
        String username = "";
        assertThrows(IllegalArgumentException.class, () -> userFacadeImp.logout(id, username));
    }

    @Test
    public void givenInvalidUserName_WhenLogout_ThenThrowException() {
        int id = 123;
        String invalidUsername = "vInvalidUser"; // Non-registered user format
        assertThrows(IllegalArgumentException.class, () -> userFacadeImp.logout(id, invalidUsername));
    }

    @Test
    public void givenUserAlreadyLoggedOut_WhenLogout_ThenThrowException() {
        String username = "rValidUser";
        int id = 123;

        Registered user = new Registered(username.substring(1), "encrypted_password", LocalDate.now());
        user.setCart(new Cart());
        userRepository.saveUser(user);

        assertThrows(IllegalArgumentException.class, () -> userFacadeImp.logout(id, username));
    }

    @Test
    public void givenNonRegisteredUserPerformingLogout_WhenLogout_ThenThrowException() {
        int id = 123;
        String nonRegisteredUsername = "rNonRegisteredUser";
        assertThrows(IllegalArgumentException.class, () -> userFacadeImp.logout(id, nonRegisteredUsername));
    }

    @Test
    public void givenNonExistingUser_WhenLogout_ThenThrowException() {
        String username = "rNonExistingUser";
        int id = 123;
        assertThrows(IllegalArgumentException.class, () -> userFacadeImp.logout(id, username));
    }

}