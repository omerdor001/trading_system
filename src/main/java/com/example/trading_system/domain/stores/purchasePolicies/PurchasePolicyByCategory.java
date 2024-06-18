package com.example.trading_system.domain.stores.purchasePolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;

import java.time.LocalDateTime;
import java.util.Collection;

public class PurchasePolicyByCategory implements PurchasePolicy{
    private int category;
    private int productId;
    public PurchasePolicyByCategory(int category, int productId){
        this.category=category;
        this.productId=productId;
    }

    @Override
    public boolean isPurchasePolicySatisfied(Collection<ProductInSaleDTO> items, int age) {
        for (ProductInSaleDTO productInSaleDTO:items){
            if(productInSaleDTO.getCategory()!=category && productInSaleDTO.getId()==productId){
                return false;
            }
        }
        return true;
    }

    @Override
    public void setPurchasePolicyFirst(PurchasePolicy first) {
        throw new RuntimeException("This is a simple, uncomplicated purchase policy");
    }

    @Override
    public void setPurchasePolicySecond(PurchasePolicy second) {
        throw new RuntimeException("This is a simple, uncomplicated purchase policy");
    }

    @Override
    public void setPurchasePolicyCategory(int categoryId) {
        if(categoryId<=0)
            throw new IllegalArgumentException("Parameter "+categoryId+" cannot be negative or zero");
        this.category=categoryId;
    }

    @Override
    public void setPurchasePolicyProduct(int productID) {
        if(productID<=0 )
            throw new IllegalArgumentException("Parameter "+productID+" cannot be negative or zero");
        this.productId=productID;
    }

    @Override
    public void setPurchasePolicyNumOfQuantity(int sum) {
        throw new RuntimeException("Action not allowed for policy by category");
    }

    @Override
    public void setPurchasePolicyDateTime(LocalDateTime date) {
        throw new RuntimeException("Action not allowed for policy by category");
    }

    @Override
    public void setPurchasePolicyAge(int age) {
        throw new RuntimeException("Action not allowed for policy by category");
    }

    @Override
    public String getPurchasePolicyInfo() {
        return "{ \"type\": \"ShoppingCart category \", \"categoryId\": " + category +  " }";
    }

}
