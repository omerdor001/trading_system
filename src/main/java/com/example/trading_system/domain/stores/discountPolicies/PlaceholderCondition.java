package com.example.trading_system.domain.stores.discountPolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Collection;

@Entity
@DiscriminatorValue("PLACEHOLDER")
public class PlaceholderCondition extends Condition {



    @Override
    public boolean isSatisfied(Collection<ProductInSaleDTO> items) {
        return false;
    }

    @Override
    public void setCategory(int category) {
        throw new RuntimeException("Action not allowed for placeholder condition");
    }

    @Override
    public void setCount(int count) {
        throw new RuntimeException("Action not allowed for placeholder condition");
    }

    @Override
    public void setSum(double requiredSum) {
        throw new RuntimeException("Action not allowed for placeholder condition");
    }

    @Override
    public void setProductId(int productId) {
        throw new RuntimeException("Action not allowed for placeholder condition");
    }

    @Override
    public String getInfo() {
        return "{ \"type\": \"placeholderCondition\" }";
    }
}
