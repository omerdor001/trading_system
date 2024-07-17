package com.example.trading_system.UnitTests.Market;

import com.example.trading_system.domain.stores.ProductInSaleDTO;
import com.example.trading_system.domain.stores.Store;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PurchasePolicyUnitTests {
    Store store;
    List<ProductInSaleDTO> items;

    @BeforeEach
    void setUp() {
        store = new Store("store1", "", "user", 5.0);
        items = new LinkedList<>();

        ProductInSaleDTO product = new ProductInSaleDTO();
        product.setStoreId("store1");
        product.setId(0);
        product.setPrice(1);
        product.setQuantity(1);
        product.setCategory(1);
        items.add(product);
        store.addProduct(product.getId(), "Product1", "Description1", product.getPrice(), product.getQuantity(), 4.5, product.getCategory(), List.of("keyword1"));
    }

    @Test
    public void testValidatePurchasePolicies_AllSatisfied() {
        store.addPurchasePolicyByAge(18, 1);
        store.addPurchasePolicyByShoppingCartMinProducts(1); // Replace addPurchasePolicyByCategory with a relevant policy
        boolean result = store.validatePurchasePolicies(items, 20);
        assertTrue(result);
    }


    @Test
    public void testValidatePurchasePolicies_NoPolicies() {
        boolean result = store.validatePurchasePolicies(items, 20);
        assertTrue(result);
    }

    @Test
    public void testValidatePurchasePolicies_AgeNotSatisfied() {
        store.addPurchasePolicyByAge(21, 1);
        boolean result = store.validatePurchasePolicies(items, 20);
        assertFalse(result);
    }

    @Test
    public void testValidatePurchasePolicies_DateSatisfied() {
        store.addPurchasePolicyByDate(LocalDateTime.now().minusDays(1));
        boolean result = store.validatePurchasePolicies(items, 20);
        assertTrue(result);
    }

    @Test
    public void testValidatePurchasePolicies_DateNotSatisfied() {
        store.addPurchasePolicyByDate(LocalDateTime.now().plusDays(1));
        boolean result = store.validatePurchasePolicies(items, 20);
        assertFalse(result);
    }

    @Test
    public void testValidatePurchasePolicies_ProductAndDateSatisfied() {
        store.addPurchasePolicyByProductAndDate(0, LocalDateTime.now().minusDays(1));
        boolean result = store.validatePurchasePolicies(items, 20);
        assertTrue(result);
    }

    @Test
    public void testValidatePurchasePolicies_ProductAndDateNotSatisfied() {
        store.addPurchasePolicyByProductAndDate(0, LocalDateTime.now().plusDays(1));
        boolean result = store.validatePurchasePolicies(items, 20);
        assertFalse(result);
    }

    @Test
    public void testValidatePurchasePolicies_ShoppingCartMaxProductsUnitSatisfied() {
        store.addPurchasePolicyByShoppingCartMaxProductsUnit(0, 5);
        boolean result = store.validatePurchasePolicies(items, 20);
        assertTrue(result);
    }

    @Test
    public void testValidatePurchasePolicies_ShoppingCartMaxProductsUnitNotSatisfied() {
        ProductInSaleDTO product = new ProductInSaleDTO();
        product.setStoreId("store1");
        product.setId(1);
        product.setPrice(1);
        product.setQuantity(2);
        product.setCategory(1);
        items.add(product);
        store.addProduct(product.getId(), "Product2", "Description1", product.getPrice(), product.getQuantity(), 4.5, product.getCategory(), List.of("keyword1"));
        store.addPurchasePolicyByShoppingCartMaxProductsUnit(1, 1);
        boolean result = store.validatePurchasePolicies(items, 20);
        assertFalse(result);
    }

    @Test
    public void testValidatePurchasePolicies_ShoppingCartMinProductsSatisfied() {
        store.addPurchasePolicyByShoppingCartMinProducts(1);
        boolean result = store.validatePurchasePolicies(items, 20);
        assertTrue(result);
    }

    @Test
    public void testValidatePurchasePolicies_ShoppingCartMinProductsNotSatisfied() {
        store.addPurchasePolicyByShoppingCartMinProducts(2);
        boolean result = store.validatePurchasePolicies(items, 20);
        assertFalse(result);
    }

    @Test
    public void testValidatePurchasePolicies_ShoppingCartMinProductsUnitSatisfied() {
        store.addPurchasePolicyByShoppingCartMinProductsUnit(0, 1);
        boolean result = store.validatePurchasePolicies(items, 20);
        assertTrue(result);
    }

    @Test
    public void testValidatePurchasePolicies_ShoppingCartMinProductsUnitNotSatisfied() {
        store.addPurchasePolicyByShoppingCartMinProductsUnit(0, 2);
        boolean result = store.validatePurchasePolicies(items, 20);
        assertFalse(result);
    }

    @Test
    public void testGetPurchasePoliciesInfo_EmptyPolicies() {
        String result = store.getPurchasePoliciesInfo();
        assertEquals("[  ]", result);
    }

    @Test
    public void testGetPurchasePoliciesInfo_WithPolicies() {
        store.addPurchasePolicyByAge(18, 1);
        store.addPurchasePolicyByShoppingCartMinProducts(1);
        String result = store.getPurchasePoliciesInfo();
        assertTrue(result.contains("age"));
    }

    @Test
    public void testAddPurchasePolicyByAge_Valid() {
        store.addPurchasePolicyByAge(18, 1);
        assertEquals(1, store.getPurchasePolicies().size());
    }

    @Test
    public void testAddPurchasePolicyByAge_InvalidAge() {
        assertThrows(IllegalArgumentException.class, () -> store.addPurchasePolicyByAge(-1, 1));
    }

    @Test
    public void testAddPurchasePolicyByAge_InvalidCategory() {
        assertThrows(IllegalArgumentException.class, () -> store.addPurchasePolicyByAge(18, -1));
    }

    @Test
    public void testAddPurchasePolicyByCategoryAndDate_Valid() {
        store.addPurchasePolicyByCategoryAndDate(1, LocalDateTime.now());
        assertEquals(1, store.getPurchasePolicies().size());
    }

    @Test
    public void testAddPurchasePolicyByCategoryAndDate_InvalidCategory() {
        assertThrows(IllegalArgumentException.class, () -> store.addPurchasePolicyByCategoryAndDate(-1, LocalDateTime.now()));
    }

    @Test
    public void testAddPurchasePolicyByCategoryAndDate_NullDate() {
        assertThrows(IllegalArgumentException.class, () -> store.addPurchasePolicyByCategoryAndDate(1, null));
    }

    @Test
    public void testAddPurchasePolicyByDate_Valid() {
        store.addPurchasePolicyByDate(LocalDateTime.now());
        assertEquals(1, store.getPurchasePolicies().size());
    }

    @Test
    public void testAddPurchasePolicyByDate_NullDate() {
        assertThrows(IllegalArgumentException.class, () -> store.addPurchasePolicyByDate(null));
    }

    @Test
    public void testAddPurchasePolicyByProductAndDate_Valid() {
        store.addPurchasePolicyByProductAndDate(0, LocalDateTime.now());
        assertEquals(1, store.getPurchasePolicies().size());
    }

    @Test
    public void testAddPurchasePolicyByProductAndDate_InvalidProductId() {
        assertThrows(IllegalArgumentException.class, () -> store.addPurchasePolicyByProductAndDate(-1, LocalDateTime.now()));
    }

    @Test
    public void testAddPurchasePolicyByProductAndDate_NullDate() {
        assertThrows(IllegalArgumentException.class, () -> store.addPurchasePolicyByProductAndDate(0, null));
    }

    @Test
    public void testAddPurchasePolicyByShoppingCartMaxProductsUnit_Valid() {
        store.addPurchasePolicyByShoppingCartMaxProductsUnit(0, 5);
        assertEquals(1, store.getPurchasePolicies().size());
    }

    @Test
    public void testAddPurchasePolicyByShoppingCartMaxProductsUnit_InvalidProductId() {
        assertThrows(IllegalArgumentException.class, () -> store.addPurchasePolicyByShoppingCartMaxProductsUnit(-1, 5));
    }

    @Test
    public void testAddPurchasePolicyByShoppingCartMaxProductsUnit_InvalidQuantity() {
        assertThrows(IllegalArgumentException.class, () -> store.addPurchasePolicyByShoppingCartMaxProductsUnit(0, 0));
    }

    @Test
    public void testAddPurchasePolicyByShoppingCartMinProducts_Valid() {
        store.addPurchasePolicyByShoppingCartMinProducts(1);
        assertEquals(1, store.getPurchasePolicies().size());
    }

    @Test
    public void testAddPurchasePolicyByShoppingCartMinProducts_InvalidQuantity() {
        assertThrows(IllegalArgumentException.class, () -> store.addPurchasePolicyByShoppingCartMinProducts(0));
    }

    @Test
    public void testAddPurchasePolicyByShoppingCartMinProductsUnit_Valid() {
        store.addPurchasePolicyByShoppingCartMinProductsUnit(0, 1);
        assertEquals(1, store.getPurchasePolicies().size());
    }

    @Test
    public void testAddPurchasePolicyByShoppingCartMinProductsUnit_InvalidProductId() {
        assertThrows(IllegalArgumentException.class, () -> store.addPurchasePolicyByShoppingCartMinProductsUnit(-1, 1));
    }

    @Test
    public void testAddPurchasePolicyByShoppingCartMinProductsUnit_InvalidQuantity() {
        assertThrows(IllegalArgumentException.class, () -> store.addPurchasePolicyByShoppingCartMinProductsUnit(0, 0));
    }

    @Test
    public void testSetFirstPurchasePolicy_Valid() {
        store.addOrPurchasePolicy();
        store.addPurchasePolicyByShoppingCartMinProducts(1);
        store.setFirstPurchasePolicy(0, 1);
        // Verify state indirectly
        assertEquals(1, store.getPurchasePolicies().size());
    }

    @Test
    public void testSetFirstPurchasePolicy_InvalidIndexesSame() {
        store.addPurchasePolicyByAge(18, 1);
        store.addPurchasePolicyByShoppingCartMinProducts(1);
        assertThrows(IllegalArgumentException.class, () -> store.setFirstPurchasePolicy(0, 0));
    }

    @Test
    public void testSetSecondPurchasePolicy_Valid() {
        store.addOrPurchasePolicy();
        store.addPurchasePolicyByShoppingCartMinProducts(1);
        store.setSecondPurchasePolicy(0, 1);
        // Verify state indirectly
        assertEquals(1, store.getPurchasePolicies().size());
    }

    @Test
    public void testSetSecondPurchasePolicy_InvalidIndexesSame() {
        store.addPurchasePolicyByAge(18, 1);
        store.addPurchasePolicyByShoppingCartMinProducts(1);
        assertThrows(IllegalArgumentException.class, () -> store.setSecondPurchasePolicy(0, 0));
    }

    @Test
    public void testSetPurchasePolicyProductId_InvalidProductId() {
        store.addPurchasePolicyByAge(18, 1);
        assertThrows(IllegalArgumentException.class, () -> store.setPurchasePolicyProductId(0, -1));
    }


    @Test
    public void testSetPurchasePolicyNumOfQuantity_InvalidQuantity() {
        store.addPurchasePolicyByAge(18, 1);
        assertThrows(IllegalArgumentException.class, () -> store.setPurchasePolicyNumOfQuantity(0, 0));
    }

    @Test
    public void testSetPurchasePolicyDateTime_NullDate() {
        store.addPurchasePolicyByAge(18, 1);
        assertThrows(IllegalArgumentException.class, () -> store.setPurchasePolicyDateTime(0, null));
    }

    @Test
    public void testSetPurchasePolicyAge_InvalidAge() {
        store.addPurchasePolicyByAge(18, 1);
        assertThrows(IllegalArgumentException.class, () -> store.setPurchasePolicyAge(0, 0));
    }

    @Test
    public void testSetPurchasePolicyCategory_InvalidCategory() {
        store.addPurchasePolicyByAge(18, 1);
        assertThrows(IllegalArgumentException.class, () -> store.setPurchasePolicyCategory(0, -1));
    }

    @Test
    public void testRemovePurchasePolicy_Valid() {
        store.addPurchasePolicyByAge(18, 1);
        store.removePurchasePolicy(0);
        assertTrue(store.getPurchasePolicies().isEmpty());
    }

    @Test
    public void testRemovePurchasePolicy_InvalidIndex() {
        store.addPurchasePolicyByAge(18, 1);
        assertThrows(IllegalArgumentException.class, () -> store.removePurchasePolicy(1));
    }

}
