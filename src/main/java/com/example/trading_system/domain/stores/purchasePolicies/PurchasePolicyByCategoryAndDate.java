package com.example.trading_system.domain.stores.purchasePolicies;
import com.example.trading_system.domain.stores.ProductInSaleDTO;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;
import java.util.Collection;
@Entity
@DiscriminatorValue("PurchasePolicyByCategoryAndDate")
public class PurchasePolicyByCategoryAndDate extends PurchasePolicy {
    @Column(name = "category")

    private int category;
    @Column(name = "dateTime")

    private LocalDateTime dateTime;
    public PurchasePolicyByCategoryAndDate(int category, LocalDateTime dateTime){
        this.category=category;
        this.dateTime=dateTime;
    }

    public PurchasePolicyByCategoryAndDate() {

    }

    @Override
    public boolean isPurchasePolicySatisfied(Collection<ProductInSaleDTO> items, int age) {
        for (ProductInSaleDTO productInSaleDTO:items){
            if(productInSaleDTO.getCategory()==category  &&
                    (dateTime.isBefore(LocalDateTime.now()) || dateTime.isEqual(LocalDateTime.now()))){
                return false;
            } else if (productInSaleDTO.getCategory()==category &&
                    dateTime.isAfter(LocalDateTime.now())) {
                return true;
            }
            else return true;
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
        if(categoryId<=0)
            throw new IllegalArgumentException("Parameter "+categoryId+" cannot be negative or zero");
       this.category=categoryId;
    }

    @Override
    public void setPurchasePolicyProduct(int productID) {
        throw new RuntimeException("Action not allowed for policy by category and date");
    }

    @Override
    public void setPurchasePolicyNumOfQuantity(int sum) {
        throw new RuntimeException("Action not allowed for policy by category and date");
    }

    @Override
    public void setPurchasePolicyDateTime(LocalDateTime date) {
        if(dateTime==null)
            throw new IllegalArgumentException("Parameter "+dateTime+" cannot be null");
       this.dateTime = date;
    }

    @Override
    public void setPurchasePolicyAge(int age) {
        throw new RuntimeException("Action not allowed for policy by category and date");
    }

    @Override
    public String getPurchasePolicyInfo() {
        return "{ \"type\": \"Category and Date Policy\", \"date\": \"" + dateTime.toLocalDate().toString() + "\", \"category\": " + category + " }";
    }
}
