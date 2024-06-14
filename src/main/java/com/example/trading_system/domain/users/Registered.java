package com.example.trading_system.domain.users;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.*;

public class Registered extends User {
    private String userName;      //May be removed
    private String encrypted_pass;

    private String address;

    private LocalDate birthdate;
    private boolean isAdmin;
    @Getter
    @Setter
    private boolean isLogged = false;

    private List<Role> roles;
    private List<Notification> notifications;
    private HashMap<String, List<Boolean>> managerToApprove;
    private List<String> ownerToApprove;

    public Registered(String userName, String encryption, LocalDate birthdate) {
        super(userName);
        this.userName = userName;
        this.encrypted_pass = encryption;
        this.address = "No address";
        this.birthdate = birthdate;
        this.isAdmin = false;
        this.isLogged = false;
        this.notifications = new LinkedList<>();
        this.roles=new ArrayList<>();
        this.managerToApprove = new HashMap<>();
        this.ownerToApprove = new ArrayList<>();
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
         roles.remove(getRoleByStoreId(storeName)); }

    @Override
    public void removeManagerRole(String storeName) {
        roles.remove(getRoleByStoreId(storeName)); }


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
    public void receiveNotification(String notificationString) {
        if (!isLogged) {
            Notification notification = Notification.fromString(notificationString);
            this.notifications.add(notification);
        } else {
            //TODO show in UI
        }
    }

    public void addManagerRole(String appoint, String store_name_id) {
        Role manager = new Role(store_name_id, appoint);
        manager.setRoleState(new Manager());
        getRoles().add(manager);
    }


    public void setPermissionsToManager(String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) {
        Role manager = getRoleByStoreId(store_name_id);
        manager.getRoleState().setWatch(watch);
        manager.getRoleState().setEditSupply(editSupply);
        manager.getRoleState().setEditBuyPolicy(editBuyPolicy);
        manager.getRoleState().setEditDiscountPolicy(editDiscountPolicy);
    }

    public Role getRoleByStoreId(String store_name_id) {
        if (roles.isEmpty()) {
            throw new NoSuchElementException("User doesn't have roles");
        }
        for (Role role : roles) {
            if (role.getStoreId().equals(store_name_id)) return role;
        }
        throw new NoSuchElementException("User doesn't have permission to this store");
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
        } else return getRoleByStoreId(store_name_id).getRoleState().isOwner();
    }

    public boolean isManager(String store_name_id) {
        if (roles.isEmpty()) {
            return false;
        } else return getRoleByStoreId(store_name_id).getRoleState().isManager();
    }

    public void addWaitingAppoint_Manager(String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) {
        managerToApprove.put(store_name_id, Arrays.asList(watch, editSupply, editBuyPolicy, editDiscountPolicy));
    }

    public void addWaitingAppoint_Owner(String storeName) {
        ownerToApprove.add(storeName);
    }

    public List<Boolean> removeWaitingAppoint_Manager(String store_name_id) {
        return managerToApprove.remove(store_name_id);
    }

    public void removeWaitingAppoint_Owner(String storeName) {
        ownerToApprove.remove(storeName);
    }

    public List<Role> getRoles() {
        return roles;
    }

    public String getAddress() {
        return address;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

}
