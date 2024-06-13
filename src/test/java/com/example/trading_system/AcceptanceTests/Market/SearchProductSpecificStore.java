package com.example.trading_system.AcceptanceTests.Market;

import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SearchProductSpecificStore {
    //TODO FIX ME
 /*   private static TradingSystem tradingSystem;
    private static String token;
    private static String username;

    @BeforeAll
    void setupOnce() {
        tradingSystem = TradingSystemImp.getInstance();
        tradingSystem.register(0, "owner1", "password123", LocalDate.now());
        tradingSystem.register(1, "manager", "password123", LocalDate.now());
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
    }
    @Test
    public void testSearchNameInStore_Successful() {
        tradingSystem.openStore(username,token,"store1", "General Store", new StorePolicy());
        tradingSystem.addProduct(username,token,1, "store1", "product1", "desc1", 10.0, 100, 4,0,new ArrayList<>());
        ResponseEntity<String> response = tradingSystem.searchNameInStore("product1", "store1", null, null, null, Category.Sport.getIntValue());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testSearchCategoryInStore_Successful() {
        tradingSystem.openStore(username,token,"store1", "General Store", new StorePolicy());
        tradingSystem.addProduct(username,token,1, "store1", "product1", "desc1", 10.0, 100, 4,0,new ArrayList<>());
        ResponseEntity<String> response = tradingSystem.searchCategoryInStore(Category.Sport.getIntValue(), "store1", null, null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testSearchKeywordsInStore_Successful() {
        tradingSystem.openStore(username,token,"store1", "General Store", new StorePolicy());
        tradingSystem.addProduct(username,token,1, "store1", "product1", "desc1", 10.0, 100, 4,0,new ArrayList<>());
        ResponseEntity<String> response = tradingSystem.searchKeywordsInStore("keyword", "store1", null, null, null, Category.Sport.getIntValue());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testSearchNameInStore_NoNameProvided() {
        ResponseEntity<String> response = tradingSystem.searchNameInStore(null, "store1", null, null, null, Category.Sport.getIntValue());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No name provided", response.getBody());
    }

    @Test
    public void testSearchNameInStore_NoStoreNameProvided() {
        ResponseEntity<String> response = tradingSystem.searchNameInStore("product1", null, null, null, null, Category.Sport.getIntValue());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No store name provided", response.getBody());
    }

    @Test
    public void testSearchCategoryInStore_NoCategoryProvided() {
        ResponseEntity<String> response = tradingSystem.searchCategoryInStore(-1, "store1", null, null, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No category provided", response.getBody());
    }

    @Test
    public void testSearchCategoryInStore_NoStoreNameProvided() {
        ResponseEntity<String> response = tradingSystem.searchCategoryInStore(Category.Sport.getIntValue(), null, null, null, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No store name provided", response.getBody());
    }

    @Test
    public void testSearchKeywordsInStore_NoKeywordsProvided() {
        ResponseEntity<String> response = tradingSystem.searchKeywordsInStore(null, "store1", null, null, null, Category.Sport.getIntValue());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No keywords provided", response.getBody());
    }

    @Test
    public void testSearchKeywordsInStore_NoStoreNameProvided() {
        ResponseEntity<String> response = tradingSystem.searchKeywordsInStore("keyword", null, null, null, null, Category.Sport.getIntValue());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No store name provided", response.getBody());
    }*/
}