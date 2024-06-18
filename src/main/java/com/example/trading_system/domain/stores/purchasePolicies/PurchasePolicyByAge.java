package com.example.trading_system.domain.stores.purchasePolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;

import java.time.LocalDateTime;
import java.util.Collection;

public class PurchasePolicyByAge implements PurchasePolicy {
    private int ageToCheck;
    private int categoryId;
    public PurchasePolicyByAge(int age, int productId){
        this.categoryId=productId;
        this.ageToCheck=age;
    }

    @Override
    public boolean isSatisfied(Collection<ProductInSaleDTO> items, int age) {
        for (ProductInSaleDTO productInSaleDTO:items){
            if(productInSaleDTO.getId()==categoryId && age<=ageToCheck){
                return false;
            }
        }
        return true;
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
        this.categoryId=categoryId;
    }

    @Override
    public void setProduct(int productID) {
        throw new RuntimeException("Action not allowed for policy by age");
    }

    @Override
    public void setNumOfQuantity(int sum) {
        throw new RuntimeException("Action not allowed for policy by age");
    }

    @Override
    public void setSumOfProducts(int sum) {
        throw new RuntimeException("Action not allowed for policy by age");
    }

    @Override
    public void setDateTime(LocalDateTime date) {
        throw new RuntimeException("Action not allowed for policy by age");
    }

    @Override
    public void setWeight(int weight) {
        throw new RuntimeException("Action not allowed for policy by age");
    }

    @Override
    public void setAge(int age) {
        if(age<=0 )
            throw new IllegalArgumentException("Parameter "+age+" cannot be negative or zero");
        this.ageToCheck=age;
    }

    @Override
    public String getInfo() {
        return "{ \"type\": \"ShoppingCart age and category\",  \"category\": " + categoryId+  " }";
    }


}
