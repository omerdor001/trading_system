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
        userFacade.getVisitors().put(1, new Visitor(1));
        userFacade.getVisitors().put(2, new Visitor(2));
        userFacade.getVisitors().put(3, new Visitor(3));
    }

    @AfterEach
    void reset() {
        userFacade.getVisitors().clear();
        userFacade.getRegistered().clear();
    }

    @Test
    void registration_Success(){
        int id = 1;
        String username = "testuser";
        String encryption = "testpassword";
        LocalDate birthdate = LocalDate.of(1990, 5, 15);

        assertDoesNotThrow(() -> userFacade.register(id, username, encryption, birthdate));
        assertTrue(userFacade.getRegistered().containsKey(username));
        assertTrue(userFacade.getVisitors().containsKey(id));
    }

    @Test
    void registration_NullUsername() {
        int id = 1;
        String username = null;
        String encryption = "testpassword";
        LocalDate birthdate = LocalDate.of(1990, 5, 15);

        Exception exception = assertThrows(Exception.class, () -> userFacade.register(id, username, encryption, birthdate));
        assertEquals("Username is null", exception.getMessage());
    }

    @Test
    void registration_EmptyUsername() {
        int id = 1;
        String username = "";
        String encryption = "testpassword";
        LocalDate birthdate = LocalDate.of(1990, 5, 15);

        Exception exception = assertThrows(Exception.class, () -> userFacade.register(id, username, encryption, birthdate));
        assertEquals("Username is empty", exception.getMessage());
    }

    @Test
    void registration_NullEncryption() {
        int id = 1;
        String username = "testuser";
        String encryption = null;
        LocalDate birthdate = LocalDate.of(1990, 5, 15);

        Exception exception = assertThrows(Exception.class, () -> userFacade.register(id, username, encryption, birthdate));
        assertEquals("Encrypted password is null", exception.getMessage());
    }

    @Test
    void registration_EmptyEncryption() {
        int id = 1;
        String username = "testuser";
        String encryption = "";
        LocalDate birthdate = LocalDate.of(1990, 5, 15);

        Exception exception = assertThrows(Exception.class, () -> userFacade.register(id, username, encryption, birthdate));
        assertEquals("Encrypted password is empty", exception.getMessage());
    }

    @Test
    void registration_NullBirthdate() {
        int id = 1;
        String username = "testuser";
        String encryption = "testpassword";
        LocalDate birthdate = null;

        Exception exception = assertThrows(Exception.class, () -> userFacade.register(id, username, encryption, birthdate));
        assertEquals("Birthdate password is null", exception.getMessage());
    }

    @Test
    void registration_DuplicateUsername() throws Exception {
        int id = 2;
        String username = "testuser";
        String encryption = "testpassword";
        LocalDate birthdate = LocalDate.of(1990, 5, 15);
        // Add the user once
        userFacade.register(id, username, encryption, birthdate);
        // Attempt to add the same user again
        Exception exception = assertThrows(Exception.class, () -> userFacade.register(3, username, "newpassword", LocalDate.of(1991, 6, 20)));
        assertEquals("username already exists - " + username, exception.getMessage());
    }

}