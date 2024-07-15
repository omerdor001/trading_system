package com.example.trading_system.domain.users;

import com.example.trading_system.domain.Message;
import com.example.trading_system.domain.stores.StoreRepository;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@MappedSuperclass
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class User {
    private static final Logger logger = LoggerFactory.getLogger(User.class);

    @Getter
    @Id
    @Column(nullable = false, unique = true)
    private String username;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_id", referencedColumnName = "id")
    private Cart cart;

    @Column
    private boolean suspended;

    @Column
    private LocalDateTime suspendedStart;

    @Column
    private LocalDateTime suspendedEnd;

    @Column
    private String address;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages;

    @Column(nullable = false)
    private boolean isTimerCancelled;

    public User(String username) {
        this.username = username;
        this.cart = new Cart();
        this.suspended = false;
        this.suspendedStart = null;
        this.suspendedEnd = null;
        this.address = "";
        this.messages = new LinkedList<>();
        this.isTimerCancelled = true;
    }

    public User() {
    }

    public void setId(String username) {
        this.username = username;
    }

    public String getPass() {
        throw new RuntimeException("Only registered users have a password");
    }

    public boolean isSuspended() {
        return suspended;
    }

    public LocalDateTime getSuspendedStart() {
        return suspendedStart;
    }

    public LocalDateTime getSuspendedEnd() {
        return suspendedEnd;
    }

    public void setSuspendedEnd(LocalDateTime suspendedEnd) {
        this.suspendedEnd = suspendedEnd;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void suspend(LocalDateTime suspendedUntil) {
        suspended = true;
        suspendedStart = LocalDateTime.now();
        setSuspendedEnd(suspendedUntil);
    }

    public void finishSuspension() {
        suspendedStart = null;
        suspendedEnd = null;
        suspended = false;
    }

    public abstract void login();

    public abstract void logout();

    public abstract void openStore(String storeName);

    public abstract boolean isOwner(String store_name_id);

    public abstract boolean isManager(String store_name_id);

    public abstract void addWaitingAppoint_Manager(String store_name_id,String appointee, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy, boolean acceptBids, boolean createLottery);

    public abstract boolean isWatch(String storeName);

    public abstract boolean isEditSupply(String storeName);

    public abstract boolean isEditPurchasePolicy(String storeName);

    public abstract boolean isEditDiscountPolicy(String storeName);

    public abstract boolean isAcceptBids(String storeName);

    public abstract boolean isCreateLottery(String storeName);

    public abstract boolean removeWaitingAppoint_Owner(String storeName);

    public abstract List<Boolean> removeWaitingAppoint_Manager(String store_name_id) throws IllegalAccessException;

    public abstract void addManagerRole(String appoint, String store_name_id);

    public abstract void setPermissionsToManager(String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy, boolean acceptBids, boolean createLottery);

    public abstract void addOwnerRole(String appoint, String storeName);

    public void removeOwnerRole(String storeName) {
        throw new RuntimeException("Only registered users can be owners.");
    }

    public void removeManagerRole(String storeName) {
        throw new RuntimeException("Only registered users can be managers.");
    }

    public abstract Role getRoleByStoreId(String store_name_id);

    public abstract boolean isAdmin();

    public abstract void setAdmin(boolean value);

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public LinkedList<Message> getMessages(){
        return (LinkedList<Message>) this.messages;
    }

    public String getMessagesJSON(){
        return Message.toJsonList(this.messages);
    }

    public abstract boolean getLogged();

    public abstract Map<String, String> getOwnerSuggestions();

    public abstract List<ManagerSuggestion> getManagerSuggestions();

    public abstract List<Notification> getNotifications();

    public abstract String getNotificationsJson();

    public abstract void clearPendingNotifications();

    public abstract void receiveDelayedNotification(Notification notification);

    public abstract LocalDate getBirthdate();

    public abstract int getAge();

    public abstract String getStoresIOwn();

    public abstract String getStoresIManage();

    public abstract void addWaitingAppoint_Owner(String storeName,String appointee);

    public void addProductToCart(int productId, int quantity, String storeName, double price, int category) {
        this.cart.addProductToCart(productId, quantity, storeName, price, category);
    }

    public void removeProductFromCart(int productId, int quantity, String storeName) {
        this.cart.removeProductFromCart(productId, quantity, storeName);
    }

    public String getShoppingCart_ToString() {
        return cart.toString();
    }

    public int checkProductQuantity(int productId, String storeName) {
        return cart.checkProductQuantity(productId, storeName);
    }

    public void removeReservedProducts(StoreRepository storeRepository) {
        cart.removeReservedProducts(storeRepository);
    }

    public void releaseReservedProducts(StoreRepository storeRepository) {
        cart.releaseReservedProducts(storeRepository);
    }

    public void checkAvailabilityAndConditions(StoreRepository storeRepository) {
        if (cart == null || cart.getShoppingBags().isEmpty()) {
            logger.error("Cart is empty or null");
            throw new RuntimeException("Cart is empty or null");
        }
        if (!getLogged()) {
            logger.error("User is not logged in");
            throw new RuntimeException("User is not logged in");
        }
        cart.checkAvailabilityAndConditions(storeRepository);
    }

    public void addPurchase(StoreRepository storeRepository,String username) {
        cart.addPurchase(storeRepository,username);
    }

    public void receiveMessage(String senderId, String senderUsername, String content){
        this.messages.add(new Message(senderId, senderUsername, content));
    }
    public boolean isTimerCancelled() {
        return isTimerCancelled;
    }

    public void setTimerCancelled(boolean timerCancelled) {
        isTimerCancelled = timerCancelled;
    }
}
