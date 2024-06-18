package com.example.trading_system.domain.stores.purchasePolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;

import java.time.LocalDateTime;
import java.util.Collection;

public class PurchasePolicyByShoppingBagAndDate implements PurchasePolicy {
    private LocalDateTime dateTime;
    public PurchasePolicyByShoppingBagAndDate(LocalDateTime dateTime){
        this.dateTime=dateTime;
    }

    @Override
    public boolean isSatisfied(Collection<ProductInSaleDTO> items, int age) {
        if(dateTime.isAfter(LocalDateTime.now())){
            return false;
        }
        else{
            return true;
        }
    }

    @Override
    public void setFirst(PurchasePolicy first) {
        throw new RuntimeException("This is a simple, uncomplicated purchase policy");
    }

    @Override
    public void setSecond(PurchasePolicy second) {
        throw new RuntimeException("This is a simple, uncomplicated purchase policy");
    }

    @Override
    public void setCategory(int categoryId) {
        throw new RuntimeException("Action not allowed for policy by shopping cart and date");
    }

    @Override
    public void setProduct(int productID) {
        throw new RuntimeException("Action not allowed for policy by shopping cart and date");
    }

    @Override
    public void setNumOfQuantity(int sum) {
        throw new RuntimeException("Action not allowed for policy by shopping cart and date");
    }

    @Override
    public void setSumOfProducts(int sum) {
        throw new RuntimeException("Action not allowed for policy by shopping cart and date");
    }

    @Override
    public void setDateTime(LocalDateTime date) {
        if(dateTime==null)
            throw new IllegalArgumentException("Parameter "+dateTime+" cannot be null");
       this.dateTime=date;
    }

    @Override
    public void setWeight(int weight) {
        throw new RuntimeException("Action not allowed for policy by shopping cart and date");
    }

    @Override
    public void setAge(int age) {
        throw new RuntimeException("Action not allowed for policy by shopping cart and date");
    }

    @Override
    public String getInfo() {
        return "{ \"type\": \"ShoppingCart date after now\", \"dateTime_limit\": " + dateTime+  " }";
    }

}
