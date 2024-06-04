package com.example.trading_system.domain.users;

import com.example.trading_system.domain.stores.*;
import com.example.trading_system.service.UserServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.*;

public class UserFacadeImp implements UserFacade {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);
    private static UserFacadeImp instance = null;
    private MarketFacade marketFacade;
    private HashMap<String,User> users; //May be on User Repo

    private UserFacadeImp() {
        this.users=new HashMap<>();
        this.marketFacade = MarketFacadeImp.getInstance();
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
        return users;
    }

   @Override
   public void enter(int id) {
       Visitor visitor = new Visitor("v"+id);    //Responsibility of Repo
       users.put("v"+id,visitor);    //Responsibility of Repo
   }

    @Override
    public void exit(String username) throws Exception {
        if (users.containsKey(username)) {     //Responsibility of Repo
            users.remove(username);     //Responsibility of Repo
        } else {
            throw new Exception("No such user with username- " + username);
        }
    }

    @Override
    public void logout(int id,String username) {
        if(username.charAt(0)!='r'){
            throw new IllegalArgumentException("User performs Not like a registered");
        }
        User u = users.get(username);
        if (u == null)
            throw new IllegalArgumentException("No such user " + username);
        if (username.charAt(0)=='r' && !u.getLogged())
            throw new RuntimeException("User " + username + "already Logged out");
        saveUserCart(username);
        u.logout();
        enter(id);
    }

    @Override
    public void register(String username, String password, LocalDate birthdate) throws Exception {
        if (users.containsKey("r"+username))
            throw new Exception("username already exists - " + username);
        registerChecks(username, password, birthdate);
        String encrypted_pass = encrypt(password);
        Registered newUser = new Registered(username, encrypted_pass, birthdate);
        if (checkIfRegistersEmpty())       //Responsibility of Repo
            newUser.setAdmin(true);
        users.put("r"+username, newUser);   //Responsibility of Repo
    }

    private boolean checkIfRegistersEmpty(){
        if(users.isEmpty()){
            return false;
        }
        for(String username:users.keySet()){
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
        User u = users.get("r"+username);
        if (u == null)
            throw new RuntimeException("No such user " + username);
        if (username.charAt(0)=='r' && u.getLogged())
            throw new RuntimeException("User " + username + " already logged in");
        if (checkPassword(password, u.getPass())) {
            users.remove(usernameV); //Responsibility of Repo
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

    private void saveUserCart(String username) {
        users.get(username).getShopping_cart().saveCart();
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
        if (users.containsKey(username)) {
            users.get(username).getShopping_cart().addProductToCart(productId, quantity, storeName);
        }
        int quantityInStore = marketFacade.getStores().get(storeName).getProducts().get(productId).getProduct_quantity();
        int quantityInShoppingBag = users.get(username).getShopping_cart().getShoppingBags().get(storeName).getProducts_list().get(productId);
        if (quantity + quantityInShoppingBag > quantityInStore) {
            logger.error("Product quantity is too low");
            throw new RuntimeException("Product quantity is too low");
        }
    }

   @Override
   public synchronized String viewCart(String username) {
       if (!users.containsKey(username)) {
           logger.error("User not found");
           throw new RuntimeException("User not found");
       }
       if (username.charAt(0)=='r' && !users.get(username).getLogged()){
           logger.error("Registered user is not logged");
           throw new RuntimeException("Registered user is not logged");
       }
       Cart cart = users.get(username).getShopping_cart();
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
        if (!users.containsKey(username)) {
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
        if (username.charAt(0)=='r' && !users.get(username).getLogged()){
            logger.error("User is not logged");
            throw new RuntimeException("User is not logged");
        }
        int quantityInStore = marketFacade.getStores().get(storeName).getProducts().get(productId).getProduct_quantity();
        int quantityInShoppingBag = users.get(username).getShopping_cart().getShoppingBags().get(storeName).getProducts_list().get(productId);
        if (quantity + quantityInShoppingBag > quantityInStore) {
            logger.error("Product quantity is too low");
            throw new RuntimeException("Product quantity is too low");
        }
        users.get(username).getShopping_cart().addProductToCart(productId, quantity, storeName);
    }

    @Override
    public synchronized void removeFromCart(String username,int productId, String storeName, int quantity) {
        if (!users.containsKey(username)) {
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
        if (username.charAt(0)=='r' && !users.get(username).getLogged()){
            logger.error("User is not logged");
            throw new RuntimeException("User is not logged");
        }
        users.get(username).getShopping_cart().removeProductFromCart(productId, quantity, storeName);
    }

    @Override
    public void openStore(String username, String storeName, String description, StorePolicy policy) {
        marketFacade = MarketFacadeImp.getInstance();
        if (storeName == null) {
            logger.error("Store name is null");
            throw new RuntimeException("Store name is null");
        }
        if (marketFacade.isStoreExist(storeName) && !marketFacade.isStoresEmpty()) {
            logger.error("Store with name " + storeName + " already exists");
            throw new RuntimeException("Store with name " + storeName + " already exists");
        }
        if (!users.containsKey(username)) {
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
        marketFacade.addStore(storeName, description, policy, username,null);
        users.get(username).openStore(storeName);
    }

    @Override
    public void suggestOwner(String appoint, String newOwner, String storeName) throws IllegalAccessException, NoSuchElementException {
        if (!users.containsKey(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + "exist");
        }
        if (!users.containsKey(newOwner)) {
            throw new NoSuchElementException("No user called " + newOwner + "exist");
        }

        if (appoint.charAt(0)!='r') {
            throw new NoSuchElementException("No user called " + appoint + " is registered");
        }
        if (newOwner.charAt(0)!='r') {
            throw new NoSuchElementException("No user called " + newOwner + "is registered");
        }
        User appointUser = users.get(appoint);
        User newOwnerUser = users.get(newOwner);
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
        if (!users.containsKey(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + "exist");
        }
        if (!users.containsKey(newManager)) {
            throw new NoSuchElementException("No user called " + newManager + "exist");
        }
        if (appoint.charAt(0)!='r') {
            throw new NoSuchElementException("No user called " + appoint + " is registered");
        }
        if (newManager.charAt(0)!='r') {
            throw new NoSuchElementException("No user called " + newManager + "is registered");
        }
        User appointUser = users.get(appoint);
        User newManagerUser = users.get(newManager);
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
        if (!users.containsKey(newManager)) {
            throw new NoSuchElementException("No user called " + newManager + "exist");
        }
        if (!users.containsKey(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + "exist");
        }
        if (appoint.charAt(0)!='r') {
            throw new NoSuchElementException("No user called " + appoint + " is registered");
        }
        if (newManager.charAt(0)!='r') {
            throw new NoSuchElementException("No user called " + newManager + "is registered");
        }
        User appointUser = users.get(appoint);
        User newManagerUser = users.get(newManager);
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
    public void approveOwner(String newOwner, String storeName, String appoint) throws IllegalAccessException {
        if (!users.containsKey(newOwner)) {
            throw new NoSuchElementException("No user called " + newOwner + "exist");
        }
        if (!users.containsKey(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + "exist");
        }
        if (appoint.charAt(0)!='r') {
            throw new NoSuchElementException("No user called " + appoint + " is registered");
        }
        if (newOwner.charAt(0)!='r') {
            throw new NoSuchElementException("No user called " + newOwner + "is registered");
        }
        User appointUser = users.get(appoint);
        if (!appointUser.isOwner(storeName)) {
            throw new IllegalAccessException("User must be Owner");
        }
        User newOwnerUser = users.get(newOwner);
        if (newOwnerUser.isOwner(storeName)) {
            throw new IllegalAccessException("User already Owner of this store");
        }
        newOwnerUser.removeWaitingAppoint_Owner(storeName);
        newOwnerUser.addOwnerRole(appoint, storeName);
    }

    public void appointManager(String appoint, String newManager, String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) throws IllegalAccessException, NoSuchElementException {
        if (!users.containsKey(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + "exist");
        }
        if (!users.containsKey(newManager)) {
            throw new NoSuchElementException("No user called " + newManager + "exist");
        }
        if (appoint.charAt(0)!='r') {
            throw new NoSuchElementException("No user called " + appoint + " is registered");
        }
        if (newManager.charAt(0)!='r') {
            throw new NoSuchElementException("No user called " + newManager + "is registered");
        }
        User appointUser = users.get(appoint);
        User newManagerUser = users.get(newManager);
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
        if (!users.containsKey(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + "exist");
        }
        if (!users.containsKey(newOwner)) {
            throw new NoSuchElementException("No user called " + newOwner + "exist");
        }
        if (appoint.charAt(0)!='r') {
            throw new NoSuchElementException("No user called " + appoint + " is registered");
        }
        if (newOwner.charAt(0)!='r') {
            throw new NoSuchElementException("No user called " + newOwner + "is registered");
        }
        User appointUser = users.get(appoint);
        User newOwnerUser = users.get(newOwner);
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
        if (users.containsKey(userId)) {
            throw new NoSuchElementException("No user called " + userId + "exist");
        }
        if (!users.containsKey(managerToEdit)) {
            throw new NoSuchElementException("No user called " + managerToEdit + "exist");
        }
        if (userId.charAt(0)!='r') {
            throw new NoSuchElementException("No user called " + userId + " is registered");
        }
        if (managerToEdit.charAt(0)!='r') {
            throw new NoSuchElementException("No user called " + managerToEdit + "is registered");
        }
        User appointUser = users.get(userId);
        User newManagerUser = users.get(managerToEdit);
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
        User u = users.get(username);
        if (u == null)
            throw new RuntimeException("No such registered user " + username);
        return u.getPass();
    }

    @Override
    public boolean isAdminRegistered() {
        boolean exists = false;
        for (User r : users.values())
            if (r.isAdmin()) {
                exists = true;
                break;
            }
        return exists;
    }

    @Override
    public boolean isAdmin(String username){
        for (User r : users.values())
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

}


