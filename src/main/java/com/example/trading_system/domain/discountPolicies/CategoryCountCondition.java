package com.example.trading_system.domain.discountPolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;

import java.util.Collection;

public class CategoryCountCondition implements Condition {
    private int category;
    private int count;

    public CategoryCountCondition(int category, int count) {
        this.category = category;
        this.count = count;
    }

    @Override
    public boolean isSatisfied(Collection<ProductInSaleDTO> items) {
        int amount = 0;
        for (ProductInSaleDTO p : items) {
            if (p.getCategory() == category) amount += p.getQuantity();
        }
        return amount > this.count;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public void setSum(int requiredSum) {
        throw new RuntimeException("Action not allowed for category count condition");
    }
}
