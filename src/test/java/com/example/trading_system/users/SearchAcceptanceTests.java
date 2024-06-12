package com.example.trading_system.users;

import com.example.trading_system.domain.stores.Category;
import com.example.trading_system.service.MarketService;
import com.example.trading_system.service.TradingSystem;
import com.example.trading_system.service.TradingSystemImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class SearchAcceptanceTests {
/* //TODO FIX ME
    private TradingSystem tradingSystem;
    private MarketService marketServiceMock;

    @BeforeEach
    public void setUp() {
        tradingSystem = TradingSystemImp.getInstance();
        marketServiceMock = mock(MarketService.class);
    }

    @Test
    public void testSearchNameInStore_Success() {
        String storeName = "store1";
        String name = "product1";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        Category category = Category.Food;

        String expectedResponse = "mocked response";
        when(marketServiceMock.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category.getIntValue())).thenReturn(expectedResponse);

        String result = tradingSystem.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category.getIntValue()).getBody();

        assertEquals(expectedResponse, result);
        verify(marketServiceMock, times(1)).searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category.getIntValue());
    }

    @Test
    public void testSearchCategoryInStore_Success() {
        String storeName = "store1";
        Category category = Category.Food;
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;

        String expectedResponse = "mocked response";
        when(marketServiceMock.searchCategoryInStore(category.getIntValue(), storeName, minPrice, maxPrice, minRating)).thenReturn(expectedResponse);

        String result = tradingSystem.searchCategoryInStore(category.getIntValue(), storeName, minPrice, maxPrice, minRating).getBody();

        assertEquals(expectedResponse, result);
        verify(marketServiceMock, times(1)).searchCategoryInStore(category.getIntValue(), storeName, minPrice, maxPrice, minRating);
    }

    @Test
    public void testSearchKeywordsInStore_Success() {
        String storeName = "store1";
        String keyWords = "apple";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        Category category = Category.Food;

        String expectedResponse = "mocked response";
        when(marketServiceMock.searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category.getIntValue())).thenReturn(expectedResponse);

        String result = tradingSystem.searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category.getIntValue()).getBody();

        assertEquals(expectedResponse, result);
        verify(marketServiceMock, times(1)).searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category.getIntValue());
    }

    @Test
    public void testSearchNameInStore_Exception() {
        String storeName = "store1";
        String name = "product1";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        Category category = Category.Food;

        when(marketServiceMock.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category.getIntValue())).thenThrow(new RuntimeException("Mocked exception"));

        ResponseEntity<String> result = tradingSystem.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category.getIntValue());

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        verify(marketServiceMock, times(1)).searchNameInStore(name, storeName, minPrice, maxPrice, minRating,category.getIntValue());
    }

    @Test
    public void testSearchCategoryInStore_Exception() {
        String storeName = "store1";
        Category category = Category.Food;
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;

        when(marketServiceMock.searchCategoryInStore(category.getIntValue(), storeName, minPrice, maxPrice, minRating)).thenThrow(new RuntimeException("Mocked exception"));

        ResponseEntity<String> result = tradingSystem.searchCategoryInStore(category.getIntValue(), storeName, minPrice, maxPrice, minRating);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        verify(marketServiceMock, times(1)).searchCategoryInStore(category.getIntValue(), storeName, minPrice, maxPrice, minRating);
    }

    @Test
    public void testSearchKeywordsInStore_Exception() {
        String storeName = "store1";
        String keyWords = "apple";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        Category category = Category.Food;

        when(marketServiceMock.searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category.getIntValue())).thenThrow(new RuntimeException("Mocked exception"));

        ResponseEntity<String> result = tradingSystem.searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category.getIntValue());

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        verify(marketServiceMock, times(1)).searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category.getIntValue());
    }

    @Test
    public void testSearchNameInStore_FilterByMinPrice() {
        String storeName = "store1";
        String name = "product";
        Double minPrice = 30.0;
        Double maxPrice = 40.0;
        Double minRating = null;
        Category category = null;

        String product1 = "{\"product_id\":1, \"store_name\":\"store1\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":25.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"foof\"]}";
        String product2 = "{\"product_id\":2, \"store_name\":\"store1\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":35.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"food\"]}";

        String expectedResponse = "[" + product2 + "]";
        when(marketServiceMock.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category.getIntValue())).thenReturn(expectedResponse);

        String result = tradingSystem.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category.getIntValue()).getBody();

        assertEquals(expectedResponse, result);
        verify(marketServiceMock, times(1)).searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category.getIntValue());
    }

    @Test
    public void testSearchNameInStore_FilterByMaxPrice() {
        String storeName = "store1";
        String name = "product";
        Double minPrice = 30.0;
        Double maxPrice = 40.0;
        Double minRating = null;
        Category category = null;

        String product1 = "{\"product_id\":1, \"store_name\":\"store1\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":29.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"foof\"]}";
        String product2 = "{\"product_id\":2, \"store_name\":\"store1\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":35.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"food\"]}";

        String expectedResponse = "[" + product2 + "]";
        when(marketServiceMock.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category.getIntValue())).thenReturn(expectedResponse);

        String result = tradingSystem.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category.getIntValue()).getBody();

        assertEquals(expectedResponse, result);
        verify(marketServiceMock, times(1)).searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category.getIntValue());
    }

    @Test
    public void testSearchCategoryInStore_FilterByRating() {
        String storeName = "store1";
        Category category = Category.Food;
        Double minPrice = null;
        Double maxPrice = null;
        Double minRating = 4.5;

        String product1 = "{\"product_id\":1, \"store_name\":\"store1\", \"product_name\":\"product1\", \"product_description\":\"desc\", \"product_price\":25.0, \"product_quantity\":5, \"rating\":5.0, \"category\":\"Food\", \"keyWords\":[\"Food\"]}";
        String product2 = "{\"product_id\":2, \"store_name\":\"store1\", \"product_name\":\"product2\", \"product_description\":\"desc\", \"product_price\":35.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"Food\"]}";

        String expectedResponse = "[" + product1 + "]";
        when(marketServiceMock.searchCategoryInStore(category.getIntValue(), storeName, minPrice, maxPrice, minRating)).thenReturn(expectedResponse);

        String result = tradingSystem.searchCategoryInStore(category.getIntValue(), storeName, minPrice, maxPrice, minRating).getBody();

        assertEquals(expectedResponse, result);
        verify(marketServiceMock, times(1)).searchCategoryInStore(category.getIntValue(), storeName, minPrice, maxPrice, minRating);
    }

    @Test
    public void testSearchKeywordsInStore_FilterByCategory() {
        String storeName = "store1";
        String keyWords = "apple";
        Double minPrice = null;
        Double maxPrice = null;
        Double minRating = null;
        Category category = Category.Food;

        String product1 = "{\"product_id\":1, \"store_name\":\"store1\", \"product_name\":\"product1\", \"product_description\":\"desc\", \"product_price\":25.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"Food\"]}";
        String product2 = "{\"product_id\":2, \"store_name\":\"store1\", \"product_name\":\"product2\", \"product_description\":\"desc\", \"product_price\":35.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"Food\"]}";

        String expectedResponse = "[" + product1 + "," + product2 + "]";
        when(marketServiceMock.searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category.getIntValue())).thenReturn(expectedResponse);

        String result = tradingSystem.searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category.getIntValue()).getBody();

        assertEquals(expectedResponse, result);
        verify(marketServiceMock, times(1)).searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category.getIntValue());
    }

    @Test
    public void testSearchNameInStore_FilterByAllCriteria() {
        String storeName = "store1";
        String name = "product";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        Category category = Category.Food;

        String product1 = "{\"product_id\":1, \"store_name\":\"store1\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":20.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"Food\"]}";
        String product2 = "{\"product_id\":2, \"store_name\":\"store1\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":40.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"Food\"]}";

        String expectedResponse = "[" + product1 + "," + product2 + "]";
        when(marketServiceMock.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category.getIntValue())).thenReturn(expectedResponse);

        String result = tradingSystem.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category.getIntValue()).getBody();

        assertEquals(expectedResponse, result);
        verify(marketServiceMock, times(1)).searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category.getIntValue());
    }*/
/*
    @Test
    public void testSearchNameInStores_Success() {
        String name = "product1";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        Category category = Category.Food;

        String expectedResponse = "mocked response";
        when(marketServiceMock.searchNameInStores(name, minPrice, maxPrice, minRating, category)).thenReturn(expectedResponse);

        String result = tradingSystem.searchNameInStores(name, minPrice, maxPrice, minRating, category).getBody();

        assertEquals(expectedResponse, result);
        verify(marketServiceMock, times(1)).searchNameInStores(name, minPrice, maxPrice, minRating, category);
    }

    @Test
    public void testSearchCategoryInStores_Success() {
        Category category = Category.Food;
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;

        String expectedResponse = "mocked response";
        when(marketServiceMock.searchCategoryInStores(category, minPrice, maxPrice, minRating)).thenReturn(expectedResponse);

        String result = tradingSystem.searchCategoryInStores(category, minPrice, maxPrice, minRating).getBody();

        assertEquals(expectedResponse, result);
        verify(marketServiceMock, times(1)).searchCategoryInStores(category, minPrice, maxPrice, minRating);
    }

    @Test
    public void testSearchKeywordsInStores_Success() {
        String keyWords = "apple";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        Category category = Category.Food;

        String expectedResponse = "mocked response";
        when(marketServiceMock.searchKeywordsInStores(keyWords, minPrice, maxPrice, minRating, category)).thenReturn(expectedResponse);

        String result = tradingSystem.searchKeywordsInStores(keyWords, minPrice, maxPrice, minRating, category).getBody();

        assertEquals(expectedResponse, result);
        verify(marketServiceMock, times(1)).searchKeywordsInStores(keyWords, minPrice, maxPrice, minRating, category);
    }

    @Test
    public void testSearchNameInStores_Exception() {
        String name = "product1";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        Category category = Category.Food;

        when(marketServiceMock.searchNameInStores(name, minPrice, maxPrice, minRating, category)).thenThrow(new RuntimeException("Mocked exception"));

        ResponseEntity<String> result = tradingSystem.searchNameInStores(name, minPrice, maxPrice, minRating, category);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        verify(marketServiceMock, times(1)).searchNameInStores(name, minPrice, maxPrice, minRating, category);
    }

    @Test
    public void testSearchCategoryInStores_Exception() {
        Category category = Category.Food;
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;

        when(marketServiceMock.searchCategoryInStores(category, minPrice, maxPrice, minRating)).thenThrow(new RuntimeException("Mocked exception"));

        ResponseEntity<String> result = tradingSystem.searchCategoryInStores(category, minPrice, maxPrice, minRating);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        verify(marketServiceMock, times(1)).searchCategoryInStores(category, minPrice, maxPrice, minRating);
    }

    @Test
    public void testSearchKeywordsInStores_Exception() {
        String keyWords = "apple";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        Category category = Category.Food;

        when(marketServiceMock.searchKeywordsInStores(keyWords, minPrice, maxPrice, minRating, category)).thenThrow(new RuntimeException("Mocked exception"));

        ResponseEntity<String> result = tradingSystem.searchKeywordsInStores(keyWords, minPrice, maxPrice, minRating, category);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        verify(marketServiceMock, times(1)).searchKeywordsInStores(keyWords, minPrice, maxPrice, minRating, category);
    }*/
}