package com.example.trading_system.domain.stores.discountPolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Collection;
@Entity
@DiscriminatorValue("ProductCountCondition")

public class ProductCountCondition extends Condition {
    private int productId;
    private int count;
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    public ProductCountCondition(int productId, int amount) {
        this.productId = productId;
        this.count = amount;
    }

    public ProductCountCondition() {

    }

    @Override
    public boolean isSatisfied(Collection<ProductInSaleDTO> items) {
        int amount = 0;
        for (ProductInSaleDTO p : items) {
            if (p.getId() == productId) {
                amount += p.getQuantity();
                break;
            }
        }
        return amount > this.count;
    }

    @Override
    public void setCategory(int category) {
        throw new RuntimeException("Action not allowed for product count condition");
    }

    @Override
    public void setCount(int count) {
        this.count = count;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    @Override
    public void setSum(double requiredSum) {
        throw new RuntimeException("Action not allowed for product count condition");
    }

    @Override
    public String getInfo() {
        return "{ \"type\": \"productCount\", \"productId\": " + productId + ", \"count\": " + count + " }";
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
