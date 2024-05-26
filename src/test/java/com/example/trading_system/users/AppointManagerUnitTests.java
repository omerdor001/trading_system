package com.example.trading_system.users;

import com.example.trading_system.domain.users.UserFacade;
import com.example.trading_system.domain.users.UserFacadeImp;
import com.example.trading_system.service.UserService;
import com.example.trading_system.service.UserServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;


class AppointManagerUnitTests {

    private UserFacade userFacade;
    private UserService userService;
    @BeforeEach
    public void setUp() {
        userFacade=new UserFacadeImp();
        userService=new UserServiceImp(userFacade);
    }

    @Test
    void appointManager_Success() {
        userService.register(2,"appointTest","encryption1", LocalDate.of(2000,1,1));
        userService.register(3,"newManagerTest","encryption2", LocalDate.of(2000,1,1));
        //TODO owner appointTest
        userService.login(2,"appointTest","encryption1");







    }
}