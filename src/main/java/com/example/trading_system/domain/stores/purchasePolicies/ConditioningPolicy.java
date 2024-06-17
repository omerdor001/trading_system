package com.example.trading_system.domain.stores.purchasePolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;

import java.time.LocalDateTime;
import java.util.Collection;

public class ConditioningPolicy implements PurchasePolicy {
    private PurchasePolicy predicateOne;
    private PurchasePolicy predicateTwo;

    public ConditioningPolicy() {
        predicateOne = new PlaceholderPurchasePolicy();
        predicateTwo = new PlaceholderPurchasePolicy();
    }

    @Override
    public boolean isSatisfied(Collection<ProductInSaleDTO> items, int age) {
        if (predicateTwo.isSatisfied(items, age)){
            return predicateOne.isSatisfied(items, age);
        }
        return true;
    }

    @Override
    public void setFirst(PurchasePolicy first) {
        predicateOne=first;

    }

    @Override
    public void setSecond(PurchasePolicy second) {
        predicateTwo=second;
    }

    @Override
    public void setCategory(int categoryId) {
        throw new RuntimeException("Action not allowed for policy by conditional policy");
    }

    @Override
    public void setProduct(int productID) {
        throw new RuntimeException("Action not allowed for policy by conditional policy");
    }

    @Override
    public void setNumOfQuantity(int sum) {
        throw new RuntimeException("Action not allowed for policy by conditional policy");
    }

    @Override
    public void setSumOfProducts(int sum) {
        throw new RuntimeException("Action not allowed for policy by conditional policy");
    }

    @Override
    public void setDateTime(LocalDateTime date) {
        throw new RuntimeException("Action not allowed for policy by conditional policy");
    }

    @Override
    public void setWeight(int weight) {
        throw new RuntimeException("Action not allowed for policy by conditional policy");
    }

    @Override
    public void setAge(int age) {
        throw new RuntimeException("Action not allowed for policy by conditional policy");
    }

    public String getInfo() {
        String firstInfo = predicateOne.getInfo();
        String secondInfo = predicateTwo.getInfo();
        return "{ \"type\": \"conditioning\", \"first\": " + firstInfo + ", \"second\": " + secondInfo + " }";
    }
}
