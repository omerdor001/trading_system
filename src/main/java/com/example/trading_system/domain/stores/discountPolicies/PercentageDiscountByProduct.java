package com.example.trading_system.domain.stores.discountPolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;

import java.util.Collection;

public class PercentageDiscountByProduct implements DiscountPolicy, Condition {
    private double discountPercent;
    private int productId;

    public PercentageDiscountByProduct(double discountPercent, int productId) {
        this.discountPercent = discountPercent;
        this.productId = productId;
    }

    @Override
    public double calculateDiscount(Collection<ProductInSaleDTO> items) {
        double discount = 0;
        for (ProductInSaleDTO p : items) {
            if (p.getId() == productId) discount += p.getPrice() * discountPercent * p.getQuantity();
        }
        return discount;
    }

    @Override
    public void setFirst(DiscountPolicy first) {
        throw new RuntimeException("Action not allowed for product percentage discount");
    }

    @Override
    public void setSecond(DiscountPolicy second) {
        throw new RuntimeException("Action not allowed for product percentage discount");
    }

    @Override
    public void setFirst(Condition first) {
        throw new RuntimeException("Action not allowed for product percentage discount");
    }

    @Override
    public void setSecond(Condition second) {
        throw new RuntimeException("Action not allowed for product percentage discount");
    }

    @Override
    public void setThen(DiscountPolicy then) {
        throw new RuntimeException("Action not allowed for product percentage discount");
    }

    @Override
    public void setCategory(int discountedCategory) {
        throw new RuntimeException("Action not allowed for product percentage discount");
    }

    @Override
    public boolean isSatisfied(Collection<ProductInSaleDTO> items) {
        return calculateDiscount(items) > 0;
    }

    @Override
    public void setCount(int count) {
        throw new RuntimeException("Action not allowed for product percentage discount");
    }

    @Override
    public void setSum(int requiredSum) {
        throw new RuntimeException("Action not allowed for product percentage discount");
    }

    public void setPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    @Override
    public void setDecider(Condition decider) {
        throw new RuntimeException("Action not allowed for product percentage discount");
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
