package users;

import stores.ShoppingCart;

public class Visitor extends User{
    public Visitor(int id, ShoppingCart shopping_cart) {
        super(id, shopping_cart);
    }

    public void registration(){ }   //Missing details to register

    public void login(){ }   //Missing details to register

}
