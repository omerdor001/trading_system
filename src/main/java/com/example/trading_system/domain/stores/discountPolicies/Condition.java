package com.example.trading_system.domain.stores.discountPolicies;


import com.example.trading_system.domain.stores.ProductInSaleDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Collection;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public abstract  class Condition implements ConditionInterface{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    public abstract boolean isSatisfied(Collection<ProductInSaleDTO> items);

    public abstract void setCategory(int category);

    public abstract void setCount(int count);

    public abstract void setSum(double requiredSum);

    public abstract String getInfo();
}
