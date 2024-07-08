package com.example.trading_system.domain.stores.discountPolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Embeddable;
import jakarta.persistence.*;

import java.util.Collection;
@Entity
@DiscriminatorValue("TotalSumCondition")

public class TotalSumCondition extends Condition {
    private double requiredSum;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    public TotalSumCondition(double requiredSum) {
        this.requiredSum = requiredSum;
    }

    public TotalSumCondition() {

    }

    @Override
    public boolean isSatisfied(Collection<ProductInSaleDTO> items) {
        int totalSum = 0;
        for (ProductInSaleDTO p : items) {
            totalSum += p.getPrice() * p.getQuantity();
        }
        return totalSum > requiredSum;
    }

    @Override
    public void setCategory(int category) {
        throw new RuntimeException("Action not allowed for total sum condition");
    }

    @Override
    public void setCount(int count) {
        throw new RuntimeException("Action not allowed for total sum condition");
    }

    public void setSum(double requiredSum) {
        this.requiredSum = requiredSum;
    }

    @Override
    public String getInfo() {
        return "{ \"type\": \"totalSum\", \"requiredSum\": " + requiredSum + " }";
    }
}
