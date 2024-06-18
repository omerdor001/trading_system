package com.example.trading_system.domain.stores.purchasePolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;

import java.time.LocalDateTime;
import java.util.Collection;

public class PurchasePolicyByShoppingCartMaxProducts implements PurchasePolicy {
    private int sumOfProducts;
    public PurchasePolicyByShoppingCartMaxProducts(int sumOfProducts){
        this.sumOfProducts=sumOfProducts;
    }

    @Override
    public boolean isSatisfied(Collection<ProductInSaleDTO> items, int age) {
        int sum=0;
        for (ProductInSaleDTO productInSaleDTO:items){
            sum=sum+productInSaleDTO.getQuantity();
        }
        if(sum>sumOfProducts){
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
        throw new RuntimeException("Action not allowed for policy by shopping cart max products");
    }

    @Override
    public void setProduct(int productID) {
        throw new RuntimeException("Action not allowed for policy by shopping cart max products");
    }

    @Override
    public void setNumOfQuantity(int sum) {
        throw new RuntimeException("Action not allowed for policy by shopping cart max products");
    }

    @Override
    public void setSumOfProducts(int sum) {
        if(sum<=0)
            throw new IllegalArgumentException("Parameter "+sum+" cannot be negative and equal");
       this.sumOfProducts=sum;
    }

    @Override
    public void setDateTime(LocalDateTime date) {
        throw new RuntimeException("Action not allowed for policy by shopping cart max products");
    }

    @Override
    public void setWeight(int weight) {
        throw new RuntimeException("Action not allowed for policy by shopping cart max products");
    }

    @Override
    public void setAge(int age) {
        throw new RuntimeException("Action not allowed for policy by shopping cart max products");
    }

    @Override
    public String getInfo() {
        return "{ \"type\": \"ShoppingCart sum of product too high\", \"sumOfProducts_limit\": " + sumOfProducts+  " }";
    }
}
