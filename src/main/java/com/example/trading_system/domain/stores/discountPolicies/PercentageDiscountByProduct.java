package com.example.trading_system.domain.stores.discountPolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Embeddable;
import jakarta.persistence.*;

import java.util.Collection;
@Entity
@DiscriminatorValue("PercentageDiscountByProduct")

public class PercentageDiscountByProduct extends  DiscountPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    private double discountPercent;
    private int productId;

    public PercentageDiscountByProduct(double discountPercent, int productId) {
        this.discountPercent = discountPercent;
        this.productId = productId;
    }

    public PercentageDiscountByProduct() {

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
    public void setSum(double requiredSum) {
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

    @Override
    public String getInfo() {
        return "{ \"type\": \"percentageProduct\", \"productId\": " + productId + ", \"percent\": " + discountPercent + " }";
    }
}
