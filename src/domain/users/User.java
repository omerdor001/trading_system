package users;

import stores.ShoppingCart;

public class User {
    public int id;
    private ShoppingCart shopping_cart;

    public User(int id,ShoppingCart shopping_cart) {
        this.id = id;            //Might be change ?
        this.shopping_cart = shopping_cart;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ShoppingCart getShopping_cart() {
        return shopping_cart;
    }

    public void setShopping_cart(ShoppingCart shopping_cart) {
        this.shopping_cart = shopping_cart;
    }
}
