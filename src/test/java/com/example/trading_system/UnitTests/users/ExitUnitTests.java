package com.example.trading_system.UnitTests.users;

import com.example.trading_system.domain.users.UserFacadeImp;
import com.example.trading_system.domain.users.Visitor;
import com.example.trading_system.domain.users.Registered;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ExitUnitTests {
    private UserFacadeImp userFacade;

    @BeforeEach
    void setUp() {
        userFacade = UserFacadeImp.getInstance();
        // Adding sample visitors and registered users
        userFacade.getUsers().put("v1", new Visitor("v1"));
        userFacade.getUsers().put("v2", new Visitor("v2"));
        userFacade.getUsers().put("v3", new Visitor("v3"));
        userFacade.getUsers().put("ruser1", new Registered( "user1", "password1", LocalDate.of(1990, 5, 15)));
        userFacade.getUsers().put("ruser2", new Registered( "user2", "password2", LocalDate.of(1991, 6, 20)));
    }

    @Test
    void exitVisitor_Success() {
        int id = 1;
        assertDoesNotThrow(() -> userFacade.exit("v"+id));
        assertFalse(userFacade.getUsers().containsKey("v"+id));
    }

    @Test
    void exitVisitor_NoSuchVisitor() {
        int id = 4; // Assuming visitor with id 4 doesn't exist
        Exception exception = assertThrows(Exception.class, () -> userFacade.exit("v"+id));
        assertEquals("No such visitor with id- " + id, exception.getMessage());
    }

    @Test
    void exitRegisteredUser_Success() {
        String username = "user1";
        assertDoesNotThrow(() -> userFacade.exit(username));
        assertFalse(userFacade.getUsers().containsKey("r"+username));
    }

    @Test
    void exitRegisteredUser_NoSuchUser() {
        String username = "nonexistent"; // Assuming this user doesn't exist
        Exception exception = assertThrows(Exception.class, () -> userFacade.exit("v"+username));
        assertEquals("No such user with username- " + username, exception.getMessage());
    }
}