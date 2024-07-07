package com.example.trading_system.domain.stores.purchasePolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;
import jakarta.persistence.Embeddable;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Collection;
@Entity
@DiscriminatorValue(("OrPolicy"))
public class OrPolicy extends PurchasePolicy{
    @ManyToOne(cascade = CascadeType.ALL)
    private PurchasePolicy predicateOne;
    @ManyToOne(cascade = CascadeType.ALL)
    private PurchasePolicy predicateTwo;

    public OrPolicy() {
        predicateOne = new PlaceholderPurchasePolicy();
        predicateTwo = new PlaceholderPurchasePolicy();
    }
    @Override
    public boolean isPurchasePolicySatisfied(Collection<ProductInSaleDTO> items ,int age) {
        return predicateOne.isPurchasePolicySatisfied(items,age) || predicateTwo.isPurchasePolicySatisfied(items,age);
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
        throw new RuntimeException("Action not allowed for policy by or policy");
    }

    @Override
    public void setPurchasePolicyProduct(int productID) {
        throw new RuntimeException("Action not allowed for policy by or policy");
    }

    @Override
    public void setPurchasePolicyNumOfQuantity(int sum) {
        throw new RuntimeException("Action not allowed for policy by or policy");
    }

    @Override
    public void setPurchasePolicyDateTime(LocalDateTime date) {
        throw new RuntimeException("Action not allowed for policy by or policy");
    }

    @Override
    public void setPurchasePolicyAge(int age) {
        throw new RuntimeException("Action not allowed for policy by or policy");
    }

    public String getPurchasePolicyInfo() {
        String firstInfo = predicateOne.getPurchasePolicyInfo();
        String secondInfo = predicateTwo.getPurchasePolicyInfo();
        return "{ \"type\": \"or\", \"first\": " + firstInfo + ", \"second\": " + secondInfo + " }";
    }
}
