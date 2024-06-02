package com.example.trading_system.UnitTests.users;

import com.example.trading_system.domain.users.UserFacadeImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class EnterUnitTests {
    private UserFacadeImp userFacade;

    @BeforeEach
    void setUp() {
        userFacade = UserFacadeImp.getInstance();
        // Ensure the visitors map is cleared before each test
        userFacade.getVisitors().clear();
    }

    @Test
    void enterVisitor_Success() {
        int id = 1;
        assertDoesNotThrow(() -> userFacade.enter(id));
        assertTrue(userFacade.getVisitors().containsKey(id));
    }

    @Test
    void enterVisitor_ExistingVisitor() {
        int id = 1;
        userFacade.enter(id); // Enter the visitor for the first time
        assertDoesNotThrow(() -> userFacade.enter(id)); // Enter the visitor again
        assertEquals(1, userFacade.getVisitors().size());
    }

    @Test
    void enterMultipleVisitors() {
        int id1 = 1;
        int id2 = 2;
        int id3 = 3;

        userFacade.enter(id1);
        userFacade.enter(id2);
        userFacade.enter(id3);

        assertTrue(userFacade.getVisitors().containsKey(id1));
        assertTrue(userFacade.getVisitors().containsKey(id2));
        assertTrue(userFacade.getVisitors().containsKey(id3));
        assertEquals(3, userFacade.getVisitors().size());
    }

}
