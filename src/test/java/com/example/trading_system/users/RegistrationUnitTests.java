package com.example.trading_system.users;

import com.example.trading_system.domain.users.UserFacadeImp;
import com.example.trading_system.domain.users.Visitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrationUnitTests {
    private UserFacadeImp userFacade;

    @BeforeEach
    void setUp() {
        userFacade = new UserFacadeImp();
        // Adding a sample visitor
        userFacade.getVisitors().put(1, new Visitor(1));
        userFacade.getVisitors().put(2, new Visitor(2));
        userFacade.getVisitors().put(3, new Visitor(3));
    }

    @Test
    void registration_Success() throws Exception {
        int id = 1;
        String username = "testuser";
        String encryption = "testpassword";
        LocalDate birthdate = LocalDate.of(1990, 5, 15);

        assertDoesNotThrow(() -> userFacade.registration(id, username, encryption, birthdate));
        assertTrue(userFacade.getRegisters().containsKey(username));
        assertFalse(userFacade.getVisitors().containsKey(id));
    }

    @Test
    void registration_NullUsername() {
        int id = 1;
        String username = null;
        String encryption = "testpassword";
        LocalDate birthdate = LocalDate.of(1990, 5, 15);

        Exception exception = assertThrows(Exception.class, () -> userFacade.registration(id, username, encryption, birthdate));
        assertEquals("Username is null", exception.getMessage());
    }

    @Test
    void registration_EmptyUsername() {
        int id = 1;
        String username = "";
        String encryption = "testpassword";
        LocalDate birthdate = LocalDate.of(1990, 5, 15);

        Exception exception = assertThrows(Exception.class, () -> userFacade.registration(id, username, encryption, birthdate));
        assertEquals("Username is empty", exception.getMessage());
    }

    @Test
    void registration_NullEncryption() {
        int id = 1;
        String username = "testuser";
        String encryption = null;
        LocalDate birthdate = LocalDate.of(1990, 5, 15);

        Exception exception = assertThrows(Exception.class, () -> userFacade.registration(id, username, encryption, birthdate));
        assertEquals("Encrypted password is null", exception.getMessage());
    }

    @Test
    void registration_EmptyEncryption() {
        int id = 1;
        String username = "testuser";
        String encryption = "";
        LocalDate birthdate = LocalDate.of(1990, 5, 15);

        Exception exception = assertThrows(Exception.class, () -> userFacade.registration(id, username, encryption, birthdate));
        assertEquals("Encrypted password is empty", exception.getMessage());
    }

    @Test
    void registration_NullBirthdate() {
        int id = 1;
        String username = "testuser";
        String encryption = "testpassword";
        LocalDate birthdate = null;

        Exception exception = assertThrows(Exception.class, () -> userFacade.registration(id, username, encryption, birthdate));
        assertEquals("Birthdate password is null", exception.getMessage());
    }

    @Test
    void registration_DuplicateUsername() throws Exception {
        int id = 2;
        String username = "testuser";
        String encryption = "testpassword";
        LocalDate birthdate = LocalDate.of(1990, 5, 15);
        // Add the user once
        userFacade.registration(id, username, encryption, birthdate);
        // Attempt to add the same user again
        Exception exception = assertThrows(Exception.class, () -> userFacade.registration(3, username, "newpassword", LocalDate.of(1991, 6, 20)));
        assertEquals("username already exists - " + username, exception.getMessage());
    }

    @Test
    void registration_NoVisitorWithId() {
        int id = 4; // Assuming visitor with id 4 doesn't exist
        String username = "testuser";
        String encryption = "testpassword";
        LocalDate birthdate = LocalDate.of(1990, 5, 15);

        Exception exception = assertThrows(Exception.class, () -> userFacade.registration(id, username, encryption, birthdate));
        assertEquals("No visitor with id - " + id, exception.getMessage());
    }

}