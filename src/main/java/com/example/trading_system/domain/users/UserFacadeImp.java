package com.example.trading_system.domain.users;

import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
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
    private UserRepository userRepository;
    private DeliveryService deliveryService;
    private PaymentService paymentService;


    private UserFacadeImp(PaymentService paymentService, DeliveryService deliveryService,UserRepository userRepository,StoreRepository storeRepository) {
        this.marketFacade = MarketFacadeImp.getInstance(storeRepository);
        this.userRepository = userRepository;
        this.paymentService = paymentService;
        this.deliveryService = deliveryService;
    }

    public static UserFacadeImp getInstance(PaymentService paymentService, DeliveryService deliveryService,UserRepository userRepository,StoreRepository storeRepository) {
        if (instance == null) {
            instance = new UserFacadeImp(paymentService,deliveryService,userRepository,storeRepository);
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
        this.userRepository.deleteInstance();
    }

    @Override
    public HashMap<String, User> getUsers() {
        return userRepository.getAllUsers();
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
        User u = userRepository.getUser(username);
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
        if (!userRepository.isExist(admin)) {
            throw new IllegalArgumentException("Admin user doesn't exist in the system");
        }
        if (!userRepository.isExist(toSuspend)) {
            throw new IllegalArgumentException("User to suspend doesn't exist in the system");
        }
        if (!userRepository.getUser(admin).isAdmin()) {
            throw new IllegalArgumentException("Only admin user can suspend users");
        }
        if (endSuspention.compareTo(LocalDateTime.now()) < 0) {
            throw new IllegalArgumentException("Date of suspension cannot be before now");
        }
        User toSuspendUser = userRepository.getUser(toSuspend);
        toSuspendUser.suspend(endSuspention);
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
    }

    @Override
    public void setAddress(String username, String address){
        if (!userRepository.isExist(username)) {
            throw new IllegalArgumentException("User doesn't exist in the system");
        }
        User user = userRepository.getUser(username);
        user.setAddress(address);
    }

    @Override
    public String watchSuspensions(String admin) {
        StringBuilder details = new StringBuilder();
        if (!userRepository.isExist(admin)) {
            throw new IllegalArgumentException("Admin user doesn't exist in the system");
        }
        if (!userRepository.getUser(admin).isAdmin()) {
            throw new IllegalArgumentException("Only admin user can suspend users");
        }
        for (User user : userRepository.getAllUsersAsList()) {
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
        if (!userRepository.isExist(username)) {
            throw new IllegalArgumentException("User doesn't exist in the system");
        }
        User user = userRepository.getUser(username);
        if (user.getSuspendedEnd() == null) {
            return false;
        }
        if(user.getSuspendedEnd().compareTo(LocalDateTime.now())<=0){
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
        if (userRepository.isExist("r" + username))
            throw new Exception("username already exists - " + username);
        registerChecks(username, password, birthdate);
        String encrypted_pass = encrypt(password);
        if (checkIfRegistersEmpty()) {
            userRepository.addRegistered("r" + username, encrypted_pass, birthdate);
            userRepository.getUser("r" + username).setAdmin(true);
        } else {
            userRepository.addRegistered("r" + username, encrypted_pass, birthdate);
        }

    }

    private boolean checkIfRegistersEmpty() {
        if (userRepository.isEmpty()) {
            return true;
        }
        for (String username :userRepository.getAllUsersAsUsernames()) {
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
        User u = userRepository.getUser("r" + username);
        if (u == null)
            throw new RuntimeException("No such user " + username);
        if (username.charAt(0) == 'r' && u.getLogged())
            throw new RuntimeException("User " + username + " already logged in");
        if (checkPassword(password, u.getPass())) {
            userRepository.deleteUser(usernameV);
            u.login();
        }
        else {
            logger.error("Wrong password, Failed login user: {}", username);
            throw new RuntimeException("Wrong password");
        }
    }

    @Override
    public boolean sendNotification(User sender, User receiver, String content) {
//        String notification = sender.sendNotification(receiver.getUsername(), content);
//        receiver.receiveNotification(notification);
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
        if (!marketFacade.getStore(storeName).isOpen())
            throw new IllegalArgumentException("When store is closed you cant add to cart from this store");
        if (username.charAt(0) == 'r' && !userRepository.getUser(username).getLogged()) {
            logger.error("User is not logged in: " + username);
            throw new RuntimeException("User is not logged in: " + username);
        }
        checkProductQuantity(username, productId, storeName, quantity);
        Product p = marketFacade.getStore(storeName).getProduct(productId);
        userRepository.getUser(username).addProductToCart(productId, quantity, storeName, p.getProduct_price(), p.getCategory().getIntValue());
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
        if (!store.isOpen())
            throw new IllegalArgumentException("When store is closed cant to check product quantity");
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
        userRepository.getUser(username).removeProductFromCart(productId, quantity, storeName);
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
            userRepository.getUser(username).openStore(storeName);
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
        newOwnerUser.addWaitingAppoint_Owner(storeName);
    }

    @Override
    public void suggestManage(String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) throws IllegalAccessException, NoSuchElementException {
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
        newManagerUser.addWaitingAppoint_Manager(store_name_id, watch, editSupply, editBuyPolicy, editDiscountPolicy);
    }

    @Override
    public void approveManage(String newManager, String store_name_id, String appoint) throws IllegalAccessException {
        if (!marketFacade.isStoreExist(store_name_id))
            throw new NoSuchElementException("No store called " + store_name_id + " exist");
        if (!userRepository.isExist(newManager)) {
            throw new NoSuchElementException("No user called " + newManager + " exist");
        }
        if (!userRepository.isExist(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + " exist");
        }
        if(isSuspended(newManager)){
            throw new RuntimeException("User is suspended from the system");
        }
        User appointUser = userRepository.getUser(appoint);
        User newManagerUser = userRepository.getUser(newManager);
        if (!newManagerUser.getLogged()) {
            throw new IllegalAccessException("New Manager user is not logged");
        }
        if (!appointUser.isOwner(store_name_id)) {
            throw new IllegalAccessException("User must be Owner");
        }
        if (newManagerUser.isManager(store_name_id)) {
            throw new IllegalAccessException("User already Manager of this store");
        }
        if (newManagerUser.isOwner(store_name_id)) {
            throw new IllegalAccessException("User cannot be owner of this store");
        }
        if(newManagerUser.removeWaitingAppoint_Manager(store_name_id) == null)
            throw new RuntimeException("No appointment requests in this store.");
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
        if(isSuspended(userName)){
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
        List<Boolean> perm = newManagerUser.removeWaitingAppoint_Manager(storeName);
        if (perm == null)
            throw new IllegalAccessException("No one suggest this user to be a manager");
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
        if(isSuspended(newOwner)){
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
        if(!newOwnerUser.removeWaitingAppoint_Owner(storeName))
            throw new IllegalAccessException("No one suggest this user to be a owner");

        if(marketFacade.getStore(storeName).getManagers().contains(newOwner)) {
            newOwnerUser.removeManagerRole(storeName);
            marketFacade.getStore(storeName).removeManager(newOwner);
        }
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
        if(isSuspended(userName)){
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
        if(!newOwnerUser.removeWaitingAppoint_Owner(storeName))
            throw new IllegalAccessException("No one suggest this user to be a owner");
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
        if (store.getFounder().equals(userName))
            throw new IllegalAccessException("Founder cant waive on ownership");

        List<String> storeOwners = store.getOwners();
        List<String> storeManagers = store.getManagers();

        for (String storeOwner : storeOwners) {
            User user = userRepository.getUser(storeOwner);
            if (user.getRoleByStoreId(storeName).getAppointedById().equals(userName))
            {
                user.removeOwnerRole(storeName);
                store.removeOwner(storeName);
            }
        }
        for (String storeOwner : storeOwners) {
            User user = userRepository.getUser(storeOwner);
            if (user.getRoleByStoreId(storeName).getAppointedById().equals(userName))
            {
                user.removeOwnerRole(storeName);
                store.removeOwner(storeOwner);
            }
        }

        for (String storeManager : storeManagers) {
            User user = userRepository.getUser(storeManager);
            if (user.getRoleByStoreId(storeName).getAppointedById().equals(userName))
            {
                user.removeManagerRole(storeName);
                store.removeManager(storeManager);
            }
        }

        owner.removeOwnerRole(storeName);
        marketFacade.getStore(storeName).removeOwner(userName);

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
        marketFacade.getStore(storeName).removeManager(manager);
    }

    @Override
    public void fireOwner(String ownerAppoint, String storeName, String owner) throws IllegalAccessException
    {
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

        Store store = marketFacade.getStore(storeName);
        List<String> storeOwners = store.getOwners();
        List<String> storeManagers = store.getManagers();

        for (String storeOwner : storeOwners) {
            User user = userRepository.getUser(storeOwner);
            if (user.getRoleByStoreId(storeName).getAppointedById().equals(owner))
            {
                user.removeOwnerRole(storeName);
                store.removeOwner(storeOwner);
            }
        }

        for (String storeManager : storeManagers) {
            User user = userRepository.getUser(storeManager);
            if (user.getRoleByStoreId(storeName).getAppointedById().equals(owner))
            {
                user.removeManagerRole(storeName);
                store.removeManager(storeManager);
            }
        }
        ownerUser.removeOwnerRole(storeName);
        marketFacade.getStore(storeName).removeOwner(owner);
    }


    public void appointManager(String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) throws IllegalAccessException, NoSuchElementException {
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
        if (!appointUser.getLogged()) {
            throw new IllegalAccessException("Appoint user is not logged");
        }
        if (!appointUser.isOwner(store_name_id)) {
            throw new IllegalAccessException("Appoint user must be Owner");
        }
        if (newManagerUser.isManager(store_name_id)) {
            throw new IllegalAccessException("User already Manager of this store");
        }
        if (newManagerUser.isOwner(store_name_id)) {
            throw new IllegalAccessException("User cannot be owner of this store");
        }
        newManagerUser.addManagerRole(appoint, store_name_id);
        marketFacade.getStore(store_name_id).addManager(newManager);
        newManagerUser.setPermissionsToManager(store_name_id, watch, editSupply, editBuyPolicy, editDiscountPolicy);
    }

    @Override
    public void appointOwner(String appoint, String newOwner, String storeName) throws IllegalAccessException, NoSuchElementException {
        if (!marketFacade.isStoreExist(storeName))
            throw new NoSuchElementException("No store called " + storeName + " exist");
        if (!userRepository.isExist(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + " exist");
        }
        if (isSuspended(appoint)) {
            throw new RuntimeException("User is suspended from the system");
        }
        if (!userRepository.isExist(newOwner)) {
            throw new NoSuchElementException("No user called " + newOwner + " exist");
        }
        User appointUser = userRepository.getUser(appoint);
        User newOwnerUser = userRepository.getUser(newOwner);
        if (!appointUser.getLogged()) {
            throw new IllegalAccessException("Appoint user is not logged");
        }
        if (!appointUser.isOwner(storeName)) {
            throw new IllegalAccessException("Appoint user must be Owner");
        }
        if (newOwnerUser.isOwner(storeName)) {
            throw new IllegalAccessException("User already Owner of this store");
        }
        newOwnerUser.addOwnerRole(appoint, storeName);
        marketFacade.getStore(storeName).addOwner(newOwner);
    }

    @Override
    public void editPermissionForManager(String userId, String managerToEdit, String storeNameId, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) throws IllegalAccessException, NoSuchElementException {
        if (!marketFacade.isStoreExist(storeNameId))
            throw new NoSuchElementException("No store called " + storeNameId + " exist");
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
        if (!appointUser.isOwner(storeNameId)) {
            throw new IllegalAccessException("User must be owner of this store");
        }
        if (!managerUser.isManager(storeNameId)) {
            throw new IllegalAccessException("User must be a  Manager of this store");
        }
        if (!managerUser.getRoleByStoreId(storeNameId).getAppointedById().equals(userId)) {
            throw new IllegalAccessException("Owner cant edit permissions to manager that he/she didn't appointed");
        }
        managerUser.setPermissionsToManager(storeNameId, watch, editSupply, editBuyPolicy, editDiscountPolicy);
    }

    @Override
    public String getUserPassword(String username) {
        if (username.charAt(0) != 'r') {
            throw new NoSuchElementException("No user called " + username + " is registered");
        }
        User u = userRepository.getUser(username);
        if (u == null)
            throw new RuntimeException("No such registered user " + username);
        return u.getPass();
    }

    @Override
    public boolean isAdminRegistered() {
        boolean exists = false;
        for (User r : userRepository.getAllUsersAsList())
            if (r.isAdmin()) {
                exists = true;
                break;
            }
        return exists;
    }

    @Override
    public boolean isAdmin(String username) {
        for (User r : userRepository.getAllUsersAsList())
            if (r.getUsername().equals(username.substring(1))) {
                return r.isAdmin();
            }
        return false;
    }

    public boolean isUserExist(String username) {
        return userRepository.isExist(username);
    }

    public User getUser(String username) {
        return userRepository.getUser(username);
    }


    ///Purchase SECTION - already added to store history
    private void addPurchase(String registeredId) {
        getUser(registeredId).addPurchasedProduct();
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
        if(!marketFacade.isStoreExist(storeName)){
            logger.error("Store {} does not exist", storeName);
            throw new RuntimeException("Store does not exist");
        }
        return marketFacade.getStore(storeName).getPurchaseHistoryString(username);
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
        HashMap<String, ShoppingBag> shoppingBags = getUser(username).getCart().getShoppingBags();
        User user=userRepository.getUser(username);
        if(!marketFacade.validatePurchasePolicies(user.getCart().toJson(),user.getAge())){
            logger.error("Products do not meet purchase policies conditions.");
            throw new RuntimeException("Products do not meet purchase policies conditions.");
        }
        removeReservedProducts(username);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                releaseReservedProducts(username);
                throw new RuntimeException("time out !");
            }
        }, 10 * 60 * 1000);
        double totalPrice = marketFacade.calculateTotalPrice(user.getCart().toJson());
        int deliveryId = 0;  //For cancelling
        String address =user.getAddress();
        try {
            deliveryId = deliveryService.makeDelivery(address);
        } catch (Exception e) {
            releaseReservedProducts(username);
            throw new Exception("Error in Delivery");
        }
        if(deliveryId<0){                                //Can Use Them
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
        if(paymentId<0){
            deliveryService.cancelDelivery(deliveryId);
            releaseReservedProducts(username);
            throw new Exception("Error in Payment");
        }
        for (Map.Entry<String, ShoppingBag> shoppingBagInStore : shoppingBags.entrySet()) {
            Store store = marketFacade.getStore(shoppingBagInStore.getValue().getStoreId());
            Purchase purchase = new Purchase(username,shoppingBagInStore.getValue().getProducts_list().values().stream().toList(),shoppingBagInStore.getValue().calculateTotalPrice(),shoppingBagInStore.getValue().getStoreId());
            store.addPurchase(purchase);
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
