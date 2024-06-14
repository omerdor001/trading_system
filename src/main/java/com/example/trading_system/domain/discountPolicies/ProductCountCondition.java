package com.example.trading_system.domain.discountPolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;

import java.util.Collection;

public class ProductCountCondition implements Condition {
    private int productId;
    private int count;

    public ProductCountCondition(int productId, int amount) {
        this.productId = productId;
        this.count = amount;
    }

    @Override
    public boolean isSatisfied(Collection<ProductInSaleDTO> items) {
        int amount = 0;
        for (ProductInSaleDTO p : items) {
            if (p.getProductId() == productId) {
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
    public void setSum(int requiredSum) {
        throw new RuntimeException("Action not allowed for product count condition");
    }
}
