package com.example.trading_system.domain.users;

import com.example.trading_system.domain.stores.Category;

import java.util.List;

public abstract class RoleState {
    protected Role role;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    private void addProduct(int storeId,String product_details) {}

    private void editProduct(int storeId,int productId) {}

    private void removeProduct(int storeId,int productId) {}

    private void changeProduct(int storeId,int productId) {}

    private void addBuyPolicy(int storeId,int productId) {}

    private void removeBuyPolicy(int storeId,int productId) {}

    private void editBuyPolicy(int storeId,int productId) {}

    private void addDiscountPolicy(int storeId,int productId) {}

    private void removeDiscountPolicy(int storeId,int productId) {}

    private void editDiscountPolicy(int storeId,int productId) {}
    public void requestInformationAboutRolesInStore(int storeId)throws IllegalAccessException{throw new IllegalArgumentException("");}

    public abstract void addProduct(String username,  int product_id, String store_name, String product_name, String product_description,
                                    double product_price, int product_quantity, double rating, Category category, List<String> keyWords) throws IllegalAccessException;

    public abstract void removeProduct(String username, String store_name_id, int product_id) throws IllegalAccessException;

    public abstract void setProduct_name(String username,String store_name_id,int productId,String product_name) throws IllegalAccessException;

    public abstract void setProduct_description(String username,String store_name_id,int productId,String product_description) throws IllegalAccessException;

    public abstract void setProduct_price(String username,String store_name_id,int productId,int product_price) throws IllegalAccessException;

    public abstract void setProduct_quantity(String username,String store_name_id,int productId,int product_quantity) throws IllegalAccessException;

    public abstract void setRating(String username,String store_name_id,int productId,int rating) throws IllegalAccessException;

    public abstract void setCategory(String username,String store_name_id,int productId,Category category) throws IllegalAccessException;

    public abstract boolean isManager();

    public abstract boolean isOwner();

    public abstract void setWatch(boolean watch);

    public abstract void setEditSupply(boolean editSupply);

    public abstract void setEditBuyPolicy(boolean editBuyPolicy);

    public abstract void setEditDiscountPolicy(boolean editDiscountPolicy);
}
