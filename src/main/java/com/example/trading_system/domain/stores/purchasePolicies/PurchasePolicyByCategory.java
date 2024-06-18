package com.example.trading_system.domain.stores.purchasePolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;

import java.time.LocalDateTime;
import java.util.Collection;

public class PurchasePolicyByCategory implements PurchasePolicy{
    private int category;
    public PurchasePolicyByCategory(int category){
        this.category=category;
    }

    @Override
    public boolean isSatisfied(Collection<ProductInSaleDTO> items, int age) {
        for (ProductInSaleDTO productInSaleDTO:items){
            if(productInSaleDTO.getCategory()!=category){
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
        if(categoryId<=0)
            throw new IllegalArgumentException("Parameter "+categoryId+" cannot be negative or zero");
        this.category=categoryId;
    }

    @Override
    public void setProduct(int productID) {
        throw new RuntimeException("Action not allowed for policy by category");
    }

    @Override
    public void setNumOfQuantity(int sum) {
        throw new RuntimeException("Action not allowed for policy by category");
    }

    @Override
    public void setSumOfProducts(int sum) {
        throw new RuntimeException("Action not allowed for policy by category");
    }

    @Override
    public void setDateTime(LocalDateTime date) {
        throw new RuntimeException("Action not allowed for policy by category");
    }

    @Override
    public void setWeight(int weight) {
        throw new RuntimeException("Action not allowed for policy by category");
    }

    @Override
    public void setAge(int age) {
        throw new RuntimeException("Action not allowed for policy by category");
    }

    @Override
    public String getInfo() {
        return "{ \"type\": \"ShoppingCart category \", \"categoryId\": " + category +  " }";
    }


}
