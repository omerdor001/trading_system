package com.example.trading_system.AcceptanceTests.Market;

import com.example.trading_system.domain.stores.Category;
import com.example.trading_system.domain.stores.StorePolicy;
import com.example.trading_system.service.TradingSystem;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class SearchAcceptanceTests {
//TODO

    private TradingSystem tradingSystem;
    private static String token;
    private static String username;

    @BeforeEach
    public void setUp() {
        tradingSystem = TradingSystemImp.getInstance();
        tradingSystem.register( "owner1", "password123", LocalDate.now());
        tradingSystem.register( "manager", "password123", LocalDate.now());
        tradingSystem.openSystem();

        String userTokenResponse = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userTokenResponse);
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            Assertions.fail("Setup failed: Unable to extract token from JSON response");
        }

        String loginResponse = tradingSystem.login(token, "0", "owner1", "password123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(loginResponse);
            username = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            Assertions.fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystem.openStore(username,token,"store1", "General Store", new StorePolicy());

    }

    @Test
    public void testSearchNameInStore_Success() {
        String storeName = "store1";
        String name = "product1";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        int category = Category.Food.getIntValue();

        String expectedResponse = "mocked response";
////        when(tradingSystem.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category)).thenReturn(expectedResponse);

        String result = String.valueOf(tradingSystem.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category));

        assertEquals(expectedResponse, result);
        verify(tradingSystem, times(1)).searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category);
    }

    @Test
    public void testSearchCategoryInStore_Success() {
        String storeName = "store1";
        int category = Category.Food.getIntValue();
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;

        String expectedResponse = "mocked response";
//        when(tradingSystem.searchCategoryInStore(category, storeName, minPrice, maxPrice, minRating)).thenReturn(expectedResponse);

        String result = String.valueOf(tradingSystem.searchCategoryInStore(category, storeName, minPrice, maxPrice, minRating));

        assertEquals(expectedResponse, result);
        verify(tradingSystem, times(1)).searchCategoryInStore(category, storeName, minPrice, maxPrice, minRating);
    }

    @Test
    public void testSearchKeywordsInStore_Success() {
        String storeName = "store1";
        String keyWords = "apple";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        int category = Category.Food.getIntValue();

        String expectedResponse = "mocked response";
//        when(tradingSystem.searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category)).thenReturn(expectedResponse);

        String result = String.valueOf(tradingSystem.searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category));

        assertEquals(expectedResponse, result);
        verify(tradingSystem, times(1)).searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category);
    }

    @Test
    public void testSearchNameInStore_Exception() {
        String storeName = "store1";
        String name = "product1";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        int category = Category.Food.getIntValue();

//        when(tradingSystem.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category)).thenThrow(new RuntimeException("Mocked exception"));

        String result = String.valueOf(tradingSystem.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category));

        assertEquals("", result);
        verify(tradingSystem, times(1)).searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category);
    }

    @Test
    public void testSearchCategoryInStore_Exception() {
        String storeName = "store1";
        int category = Category.Food.getIntValue();
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;

//        when(tradingSystem.searchCategoryInStore(category, storeName, minPrice, maxPrice, minRating)).thenThrow(new RuntimeException("Mocked exception"));

        String result = String.valueOf(tradingSystem.searchCategoryInStore(category, storeName, minPrice, maxPrice, minRating));

        assertEquals("", result);
        verify(tradingSystem, times(1)).searchCategoryInStore(category, storeName, minPrice, maxPrice, minRating);
    }

    @Test
    public void testSearchKeywordsInStore_Exception() {
        String storeName = "store1";
        String keyWords = "apple";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        int category = Category.Food.getIntValue();

//        when(tradingSystem.searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category)).thenThrow(new RuntimeException("Mocked exception"));

        String result = String.valueOf(tradingSystem.searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category));

        assertEquals("", result);
        verify(tradingSystem, times(1)).searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category);
    }

    @Test
    public void testSearchNameInStore_FilterByMinPrice() {
        String storeName = "store1";
        String name = "product";
        Double minPrice = 30.0;
        Double maxPrice = 40.0;
        Double minRating = null;
        int category = Integer.parseInt(null);

        String product1 = "{\"product_id\":1, \"store_name\":\"store1\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":25.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"foof\"]}";
        String product2 = "{\"product_id\":2, \"store_name\":\"store1\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":35.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"food\"]}";

        String expectedResponse = "[" + product2 + "]";
//        when(tradingSystem.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category)).thenReturn(expectedResponse);

        String result = String.valueOf(tradingSystem.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category));

        assertEquals(expectedResponse, result);
        verify(tradingSystem, times(1)).searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category);
    }

    @Test
    public void testSearchNameInStore_FilterByMaxPrice() {
        String storeName = "store1";
        String name = "product";
        Double minPrice = 30.0;
        Double maxPrice = 40.0;
        Double minRating = null;
        int category = Integer.parseInt(null);


        String product1 = "{\"product_id\":1, \"store_name\":\"store1\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":29.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"foof\"]}";
        String product2 = "{\"product_id\":2, \"store_name\":\"store1\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":35.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"food\"]}";

        String expectedResponse = "[" + product1 + "]";
//        when(tradingSystem.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category)).thenReturn(expectedResponse);

        String result = String.valueOf(tradingSystem.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category));

        assertEquals(expectedResponse, result);
        verify(tradingSystem, times(1)).searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category);
    }


    @Test
    public void testSearchCategoryInStore_FilterByRating() {

        //TODO HERE IS A WORKING TEST!! - BUT THE RETURN VALUE SHOULD BE CHANGED
        String storeName = "store1";
        int category = Category.Food.getIntValue();
        Double minPrice = null;
        Double maxPrice = null;
        Double minRating = 4.5;

        String product1 = "{\"product_id\":1, \"store_name\":\"store1\", \"product_name\":\"product1\", \"product_description\":\"desc\", \"product_price\":25.0, \"product_quantity\":5, \"rating\":5.0, \"category\":\"Food\", \"keyWords\":[\"Food\"]}";
        String product2 = "{\"product_id\":2, \"store_name\":\"store1\", \"product_name\":\"product2\", \"product_description\":\"desc\", \"product_price\":35.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"Food\"]}";

//        String expectedResponse = "[" + product1 + "]";

        String expectedResponse = "<200 OK OK,FINISHED Searching products in store ,[]>";
//        when(tradingSystem.searchCategoryInStore(category, storeName, minPrice, maxPrice, minRating)).thenReturn(expectedResponse);

        String result = String.valueOf(tradingSystem.searchCategoryInStore(category, storeName, minPrice, maxPrice, minRating));

        assertEquals(expectedResponse, result);
//        verify(tradingSystem, times(1)).searchCategoryInStore(category, storeName, minPrice, maxPrice, minRating);
    }

    @Test
    public void testSearchKeywordsInStore_FilterByCategory() {
        String storeName = "store1";
        String keyWords = "apple";
        Double minPrice = null;
        Double maxPrice = null;
        Double minRating = null;
        int category = Category.Food.getIntValue();

        String product1 = "{\"product_id\":1, \"store_name\":\"store1\", \"product_name\":\"product1\", \"product_description\":\"desc\", \"product_price\":25.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"Food\"]}";
        String product2 = "{\"product_id\":2, \"store_name\":\"store1\", \"product_name\":\"product2\", \"product_description\":\"desc\", \"product_price\":35.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"Food\"]}";

        String expectedResponse = "[" + product1 + "," + product2 + "]";
//        when(tradingSystem.searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category)).thenReturn(expectedResponse);

        String result = String.valueOf(tradingSystem.searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category));

        assertEquals(expectedResponse, result);
        verify(tradingSystem, times(1)).searchKeywordsInStore(keyWords, storeName, minPrice, maxPrice, minRating, category);
    }

    @Test
    public void testSearchNameInStore_FilterByAllCriteria() {
        String storeName = "store1";
        String name = "product";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        int category = Category.Food.getIntValue();

        String product1 = "{\"product_id\":1, \"store_name\":\"store1\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":20.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"Food\"]}";
        String product2 = "{\"product_id\":2, \"store_name\":\"store1\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":40.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"Food\"]}";

        String expectedResponse = "[" + product1 + "," + product2 + "]";
//        when(tradingSystem.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category)).thenReturn(expectedResponse);

        String result = String.valueOf(tradingSystem.searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category));

        assertEquals(expectedResponse, result);
        verify(tradingSystem, times(1)).searchNameInStore(name, storeName, minPrice, maxPrice, minRating, category);
    }





    ///////////////////////////tests for searching in stores - without specific focus






    @Test
    public void testSearchNameInStores_Success() {
        String name = "product1";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        int category = Category.Food.getIntValue();

        String expectedResponse = "mocked response";
//        when(tradingSystem.searchNameInStores(name, minPrice, maxPrice, minRating, category)).thenReturn(expectedResponse);

        String result = String.valueOf(tradingSystem.searchNameInStores(name, minPrice, maxPrice, minRating, category,null));

        assertEquals(expectedResponse, result);
        verify(tradingSystem, times(1)).searchNameInStores(name, minPrice, maxPrice, minRating, category,null);
    }

    @Test
    public void testSearchCategoryInStores_Success() {
        int category = Category.Food.getIntValue();
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;

        String expectedResponse = "mocked response";
//        when(tradingSystem.searchCategoryInStores(category,minPrice, maxPrice, minRating,null)).thenReturn(expectedResponse);

        String result = String.valueOf(tradingSystem.searchCategoryInStores(category,  minPrice, maxPrice, minRating,null));

        assertEquals(expectedResponse, result);
        verify(tradingSystem, times(1)).searchCategoryInStores(category, minPrice, maxPrice, minRating,null);
    }

    @Test
    public void testSearchKeywordsInStores_Success() {
        String keyWords = "apple";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        int category = Category.Food.getIntValue();

        String expectedResponse = "mocked response";
//        when(tradingSystem.searchKeywordsInStores(keyWords, minPrice, maxPrice, minRating, category,null)).thenReturn(expectedResponse);

        String result = String.valueOf(tradingSystem.searchKeywordsInStores(keyWords,minPrice, maxPrice, minRating, category,null));

        assertEquals(expectedResponse, result);
        verify(tradingSystem, times(1)).searchKeywordsInStores(keyWords, minPrice, maxPrice, minRating, category,null);
    }

    @Test
    public void testSearchNameInStores_Exception() {
        String name = "product1";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        int category = Category.Food.getIntValue();

//        when(tradingSystem.searchNameInStores(name,minPrice, maxPrice, minRating, category,null)).thenThrow(new RuntimeException("Mocked exception"));

        String result = String.valueOf(tradingSystem.searchNameInStores(name,minPrice, maxPrice, minRating, category,null));

        assertEquals("", result);
        verify(tradingSystem, times(1)).searchNameInStores(name,minPrice, maxPrice, minRating, category,null);
    }

    @Test
    public void testSearchCategoryInStores_Exception() {
        String storeName = "store1";
        int category = Category.Food.getIntValue();
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;

//        when(tradingSystem.searchCategoryInStores(  category,  minPrice, maxPrice, minRating,null)).thenThrow(new RuntimeException("Mocked exception"));

        String result = String.valueOf(tradingSystem.searchCategoryInStores(category,minPrice, maxPrice, minRating,null));

        assertEquals("", result);
        verify(tradingSystem, times(1)).searchCategoryInStores(category,minPrice, maxPrice, minRating,null);
    }

    @Test
    public void testSearchKeywordsInStores_Exception() {
        String keyWords = "apple";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        int category = Category.Food.getIntValue();

//        when(tradingSystem.searchKeywordsInStores(keyWords,minPrice, maxPrice, minRating, category,null)).thenThrow(new RuntimeException("Mocked exception"));

        String result = String.valueOf(tradingSystem.searchKeywordsInStores(keyWords, minPrice, maxPrice, minRating, category,null));

        assertEquals("", result);
        verify(tradingSystem, times(1)).searchKeywordsInStores(keyWords, minPrice, maxPrice, minRating, category,null);
    }

    @Test
    public void testSearchNameInStores_FilterByMinPrice() {
        String name = "product";
        Double minPrice = 30.0;
        Double maxPrice = 40.0;
        Double minRating = null;
        int category = Integer.parseInt(null);

        String product1 = "{\"product_id\":1, \"store_name\":\"store1\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":25.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"foof\"]}";
        String product2 = "{\"product_id\":2, \"store_name\":\"store2\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":35.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"food\"]}";

        String expectedResponse = "[" + product2 + "]";
////        when(tradingSystem.searchNameInStores(name, minPrice, maxPrice, minRating, category,null)).thenReturn(expectedResponse);

        String result = String.valueOf(tradingSystem.searchNameInStores(name,minPrice, maxPrice, minRating, category,null));

        assertEquals(expectedResponse, result);
        verify(tradingSystem, times(1)).searchNameInStores(name, minPrice, maxPrice, minRating, category,null);
    }

    @Test
    public void testSearchNameInStores_FilterByMaxPrice() {
        String name = "product";
        Double minPrice = 30.0;
        Double maxPrice = 40.0;
        Double minRating = null;
        int category = Integer.parseInt(null);

        String product1 = "{\"product_id\":1, \"store_name\":\"store1\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":29.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"foof\"]}";
        String product2 = "{\"product_id\":2, \"store_name\":\"store3\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":35.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"food\"]}";

        String expectedResponse = "[" + product1 + "]";
////        when(tradingSystem.searchNameInStores(name, minPrice, maxPrice, minRating, category,null)).thenReturn(expectedResponse);

        String result = String.valueOf(tradingSystem.searchNameInStores(name,  minPrice, maxPrice, minRating, category,null));

        assertEquals(expectedResponse, result);
        verify(tradingSystem, times(1)).searchNameInStores(name, minPrice, maxPrice, minRating, category,null);
    }

    @Test
    public void testSearchCategoryInStores_FilterByRating() {
        int category = Category.Food.getIntValue();
        Double minPrice = null;
        Double maxPrice = null;
        Double minRating = 4.5;

        String product1 = "{\"product_id\":1, \"store_name\":\"store1\", \"product_name\":\"product1\", \"product_description\":\"desc\", \"product_price\":25.0, \"product_quantity\":5, \"rating\":5.0, \"category\":\"Food\", \"keyWords\":[\"Food\"]}";
        String product2 = "{\"product_id\":2, \"store_name\":\"store1\", \"product_name\":\"product2\", \"product_description\":\"desc\", \"product_price\":35.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"Food\"]}";

        String expectedResponse = "[" + product1 + "]";
//        when(tradingSystem.searchCategoryInStores(category,minPrice, maxPrice, minRating,null)).thenReturn(expectedResponse);

        String result = String.valueOf(tradingSystem.searchCategoryInStores(category, minPrice, maxPrice, minRating,null));

        assertEquals(expectedResponse, result);
        verify(tradingSystem, times(1)).searchCategoryInStores(category, minPrice, maxPrice, minRating,null);
    }

    @Test
    public void testSearchKeywordsInStores_FilterByCategory() {
        String keyWords = "apple";
        Double minPrice = null;
        Double maxPrice = null;
        Double minRating = null;
        int category = Category.Food.getIntValue();

        String product1 = "{\"product_id\":1, \"store_name\":\"store1\", \"product_name\":\"product1\", \"product_description\":\"desc\", \"product_price\":25.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"Food\"]}";
        String product2 = "{\"product_id\":2, \"store_name\":\"store1\", \"product_name\":\"product2\", \"product_description\":\"desc\", \"product_price\":35.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"Food\"]}";

        String expectedResponse = "[" + product1 + "," + product2 + "]";
//        when(tradingSystem.searchKeywordsInStores(keyWords,minPrice, maxPrice, minRating, category,null)).thenReturn(expectedResponse);

        String result = String.valueOf(tradingSystem.searchKeywordsInStores(keyWords, minPrice, maxPrice, minRating, category,null));

        assertEquals(expectedResponse, result);
        verify(tradingSystem, times(1)).searchKeywordsInStores(keyWords, minPrice, maxPrice, minRating, category,null);
    }

    @Test
    public void testSearchNameInStores_FilterByAllCriteria() {
        String name = "product";
        Double minPrice = 10.0;
        Double maxPrice = 50.0;
        Double minRating = 4.0;
        int category = Category.Food.getIntValue();

        String product1 = "{\"product_id\":1, \"store_name\":\"store1\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":20.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"Food\"]}";
        String product2 = "{\"product_id\":2, \"store_name\":\"store1\", \"product_name\":\"product\", \"product_description\":\"desc\", \"product_price\":40.0, \"product_quantity\":5, \"rating\":4.0, \"category\":\"Food\", \"keyWords\":[\"Food\"]}";

        String expectedResponse = "[" + product1 + "," + product2 + "]";
////        when(tradingSystem.searchNameInStores(name, minPrice, maxPrice, minRating, category,null)).thenReturn(expectedResponse);

        String result = String.valueOf(tradingSystem.searchNameInStores(name, minPrice, maxPrice, minRating, category,null));

        assertEquals(expectedResponse, result);
        verify(tradingSystem, times(1)).searchNameInStores(name, minPrice, maxPrice, minRating, category,null);
    }


}
