package com.example.trading_system.domain.stores.discountPolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;

import java.util.Collection;

public interface DiscountPolicyInterface {
    double calculateDiscount(Collection<ProductInSaleDTO> items);

    void setFirst(DiscountPolicy first);

    void setSecond(DiscountPolicy second);

    void setFirst(Condition first);

    void setSecond(Condition second);

    void setThen(DiscountPolicy then);

    void setCategory(int discountedCategory);

    void setProductId(int productId);

    void setPercent(double discountPercent);

    void setDecider(Condition decider);
    boolean isSatisfied(Collection<ProductInSaleDTO> items);


    void setCount(int count);

    void setSum(double requiredSum);

    String getInfo();
}