package com.example.trading_system.domain.stores.purchasePolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;
import java.util.Collection;
@Entity
@DiscriminatorValue("PurchasePolicyByProductAndDate")
public class PurchasePolicyByProductAndDate extends PurchasePolicy {
    @Column(name = "productId")

    private int productId;
    @Column(name = "dateTime")

    private LocalDateTime dateTime;
    public PurchasePolicyByProductAndDate(int productId, LocalDateTime localDateTime){
        this.productId=productId;
        this.dateTime=localDateTime;
    }

    public PurchasePolicyByProductAndDate() {

    }

    @Override
    public boolean isPurchasePolicySatisfied(Collection<ProductInSaleDTO> items, int age) {
        for (ProductInSaleDTO productInSaleDTO:items){
            if(productInSaleDTO.getId()==productId && dateTime.isAfter(LocalDateTime.now())){
                return false;
            }
            else {
                return true;
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
        throw new RuntimeException("Action not allowed for policy by product and date");
    }

    @Override
    public void setPurchasePolicyProduct(int productID) {
        if(productID<=0 )
            throw new IllegalArgumentException("Parameter "+productID+" cannot be negative or zero");
        this.productId=productID;
    }

    @Override
    public void setPurchasePolicyNumOfQuantity(int sum) {
        throw new RuntimeException("Action not allowed for policy by product and date");
    }

    @Override
    public void setPurchasePolicyDateTime(LocalDateTime date) {
        if(dateTime==null)
            throw new IllegalArgumentException("Parameter "+dateTime+" cannot be null");
       this.dateTime=date;
    }

    @Override
    public void setPurchasePolicyAge(int age) {
        throw new RuntimeException("Action not allowed for policy by product and date");
    }

    @Override
    public String getPurchasePolicyInfo() {
        return "{ \"type\": \"ShoppingCart product and date after now\", \"dateTime_limit\": " + dateTime+", \"productId\": " + productId +  " }";
    }
}
