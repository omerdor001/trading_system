package com.example.trading_system.domain.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Entity
public class Registered extends User {
    @Column(nullable = false)
    private String encrypted_pass;

    @Column(nullable = false)
    private LocalDate birthdate;

    @Column(nullable = false)
    private boolean isAdmin;

    @Column(nullable = false)
    @Getter
    @Setter
    private boolean isLogged;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "registered_username", referencedColumnName = "username")
    private List<Role> roles;

    @OneToMany(mappedBy = "receiverUsername", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications;

    @ElementCollection
    @CollectionTable(name = "manager_suggestions", joinColumns = @JoinColumn(name = "registered_id"))
    @MapKeyColumn(name = "store_name")
    @Column(name = "suggestion_map")
    private HashMap<String, HashMap<String,List<Boolean>>> managerSuggestions;

    @ElementCollection
    @CollectionTable(name = "owner_suggestions", joinColumns = @JoinColumn(name = "registered_id"))
    @MapKeyColumn(name = "store_name")
    @Column(name = "owner_suggestion")
    private HashMap<String, String> ownerSuggestions;

    public Registered(String userName, String encryption, LocalDate birthdate) {
        super(userName);
        this.encrypted_pass = encryption;
        this.birthdate = birthdate;
        this.isAdmin = false;
        this.isLogged = false;
        this.notifications = new LinkedList<>();
        this.roles = new ArrayList<>();
        this.managerSuggestions = new HashMap<>();
        this.ownerSuggestions = new HashMap<>();
    }

    public Registered() {

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

    public HashMap<String, String> getOwnerSuggestions() {
        return ownerSuggestions;
    }

    public HashMap<String, HashMap<String, List<Boolean>>> getManagerSuggestions() {
        return managerSuggestions;
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

    public void setPermissionsToManager(String store_name_id, boolean watch, boolean editSupply, boolean editPurchasePolicy, boolean editDiscountPolicy, boolean acceptBids, boolean createLottery) {
        Role manager = getRoleByStoreId(store_name_id);
        manager.getRoleState().setWatch(watch);
        manager.getRoleState().setEditSupply(editSupply);
        manager.getRoleState().setEditPurchasePolicy(editPurchasePolicy);
        manager.getRoleState().setEditDiscountPolicy(editDiscountPolicy);
        manager.getRoleState().setAcceptBids(acceptBids);
        manager.getRoleState().setCreateLottery(createLottery);
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

    public void addWaitingAppoint_Manager(String store_name_id,String appointee, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy, boolean acceptBids, boolean createLottery) {
        HashMap<String, List<Boolean>> permissions = new HashMap<>();
        permissions.put(appointee, Arrays.asList(watch, editSupply, editBuyPolicy, editDiscountPolicy, acceptBids, createLottery));
        managerSuggestions.put(store_name_id, permissions);
    }

    public boolean isWatch(String storeName){
        if (isOwner(storeName))
            return true;
        else if(isManager(storeName)){
            return getRoleByStoreId(storeName).isWatch();
        }
        else return false;
    }

    public boolean isEditSupply(String storeName){
        if (isOwner(storeName))
            return true;
        else if(isManager(storeName)){
            return getRoleByStoreId(storeName).isEditSupply();
        }
        else return false;
    }

    public boolean isEditPurchasePolicy(String storeName){
        if (isOwner(storeName))
            return true;
        else if(isManager(storeName)){
            return getRoleByStoreId(storeName).isEditPurchasePolicy();
        }
        else return false;
    }

    public boolean isEditDiscountPolicy(String storeName){
        if (isOwner(storeName))
            return true;
        else if(isManager(storeName)){
            return getRoleByStoreId(storeName).isEditDiscountPolicy();
        }
        else return false;
    }


    public void addWaitingAppoint_Owner(String storeName,String appointee) {
        ownerSuggestions.put(storeName,appointee);
    }

    public List<Boolean> removeWaitingAppoint_Manager(String store_name_id, String appointee) throws IllegalAccessException {
        HashMap<String,List<Boolean>> removed= managerSuggestions.remove(store_name_id);
        if (removed == null) throw new IllegalAccessException("No one suggest this user to be a manager");
        return removed.get(appointee);
    }

    public boolean removeWaitingAppoint_Owner(String storeName) {
        return ownerSuggestions.remove(storeName)!=null;
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
