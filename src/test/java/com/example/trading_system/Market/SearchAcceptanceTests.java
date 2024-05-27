package com.example.trading_system.Market;

import com.example.trading_system.domain.stores.Category;
import com.example.trading_system.domain.stores.MarketFacade;
import com.example.trading_system.service.TradingSystem;
import com.example.trading_system.service.TradingSystemImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class SearchAcceptanceTests {

    private TradingSystem marketService;
    private MarketFacade marketFacadeMock;
    private static final int ADMIN_ID = 1;
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "password";
    private static final LocalDate ADMIN_BIRTHDATE = LocalDate.of(1990, 1, 1);

    @BeforeEach
    public void setUp() {
        marketService = TradingSystemImp.getInstance();
        marketFacadeMock = mock(MarketFacade.class);

        // Register an admin and open the system
        marketService.register(ADMIN_ID, ADMIN_USERNAME, ADMIN_PASSWORD, ADMIN_BIRTHDATE);
        marketService.openSystem();
    }

    @Test
    public void testSearchNameInStore_Success() {
        String storeName = "store1";
        String name = "product1";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        Category category = Category.Food;

        ResponseEntity<String> expectedResponse = new ResponseEntity<>("mocked response", HttpStatus.OK);
        when(marketFacadeMock.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category)).thenReturn(expectedResponse.getBody());

        ResponseEntity<String> result = marketService.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category);

        assertEquals(expectedResponse, result);
        verify(marketFacadeMock, times(1)).searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category);
    }

    @Test
    public void testSearchCategoryInStore_Success() {
        String storeName = "store1";
        Category category = Category.Food;
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;

        ResponseEntity<String> expectedResponse = new ResponseEntity<>("mocked response", HttpStatus.OK);
        when(marketFacadeMock.searchCategoryInStore(category, storeName, minPrice, maxPrice, minRating)).thenReturn(expectedResponse.getBody());

        ResponseEntity<String> result = marketService.searchCategoryInStore(category, storeName, minPrice, maxPrice, minRating);

        assertEquals(expectedResponse, result);
        verify(marketFacadeMock, times(1)).searchCategoryInStore(category, storeName, minPrice, maxPrice, minRating);
    }

    @Test
    public void testSearchKeywordsInStore_Success() {
        String storeName = "store1";
        String keyWords = "apple";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        Category category = Category.Food;

        ResponseEntity<String> expectedResponse = new ResponseEntity<>("mocked response", HttpStatus.OK);
        when(marketFacadeMock.searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category)).thenReturn(expectedResponse.getBody());

        ResponseEntity<String> result = marketService.searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category);

        assertEquals(expectedResponse, result);
        verify(marketFacadeMock, times(1)).searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category);
    }

    @Test
    public void testSearchNameInStore_Exception() {
        String storeName = "store1";
        String name = "product1";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        Category category = Category.Food;

        when(marketFacadeMock.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category)).thenThrow(new RuntimeException("Mocked exception"));

        ResponseEntity<String> result = marketService.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        verify(marketFacadeMock, times(1)).searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category);
    }

    @Test
    public void testSearchCategoryInStore_Exception() {
        String storeName = "store1";
        Category category = Category.Food;
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;

        when(marketFacadeMock.searchCategoryInStore(category, storeName, minPrice, maxPrice, minRating)).thenThrow(new RuntimeException("Mocked exception"));

        ResponseEntity<String> result = marketService.searchCategoryInStore(category, storeName, minPrice, maxPrice, minRating);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        verify(marketFacadeMock, times(1)).searchCategoryInStore(category, storeName, minPrice, maxPrice, minRating);
    }

    @Test
    public void testSearchKeywordsInStore_Exception() {
        String storeName = "store1";
        String keyWords = "apple";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        Category category = Category.Food;

        when(marketFacadeMock.searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category)).thenThrow(new RuntimeException("Mocked exception"));

        ResponseEntity<String> result = marketService.searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        verify(marketFacadeMock, times(1)).searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category);
    }

    @Test
    public void testSearchNameInStore_FilterByMinPrice() {
        String storeName = "store1";
        String name = "product";
        Double minPrice = 30.0;
        Double maxPrice = 40.0;
        Double minRating = null;
        Category category = null;

        String product1 = "{\"product_id\":1, \"store_name\":\"store1\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":25.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"food\"]}";
        String product2 = "{\"product_id\":2, \"store_name\":\"store1\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":35.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"food\"]}";

        String expectedResponse = "[" + product2 + "]";
        when(marketFacadeMock.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category)).thenReturn(expectedResponse);

        ResponseEntity<String> result = marketService.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResponse, result.getBody());
        verify(marketFacadeMock, times(1)).searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category);
    }

    @Test
    public void testSearchNameInStore_FilterByMaxPrice() {
        String storeName = "store1";
        String name = "product";
        Double minPrice = 30.0;
        Double maxPrice = 40.0;
        Double minRating = null;
        Category category = null;

        String product1 = "{\"product_id\":1, \"store_name\":\"store1\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":29.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"food\"]}";
        String product2 = "{\"product_id\":2, \"store_name\":\"store1\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":35.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"food\"]}";

        String expectedResponse = "[" + product1 + "]";
        when(marketFacadeMock.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category)).thenReturn(expectedResponse);

        ResponseEntity<String> result = marketService.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResponse, result.getBody());
        verify(marketFacadeMock, times(1)).searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category);
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
        when(marketFacadeMock.searchCategoryInStore(category, storeName, minPrice, maxPrice, minRating)).thenReturn(expectedResponse);

        ResponseEntity<String> result = marketService.searchCategoryInStore(category, storeName, minPrice, maxPrice, minRating);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResponse, result.getBody());
        verify(marketFacadeMock, times(1)).searchCategoryInStore(category, storeName, minPrice, maxPrice, minRating);
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
        when(marketFacadeMock.searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category)).thenReturn(expectedResponse);

        ResponseEntity<String> result = marketService.searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResponse, result.getBody());
        verify(marketFacadeMock, times(1)).searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category);
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
        when(marketFacadeMock.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category)).thenReturn(expectedResponse);

        ResponseEntity<String> result = marketService.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResponse, result.getBody());
        verify(marketFacadeMock, times(1)).searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category);
    }

    ///////////////////////////tests for searching in stores - without specific focus

    @Test
    public void testSearchNameInStores_Success() {
        String name = "product1";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        Category category = Category.Food;

        ResponseEntity<String> expectedResponse = new ResponseEntity<>("mocked response", HttpStatus.OK);
        when(marketFacadeMock.searchNameInStores(name, minPrice, maxPrice, minRating, category)).thenReturn(expectedResponse.getBody());

        ResponseEntity<String> result = marketService.searchNameInStores(name, minPrice, maxPrice, minRating, category);

        assertEquals(expectedResponse, result);
        verify(marketFacadeMock, times(1)).searchNameInStores(name, minPrice, maxPrice, minRating, category);
    }

    @Test
    public void testSearchCategoryInStores_Success() {
        Category category = Category.Food;
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;

        ResponseEntity<String> expectedResponse = new ResponseEntity<>("mocked response", HttpStatus.OK);
        when(marketFacadeMock.searchCategoryInStores(category, minPrice, maxPrice, minRating)).thenReturn(expectedResponse.getBody());

        ResponseEntity<String> result = marketService.searchCategoryInStores(category, minPrice, maxPrice, minRating);

        assertEquals(expectedResponse, result);
        verify(marketFacadeMock, times(1)).searchCategoryInStores(category, minPrice, maxPrice, minRating);
    }

    @Test
    public void testSearchKeywordsInStores_Success() {
        String keyWords = "apple";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        Category category = Category.Food;

        ResponseEntity<String> expectedResponse = new ResponseEntity<>("mocked response", HttpStatus.OK);
        when(marketFacadeMock.searchKeywordsInStores(keyWords, minPrice, maxPrice, minRating, category)).thenReturn(expectedResponse.getBody());

        ResponseEntity<String> result = marketService.searchKeywordsInStores(keyWords, minPrice, maxPrice, minRating, category);

        assertEquals(expectedResponse, result);
        verify(marketFacadeMock, times(1)).searchKeywordsInStores(keyWords, minPrice, maxPrice, minRating, category);
    }

    @Test
    public void testSearchNameInStores_Exception() {
        String name = "product1";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        Category category = Category.Food;

        when(marketFacadeMock.searchNameInStores(name, minPrice, maxPrice, minRating, category)).thenThrow(new RuntimeException("Mocked exception"));

        ResponseEntity<String> result = marketService.searchNameInStores(name, minPrice, maxPrice, minRating, category);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        verify(marketFacadeMock, times(1)).searchNameInStores(name, minPrice, maxPrice, minRating, category);
    }

    @Test
    public void testSearchCategoryInStores_Exception() {
        Category category = Category.Food;
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;

        when(marketFacadeMock.searchCategoryInStores(category, minPrice, maxPrice, minRating)).thenThrow(new RuntimeException("Mocked exception"));

        ResponseEntity<String> result = marketService.searchCategoryInStores(category, minPrice, maxPrice, minRating);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        verify(marketFacadeMock, times(1)).searchCategoryInStores(category, minPrice, maxPrice, minRating);
    }

    @Test
    public void testSearchKeywordsInStores_Exception() {
        String keyWords = "apple";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        Category category = Category.Food;

        when(marketFacadeMock.searchKeywordsInStores(keyWords, minPrice, maxPrice, minRating, category)).thenThrow(new RuntimeException("Mocked exception"));

        ResponseEntity<String> result = marketService.searchKeywordsInStores(keyWords, minPrice, maxPrice, minRating, category);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        verify(marketFacadeMock, times(1)).searchKeywordsInStores(keyWords, minPrice, maxPrice, minRating, category);
    }

    @Test
    public void testSearchNameInStores_FilterByMinPrice() {
        String name = "product";
        Double minPrice = 30.0;
        Double maxPrice = 40.0;
        Double minRating = null;
        Category category = null;

        String product1 = "{\"product_id\":1, \"store_name\":\"store1\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":25.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"food\"]}";
        String product2 = "{\"product_id\":2, \"store_name\":\"store2\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":35.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"food\"]}";

        String expectedResponse = "[" + product2 + "]";
        when(marketFacadeMock.searchNameInStores(name, minPrice, maxPrice, minRating, category)).thenReturn(expectedResponse);

        ResponseEntity<String> result = marketService.searchNameInStores(name, minPrice, maxPrice, minRating, category);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResponse, result.getBody());
        verify(marketFacadeMock, times(1)).searchNameInStores(name, minPrice, maxPrice, minRating, category);
    }

    @Test
    public void testSearchNameInStores_FilterByMaxPrice() {
        String name = "product";
        Double minPrice = 30.0;
        Double maxPrice = 40.0;
        Double minRating = null;
        Category category = null;

        String product1 = "{\"product_id\":1, \"store_name\":\"store1\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":29.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"food\"]}";
        String product2 = "{\"product_id\":2, \"store_name\":\"store3\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":35.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"food\"]}";

        String expectedResponse = "[" + product1 + "]";
        when(marketFacadeMock.searchNameInStores(name, minPrice, maxPrice, minRating, category)).thenReturn(expectedResponse);

        ResponseEntity<String> result = marketService.searchNameInStores(name, minPrice, maxPrice, minRating, category);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResponse, result.getBody());
        verify(marketFacadeMock, times(1)).searchNameInStores(name, minPrice, maxPrice, minRating, category);
    }

    @Test
    public void testSearchCategoryInStores_FilterByRating() {
        Category category = Category.Food;
        Double minPrice = null;
        Double maxPrice = null;
        Double minRating = 4.5;

        String product1 = "{\"product_id\":1, \"store_name\":\"store1\", \"product_name\":\"product1\", \"product_description\":\"desc\", \"product_price\":25.0, \"product_quantity\":5, \"rating\":5.0, \"category\":\"Food\", \"keyWords\":[\"Food\"]}";
        String product2 = "{\"product_id\":2, \"store_name\":\"store1\", \"product_name\":\"product2\", \"product_description\":\"desc\", \"product_price\":35.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"Food\"]}";

        String expectedResponse = "[" + product1 + "]";
        when(marketFacadeMock.searchCategoryInStores(category, minPrice, maxPrice, minRating)).thenReturn(expectedResponse);

        ResponseEntity<String> result = marketService.searchCategoryInStores(category, minPrice, maxPrice, minRating);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResponse, result.getBody());
        verify(marketFacadeMock, times(1)).searchCategoryInStores(category, minPrice, maxPrice, minRating);
    }

    @Test
    public void testSearchKeywordsInStores_FilterByCategory() {
        String keyWords = "apple";
        Double minPrice = null;
        Double maxPrice = null;
        Double minRating = null;
        Category category = Category.Food;

        String product1 = "{\"product_id\":1, \"store_name\":\"store1\", \"product_name\":\"product1\", \"product_description\":\"desc\", \"product_price\":25.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"Food\"]}";
        String product2 = "{\"product_id\":2, \"store_name\":\"store1\", \"product_name\":\"product2\", \"product_description\":\"desc\", \"product_price\":35.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"Food\"]}";

        String expectedResponse = "[" + product1 + "," + product2 + "]";
        when(marketFacadeMock.searchKeywordsInStores(keyWords, minPrice, maxPrice, minRating, category)).thenReturn(expectedResponse);

        ResponseEntity<String> result = marketService.searchKeywordsInStores(keyWords, minPrice, maxPrice, minRating, category);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResponse, result.getBody());
        verify(marketFacadeMock, times(1)).searchKeywordsInStores(keyWords, minPrice, maxPrice, minRating, category);
    }

    @Test
    public void testSearchNameInStores_FilterByAllCriteria() {
        String name = "product";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        Category category = Category.Food;

        String product1 = "{\"product_id\":1, \"store_name\":\"store1\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":20.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"Food\"]}";
        String product2 = "{\"product_id\":2, \"store_name\":\"store1\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":40.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"Food\"]}";

        String expectedResponse = "[" + product1 + "," + product2 + "]";
        when(marketFacadeMock.searchNameInStores(name, minPrice, maxPrice, minRating, category)).thenReturn(expectedResponse);

        ResponseEntity<String> result = marketService.searchNameInStores(name, minPrice, maxPrice, minRating, category);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResponse, result.getBody());
        verify(marketFacadeMock, times(1)).searchNameInStores(name, minPrice, maxPrice, minRating, category);
    }
}
