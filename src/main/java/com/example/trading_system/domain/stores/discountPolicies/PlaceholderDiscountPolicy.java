package com.example.trading_system.domain.stores.discountPolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Embeddable;
import jakarta.persistence.*;

import java.util.Collection;

@Entity
@DiscriminatorValue("PLACEHOLDER")
public class PlaceholderDiscountPolicy extends DiscountPolicy {


    public PlaceholderDiscountPolicy() {
    }

    @Override
    public double calculateDiscount(Collection<ProductInSaleDTO> items) {
        return 0;
    }

    @Override
    public void setFirst(DiscountPolicy first) {
        throw new RuntimeException("Action not allowed for placeholder discount");
    }

    @Override
    public void setSecond(DiscountPolicy second) {
        throw new RuntimeException("Action not allowed for placeholder discount");
    }

    @Override
    public void setFirst(Condition first) {
        throw new RuntimeException("Action not allowed for placeholder discount");
    }

    @Override
    public void setSecond(Condition second) {
        throw new RuntimeException("Action not allowed for placeholder discount");
    }

    @Override
    public void setThen(DiscountPolicy then) {
        throw new RuntimeException("Action not allowed for placeholder discount");
    }

    @Override
    public boolean isSatisfied(Collection<ProductInSaleDTO> items) {
        return false;
    }

    @Override
    public void setCategory(int discountedCategory) {
        throw new RuntimeException("Action not allowed for placeholder discount");
    }

    @Override
    public void setCount(int count) {
        throw new RuntimeException("Action not allowed for placeholder discount");
    }

    @Override
    public void setSum(double requiredSum) {
        throw new RuntimeException("Action not allowed for placeholder discount");
    }

    @Override
    public void setProductId(int productId) {
        throw new RuntimeException("Action not allowed for placeholder discount");
    }

    @Override
    public void setPercent(double discountPercent) {
        throw new RuntimeException("Action not allowed for placeholder discount");
    }

    @Override
    public void setDecider(Condition decider) {
        throw new RuntimeException("Action not allowed for placeholder discount");
    }

    @Override
    public String getInfo() {
        return "{ \"type\": \"placeholderDiscount\" }";
    }
}
