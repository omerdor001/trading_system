package com.example.trading_system.domain.stores.discountPolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Collection;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")

public abstract class DiscountPolicy implements DiscountPolicyInterface {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @ManyToOne
    @JsonIgnore
    private DiscountPolicy first;

    @ManyToOne
    @JsonIgnore

    private DiscountPolicy second;

    @ManyToOne

    private DiscountPolicy then;

    public abstract double calculateDiscount(Collection<ProductInSaleDTO> items);

    public abstract void setFirst(DiscountPolicy first);

    public abstract void setSecond(DiscountPolicy second);

    public abstract void setFirst(Condition first);

    public abstract void setSecond(Condition second);

    public abstract void setThen(DiscountPolicy then);

    public abstract void setCategory(int discountedCategory);

    public abstract void setProductId(int productId);

    public abstract void setPercent(double discountPercent);

    public abstract void setDecider(Condition decider);

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
