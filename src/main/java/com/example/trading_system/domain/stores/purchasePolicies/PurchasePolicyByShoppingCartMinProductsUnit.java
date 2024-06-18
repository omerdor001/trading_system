package com.example.trading_system.domain.stores.purchasePolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;

import java.time.LocalDateTime;
import java.util.Collection;

public class PurchasePolicyByShoppingCartMinProductsUnit implements PurchasePolicy {
    private int productId;
    private int numOfQuantity;
    public PurchasePolicyByShoppingCartMinProductsUnit(int productId, int numOfQuantity){
        this.productId=productId;
        this.numOfQuantity=numOfQuantity;
    }

    @Override
    public boolean isSatisfied(Collection<ProductInSaleDTO> items, int age) {
        for (ProductInSaleDTO productInSaleDTO:items){
            if(productInSaleDTO.getId()==productId && productInSaleDTO.getQuantity()<numOfQuantity){
                return false;
            }
        }
        return true;
    }

    @Override
    public void setFirst(PurchasePolicy first) {
        throw new RuntimeException("This is a simple, uncomplicated purchase policy");
    }

    @Override
    public void setSecond(PurchasePolicy second) {
        throw new RuntimeException("This is a simple, uncomplicated purchase policy");
    }

    @Override
    public void setCategory(int categoryId) {
        throw new RuntimeException("Action not allowed for policy by shopping cart min products unit");
    }

    @Override
    public void setProduct(int productID) {
        if(productID<=0 )
            throw new IllegalArgumentException("Parameter "+productID+" cannot be negative or zero");
       this.productId=productID;
    }

    @Override
    public void setNumOfQuantity(int sum) {
        if(numOfQuantity<=0)
            throw new IllegalArgumentException("Parameter "+numOfQuantity+" cannot be negative and equal");
      this.numOfQuantity=sum;
    }

    @Override
    public void setSumOfProducts(int sum) {
        throw new RuntimeException("Action not allowed for policy by shopping cart min products unit");
    }

    @Override
    public void setDateTime(LocalDateTime date) {
        throw new RuntimeException("Action not allowed for policy by shopping cart min products unit");
    }

    @Override
    public void setWeight(int weight) {
        throw new RuntimeException("Action not allowed for policy by shopping cart min products unit");
    }

    @Override
    public void setAge(int age) {
        throw new RuntimeException("Action not allowed for policy by shopping cart min products unit");
    }

    @Override
    public String getInfo() {
        return "{ \"type\": \"ShoppingCart low quantity\", \"productId\": " + productId+ ", \"numOfQuantity_limit\": " + numOfQuantity + " }";
    }
}
