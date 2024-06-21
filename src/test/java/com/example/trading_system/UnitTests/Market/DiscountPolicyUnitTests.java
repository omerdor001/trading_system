package com.example.trading_system.UnitTests.Market;

import com.example.trading_system.domain.stores.ProductInSaleDTO;
import com.example.trading_system.domain.stores.Store;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

public class DiscountPolicyUnitTests {
    Store store;
    ProductInSaleDTO product;
    LinkedList<ProductInSaleDTO> bag = new LinkedList<>();

    @BeforeEach
    void setUp() {
        store = new Store("store1", "", "user", 5.0);
        store.addProduct(0, "product1", "", 1, 5, 1, 1, new LinkedList<>());
        product = new ProductInSaleDTO();
        product.setStoreId("store1");
        product.setId(0);
        product.setPrice(1);
        product.setQuantity(1);
        product.setCategory(1);
        bag.add(product);
    }

    @Test
    public void testPercentageByCategory() {
        store.addCategoryPercentageDiscount(1, 0.5);
        double price = store.calculatePrice(bag);
        assertEquals(0.5, price);
    }

    @Test
    public void testPercentageByCategory_NoMatchingItem() {
        store.addCategoryPercentageDiscount(1, 0.5);
        product.setCategory(2);
        double price = store.calculatePrice(bag);
        assertEquals(1.0, price);
    }

    @Test
    public void testPercentageByProduct() {
        store.addProductPercentageDiscount(0, 0.5);
        double price = store.calculatePrice(bag);
        assertEquals(0.5, price);
    }

    @Test
    public void testPercentageByProduct_NoMatchingItem() {
        store.addProductPercentageDiscount(0, 0.5);
        product.setId(1);
        double price = store.calculatePrice(bag);
        assertEquals(1.0, price);
    }

    @Test
    public void testPercentageByStore() {
        store.addStoreDiscount( 0.5);
        double price = store.calculatePrice(bag);
        assertEquals(0.5, price);
    }

    @Test
    public void testAdditiveDiscount() {
        store.addStoreDiscount(0.5);
        store.addStoreDiscount(0.25);
        store.addAdditiveDiscount();
        store.setFirstDiscount(2,0);
        store.setSecondDiscount(1,0);
        double price = store.calculatePrice(bag);
        assertEquals(0.25, price);
    }


    @Test
    public void testMaxDiscount_FirstMax() {
        store.addStoreDiscount(0.5);
        store.addStoreDiscount(0.25);
        store.addMaxDiscount();
        store.setFirstDiscount(2,0);
        store.setSecondDiscount(1,0);
        double price = store.calculatePrice(bag);
        assertEquals(0.5, price);
    }

    @Test
    public void testMaxDiscount_SecondMax() {
        store.addStoreDiscount(0.5);
        store.addStoreDiscount(0.25);
        store.addMaxDiscount();
        store.setSecondDiscount(2,0);
        store.setFirstDiscount(1,0);
        double price = store.calculatePrice(bag);
        assertEquals(0.5, price);
    }

    @Test
    public void testConditionalDiscount_CategoryCount() {
        store.addStoreDiscount(0.5);
        store.addCategoryCountCondition(1,1);
        store.addConditionalDiscount();
        store.setFirstCondition(1,2);
        store.setThenDiscount(1,0);
        product.setQuantity(2);
        double price = store.calculatePrice(bag);
        assertEquals(1.0, price);
    }

    @Test
    public void testConditionalDiscount_CategoryCount_NotSatisfied() {
        store.addStoreDiscount(0.5);
        store.addCategoryCountCondition(1,1);
        store.addConditionalDiscount();
        store.setFirstCondition(1,2);
        store.setThenDiscount(1,0);
        product.setCategory(2);
        double price = store.calculatePrice(bag);
        assertEquals(1.0, price);
    }

    @Test
    public void testConditionalDiscount_ProductCount() {
        store.addStoreDiscount(0.5);
        store.addProductCountCondition(0,1);
        store.addConditionalDiscount();
        store.setFirstCondition(1,2);
        store.setThenDiscount(1,0);
        product.setQuantity(2);
        double price = store.calculatePrice(bag);
        assertEquals(1.0, price);
    }

    @Test
    public void testConditionalDiscount_ProductCount_NotSatisfied() {
        store.addStoreDiscount(0.5);
        store.addProductCountCondition(0,1);
        store.addConditionalDiscount();
        store.setFirstCondition(1,2);
        store.setThenDiscount(1,0);
        product.setQuantity(1);
        double price = store.calculatePrice(bag);
        assertEquals(1.0, price);
    }

    @Test
    public void testConditionalDiscount_TotalSum() {
        store.addStoreDiscount(0.5);
        store.addTotalSumCondition(1);
        store.addConditionalDiscount();
        store.setFirstCondition(1,2);
        store.setThenDiscount(1,0);
        product.setQuantity(2);
        double price = store.calculatePrice(bag);
        assertEquals(1.0, price);
    }

