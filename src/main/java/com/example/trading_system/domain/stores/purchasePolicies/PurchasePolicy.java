package com.example.trading_system.domain.stores.purchasePolicies;
import com.example.trading_system.domain.stores.ProductInSaleDTO;
import com.example.trading_system.domain.stores.discountPolicies.DiscountPolicy;

import java.time.LocalDateTime;
import java.util.Collection;

public interface PurchasePolicy {
    boolean isPurchasePolicySatisfied(Collection<ProductInSaleDTO> items, int age);
    void setPurchasePolicyFirst(PurchasePolicy first);
    void setPurchasePolicySecond(PurchasePolicy second);
    void setPurchasePolicyCategory(int categoryId);
    void setPurchasePolicyProduct(int productID);
    void setPurchasePolicyNumOfQuantity(int sum);
    void setPurchasePolicyDateTime(LocalDateTime date);
    void setPurchasePolicyAge(int age);
    String getPurchasePolicyInfo();
}
