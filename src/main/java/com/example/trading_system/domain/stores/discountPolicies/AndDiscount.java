package com.example.trading_system.domain.stores.discountPolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Collection;

@Entity
@DiscriminatorValue("AND")
public class AndDiscount extends DiscountPolicy {

    @OneToOne
    @JoinColumn(name = "first_condition_id")
    private Condition first;

    @OneToOne
    @JoinColumn(name = "second_condition_id")
    private Condition second;

    @OneToOne
    @JoinColumn(name = "then_discount_policy_id")
    private DiscountPolicy then;

    public AndDiscount() {
        this.first = new PlaceholderCondition();
        this.second = new PlaceholderCondition();
        this.then = new PlaceholderDiscountPolicy();
    }

    @Override
    public double calculateDiscount(Collection<ProductInSaleDTO> items) {
        if (first.isSatisfied(items) && second.isSatisfied(items)) {
            return then.calculateDiscount(items);
        } else {
            return 0;
        }
    }

    @Override
    public void setFirst(DiscountPolicy first) {
        throw new RuntimeException("Action not allowed for and discount");
    }

    @Override
    public void setSecond(DiscountPolicy second) {
        throw new RuntimeException("Action not allowed for and discount");
    }

    public void setFirst(Condition first) {
        this.first = first;
    }

    public void setSecond(Condition second) {
        this.second = second;
    }

    public void setThen(DiscountPolicy then) {
        this.then = then;
    }

    @Override
    public void setCategory(int discountedCategory) {
        throw new RuntimeException("Action not allowed for and discount");
    }

    @Override
    public void setProductId(int productId) {
        throw new RuntimeException("Action not allowed for and discount");
    }

    @Override
    public void setPercent(double discountPercent) {
        throw new RuntimeException("Action not allowed for and discount");
    }

    @Override
    public void setDecider(Condition decider) {
        throw new RuntimeException("Action not allowed for and discount");
    }

    @Override
    public boolean isSatisfied(Collection<ProductInSaleDTO> items) {
        return calculateDiscount(items) > 0;
    }

    @Override
    public void setCount(int count) {
        throw new RuntimeException("Action not allowed for and discount");
    }

    @Override
    public void setSum(double requiredSum) {
        throw new RuntimeException("Action not allowed for and discount");
    }

    @Override
    public String getInfo() {
        String firstInfo = first.getInfo();
        String secondInfo = second.getInfo();
        String thenInfo = then.getInfo();
        return "{ \"type\": \"and\", \"first\": " + firstInfo + ", \"second\": " + secondInfo + ", \"then\": " + thenInfo + " }";
    }
}
