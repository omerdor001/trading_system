package com.example.trading_system.domain.users;

import java.util.List;

public class Owner extends RoleState{
    public Owner(Role role) {
        this.role=role;
    }

    @Override
    public void addProduct(String username,  int product_id, String store_name, String product_name, String product_description, double product_price, int product_quantity, double rating, int category, List<String> keyWords) {}

    @Override
    public void removeProduct(String username, String store_name_id, int product_id) {}

    @Override
    public void setProduct_name(String username, String store_name_id, int productId, String product_name) {}

    @Override
    public void setProduct_description(String username, String store_name_id, int productId, String product_description) {}

    @Override
    public void setProduct_price(String username, String store_name_id, int productId, int product_price) {}

    @Override
    public void setProduct_quantity(String username, String store_name_id, int productId, int product_quantity) {}

    @Override
    public void setRating(String username, String store_name_id, int productId, int rating) {}

    @Override
    public void setCategory(String username, String store_name_id, int productId, int category) {}


}
