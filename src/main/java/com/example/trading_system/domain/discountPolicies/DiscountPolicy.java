package com.example.trading_system.domain.discountPolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;

import java.util.Collection;

public interface DiscountPolicy extends Condition {
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
}
