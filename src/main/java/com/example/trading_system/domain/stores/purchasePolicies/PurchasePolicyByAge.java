package com.example.trading_system.domain.stores.purchasePolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;


@Entity
@DiscriminatorValue("AGE")
public class PurchasePolicyByAge extends PurchasePolicy {

    @Column(name = "age_to_check")
    private int ageToCheck;

    @Column(name = "category_id")
    private int categoryId;

    public PurchasePolicyByAge() {

    }

    @Override
    public boolean isPurchasePolicySatisfied(Collection<ProductInSaleDTO> items, int age) {
        for (ProductInSaleDTO productInSaleDTO:items){
            if(productInSaleDTO.getCategory()==categoryId && age<=ageToCheck){
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
        this.categoryId=categoryId;
    }

    @Override
    public void setPurchasePolicyProduct(int productID) {
        throw new RuntimeException("Action not allowed for policy by age");
    }

    @Override
    public void setPurchasePolicyNumOfQuantity(int sum) {
        throw new RuntimeException("Action not allowed for policy by age");
    }

    @Override
    public void setPurchasePolicyDateTime(LocalDateTime date) {
        throw new RuntimeException("Action not allowed for policy by age");
    }

    @Override
    public void setPurchasePolicyAge(int age) {
        if(age<=0)
            throw new IllegalArgumentException("Parameter "+age+" cannot be negative or zero");
        this.ageToCheck=age;
    }

    @Override
    public String getPurchasePolicyInfo() {
        return "{ \"type\": \"ShoppingCart age and category\", \"category\": " + categoryId+ ", \"ageToCheck\": " + ageToCheck +  " }";
    }


}
