package com.example.trading_system.UnitTests.users;

import com.example.trading_system.domain.users.UserFacadeImp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class EnterUnitTests {
    private UserFacadeImp userFacade;

    @BeforeEach
    void setUp() {
        userFacade = UserFacadeImp.getInstance();
        // Ensure the visitors map is cleared before each test
        userFacade.getUsers().clear();
    }

    @AfterEach
    void setDown(){
        userFacade.deleteInstance();
    }

    @Test
    void enterVisitor_Success() {
        int id = 1;
        assertDoesNotThrow(() -> userFacade.enter(id));
        assertTrue(userFacade.isUserExist("v"+id));
    }

    @Test
    void enterVisitor_ExistingVisitor() {
        int id = 1;
        userFacade.enter(id); // Enter the visitor for the first time
        assertDoesNotThrow(() -> userFacade.enter(id)); // Enter the visitor again
        assertEquals(1, userFacade.getUsers().size());
    }

    @Test
    void enterMultipleVisitors() {
        String id1 ="v1";
        String id2 = "v2";
        String id3 = "v3";

        userFacade.enter(1);
        userFacade.enter(2);
        userFacade.enter(3);

        assertTrue(userFacade.isUserExist(id1));
        assertTrue(userFacade.isUserExist(id2));
        assertTrue(userFacade.isUserExist(id3));
        assertEquals(3, userFacade.getUsers().size());
    }

}
