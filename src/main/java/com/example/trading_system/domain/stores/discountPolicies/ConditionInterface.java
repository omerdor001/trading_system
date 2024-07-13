package com.example.trading_system.domain.stores.discountPolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;
import java.util.Collection;

public interface ConditionInterface {
    boolean isSatisfied(Collection<ProductInSaleDTO> items);

    void setCategory(int category);

    void setCount(int count);

    void setSum(double requiredSum);

    void setProductId(int productId);

    String getInfo();
}