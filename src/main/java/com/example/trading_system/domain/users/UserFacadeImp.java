package com.example.trading_system.domain.users;

import com.example.trading_system.domain.stores.MarketFacadeImp;
import com.example.trading_system.domain.stores.Product;
import com.example.trading_system.domain.stores.Store;
import com.example.trading_system.domain.stores.StorePolicy;
import com.example.trading_system.service.UserServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashMap;

import java.util.Map;

public class UserFacadeImp implements UserFacade{
    private HashMap<Integer, Visitor> visitors;
    private HashMap<String, Registered> registers;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);
    MarketFacadeImp marketFacade = MarketFacadeImp.getInstance();

    public UserFacadeImp() {
        this.registers = new HashMap<>();
        this.visitors = new HashMap<>();
    }

    public HashMap<Integer, Visitor> getVisitors() {
        return visitors;
    }

    public HashMap<String, Registered> getRegisters() {
        return registers;
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
    public void registration(int id, String username, String encryption, LocalDate birthdate) throws Exception {
        registerChecks(id, username, encryption, birthdate);
        Cart shopping_cart = new Cart();
        Registered newUser = new Registered(id,username,encryption, birthdate);
        registers.put(username,newUser);
        visitors.remove(id);
    }

    private void registerChecks(int id, String username, String encryption, LocalDate birthdate) throws Exception {
        if(username == null) throw new Exception("Username is null");
        if(username.isEmpty()) throw new Exception("Username is empty");
        if(encryption == null) throw new Exception("Encrypted password is null");
        if(encryption.isEmpty()) throw new Exception("Encrypted password is empty");
        if(birthdate == null) throw new Exception("Birthdate password is null");
        if(!visitors.containsKey(id)) throw new Exception("No visitor with id - " + id);
        if(registers.containsKey(username)) throw new Exception("username already exists - " + username);
    }

    @Override
    public void login() {

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
        if(!registers.containsKey(username)){
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
        Store store = new Store(storeName, description, policy);
        marketFacade.addStore(store);
        registers.get(username).openStore();

    }

    @Override
    public String visitorViewCart(int id) {
        if(!visitors.containsKey(id)){
            logger.error("Visitor with id - " + id + " does not exist");
            throw new RuntimeException("Visitor with id - " + id + " does not exist");
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
    public String registerdViewCart(String username) {
        if (!registers.containsKey(username)) {
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
        if (!registers.get(username).getLogged()) {
            logger.error("user is not logged");
            throw new RuntimeException("user is not logged");
        }
        Cart cart = registers.get(username).getShopping_cart();
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
