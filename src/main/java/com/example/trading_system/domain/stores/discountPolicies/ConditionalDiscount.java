package com.example.trading_system.domain.stores.discountPolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Collection;

@Entity
@DiscriminatorValue("CONDITIONAL")
public class ConditionalDiscount extends DiscountPolicy {

    @OneToOne(cascade = CascadeType.ALL)
    private Condition condition;

    @OneToOne(cascade = CascadeType.ALL)
    private DiscountPolicy then;

    public ConditionalDiscount() {
        this.condition = new PlaceholderCondition();
        this.then = new PlaceholderDiscountPolicy();
    }

    @Override
    public double calculateDiscount(Collection<ProductInSaleDTO> items) {
        if (condition.isSatisfied(items)) return then.calculateDiscount(items);
        else return 0;
    }

    @Override
    public void setFirst(DiscountPolicy first) {
        throw new RuntimeException("Action not allowed for conditional discount");
    }

    @Override
    public void setSecond(DiscountPolicy second) {
        throw new RuntimeException("Action not allowed for conditional discount");
    }

    @Override
    public void setFirst(Condition first) {
        this.condition = first;
    }

    @Override
    public void setSecond(Condition second) {
        throw new RuntimeException("Action not allowed for conditional discount");
    }

    @Override
    public boolean isSatisfied(Collection<ProductInSaleDTO> items) {
        return calculateDiscount(items) > 0;
    }

    @Override
    public void setCount(int count) {
        throw new RuntimeException("Action not allowed for conditional discount");
    }

    @Override
    public void setSum(double requiredSum) {
        throw new RuntimeException("Action not allowed for conditional discount");
    }

    @Override
    public void setThen(DiscountPolicy then) {
        this.then = then;
    }

    @Override
    public void setCategory(int discountedCategory) {
        throw new RuntimeException("Action not allowed for conditional discount");
    }

    @Override
    public void setProductId(int productId) {
        throw new RuntimeException("Action not allowed for conditional discount");
    }

    @Override
    public void setPercent(double discountPercent) {
        throw new RuntimeException("Action not allowed for conditional discount");
    }

    @Override
    public void setDecider(Condition decider) {
        throw new RuntimeException("Action not allowed for conditional discount");
    }

    @Override
    public String getInfo() {
        String conditionInfo = condition.getInfo();
        String thenInfo = then.getInfo();
        return "{ \"type\": \"conditional\", \"condition\": " + conditionInfo + ", \"then\": " + thenInfo + " }";
    }

}
