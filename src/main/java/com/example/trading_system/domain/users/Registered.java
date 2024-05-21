package com.example.trading_system.domain.users;
import java.time.LocalDate;

public class Registered extends User{
    private String userName;
    private String address;
    private LocalDate birthdate;
    private boolean isAdmin;

    public Registered(int id, Cart shoppingCart, String userName, String address, LocalDate birthdate) {
        super(id, shoppingCart);
        this.userName=userName;    //Can be change to email
        this.address=address;
        this.birthdate = birthdate;
        this.isAdmin=false;
    }

    public Registered(int id, Cart shoppingCart, String userName, LocalDate birthdate) {
        super(id, shoppingCart);
        this.userName=userName;    //Can be change to email
        this.birthdate = birthdate;
        this.isAdmin=false;
    }

    public void logout(){}
    public void openStore(){}
    public void performBuying(Cart shopping_cart) {}
    public boolean approveAppointment(int userId) { return false; }
    public void searchProduct(int productId) {}
    public void seeHistoryPurchase() {}
    public void seeHistoryPurchase(int storeId,int userId,int productId) {}
    public void openMarket() {}
    public void closeMarket() {}
}
