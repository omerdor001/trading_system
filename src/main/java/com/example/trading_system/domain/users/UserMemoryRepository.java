package com.example.trading_system.domain.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@Service
public class UserMemoryRepository implements UserRepository {
    private static UserMemoryRepository instance = null;
    private HashMap<String, User> users;
    private final UserDatabaseRepository userDatabaseRepository;

    @Autowired
    private UserMemoryRepository(UserDatabaseRepository userDatabaseRepository) {
        this.userDatabaseRepository = userDatabaseRepository;
        users = new HashMap<>();
    }

    public static UserMemoryRepository getInstance(UserDatabaseRepository userDatabaseRepository) {
        if (instance == null) instance = new UserMemoryRepository(userDatabaseRepository);
        return instance;
    }

    @Override
    public void deleteInstance() {
        if (users != null) {
            this.users.clear();
            this.users = null;
        }
        instance = null;
        userDatabaseRepository.deleteInstance();
    }

    @Override
    public User getUser(String username) {
        if (users.containsKey(username)) {
            return users.get(username);
        } else {
            return userDatabaseRepository.getUser(username);
        }
    }

    @Override
    public boolean isExist(String username) {
        if (users.containsKey(username)) {
            return true;
        } else {
            return userDatabaseRepository.isExist(username);
        }
    }

    @Override
    public boolean isAdmin(String username) {
        if (isExist(username)) return getUser(username).isAdmin();
        return false;
    }

    @Override
    public boolean isAdminRegistered() {
        for (User r : users.values()) {
            if (r.isAdmin()) {
                return true;
            }
        }
        return userDatabaseRepository.isAdminRegistered();
    }

    @Override
    public HashMap<String, User> getAllUsers() {
        HashMap<String, User> allUsers = new HashMap<>(users);
        allUsers.putAll(userDatabaseRepository.getAllUsers());
        return allUsers;
    }

    @Override
    public Collection<User> getAllUsersAsList() {
        Collection<User> allUsers = users.values();
        allUsers.addAll(userDatabaseRepository.getAllUsersAsList());
        return allUsers;
    }

    @Override
    public void deleteUser(String username) {
        users.remove(username);
        userDatabaseRepository.deleteUser(username);
    }

    @Override
    public boolean isEmpty() {
        return users.isEmpty() && userDatabaseRepository.isEmpty();
    }

    @Override
    public void addVisitor(String username) {
        users.put(username, new Visitor(username.substring(1)));
    }

    @Override
    public void addRegistered(String userName, String encryption, LocalDate birthdate) {
        users.put(userName, new Registered(userName, encryption, birthdate));
        userDatabaseRepository.addRegistered(userName, encryption, birthdate);
    }

    @Override
    public void saveUser(User user) {
        users.put(user.getUsername(), user);
        userDatabaseRepository.saveUser(user);
    }

    @Override
    public boolean checkIfRegistersEmpty() {
        for (String username : users.keySet()) {
            if (username.startsWith("r")) return false;
        }
        return userDatabaseRepository.checkIfRegistersEmpty();
    }
}
