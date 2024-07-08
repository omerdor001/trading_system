package com.example.trading_system.domain.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "owner_roles")
public class Owner extends RoleState {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Owner(Role role) {
        this.role = role;
    }

    @Override
    public void addProduct(String username, int product_id, String store_name, String product_name, String product_description, double product_price, int product_quantity, double rating, int category, List<String> keyWords) {
    }

    @Override
    public void removeProduct(String username, String store_name_id, int product_id) {
    }

    @Override
    public void setProduct_name(String username, String store_name_id, int productId, String product_name) {
    }

    @Override
    public void setProduct_description(String username, String store_name_id, int productId, String product_description) {
    }

    @Override
    public void setProduct_price(String username, String store_name_id, int productId, double product_price) {
    }

    @Override
    public void setProduct_quantity(String username, String store_name_id, int productId, int product_quantity) {
    }

    @Override
    public void setRating(String username, String store_name_id, int productId, double rating) {
    }

    @Override
    public void setCategory(String username, String store_name_id, int productId, int category) {
    }

    @Override
    public void addKeywordToProduct(String username, String store_name_id, int productId, String keyword) {

    }

    @Override
    public void removeKeywordFromProduct(String username, String store_name_id, int productId, String keyword) {

    }

    @Override
    public void getHistoryPurchasesByCustomer() {
    }

    @Override
    public void getAllHistoryPurchases() {
    }

    @Override
    public void requestInformationAboutOfficialsInStore() {
    }

    @Override
    public boolean isManager() {
        return false;
    }

    @Override
    public boolean isOwner() {
        return true;
    }

    @Override
    public void setWatch(boolean watch) {
    }

    @Override
    public void setEditSupply(boolean editSupply) {
    }

    @Override
    public void setEditPurchasePolicy(boolean editPurchasePolicy) {
    }

    @Override
    public void setEditDiscountPolicy(boolean editDiscountPolicy) {
    }

    @Override
    public void setCreateLottery(boolean createLottery) {

    }

    @Override
    public void setAcceptBids(boolean acceptBids) {

    }

    @Override
    public boolean isAcceptBids() {
        return true;
    }

    @Override
    public boolean isCreateLottery(){
        return true;
}

    @Override
    public void requestManagersPermissions() {
    }

    @Override
    public void requestInformationAboutSpecificOfficialInStore() {
    }

    @Override
    public boolean isWatch() {
        return true;
    }

    @Override
    public boolean isEditSupply() {
        return true;
    }

    @Override
    public boolean isEditPurchasePolicy() {
        return true;
    }

    @Override
    public boolean isEditDiscountPolicy() {
        return true;
    }

    @Override
    public void approveBid() {
    }

    @Override
    public void rejectBid() {
    }

    @Override
    public void placeCounterOffer() {

    }

    @Override
    public void getStoreBids() {
    }

    @Override
    public void createProductLottery(){

    }

}