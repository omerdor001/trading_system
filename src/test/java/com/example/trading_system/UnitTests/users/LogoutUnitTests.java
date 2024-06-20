package com.example.trading_system.UnitTests.users;

import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.users.Cart;
import com.example.trading_system.domain.users.User;
import com.example.trading_system.domain.users.UserFacadeImp;
import org.junit.jupiter.api.*;
import org.mockito.*;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class LogoutUnitTests {

    @Mock
    User user;
    @Mock
    Cart shoppingCart;

    UserFacadeImp userFacadeImp;

    @BeforeEach
    public void init() {
        initMocks(this);
        userFacadeImp = UserFacadeImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class));
    }

    @AfterEach
    public void tearDown() {
        userFacadeImp.deleteInstance();
    }

    @Test
    public void givenValidUser_WhenLogout_ThenSuccess() {
        String username = "rValidUser";
        int id = 123;

        when(user.getLogged()).thenReturn(true);
        when(user.getCart()).thenReturn(shoppingCart);
        userFacadeImp.getUsers().put(username, user);

        Assertions.assertDoesNotThrow(() -> userFacadeImp.logout(id, username));

        verify(user).getLogged();
        verify(user).logout();
        verify(shoppingCart).saveCart();
    }

    @Test
    public void givenNullUsername_WhenLogout_ThenThrowException() {
        int id = 123;
        //String username = null;

        Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.logout(id, null));

        verify(user, never()).getLogged();
    }

    @Test
    public void givenEmptyUsername_WhenLogout_ThenThrowException() {
        int id = 123;
        String username = "";

        Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.logout(id, username));

        verify(user, never()).getLogged();
    }

    @Test
    public void givenInvalidUserName_WhenLogout_ThenThrowException() {
        int id = 123;
        String invalidUsername = "vInvalidUser"; // Non-registered user format

        Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.logout(id, invalidUsername));

        verify(user, never()).getLogged();
    }

    @Test
    public void givenUserAlreadyLoggedOut_WhenLogout_ThenThrowException() {
        String username = "rValidUser";
        int id = 123;

        when(user.getLogged()).thenReturn(false);
        userFacadeImp.getUsers().put(username, user);

        Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.logout(id, username));

        verify(user).getLogged();
        verify(user, never()).logout();
        verify(shoppingCart, never()).saveCart();
    }

    @Test
    public void givenNonRegisteredUserPerformingLogout_WhenLogout_ThenThrowException() {
        int id = 123;
        String nonRegisteredUsername = "rNonRegisteredUser";

        Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.logout(id, nonRegisteredUsername));

        verify(user, never()).getLogged();
    }

    @Test
    public void givenNonExistingUser_WhenLogout_ThenThrowException() {
        String username = "rNonExistingUser";
        int id = 123;

        Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.logout(id, username));

        verify(user, never()).getLogged();
        verify(user, never()).logout();
        verify(shoppingCart, never()).saveCart();
    }

    @Test
    public void givenUserWithoutCart_WhenSaveUserCart_ThenThrowException() {
        String username = "rValidUserWithoutCart";

        when(user.getCart()).thenReturn(null);
        userFacadeImp.getUsers().put(username, user);

        Assertions.assertThrows(IllegalArgumentException.class, () -> userFacadeImp.logout(123, username));

        verify(shoppingCart, never()).saveCart();
    }
}
