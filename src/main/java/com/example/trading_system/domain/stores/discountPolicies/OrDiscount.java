package com.example.trading_system.domain.stores.discountPolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Collection;

@Entity
@DiscriminatorValue("OR")
public class OrDiscount extends DiscountPolicy {



    @OneToOne(cascade = CascadeType.ALL)
    private Condition first;

    @OneToOne(cascade = CascadeType.ALL)
    private Condition second;

    @OneToOne(cascade = CascadeType.ALL)
    private DiscountPolicy then;

    public OrDiscount() {
        this.first = new PlaceholderCondition();
        this.second = new PlaceholderCondition();
        this.then = new PlaceholderDiscountPolicy();
    }

    @Override
    public double calculateDiscount(Collection<ProductInSaleDTO> items) {
        if (first.isSatisfied(items) || second.isSatisfied(items)) return then.calculateDiscount(items);
        else return 0;
    }

    @Override
    public void setFirst(DiscountPolicy first) {
        throw new RuntimeException("Action not allowed for or discount");
    }

    @Override
    public void setSecond(DiscountPolicy second) {
        throw new RuntimeException("Action not allowed for or discount");
    }

    @Override
    public boolean isSatisfied(Collection<ProductInSaleDTO> items) {
        return calculateDiscount(items) > 0;
    }

    @Override
    public void setCount(int count) {
        throw new RuntimeException("Action not allowed for or discount");
    }

    @Override
    public void setSum(double requiredSum) {
        throw new RuntimeException("Action not allowed for or discount");
    }

    @Override
    public void setFirst(Condition first) {
        this.first = first;
    }

    @Override
    public void setSecond(Condition second) {
        this.second = second;
    }

    @Override
    public void setThen(DiscountPolicy then) {
        this.then = then;
    }

    @Override
    public void setCategory(int discountedCategory) {
        throw new RuntimeException("Action not allowed for or discount");
    }

    @Override
    public void setProductId(int productId) {
        throw new RuntimeException("Action not allowed for or discount");
    }

    @Override
    public void setPercent(double discountPercent) {
        throw new RuntimeException("Action not allowed for or discount");
    }

    @Override
    public void setDecider(Condition decider) {
        throw new RuntimeException("Action not allowed for or discount");
    }

    @Override
    public String getInfo() {
        String firstInfo = first.getInfo();
        String secondInfo = second.getInfo();
        String thenInfo = then.getInfo();
        return "{ \"type\": \"or\", \"first\": " + firstInfo + ", \"second\": " + secondInfo + ", \"then\": " + thenInfo + " }";
    }

}
