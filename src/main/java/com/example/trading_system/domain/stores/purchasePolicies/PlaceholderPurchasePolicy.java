package com.example.trading_system.domain.stores.purchasePolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;
import java.util.Collection;
@Entity
@DiscriminatorValue("PlaceholderPurchasePolicy")
public class PlaceholderPurchasePolicy extends PurchasePolicy{

    public PlaceholderPurchasePolicy() {
    }

    @Override
    public boolean isPurchasePolicySatisfied(Collection<ProductInSaleDTO> items, int age) {
        return true;
    }

    @Override
    public void setPurchasePolicyFirst(PurchasePolicy first) {
        throw new RuntimeException("Action not allowed for policy by placeholder purchase policy");
    }

    @Override
    public void setPurchasePolicySecond(PurchasePolicy second) {
        throw new RuntimeException("Action not allowed for policy by placeholder purchase policy");
    }

    @Override
    public void setPurchasePolicyCategory(int categoryId) {
        throw new RuntimeException("Action not allowed for policy by placeholder purchase policy");
    }

    @Override
    public void setPurchasePolicyProduct(int productID) {
        throw new RuntimeException("Action not allowed for policy by placeholder purchase policy");
    }

    @Override
    public void setPurchasePolicyNumOfQuantity(int sum) {
        throw new RuntimeException("Action not allowed for policy by placeholder purchase policy");
    }

    @Override
    public void setPurchasePolicyDateTime(LocalDateTime date) {
        throw new RuntimeException("Action not allowed for policy by placeholder purchase policy");
    }

    @Override
    public void setPurchasePolicyAge(int age) {
        throw new RuntimeException("Action not allowed for policy by placeholder purchase policy");
    }

    @Override
    public String getPurchasePolicyInfo() {
        return "{ \"type\": \"placeholderPurchasePolicy\" }";
    }
}
