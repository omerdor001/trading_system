package com.example.trading_system.domain.stores.purchasePolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;

import java.time.LocalDateTime;
import java.util.Collection;

public class PurchasePolicyByShoppingCartMinProducts implements PurchasePolicy {
    private int weight;
    public PurchasePolicyByShoppingCartMinProducts(int weight){
        this.weight=weight;
    }

    @Override
    public boolean isSatisfied(Collection<ProductInSaleDTO> items, int age) {
        for (ProductInSaleDTO productInSaleDTO:items){
            if(productInSaleDTO.getQuantity()>weight){
                return false;
            }
            else {
                return true;
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
        throw new RuntimeException("Action not allowed for policy by shopping cart min products");
    }

    @Override
    public void setProduct(int productID) {
        throw new RuntimeException("Action not allowed for policy by shopping cart min products");
    }

    @Override
    public void setNumOfQuantity(int sum) {
        throw new RuntimeException("Action not allowed for policy by shopping cart min products");
    }

    @Override
    public void setSumOfProducts(int sum) {
        throw new RuntimeException("Action not allowed for policy by shopping cart min products");
    }

    @Override
    public void setDateTime(LocalDateTime date) {
        throw new RuntimeException("Action not allowed for policy by shopping cart min products");
    }

    @Override
    public void setWeight(int weight) {
        if(weight<=0)
            throw new IllegalArgumentException("Parameter "+weight+" cannot be negative and equal");
        this.weight=weight;
    }

    @Override
    public void setAge(int age) {
        throw new RuntimeException("Action not allowed for policy by shopping cart min products");
    }

    @Override
    public String getInfo() {
        return "{ \"type\": \"ShoppingCart high weight\", \"weight_limit\": " + weight+  " }";
    }
}
