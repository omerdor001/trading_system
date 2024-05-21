package com.example.trading_system.domain.users;
import java.time.LocalDate;

public interface UserFacade {

    void exit();
    void registration(int id, String username, String token, LocalDate birthdate) throws Exception;    //Complete with what to register
    void login();     //Complete with what to login
    void addUser(User user);
    void removeUser(User user);
    boolean sendNotification(User sender, User receiver, String content);
}
