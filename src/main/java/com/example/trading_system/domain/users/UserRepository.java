package com.example.trading_system.domain.users;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

public interface UserRepository {
    void deleteInstance();
    User getUser(String username);

    boolean isExist(String username);

    HashMap<String, User> getAllUsers();

    Collection<User> getAllUsersAsList();

    Collection<String> getAllUsersAsUsernames();

    void deleteUser(String username);

    boolean isEmpty();

    void addVisitor(String username);

    void addRegistered(String userName, String encryption, LocalDate birthdate);
}
