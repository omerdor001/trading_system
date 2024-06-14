package com.example.trading_system.domain.discountPolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;

import java.util.Collection;

public class PlaceholderCondition implements Condition {

    public PlaceholderCondition() {
    }

    @Override
    public boolean isSatisfied(Collection<ProductInSaleDTO> items) {
        return false;
    }

    @Override
    public void setCategory(int category) {
        throw new RuntimeException("Action not allowed for placeholder condition");
    }

    @Override
    public void setCount(int count) {
        throw new RuntimeException("Action not allowed for placeholder condition");
    }

    @Override
    public void setSum(int requiredSum) {
        throw new RuntimeException("Action not allowed for placeholder condition");
    }
}
