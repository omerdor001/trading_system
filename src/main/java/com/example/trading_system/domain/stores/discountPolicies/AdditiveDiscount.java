package com.example.trading_system.domain.stores.discountPolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Collection;

@Entity
@DiscriminatorValue("Additive")
public class AdditiveDiscount extends DiscountPolicy {

    @OneToOne
    @JoinColumn(name = "first_discount_id")
    private DiscountPolicy first;

    @OneToOne
    @JoinColumn(name = "second_discount_id")
    private DiscountPolicy second;

    public AdditiveDiscount() {
        this.first = new PlaceholderDiscountPolicy();
        this.second = new PlaceholderDiscountPolicy();
    }

    @Override
    public double calculateDiscount(Collection<ProductInSaleDTO> items) {
        return first.calculateDiscount(items) + second.calculateDiscount(items);
    }

    @Override
    public void setFirst(DiscountPolicy first) {
        this.first = first;
    }

    @Override
    public void setSecond(DiscountPolicy second) {
        this.second = second;
    }

    @Override
    public void setFirst(Condition first) {
        throw new UnsupportedOperationException("Action not allowed for additive discount");
    }

    @Override
    public void setSecond(Condition second) {
        throw new UnsupportedOperationException("Action not allowed for additive discount");
    }

    @Override
    public void setThen(DiscountPolicy then) {
        throw new UnsupportedOperationException("Action not allowed for additive discount");
    }

    @Override
    public void setCategory(int discountedCategory) {
        throw new UnsupportedOperationException("Action not allowed for additive discount");
    }

    @Override
    public void setProductId(int productId) {
        throw new UnsupportedOperationException("Action not allowed for additive discount");
    }

    @Override
    public void setPercent(double discountPercent) {
        throw new UnsupportedOperationException("Action not allowed for additive discount");
    }

    @Override
    public void setDecider(Condition decider) {
        throw new UnsupportedOperationException("Action not allowed for additive discount");
    }

    @Override
    public boolean isSatisfied(Collection<ProductInSaleDTO> items) {
        return calculateDiscount(items) > 0;
    }

    @Override
    public void setCount(int count) {
        throw new UnsupportedOperationException("Action not allowed for additive discount");
    }

    @Override
    public void setSum(double requiredSum) {
        throw new UnsupportedOperationException("Action not allowed for additive discount");
    }

    @Override
    public String getInfo() {
        String firstInfo = first.getInfo();
        String secondInfo = second.getInfo();
        return "{ \"type\": \"additive\", \"first\": " + firstInfo + ", \"second\": " + secondInfo + " }";
    }
}
