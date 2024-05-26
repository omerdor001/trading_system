package com.example.trading_system.users;

import com.example.trading_system.domain.users.Registered;
import com.example.trading_system.domain.users.User;
import com.example.trading_system.domain.users.UserFacade;
import com.example.trading_system.service.Security;
import com.example.trading_system.service.UserServiceImp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LogoutUnitTests {

    @Mock
    UserFacade userFacade;
    @Mock
    UserServiceImp userService;
    @Mock
    Registered registered;

    String username1 = "testUser";
    String password = "password123";
    int userId = 1;


    @BeforeEach
    void setUp() {
        userFacade = Mockito.mock(UserFacade.class);
        userService = new UserServiceImp(userFacade);
    }


    @ParameterizedTest
    @ValueSource(strings = {"testVisitor"})
    void givenValidCredentials_whenNotRegistered_thenThrowNewException(String username) {

        Mockito.when(userFacade.getRegistered().get(username)).thenReturn(null);
//        Mockito.when(userFacade.getRegistered().get(username)).thenReturn(registered);

        Assertions.assertTrue(userService.logout(userId, username), "logout should not be successful");

        verify(userFacade, times(1)).saveUserCart(username);
        verify(userFacade, times(1)).logout(username);
    }



    @Test
    void givenValidCredentials_whenNotSavedCart_thenThrowNewException() {

//        Mockito.doThrow(/*some Exception*/)when(userFacade.saveUserCart(username1));
        Mockito.when(userFacade.getRegistered().get(username1)).thenReturn(registered);

        boolean logoutResult = userService.logout(userId, username1);
        Assertions.assertTrue(logoutResult, "logout should not be successful");

        verify(userFacade, times(1)).saveUserCart(username1);
        verify(userFacade, times(1)).logout(username1);

    }




    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"testVisitor"})
    void givenValidUser_whenLoggedIn_thenSuccess(String username) {

        Mockito.when(userFacade.getRegistered().get(username)).thenReturn(registered);

        boolean logoutResult = userService.logout(userId, username);
        Assertions.assertTrue(logoutResult, "logout should be successful");

        verify(userFacade, times(1)).saveUserCart(username);
        verify(userFacade, times(1)).logout(username);
    }

}
