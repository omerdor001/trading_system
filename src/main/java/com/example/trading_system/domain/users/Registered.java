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

    @Getter
    @Column(nullable = false)
    private LocalDate birthdate;

    @Column
    private boolean isAdmin;

    @Column
    @Setter
    private boolean isLogged;

    @Getter
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "registered_username", referencedColumnName = "username")
    private List<Role> roles;

    @OneToMany(mappedBy = "receiverUsername",fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications;

    @Getter
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_manager_suggestions", referencedColumnName = "username")
    private List<ManagerSuggestion> managerSuggestions = new ArrayList<>();

    @Getter
    @ElementCollection
    @CollectionTable(name = "owner_suggestions", joinColumns = @JoinColumn(name = "registered_id"))
    @MapKeyColumn(name = "store_name")
    @Column(name = "owner_suggestion")
    private Map<String, String> ownerSuggestions = new HashMap<>();

    public Registered(String userName, String encryption, LocalDate birthdate) {
        super(userName);
        this.encrypted_pass = encryption;
        this.birthdate = birthdate;
        this.isAdmin = false;
        this.isLogged = false;
        this.notifications = new LinkedList<>();
        this.roles = new ArrayList<>();
        this.managerSuggestions = new ArrayList<>();
        this.ownerSuggestions = new HashMap<>();
    }

    public Registered() {
    }

    public void openStore(String storeName) {
        addOwnerRole(getUsername(), storeName);
    }

    public void addOwnerRole(String appoint, String storeName) {
        try {
            if (getRoleByStoreId(storeName) != null) removeManagerRole(storeName);
        } catch (Exception ignored) {
        } finally {
            Role owner = new Role(storeName, appoint);
            owner.setRoleState(new Owner(owner));
            getRoles().add(owner);
        }
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


    @Override
    public void receiveDelayedNotification(Notification notification) {
        notifications.add(notification);
    }

    public void addManagerRole(String appoint, String store_name_id) {
        Role manager = new Role(store_name_id, appoint);
        manager.setRoleState(new Manager());
        getRoles().add(manager);
    }

    public void setPermissionsToManager(String store_name_id, boolean watch, boolean editSupply, boolean editPurchasePolicy, boolean editDiscountPolicy, boolean acceptBids) {
        Role manager = getRoleByStoreId(store_name_id);
        manager.getRoleState().setWatch(watch);
        manager.getRoleState().setEditSupply(editSupply);
        manager.getRoleState().setEditPurchasePolicy(editPurchasePolicy);
        manager.getRoleState().setEditDiscountPolicy(editDiscountPolicy);
        manager.getRoleState().setAcceptBids(acceptBids);
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

    public void addWaitingAppoint_Manager(String store_name_id, String appointee, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy, boolean acceptBids) {
        ManagerSuggestion managerSuggestion = new ManagerSuggestion(store_name_id , appointee, new LinkedList<>(Arrays.asList(watch, editSupply, editBuyPolicy, editDiscountPolicy, acceptBids)));
        managerSuggestions.add(managerSuggestion);
    }

    public boolean isWatch(String storeName) {
        if (isOwner(storeName)) return true;
        else if (isManager(storeName)) {
            return getRoleByStoreId(storeName).isWatch();
        } else return false;
    }

    public boolean isEditSupply(String storeName) {
        if (isOwner(storeName)) return true;
        else if (isManager(storeName)) {
            return getRoleByStoreId(storeName).isEditSupply();
        } else return false;
    }

    public boolean isEditPurchasePolicy(String storeName) {
        if (isOwner(storeName)) return true;
        else if (isManager(storeName)) {
            return getRoleByStoreId(storeName).isEditPurchasePolicy();
        } else return false;
    }

    public boolean isEditDiscountPolicy(String storeName) {
        if (isOwner(storeName)) return true;
        else if (isManager(storeName)) {
            return getRoleByStoreId(storeName).isEditDiscountPolicy();
        } else return false;
    }

    @Override
    public boolean isAcceptBids(String storeName) {
        if (isOwner(storeName)) return true;
        else if (isManager(storeName)) {
            return getRoleByStoreId(storeName).isAcceptBids();
        } else return false;
    }


    public void addWaitingAppoint_Owner(String storeName, String appointee) {
        ownerSuggestions.put(storeName, appointee);
    }

    public List<Boolean> removeWaitingAppoint_Manager(String suggestionStore, String suggestionUser) {
        Iterator<ManagerSuggestion> iterator = managerSuggestions.iterator();
        System.out.println("Attempting to remove waiting appointment for user: " + suggestionUser + " Store: " + suggestionStore);
        while (iterator.hasNext()) {
            ManagerSuggestion suggestion = iterator.next();
            if (suggestion.getSuggestionStore().equals(suggestionStore) && suggestion.getSuggestionUser().equals(suggestionUser)) {
                System.out.println("Found and removing suggestion for user: " + suggestionUser + " Store: " + suggestionStore);
                iterator.remove();
                return suggestion.getSuggestionValues();
            }
        }
        System.out.println("No suggestion found for user: " + suggestionUser + " Store: " + suggestionStore);
        return null;
    }


    public boolean removeWaitingAppoint_Owner(String storeName) {
        return ownerSuggestions.remove(storeName) != null;
    }

    @Override
    public int getAge() {
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthdate, currentDate).getYears();
    }

    @Override
    public String getStoresIOwn() {
        List<String> stores = new ArrayList<>();
        for (Role role : roles) {
            if (role.getRoleState().isOwner()) {
                stores.add(role.getStoreId());
            }
        }
        return stores.toString();
    }

    @Override
    public String getStoresIManage() {
        List<String> stores = new ArrayList<>();
        for (Role role : roles) {
            if (role.getRoleState().isManager()) {
                stores.add(role.getStoreId());
            }
        }
        return stores.toString();
    }


    @Override
    public Set<String> cancelOwnerShip(String storeName) {
        Set<Registered> usersAppointedByMe = getRoleByStoreId(storeName).getUsersAppointedByMe();
        Set<String> influencedUsers = new HashSet<>();
        if (usersAppointedByMe.isEmpty()) {
            removeOwnerRole(storeName);
            return influencedUsers;
        }

        for (User registered : usersAppointedByMe) {
            if (registered.isOwner(storeName)) influencedUsers.addAll(registered.cancelOwnerShip(storeName));
            else registered.removeManagerRole(storeName);
            influencedUsers.add("r" + registered.getUsername());
        }
        removeOwnerRole(storeName);
        return influencedUsers;
    }

    @Override
    public void removeManagerSuggestions() {
        managerSuggestions.clear();
    }
}
