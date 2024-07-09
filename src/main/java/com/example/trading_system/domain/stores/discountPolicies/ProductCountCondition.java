package com.example.trading_system.domain.stores.discountPolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Collection;

@Entity
@DiscriminatorValue("PRODUCT_COUNT")
public class ProductCountCondition extends Condition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(name = "product_id")
    private int productId;

    @Column(name = "count")
    private int count;

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
