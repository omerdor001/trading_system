package com.example.trading_system.domain.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "manager_roles")
public class Manager extends RoleState {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "watch")
    private boolean watch;

    @Column(name = "edit_supply")
    private boolean editSupply;

    @Column(name = "edit_purchase_policy")
    private boolean editPurchasePolicy;

    @Column(name = "edit_discount_policy")
    private boolean editDiscountPolicy;
    private boolean acceptBids;
    private boolean createLottery;

    public Manager() {
        this.watch = false;
        this.editPurchasePolicy = false;
        this.editDiscountPolicy = false;
        this.editSupply = false;
        this.acceptBids = false;
        this.createLottery = false;
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

    @Override
    public boolean isEditPurchasePolicy() {
        return editPurchasePolicy;
    }

    @Override
    public void setEditPurchasePolicy(boolean editPurchasePolicy) {
        this.editPurchasePolicy = editPurchasePolicy;
    }

    @Override
    public boolean isEditDiscountPolicy() {
        return editDiscountPolicy;
    }

    @Override
    public void setEditDiscountPolicy(boolean editDiscountPolicy) {
        this.editDiscountPolicy = editDiscountPolicy;
    }

    @Override
    public void setAcceptBids(boolean acceptBids){
        this.acceptBids = acceptBids;
    }

    @Override
    public boolean isAcceptBids(){
        return acceptBids;
    }

    @Override
    public void setCreateLottery(boolean createLottery){
        this.createLottery = createLottery;
    }

    @Override
    public boolean isCreateLottery()
    {
        return createLottery;
    }


    @Override
    public void addProduct(String username, int product_id, String store_name, String product_name, String product_description, double product_price, int product_quantity, double rating, int category, List<String> keyWords) throws IllegalAccessException {
        if (!this.editSupply) throw new IllegalAccessException("Manager cannot add products");
    }

    @Override
    public void removeProduct(String username, String store_name_id, int product_id) throws IllegalAccessException {
        if (!this.editSupply) throw new IllegalAccessException("Manager cannot remove products");
    }

    @Override
    public void setProduct_name(String username, String store_name_id, int productId, String product_name) throws IllegalAccessException {
        if (!this.editSupply) throw new IllegalAccessException("Manager cannot edit products");
    }

    @Override
    public void setProduct_description(String username, String store_name_id, int productId, String product_description) throws IllegalAccessException {
        if (!this.editSupply) throw new IllegalAccessException("Manager cannot edit products");
    }

    @Override
    public void setProduct_price(String username, String store_name_id, int productId, double product_price) throws IllegalAccessException {
        if (!this.editSupply) throw new IllegalAccessException("Manager cannot edit products");
    }

    @Override
    public void setProduct_quantity(String username, String store_name_id, int productId, int product_quantity) throws IllegalAccessException {
        if (!this.editSupply) throw new IllegalAccessException("Manager cannot edit products");
    }

    @Override
    public void setRating(String username, String store_name_id, int productId, double rating) throws IllegalAccessException {
        if (!this.editSupply) throw new IllegalAccessException("Manager cannot edit products");
    }

    @Override
    public void setCategory(String username, String store_name_id, int productId, int category) throws IllegalAccessException {
        if (!this.editSupply) throw new IllegalAccessException("Manager cannot edit products");
    }

    @Override
    public void addKeywordToProduct(String username, String store_name_id, int productId, String keyword) throws IllegalAccessException {
        if (!this.editSupply) throw new IllegalAccessException("Manager cannot edit products");
    }

    @Override
    public void removeKeywordFromProduct(String username, String store_name_id, int productId, String keyword) throws IllegalAccessException {
        if (!this.editSupply) throw new IllegalAccessException("Manager cannot edit products");
    }

    @Override
    public void getHistoryPurchasesByCustomer() throws IllegalAccessException {
        if (!this.watch) throw new IllegalAccessException("Manager cannot get history purchases by customer");

    }

    @Override
    public void getAllHistoryPurchases() throws IllegalAccessException {
        if (!this.watch) throw new IllegalAccessException("Manager cannot get all history purchases");
    }

    @Override
    public boolean isManager() {
        return true;
    }

    @Override
    public boolean isOwner() {
        return false;
    }

    @Override
    public void approveBid()  throws IllegalAccessException{
        if(!this.acceptBids) throw new IllegalAccessException("Manager cannot approve bid");
    }

    @Override
    public void rejectBid()  throws IllegalAccessException{
        if(!this.acceptBids) throw new IllegalAccessException("Manager cannot reject bid");
    }

    @Override
    public void placeCounterOffer() throws IllegalAccessException{
        if(!this.acceptBids) throw new IllegalAccessException("Manager cannot place counter offer");
    }

    @Override
    public void getStoreBids() throws IllegalAccessException {
        if(!this.acceptBids) throw new IllegalAccessException("Manager cannot view store bids");

    }

    @Override
    public void createProductLottery() throws IllegalAccessException{
        if(!this.createLottery) throw new IllegalAccessException("Manager has not permission for create product lottery");
    }

}
