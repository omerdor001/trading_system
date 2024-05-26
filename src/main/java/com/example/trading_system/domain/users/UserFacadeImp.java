package com.example.trading_system.domain.users;

import com.example.trading_system.domain.stores.MarketFacadeImp;
import com.example.trading_system.domain.stores.Store;
import com.example.trading_system.domain.stores.StorePolicy;
import com.example.trading_system.service.UserServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.NoSuchElementException;

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
    public void registerdRemoveFromCart(String username, int productId, String storeName, int quantity){
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
        Store store = new Store(storeName, description, policy, username);
        marketFacade.addStore(store);
        registered.get(username).openStore();

    }

    public void suggestOwner(String appoint, String newOwner, String storeName) throws IllegalAccessException, NoSuchElementException{
        if(!registered.containsKey(appoint)){
            throw new NoSuchElementException("No user called "+appoint+ "exist");
        }
        if(!registered.containsKey(newOwner)){
            throw new NoSuchElementException("No user called "+newOwner+ "exist");
        }
        Registered appointUser=registered.get(appoint);
        Registered newOwnerUser=registered.get(newOwner);
        if(!appointUser.isOwner(storeName)){
            throw new IllegalAccessException("Appoint user must be Owner");
        }
        if(!appointUser.getLogged()){
            throw new IllegalAccessException("Appoint user is not logged");
        }
        if(newOwnerUser.isOwner(storeName)){
            throw new IllegalAccessException("User already Owner of this store");
        }
        newOwnerUser.addWaitingAppoint_Owner(storeName);

    }

    public void suggestManage(String appoint, String newManager, String store_name_id,boolean watch,boolean editSupply,boolean editBuyPolicy,boolean editDiscountPolicy) throws IllegalAccessException, NoSuchElementException {
        if(!registered.containsKey(appoint)){
            throw new NoSuchElementException("No user called "+appoint+ "exist");
        }
        if(!registered.containsKey(newManager)){
            throw new NoSuchElementException("No user called "+newManager+ "exist");
        }
        Registered appointUser=registered.get(appoint);
        Registered newManagerUser=registered.get(newManager);
        if(!appointUser.isOwner(store_name_id)){
            throw new IllegalAccessException("User must be Owner");
        }
        if(!appointUser.getLogged()){
            throw new IllegalAccessException("Appoint user is not logged");
        }
        if(newManagerUser.isManager(store_name_id)){
            throw new IllegalAccessException("User already Manager of this store");
        }
        if(newManagerUser.isOwner(store_name_id)){
            throw new IllegalAccessException("User cannot be owner of this store");
        }
        newManagerUser.addWaitingAppoint_Manager(store_name_id,watch,editSupply,editBuyPolicy,editDiscountPolicy);
    }

    public void approveManage(String newManager,String store_name_id) throws NoSuchElementException{
        if(!registered.containsKey(newManager)){
            throw new NoSuchElementException("No user called "+newManager+ "exist");
        }
        Registered newManagerUser=registered.get(newManager);
        newManagerUser.removeWaitingAppoint_Manager(store_name_id);
    }


    public void approveOwner(String newOwner,String storeName) throws NoSuchElementException{
        if(!registered.containsKey(newOwner)){
            throw new NoSuchElementException("No user called "+newOwner+ "exist");
        }
        Registered newOwnerUser=registered.get(newOwner);
        newOwnerUser.removeWaitingAppoint_Owner(storeName);
    }

    public void appointManager(String appoint, String newManager, String store_name_id,boolean watch,boolean editSupply,boolean editBuyPolicy,boolean editDiscountPolicy) throws IllegalAccessException, NoSuchElementException {
        if(!registered.containsKey(appoint)){
            throw new NoSuchElementException("No user called "+appoint+ "exist");
        }
        if(!registered.containsKey(newManager)){
            throw new NoSuchElementException("No user called "+newManager+ "exist");
        }
        Registered appointUser=registered.get(appoint);
        Registered newManagerUser=registered.get(newManager);
        if(!appointUser.isOwner(store_name_id)){
            throw new IllegalAccessException("User must be Owner");
        }
        if(!appointUser.getLogged()){
            throw new IllegalAccessException("Appoint user is not logged");
        }
        if(newManagerUser.isManager(store_name_id)){
            throw new IllegalAccessException("User already Manager of this store");
        }
        if(newManagerUser.isOwner(store_name_id)){
            throw new IllegalAccessException("User cannot be owner of this store");
        }
        newManagerUser.addManagerRole(appoint,store_name_id);
        newManagerUser.setPermissionsToManager(store_name_id,watch,editSupply,editBuyPolicy,editDiscountPolicy);

    }

    @Override
    public void appointOwner(String appoint, String newOwner, String storeName) throws IllegalAccessException, NoSuchElementException {
        if(!registered.containsKey(appoint)){
            throw new NoSuchElementException("No user called "+appoint+ "exist");
        }
        if(!registered.containsKey(newOwner)){
            throw new NoSuchElementException("No user called "+newOwner+ "exist");
        }
        Registered appointUser=registered.get(appoint);
        Registered newOwnerUser=registered.get(newOwner);
        if(!appointUser.isOwner(storeName)){
            throw new IllegalAccessException("Appoint user must be Owner");
        }
        if(newOwnerUser.isOwner(storeName)){
            throw new IllegalAccessException("User already Owner of this store");
        }
        newOwnerUser.addOwnerRole(appoint,storeName);
    }

    @Override
    public void editPermissionForManager(String userId, String managerToEdit, String storeNameId, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) throws IllegalAccessException, NoSuchElementException {
        if(registered.containsKey(userId)){
            throw new NoSuchElementException("No user called "+userId+ "exist");
        }
        if(!registered.containsKey(managerToEdit)){
            throw new NoSuchElementException("No user called "+managerToEdit+ "exist");
        }
        Registered appointUser=registered.get(userId);
        Registered newManagerUser=registered.get(managerToEdit);
        if(appointUser.isOwner(storeNameId)){
            throw new IllegalAccessException("User cannot be owner of this store");
        }
        if(newManagerUser.isManager(storeNameId)){
            throw new IllegalAccessException("User already Manager of this store");
        }
        if(newManagerUser.getRoleByStoreId(storeNameId).getAppointedById().equals(userId)){
            throw new IllegalAccessException("Owner cant edit permissions to manager that he/she didn't appointed");
        }
        newManagerUser.setPermissionsToManager(storeNameId,watch,editSupply,editBuyPolicy,editDiscountPolicy);
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
