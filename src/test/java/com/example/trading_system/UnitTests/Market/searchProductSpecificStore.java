package com.example.trading_system.UnitTests.Market;

import com.example.trading_system.domain.stores.*;
import com.example.trading_system.domain.users.UserFacadeImp;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class searchProductSpecificStore {
    private MarketFacadeImp marketFacade;
    private UserFacadeImp userFacadeImp;
    private StoreMemoryRepository storeMemoryRepository;
    LocalDate birthdate = LocalDate.of(1990, 5, 15);

    @BeforeAll
    void setUpOnce() throws Exception {
        userFacadeImp = UserFacadeImp.getInstance();
        marketFacade = MarketFacadeImp.getInstance();
        storeMemoryRepository = StoreMemoryRepository.getInstance();
        userFacadeImp.enter(1);
        userFacadeImp.register(1, "testuser", "testpassword", birthdate);
        userFacadeImp.login(1, "testuser", "testpassword");
        Store store1 = mock(Store.class);
        Store store2 = mock(Store.class);
        userFacadeImp.openStore("testuser", "store1", "description", new StorePolicy());
        userFacadeImp.openStore("testuser", "store2", "description", new StorePolicy());
        when(store1.toString()).thenReturn("Store 1");
        when(store1.toString()).thenReturn("Store 2");
        marketFacade.addProduct("testuser", 1, "store1", "product1", "", 5, 5, 5, 1, new ArrayList<>(List.of("keyword")));
        marketFacade.addProduct("testuser", 2, "store1", "product2", "", 8, 5, 5, 1, new ArrayList<>());

    }

    @Test
    void testSearchNameInStore_ValidInput() {
        String expected = "[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[keyword]}]";
        String actual = marketFacade.searchNameInStore("product1", "store1", null, null, null, null);
        assertEquals(expected, actual);
    }

    @Test
    void testSearchNameInStore_NoNameProvided() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                marketFacade.searchNameInStore(null, "store1", null, null, null, null));
        assertEquals("No name provided", exception.getMessage());
    }

    @Test
    void testSearchNameInStore_NoProductsAvailable() {
        String result = marketFacade.searchNameInStore("product1", "store2", null, null, null, null);
        assertEquals("{}", result);
    }

    @Test
    void testSearchNameInStore_NullStoreName() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                marketFacade.searchNameInStore("product1", null, null, null, null, null));
        assertEquals("No store name provided", exception.getMessage());
    }

    @Test
    void testSearchCategoryInStore_ValidInput() {

        String result = marketFacade.searchCategoryInStore(Category.Sport, "store1", null, null, null);
        assertEquals("[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[keyword]}, {\"product_id\":2, \"store_name\":\"\", \"product_name\":\"product2\", \"product_description\":\"\", \"product_price\":8.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[]}]",
                result);
    }

    @Test
    void testSearchCategoryInStore_NoCategoryProvided() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                marketFacade.searchCategoryInStore(null, "store1", null, null, null));
        assertEquals("No category provided", exception.getMessage());
    }

    @Test
    void testSearchCategoryInStore_NoStoreNameProvided() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                marketFacade.searchCategoryInStore(Category.Art, null, null, null, null));
        assertEquals("No store name provided", exception.getMessage());
    }

    @Test
    void testSearchCategoryInStore_NoProductsAvailable() {
        String result = marketFacade.searchCategoryInStore(Category.Sport, "store2", null, null, null);
        assertEquals("{}", result);
    }

    @Test
    void testSearchKeywordsInStore_ValidInput() {
        String result = marketFacade.searchKeywordsInStore("keyword", "store1", null, null, null, null);
        assertEquals("[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[keyword]}]",
                result);

    }

    @Test
    void testSearchKeywordsInStore_NoKeywordsProvided() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                marketFacade.searchKeywordsInStore(null, "store1", null, null, null, null));
        assertEquals("No keywords provided", exception.getMessage());
    }

    @Test
    void testSearchKeywordsInStore_NoStoreNameProvided() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                marketFacade.searchKeywordsInStore("keyword", null, null, null, null, null));
        assertEquals("No store name provided", exception.getMessage());
    }

    @Test
    void testSearchKeywordsInStore_NoProductsAvailable() {
        String result = marketFacade.searchKeywordsInStore("keyword", "store2", null, null, null, null);
        assertEquals("{}", result);
    }

    @Test
    void testFilterProducts_AllParametersNull() {
        String expected = "[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[keyword]}]";
        String actual = marketFacade.searchNameInStore("product1", "store1", null, null, null, null);
        assertEquals(expected, actual);
    }

    @Test
    void testFilterProducts_MinPriceOnly() {
        String actual = marketFacade.searchCategoryInStore(Category.Sport, "store1", 3.0, null, null);
        assertEquals("[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[keyword]}, {\"product_id\":2, \"store_name\":\"\", \"product_name\":\"product2\", \"product_description\":\"\", \"product_price\":8.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[]}]"
                , actual);
    }

    @Test
    void testFilterProducts_MaxPriceOnly() {
        String expected = "[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[keyword]}]";
        String actual = marketFacade.searchNameInStore("product1", "store1", null, 6.0, null, null);
        assertEquals(expected, actual);
    }

    @Test
    void testFilterProducts_MinRatingOnly() {
        String actual = marketFacade.searchCategoryInStore(Category.Sport, "store1", null, null, 3.0);
        assertEquals("[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[keyword]}, {\"product_id\":2, \"store_name\":\"\", \"product_name\":\"product2\", \"product_description\":\"\", \"product_price\":8.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[]}]"
                , actual);
    }

    @Test
    void testFilterProducts_MinPriceAndMaxPrice() {
        String expected = "[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[keyword]}]";
        String actual = marketFacade.searchNameInStore("product1", "store1", 2.0, 6.0, null, null);
        assertEquals(expected, actual);
    }

}