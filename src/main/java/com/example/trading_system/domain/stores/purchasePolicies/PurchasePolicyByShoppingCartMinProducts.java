package com.example.trading_system.domain.stores.purchasePolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;
import java.util.Collection;
@Entity
@DiscriminatorValue("PurchasePolicyByShoppingCartMinProducts")
public class PurchasePolicyByShoppingCartMinProducts extends PurchasePolicy {
    @Column(name = "numOfQuantity")

    private int numOfQuantity;
    public PurchasePolicyByShoppingCartMinProducts(int numOfQuantity){

        this.numOfQuantity=numOfQuantity;
    }

    public PurchasePolicyByShoppingCartMinProducts() {

    }

    @Override
    public boolean isPurchasePolicySatisfied(Collection<ProductInSaleDTO> items, int age) {
        int sum=0;
        for (ProductInSaleDTO productInSaleDTO:items){
            sum=sum+productInSaleDTO.getQuantity();
        }
        if(sum<numOfQuantity){
            return false;
        }
        else{
            return true;
        }
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
        throw new RuntimeException("Action not allowed for policy by shopping cart min products");
    }

    @Override
    public void setPurchasePolicyProduct(int productID) {
        throw new RuntimeException("Action not allowed for policy by shopping cart min products");
    }

    @Override
    public void setPurchasePolicyNumOfQuantity(int sum) {
        if(numOfQuantity<=0)
            throw new IllegalArgumentException("Parameter "+numOfQuantity+" cannot be negative and equal");
        this.numOfQuantity=sum;
    }

    @Override
    public void setPurchasePolicyDateTime(LocalDateTime date) {
        throw new RuntimeException("Action not allowed for policy by shopping cart min products");
    }

    @Override
    public void setPurchasePolicyAge(int age) {
        throw new RuntimeException("Action not allowed for policy by shopping cart min products");
    }

    @Override
    public String getPurchasePolicyInfo() {
        return "{ \"type\": \"ShoppingCart high weight\", \"weight_limit\": " + numOfQuantity +  " }";
    }
}
