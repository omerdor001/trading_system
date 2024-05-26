package com.example.trading_system.domain.users;

import java.util.List;

public class Manager extends RoleState{
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

    public boolean isWatch() {
        return watch;
    }

    public void setWatch(boolean watch) {
        this.watch = watch;
    }

    public boolean isEditSupply() {
        return editSupply;
    }

    public void setEditSupply(boolean editSupply) {
        this.editSupply = editSupply;
    }

    public boolean isEditBuyPolicy() {
        return editBuyPolicy;
    }

    public void setEditBuyPolicy(boolean editBuyPolicy) {
        this.editBuyPolicy = editBuyPolicy;
    }

    public boolean isEditDiscountPolicy() {
        return editDiscountPolicy;
    }

    public void setEditDiscountPolicy(boolean editDiscountPolicy) {
        this.editDiscountPolicy = editDiscountPolicy;
    }


    @Override
    public void addProduct(String username, int product_id, String store_name, String product_name, String product_description, double product_price, int product_quantity, double rating, int category, List<String> keyWords) throws IllegalAccessException {
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
    public void setCategory(String username,String store_name_id,int productId,int category) throws IllegalAccessException {
        throw new IllegalAccessException("Manager cannot remove products");
    }
}
