package com.example.trading_system.domain.users;

import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.DeliveryServiceProxy;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.externalservices.PaymentServiceProxy;
import com.example.trading_system.domain.stores.*;
import com.example.trading_system.service.UserServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class UserFacadeImp implements UserFacade {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);
    private static UserFacadeImp instance = null;
    private MarketFacade marketFacade;
    private UserRepository userMemoryRepository;
    private StoreSalesHistory storeSalesHistory;
    private DeliveryService deliveryService;
    private PaymentService paymentService;


    private UserFacadeImp(PaymentService paymentService, DeliveryService deliveryService) {
        this.marketFacade = MarketFacadeImp.getInstance();
        this.userMemoryRepository = UserMemoryRepository.getInstance();
        this.storeSalesHistory = StoreSalesHistory.getInstance();
        this.paymentService = paymentService;
        this.deliveryService = deliveryService;

        marketFacade.initialize(this);
    }

    public static UserFacadeImp getInstance() {
        if (instance == null) {
            instance = new UserFacadeImp(new PaymentServiceProxy(), new DeliveryServiceProxy());
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
    public void deleteInstance() {
        instance = null;
        if (marketFacade != null)
            marketFacade.deleteInstance();
        this.marketFacade = null;
        this.userMemoryRepository.deleteInstance();
    }

    @Override
    public HashMap<String, User> getUsers() {
        return userMemoryRepository.getAllUsers();
    }

    @Override
    public void enter(int id) {
        userMemoryRepository.addVisitor("v" + id);
    }

    @Override
    public void exit(String username) throws Exception {
        if (userMemoryRepository.isExist(username)) {
            userMemoryRepository.deleteUser(username);
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
        User u = userMemoryRepository.getUser(username);
        if (u == null)
            throw new IllegalArgumentException("No such user " + username);
        if (username.charAt(0) == 'r' && !u.getLogged())
            throw new IllegalArgumentException("User " + username + " already Logged out");
        if (!u.isSuspended()) {
            saveUserCart(username);
        }
        u.logout();
        enter(id);
    }

    @Override
    public void suspendUser(String admin, String toSuspend, LocalDateTime endSuspention) {
        if (!userMemoryRepository.isExist(admin)) {
            throw new IllegalArgumentException("Admin user doesn't exist in the system");
        }
        if (!userMemoryRepository.isExist(toSuspend)) {
            throw new IllegalArgumentException("User to suspend doesn't exist in the system");
        }
        if (!userMemoryRepository.getUser(admin).isAdmin()) {
            throw new IllegalArgumentException("Only admin user can suspend users");
        }
        if (endSuspention.compareTo(LocalDateTime.now()) < 0) {
            throw new IllegalArgumentException("Date of suspension cannot be before now");
        }
        User toSuspendUser = userMemoryRepository.getUser(toSuspend);
        toSuspendUser.suspend(endSuspention);
    }

    @Override
    public void endSuspendUser(String admin, String toSuspend) {
        if (!userMemoryRepository.isExist(admin)) {
            throw new IllegalArgumentException("Admin user doesn't exist in the system");
        }
        if (!userMemoryRepository.isExist(toSuspend)) {
            throw new IllegalArgumentException("User to suspend doesn't exist in the system");
        }
        if (!userMemoryRepository.getUser(admin).isAdmin()) {
            throw new IllegalArgumentException("Only admin user can suspend users");
        }
        if (!userMemoryRepository.getUser(toSuspend).isSuspended()) {
            throw new IllegalArgumentException("User need to be suspend for ending suspend");
        }
        User toSuspendUser = userMemoryRepository.getUser(toSuspend);
        toSuspendUser.finishSuspension();
    }

    @Override
    public String watchSuspensions(String admin) {
        StringBuilder details = new StringBuilder();
        if (!userMemoryRepository.isExist(admin)) {
            throw new IllegalArgumentException("Admin user doesn't exist in the system");
        }
        if (!userMemoryRepository.getUser(admin).isAdmin()) {
            throw new IllegalArgumentException("Only admin user can suspend users");
        }
        for (User user : userMemoryRepository.getAllUsersAsList()) {
            if (user.isSuspended()) {
                details.append("Username - ").append(user.getUsername()).append("\n");
                details.append("Start of suspension - ").append(user.getSuspendedStart().truncatedTo(ChronoUnit.SECONDS)).append("\n");
                details.append("Time of suspension (in days) - ").append(Math.max(0, Math.abs(Duration.between(user.getSuspendedStart(), user.getSuspendedEnd()).toDays()))).append("\n");
                details.append("Time of suspension (in hours) - ").append(Math.max(0, Math.abs(Duration.between(user.getSuspendedStart(), user.getSuspendedEnd()).toHours()))).append("\n");
                details.append("End of suspension - ").append(user.getSuspendedEnd().toString());
            }
        }
        return details.toString();
    }

    @Override
    public boolean isSuspended(String username) {
        if (!userMemoryRepository.isExist(username)) {
            throw new IllegalArgumentException("User doesn't exist in the system");
        }
        User user = userMemoryRepository.getUser(username);
        if (user.getSuspendedEnd() == null) {
            return false;
        }
        if (user.getSuspendedEnd().compareTo(LocalDateTime.now()) <= 0) {
            user.finishSuspension();
        }
        return user.isSuspended();
    }

    private void saveUserCart(String username) {
        User user = userMemoryRepository.getUser(username);
        if (user == null || user.getCart() == null) {
            throw new IllegalArgumentException("user doesn't exist in the system");
        }
        if (isSuspended(username)) {
            throw new RuntimeException("User is suspended from the system");
        }
        userMemoryRepository.getUser(username).getCart().saveCart();
    }

    @Override
    public void register(String username, String password, LocalDate birthdate) throws Exception {
        if (userMemoryRepository.isExist("r" + username))
            throw new Exception("username already exists - " + username);
        registerChecks(username, password, birthdate);
        String encrypted_pass = encrypt(password);
        if (checkIfRegistersEmpty()) {
            userMemoryRepository.addRegistered("r" + username, encrypted_pass, birthdate);
            userMemoryRepository.getUser("r" + username).setAdmin(true);
        } else {
            userMemoryRepository.addRegistered("r" + username, encrypted_pass, birthdate);
        }

    }

    private boolean checkIfRegistersEmpty() {
        if (userMemoryRepository.isEmpty()) {
            return true;
        }
        for (String username : userMemoryRepository.getAllUsersAsUsernames()) {
            if (username.charAt(0) == 'r') {
                return false;
            }
        }
        return true;
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
        User u = userMemoryRepository.getUser("r" + username);
        if (u == null)
            throw new RuntimeException("No such user " + username);
        if (username.charAt(0) == 'r' && u.getLogged())
            throw new RuntimeException("User " + username + " already logged in");
        if (checkPassword(password, u.getPass())) {
            userMemoryRepository.deleteUser(usernameV);
            u.login();
        } else {
            logger.error("Wrong password, Failed login user: {}", username);
            throw new RuntimeException("Wrong password");
        }
    }

    @Override
    public boolean sendNotification(User sender, User receiver, String content) {
        String notification = sender.sendNotification(receiver.getUsername(), content);
        receiver.receiveNotification(notification);
        return receiver.getLogged();
        //TODO return something to show the notification if receiver is logged in - maybe boolean if logged in
    }

    @Override
    public void saveUserCart(String username, int productId, String storeName, int quantity) {
        if (storeName == null || storeName.trim().isEmpty()) {
            logger.error("Store name is null");
            throw new RuntimeException("Store name is null");
        }
        if (marketFacade.isStoreExist(storeName)) {
            logger.error("Store with name " + storeName + " already exists");  // todo : why its illegal?
            throw new RuntimeException("Store with name " + storeName + " already exists");
        }
        if (userMemoryRepository.isExist(username)) {
            if (isSuspended(username)) {
                throw new RuntimeException("User is suspended from the system");
            }
            Product p = marketFacade.getStore(storeName).getProduct(productId);
            double price = p.getProduct_price();
            int category = p.getCategory().getIntValue();
            userMemoryRepository.getUser(username).getCart().addProductToCart(productId, quantity, storeName, price, category);
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
        if (!userMemoryRepository.isExist(username)) {
            logger.error("View Cart - User not found");
            throw new RuntimeException("User not found");
        }
        if (isSuspended(username)) {
            throw new RuntimeException("User is suspended from the system");
        }
        User user = userMemoryRepository.getUser(username);
        if (username.charAt(0) == 'r' && !user.getLogged()) {
            logger.error("View Cart - Registered user is not logged");
            throw new RuntimeException("Registered user is not logged");
        }
        //Maybe before getting the information from the user cart,
        // we should check if the products information are up-to-date and correct
        return user.getShoppingCart_ToString();
    }

    @Override
    public synchronized void addToCart(String username, int productId, String storeName, int quantity) {
        if (username == null || username.trim().isEmpty()) {
            logger.error("Username cannot be null or empty");
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (!userMemoryRepository.isExist(username)) {
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
        if (!marketFacade.getStore(storeName).isOpen())
            throw new IllegalArgumentException("When store is closed you cant add to cart from this store");
        if (username.charAt(0) == 'r' && !userMemoryRepository.getUser(username).getLogged()) {
            logger.error("User is not logged in: " + username);
            throw new RuntimeException("User is not logged in: " + username);
        }
        checkProductQuantity(username, productId, storeName, quantity);
        Product p = marketFacade.getStore(storeName).getProduct(productId);
        userMemoryRepository.getUser(username).addProductToCart(productId, quantity, storeName, p.getProduct_price(), p.getCategory().getIntValue());
    }

    private void checkProductQuantity(String username, int productId, String storeName, int quantity) {
        if (!userMemoryRepository.isExist(username)) {
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
        if (!store.isOpen())
            throw new IllegalArgumentException("When store is closed cant to check product quantity");
        Product product = store.getProducts().get(productId);
        if (product == null) {
            logger.error("Product not found: " + productId);
            throw new NoSuchElementException("Product not found: " + productId);
        }
        int quantityInStore = product.getProduct_quantity();
        int quantityInShoppingBag = userMemoryRepository.getUser(username).checkProductQuantity(productId, storeName);

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
        if (!userMemoryRepository.isExist(username)) {
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
        if (username.charAt(0) == 'r' && !userMemoryRepository.getUser(username).getLogged()) {
            logger.error("User is not logged in: " + username);
            throw new RuntimeException("User is not logged in: " + username);
        }
        userMemoryRepository.getUser(username).removeProductFromCart(productId, quantity, storeName);
    }

    @Override
    public void createStore(String username, String storeName, String description) {
        if (!userMemoryRepository.isExist(username)) {
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
            userMemoryRepository.getUser(username).openStore(storeName);
        } catch (Exception e) {
            logger.error("Failed to open store: {}", e.getMessage());
            throw new IllegalArgumentException("Failed to open store", e);
        }
    }

    @Override
    public void suggestOwner(String appoint, String newOwner, String storeName) throws IllegalAccessException, NoSuchElementException {
        if (!marketFacade.isStoreExist(storeName))
            throw new NoSuchElementException("No store called " + storeName + " exist");
        if (!userMemoryRepository.isExist(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + " exist");
        }
        if (!userMemoryRepository.isExist(newOwner)) {
            throw new NoSuchElementException("No user called " + newOwner + " exist");
        }
        if (isSuspended(appoint)) {
            throw new RuntimeException("User is suspended from the system");
        }
        if (appoint.charAt(0) != 'r') {
            throw new NoSuchElementException("No user called " + appoint + " is registered");
        }
        if (newOwner.charAt(0) != 'r') {
            throw new NoSuchElementException("No user called " + newOwner + "is registered");
        }
        User appointUser = userMemoryRepository.getUser(appoint);
        User newOwnerUser = userMemoryRepository.getUser(newOwner);
        if (!appointUser.isOwner(storeName)) {
            throw new IllegalAccessException("Appoint user must be Owner");
        }
        if (!appointUser.getLogged()) {
            throw new IllegalAccessException("Appoint user is not logged");
        }
        if (newOwnerUser.isOwner(storeName)) {
            throw new IllegalAccessException("User already Owner of this store");
        }
        newOwnerUser.addWaitingAppoint_Owner(storeName);
    }

    @Override
    public void suggestManage(String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) throws IllegalAccessException, NoSuchElementException {
        if (!marketFacade.isStoreExist(store_name_id))
            throw new NoSuchElementException("No store called " + store_name_id + " exist");
        if (!userMemoryRepository.isExist(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + " exist");
        }
        if (isSuspended(appoint)) {
            throw new RuntimeException("User is suspended from the system");
        }
        if (!userMemoryRepository.isExist(newManager)) {
            throw new NoSuchElementException("No user called " + newManager + " exist");
        }
        if (appoint.charAt(0) != 'r') {
            throw new NoSuchElementException("No user called " + appoint + " is registered");
        }
        if (newManager.charAt(0) != 'r') {
            throw new NoSuchElementException("No user called " + newManager + "is registered");
        }
        User appointUser = userMemoryRepository.getUser(appoint);
        User newManagerUser = userMemoryRepository.getUser(newManager);
        if (!appointUser.isOwner(store_name_id)) {
            throw new IllegalAccessException("User must be Owner");
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
        newManagerUser.addWaitingAppoint_Manager(store_name_id, watch, editSupply, editBuyPolicy, editDiscountPolicy);
    }

    @Override
    public void approveManage(String newManager, String store_name_id, String appoint) throws IllegalAccessException {
        if (!marketFacade.isStoreExist(store_name_id))
            throw new NoSuchElementException("No store called " + store_name_id + " exist");
        if (!userMemoryRepository.isExist(newManager)) {
            throw new NoSuchElementException("No user called " + newManager + " exist");
        }
        if (!userMemoryRepository.isExist(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + " exist");
        }
        if (isSuspended(appoint)) {
            throw new RuntimeException("User is suspended from the system");
        }
        if (appoint.charAt(0) != 'r') {
            throw new NoSuchElementException("No user called " + appoint + " is registered");
        }
        if (newManager.charAt(0) != 'r') {
            throw new NoSuchElementException("No user called " + newManager + "is registered");
        }
        User appointUser = userMemoryRepository.getUser(appoint);
        User newManagerUser = userMemoryRepository.getUser(newManager);
        if (!appointUser.isOwner(store_name_id)) {
            throw new IllegalAccessException("User must be Owner");
        }
        if (newManagerUser.isManager(store_name_id)) {
            throw new IllegalAccessException("User already Manager of this store");
        }
        if (newManagerUser.isOwner(store_name_id)) {
            throw new IllegalAccessException("User cannot be owner of this store");
        }
        List<Boolean> permissions = newManagerUser.removeWaitingAppoint_Manager(store_name_id);
        newManagerUser.addManagerRole(appoint, store_name_id);
        newManagerUser.setPermissionsToManager(store_name_id, permissions.get(0), permissions.get(1), permissions.get(2), permissions.get(3));
        marketFacade.getStore(store_name_id).addManager(newManager);
    }

    @Override
    public void rejectToManageStore(String userName, String storeName, String appoint) throws IllegalAccessException {
        if (!marketFacade.isStoreExist(storeName))
            throw new NoSuchElementException("No store called " + storeName + " exist");
        if (!userMemoryRepository.isExist(userName)) {
            throw new NoSuchElementException("No user called " + userName + " exist");
        }
        if (!userMemoryRepository.isExist(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + " exist");
        }
        if (appoint.charAt(0) != 'r') {
            throw new NoSuchElementException("No user called " + appoint + " is registered");
        }
        if (userName.charAt(0) != 'r') {
            throw new NoSuchElementException("No user called " + userName + "is registered");
        }
        if (isSuspended(appoint)) {
            throw new RuntimeException("User is suspended from the system");
        }
        User appointUser = userMemoryRepository.getUser(appoint);
        User newManagerUser = userMemoryRepository.getUser(userName);
        if (!appointUser.isOwner(storeName)) {
            throw new IllegalAccessException("User must be Owner");
        }
        if (newManagerUser.isManager(storeName)) {
            throw new IllegalAccessException("User already Manager of this store");
        }
        if (newManagerUser.isOwner(storeName)) {
            throw new IllegalAccessException("User cannot be owner of this store");
        }
        newManagerUser.removeWaitingAppoint_Manager(storeName);
    }

    @Override
    public void approveOwner(String newOwner, String storeName, String appoint) throws IllegalAccessException {
        if (!marketFacade.isStoreExist(storeName))
            throw new NoSuchElementException("No store called " + storeName + " exist");
        if (!userMemoryRepository.isExist(newOwner)) {
            throw new NoSuchElementException("No user called " + newOwner + " exist");
        }
        if (!userMemoryRepository.isExist(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + " exist");
        }
        if (isSuspended(appoint)) {
            throw new RuntimeException("User is suspended from the system");
        }
        if (appoint.charAt(0) != 'r') {
            throw new NoSuchElementException("No user called " + appoint + " is registered");
        }
        if (newOwner.charAt(0) != 'r') {
            throw new NoSuchElementException("No user called " + newOwner + "is registered");
        }
        User appointUser = userMemoryRepository.getUser(appoint);
        if (!appointUser.isOwner(storeName)) {
            throw new IllegalAccessException("User must be Owner");
        }
        User newOwnerUser = userMemoryRepository.getUser(newOwner);
        if (newOwnerUser.isOwner(storeName)) {
            throw new IllegalAccessException("User already Owner of this store");
        }
        newOwnerUser.removeWaitingAppoint_Owner(storeName);
        newOwnerUser.addOwnerRole(appoint, storeName);
        marketFacade.getStore(storeName).addOwner(newOwner);
    }

    @Override
    public void rejectToOwnStore(String userName, String storeName, String appoint) throws IllegalAccessException {
        if (!marketFacade.isStoreExist(storeName))
            throw new NoSuchElementException("No store called " + storeName + " exist");
        if (!userMemoryRepository.isExist(userName)) {
            throw new NoSuchElementException("No user called " + userName + " exist");
        }
        if (!userMemoryRepository.isExist(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + " exist");
        }
        if (appoint.charAt(0) != 'r') {
            throw new NoSuchElementException("No user called " + appoint + " is registered");
        }
        if (userName.charAt(0) != 'r') {
            throw new NoSuchElementException("No user called " + userName + "is registered");
        }
        if (isSuspended(appoint)) {
            throw new RuntimeException("User is suspended from the system");
        }
        User appointUser = userMemoryRepository.getUser(appoint);
        if (!appointUser.isOwner(storeName)) {
            throw new IllegalAccessException("User must be Owner");
        }
        User newOwnerUser = userMemoryRepository.getUser(userName);
        if (newOwnerUser.isOwner(storeName)) {
            throw new IllegalAccessException("User already Owner of this store");
        }
        newOwnerUser.removeWaitingAppoint_Owner(storeName);
    }

    @Override
    public void waiverOnOwnership(String userName, String storeName) throws IllegalAccessException {
        if (!marketFacade.isStoreExist(storeName))
            throw new NoSuchElementException("No store called " + storeName + " exist");
        if (!userMemoryRepository.isExist(userName)) {
            throw new NoSuchElementException("No user called " + userName + " exist");
        }
        if (isSuspended(userName)) {
            throw new RuntimeException("User is suspended from the system");
        }
        if (userName.charAt(0) != 'r') {
            throw new NoSuchElementException("No user called " + userName + "is registered");
        }
        User owner = userMemoryRepository.getUser(userName);
        if (!owner.isOwner(storeName)) {
            throw new IllegalAccessException("User is not owner of this store");
        }
        owner.removeOwnerRole(storeName);
        marketFacade.getStore(storeName).removeOwner(userName);

    }

    @Override
    public void fireManager(String owner, String storeName, String manager) throws IllegalAccessException {
        if (!marketFacade.isStoreExist(storeName))
            throw new NoSuchElementException("No store called " + storeName + " exist");
        if (!userMemoryRepository.isExist(owner)) {
            throw new NoSuchElementException("No user called " + owner + " exist");
        }
        if (isSuspended(owner)) {
            throw new RuntimeException("Owner is suspended from the system");
        }
        if (!userMemoryRepository.isExist(manager)) {
            throw new NoSuchElementException("No user called " + manager + " exist");
        }
        if (owner.charAt(0) != 'r') {
            throw new NoSuchElementException("No user called " + owner + " is registered");
        }
        if (manager.charAt(0) != 'r') {
            throw new NoSuchElementException("No user called " + manager + "is registered");
        }
        User ownerUser = userMemoryRepository.getUser(owner);
        User managerUser = userMemoryRepository.getUser(manager);
        if (!ownerUser.isOwner(storeName)) {
            throw new IllegalAccessException("User must be Owner");
        }
        if (!managerUser.isManager(storeName)) {
            throw new IllegalAccessException("Fired user must be Manager");
        }
        if (!ownerUser.getLogged()) {
            throw new IllegalAccessException("owner user is not logged");
        }
        if (managerUser.getRoleByStoreId(storeName).getAppointedById().equals(owner)) {
            throw new IllegalAccessException("Owner cant fire manager that he/she didn't appointed");
        }
        ownerUser.removeManagerRole(storeName);
        marketFacade.getStore(storeName).removeManager(manager);
    }


    public void appointManager(String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) throws IllegalAccessException, NoSuchElementException {
        if (!marketFacade.isStoreExist(store_name_id))
            throw new NoSuchElementException("No store called " + store_name_id + " exist");
        if (!userMemoryRepository.isExist(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + " exist");
        }
        if (isSuspended(appoint)) {
            throw new RuntimeException("User is suspended from the system");
        }
        if (!userMemoryRepository.isExist(newManager)) {
            throw new NoSuchElementException("No user called " + newManager + " exist");
        }
        if (appoint.charAt(0) != 'r') {
            throw new NoSuchElementException("No user called " + appoint + " is registered");
        }
        if (newManager.charAt(0) != 'r') {
            throw new NoSuchElementException("No user called " + newManager + "is registered");
        }
        User appointUser = userMemoryRepository.getUser(appoint);
        User newManagerUser = userMemoryRepository.getUser(newManager);
        if (!appointUser.isOwner(store_name_id)) {
            throw new IllegalAccessException("User must be Owner");
        }
        if (appoint.charAt(0) == 'r' && !appointUser.getLogged()) {
            throw new IllegalAccessException("Appoint user is not logged");
        }
        if (newManagerUser.isManager(store_name_id)) {
            throw new IllegalAccessException("User already Manager of this store");
        }
        if (newManagerUser.isOwner(store_name_id)) {
            throw new IllegalAccessException("User cannot be owner of this store");
        }
        newManagerUser.addManagerRole(appoint, store_name_id);
        newManagerUser.setPermissionsToManager(store_name_id, watch, editSupply, editBuyPolicy, editDiscountPolicy);

    }

    @Override
    public void appointOwner(String appoint, String newOwner, String storeName) throws IllegalAccessException, NoSuchElementException {
        if (!marketFacade.isStoreExist(storeName))
            throw new NoSuchElementException("No store called " + storeName + " exist");
        if (!userMemoryRepository.isExist(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + " exist");
        }
        if (isSuspended(appoint)) {
            throw new RuntimeException("User is suspended from the system");
        }
        if (!userMemoryRepository.isExist(newOwner)) {
            throw new NoSuchElementException("No user called " + newOwner + " exist");
        }
        if (appoint.charAt(0) != 'r') {
            throw new NoSuchElementException("No user called " + appoint + " is registered");
        }
        if (newOwner.charAt(0) != 'r') {
            throw new NoSuchElementException("No user called " + newOwner + "is registered");
        }
        User appointUser = userMemoryRepository.getUser(appoint);
        User newOwnerUser = userMemoryRepository.getUser(newOwner);
        if (!appointUser.isOwner(storeName)) {
            throw new IllegalAccessException("Appoint user must be Owner");
        }
        if (newOwnerUser.isOwner(storeName)) {
            throw new IllegalAccessException("User already Owner of this store");
        }
        newOwnerUser.addOwnerRole(appoint, storeName);
    }

    @Override
    public void editPermissionForManager(String userId, String managerToEdit, String storeNameId, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) throws IllegalAccessException, NoSuchElementException {
        if (!marketFacade.isStoreExist(storeNameId))
            throw new NoSuchElementException("No store called " + storeNameId + " exist");
        if (!userMemoryRepository.isExist(userId)) {
            throw new NoSuchElementException("No user called " + userId + "exist");
        }
        if (!userMemoryRepository.isExist(managerToEdit)) {
            throw new NoSuchElementException("No user called " + managerToEdit + "exist");
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
        User appointUser = userMemoryRepository.getUser(userId);
        User newManagerUser = userMemoryRepository.getUser(managerToEdit);
        if (appointUser.isOwner(storeNameId)) {
            throw new IllegalAccessException("User cannot be owner of this store");
        }
        if (newManagerUser.isManager(storeNameId)) {
            throw new IllegalAccessException("User already Manager of this store");
        }
        if (newManagerUser.getRoleByStoreId(storeNameId).getAppointedById().equals(userId)) {
            throw new IllegalAccessException("Owner cant edit permissions to manager that he/she didn't appointed");
        }
        newManagerUser.setPermissionsToManager(storeNameId, watch, editSupply, editBuyPolicy, editDiscountPolicy);
    }

    @Override
    public String getUserPassword(String username) {
        if (username.charAt(0) != 'r') {
            throw new NoSuchElementException("No user called " + username + " is registered");
        }
        User u = userMemoryRepository.getUser(username);
        if (u == null)
            throw new RuntimeException("No such registered user " + username);
        return u.getPass();
    }

    @Override
    public boolean isAdminRegistered() {
        boolean exists = false;
        for (User r : userMemoryRepository.getAllUsersAsList())
            if (r.isAdmin()) {
                exists = true;
                break;
            }
        return exists;
    }

    @Override
    public boolean isAdmin(String username) {
        for (User r : userMemoryRepository.getAllUsersAsList())
            if (r.getUsername().equals(username.substring(1))) {
                return r.isAdmin();
            }
        return false;
    }

    public boolean isUserExist(String username) {
        return userMemoryRepository.isExist(username);
    }

    public User getUser(String username) {
        return userMemoryRepository.getUser(username);
    }


    ///Purchase SECTION
    private void addPurchase(String registeredId) {
        storeSalesHistory.addPurchase(getUser(registeredId).addPurchasedProduct());
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
        return storeSalesHistory.getPurchaseHistory(username, storeName);
    }

    private synchronized boolean checkAvailabilityAndConditions(String username) {
        if (!isUserExist(username)) {
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
        if (isSuspended(username)) {
            throw new RuntimeException("User is suspended from the system");
        }
        if (!getUser(username).getLogged()) {
            logger.error("User is not logged in");
            throw new RuntimeException("User is not logged in");
        }
        User user = getUser(username);
        Cart cart = user.getCart();
        if (cart == null || cart.getShoppingBags().isEmpty()) {
            logger.error("Cart is empty or null");
            throw new RuntimeException("Cart is empty or null");
        }
        HashMap<String, ShoppingBag> shoppingBags = getUser(username).getCart().getShoppingBags();
        for (Map.Entry<String, ShoppingBag> shoppingBagInStore : shoppingBags.entrySet()) {
            for (Map.Entry<Integer, ProductInSale> productEntry : shoppingBagInStore.getValue().getProducts_list().entrySet()) {
                if (marketFacade.getStore(shoppingBagInStore.getKey()) == null)
                    throw new RuntimeException("store not exist");
                checkProductQuantity(username, productEntry.getKey(), shoppingBagInStore.getKey(), 0);
            }
        }
        return true;
    }

    private synchronized void releaseReservedProducts(String username) {
        HashMap<String, ShoppingBag> shoppingBags = getUser(username).getCart().getShoppingBags();
        for (Map.Entry<String, ShoppingBag> shoppingBagInStore : shoppingBags.entrySet()) {
            for (Map.Entry<Integer, ProductInSale> productEntry : shoppingBagInStore.getValue().getProducts_list().entrySet()) {
                marketFacade.releaseReservedProducts(productEntry.getKey(), productEntry.getValue().getQuantity(), shoppingBagInStore.getKey());
            }
        }
    }

    private synchronized void removeReservedProducts(String username) {
        HashMap<String, ShoppingBag> shoppingBags = getUser(username).getCart().getShoppingBags();
        for (Map.Entry<String, ShoppingBag> shoppingBagInStore : shoppingBags.entrySet()) {
            for (Map.Entry<Integer, ProductInSale> productEntry : shoppingBagInStore.getValue().getProducts_list().entrySet()) {
                marketFacade.removeReservedProducts(productEntry.getKey(), productEntry.getValue().getQuantity(), shoppingBagInStore.getKey());
            }
        }
    }

    @Override
    public synchronized void purchaseCart(String username) throws Exception {
        if (!checkAvailabilityAndConditions(username)) {
            logger.error("Products are not available or do not meet purchase conditions.");
            throw new RuntimeException("Products are not available or do not meet purchase conditions.");
        }

        removeReservedProducts(username);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                releaseReservedProducts(username);
                throw new RuntimeException("time out !");
            }
        }, 10 * 60 * 1000);
        double totalPrice = marketFacade.calculateTotalPrice(getUser(username).getCart().toJson());
        //TODO: fix process Payment
        int deliveryId = 0;  //For cancelling
        String address = ""; //TODO : Need to be a parameter of this function
        try {
            deliveryId = deliveryService.makeDelivery(address);
        } catch (Exception e) {
            releaseReservedProducts(username);
            throw new Exception("Error in Delivery");
        }

        int paymentId = 0;
        try {
            paymentId = paymentService.makePayment(totalPrice);
        } catch (Exception e) {
            deliveryService.cancelDelivery(deliveryId);
            releaseReservedProducts(username);
            throw new Exception("Error in Payment");
        }

        addPurchase(username);
        timer.cancel();
        timer.purge();
    }

    @Override
    public String calculatePrice(String username) throws Exception {
        double result = marketFacade.calculateTotalPrice(getUser(username).getCart().toJson());
        return Double.toString(Math.round(result * 100.0) / 100.0); // round to 2 decimal points
    }
}
