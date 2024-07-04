package com.example.trading_system.domain.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

public class Registered extends User {
    private String encrypted_pass;
    private LocalDate birthdate;
    private boolean isAdmin;
    @Getter
    @Setter
    private boolean isLogged = false;
    private List<Role> roles;
    private List<Notification> notifications;
    private HashMap<String, HashMap<String,List<Boolean>>> managerToApprove;
    private HashMap<String, String> ownerToApprove;

    public Registered(String userName, String encryption, LocalDate birthdate) {
        super(userName);
        this.encrypted_pass = encryption;
        this.birthdate = birthdate;
        this.isAdmin = false;
        this.isLogged = false;
        this.notifications = new LinkedList<>();
        this.roles = new ArrayList<>();
        this.managerToApprove = new HashMap<>();
        this.ownerToApprove = new HashMap<>();
    }

    public void openStore(String storeName) {
        addOwnerRole(username, storeName);
    }

    public void addOwnerRole(String appoint, String storeName) {
        Role owner = new Role(storeName, appoint);
        owner.setRoleState(new Owner(owner));
        getRoles().add(owner);
    }

    @Override
    public void removeOwnerRole(String storeName) {
        roles.remove(getRoleByStoreId(storeName));
    }

    @Override
    public void removeManagerRole(String storeName) {
        roles.remove(getRoleByStoreId(storeName));
    }


    @Override
    public String getPass() {
        return this.encrypted_pass;
    }

    @Override
    public void login() {
        this.isLogged = true;
    }

    @Override
    public void logout() {
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
    public String getNotificationsJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setTimeZone(TimeZone.getDefault());
        try {
            return objectMapper.writeValueAsString(notifications);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting list of Notifications to JSON", e);
        }
    }

    @Override
    public void clearPendingNotifications() {
        this.notifications.clear();
    }

    public HashMap<String, String> getOwnerToApprove() {
        return ownerToApprove;
    }

    public HashMap<String, HashMap<String, List<Boolean>>> getManagerToApprove() {
        return managerToApprove;
    }

    @Override
    public void receiveDelayedNotification(Notification notification) {
        notifications.add(notification);
    }

    public void addManagerRole(String appoint, String store_name_id) {
        Role manager = new Role(store_name_id, appoint);
        manager.setRoleState(new Manager());
        getRoles().add(manager);
    }

    public void setPermissionsToManager(String store_name_id, boolean watch, boolean editSupply, boolean editPurchasePolicy, boolean editDiscountPolicy) {
        Role manager = getRoleByStoreId(store_name_id);
        manager.getRoleState().setWatch(watch);
        manager.getRoleState().setEditSupply(editSupply);
        manager.getRoleState().setEditPurchasePolicy(editPurchasePolicy);
        manager.getRoleState().setEditDiscountPolicy(editDiscountPolicy);
    }

    public Role getRoleByStoreId(String store_name_id) {
        if (roles.isEmpty()) {
            throw new NoSuchElementException("User doesn't have roles");
        }
        for (Role role : roles) {
            if (role.getStoreId().equals(store_name_id)) return role;
        }
        return null;
    }

    public boolean isAdmin() {
        return this.isAdmin;
    }

    public void setAdmin(boolean value) {
        this.isAdmin = value;
    }

    public boolean isOwner(String store_name_id) {
        if (roles.isEmpty()) {
            return false;
        } else {
            Role role = getRoleByStoreId(store_name_id);
            if (role == null) return false;
            else return role.getRoleState().isOwner();
        }
    }

    public boolean isManager(String store_name_id) {
        if (roles.isEmpty()) {
            return false;
        } else {
            Role role = getRoleByStoreId(store_name_id);
            if (role == null) return false;
            else return role.getRoleState().isManager();
        }
    }

    public void addWaitingAppoint_Manager(String store_name_id,String appointee, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) {
        HashMap<String,List<Boolean>> permissions=new HashMap<>();
        permissions.put(appointee,Arrays.asList(watch, editSupply, editBuyPolicy, editDiscountPolicy));
        managerToApprove.put(store_name_id,permissions);
    }

    public void addWaitingAppoint_Owner(String storeName,String appointee) {
        ownerToApprove.put(storeName,appointee);
    }

    public List<Boolean> removeWaitingAppoint_Manager(String store_name_id, String appointee) {
        HashMap<String,List<Boolean>> removed=managerToApprove.remove(store_name_id);
        return removed.get(appointee);
    }

    public boolean removeWaitingAppoint_Owner(String storeName) {
        return ownerToApprove.remove(storeName)!=null;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    @Override
    public int getAge() {
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthdate, currentDate).getYears();
    }

    @Override
    public String getStoresIOwn(){
        List stores=new ArrayList();
        for (Role role:roles){
            if(role.getRoleState().isOwner()){
                stores.add(role.getStoreId());
            }
        }
        return stores.toString();
    }

    @Override
    public String getStoresIManage(){
        List stores=new ArrayList();
        for (Role role:roles){
            if(role.getRoleState().isManager()){
                stores.add(role.getStoreId());
            }
        }
        return stores.toString();
    }
}
