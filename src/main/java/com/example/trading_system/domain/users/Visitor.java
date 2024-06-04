package com.example.trading_system.domain.users;

import java.util.List;

public class Visitor extends User {
    public Visitor(String username) {
        super(username);
    }

    @Override
    public boolean getLogged() {
        return false;
    }

    @Override
    public List<Notification> getNotifications() {
        throw new UnsupportedOperationException("Visitors cannot receive notifications");
    }

    @Override
    public void receiveNotification(String notification) {
        throw new UnsupportedOperationException("Visitors cannot receive notifications");
    }

    @Override
    public void login() {
        throw new RuntimeException("Only registered users can login");
    }

    @Override
    public void logout() {
        throw new RuntimeException("Only registered users can logout");
    }

    @Override
    public void openStore(String storeName) {}

    @Override
    public boolean isOwner(String store_name_id) {
        return false;
    }

    @Override
    public void addWaitingAppoint_Owner(String storeName) {}

    @Override
    public boolean isManager(String store_name_id) {
        return false;
    }

    @Override
    public void addWaitingAppoint_Manager(String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) {}

    @Override
    public void removeWaitingAppoint_Owner(String storeName) {}

    @Override
    public List<Boolean> removeWaitingAppoint_Manager(String store_name_id) {
        return List.of();
    }

    @Override
    public void addManagerRole(String appoint, String store_name_id) {}

    @Override
    public void setPermissionsToManager(String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) {}

    @Override
    public void addOwnerRole(String appoint, String storeName) {}

    @Override
    public Role getRoleByStoreId(String store_name_id) {
        return null;
    }

    @Override
    public boolean isAdmin() {
        return false;
    }


}
