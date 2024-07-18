package com.example.trading_system.domain.users;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDate;
import java.util.*;

@Repository
@Transactional
public class UserDatabaseRepository implements UserRepository {

    private static UserDatabaseRepository instance = null;

    @PersistenceContext
    private EntityManager entityManager;

    private HashMap<String, User> visitors = new HashMap<>();

    public UserDatabaseRepository() {
    }

    public void setEntityManager(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    public static UserDatabaseRepository getInstance(EntityManager entityManager) {
        if (instance == null) {
            instance = new UserDatabaseRepository();
            instance.entityManager = entityManager;
        }
        return instance;
    }

    @Override
    public void deleteInstance() {
        visitors.clear();
        instance = null;
    }

    @Override
    public User getUser(String username) {
        if (username.startsWith("v")) {
            return visitors.get(username);
        } else {
            return entityManager.find(Registered.class, username.substring(1));
        }
    }

    @Override
    public boolean isExist(String username) {
        if (username.startsWith("v")) {
            return visitors.containsKey(username);
        } else {
            boolean b=entityManager.find(Registered.class, username.substring(1)) != null;
            return b;
        }
    }

    @Override
    public boolean isAdmin(String username) {
        User user = getUser(username);
        return user != null && user.isAdmin();
    }

    @Override
    public boolean isAdminRegistered() {
        List<Registered> registeredUsers = entityManager.createQuery("SELECT u FROM Registered u", Registered.class).getResultList();
        for (User user : registeredUsers) {
            if (user.isAdmin()) return true;
        }
        return false;
    }

    public void saveCart(User user) {
        entityManager.merge(user.getCart());
    }

    @Override
    public HashMap<String, User> getAllUsers() {
        List<Registered> registeredUsers = entityManager.createQuery("SELECT u FROM Registered u", Registered.class).getResultList();
        HashMap<String, User> allUsers = new HashMap<>(visitors);
        for (User user : registeredUsers) {
            allUsers.put(user.getUsername(), user);
        }
        return allUsers;
    }

    @Override
    public Collection<User> getAllUsersAsList() {
        List<Registered> registeredUsers = entityManager.createQuery("SELECT u FROM Registered u", Registered.class).getResultList();
        List<User> allUsers = new ArrayList<>(registeredUsers);
        allUsers.addAll(visitors.values());
        return allUsers;
    }

    @Override
    public void deleteUser(String username) {
        if (username.startsWith("v")) {
            visitors.remove(username);
        } else {
            Registered user = entityManager.find(Registered.class, username.substring(1));
            if (user != null) {
                entityManager.remove(user);
            }
        }
    }

    @Override
    public boolean isEmpty() {
        Long count = entityManager.createQuery("SELECT COUNT(u) FROM Registered u", Long.class).getSingleResult();
        return visitors.isEmpty() && count == 0;
    }

    @Override
    public void addVisitor(String username) {
        visitors.put(username, new Visitor(username.substring(1)));
    }

    @Override

    public void addRegistered(String userName, String encryption, LocalDate birthdate) {
        Registered user = new Registered(userName.substring(1), encryption, birthdate);
        entityManager.persist(user);
    }

    @Override
    public void saveUser(User user) {
        if (user instanceof Registered) {
            Registered registeredUser = (Registered) user;
            entityManager.merge(registeredUser); // merge the user first
            Cart cart = registeredUser.getCart();
            if (cart != null) {
                entityManager.merge(cart); // merge the cart
            }
            for (Role role : registeredUser.getRoles()) {
                role.setRegisteredUser(registeredUser);
                entityManager.merge(role); // merge the role
                entityManager.merge(role.getRoleState()); // merge the role state
            }
        }
    }

    @Override
    public void saveUsers(List<User> users) {
        for (User user : users)
            saveUser(user);
    }

    @Override
    public boolean checkIfRegistersEmpty() {
        Long count = entityManager.createQuery("SELECT COUNT(u) FROM Registered u", Long.class).getSingleResult();
        return count == 0;
    }

    @Override
    public Registered getRegistered(String userName) {
        Registered user = entityManager.find(Registered.class, userName);
        if (user == null) throw new RuntimeException("No such user");
        return user;
    }

    @Override
    public void deleteData() {
        entityManager.createQuery("DELETE FROM Registered").executeUpdate();
        this.visitors.clear();
    }
}
