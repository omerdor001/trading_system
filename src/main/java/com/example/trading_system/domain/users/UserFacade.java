package com.example.trading_system.domain.users;

public interface UserFacade {
    void exit();
    void registration();    //Complete with what to register
    void login();     //Complete with what to login
    void addUser(User user);
    void removeUser(User user);
}
