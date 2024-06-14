package com.example.trading_system.domain.discountPolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;

import java.util.Collection;


public class XorDiscount implements DiscountPolicy, Condition {
    private DiscountPolicy first;
    private DiscountPolicy second;
    private Condition decider;

    public XorDiscount() {
        this.first = new PlaceholderDiscountPolicy();
        this.second = new PlaceholderDiscountPolicy();
        this.decider = new PlaceholderCondition();
    }

    @Override
    public double calculateDiscount(Collection<ProductInSaleDTO> items) {
        double firstDiscount = first.calculateDiscount(items);
        double secondDiscount = second.calculateDiscount(items);
        if (firstDiscount > 0) {
            if (secondDiscount > 0) {
                if (decider.isSatisfied(items)) return firstDiscount;
                else return secondDiscount;
            } else return firstDiscount;
        } else return secondDiscount;
    }

    public void setFirst(DiscountPolicy first) {
        this.first = first;
    }

    public void setSecond(DiscountPolicy second) {
        this.second = second;
    }

    @Override
    public void setFirst(Condition first) {
        throw new RuntimeException("Action not allowed for xor discount");
    }

    @Override
    public void setSecond(Condition second) {
        throw new RuntimeException("Action not allowed for xor discount");
    }

    @Override
    public void setThen(DiscountPolicy then) {
        throw new RuntimeException("Action not allowed for xor discount");
    }

    @Override
    public void setCategory(int discountedCategory) {
        throw new RuntimeException("Action not allowed for xor discount");
    }

    @Override
    public void setProductId(int productId) {
        throw new RuntimeException("Action not allowed for xor discount");
    }

    @Override
    public void setPercent(double discountPercent) {
        throw new RuntimeException("Action not allowed for xor discount");
    }

    public void setDecider(Condition decider) {
        this.decider = decider;
    }

    @Override
    public boolean isSatisfied(Collection<ProductInSaleDTO> items) {
        return calculateDiscount(items) > 0;
    }

    @Override
    public void setCount(int count) {
        throw new RuntimeException("Action not allowed for xor discount");
    }

    @Override
    public void setSum(int requiredSum) {
        throw new RuntimeException("Action not allowed for xor discount");
    }
}
