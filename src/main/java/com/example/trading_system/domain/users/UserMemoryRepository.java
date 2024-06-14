package com.example.trading_system.domain.users;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

public class UserMemoryRepository implements UserRepository {
    private static UserMemoryRepository instance = null;
    private HashMap<String, User> users;

    private UserMemoryRepository() {
        users = new HashMap<>();
    }

    public static UserMemoryRepository getInstance() {
        if (instance == null)
            instance = new UserMemoryRepository();
        return instance;
    }

    @Override
    public void deleteInstance() {
        this.users.clear();
        this.users = null;
        instance = null;
    }

    @Override
    public User getUser(String username) {
        return users.get(username);
    }

    @Override
    public boolean isExist(String username) {
        return users.containsKey(username);
    }

    @Override
    public HashMap<String, User> getAllUsers() {
        return users;
    }

    @Override
    public Collection<User> getAllUsersAsList() {
        return users.values();
    }

    @Override
    public Collection<String> getAllUsersAsUsernames() {
        return users.keySet();
    }

    @Override
    public void deleteUser(String username) {
        users.remove(username);
    }

    @Override
    public boolean isEmpty() {
        return users.isEmpty();
    }

    @Override
    public void addVisitor(String username) {
        users.put(username, new Visitor(username.substring(1)));
    }

    @Override
    public void addRegistered(String userName, String encryption, LocalDate birthdate) {
        users.put(userName, new Registered(userName.substring(1), encryption, birthdate));
    }
}
