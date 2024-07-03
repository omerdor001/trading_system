package com.example.trading_system.domain.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Entity
@Table(name = "registered_users")
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
    private boolean isLogged = false;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "registered_id")
    private List<Role> roles;

    @OneToMany(mappedBy = "receiverUsername", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications;

    @ElementCollection
    @CollectionTable(name = "manager_suggestions", joinColumns = @JoinColumn(name = "username"))
    @MapKeyColumn(name = "store_name")
    @Column(name = "permission_flags")
    private HashMap<String, List<Boolean>> managerSuggestions;

    @ElementCollection
    @CollectionTable(name = "owner_suggestions", joinColumns = @JoinColumn(name = "username"))
    @Column(name = "store_name")
    private List<String> ownerSuggestions;

    public Registered(String userName, String encryption, LocalDate birthdate) {
        super(userName);
        this.encrypted_pass = encryption;
        this.birthdate = birthdate;
        this.isAdmin = false;
        this.isLogged = false;
        this.notifications = new LinkedList<>();
        this.roles = new ArrayList<>();
        this.managerSuggestions = new HashMap<>();
        this.ownerSuggestions = new ArrayList<>();
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

    public List<String> getOwnerSuggestions() {
        return ownerSuggestions;
    }

    public HashMap<String, List<Boolean>> getManagerSuggestions() {
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

    public void addWaitingAppoint_Manager(String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) {
        managerSuggestions.put(store_name_id, Arrays.asList(watch, editSupply, editBuyPolicy, editDiscountPolicy));
    }

    public void addWaitingAppoint_Owner(String storeName) {
        ownerSuggestions.add(storeName);
    }

    public List<Boolean> removeWaitingAppoint_Manager(String store_name_id) {
        return managerSuggestions.remove(store_name_id);
    }

    public boolean removeWaitingAppoint_Owner(String storeName) {
        return ownerSuggestions.remove(storeName);
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
}
