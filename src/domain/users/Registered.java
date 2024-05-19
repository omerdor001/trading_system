package users;

import stores.ShoppingCart;

public class Registered extends User{
    private int userId;
    private String userName;
    private String address;
    private int age;
    private boolean isAdmin;

    public Registered(int id, ShoppingCart shopping_cart, int userId, String userName, String address, int age) {
        super(id, shopping_cart);
        this.userId=userId;
        this.userName=userName;    //Can be change to email
        this.address=address;
        this.age=age;
        this.isAdmin=false;
    }

    public void logout(){}
    public void openStore(){}
    public void performBuying(ShoppingCart shopping_cart) {}
    public boolean approveAppointment(int userId) { return false; }
    public void searchProduct(int productId) {}
    public void seeHistoryPurchase() {}
    public void seeHistoryPurchase(int storeId,int userId,int productId) {}
    public void openMarket() {}
    public void closeMarket() {}
}
