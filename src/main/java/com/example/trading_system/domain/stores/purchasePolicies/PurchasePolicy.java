package com.example.trading_system.domain.stores.purchasePolicies;
import com.example.trading_system.domain.stores.ProductInSaleDTO;
import com.example.trading_system.domain.stores.discountPolicies.DiscountPolicy;

import java.time.LocalDateTime;
import java.util.Collection;

public interface PurchasePolicy {
    boolean isSatisfied(Collection<ProductInSaleDTO> items, int age);
    void setFirst(PurchasePolicy first);
    void setSecond(PurchasePolicy second);
    void setCategory(int categoryId);
    void setProduct(int productID);
    void setNumOfQuantity(int sum);
    void setSumOfProducts(int sum);
    void setDateTime(LocalDateTime date);
    void setWeight(int weight);
    void setAge(int age);
    String getInfo();
}
