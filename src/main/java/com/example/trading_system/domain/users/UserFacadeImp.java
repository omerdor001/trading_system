package com.example.trading_system.domain.users;

import com.example.trading_system.domain.stores.*;
import com.example.trading_system.service.UserServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class UserFacadeImp implements UserFacade {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);
    private static UserFacadeImp instance = null;
    private MarketFacade marketFacade;
    private UserRepository userMemoryRepository;

    private UserFacadeImp() {
        this.marketFacade = MarketFacadeImp.getInstance();
        this.userMemoryRepository= UserMemoryRepository.getInstance();
        marketFacade.initialize(this);
    }

    public static UserFacadeImp getInstance() {
        if (instance == null)
            instance = new UserFacadeImp();
        return instance;
    }

    @Override
    public void deleteInstance() {
        instance = null;
        if(marketFacade != null)
            marketFacade.deleteInstance();
        this.marketFacade = null;
    }

    @Override
    public HashMap<String, User> getUsers() {
        return userMemoryRepository.getAllUsers();
    }

   @Override
   public void enter(int id) {
        userMemoryRepository.addVisitor("v"+id);
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
    public void logout(int id,String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if(username.charAt(0)!='r'){
            throw new IllegalArgumentException("User performs Not like a registered");
        }
        User u = userMemoryRepository.getUser(username);
        if (u == null)
            throw new IllegalArgumentException("No such user " + username);
        if (username.charAt(0)=='r' && !u.getLogged())
            throw new IllegalArgumentException("User " + username + "already Logged out");
        saveUserCart(username);
        u.logout();
        enter(id);
    }

    @Override
    public void suspendUser(String admin, String toSuspend, LocalDateTime endSuspention) {
        if(!userMemoryRepository.isExist(admin)){
            throw new IllegalArgumentException("Admin user doesn't exist in the system");
        }
        if(!userMemoryRepository.isExist(toSuspend)){
            throw new IllegalArgumentException("User to suspend doesn't exist in the system");
        }
        if(!userMemoryRepository.getUser(admin).isAdmin()){
            throw new IllegalArgumentException("Only admin user can suspend users");
        }
        if(endSuspention.compareTo(LocalDateTime.now())<0){
            throw new IllegalArgumentException("Date of suspension cannot be before now");
        }
        User toSuspendUser=userMemoryRepository.getUser(toSuspend);
        toSuspendUser.suspend(endSuspention);
    }

    @Override
    public void endSuspendUser(String admin, String toSuspend) {
        if(!userMemoryRepository.isExist(admin)){
            throw new IllegalArgumentException("Admin user doesn't exist in the system");
        }
        if(!userMemoryRepository.isExist(toSuspend)){
            throw new IllegalArgumentException("User to suspend doesn't exist in the system");
        }
        if(!userMemoryRepository.getUser(admin).isAdmin()){
            throw new IllegalArgumentException("Only admin user can suspend users");
        }
        User toSuspendUser=userMemoryRepository.getUser(toSuspend);
        toSuspendUser.finishSuspensionByAdmin();
    }

    @Override
    public void checkForEndingSuspension(String toSuspend) {
        if(!userMemoryRepository.isExist(toSuspend)){
            throw new IllegalArgumentException("User to suspend doesn't exist in the system");
        }
        User toSuspendUser=userMemoryRepository.getUser(toSuspend);
        toSuspendUser.finishSuspension();
    }

    @Override
    public String watchSuspensions(String admin) {
        StringBuilder details = new StringBuilder();
        if(!userMemoryRepository.isExist(admin)){
            throw new IllegalArgumentException("Admin user doesn't exist in the system");
        }
        if(!userMemoryRepository.getUser(admin).isAdmin()){
            throw new IllegalArgumentException("Only admin user can suspend users");
        }
        for (User user:userMemoryRepository.getAllUsersAsList()){
            details.append("Username - "+user.getUsername() + "\n");
            details.append("Start of suspension - "+user.getSuspendedStart().toString()+"\n");
            details.append("Time of suspension (in days) - "+ Duration.between(user.getSuspendedStart(), user.getSuspendedEnd()).toDays()+"\n");
            details.append("Time of suspension (in hours) - "+ Duration.between(user.getSuspendedStart(), user.getSuspendedEnd()).toHours()+"\n");
            details.append("End of suspension - "+user.getSuspendedEnd().toString());
        }
        return details.toString();
    }

    private void saveUserCart(String username) {
        User user = userMemoryRepository.getUser(username);
        if (user == null || user.getShopping_cart() == null) {
            throw new IllegalArgumentException("user doesn't exist in the system");
        }
        userMemoryRepository.getUser(username).getShopping_cart().saveCart();
    }

    @Override
    public void register(String username, String password, LocalDate birthdate) throws Exception {
        if (userMemoryRepository.isExist("r"+username))
            throw new Exception("username already exists - " + username);
        registerChecks(username, password, birthdate);
        String encrypted_pass = encrypt(password);
        userMemoryRepository.addRegistered("r"+username,encrypted_pass,birthdate);
        if (checkIfRegistersEmpty())
            userMemoryRepository.getUser("r"+username).setAdmin(true);
    }

    private boolean checkIfRegistersEmpty(){
        if(userMemoryRepository.isEmpty()){
            return false;
        }
        for(String username:userMemoryRepository.getAllUsersAsUsernames()){
            if(username.charAt(0)=='r'){
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
    public void login(String usernameV,String username, String password) {
        User u = userMemoryRepository.getUser("r"+username);
        if (u == null)
            throw new RuntimeException("No such user " + username);
        if (username.charAt(0)=='r' && u.getLogged())
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
        if (storeName == null) {
            logger.error("Store name is null");
            throw new RuntimeException("Store name is null");
        }
        if (marketFacade.isStoreExist(storeName)) {
            logger.error("Store with name " + storeName + " already exists");
            throw new RuntimeException("Store with name " + storeName + " already exists");
        }
        if (userMemoryRepository.isExist(username)) {
            userMemoryRepository.getUser(username).getShopping_cart().addProductToCart(productId, quantity, storeName);
        }
        int quantityInStore = marketFacade.getStores().get(storeName).getProducts().get(productId).getProduct_quantity();
        int quantityInShoppingBag = userMemoryRepository.getUser(username).getShopping_cart().getShoppingBags().get(storeName).getProducts_list().get(productId);
        if (quantity + quantityInShoppingBag > quantityInStore) {
            logger.error("Product quantity is too low");
            throw new RuntimeException("Product quantity is too low");
        }
    }

   @Override
   public synchronized String viewCart(String username) {
       if (!userMemoryRepository.isExist(username)) {
           logger.error("User not found");
           throw new RuntimeException("User not found");
       }
       if (username.charAt(0)=='r' && !userMemoryRepository.getUser(username).getLogged()){
           logger.error("Registered user is not logged");
           throw new RuntimeException("Registered user is not logged");
       }
       Cart cart = userMemoryRepository.getUser(username).getShopping_cart();
       StringBuilder cartDetails = new StringBuilder();
       double totalAllStores = 0.0;
       for (Map.Entry<String, ShoppingBag> entry : cart.getShoppingBags().entrySet()) {
           String storeId = entry.getKey();
           ShoppingBag shoppingBag = entry.getValue();
           cartDetails.append("Store name: ").append(storeId).append("\n");
           double totalStore = 0.0;
           for (Map.Entry<Integer, Integer> productEntry : shoppingBag.getProducts_list().entrySet()) {
               Product product = marketFacade.getStores().get(storeId).getProducts().get(productEntry.getKey());
               int quantity = productEntry.getValue();
               double price = product.getProduct_price();
               double totalPrice = price * quantity;
               totalStore += totalPrice;
               cartDetails.append("Product Id: ").append(product.getProduct_id()).append(", Name: ").append(product.getProduct_name())
                       .append(", Quantity: ").append(quantity).append(", Price per unit: ").append(price).append(", Total Price: ").append(totalPrice).append("\n");
           }
           cartDetails.append("Total for Store name ").append(storeId).append(": ").append(totalStore).append("\n\n");
           totalAllStores += totalStore;
       }
       cartDetails.append("Overall Total for All Stores: ").append(totalAllStores).append("\n");
       return cartDetails.toString();
   }

    @Override
    public synchronized void addToCart(String username, int productId, String storeName, int quantity) {
        if (!userMemoryRepository.isExist(username)) {
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
        if (storeName == null) {
            logger.error("Store name is null");
            throw new RuntimeException("Store name is null");
        }
        if (marketFacade.isStoreExist(storeName)) {
            logger.error("Store with name " + storeName + " already exists");
            throw new RuntimeException("Store with name " + storeName + " already exists");
        }
        if (username.charAt(0)=='r' && !userMemoryRepository.getUser(username).getLogged()){
            logger.error("User is not logged");
            throw new RuntimeException("User is not logged");
        }
        int quantityInStore = marketFacade.getStores().get(storeName).getProducts().get(productId).getProduct_quantity();
        int quantityInShoppingBag = userMemoryRepository.getUser(username).getShopping_cart().getShoppingBags().get(storeName).getProducts_list().get(productId);
        if (quantity + quantityInShoppingBag > quantityInStore) {
            logger.error("Product quantity is too low");
            throw new RuntimeException("Product quantity is too low");
        }
        userMemoryRepository.getUser(username).getShopping_cart().addProductToCart(productId, quantity, storeName);
    }

    @Override
    public synchronized void removeFromCart(String username,int productId, String storeName, int quantity) {
        if (!userMemoryRepository.isExist(username)) {
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
        if (storeName == null) {
            logger.error("Store name is null");
            throw new RuntimeException("Store name is null");
        }
        if (marketFacade.isStoreExist(storeName)) {
            logger.error("Store with name " + storeName + " already exists");
            throw new RuntimeException("Store with name " + storeName + " already exists");
        }
        if (username.charAt(0)=='r' && !userMemoryRepository.getUser(username).getLogged()){
            logger.error("User is not logged");
            throw new RuntimeException("User is not logged");
        }
        userMemoryRepository.getUser(username).getShopping_cart().removeProductFromCart(productId, quantity, storeName);
    }

    @Override
    public void openStore(String username, String storeName, String description, StorePolicy policy) {
        if (!userMemoryRepository.isExist(username)) {
            logger.error("While opening store - User not found");
            throw new IllegalArgumentException("User not found");
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
            marketFacade.addStore(storeName, description, policy, username, null);
            userMemoryRepository.getUser(username).openStore(storeName);
        } catch (Exception e) {
            logger.error("Failed to open store: {}", e.getMessage());
            throw new IllegalArgumentException("Failed to open store", e);
        }
    }

    @Override
    public void suggestOwner(String appoint, String newOwner, String storeName) throws IllegalAccessException, NoSuchElementException {
        if(!marketFacade.isStoreExist(storeName))
            throw new NoSuchElementException("No store called " + storeName + " exist");
        if (!userMemoryRepository.isExist(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + " exist");
        }
        if (!userMemoryRepository.isExist(newOwner)) {
            throw new NoSuchElementException("No user called " + newOwner + " exist");
        }

        if (appoint.charAt(0)!='r') {
            throw new NoSuchElementException("No user called " + appoint + " is registered");
        }
        if (newOwner.charAt(0)!='r') {
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
        if(!marketFacade.isStoreExist(store_name_id))
            throw new NoSuchElementException("No store called " + store_name_id + " exist");
        if (!userMemoryRepository.isExist(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + " exist");
        }
        if (!userMemoryRepository.isExist(newManager)) {
            throw new NoSuchElementException("No user called " + newManager + " exist");
        }
        if (appoint.charAt(0)!='r') {
            throw new NoSuchElementException("No user called " + appoint + " is registered");
        }
        if (newManager.charAt(0)!='r') {
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
        if(!marketFacade.isStoreExist(store_name_id))
            throw new NoSuchElementException("No store called " + store_name_id + " exist");
        if (!userMemoryRepository.isExist(newManager)) {
            throw new NoSuchElementException("No user called " + newManager + " exist");
        }
        if (!userMemoryRepository.isExist(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + " exist");
        }
        if (appoint.charAt(0)!='r') {
            throw new NoSuchElementException("No user called " + appoint + " is registered");
        }
        if (newManager.charAt(0)!='r') {
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
    }

    @Override
    public void rejectToManageStore(String userName, String storeName, String appoint) throws IllegalAccessException {
        if(!marketFacade.isStoreExist(storeName))
            throw new NoSuchElementException("No store called " + storeName + " exist");
        if (!userMemoryRepository.isExist(userName)) {
            throw new NoSuchElementException("No user called " + userName + " exist");
        }
        if (!userMemoryRepository.isExist(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + " exist");
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
        if(!marketFacade.isStoreExist(storeName))
            throw new NoSuchElementException("No store called " + storeName + " exist");
        if (!userMemoryRepository.isExist(newOwner)) {
            throw new NoSuchElementException("No user called " + newOwner + " exist");
        }
        if (!userMemoryRepository.isExist(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + " exist");
        }
        if (appoint.charAt(0)!='r') {
            throw new NoSuchElementException("No user called " + appoint + " is registered");
        }
        if (newOwner.charAt(0)!='r') {
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
    }

    @Override
    public void rejectToOwnStore(String userName, String storeName, String appoint) throws IllegalAccessException {
        if(!marketFacade.isStoreExist(storeName))
            throw new NoSuchElementException("No store called " + storeName + " exist");
        if (!userMemoryRepository.isExist(userName)) {
            throw new NoSuchElementException("No user called " + userName + " exist");
        }
        if (!userMemoryRepository.isExist(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + " exist");
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

    public void appointManager(String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) throws IllegalAccessException, NoSuchElementException {
        if(!marketFacade.isStoreExist(store_name_id))
            throw new NoSuchElementException("No store called " + store_name_id + " exist");
        if (!userMemoryRepository.isExist(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + " exist");
        }
        if (!userMemoryRepository.isExist(newManager)) {
            throw new NoSuchElementException("No user called " + newManager + " exist");
        }
        if (appoint.charAt(0)!='r') {
            throw new NoSuchElementException("No user called " + appoint + " is registered");
        }
        if (newManager.charAt(0)!='r') {
            throw new NoSuchElementException("No user called " + newManager + "is registered");
        }
        User appointUser = userMemoryRepository.getUser(appoint);
        User newManagerUser = userMemoryRepository.getUser(newManager);
        if (!appointUser.isOwner(store_name_id)) {
            throw new IllegalAccessException("User must be Owner");
        }
        if (appoint.charAt(0)=='r' && !appointUser.getLogged()) {
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
        if(!marketFacade.isStoreExist(storeName))
            throw new NoSuchElementException("No store called " + storeName + " exist");
        if (!userMemoryRepository.isExist(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + " exist");
        }
        if (!userMemoryRepository.isExist(newOwner)) {
            throw new NoSuchElementException("No user called " + newOwner + " exist");
        }
        if (appoint.charAt(0)!='r') {
            throw new NoSuchElementException("No user called " + appoint + " is registered");
        }
        if (newOwner.charAt(0)!='r') {
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
        if(!marketFacade.isStoreExist(storeNameId))
            throw new NoSuchElementException("No store called " + storeNameId + " exist");
        if (!userMemoryRepository.isExist(userId)) {
            throw new NoSuchElementException("No user called " + userId + "exist");
        }
        if (!userMemoryRepository.isExist(managerToEdit)) {
            throw new NoSuchElementException("No user called " + managerToEdit + "exist");
        }
        if (userId.charAt(0)!='r') {
            throw new NoSuchElementException("No user called " + userId + " is registered");
        }
        if (managerToEdit.charAt(0)!='r') {
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
        if (username.charAt(0)!='r') {
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
    public boolean isAdmin(String username){
        for (User r : userMemoryRepository.getAllUsersAsList())
            if (r.getUsername().equals(username.substring(1))) {
                return r.isAdmin();
            }
        return false;
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

    public boolean isUserExist(String username){
        return userMemoryRepository.isExist(username);
    }

    public User getUser(String username){
        return userMemoryRepository.getUser(username);
    }

}


