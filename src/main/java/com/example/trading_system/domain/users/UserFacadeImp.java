package com.example.trading_system.domain.users;

import com.example.trading_system.domain.stores.MarketFacadeImp;
import com.example.trading_system.domain.stores.Store;
import com.example.trading_system.domain.stores.StorePolicy;
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

    public HashMap<String, Registered> getRegistered() {
        return registered;
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
        if(registers.containsKey(username)){
            registers.remove(username);
        }
        else{
            throw new Exception("No such user with username- " + username);
        }
    }


    @Override
    public void registration(int id, String username, String encrypted_pass, LocalDate birthdate) throws Exception {
        registerChecks(id, username, encrypted_pass, birthdate);
        Registered newUser = new Registered(id,username,encrypted_pass, birthdate);
        registered.put(username,newUser);
        visitors.remove(id);
    }

    private void registerChecks(int id, String username, String encrypted_pass, LocalDate birthdate) throws Exception {
        if(username == null) throw new Exception("Username is null");
        if(username.isEmpty()) throw new Exception("Username is empty");
        if(encrypted_pass == null) throw new Exception("Encrypted password is null");
        if(encrypted_pass.isEmpty()) throw new Exception("Encrypted password is empty");
        if(birthdate == null) throw new Exception("Birthdate password is null");
        if(!visitors.containsKey(id)) throw new Exception("No visitor with id - " + id);
        if(registered.containsKey(username)) throw new Exception("username already exists - " + username);
    }

    @Override
    public void login(String username) {
        User u = registered.get(username);
        if (u == null)
            throw new RuntimeException("No such user " + username);
        u.login();
    }

    @Override
    public void addUser(User user) {

    }

    @Override
    public void removeUser(User user) {

    }

    @Override
    public boolean sendNotification(User sender, User receiver, String content){
        String notification = sender.sendNotification(receiver.getId(), content);
        receiver.receiveNotification(notification);
        return receiver.getLogged();
        //TODO return something to show the notification if receiver is logged in - maybe boolean if logged in
    }

    @Override
    public void addToCart(int id, int productId, String storeName, int quantity) {

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
