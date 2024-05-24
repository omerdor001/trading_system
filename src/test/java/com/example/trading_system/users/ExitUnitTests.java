package com.example.trading_system.users;

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
        userFacade = new UserFacadeImp();
        // Adding sample visitors and registered users
        userFacade.getVisitors().put(1, new Visitor(1));
        userFacade.getVisitors().put(2, new Visitor(2));
        userFacade.getVisitors().put(3, new Visitor(3));
        userFacade.getRegisters().put("user1", new Registered(4, "user1", "password1", LocalDate.of(1990, 5, 15)));
        userFacade.getRegisters().put("user2", new Registered(5, "user2", "password2", LocalDate.of(1991, 6, 20)));
    }

    @Test
    void exitVisitor_Success() {
        int id = 1;
        assertDoesNotThrow(() -> userFacade.exit(id));
        assertFalse(userFacade.getVisitors().containsKey(id));
    }

    @Test
    void exitVisitor_NoSuchVisitor() {
        int id = 4; // Assuming visitor with id 4 doesn't exist
        Exception exception = assertThrows(Exception.class, () -> userFacade.exit(id));
        assertEquals("No such visitor with id- " + id, exception.getMessage());
    }

    @Test
    void exitRegisteredUser_Success() {
        String username = "user1";
        assertDoesNotThrow(() -> userFacade.exit(username));
        assertFalse(userFacade.getRegisters().containsKey(username));
    }

    @Test
    void exitRegisteredUser_NoSuchUser() {
        String username = "nonexistent"; // Assuming this user doesn't exist
        Exception exception = assertThrows(Exception.class, () -> userFacade.exit(username));
        assertEquals("No such user with username- " + username, exception.getMessage());
    }
}