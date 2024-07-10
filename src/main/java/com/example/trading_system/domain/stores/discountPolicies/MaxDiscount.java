package com.example.trading_system.domain.stores.discountPolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Collection;

@Entity
@DiscriminatorValue("MAX")
public class MaxDiscount extends DiscountPolicy {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private DiscountPolicy first;

    @OneToOne(cascade = CascadeType.ALL)
    private DiscountPolicy second;

    public MaxDiscount() {
        this.first = new PlaceholderDiscountPolicy();
        this.second = new PlaceholderDiscountPolicy();
    }

    @Override
    public double calculateDiscount(Collection<ProductInSaleDTO> items) {
        return Math.max(first.calculateDiscount(items), second.calculateDiscount(items));
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
        throw new RuntimeException("Action not allowed for max discount");
    }

    @Override
    public void setSecond(Condition second) {
        throw new RuntimeException("Action not allowed for max discount");
    }

    @Override
    public void setThen(DiscountPolicy then) {
        throw new RuntimeException("Action not allowed for max discount");
    }

    @Override
    public void setCategory(int discountedCategory) {
        throw new RuntimeException("Action not allowed for max discount");
    }

    @Override
    public void setProductId(int productId) {
        throw new RuntimeException("Action not allowed for max discount");
    }

    @Override
    public void setPercent(double discountPercent) {
        throw new RuntimeException("Action not allowed for max discount");
    }

    @Override
    public void setDecider(Condition decider) {
        throw new RuntimeException("Action not allowed for max discount");
    }

    @Override
    public boolean isSatisfied(Collection<ProductInSaleDTO> items) {
        return calculateDiscount(items) > 0;
    }

    @Override
    public void setCount(int count) {
        throw new RuntimeException("Action not allowed for max discount");
    }

    @Override
    public void setSum(double requiredSum) {
        throw new RuntimeException("Action not allowed for max discount");
    }

    @Override
    public String getInfo() {
        String firstInfo = first.getInfo();
        String secondInfo = second.getInfo();
        return "{ \"type\": \"max\", \"first\": " + firstInfo + ", \"second\": " + secondInfo + " }";
    }

}
