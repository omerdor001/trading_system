package com.example.trading_system.UnitTests.Users;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.StoreDatabaseRepository;
import com.example.trading_system.domain.stores.StoreMemoryRepository;
import com.example.trading_system.domain.stores.StoreRepository;
import com.example.trading_system.domain.users.UserDatabaseRepository;
import com.example.trading_system.domain.users.UserFacadeImp;
import com.example.trading_system.domain.users.UserMemoryRepository;
import com.example.trading_system.domain.users.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
@SpringBootTest
@Transactional
class ExitUnitTests {
    private UserFacadeImp userFacade;
    @Autowired
    private UserDatabaseRepository userRepository;

    @Autowired
    private StoreDatabaseRepository storeRepository;

    @BeforeEach
    void setUp() {
        userFacade = UserFacadeImp.getInstance(mock(PaymentService.class), mock(DeliveryService.class), mock(NotificationSender.class), userRepository, storeRepository);

        // Adding sample visitors and registered users
        userFacade.enter(1);
        userFacade.enter(2);
        userFacade.enter(3);
        try {
            userFacade.register("user1", "password1", LocalDate.of(1990, 5, 15));
            userFacade.register("user2", "password2", LocalDate.of(1991, 6, 20));
        } catch (Exception e) {

        }
    }

    @AfterEach
    void setDown(){
        userFacade.deleteInstance();
    }

    @Test
    void exitVisitor_Success() {
        int id = 1;
        assertDoesNotThrow(() -> userFacade.exit("v" + id));
        assertFalse(userFacade.isUserExist("v" + id));
    }

    @Test
    void exitVisitor_NoSuchVisitor() {
        int id = 4; // Assuming visitor with id 4 doesn't exist
        Exception exception = assertThrows(Exception.class, () -> userFacade.exit("v" + id));
        assertEquals("No such user with username- " + "v" + id, exception.getMessage());
    }

    @Test
    void exitRegisteredUser_Success() {
        String username = "user1";
        assertDoesNotThrow(() -> userFacade.exit("r" + username));
        assertFalse(userFacade.isUserExist("r" + username));
    }

    @Test
    void exitRegisteredUser_NoSuchUser() {
        String username = "nonexistent"; // Assuming this user doesn't exist
        Exception exception = assertThrows(Exception.class, () -> userFacade.exit("r" + username));
        assertEquals("No such user with username- " + "r" + username, exception.getMessage());
    }
}