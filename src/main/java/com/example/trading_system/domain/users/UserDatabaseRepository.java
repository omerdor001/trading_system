    package com.example.trading_system.domain.users;

    import jakarta.persistence.metamodel.EntityType;
    import org.springframework.beans.factory.annotation.Autowired;

    import jakarta.persistence.EntityManager;
    import jakarta.persistence.PersistenceContext;
    import org.springframework.context.annotation.Primary;
    import org.springframework.stereotype.Repository;
    import org.springframework.stereotype.Service;

    import java.time.LocalDate;
    import java.util.*;

//    @Primary
//    @Repository
    public class UserDatabaseRepository implements UserRepository {

        private HashMap<String, User> visitors = new HashMap<>();

//        @PersistenceContext
        private EntityManager entityManager;

//        @Autowired
        public UserDatabaseRepository(EntityManager entityManager) {
            this.entityManager = entityManager;
        }

    //    public static UserDatabaseRepository getInstance() {
    //        if (instance == null)
    //            instance = new UserDatabaseRepository();
    //        return instance;
    //    }

        @Override
        public void deleteInstance() {
            if (visitors != null) {
                this.visitors.clear();
                this.visitors = null;
            }
        }

        @Override
        public User getUser(String username) {
            if (username.startsWith("v")) {
                return visitors.get(username);
            } else {
                return entityManager.find(Registered.class, username);
            }
        }

        @Override
        public Registered getRegistered(String userName) throws Exception{
            return entityManager.find(Registered.class,userName);
        }

        @Override
        public boolean isExist(String username) {
            if (username.startsWith("v")) {
                return visitors.containsKey(username);
            } else {
                return entityManager.find(Registered.class, username) != null;
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
    //        System.out.println(entityManager.createNativeQuery("SELECT @@SERVERNAME").getSingleResult());
    //        System.out.println(entityManager.createNativeQuery("SELECT DB_NAME() AS DatabaseName").getSingleResult());
            Set<EntityType<?>> entities = entityManager.getEntityManagerFactory().getMetamodel().getEntities();
            for (EntityType<?> entity : entities) {
                System.out.println("Entity Name: " + entity.getName());
                System.out.println("Java Type: " + entity.getJavaType().getName());
                System.out.println("Table Name: " + entity.getName());
                System.out.println("-----------------------------------");
            }
            List<Registered> registeredUsers = entityManager.createQuery("SELECT u FROM Registered u", Registered.class).getResultList();
            for (User user : registeredUsers) {
                if (user.isAdmin())
                    return true;
            }
            return false;
        }

        @Override
        public HashMap<String, User> getAllUsers() {
            HashMap<String, User> allUsers = new HashMap<>(visitors);
            List<Registered> registeredUsers = entityManager.createQuery("SELECT u FROM Registered u", Registered.class).getResultList();
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
                Registered user = entityManager.find(Registered.class, username);
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
            Registered user = new Registered(userName, encryption, birthdate);
            entityManager.persist(user);
        }

        @Override
        public void saveUser(User user) {
            entityManager.persist(user);
        }

        @Override
        public void saveUsers(List<User> users){
            for (User user: users)
                saveUser(user);
        }

        @Override
        public boolean checkIfRegistersEmpty() {
            Long count = entityManager.createQuery("SELECT COUNT(u) FROM Registered u", Long.class).getSingleResult();
            return count > 0;
        }
    }
