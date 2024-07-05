package com.example.trading_system.domain.users;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Transactional
public class UserFacadeImp implements UserFacade {
    private static final Logger logger = LoggerFactory.getLogger(UserFacadeImp.class);
    private static UserFacadeImp instance = null;
    private final NotificationSender notificationSender;
    private UserRepository userRepository;
    private DeliveryService deliveryService;
    private PaymentService paymentService;
    private MarketFacade marketFacade;

    public UserFacadeImp(PaymentService paymentService, DeliveryService deliveryService, NotificationSender notificationSender, UserRepository userRepository, StoreRepository storeRepository) {
        this.paymentService = paymentService;
        this.deliveryService = deliveryService;
        this.userRepository = userRepository;
        this.marketFacade = MarketFacadeImp.getInstance(storeRepository);
        this.notificationSender = notificationSender;
        marketFacade.initialize(this);
    }

    public static UserFacadeImp getInstance(PaymentService paymentService, DeliveryService deliveryService, NotificationSender notificationSender, UserRepository userRepository, StoreRepository storeRepository) {
        if (instance == null) {
            instance = new UserFacadeImp(paymentService, deliveryService, notificationSender, userRepository, storeRepository);
            instance.marketFacade.initialize(instance);
        }
        return instance;
    }

    // Method to encrypt a given password
    private static String encrypt(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    // Method to check if a password matches its hashed version
    private static boolean checkPassword(String password, String hashedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(password, hashedPassword);
    }

    @Override
    public UserRepository getUserRepository() {
        return userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void deleteInstance() {
        instance = null;
        if (marketFacade != null) marketFacade.deleteInstance();
        this.marketFacade = null;
        if (userRepository != null) {
            this.userRepository.deleteInstance();
            userRepository = null;
        }
        this.paymentService = null;
        this.deliveryService = null;
    }

    @Override
    public HashMap<String, User> getUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public String getPendingUserNotifications(String admin, String username) {
        if (!userRepository.isExist(admin)) {
            throw new IllegalArgumentException("Admin user doesn't exist in the system");
        }
        if (!userRepository.isExist(username)) {
            throw new IllegalArgumentException("User doesn't exist in the system");
        }
        if (!isAdmin(admin)) {
            throw new IllegalArgumentException("Only admin user can get user notifications");
        }
        User usernameUser = userRepository.getUser(username);
        return usernameUser.getNotificationsJson();
    }

    @Override
    public void makeAdmin(String admin, String newAdmin) {
        if (!userRepository.isExist(admin)) {
            throw new IllegalArgumentException("Admin user doesn't exist in the system");
        }
        if (!userRepository.isExist(newAdmin)) {
            throw new IllegalArgumentException("User doesn't exist in the system");
        }
        if (!isAdmin(admin)) {
            throw new IllegalArgumentException("Only admin user can get user notifications");
        }
        User newAdminUser = userRepository.getUser(newAdmin);
        newAdminUser.setAdmin(true);
        userRepository.saveUser(newAdminUser);
    }

    @Override
    public void enter(int id) {
        userRepository.addVisitor("v" + id);
    }

    @Override
    public void exit(String username) throws Exception {
        if (userRepository.isExist(username)) {
            userRepository.deleteUser(username);
        } else {
            throw new Exception("No such user with username- " + username);
        }
    }

    @Override
    public void logout(int id, String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (username.charAt(0) != 'r') {
            throw new IllegalArgumentException("User performs not like a registered");
        }
        User user = userRepository.getUser(username);
        if (user == null) throw new IllegalArgumentException("No such user " + username);
        if (username.charAt(0) == 'r' && !user.getLogged())
            throw new IllegalArgumentException("User " + username + " already Logged out");
        if (!user.isSuspended()) {
            saveUserCart(username);
        }
        user.logout();
        userRepository.saveUser(user);
        enter(id);
    }

    @Override
    public void suspendUser(String admin, String toSuspend, LocalDateTime endSuspension) {
        if (!userRepository.isExist(admin)) {
            throw new IllegalArgumentException("Admin user doesn't exist in the system");
        }
        if (!userRepository.isExist(toSuspend)) {
            throw new IllegalArgumentException("User to suspend doesn't exist in the system");
        }
        if (!userRepository.getUser(admin).isAdmin()) {
            throw new IllegalArgumentException("Only admin user can suspend users");
        }
        if (endSuspension.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Date of suspension cannot be before now");
        }
        User toSuspendUser = userRepository.getUser(toSuspend);
        toSuspendUser.suspend(endSuspension);
        userRepository.saveUser(toSuspendUser);
    }

    @Override
    public void endSuspendUser(String admin, String toSuspend) {
        if (!userRepository.isExist(admin)) {
            throw new IllegalArgumentException("Admin user doesn't exist in the system");
        }
        if (!userRepository.isExist(toSuspend)) {
            throw new IllegalArgumentException("User to suspend doesn't exist in the system");
        }
        if (!userRepository.getUser(admin).isAdmin()) {
            throw new IllegalArgumentException("Only admin user can suspend users");
        }
        if (!userRepository.getUser(toSuspend).isSuspended()) {
            throw new IllegalArgumentException("User need to be suspend for ending suspend");
        }
        User toSuspendUser = userRepository.getUser(toSuspend);
        toSuspendUser.finishSuspension();
        userRepository.saveUser(toSuspendUser);
    }

    @Override
    public void setAddress(String username, String address) {
        if (!userRepository.isExist(username)) {
            throw new IllegalArgumentException("User doesn't exist in the system");
        }
        User user = userRepository.getUser(username);
        user.setAddress(address);
        userRepository.saveUser(user);
    }

    @Override
    public String watchSuspensions(String admin) {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> suspensionDetailsList = new ArrayList<>();

        if (!userRepository.isExist(admin)) {
            throw new IllegalArgumentException("Admin user doesn't exist in the system");
        }
        if (!userRepository.getUser(admin).isAdmin()) {
            throw new IllegalArgumentException("Only admin user can suspend users");
        }
        for (User user : userRepository.getAllUsersAsList()) {
            if (user.isSuspended()) {
                Map<String, Object> details = new HashMap<>();
                details.put("Username", user.getUsername());
                details.put("Start of suspension", user.getSuspendedStart().truncatedTo(ChronoUnit.SECONDS).toString());
                details.put("Time of suspension (in days)", Math.max(0, Math.abs(Duration.between(user.getSuspendedStart(), user.getSuspendedEnd()).toDays())));
                details.put("Time of suspension (in hours)", Math.max(0, Math.abs(Duration.between(user.getSuspendedStart(), user.getSuspendedEnd()).toHours())));
                details.put("End of suspension", user.getSuspendedEnd().toString());
                suspensionDetailsList.add(details);
            }
        }
        try {
            return mapper.writeValueAsString(suspensionDetailsList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert suspension details to JSON: " + e.getMessage());
        }
    }

    @Override
    public boolean isSuspended(String username) {
        if (!userRepository.isExist(username)) {
            throw new IllegalArgumentException("User doesn't exist in the system");
        }
        User user = userRepository.getUser(username);
        if (user.getSuspendedEnd() == null) {
            return false;
        }
        if (!user.getSuspendedEnd().isAfter(LocalDateTime.now())) {
            user.finishSuspension();
        }
        return user.isSuspended();
    }

    private void saveUserCart(String username) {
        User user = userRepository.getUser(username);
        if (user == null || user.getCart() == null) {
            throw new IllegalArgumentException("user doesn't exist in the system");
        }
        if (isSuspended(username)) {
            throw new RuntimeException("User is suspended from the system");
        }
        userRepository.getUser(username).getCart().saveCart();
    }

    @Override
    public void register(String username, String password, LocalDate birthdate) throws Exception {
        if (userRepository.isExist("r" + username)) throw new Exception("username already exists - " + username);
        registerChecks(username, password, birthdate);
        String encrypted_pass = encrypt(password);
        if (userRepository.checkIfRegistersEmpty()) {
            userRepository.addRegistered("r" + username, encrypted_pass, birthdate);
            User user = userRepository.getUser("r" + username);
            user.setAdmin(true);
            userRepository.saveUser(user);
        } else {
            userRepository.addRegistered("r" + username, encrypted_pass, birthdate);
        }
    }

    private void registerChecks(String username, String password, LocalDate birthdate) throws Exception {
        if (username == null) throw new Exception("Username is null");
        if (username.isEmpty()) throw new Exception("Username is empty");
        if (password == null) throw new Exception("Encrypted password is null");
        if (password.isEmpty()) throw new Exception("Encrypted password is empty");
        if (birthdate == null) throw new Exception("Birthdate password is null");
    }

    @Override
    public void login(String usernameV, String username, String password) {
        if (isSuspended(usernameV)) {
            throw new RuntimeException("User is suspended from the system");
        }
        User user = userRepository.getUser("r" + username);
        if (user == null) throw new RuntimeException("No such user " + username);
        if (username.charAt(0) == 'r' && user.getLogged())
            throw new RuntimeException("User " + username + " already logged in");
        if (checkPassword(password, user.getPass())) {
            userRepository.deleteUser(usernameV);
            user.login();
        } else {
            logger.error("Wrong password, Failed login user: {}", username);
            throw new RuntimeException("Wrong password");
        }
        userRepository.saveUser(user);
    }

    @Override
    public void sendPendingNotifications(String username) {
        if (!isUserExist(username)) throw new RuntimeException("No such user " + username);
        User user = userRepository.getUser(username);
        if (!user.getLogged()) throw new RuntimeException("User is not logged in");
        for (Notification notification : user.getNotifications()) {
            notificationSender.sendNotification(username, notification.toString());
        }
        user.clearPendingNotifications();
        userRepository.saveUser(user);
    }

    @Override
    public void sendNotification(String sender, String receiver, String content) {
        if (!isUserExist(sender)) throw new RuntimeException("Not a valid sender: " + sender);
        if (!isUserExist(receiver)) throw new RuntimeException("Not a valid receiver: " + receiver);
        if (content.isEmpty()) throw new RuntimeException("Empty notification content");
        Notification notification = new Notification(sender, receiver, content);
        User senderUser = userRepository.getUser(sender);
        if (!senderUser.getLogged()) throw new RuntimeException("Notification sender must be logged in");
        User receiverUser = userRepository.getUser(receiver);
        if (!receiverUser.getLogged()) receiverUser.receiveDelayedNotification(notification);
        else notificationSender.sendNotification(receiver, notification.toString());
        userRepository.saveUser(receiverUser);
    }

    @Override
    public void sendNotificationToStoreOwners(String sender, List<String> owners, String content) {
        if (!isUserExist(sender)) throw new RuntimeException("Not a valid sender: " + sender);
        User senderUser = userRepository.getUser(sender);
        if (!senderUser.getLogged()) throw new RuntimeException("Notification sender must be logged in");
        for (String owner : owners) {
            if (!isUserExist(owner)) throw new RuntimeException("Not a valid receiver: " + owner);
            Notification notification = new Notification(sender, owner, content);
            User receiverUser = userRepository.getUser(owner);
            if (!receiverUser.getLogged()) receiverUser.receiveDelayedNotification(notification);
            else notificationSender.sendNotification(owner, notification.toString());
            userRepository.saveUser(receiverUser);
        }
    }

    //TODO check if function is needed
    @Override
    public void saveUserCart(String username, int productId, String storeName, int quantity) {
        if (storeName == null || storeName.trim().isEmpty()) {
            logger.error("Store name is null");
            throw new RuntimeException("Store name is null");
        }
        if (marketFacade.isStoreExist(storeName)) {
            logger.error("Store with name {} already exists", storeName);
            throw new RuntimeException("Store with name " + storeName + " already exists");
        }
        if (userRepository.isExist(username)) {
            if (isSuspended(username)) {
                throw new RuntimeException("User is suspended from the system");
            }
            Product p = marketFacade.getStore(storeName).getProduct(productId);
            double price = p.getProduct_price();
            int category = p.getCategory().getIntValue();
            userRepository.getUser(username).getCart().addProductToCart(productId, quantity, storeName, price, category);
        }
        checkProductQuantity(username, productId, storeName, quantity);
    }

    @Override
    public synchronized String viewCart(String username) {
        if (username == null) {
            logger.error("View Cart - Username is null");
            throw new IllegalArgumentException("Username cannot be null");
        }
        if (username.isEmpty()) {
            logger.error("View Cart - Username is empty");
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (!userRepository.isExist(username)) {
            logger.error("View Cart - User not found");
            throw new RuntimeException("User not found");
        }
        if (isSuspended(username)) {
            throw new RuntimeException("User is suspended from the system");
        }
        User user = userRepository.getUser(username);
        if (username.charAt(0) == 'r' && !user.getLogged()) {
            logger.error("View Cart - Registered user is not logged");
            throw new RuntimeException("Registered user is not logged");
        }
        //TODO
        // Maybe before getting the information from the user cart,
        // we should check if the products information are up-to-date and correct
        return user.getShoppingCart_ToString();
    }

    @Override
    public synchronized void addToCart(String username, int productId, String storeName, int quantity) {
        if (username == null || username.trim().isEmpty()) {
            logger.error("Username cannot be null or empty");
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (!userRepository.isExist(username)) {
            logger.error("User not found: {}", username);
            throw new NoSuchElementException("User not found: " + username);
        }
        if (isSuspended(username)) {
            throw new RuntimeException("User is suspended from the system");
        }
        if (storeName == null || storeName.trim().isEmpty()) {
            logger.error("Store name cannot be null or empty");
            throw new IllegalArgumentException("Store name cannot be null or empty");
        }
        if (!marketFacade.isStoreExist(storeName)) {
            logger.error("Store not found: " + storeName);
            throw new NoSuchElementException("Store not found: " + storeName);
        }
        if (!marketFacade.getStore(storeName).isOpen())
            throw new IllegalArgumentException("When store is closed you cant add to cart from this store");
        if (username.charAt(0) == 'r' && !userRepository.getUser(username).getLogged()) {
            logger.error("User is not logged in: " + username);
            throw new RuntimeException("User is not logged in: " + username);
        }
        checkProductQuantity(username, productId, storeName, quantity);
        Product p = marketFacade.getStore(storeName).getProduct(productId);
        User user = userRepository.getUser(username);
        user.addProductToCart(productId, quantity, storeName, p.getProduct_price(), p.getCategory().getIntValue());
        userRepository.saveUser(user);
        //TODO save product in store?
    }

    private void checkProductQuantity(String username, int productId, String storeName, int quantity) {
        if (!userRepository.isExist(username)) {
            logger.error("User not found: " + username);
            throw new NoSuchElementException("User not found: " + username);
        }
        if (isSuspended(username)) {
            throw new RuntimeException("User is suspended from the system");
        }
        Store store = marketFacade.getStore(storeName);
        if (store == null) {
            logger.error("Store not found: " + storeName);
            throw new NoSuchElementException("Store not found: " + storeName);
        }
        if (!store.isOpen()) throw new IllegalArgumentException("When store is closed cant to check product quantity");
        Product product = store.getProducts().get(productId);
        if (product == null) {
            logger.error("Product not found: " + productId);
            throw new NoSuchElementException("Product not found: " + productId);
        }
        int quantityInStore = product.getProduct_quantity();
        int quantityInShoppingBag = userRepository.getUser(username).checkProductQuantity(productId, storeName);
        if (quantity + quantityInShoppingBag > quantityInStore) {
            logger.error("Insufficient product quantity in store for product ID: {}", productId);
            throw new RuntimeException("Product quantity is too low");
        }
    }

    @Override
    public synchronized void removeFromCart(String username, int productId, String storeName, int quantity) {
        if (username == null || username.trim().isEmpty()) {
            logger.error("Username cannot be null or empty");
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (!userRepository.isExist(username)) {
            logger.error("User not found: " + username);
            throw new NoSuchElementException("User not found: " + username);
        }
        if (isSuspended(username)) {
            throw new RuntimeException("User is suspended from the system");
        }
        if (storeName == null || storeName.trim().isEmpty()) {
            logger.error("Store name cannot be null or empty");
            throw new IllegalArgumentException("Store name cannot be null or empty");
        }
        if (!marketFacade.isStoreExist(storeName)) {
            logger.error("Store not found: " + storeName);
            throw new NoSuchElementException("Store not found: " + storeName);
        }
        if (username.charAt(0) == 'r' && !userRepository.getUser(username).getLogged()) {
            logger.error("User is not logged in: " + username);
            throw new RuntimeException("User is not logged in: " + username);
        }
        User user = userRepository.getUser(username);
        user.removeProductFromCart(productId, quantity, storeName);
        userRepository.saveUser(user);
    }

    @Override
    public void createStore(String username, String storeName, String description) {
        if (!userRepository.isExist(username)) {
            logger.error("While opening store - User not found");
            throw new IllegalArgumentException("User not found");
        }
        if (isSuspended(username)) {
            throw new RuntimeException("User is suspended from the system");
        }
        if (storeName == null || storeName.trim().isEmpty()) {
            logger.error("While opening store - Store name is null");
            throw new IllegalArgumentException("Store name should not be null");
        }
        if (marketFacade.isStoreExist(storeName)) {
            logger.error("While opening store - Store with name: {} already exists", storeName);
            throw new IllegalArgumentException("Store with name " + storeName + " already exists");
        }
        try {
            marketFacade.addStore(storeName, description, username, null);
            User user = userRepository.getUser(username);
            user.openStore(storeName);
            userRepository.saveUser(user);
        } catch (Exception e) {
            logger.error("Failed to open store: {}", e.getMessage());
            throw new IllegalArgumentException("Failed to open store", e);
        }
    }

    @Override
    public void suggestOwner(String appoint, String newOwner, String storeName) throws IllegalAccessException, NoSuchElementException {
        if (!marketFacade.isStoreExist(storeName))
            throw new NoSuchElementException("No store called " + storeName + " exist");
        if (!userRepository.isExist(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + " exist");
        }
        if (!userRepository.isExist(newOwner)) {
            throw new NoSuchElementException("No user called " + newOwner + " exist");
        }
        if (isSuspended(appoint)) {
            throw new RuntimeException("User is suspended from the system");
        }
        User appointUser = userRepository.getUser(appoint);
        User newOwnerUser = userRepository.getUser(newOwner);
        if (!appointUser.isOwner(storeName)) {
            throw new IllegalAccessException("Appoint user must be Owner");
        }
        if (!appointUser.getLogged()) {
            throw new IllegalAccessException("Appoint user is not logged");
        }
        if (newOwnerUser.isOwner(storeName)) {
            throw new IllegalAccessException("User already Owner of this store");
        }
        newOwnerUser.addWaitingAppoint_Owner(storeName, appoint);
        sendNotification(appoint, newOwner, appointUser.getUsername() + " suggests you to become a store owner at " + storeName);
        userRepository.saveUser(newOwnerUser);
    }

    @Override
    public void suggestManager(String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) throws IllegalAccessException, NoSuchElementException {
        if (!marketFacade.isStoreExist(store_name_id))
            throw new NoSuchElementException("No store called " + store_name_id + " exist");
        if (!userRepository.isExist(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + " exist");
        }
        if (isSuspended(appoint)) {
            throw new RuntimeException("User is suspended from the system");
        }
        if (!userRepository.isExist(newManager)) {
            throw new NoSuchElementException("No user called " + newManager + " exist");
        }
        User appointUser = userRepository.getUser(appoint);
        User newManagerUser = userRepository.getUser(newManager);
        if (!appointUser.isOwner(store_name_id)) {
            throw new IllegalAccessException("Appoint user must be Owner");
        }
        if (!appointUser.getLogged()) {
            throw new IllegalAccessException("Appoint user is not logged");
        }
        if (newManagerUser.isManager(store_name_id)) {
            throw new IllegalAccessException("User already Manager of this store");
        }
        if (newManagerUser.isOwner(store_name_id)) {
            throw new IllegalAccessException("User cannot be owner of this store");
        }
        newManagerUser.addWaitingAppoint_Manager(store_name_id, appoint, watch, editSupply, editBuyPolicy, editDiscountPolicy);
        sendNotification(appoint, newManager, appointUser.getUsername() + " suggests you to become a store manager at " + store_name_id);
        userRepository.saveUser(newManagerUser);
    }

    @Override
    public void approveOwner(String newOwner, String storeName, String appoint) throws IllegalAccessException {
        if (!marketFacade.isStoreExist(storeName))
            throw new NoSuchElementException("No store called " + storeName + " exist");
        if (!userRepository.isExist(newOwner)) {
            throw new NoSuchElementException("No user called " + newOwner + " exist");
        }
        if (!userRepository.isExist(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + " exist");
        }
        if (isSuspended(newOwner)) {
            throw new RuntimeException("User is suspended from the system");
        }
        User appointUser = userRepository.getUser(appoint);
        if (!appointUser.isOwner(storeName)) {
            throw new IllegalAccessException("User must be Owner");
        }
        User newOwnerUser = userRepository.getUser(newOwner);
        if (!newOwnerUser.getLogged()) {
            throw new IllegalAccessException("New owner user is not logged");
        }
        if (newOwnerUser.isOwner(storeName)) {
            throw new IllegalAccessException("User already Owner of this store");
        }
        if (!newOwnerUser.removeWaitingAppoint_Owner(storeName))
            throw new IllegalAccessException("No one suggest this user to be a owner");
        Store store = marketFacade.getStore(storeName);
        if (store.getManagers().contains(newOwner)) {
            newOwnerUser.removeManagerRole(storeName);
            store.removeManager(newOwner);
        }
        newOwnerUser.addOwnerRole(appoint, storeName);
        store.addOwner(newOwner);
        sendNotification(newOwner, appoint, newOwnerUser.getUsername() + " accepted your suggestion to become an owner at store: " + storeName);
        userRepository.saveUser(newOwnerUser);
        userRepository.saveUser(appointUser);
        //TODO save store
    }

    @Override
    public void approveManager(String newManager, String storeName, String appoint, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) throws IllegalAccessException {
        if (!marketFacade.isStoreExist(storeName))
            throw new NoSuchElementException("No store called " + storeName + " exist");
        if (!userRepository.isExist(newManager)) {
            throw new NoSuchElementException("No user called " + newManager + " exist");
        }
        if (!userRepository.isExist(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + " exist");
        }
        if (isSuspended(newManager)) {
            throw new RuntimeException("User is suspended from the system");
        }
        User appointUser = userRepository.getUser(appoint);
        User newManagerUser = userRepository.getUser(newManager);
        if (!newManagerUser.getLogged()) {
            throw new IllegalAccessException("New Manager user is not logged");
        }
        if (!appointUser.isOwner(storeName)) {
            throw new IllegalAccessException("User must be Owner");
        }
        if (newManagerUser.isManager(storeName)) {
            throw new IllegalAccessException("User already Manager of this store");
        }
        if (newManagerUser.isOwner(storeName)) {
            throw new IllegalAccessException("User cannot be owner of this store");
        }
        if (newManagerUser.removeWaitingAppoint_Manager(storeName, appoint) == null)
            throw new RuntimeException("No appointment requests in this store.");
        newManagerUser.addManagerRole(appoint, storeName);
        Store store = marketFacade.getStore(storeName);
        store.addManager(newManager);
        newManagerUser.setPermissionsToManager(storeName, watch, editSupply, editBuyPolicy, editDiscountPolicy);
        sendNotification(newManager, appoint, newManagerUser.getUsername() + " accepted your suggestion to become a manager at store: " + storeName);
        userRepository.saveUser(newManagerUser);
        userRepository.saveUser(appointUser);
        //TODO save store
    }

    @Override
    public void rejectToOwnStore(String userName, String storeName, String appoint) throws IllegalAccessException {
        if (!marketFacade.isStoreExist(storeName))
            throw new NoSuchElementException("No store called " + storeName + " exist");
        if (!userRepository.isExist(userName)) {
            throw new NoSuchElementException("No user called " + userName + " exist");
        }
        if (!userRepository.isExist(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + " exist");
        }
        if (isSuspended(userName)) {
            throw new RuntimeException("User is suspended from the system");
        }
        User appointUser = userRepository.getUser(appoint);
        if (!appointUser.isOwner(storeName)) {
            throw new IllegalAccessException("User must be Owner");
        }
        User newOwnerUser = userRepository.getUser(userName);
        if (!newOwnerUser.getLogged()) {
            throw new IllegalAccessException("New owner user is not logged");
        }
        if (newOwnerUser.isOwner(storeName)) {
            throw new IllegalAccessException("User already Owner of this store");
        }
        if (!newOwnerUser.removeWaitingAppoint_Owner(storeName))
            throw new IllegalAccessException("No one suggest this user to be a owner");
        sendNotification(userName, appoint, newOwnerUser.getUsername() + " rejected your suggestion to become an owner at store: " + storeName);
        userRepository.saveUser(newOwnerUser);
        userRepository.saveUser(appointUser);
    }

    @Override
    public void waiverOnOwnership(String userName, String storeName) throws IllegalAccessException {
        if (!marketFacade.isStoreExist(storeName))
            throw new NoSuchElementException("No store called " + storeName + " exist");
        if (!userRepository.isExist(userName)) {
            throw new NoSuchElementException("No user called " + userName + " exist");
        }
        if (isSuspended(userName)) {
            throw new RuntimeException("User is suspended from the system");
        }
        if (userName.charAt(0) != 'r') {
            throw new NoSuchElementException("No user called " + userName + "is registered");
        }
        User owner = userRepository.getUser(userName);
        Store store = marketFacade.getStore(storeName);
        if (!owner.isOwner(storeName)) {
            throw new IllegalAccessException("User is not owner of this store");
        }
        if (store.getFounder().equals(userName)) throw new IllegalAccessException("Founder cant waive on ownership");

        cancelOwnerShip(userName, storeName);
    }

    private void cancelOwnerShip(String userName, String storeName) {
        Store store = marketFacade.getStore(storeName);
        User ownerUser = userRepository.getUser(userName);
        List<String> storeOwners = store.getOwners();
        List<String> storeManagers = store.getManagers();


        for (String storeOwner : storeOwners) {
            User user = userRepository.getUser(storeOwner);
            if (user.getRoleByStoreId(storeName).getAppointedById().equals(userName)) {
                cancelOwnerShip(storeOwner, storeName);
                sendNotification(userName, "r" + user.getUsername(), "You are no longer an owner at store: " + storeName + " due to user: " + ownerUser.getUsername() + " is fired/waiving his ownership");
            }
            userRepository.saveUser(user);
        }

        for (String storeManager : storeManagers) {
            User user = userRepository.getUser(storeManager);
            if (user.getRoleByStoreId(storeName).getAppointedById().equals(userName)) {
                user.removeManagerRole(storeName);
                store.removeManager(storeManager);
                sendNotification(userName, "r" + user.getUsername(), "You are no longer a manager at store: " + storeName + " due to user: " + ownerUser.getUsername() + " is fired/waiving his ownership");
            }
            userRepository.saveUser(user);
        }
        userRepository.getUser(userName).removeOwnerRole(storeName);
        marketFacade.getStore(storeName).removeOwner(userName);
        userRepository.saveUser(ownerUser);
        //TODO save store
    }

    @Override
    public void fireManager(String owner, String storeName, String manager) throws IllegalAccessException {
        if (!marketFacade.isStoreExist(storeName))
            throw new NoSuchElementException("No store called " + storeName + " exist");
        if (!userRepository.isExist(owner)) {
            throw new NoSuchElementException("No user called " + owner + " exist");
        }
        if (isSuspended(owner)) {
            throw new RuntimeException("Owner is suspended from the system");
        }
        if (!userRepository.isExist(manager)) {
            throw new NoSuchElementException("No user called " + manager + " exist");
        }
        User ownerUser = userRepository.getUser(owner);
        User managerUser = userRepository.getUser(manager);
        if (!ownerUser.isOwner(storeName)) {
            throw new IllegalAccessException("User must be Owner");
        }
        if (!managerUser.isManager(storeName)) {
            throw new IllegalAccessException("Fired user must be Manager");
        }
        if (!ownerUser.getLogged()) {
            throw new IllegalAccessException("owner user is not logged");
        }
        if (!managerUser.getRoleByStoreId(storeName).getAppointedById().equals(owner)) {
            throw new IllegalAccessException("Owner cant fire manager that he/she didn't appointed");
        }
        managerUser.removeManagerRole(storeName);
        Store store = marketFacade.getStore(storeName);
        store.removeManager(manager);
        sendNotification(owner, manager, "You are no longer a manager at store: " + storeName + " due to being fired by " + ownerUser.getUsername());
        userRepository.saveUser(managerUser);
        //TODO call save store
    }

    @Override
    public void fireOwner(String ownerAppoint, String storeName, String owner) throws IllegalAccessException {
        if (!marketFacade.isStoreExist(storeName))
            throw new NoSuchElementException("No store called " + storeName + " exist");
        if (!userRepository.isExist(owner)) {
            throw new NoSuchElementException("No user called " + owner + " exist");
        }
        if (!userRepository.isExist(ownerAppoint)) {
            throw new NoSuchElementException("No user called " + ownerAppoint + " exist");
        }
        if (owner.charAt(0) != 'r') {
            throw new NoSuchElementException("No user called " + owner + " is registered");
        }
        if (ownerAppoint.charAt(0) != 'r') {
            throw new NoSuchElementException("No user called " + ownerAppoint + "is registered");
        }

        User ownerAppointer = userRepository.getUser(ownerAppoint);
        User ownerUser = userRepository.getUser(owner);
        if (!ownerAppointer.isOwner(storeName)) {
            throw new IllegalAccessException("The user that fire owner must be Owner");
        }
        if (!ownerUser.isOwner(storeName)) {
            throw new IllegalAccessException("The user that will be fired must be Owner");
        }
        if (!ownerUser.getRoleByStoreId(storeName).getAppointedById().equals(ownerAppoint)) {
            throw new IllegalAccessException("Owner cant fire owner that he/she didn't appointed");
        }

        cancelOwnerShip(owner, storeName);
        sendNotification(ownerAppoint, owner, "You are no longer an owner at store: " + storeName + " due to being fired by user: " + ownerAppointer.getUsername());
        userRepository.saveUser(ownerUser);
        //TODO save store
    }

    @Override
    public void rejectToManageStore(String userName, String storeName, String appoint) throws IllegalAccessException {
        if (!marketFacade.isStoreExist(storeName))
            throw new NoSuchElementException("No store called " + storeName + " exist");
        if (!userRepository.isExist(userName)) {
            throw new NoSuchElementException("No user called " + userName + " exist");
        }
        if (!userRepository.isExist(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + " exist");
        }
        if (isSuspended(userName)) {
            throw new RuntimeException("User is suspended from the system");
        }
        User appointUser = userRepository.getUser(appoint);
        User newManagerUser = userRepository.getUser(userName);
        if (!newManagerUser.getLogged()) {
            throw new IllegalAccessException("New Manager user is not logged");
        }
        if (!appointUser.isOwner(storeName)) {
            throw new IllegalAccessException("User must be Owner");
        }
        if (newManagerUser.isManager(storeName)) {
            throw new IllegalAccessException("User already Manager of this store");
        }
        if (newManagerUser.isOwner(storeName)) {
            throw new IllegalAccessException("User cannot be owner of this store");
        }
        List<Boolean> perm = newManagerUser.removeWaitingAppoint_Manager(storeName, appoint);
        sendNotification(userName, appoint, newManagerUser.getUsername() + " rejected your suggestion to become a manager at store: " + storeName);
        userRepository.saveUser(newManagerUser);
        userRepository.saveUser(appointUser);
    }

    @Override
    public void editPermissionForManager(String userId, String managerToEdit, String storeName, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) throws IllegalAccessException, NoSuchElementException {
        if (!marketFacade.isStoreExist(storeName))
            throw new NoSuchElementException("No store called " + storeName + " exist");
        if (!userRepository.isExist(userId)) {
            throw new NoSuchElementException("No user called " + userId + " exist");
        }
        if (!userRepository.isExist(managerToEdit)) {
            throw new NoSuchElementException("No user called " + managerToEdit + " exist");
        }
        if (isSuspended(userId)) {
            throw new RuntimeException("User is suspended from the system");
        }
        if (userId.charAt(0) != 'r') {
            throw new NoSuchElementException("No user called " + userId + " is registered");
        }
        if (managerToEdit.charAt(0) != 'r') {
            throw new NoSuchElementException("No user called " + managerToEdit + "is registered");
        }
        User appointUser = userRepository.getUser(userId);
        User managerUser = userRepository.getUser(managerToEdit);
        if (!appointUser.isOwner(storeName)) {
            throw new IllegalAccessException("User must be owner of this store");
        }
        if (!managerUser.isManager(storeName)) {
            throw new IllegalAccessException("User must be a  Manager of this store");
        }
        if (!managerUser.getRoleByStoreId(storeName).getAppointedById().equals(userId)) {
            throw new IllegalAccessException("Owner cant edit permissions to manager that he/she didn't appointed");
        }
        managerUser.setPermissionsToManager(storeName, watch, editSupply, editBuyPolicy, editDiscountPolicy);
        sendNotification(userId, managerToEdit, "Your permissions for store: " + storeName + " were changed by user: " + appointUser.getUsername());
        userRepository.saveUser(managerUser);
    }

    @Override
    public String getStoresIOwn(String username) {
        if (!userRepository.isExist(username)) {
            throw new NoSuchElementException("No user called " + username + " exist");
        }
        if (isSuspended(username)) {
            throw new RuntimeException("User is suspended from the system");
        }
        User user = userRepository.getUser(username);
        return user.getStoresIOwn();
    }

    @Override
    public String getStoresIManage(String username) {
        if (!userRepository.isExist(username)) {
            throw new NoSuchElementException("No user called " + username + " exist");
        }
        if (isSuspended(username)) {
            throw new RuntimeException("User is suspended from the system");
        }
        User user = userRepository.getUser(username);
        return user.getStoresIManage();
    }

    @Override
    public String getUserPassword(String username) {
        if (username.charAt(0) != 'r') {
            throw new NoSuchElementException("No user called " + username + " is registered");
        }
        User u = userRepository.getUser(username);
        if (u == null) throw new RuntimeException("No such registered user " + username);
        return u.getPass();
    }

    @Override
    public boolean isAdminRegistered() {
        return userRepository.isAdminRegistered();
    }

    @Override
    public boolean isAdmin(String username) {
        return userRepository.isAdmin(username);
    }

    public boolean isUserExist(String username) {
        return userRepository.isExist(username);
    }

    public User getUser(String username) {
        return userRepository.getUser(username);
    }

    @Override
    public String getPurchaseHistory(String username, String storeName) {
        if (!isUserExist(username) && username != null) {
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
        if (isSuspended(username)) {
            throw new RuntimeException("User is suspended from the system");
        }
        if (username != null && username.charAt(0) == 'r' && !getUser(username).getLogged()) {
            logger.error("User is not logged");
            throw new RuntimeException("User is not logged");
        }
        if (!isAdmin(username)) {
            logger.error("User is not commercial manager");
            throw new RuntimeException("User is not commercial manager");
        }
        if (!marketFacade.isStoreExist(storeName)) {
            logger.error("Store {} does not exist", storeName);
            throw new RuntimeException("Store does not exist");
        }
        return marketFacade.getStore(storeName).getPurchaseHistoryString(username);
    }

    private boolean checkAvailabilityAndConditions(String username) {
        if (!isUserExist(username)) {
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
        if (isSuspended(username)) {
            throw new RuntimeException("User is suspended from the system");
        }
        getUser(username).checkAvailabilityAndConditions(marketFacade.getStoreRepository());
        return true;
    }

    @Override
    public void purchaseCart(String username) throws Exception {
        User user = userRepository.getUser(username);

        if (!checkAvailabilityAndConditions(username)) {
            user.setTimerCancelled(true);
            logger.error("Products are not available or do not meet purchase conditions.");
            throw new RuntimeException("Products are not available or do not meet purchase conditions.");
        }
        if (!marketFacade.validatePurchasePolicies(user.getCart().toJson(), user.getAge())) {
            user.setTimerCancelled(true);

            logger.error("Products do not meet purchase policies conditions.");
            throw new RuntimeException("Products do not meet purchase policies conditions.");
        }
        user.removeReservedProducts(marketFacade.getStoreRepository());
        Timer timer = new Timer();
        user.setTimerCancelled(false);
        timer.schedule(new TimerTask() {
            public void run() {
                getUser(username).releaseReservedProducts(marketFacade.getStoreRepository());
                user.setTimerCancelled(true);
                logger.error("Purchase timeout for user: " + username);
            }
        }, 10 * 60 * 1000);
        double totalPrice = marketFacade.calculateTotalPrice(user.getCart().toJson());
        int deliveryId;
        String address = user.getAddress();
        try {
            deliveryId = deliveryService.makeDelivery(address);
        } catch (Exception e) {
            user.releaseReservedProducts(marketFacade.getStoreRepository());
            timer.cancel();
            user.setTimerCancelled(true);
            throw new Exception("Error in Delivery");
        }
        if (deliveryId < 0) {
            getUser(username).releaseReservedProducts(marketFacade.getStoreRepository());
            timer.cancel();
            user.setTimerCancelled(true);
            throw new Exception("Error in Delivery");
        }
        int paymentId;
        try {
            paymentId = paymentService.makePayment(totalPrice);
        } catch (Exception e) {
            deliveryService.cancelDelivery(deliveryId);
            user.releaseReservedProducts(marketFacade.getStoreRepository());
            timer.cancel();
            user.setTimerCancelled(true);
            throw new Exception("Error in Payment");
        }
        if (paymentId < 0) {
            deliveryService.cancelDelivery(deliveryId);
            user.releaseReservedProducts(marketFacade.getStoreRepository());
            timer.cancel();
            user.setTimerCancelled(true);
            throw new Exception("Error in Payment");
        }
        user.addPurchase(marketFacade.getStoreRepository(), username);
        timer.cancel();
        user.setTimerCancelled(true);
        timer.purge();
        userRepository.saveUser(user);
        //TODO save store for each store
    }


    @Override
    public String calculatePrice(String username) throws Exception {
        double result = marketFacade.calculateTotalPrice(getUser(username).getCart().toJson());
        return Double.toString(Math.round(result * 100.0) / 100.0);
    }

    @Override
    public void sendMessageUserToUser(String sender, String receiver, String content) {
        if (!userRepository.isExist(sender)) throw new RuntimeException("Message sender user must exist");
        if (!userRepository.isExist(receiver)) throw new RuntimeException("Message receiver user must exist");
        if (content.isEmpty()) throw new RuntimeException("Message content cannot be empty");
        if (receiver.charAt(0) == 'v') throw new RuntimeException("Visitors cannot receive messages from users");
        User receiverUser = userRepository.getUser(receiver);
        User senderUser = userRepository.getUser(sender);
        receiverUser.receiveMessage(sender, sender.substring(1), content);
        sendNotification(sender, receiver, "You have received a message from user: " + senderUser.getUsername());
        userRepository.saveUser(receiverUser);
    }

    @Override
    public boolean getIsWatchPermission(String username, String storeName) throws IllegalAccessException {
        if (!marketFacade.isStoreExist(storeName))
            throw new NoSuchElementException("No store called " + storeName + " exist");
        if (!userRepository.isExist(username))
            throw new NoSuchElementException("No user called " + username + " exist");
        if (isSuspended(username)) throw new RuntimeException("User is suspended from the system");
        User user = userRepository.getUser(username);
        if (!user.isManager(storeName)) throw new IllegalAccessException("User must be Manager");
        return user.getRoleByStoreId(storeName).getRoleState().isWatch();
    }

    @Override
    public boolean getIsEditSupplyPermission(String username, String storeName) throws IllegalAccessException {
        if (!marketFacade.isStoreExist(storeName))
            throw new NoSuchElementException("No store called " + storeName + " exist");
        if (!userRepository.isExist(username))
            throw new NoSuchElementException("No user called " + username + " exist");
        if (isSuspended(username)) throw new RuntimeException("User is suspended from the system");
        User user = userRepository.getUser(username);
        if (!user.isManager(storeName)) throw new IllegalAccessException("User must be Manager");
        return user.getRoleByStoreId(storeName).getRoleState().isEditSupply();
    }

    @Override
    public boolean getIsEditDiscountPolicyPermission(String username, String storeName) throws IllegalAccessException {
        if (!marketFacade.isStoreExist(storeName))
            throw new NoSuchElementException("No store called " + storeName + " exist");
        if (!userRepository.isExist(username))
            throw new NoSuchElementException("No user called " + username + " exist");
        if (isSuspended(username)) throw new RuntimeException("User is suspended from the system");
        User user = userRepository.getUser(username);
        if (!user.isManager(storeName)) throw new IllegalAccessException("User must be Manager");
        return user.getRoleByStoreId(storeName).getRoleState().isEditDiscountPolicy();
    }

    @Override
    public boolean getIsEditPurchasePolicyPermission(String username, String storeName) throws IllegalAccessException {
        if (!marketFacade.isStoreExist(storeName))
            throw new NoSuchElementException("No store called " + storeName + " exist");
        if (!userRepository.isExist(username))
            throw new NoSuchElementException("No user called " + username + " exist");
        if (isSuspended(username)) throw new RuntimeException("User is suspended from the system");
        User user = userRepository.getUser(username);
        if (!user.isManager(storeName)) throw new IllegalAccessException("User must be Manager");
        return user.getRoleByStoreId(storeName).getRoleState().isEditPurchasePolicy();
    }

    @Override
    public String getUserMessagesJson(String admin, String username) {
        if (!userRepository.isExist(admin)) {
            throw new IllegalArgumentException("Admin user doesn't exist in the system");
        }
        if (!userRepository.isExist(username)) {
            throw new IllegalArgumentException("User doesn't exist in the system");
        }
        if (!isAdmin(admin)) {
            throw new IllegalArgumentException("Only admin user can get user notifications");
        }
        User usernameUser = userRepository.getUser(username);
        return usernameUser.getMessagesJSON();
    }

    @Override
    public String getUserRequestsOwnership(String username) {
        if (!userRepository.isExist(username)) {
            throw new IllegalArgumentException("User doesn't exist in the system");
        }
        if (isSuspended(username))
            throw new RuntimeException("User is suspended from the system");
        User user = userRepository.getUser(username);
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> toApproves = new ArrayList<>();
        for (String storeName : user.getOwnerSuggestions().keySet()) {
            Map<String, Object> approveMap = new HashMap<>();
            approveMap.put("storeName", storeName);
            approveMap.put("appointee", user.getOwnerSuggestions().get(storeName).substring(1));
            toApproves.add(approveMap);
        }
        try {
            return mapper.writeValueAsString(toApproves);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert suspension details to JSON: " + e.getMessage());
        }
    }

    @Override
    public String getUserRequestsManagement(String username) {
        if (!userRepository.isExist(username)) {
            throw new IllegalArgumentException("User doesn't exist in the system");
        }
        if (isSuspended(username)) {
            throw new RuntimeException("User is suspended from the system");
        }
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> toApproves = getMapsForManagement(username);
        try {
            return mapper.writeValueAsString(toApproves);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert suspension details to JSON: " + e.getMessage());
        }
    }

    private List<Map<String, Object>> getMapsForManagement(String username) {
        User user = userRepository.getUser(username);
        List<Map<String, Object>> toApproves = new ArrayList<>();
        for (Map.Entry<String, HashMap<String, List<Boolean>>> entry : user.getManagerSuggestions().entrySet()) {
            String storeName = entry.getKey();
            HashMap<String, List<Boolean>> appointeesPermissions = entry.getValue();
            for (Map.Entry<String, List<Boolean>> appointeeEntry : appointeesPermissions.entrySet()) {
                Map<String, Object> approveMap = getStringObjectMap(appointeeEntry, storeName);
                toApproves.add(approveMap);
            }
        }
        return toApproves;
    }

    private Map<String, Object> getStringObjectMap(Map.Entry<String, List<Boolean>> appointeeEntry, String storeName) {
        String appointee = appointeeEntry.getKey();
        List<Boolean> permissions = appointeeEntry.getValue();
        Map<String, Object> approveMap = new HashMap<>();
        approveMap.put("storeName", storeName);
        approveMap.put("appointee", appointee.substring(1));
        approveMap.put("watch", permissions.get(0));
        approveMap.put("editSupply", permissions.get(1));
        approveMap.put("editBuyPolicy", permissions.get(2));
        approveMap.put("editDiscountPolicy", permissions.get(3));
        return approveMap;
    }

    @Override
    public String getPermissionsForUserJSONFormat(String username, String storeName) {
        if (!userRepository.isExist(username)) {
            throw new IllegalArgumentException("User doesn't exist in the system");
        }
        if (!marketFacade.isStoreExist(storeName)) {
            throw new IllegalArgumentException("Store doesn't exist in the system");
        }
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> permissions = new HashMap<>();
        User user = userRepository.getUser(username);
        permissions.put("watch", user.isWatch(storeName));
        permissions.put("editSupply", user.isEditSupply(storeName));
        permissions.put("editBuyPolicy", user.isEditPurchasePolicy(storeName));
        permissions.put("editDiscountPolicy", user.isEditDiscountPolicy(storeName));
        try {
            return mapper.writeValueAsString(permissions);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert suspension details to JSON: " + e.getMessage());
        }
    }
}
