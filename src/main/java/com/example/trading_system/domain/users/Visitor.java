package com.example.trading_system.domain.users;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public class Visitor extends User {
    public Visitor(String username) {
        super(username);
    }

    @Override
    public boolean getLogged() {
        return true;
    }

    @Override
    public List<String> getOwnerToApprove() {
        return List.of();
    }

    @Override
    public HashMap<String, List<Boolean>> getManagerToApprove() {
        return null;
    }

    @Override
    public List<Notification> getNotifications() {
        throw new UnsupportedOperationException("Visitors cannot receive delayed notifications");
    }

    @Override
    public String getNotificationsJson(){
        throw new UnsupportedOperationException("Visitors cannot receive delayed notifications");
    }

    @Override
    public void clearPendingNotifications(){
        throw new UnsupportedOperationException("Visitors cannot receive delayed notifications");
    }

    @Override
    public void receiveDelayedNotification(Notification notification) {
        throw new UnsupportedOperationException("Visitors cannot receive delayed notifications");
    }

    @Override
    public LocalDate getBirthdate() {
        return null;
    }

    @Override
    public int getAge() {
        return 0;
    }

    @Override
    public String getStoresIOwn() {
        return "";
    }

    @Override
    public String getStoresIManage() {
        return "";
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
    public void openStore(String storeName) {
        throw new RuntimeException("Only registered users can open store");
    }

    @Override
    public boolean isOwner(String store_name_id) {
        return false;
    }

    @Override
    public void addWaitingAppoint_Owner(String storeName) {
    }

    @Override
    public boolean isManager(String store_name_id) {
        return false;
    }

    @Override
    public void addWaitingAppoint_Manager(String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) {
    }

    @Override
    public boolean removeWaitingAppoint_Owner(String storeName) {
        return false;
    }

    @Override
    public List<Boolean> removeWaitingAppoint_Manager(String store_name_id) {
        return List.of();
    }

    @Override
    public void addManagerRole(String appoint, String store_name_id) {
    }

    @Override
    public void setPermissionsToManager(String store_name_id, boolean watch, boolean editSupply, boolean editBuyPolicy, boolean editDiscountPolicy) {
    }

    @Override
    public void addOwnerRole(String appoint, String storeName) {
    }

    @Override
    public Role getRoleByStoreId(String store_name_id) {
        return null;
    }

    @Override
    public boolean isAdmin() {
        return false;
    }

    @Override
    public void setAdmin(boolean value) {
    }
}
