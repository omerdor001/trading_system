package com.example.trading_system.UnitTests.users;
import com.example.trading_system.domain.users.UserFacadeImp;
import com.example.trading_system.service.Security;
import com.example.trading_system.service.UserServiceImp;
import net.bytebuddy.dynamic.scaffold.FieldLocator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LogoutUnitTests {
    private UserFacadeImp userFacade;
    int id = 1;
    String username = "testuser";
    String encryption = "testpassword";
    LocalDate birthdate = LocalDate.of(1990, 5, 15);


    @BeforeEach
    void setUp() throws Exception {
        userFacade = UserFacadeImp.getInstance();
        userFacade.createVisitor(1);
        userFacade.register(id, username, encryption, birthdate);
        userFacade.login(username);
    }

    @Test
    void logout_Success() {
        assertDoesNotThrow(() -> userFacade.logout(id, username));
        assertEquals(userFacade.getRegistered().get(username).getLogged(), false);
    }

    @Test
    void logout_NonExistentUser_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                userFacade.logout(0, null));
    }

    @Test
    void logout_UserAlreadyLoggedOut_ThrowsException() {
        userFacade.logout(1, username);
        assertThrows(RuntimeException.class, () ->
                userFacade.logout(id, username));
    }


}