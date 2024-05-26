package com.example.trading_system.domain.users;

import com.example.trading_system.domain.stores.*;
import com.example.trading_system.service.Security;
import com.example.trading_system.service.UserServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class UserFacadeImp implements UserFacade {
    private HashMap<Integer, Visitor> visitors;
    private HashMap<String, Registered> registered;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);
    MarketFacade marketFacade = MarketFacadeImp.getInstance();

    private UserFacadeImp() {
        this.registered = new HashMap<>();
        this.visitors = new HashMap<>();
    }
    private static class Singleton {
        private static final UserFacadeImp INSTANCE = new UserFacadeImp();
    }

    public static UserFacadeImp getInstance() {
        return UserFacadeImp.Singleton.INSTANCE;
    }

    @Override
    public HashMap<Integer, Visitor> getVisitors() {
        return visitors;
    }

    @Override
    public HashMap<String, Registered> getRegistered() {
        return registered;
    }

    @Override
    public void createVisitor(int id) {
        Visitor visitor = new Visitor(id);
        visitors.put(id, visitor);
    }

    @Override
    public void exit() {

    }


    @Override
    public void enter(int id) {
        Visitor visitor = new Visitor(id);
        visitors.put(id, visitor);
    }

    @Override
    public void exit(int id) throws Exception {
        if (visitors.containsKey(id)) {
            visitors.remove(id);
        } else {
            throw new Exception("No such visitor with id- " + id);
        }
    }

    @Override
    public void exit(String username) throws Exception {
        if (registered.containsKey(username)) {
            registered.remove(username);
        } else {
            throw new Exception("No such user with username- " + username);
        }
    }


    @Override
    public void register(int id, String username, String password, LocalDate birthdate) throws Exception {
        registerChecks(id, username, password, birthdate);
        String encrypted_pass = Security.encrypt(password);
        Registered newUser = new Registered(id, username, encrypted_pass, birthdate);
        if (registered.isEmpty())
            newUser.setAdmin(true);
        registered.put(username, newUser);
    }

    private void registerChecks(int id, String username, String password, LocalDate birthdate) throws Exception {
        if (username == null) throw new Exception("Username is null");
        if (username.isEmpty()) throw new Exception("Username is empty");
        if (password == null) throw new Exception("Encrypted password is null");
        if (password.isEmpty()) throw new Exception("Encrypted password is empty");
        if (birthdate == null) throw new Exception("Birthdate password is null");
        if (registered.containsKey(username)) throw new Exception("username already exists - " + username);
    }

    @Override
    public void login(String username) {
        User u = registered.get(username);
        if (u == null)
            throw new RuntimeException("No such user " + username);
        if (u.getLogged())
            throw new RuntimeException("User " + username + " already logged in");
        u.login();
    }

    public void logout(int id, String username) {
        User u = getRegistered().get(username);
        if (u == null)
            throw new IllegalArgumentException("No such user " + username);
        if (!u.getLogged())
            throw new RuntimeException("User " + username + "already Logged out");
        saveUserCart(username);
        u.logout();
    }


    @Override
    public boolean sendNotification(User sender, User receiver, String content) {
        String notification = sender.sendNotification(receiver.getId(), content);
        receiver.receiveNotification(notification);
        return receiver.getLogged();
        //TODO return something to show the notification if receiver is logged in - maybe boolean if logged in
    }

    @Override
    public void saveUserCart(String username) {
        registered.get(username).getShopping_cart().saveCart();
    }

    @Override
    public void saveUserCart(int id, int productId, String storeName, int quantity) {
        int quntityInStore = marketFacade.getStores().get(storeName).getProducts().get(productId).getProduct_quantity();
        int quantityInShoppingBag = visitors.get(id).getShopping_cart().getShoppingBags().get(storeName).getProducts_list().get(productId);
        if (quantity + quantityInShoppingBag > quntityInStore) {
            logger.error("Product quantity is too low");
            throw new RuntimeException("Product quantity is too low");
        }
        if (storeName == null) {
            logger.error("Store name is null");
            throw new RuntimeException("Store name is null");
        }
        if (marketFacade.getStores().containsKey(storeName)) {
            logger.error("Store with name " + storeName + " already exists");
            throw new RuntimeException("Store with name " + storeName + " already exists");
        }
        if (visitors.containsKey(id)) {
            visitors.get(id).getShopping_cart().addProductToCart(productId, quantity, storeName);
        }
    }

    @Override
    public synchronized void visitorAddToCart(int id, int productId, String storeName, int quantity) {
        int quntityInStore = marketFacade.getStores().get(storeName).getProducts().get(productId).getProduct_quantity();
        int quantityInShoppingBag = visitors.get(id).getShopping_cart().getShoppingBags().get(storeName).getProducts_list().get(productId);
        if (!visitors.containsKey(id)) {
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
        if (quantity + quantityInShoppingBag > quntityInStore) {
            logger.error("Product quantity is too low");
            throw new RuntimeException("Product quantity is too low");
        }
        if (storeName == null) {
            logger.error("Store name is null");
            throw new RuntimeException("Store name is null");
        }
        if (marketFacade.getStores().containsKey(storeName)) {
            logger.error("Store with name " + storeName + " already exists");
            throw new RuntimeException("Store with name " + storeName + " already exists");
        }
        if (visitors.containsKey(id)) {
            visitors.get(id).getShopping_cart().addProductToCart(productId, quantity, storeName);
        }
    }

    @Override
    public synchronized void visitorRemoveFromCart(int id, int productId, String storeName, int quantity) {
        if (!visitors.containsKey(id)) {
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
        if (storeName == null) {
            logger.error("Store name is null");
            throw new RuntimeException("Store name is null");
        }
        if (marketFacade.getStores().containsKey(storeName)) {
            logger.error("Store with name " + storeName + " already exists");
            throw new RuntimeException("Store with name " + storeName + " already exists");
        }
        if (visitors.containsKey(id)) {
            visitors.get(id).getShopping_cart().removeProductFromCart(productId, quantity, storeName);
        }
    }

    @Override
    public synchronized void registeredAddToCart(String username, int productId, String storeName, int quantity) {

        if (storeName == null) {
            logger.error("Store name is null");
            throw new RuntimeException("Store name is null");
        }
        if (marketFacade.getStores().containsKey(storeName)) {
            logger.error("Store with name " + storeName + " already exists");
            throw new RuntimeException("Store with name " + storeName + " already exists");
        }
        if (registered.get(username) == null) {
            logger.error("User with name " + username + " does not exist");
            throw new RuntimeException("User with name " + username + " does not exist");
        }
        if (!registered.get(username).getLogged()) {
            logger.error("user is not logged");
            throw new RuntimeException("user is not logged");
        }
        int quntityInStore = marketFacade.getStores().get(storeName).getProducts().get(productId).getProduct_quantity();
        int quantityInShoppingBag = registered.get(username).getShopping_cart().getShoppingBags().get(storeName).getProducts_list().get(productId);
        if (quantity + quantityInShoppingBag > quntityInStore) {
            logger.error("Product quantity is too low");
            throw new RuntimeException("Product quantity is too low");
        }
        if (registered.containsKey(username)) {
            registered.get(username).getShopping_cart().addProductToCart(productId, quantity, storeName);
        }
    }

    @Override
    public synchronized void registeredRemoveFromCart(String username, int productId, String storeName, int quantity) throws Exception {
        if (storeName == null) {
            logger.error("Store name is null");
            throw new RuntimeException("Store name is null");
        }
        if (marketFacade.getStores().containsKey(storeName)) {
            logger.error("Store with name " + storeName + " already exists");
            throw new RuntimeException("Store with name " + storeName + " already exists");
        }
        if (registered.get(username) == null) {
            logger.error("User with name " + username + " does not exist");
            throw new RuntimeException("User with name " + username + " does not exist");
        }
        if (!registered.get(username).getLogged()) {
            logger.error("user is not logged");
            throw new RuntimeException("user is not logged");
        }
        if (registered.containsKey(username)) {
            registered.get(username).getShopping_cart().removeProductFromCart(productId, quantity, storeName);
        }
    }

    @Override
    public void openStore(String username, String storeName, String description, StorePolicy policy) {
        if (storeName == null) {
            logger.error("Store name is null");
            throw new RuntimeException("Store name is null");
        }
        if (marketFacade.getStores().containsKey(storeName)) {
            logger.error("Store with name " + storeName + " already exists");
            throw new RuntimeException("Store with name " + storeName + " already exists");
        }
        if (!registered.containsKey(username)) {
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
        Store store = new Store(storeName, description, policy, username);
        marketFacade.addStore(store);
        registered.get(username).openStore();

    }

    @Override
    public void suggestOwner(String appoint, String newOwner, String storeName) throws IllegalAccessException, NoSuchElementException {
        if (!registered.containsKey(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + "exist");
        }
        if (!registered.containsKey(newOwner)) {
            throw new NoSuchElementException("No user called " + newOwner + "exist");
        }
        Registered appointUser = registered.get(appoint);
        Registered newOwnerUser = registered.get(newOwner);
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
        if (!registered.containsKey(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + "exist");
        }
        if (!registered.containsKey(newManager)) {
            throw new NoSuchElementException("No user called " + newManager + "exist");
        }
        Registered appointUser = registered.get(appoint);
        Registered newManagerUser = registered.get(newManager);
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
        if (!registered.containsKey(newManager)) {
            throw new NoSuchElementException("No user called " + newManager + "exist");
        }
        if (!registered.containsKey(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + "exist");
        }
        Registered appointUser = registered.get(appoint);
        Registered newManagerUser = registered.get(newManager);
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
        if (!registered.containsKey(newOwner)) {
            throw new NoSuchElementException("No user called " + newOwner + "exist");
        }
        if (!registered.containsKey(appoint)) {
            throw new NoSuchElementException("No user called " + appoint + "exist");
        }
        Registered appointUser = registered.get(appoint);
        if (!appointUser.isOwner(storeName)) {
            throw new IllegalAccessException("User must be Owner");
        }
        Registered newOwnerUser = registered.get(newOwner);
        if (newOwnerUser.isOwner(storeName)) {
            throw new IllegalAccessException("User already Owner of this store");
        }
        newOwnerUser.removeWaitingAppoint_Owner(storeName);
        newOwnerUser.addOwnerRole(appoint, storeName);
    }

//    public void appointManager(String appoint, String newManager, String store_name_id,boolean watch,boolean editSupply,boolean editBuyPolicy,boolean editDiscountPolicy) throws IllegalAccessException, NoSuchElementException {
//        if(!registered.containsKey(appoint)){
//            throw new NoSuchElementException("No user called "+appoint+ "exist");
//        }
//        if(!registered.containsKey(newManager)){
//            throw new NoSuchElementException("No user called "+newManager+ "exist");
//        }
//        Registered appointUser=registered.get(appoint);
//        Registered newManagerUser=registered.get(newManager);
//        if(!appointUser.isOwner(store_name_id)){
//            throw new IllegalAccessException("User must be Owner");
//        }
//        if(!appointUser.getLogged()){
//            throw new IllegalAccessException("Appoint user is not logged");
//        }
//        if(newManagerUser.isManager(store_name_id)){
//            throw new IllegalAccessException("User already Manager of this store");
//        }
//        if(newManagerUser.isOwner(store_name_id)){
//            throw new IllegalAccessException("User cannot be owner of this store");
//        }
//        newManagerUser.addManagerRole(appoint,store_name_id);
//        newManagerUser.setPermissionsToManager(store_name_id,watch,editSupply,editBuyPolicy,editDiscountPolicy);
//
//    }

//    @Override
//    public void appointOwner(String appoint, String newOwner, String storeName) throws IllegalAccessException, NoSuchElementException {
//        if(!registered.containsKey(appoint)){
//            throw new NoSuchElementException("No user called "+appoint+ "exist");
//        }
//        if(!registered.containsKey(newOwner)){
//            throw new NoSuchElementException("No user called "+newOwner+ "exist");
//        }
//        Registered appointUser=registered.get(appoint);
//        Registered newOwnerUser=registered.get(newOwner);
//        if(!appointUser.isOwner(storeName)){
//            throw new IllegalAccessException("Appoint user must be Owner");
//        }
//        if(newOwnerUser.isOwner(storeName)){
//            throw new IllegalAccessException("User already Owner of this store");
//        }
//        newOwnerUser.addOwnerRole(appoint,storeName);
//    }

    @Override
    public void editPermissionForManager(String userId, String managerToEdit, String storeNameId, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) throws IllegalAccessException, NoSuchElementException {
        if (registered.containsKey(userId)) {
            throw new NoSuchElementException("No user called " + userId + "exist");
        }
        if (!registered.containsKey(managerToEdit)) {
            throw new NoSuchElementException("No user called " + managerToEdit + "exist");
        }
        Registered appointUser = registered.get(userId);
        Registered newManagerUser = registered.get(managerToEdit);
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
        User u = registered.get(username);
        if (u == null)
            throw new RuntimeException("No such registered user " + username);
        return u.getPass();
    }

    @Override
    public void removeVisitor(int id) {
        visitors.remove(id);
    }


    @Override
    public synchronized String visitorViewCart(int id) {
        if (!visitors.containsKey(id)) {
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
        Cart cart = visitors.get(id).getShopping_cart();
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
    public synchronized String registeredViewCart(String username) {
        if (!registered.containsKey(username)) {
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
        if (!registered.get(username).getLogged()) {
            logger.error("user is not logged");
            throw new RuntimeException("user is not logged");
        }
        Cart cart = registered.get(username).getShopping_cart();
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
                cartDetails.append("Product Id: ").append(product.getProduct_id()).append(", Name: ").append(product.getProduct_name()) // Optional: If you want to display product names
                        .append(", Quantity: ").append(quantity).append(", Price per unit: ").append(price).append(", Total Price: ").append(totalPrice).append("\n");
            }
            cartDetails.append("Total for Store name ").append(storeId).append(": ").append(totalStore).append("\n\n");
            totalAllStores += totalStore;
        }
        cartDetails.append("Overall Total for All Stores: ").append(totalAllStores).append("\n");
        return cartDetails.toString();

    }

    @Override
    public boolean isAdminRegistered() {
        boolean exists = false;
        for (Registered r : registered.values())
            if (r.isAdmin()) {
                exists = true;
                break;
            }
        return exists;
    }

}


