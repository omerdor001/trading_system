package com.example.trading_system.domain.stores.discountPolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;

import java.util.Collection;

public class TotalSumCondition implements Condition {
    private double requiredSum;

    public TotalSumCondition(double requiredSum) {
        this.requiredSum = requiredSum;
    }

    @Override
    public boolean isSatisfied(Collection<ProductInSaleDTO> items) {
        int totalSum = 0;
        for (ProductInSaleDTO p : items) {
            totalSum += p.getPrice() * p.getQuantity();
        }
        return totalSum > requiredSum;
    }

    @Override
    public void setCategory(int category) {
        throw new RuntimeException("Action not allowed for total sum condition");
    }

    @Override
    public void setCount(int count) {
        throw new RuntimeException("Action not allowed for total sum condition");
    }

    public void setSum(double requiredSum) {
        this.requiredSum = requiredSum;
    }

    @Override
    public String getInfo() {
        return "{ \"type\": \"totalSum\", \"requiredSum\": " + requiredSum + " }";
    }
}
