package com.example.trading_system.domain.stores.purchasePolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;

import java.time.LocalDateTime;
import java.util.Collection;

public class PlaceholderPurchasePolicy implements PurchasePolicy{

    public PlaceholderPurchasePolicy() {
    }

    @Override
    public boolean isSatisfied(Collection<ProductInSaleDTO> items, int age) {
        return true;
    }

    @Override
    public void setFirst(PurchasePolicy first) {
        throw new RuntimeException("Action not allowed for policy by placeholder purchase policy");
    }

    @Override
    public void setSecond(PurchasePolicy second) {
        throw new RuntimeException("Action not allowed for policy by placeholder purchase policy");
    }

    @Override
    public void setCategory(int categoryId) {
        throw new RuntimeException("Action not allowed for policy by placeholder purchase policy");
    }

    @Override
    public void setProduct(int productID) {
        throw new RuntimeException("Action not allowed for policy by placeholder purchase policy");
    }

    @Override
    public void setNumOfQuantity(int sum) {
        throw new RuntimeException("Action not allowed for policy by placeholder purchase policy");
    }

    @Override
    public void setSumOfProducts(int sum) {
        throw new RuntimeException("Action not allowed for policy by placeholder purchase policy");
    }

    @Override
    public void setDateTime(LocalDateTime date) {
        throw new RuntimeException("Action not allowed for policy by placeholder purchase policy");
    }

    @Override
    public void setWeight(int weight) {
        throw new RuntimeException("Action not allowed for policy by placeholder purchase policy");
    }

    @Override
    public void setAge(int age) {
        throw new RuntimeException("Action not allowed for policy by placeholder purchase policy");
    }

    @Override
    public String getInfo() {
        return "{ \"type\": \"placeholderPurchasePolicy\" }";
    }
}
