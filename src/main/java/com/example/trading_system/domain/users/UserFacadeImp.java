package com.example.trading_system.domain.users;

import com.example.trading_system.domain.stores.MarketFacadeImp;
import com.example.trading_system.domain.stores.Store;
import com.example.trading_system.domain.stores.StorePolicy;
import com.example.trading_system.service.Security;
import com.example.trading_system.service.UserServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashMap;

public class UserFacadeImp implements UserFacade{
    private HashMap<Integer, Visitor> visitors;
    private HashMap<String, Registered> registered;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);
    MarketFacadeImp marketFacade = MarketFacadeImp.getInstance();

    public UserFacadeImp() {
        this.registered = new HashMap<>();
        this.visitors = new HashMap<>();
    }

    public HashMap<Integer, Visitor> getVisitors() {
        return visitors;
    }

    @Override
    public HashMap<String, Registered> getRegistered() {
        return registered;
    }


    @Override
    public void createVisitor(int id) {
        Visitor visitor=new Visitor(id);
        visitors.put(id,visitor);
    }
    @Override
    public void exit() {
    }


    @Override
    public void enter(int id) {
        Visitor visitor=new Visitor(id);
        visitors.put(id,visitor);
    }

    @Override
    public void exit(int id) throws Exception {
        if(visitors.containsKey(id)){
            visitors.remove(id);
        }
        else{
            throw new Exception("No such visitor with id- " + id);
        }
    }

    @Override
    public void exit(String username) throws Exception {
        if(registered.containsKey(username)){
            registered.remove(username);
        }
        else{
            throw new Exception("No such user with username- " + username);
        }
    }


    @Override
    public void register(int id, String username, String password, LocalDate birthdate) throws Exception {
        registerChecks(id, username, password, birthdate);
        String encrypted_pass = Security.encrypt(password);
        Registered newUser = new Registered(id,username,encrypted_pass, birthdate);
        registered.put(username,newUser);
    }

    private void registerChecks(int id, String username, String password, LocalDate birthdate) throws Exception {
        if(username == null) throw new Exception("Username is null");
        if(username.isEmpty()) throw new Exception("Username is empty");
        if(password == null) throw new Exception("Encrypted password is null");
        if(password.isEmpty()) throw new Exception("Encrypted password is empty");
        if(birthdate == null) throw new Exception("Birthdate password is null");
        if(!visitors.containsKey(id)) throw new Exception("No visitor with id: " + id);
        if(registered.containsKey(username)) throw new Exception("username already exists - " + username);
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

    @Override
    public boolean sendNotification(User sender, User receiver, String content){
        String notification = sender.sendNotification(receiver.getId(), content);
        receiver.receiveNotification(notification);
        return receiver.getLogged();
        //TODO return something to show the notification if receiver is logged in - maybe boolean if logged in
    }

    @Override
    public void visitorAddToCart(int id, int productId, String storeName, int quantity) {
        int quntityInStore = marketFacade.getStores().get(storeName).getProducts().get(productId).getProduct_quantity();
        int quantityInShoppingBag = visitors.get(id).getShopping_cart().getShoppingBags().get(storeName).getProducts_list().get(productId);
        if(quantity+quantityInShoppingBag > quntityInStore)
        {
            logger.error("Product quantity is too low");
            throw new RuntimeException("Product quantity is too low");
        }
        if(storeName == null){
            logger.error("Store name is null");
            throw new RuntimeException("Store name is null");
        }
        if(marketFacade.getStores().containsKey(storeName)){
            logger.error("Store with name " + storeName + " already exists");
            throw new RuntimeException("Store with name " + storeName + " already exists");
        }
        if(visitors.containsKey(id)){
            visitors.get(id).getShopping_cart().addProductToCart(productId,quantity,storeName);
        }
    }
    @Override
    public void visitorRemoveFromCart(int id, int productId, String storeName, int quantity) {
        if(storeName == null){
            logger.error("Store name is null");
            throw new RuntimeException("Store name is null");
        }
        if(marketFacade.getStores().containsKey(storeName)){
            logger.error("Store with name " + storeName + " already exists");
            throw new RuntimeException("Store with name " + storeName + " already exists");
        }
        if(visitors.containsKey(id)){
            visitors.get(id).getShopping_cart().removeProductFromCart(productId,quantity,storeName);
        }
    }
    @Override
    public void registerdAddToCart(String username, int productId, String storeName, int quantity) {

        if(storeName == null){
            logger.error("Store name is null");
            throw new RuntimeException("Store name is null");
        }
        if(marketFacade.getStores().containsKey(storeName)){
            logger.error("Store with name " + storeName + " already exists");
            throw new RuntimeException("Store with name " + storeName + " already exists");
        }
        if(registered.get(username) == null){
            logger.error("User with name " + username + " does not exist");
            throw new RuntimeException("User with name " + username + " does not exist");
        }
        if(!registered.get(username).getLogged()){
            logger.error("user is not logged");
            throw new RuntimeException("user is not logged");
        }
        int quntityInStore = marketFacade.getStores().get(storeName).getProducts().get(productId).getProduct_quantity();
        int quantityInShoppingBag = registered.get(username).getShopping_cart().getShoppingBags().get(storeName).getProducts_list().get(productId);
        if(quantity+quantityInShoppingBag > quntityInStore)
        {
            logger.error("Product quantity is too low");
            throw new RuntimeException("Product quantity is too low");
        }
        if(registered.containsKey(username)){
            registered.get(username).getShopping_cart().addProductToCart(productId,quantity,storeName);
        }
    }
    @Override
    public void registerdRemoveFromCart(String username, int productId, String storeName, int quantity) throws Exception {
        if(storeName == null){
            logger.error("Store name is null");
            throw new RuntimeException("Store name is null");
        }
        if(marketFacade.getStores().containsKey(storeName)){
            logger.error("Store with name " + storeName + " already exists");
            throw new RuntimeException("Store with name " + storeName + " already exists");
        }
        if(registered.get(username) == null){
            logger.error("User with name " + username + " does not exist");
            throw new RuntimeException("User with name " + username + " does not exist");
        }
        if(!registered.get(username).getLogged()){
            logger.error("user is not logged");
            throw new RuntimeException("user is not logged");
        }
        if(registered.containsKey(username)){
            registered.get(username).getShopping_cart().removeProductFromCart(productId, quantity, storeName);
        }
    }

    @Override
    public void openStore(String username, String storeName, String description, StorePolicy policy) {
        if(storeName == null){
            logger.error("Store name is null");
            throw new RuntimeException("Store name is null");
        }
        if(marketFacade.getStores().containsKey(storeName)){
            logger.error("Store with name " + storeName + " already exists");
            throw new RuntimeException("Store with name " + storeName + " already exists");
        }
        if(!registered.containsKey(username)){
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
        Store store = new Store(storeName, description, policy);
        marketFacade.addStore(store);
        registered.get(username).openStore();

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
}