    @Test
    public void testConditionalDiscount_TotalSum_NotSatisfied() {
        store.addStoreDiscount(0.5);
        store.addTotalSumCondition(1);
        store.addConditionalDiscount();
        store.setFirstCondition(1,2);
        store.setThenDiscount(1,0);
        double price = store.calculatePrice(bag);
        assertEquals(1.0, price);
    }

    @Test
    public void testAndDiscount_BothSatisfied() {
        store.addStoreDiscount(0.5);
        store.addTotalSumCondition(1);
        store.addTotalSumCondition(2);
        store.addAndDiscount();
        store.setFirstCondition(1,2);
        store.setSecondCondition(1,2);
        store.setThenDiscount(1,0);
        product.setQuantity(3);
        double price = store.calculatePrice(bag);
        assertEquals(1.5, price);
    }

    @Test
    public void testAndDiscount_OnlyFirstSatisfied() {
        store.addStoreDiscount(0.5);
        store.addTotalSumCondition(1);
        store.addTotalSumCondition(3);
        store.addAndDiscount();
        store.setFirstCondition(1,2);
        store.setSecondCondition(1,2);
        store.setThenDiscount(1,0);
        product.setQuantity(3);
        double price = store.calculatePrice(bag);
        assertEquals(3.0, price);
    }

    @Test
    public void testAndDiscount_OnlySecondSatisfied() {
        store.addStoreDiscount(0.5);
        store.addTotalSumCondition(3);
        store.addTotalSumCondition(2);
        store.addAndDiscount();
        store.setFirstCondition(1,2);
        store.setSecondCondition(1,2);
        store.setThenDiscount(1,0);
        product.setQuantity(3);
        double price = store.calculatePrice(bag);
        assertEquals(3.0, price);
    }

    @Test
    public void testOrDiscount_BothSatisfied() {
        store.addStoreDiscount(0.5);
        store.addTotalSumCondition(1);
        store.addTotalSumCondition(2);
        store.addOrDiscount();
        store.setFirstCondition(1,2);
        store.setSecondCondition(1,2);
        store.setThenDiscount(1,0);
        product.setQuantity(3);
        double price = store.calculatePrice(bag);
        assertEquals(1.5, price);
    }

    @Test
    public void testOrDiscount_FirstSatisfied() {
        store.addStoreDiscount(0.5);
        store.addTotalSumCondition(1);
        store.addTotalSumCondition(3);
        store.addOrDiscount();
        store.setFirstCondition(1,2);
        store.setSecondCondition(1,2);
        store.setThenDiscount(1,0);
        product.setQuantity(3);
        double price = store.calculatePrice(bag);
        assertEquals(1.5, price);
    }

    @Test
    public void testOrDiscount_SecondSatisfied() {
        store.addStoreDiscount(0.5);
        store.addTotalSumCondition(3);
        store.addTotalSumCondition(2);
        store.addOrDiscount();
        store.setFirstCondition(1,2);
        store.setSecondCondition(1,2);
        store.setThenDiscount(1,0);
        product.setQuantity(3);
        double price = store.calculatePrice(bag);
        assertEquals(1.5, price);
    }

    @Test
    public void testXorDiscount_FirstSatisfied() {
        store.addStoreDiscount(0.5);
        store.addProductPercentageDiscount(1, 0.25);
        store.addProductCountCondition(1,1);
        store.addXorDiscount();
        store.setFirstDiscount(2,0);
        store.setSecondDiscount(1,0);
        store.setDeciderDiscount(0,1);
        double price = store.calculatePrice(bag);
        assertEquals(0.5, price);
    }

    @Test
    public void testXorDiscount_SecondSatisfied() {
        store.addStoreDiscount(0.5);
        store.addProductPercentageDiscount(1, 0.25);
        store.addProductCountCondition(1,1);
        store.addXorDiscount();
        store.setSecondDiscount(2,0);
        store.setFirstDiscount(1,0);
        store.setDeciderDiscount(0,1);
        double price = store.calculatePrice(bag);
        assertEquals(0.5, price);
    }

    @Test
    public void testXorDiscount_FirstDecider() {
        store.addStoreDiscount(0.5);
        store.addProductPercentageDiscount(0, 0.25);
        store.addProductCountCondition(0,1);
        store.addXorDiscount();
        store.setFirstDiscount(2,0);
        store.setSecondDiscount(1,0);
        store.setDeciderDiscount(0,1);
        double price = store.calculatePrice(bag);
        assertEquals(0.75, price);
    }

    @Test
    public void testXorDiscount_SecondDecider() {
        store.addStoreDiscount(0.5);
        store.addProductPercentageDiscount(1, 0.25);
        store.addProductCountCondition(0,1);
        store.addXorDiscount();
        store.setFirstDiscount(2,0);
        store.setSecondDiscount(1,0);
        store.setDeciderDiscount(0,1);
        double price = store.calculatePrice(bag);
        assertEquals(0.5, price);
    }
}
