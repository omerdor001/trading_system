package com.example.trading_system.domain.users;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public interface UserRepository {
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
