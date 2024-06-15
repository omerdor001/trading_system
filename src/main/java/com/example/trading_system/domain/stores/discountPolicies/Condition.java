package com.example.trading_system.domain.stores.discountPolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;

import java.util.Collection;

public interface Condition {
    boolean isSatisfied(Collection<ProductInSaleDTO> items);

    void setCategory(int category);

    void setCount(int count);

    void setSum(int requiredSum);
}
