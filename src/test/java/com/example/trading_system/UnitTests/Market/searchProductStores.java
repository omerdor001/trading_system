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

public class searchProductStores {
    private MarketFacadeImp marketFacade;
    private UserFacadeImp userFacadeImp;
    LocalDate birthdate = LocalDate.of(1990, 5, 15);

    @BeforeAll
    void setUpOnce() throws Exception {
        userFacadeImp = UserFacadeImp.getInstance();
        marketFacade = MarketFacadeImp.getInstance();
        userFacadeImp.enter(1);
        userFacadeImp.register( "testuser", "testpassword", birthdate);
        userFacadeImp.login("1", "testuser", "testpassword");
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
    void testSearchNameInStores_ValidInput() {
        String expected = "[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[keyword]}]";
        String actual = marketFacade.searchNameInStores("product1", null, null, null, null,null);
        assertEquals(expected, actual);
    }

    @Test
    void testSearchNameInStores_NoNameProvided() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                marketFacade.searchNameInStores(null, null, null, null, null,null));
        assertEquals("No name provided", exception.getMessage());
    }

    @Test
    void testSearchNameInStores_NoProductsAvailable() {
        String result = marketFacade.searchNameInStores("product3",null, null, null, null,null);
        assertEquals("{}", result);
    }



    @Test
    void testSearchCategoryInStore_ValidInput() {

        String result = marketFacade.searchCategoryInStores(Category.Sport, null, null, null,null);
        assertEquals("[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[keyword]}, {\"product_id\":2, \"store_name\":\"\", \"product_name\":\"product2\", \"product_description\":\"\", \"product_price\":8.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[]}]",
                result);
    }

    @Test
    void testSearchCategoryInStores_NoCategoryProvided() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                marketFacade.searchCategoryInStores(null, null, null, null,null));
        assertEquals("No category provided", exception.getMessage());
    }

    @Test
    void testSearchCategoryInStores_NoProductsAvailable() {
        String result = marketFacade.searchCategoryInStores(Category.Art,  null, null, null,null);
        assertEquals("{}", result);
    }

    @Test
    void testSearchKeywordsInStores_ValidInput() {
        String result = marketFacade.searchKeywordsInStores("keyword", null, null, null, null,null);
        assertEquals("[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[keyword]}]",
                result);

    }

    @Test
    void testSearchKeywordsInStores_NoKeywordsProvided() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                marketFacade.searchKeywordsInStores(null,  null, null, null, null,null));
        assertEquals("No keywords provided", exception.getMessage());
    }


    @Test
    void testSearchKeywordsInStores_NoProductsAvailable() {
        String result = marketFacade.searchKeywordsInStores("keyword3", null, null, null, null,null);
        assertEquals("{}", result);
    }

    @Test
    void testFilterProducts_AllParametersNull() {
        String expected = "[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[keyword]}]";
        String actual = marketFacade.searchNameInStores("product1",  null, null, null, null,null);
        assertEquals(expected, actual);
    }

    @Test
    void testFilterProducts_MinPriceOnly() {
        String actual = marketFacade.searchCategoryInStores(Category.Sport,  3.0, null, null,null);
        assertEquals("[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[keyword]}, {\"product_id\":2, \"store_name\":\"\", \"product_name\":\"product2\", \"product_description\":\"\", \"product_price\":8.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[]}]"
                , actual);
    }

    @Test
    void testFilterProducts_MaxPriceOnly() {
        String expected = "[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[keyword]}]";
        String actual = marketFacade.searchNameInStores("product1", null, 6.0, null, null,null);
        assertEquals(expected, actual);
    }

    @Test
    void testFilterProducts_MinRatingOnly() {
        String actual = marketFacade.searchCategoryInStores(Category.Sport, null, null, 3.0,null);
        assertEquals("[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[keyword]}, {\"product_id\":2, \"store_name\":\"\", \"product_name\":\"product2\", \"product_description\":\"\", \"product_price\":8.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[]}]"
                , actual);
    }

    @Test
    void testFilterProducts_MinPriceAndMaxPrice() {
        String expected = "[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[keyword]}]";
        String actual = marketFacade.searchNameInStores("product1",  2.0, 6.0, null, null,null);
        assertEquals(expected, actual);
    }

}