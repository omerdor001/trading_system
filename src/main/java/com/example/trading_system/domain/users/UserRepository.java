package com.example.trading_system.domain.users;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public interface UserRepository {
    void deleteInstance();

    User getUser(String username);

    boolean isExist(String username);

    boolean isAdmin(String username);

    boolean isAdminRegistered();

    HashMap<String, User> getAllUsers();

    Collection<User> getAllUsersAsList();

    void deleteUser(String username);

    boolean isEmpty();

    void addVisitor(String username);

    void addRegistered(String userName, String encryption, LocalDate birthdate);

    void saveUser(User user);

    boolean checkIfRegistersEmpty();

    void saveUsers(List<User> users);

    void saveCart(User user);

    Registered getRegistered(String userName);

    void deleteData();
}
