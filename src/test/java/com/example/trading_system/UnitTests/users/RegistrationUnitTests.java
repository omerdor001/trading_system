package com.example.trading_system.UnitTests.users;

import com.example.trading_system.domain.users.UserFacadeImp;
import com.example.trading_system.domain.users.Visitor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationUnitTests {
    private UserFacadeImp userFacade;

    @BeforeEach
    void setUp() {
        userFacade = UserFacadeImp.getInstance();
        // Adding a sample visitor
        userFacade.getUsers().put("1", new Visitor("1"));
        userFacade.getUsers().put("2", new Visitor("2"));
        userFacade.getUsers().put("3", new Visitor("3"));
    }

    @AfterEach
    void reset() {
        userFacade.getUsers().clear();
    }

    @Test
    void registration_Success(){
        int id = 1;
        String username = "testuser";
        String encryption = "testpassword";
        LocalDate birthdate = LocalDate.of(1990, 5, 15);

        assertDoesNotThrow(() -> userFacade.register(username, encryption, birthdate));
        assertTrue(userFacade.getUsers().containsKey(username));
    }

    @Test
    void registration_NullUsername() {
        int id = 1;
        String username = null;
        String encryption = "testpassword";
        LocalDate birthdate = LocalDate.of(1990, 5, 15);

        Exception exception = assertThrows(Exception.class, () -> userFacade.register(username, encryption, birthdate));
        assertEquals("Username is null", exception.getMessage());
    }

    @Test
    void registration_EmptyUsername() {
        int id = 1;
        String username = "";
        String encryption = "testpassword";
        LocalDate birthdate = LocalDate.of(1990, 5, 15);

        Exception exception = assertThrows(Exception.class, () -> userFacade.register( username, encryption, birthdate));
        assertEquals("Username is empty", exception.getMessage());
    }

    @Test
    void registration_NullEncryption() {
        int id = 1;
        String username = "testuser";
        String encryption = null;
        LocalDate birthdate = LocalDate.of(1990, 5, 15);

        Exception exception = assertThrows(Exception.class, () -> userFacade.register( username, encryption, birthdate));
        assertEquals("Encrypted password is null", exception.getMessage());
    }

    @Test
    void registration_EmptyEncryption() {
        int id = 1;
        String username = "testuser";
        String encryption = "";
        LocalDate birthdate = LocalDate.of(1990, 5, 15);

        Exception exception = assertThrows(Exception.class, () -> userFacade.register( username, encryption, birthdate));
        assertEquals("Encrypted password is empty", exception.getMessage());
    }

    @Test
    void registration_NullBirthdate() {
        int id = 1;
        String username = "testuser";
        String encryption = "testpassword";
        LocalDate birthdate = null;

        Exception exception = assertThrows(Exception.class, () -> userFacade.register( username, encryption, birthdate));
        assertEquals("Birthdate password is null", exception.getMessage());
    }

    @Test
    void registration_DuplicateUsername() throws Exception {
        int id = 2;
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