package com.example.trading_system.domain.stores.purchasePolicies;

import com.example.trading_system.domain.stores.ProductInSaleDTO;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public abstract class PurchasePolicy implements PurchasePolicyInterface {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public abstract boolean isPurchasePolicySatisfied(Collection<ProductInSaleDTO> items, int age);

    public abstract void setPurchasePolicyFirst(PurchasePolicy first);

    public abstract void setPurchasePolicySecond(PurchasePolicy second);

    public abstract void setPurchasePolicyCategory(int categoryId);

    public abstract void setPurchasePolicyProduct(int productID);

    public abstract void setPurchasePolicyNumOfQuantity(int sum);

    public abstract void setPurchasePolicyDateTime(LocalDateTime date);

    public abstract void setPurchasePolicyAge(int age);

    public abstract String getPurchasePolicyInfo();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
