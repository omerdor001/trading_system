package com.example.trading_system.domain.users;

import com.example.trading_system.domain.stores.Category;

import java.util.List;

public class Manager extends RoleState {
    private boolean watch;
    private boolean editSupply;
    private boolean editBuyPolicy;
    private boolean editDiscountPolicy;

    public Manager(Role role,boolean watch,boolean editSupply,boolean editBuyPolicy,boolean editDiscountPolicy) {
        this.role=role;
        this.watch=watch;
        this.editBuyPolicy=editBuyPolicy;
        this.editDiscountPolicy=editDiscountPolicy;
        this.editSupply=editSupply;
    }

    @Override
    public boolean isWatch() {
        return watch;
    }

    @Override
    public void setWatch(boolean watch) {
        this.watch = watch;
    }

    @Override
    public boolean isEditSupply() {
        return editSupply;
    }

    @Override
    public void setEditSupply(boolean editSupply) {
        this.editSupply = editSupply;
    }

    public boolean isEditBuyPolicy() {
        return editBuyPolicy;
    }

    @Override
    public void setEditBuyPolicy(boolean editBuyPolicy) {
        this.editBuyPolicy = editBuyPolicy;
    }

    public boolean isEditDiscountPolicy() {
        return editDiscountPolicy;
    }

    @Override
    public void setEditDiscountPolicy(boolean editDiscountPolicy) {
        this.editDiscountPolicy = editDiscountPolicy;
    }


    @Override
    public void addProduct(String username, int product_id, String store_name, String product_name, String product_description, double product_price, int product_quantity, double rating, Category category, List<String> keyWords) throws IllegalAccessException {
        throw new IllegalAccessException("Manager cannot add products");
    }

    @Override
    public void removeProduct(String username, String store_name_id, int product_id) throws IllegalAccessException{
        throw new IllegalAccessException("Manager cannot remove products");
    }

    @Override
    public void setProduct_name(String username,String store_name_id,int productId,String product_name) throws IllegalAccessException {
        throw new IllegalAccessException("Manager cannot remove products");
    }

    @Override
    public void setProduct_description(String username,String store_name_id,int productId,String product_description) throws IllegalAccessException {
        throw new IllegalAccessException("Manager cannot remove products");
    }

    @Override
    public void setProduct_price(String username,String store_name_id,int productId,int product_price) throws IllegalAccessException {
        throw new IllegalAccessException("Manager cannot remove products");
    }

    @Override
    public void setProduct_quantity(String username,String store_name_id,int productId,int product_quantity) throws IllegalAccessException {
        throw new IllegalAccessException("Manager cannot remove products");
    }

    @Override
    public void setRating(String username,String store_name_id,int productId,int rating) throws IllegalAccessException {
        throw new IllegalAccessException("Manager cannot remove products");
    }

    @Override
    public void setCategory(String username,String store_name_id,int productId,Category category) throws IllegalAccessException {
        throw new IllegalAccessException("Manager cannot remove products");
    }

    @Override
    public void getHistoryPurchasesByCustomer() throws IllegalAccessException {
        throw new IllegalAccessException("Manager cannot get history purchases by customer");

    }

    @Override
    public void getAllHistoryPurchases() throws IllegalAccessException {
        throw new IllegalAccessException("Manager cannot get all history purchases");
    }

    @Override
    public void requestInformationAboutOfficialsInStore() throws IllegalAccessException {
        throw new IllegalAccessException("Manager cannot request information about officials in store.");
    }

    @Override
    public boolean isManager() {
        return true;
    }

    @Override
    public boolean isOwner() {
        return false;
    }
}
