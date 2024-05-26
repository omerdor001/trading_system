package com.example.trading_system.domain.users;

import lombok.Getter;

import java.time.LocalDate;
import java.util.*;

public class Registered extends User {
    private String userName;
    private String encrypted_pass;
    private String address;
    private LocalDate birthdate;
    private boolean isAdmin;
    private boolean isLogged = false;
    @Getter
    private List<Role> roles;
    private List<Notification> notifications;
    private HashMap<String,List<Boolean>> managerToApprove;
    private List<String> ownerToApprove;

    public Registered(int id, String userName, String encrypted_pass, String address, LocalDate birthdate) {
        super(id);
        this.userName = userName; // Can be changed to email
        this.encrypted_pass = encrypted_pass;

        this.address = address;
        this.birthdate= birthdate;
        this.isAdmin = false;
        this.isLogged = false;
        this.notifications = new LinkedList<>();
        this.roles=new ArrayList<>();
        this.managerToApprove=new HashMap<>();
        this.ownerToApprove=new ArrayList<>();
    }

    public Registered(int id, String userName, String encryption, LocalDate birthdate) {
        super(id);
        this.userName = userName; // Can be changed to email
        this.encrypted_pass = encryption;
        this.address = "No address";
        this.birthdate= birthdate;
        this.isAdmin = false;
        this.isLogged = false;
        this.notifications = new LinkedList<>();
    }

    public void performBuying(Cart shopping_cart) {}
    public boolean approveAppointment(int userId) { return false; }
    public void searchProduct(int productId) {}
    public void seeHistoryPurchase() {}
    public void seeHistoryPurchase(int storeId, int userId, int productId) {}
    public void openMarket() {}
    public void closeMarket() {}

    public void openStore() {
        this.isAdmin=true;
        //SET THE ROLE TO OWNER OF STORE

    }
    public String getUserName() {
        return userName;
    }
    @Override
    public String getPass(){
        return this.encrypted_pass;
    }

    @Override
    public void login(){
        this.isLogged = true;
    }

    @Override
    public void logout(){
        this.isLogged = false;
    }

    @Override
    public boolean getLogged() {
        return this.isLogged;
    }

    @Override
    public List<Notification> getNotifications() {
        return notifications;
    }

    @Override
    public void receiveNotification(String notificationString) {
        if(!isLogged) {
            Notification notification = Notification.fromString(notificationString);
            this.notifications.add(notification);
        }
        else{
            //TODO show in UI
        }
    }

    public void addManagerRole(String appoint, String store_name_id){
        Role manager=new Role(store_name_id,appoint);
        manager.setRoleState(new Manager());
        getRoles().add(manager);
    }

    public void addOwnerRole(String appoint, String storeName){
        Role owner=new Role(storeName,appoint);
        owner.setRoleState(new Owner(owner));
        getRoles().add(owner);
    }

    public void setPermissionsToManager(String store_name_id,boolean watch,boolean editSupply,boolean editBuyPolicy,boolean editDiscountPolicy){
        Role manager=getRoleByStoreId(store_name_id);
        manager.getRoleState().setWatch(watch);
        manager.getRoleState().setEditSupply(editSupply);
        manager.getRoleState().setEditBuyPolicy(editBuyPolicy);
        manager.getRoleState().setEditDiscountPolicy(editDiscountPolicy);
    }

    public Role getRoleByStoreId(String store_name_id){
        for (Role role:roles){
            if (role.getStoreId().equals(store_name_id))
                return role;
        }
        throw new NoSuchElementException("User doesn't have permission to this store");
    }

    public boolean isAdmin(){
        return this.isAdmin;
    }
    public boolean isOwner(String store_name_id){
        if(getRoleByStoreId(store_name_id).getRoleState().isOwner()){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isManager(String store_name_id){
        if(getRoleByStoreId(store_name_id).getRoleState().isManager()){
            return true;
        }
        else{
            return false;
        }
    }

    public void setAdmin(boolean value){
        this.isAdmin = value;
    }
    public void addWaitingAppoint_Manager(String store_name_id,boolean watch,boolean editSupply,boolean editBuyPolicy,boolean editDiscountPolicy){
        managerToApprove.put(store_name_id,Arrays.asList(watch,editSupply,editBuyPolicy,editDiscountPolicy));
    }
    public void addWaitingAppoint_Owner(String storeName){
        ownerToApprove.add(storeName);
    }

    public List<Boolean> removeWaitingAppoint_Manager(String store_name_id){
        return managerToApprove.remove(store_name_id);
    }

    public void removeWaitingAppoint_Owner(String storeName){
        ownerToApprove.remove(storeName);
    }


    public String getAddress() {
        return address;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }
}
