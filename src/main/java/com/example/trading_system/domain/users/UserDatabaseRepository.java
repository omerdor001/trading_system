package com.example.trading_system.domain.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class UserDatabaseRepository implements UserRepository {
    private static UserDatabaseRepository instance = null;
    private HashMap<String, User> visitors;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserDatabaseRepository() {
        visitors = new HashMap<>();
    }

    public static UserDatabaseRepository getInstance() {
        if (instance == null)
            instance = new UserDatabaseRepository();
        return instance;
    }

    @Override
    public void deleteInstance() {
        if (visitors != null) {
            this.visitors.clear();
            this.visitors = null;
        }
        instance = null;
    }

    @Override
    public User getUser(String username) {
        if (username.startsWith("v")) {
            return visitors.get(username);
        } else {
            return entityManager.find(User.class, username);
        }
    }

    @Override
    public boolean isExist(String username) {
        if (username.startsWith("v")) {
            return visitors.containsKey(username);
        } else {
            return entityManager.find(User.class, username) != null;
        }
    }

    @Override
    public boolean isAdmin(String username) {
        if (isExist(username))
            return getUser(username).isAdmin();
        return false;
    }

    @Override
    public boolean isAdminRegistered() {
        List<User> registeredUsers = entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
        for (User user : registeredUsers) {
            if (user.isAdmin())
                return true;
        }
        return false;
    }

    @Override
    public HashMap<String, User> getAllUsers() {
        HashMap<String, User> allUsers = new HashMap<>(visitors);
        List<User> registeredUsers = entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
        for (User user : registeredUsers) {
            allUsers.put(user.getUsername(), user);
        }
        return allUsers;
    }

    @Override
    public Collection<User> getAllUsersAsList() {
        List<User> registeredUsers = entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
        registeredUsers.addAll(visitors.values());
        return registeredUsers;
    }

    @Override
    public void deleteUser(String username) {
        if (username.startsWith("v")) {
            visitors.remove(username);
        } else {
            User user = entityManager.find(User.class, username);
            if (user != null) {
                entityManager.remove(user);
            }
        }
    }

    @Override
    public boolean isEmpty() {
        Long count = entityManager.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult();
        return visitors.isEmpty() && count == 0;
    }

    @Override
    public void addVisitor(String username) {
        visitors.put(username, new Visitor(username.substring(1)));
    }

    @Override
    public void addRegistered(String userName, String encryption, LocalDate birthdate) {
        Registered user = new Registered(userName, encryption, birthdate);
        entityManager.persist(user);
    }

    @Override
    public void saveUser(User user) {
        entityManager.persist(user);
    }

    @Override
    public boolean checkIfRegistersEmpty() {
        Long count = entityManager.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult();
        return count > 0;
    }
}
