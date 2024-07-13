package com.example.trading_system.domain.stores.discountPolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Collection;

@Entity
@DiscriminatorValue("CATEGORYCOUNT")
public class CategoryCountCondition extends Condition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(name = "category")
    private int category;

    @Column(name = "count")
    private int count;

    public CategoryCountCondition(int category, int count) {
        this.category = category;
        this.count = count;
    }

    public CategoryCountCondition() {
    }

    @Override
    public boolean isSatisfied(Collection<ProductInSaleDTO> items) {
        int amount = 0;
        for (ProductInSaleDTO p : items) {
            if (p.getCategory() == category) amount += p.getQuantity();
        }
        return amount > this.count;
    }

    @Override
    public void setCategory(int category) {
        this.category = category;
    }

    @Override
    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public void setSum(double requiredSum) {
        throw new RuntimeException("Action not allowed for category count condition");
    }

    @Override
    public void setProductId(int productId) {
        throw new RuntimeException("Action not allowed for category count condition");
    }

    @Override
    public String getInfo() {
        return "{ \"type\": \"categoryCount\", \"category\": " + category + ", \"count\": " + count + " }";
    }
}
