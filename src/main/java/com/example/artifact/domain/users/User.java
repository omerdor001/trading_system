package com.example.artifact.domain.users;

public class User {
    public int id;
    private Cart shopping_cart;

    public User(int id, Cart shopping_cart) {
        this.id = id;            //Might be change ?
        this.shopping_cart = shopping_cart;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cart getShopping_cart() {
        return shopping_cart;
    }

    public void setShopping_cart(Cart shopping_cart) {
        this.shopping_cart = shopping_cart;
    }
}
