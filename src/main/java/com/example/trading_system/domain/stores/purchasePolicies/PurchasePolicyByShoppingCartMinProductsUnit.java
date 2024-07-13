package com.example.trading_system.domain.stores.purchasePolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;
import java.util.Collection;
@Entity
@DiscriminatorValue("PurchasePolicyByShoppingCartMinProductsUnit")
public class PurchasePolicyByShoppingCartMinProductsUnit extends PurchasePolicy {
    @Column(name = "productId")

    private int productId;
    @Column(name = "numOfQuantity")

    private int numOfQuantity;
    public PurchasePolicyByShoppingCartMinProductsUnit(int productId, int numOfQuantity){
        this.productId=productId;
        this.numOfQuantity=numOfQuantity;
    }

    public PurchasePolicyByShoppingCartMinProductsUnit() {
    }

    @Override
    public boolean isPurchasePolicySatisfied(Collection<ProductInSaleDTO> items, int age) {
        for (ProductInSaleDTO productInSaleDTO:items){
            if(productInSaleDTO.getId()==productId && productInSaleDTO.getQuantity()<numOfQuantity){
                return false;
            }
        }
        return true;
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
        throw new RuntimeException("Action not allowed for policy by shopping cart min products unit");
    }

    @Override
    public void setPurchasePolicyProduct(int productID) {
        if(productID<0)
            throw new IllegalArgumentException("Parameter "+productID+" cannot be negative or zero");
       this.productId=productID;
    }

    @Override
    public void setPurchasePolicyNumOfQuantity(int sum) {
        if(numOfQuantity<=0)
            throw new IllegalArgumentException("Parameter "+numOfQuantity+" cannot be negative and equal");
      this.numOfQuantity=sum;
    }

    @Override
    public void setPurchasePolicyDateTime(LocalDateTime date) {
        throw new RuntimeException("Action not allowed for policy by shopping cart min products unit");
    }

    @Override
    public void setPurchasePolicyAge(int age) {
        throw new RuntimeException("Action not allowed for policy by shopping cart min products unit");
    }

    @Override
    public String getPurchasePolicyInfo() {
        return "{ \"type\": \"Minimum product units in shopping bag Policy\", \"productId\": " + productId + ", \"units\": " + numOfQuantity + " }";
    }
}
