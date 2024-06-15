package com.example.trading_system.domain.users;

import java.util.List;

public abstract class RoleState {
    protected Role role;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    private void addProduct(int storeId, String product_details) {
    }

    private void editProduct(int storeId, int productId) {
    }

    private void removeProduct(int storeId, int productId) {
    }

    private void changeProduct(int storeId, int productId) {
    }

    private void addBuyPolicy(int storeId, int productId) {
    }

    private void removeBuyPolicy(int storeId, int productId) {
    }

    private void editBuyPolicy(int storeId, int productId) {
    }

    private void addDiscountPolicy(int storeId, int productId) {
    }

    private void removeDiscountPolicy(int storeId, int productId) {
    }

    private void editDiscountPolicy(int storeId, int productId) {
    }

    public void requestInformationAboutRolesInStore(int storeId) throws IllegalAccessException {
        throw new IllegalArgumentException("");
    }

    public boolean isWatch() throws IllegalAccessException {
        throw new IllegalAccessException("Only managers can access isWatch");
    }

    public abstract void setWatch(boolean watch);

    public boolean isEditSupply() throws IllegalAccessException {
        throw new IllegalAccessException("Only managers can access isEditSupply");
    }

    public abstract void setEditSupply(boolean editSupply);

    public boolean isEditBuyPolicy() throws IllegalAccessException {
        throw new IllegalAccessException("Only managers can access isEditBuyPolicy");
    }

    public abstract void setEditBuyPolicy(boolean editBuyPolicy);

    public boolean isEditDiscountPolicy() throws IllegalAccessException {
        throw new IllegalAccessException("Only managers can access isEditDiscountPolicy");
    }

    public abstract void setEditDiscountPolicy(boolean editDiscountPolicy);

    public abstract void addProduct(String username, int product_id, String store_name, String product_name, String product_description, double product_price, int product_quantity, double rating, int category, List<String> keyWords) throws IllegalAccessException;

    public abstract void getHistoryPurchasesByCustomer() throws IllegalAccessException;

    public abstract void getAllHistoryPurchases() throws IllegalAccessException;

    public void requestInformationAboutOfficialsInStore() throws IllegalAccessException {
        throw new IllegalArgumentException("only owners can request information about officials in store");}

    public void requestManagersPermissions() throws IllegalArgumentException { throw new IllegalArgumentException("only owners can request manager permissions"); }

    public void requestInformationAboutSpecificOfficialInStore() throws IllegalArgumentException {
        throw new IllegalArgumentException("only owners can request information about specific employee");
    }

    public abstract void removeProduct(String username, String store_name_id, int product_id) throws IllegalAccessException;

    public abstract void setProduct_name(String username, String store_name_id, int productId, String product_name) throws IllegalAccessException;

    public abstract void setProduct_description(String username, String store_name_id, int productId, String product_description) throws IllegalAccessException;

    public abstract void setProduct_price(String username, String store_name_id, int productId, double product_price) throws IllegalAccessException;

    public abstract void setProduct_quantity(String username, String store_name_id, int productId, int product_quantity) throws IllegalAccessException;

    public abstract void setRating(String username, String store_name_id, int productId, double rating) throws IllegalAccessException;

    public abstract void setCategory(String username, String store_name_id, int productId, int category) throws IllegalAccessException;

    public abstract boolean isManager();

    public abstract boolean isOwner();


}
