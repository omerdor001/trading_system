package com.example.trading_system.domain.stores.purchasePolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;

import java.time.LocalDateTime;
import java.util.Collection;

public class PurchasePolicyByProduct implements PurchasePolicy{
    private int productId;
    public PurchasePolicyByProduct(int productId){
        this.productId=productId;
    }

    @Override
    public boolean isSatisfied(Collection<ProductInSaleDTO> items, int age) {
        for (ProductInSaleDTO productInSaleDTO:items){
            if(productInSaleDTO.getId()!=productId){
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
        throw new RuntimeException("Action not allowed for productId");
    }

    @Override
    public void setProduct(int productID) {
        if(productID<=0 )
            throw new IllegalArgumentException("Parameter "+productID+" cannot be negative or zero");
        this.productId=productID;
    }

    @Override
    public void setNumOfQuantity(int sum) {
        throw new RuntimeException("Action not allowed for policy by product");
    }

    @Override
    public void setSumOfProducts(int sum) {
        throw new RuntimeException("Action not allowed for policy by product");
    }

    @Override
    public void setDateTime(LocalDateTime date) {
        throw new RuntimeException("Action not allowed for policy by product");
    }

    @Override
    public void setWeight(int weight) {
        throw new RuntimeException("Action not allowed for policy by product");
    }

    @Override
    public void setAge(int age) {
        throw new RuntimeException("Action not allowed for policy by product");
    }

    @Override
    public String getInfo() {
        return "{ \"type\": \"ShoppingCart product exist\", \"productId\": " + productId +  " }";
    }
}
