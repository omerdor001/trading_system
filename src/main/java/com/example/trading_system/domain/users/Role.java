package com.example.trading_system.domain.users;

import javax.persistence.*;
import java.util.List;

@Entity
public class Role {
    @OneToOne(cascade = CascadeType.ALL)
    private RoleState roleState;

    @Column(nullable = false)
    private String store_name_id;

    @Column(nullable = false)
    private String appointedById;

    public Role(String store_name_id, String appointedById) {
        this.store_name_id = store_name_id;
        this.appointedById = appointedById;
    }

    public String getStoreId() {
        return store_name_id;
    }

    public void setStoreId(String store_name_id) {
        this.store_name_id = store_name_id;
    }

    public String getAppointedById() {
        return appointedById;
    }

    public void setAppointedById(String appointedById) {
        this.appointedById = appointedById;
    }

    public RoleState getRoleState() {
        return roleState;
    }

    public void setRoleState(RoleState roleState) {
        this.roleState = roleState;
        this.roleState.setRole(this); // Set the role context in the state
    }

    public void addProduct(String username, int product_id, String store_name, String product_name, String product_description, double product_price, int product_quantity, double rating, int category, List<String> keyWords) throws IllegalAccessException {
        roleState.addProduct(username, product_id, store_name, product_name, product_description, product_price, product_quantity, rating, category, keyWords);
    }

    public void removeProduct(String username, String store_name_id, int product_id) throws IllegalAccessException {
        roleState.removeProduct(username, store_name_id, product_id);
    }

    public void setProduct_name(String username, String store_name_id, int productId, String product_name) throws IllegalAccessException {
        roleState.setProduct_name(username, store_name_id, productId, product_name);
    }

    public void setProduct_description(String username, String store_name_id, int productId, String product_description) throws IllegalAccessException {
        roleState.setProduct_description(username, store_name_id, productId, product_description);
    }

    public void setProduct_price(String username, String store_name_id, int productId, double product_price) throws IllegalAccessException {
        roleState.setProduct_price(username, store_name_id, productId, product_price);
    }

    public void setProduct_quantity(String username, String store_name_id, int productId, int product_quantity) throws IllegalAccessException {
        roleState.setProduct_quantity(username, store_name_id, productId, product_quantity);
    }

    public void setRating(String username, String store_name_id, int productId, double rating) throws IllegalAccessException {
        roleState.setRating(username, store_name_id, productId, rating);
    }

    public void setCategory(String username, String store_name_id, int productId, int category) throws IllegalAccessException {
        roleState.setCategory(username, store_name_id, productId, category);
    }

    public boolean editDiscounts() throws IllegalAccessException {
        if(roleState.isEditDiscountPolicy())
            return true;
        else throw new IllegalAccessException("Only managers can access isEditDiscountPolicy");
    }

    public boolean editPurchasePolicies() throws IllegalAccessException {
        if(roleState.isEditPurchasePolicy()){
            return true;
        }
        else throw new IllegalAccessException("Only managers can access isEditPurchasePolicy");
    }
}


