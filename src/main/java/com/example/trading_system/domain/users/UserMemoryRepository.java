package com.example.trading_system.domain.users;

import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Repository
public class UserMemoryRepository implements UserRepository {
    private static UserMemoryRepository instance = null;
    private HashMap<String, User> users;

    public UserMemoryRepository() {
        users = new HashMap<>();
    }

    public static UserMemoryRepository getInstance() {
        if (instance == null) instance = new UserMemoryRepository();
        return instance;
    }

    @Override
    public void deleteInstance() {
        if (users != null) {
            this.users.clear();
            this.users = null;
        }
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
    public boolean isAdmin(String username) {
        if (isExist(username)) return users.get(username).isAdmin();
        return false;
    }

    @Override
    public boolean isAdminRegistered() {
        for (User r : users.values())
            if (r.isAdmin()) {
                return true;
            }
        return false;
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

    @Override
    public void saveUser(User user) {
    }

    @Override
    public void saveUsers(List<User> users) {
    }

    @Override
    public boolean checkIfRegistersEmpty() {
        for (String username : users.keySet()) {
            if (username.startsWith("r")) return false;
        }
        return true;
    }

    @Override
    public void saveCart(User user) {
    }

    @Override
    public Registered getRegistered(String userName) {
        if (userName.charAt(0) != 'r') throw new RuntimeException("User is not registerd");
        else return (Registered) users.get(userName);
    }

    @Override
    public void deleteData() {
        this.users.clear();
    }
}
