package com.example.trading_system.UnitTests.users;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.StoreMemoryRepository;
import com.example.trading_system.domain.stores.StoreRepository;
import com.example.trading_system.domain.users.UserFacadeImp;
import com.example.trading_system.domain.users.UserMemoryRepository;
import com.example.trading_system.domain.users.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class RegistrationUnitTests {
    private UserFacadeImp userFacade;
    private UserRepository userRepository;
    private StoreRepository storeRepository;

    @BeforeEach
    void setUp() {
        storeRepository= StoreMemoryRepository.getInstance();
        userRepository = UserMemoryRepository.getInstance();
        userFacade = UserFacadeImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class), mock(NotificationSender.class),userRepository,storeRepository);
        // Adding a sample visitor
        userFacade.enter(1);
        userFacade.enter(2);
        userFacade.enter(3);
    }

    @AfterEach
    void reset() {
        userFacade.deleteInstance();
    }

    @Test
    void registration_Success(){
        String username = "testuser";
        String encryption = "testpassword";
        LocalDate birthdate = LocalDate.of(1990, 5, 15);

        assertDoesNotThrow(() -> userFacade.register(username, encryption, birthdate));
        assertTrue(userFacade.isUserExist("r"+username));
    }

    @Test
    void registration_NullUsername() {
       // String username = null;
        String encryption = "testpassword";
        LocalDate birthdate = LocalDate.of(1990, 5, 15);

        Exception exception = assertThrows(Exception.class, () -> userFacade.register(null, encryption, birthdate));
        assertEquals("Username is null", exception.getMessage());
    }

    @Test
    void registration_EmptyUsername() {
        String username = "";
        String encryption = "testpassword";
        LocalDate birthdate = LocalDate.of(1990, 5, 15);

        Exception exception = assertThrows(Exception.class, () -> userFacade.register( username, encryption, birthdate));
        assertEquals("Username is empty", exception.getMessage());
    }

    @Test
    void registration_NullEncryption() {
        String username = "testuser";
        //String encryption = null;
        LocalDate birthdate = LocalDate.of(1990, 5, 15);

        Exception exception = assertThrows(Exception.class, () -> userFacade.register( username, null, birthdate));
        assertEquals("Encrypted password is null", exception.getMessage());
    }

    @Test
    void registration_EmptyEncryption() {
        String username = "testuser";
        String encryption = "";
        LocalDate birthdate = LocalDate.of(1990, 5, 15);

        Exception exception = assertThrows(Exception.class, () -> userFacade.register( username, encryption, birthdate));
        assertEquals("Encrypted password is empty", exception.getMessage());
    }

    @Test
    void registration_NullBirthdate() {
        String username = "testuser";
        String encryption = "testpassword";
        //LocalDate birthdate = null;

        Exception exception = assertThrows(Exception.class, () -> userFacade.register( username, encryption, null));
        assertEquals("Birthdate password is null", exception.getMessage());
    }

    @Test
    void registration_DuplicateUsername() throws Exception {
        String username = "testuser";
        String encryption = "testpassword";
        LocalDate birthdate = LocalDate.of(1990, 5, 15);
        // Add the user once
        userFacade.register( username, encryption, birthdate);
        // Attempt to add the same user again
        Exception exception = assertThrows(Exception.class, () -> userFacade.register( username, "newpassword", LocalDate.of(1991, 6, 20)));
        assertEquals("username already exists - " + username, exception.getMessage());
    }

}