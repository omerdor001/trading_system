package com.example.trading_system.domain.users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;



@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role_type", discriminatorType = DiscriminatorType.STRING)
public abstract class RoleState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    @Transient
    protected Role role;


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

    public abstract boolean isWatch();

    public abstract void setWatch(boolean watch);

    public abstract boolean isEditSupply();

    public abstract void setEditSupply(boolean editSupply);

    public abstract void setAcceptBids(boolean acceptBids);

    public abstract boolean isEditPurchasePolicy();

    public abstract void setEditPurchasePolicy(boolean editPurchasePolicy);

    public abstract boolean isEditDiscountPolicy();

    public abstract boolean isAcceptBids();

    public abstract void setEditDiscountPolicy(boolean editDiscountPolicy);

    public abstract void setCreateLottery(boolean createLottery);

    public abstract boolean isCreateLottery();

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

    public abstract void addKeywordToProduct(String username, String store_name_id, int productId,String keyword) throws IllegalAccessException;

    public abstract void removeKeywordFromProduct(String username, String store_name_id, int productId,String keyword) throws IllegalAccessException;

    public abstract boolean isManager();

    public abstract boolean isOwner();


    public abstract void approveBid() throws IllegalAccessException;

    public abstract void rejectBid() throws IllegalAccessException;

    public abstract void placeCounterOffer() throws IllegalAccessException;

    public abstract void getStoreBids() throws IllegalAccessException;

    public abstract  void createProductLottery() throws IllegalAccessException;

}
