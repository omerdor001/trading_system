package com.example.trading_system.domain.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.HashSet;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "role_state_id")
    private RoleState roleState;

    @Column
    private String store_name_id;

    @Column
    private String appointedById;

    @Setter
    @ManyToOne
    @JoinColumn(name = "registered_username", referencedColumnName = "username", nullable = false)
    private Registered registeredUser;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "users_appointing", referencedColumnName = "id")
    @Getter
    private Set<Registered> usersAppointedByMe;

    public Role(String store_name_id, String appointedById) {
        this.store_name_id = store_name_id;
        this.appointedById = appointedById;
        this.usersAppointedByMe = new HashSet<>();
    }

    public Role() {

    }

    public void addUserAppointedByMe(Registered registered){
        this.usersAppointedByMe.add(registered);
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

    public void addKeywordToProduct(String username, String store_name_id, int productId, String keyword) throws IllegalAccessException {
        roleState.addKeywordToProduct(username,store_name_id,productId,keyword);
    }

    public void removeKeywordFromProduct(String username, String store_name_id, int productId,String keyword) throws IllegalAccessException {
        roleState.removeKeywordFromProduct(username,store_name_id,productId,keyword);
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

    public void approveBid() throws IllegalAccessException{
        roleState.approveBid();
    }

    public void rejectBid() throws IllegalAccessException{
        roleState.rejectBid();
    }

    public void placeCounterOffer() throws IllegalAccessException{
        roleState.placeCounterOffer();
    }

    public void getStoreBids() throws IllegalAccessException{
        roleState.getStoreBids();
    }

    //Getters of permissions
    public boolean isWatch(){
        return roleState.isWatch();
    }

    public boolean isEditSupply() {
        return roleState.isEditSupply();
    }

    public boolean isEditPurchasePolicy(){
        return roleState.isEditPurchasePolicy();
    }

    public boolean isEditDiscountPolicy(){
        return roleState.isEditDiscountPolicy();
    }

    public boolean isAcceptBids(){
        return roleState.isAcceptBids();
    }


}


