package com.example.trading_system.UnitTests.users;

import com.example.trading_system.service.UserService;
import com.example.trading_system.service.UserServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;


class AppointManagerUnitTests {

    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = UserServiceImp.getInstance();
    }

    @Test
    void appointManager_Success() {
        try {
            userService.register("appointTest", "encryption1", LocalDate.of(2000, 1, 1));
            userService.register("newManagerTest", "encryption2", LocalDate.of(2000, 1, 1));
            //TODO owner appointTest
            userService.login("2", "appointTest", "encryption1");
        }
        catch (Exception _){
        }
    }
}