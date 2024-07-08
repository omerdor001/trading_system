package com.example.trading_system.domain.stores.discountPolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Collection;
@Entity
@DiscriminatorValue("PercentageDiscountByCategory")

public class PercentageDiscountByCategory extends  DiscountPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private  long id;
    private int discountedCategory;
    private double discountPercent;

    public PercentageDiscountByCategory(int discountedCategory, double discountPercent) {
        this.discountedCategory = discountedCategory;
        this.discountPercent = discountPercent;
    }

    public PercentageDiscountByCategory() {

    }

    @Override
    public double calculateDiscount(Collection<ProductInSaleDTO> items) {
        double discount = 0;
        for (ProductInSaleDTO p : items) {
            if (p.getCategory() == discountedCategory) discount += p.getPrice() * discountPercent * p.getQuantity();
        }
        return discount;
    }

    @Override
    public void setFirst(DiscountPolicy first) {
        throw new RuntimeException("Action not allowed for category percentage discount");
    }

    @Override
    public void setSecond(DiscountPolicy second) {
        throw new RuntimeException("Action not allowed for category percentage discount");
    }

    @Override
    public void setFirst(Condition first) {
        throw new RuntimeException("Action not allowed for category percentage discount");
    }

    @Override
    public void setSecond(Condition second) {
        throw new RuntimeException("Action not allowed for category percentage discount");
    }

    @Override
    public void setThen(DiscountPolicy then) {
        throw new RuntimeException("Action not allowed for category percentage discount");
    }

    @Override
    public boolean isSatisfied(Collection<ProductInSaleDTO> items) {
        return calculateDiscount(items) > 0;
    }

    @Override
    public void setCategory(int discountedCategory) {
        this.discountedCategory = discountedCategory;
    }

    @Override
    public void setProductId(int productId) {
        throw new RuntimeException("Action not allowed for category percentage discount");
    }

    @Override
    public void setCount(int count) {
        throw new RuntimeException("Action not allowed for category percentage discount");
    }

    @Override
    public void setSum(double requiredSum) {
        throw new RuntimeException("Action not allowed for category percentage discount");
    }

    @Override
    public void setPercent(double percent) {
        this.discountPercent = percent;
    }

    @Override
    public void setDecider(Condition decider) {
        throw new RuntimeException("Action not allowed for category percentage discount");
    }

    @Override
    public String getInfo() {
        return "{ \"type\": \"percentageCategory\", \"category\": " + discountedCategory + ", \"percent\": " + discountPercent + " }";
    }
}
