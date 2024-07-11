package com.example.trading_system.domain.stores.purchasePolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;
import jakarta.persistence.Embeddable;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@DiscriminatorValue("CONDITIONING")
public class ConditioningPolicy extends PurchasePolicy {

    @OneToOne
    private PurchasePolicy predicateOne;

    @OneToOne
    private PurchasePolicy predicateTwo;
    public ConditioningPolicy() {
        predicateOne = new PlaceholderPurchasePolicy();
        predicateTwo = new PlaceholderPurchasePolicy();
    }

    @Override
    public boolean isPurchasePolicySatisfied(Collection<ProductInSaleDTO> items, int age) {
        if (predicateTwo.isPurchasePolicySatisfied(items, age)){
            return predicateOne.isPurchasePolicySatisfied(items, age);
        }
        return true;
    }

    @Override
    public void setPurchasePolicyFirst(PurchasePolicy first) {
        predicateOne=first;

    }

    @Override
    public void setPurchasePolicySecond(PurchasePolicy second) {
        predicateTwo=second;
    }

    @Override
    public void setPurchasePolicyCategory(int categoryId) {
        throw new RuntimeException("Action not allowed for policy by conditional policy");
    }

    @Override
    public void setPurchasePolicyProduct(int productID) {
        throw new RuntimeException("Action not allowed for policy by conditional policy");
    }

    @Override
    public void setPurchasePolicyNumOfQuantity(int sum) {
        throw new RuntimeException("Action not allowed for policy by conditional policy");
    }

    @Override
    public void setPurchasePolicyDateTime(LocalDateTime date) {
        throw new RuntimeException("Action not allowed for policy by conditional policy");
    }

    @Override
    public void setPurchasePolicyAge(int age) {
        throw new RuntimeException("Action not allowed for policy by conditional policy");
    }

    public String getPurchasePolicyInfo() {
        String firstInfo = predicateOne.getPurchasePolicyInfo();
        String secondInfo = predicateTwo.getPurchasePolicyInfo();
        return "{ \"type\": \"conditioning\", \"first\": " + firstInfo + ", \"second\": " + secondInfo + " }";
    }
}
