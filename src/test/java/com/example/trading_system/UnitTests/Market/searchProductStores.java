package com.example.trading_system.UnitTests.Market;

import com.example.trading_system.domain.stores.Category;
import com.example.trading_system.domain.stores.MarketFacadeImp;
import com.example.trading_system.domain.stores.Store;
import com.example.trading_system.domain.stores.StorePolicy;
import com.example.trading_system.domain.users.Role;
import com.example.trading_system.domain.users.User;
import com.example.trading_system.domain.users.UserFacadeImp;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class searchProductStores {
    User user;
    @Mock
    Role role;
    @Mock
    StorePolicy policy;

    UserFacadeImp userFacade;
    MarketFacadeImp marketFacade;
    String validUsername = "validUser";
    String validStoreName = "ValidStore";
    String validDescription = "This is a valid description.";

    private LocalDate birthdate = LocalDate.of(1990, 5, 15);

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        marketFacade = MarketFacadeImp.getInstance();
        userFacade = UserFacadeImp.getInstance();

        // Mock user object
        user = mock(User.class);

        // Add the mocked user to the facade
        userFacade.getUsers().put(validUsername, user);

        // Mock the user to return a valid role for the store
        when(user.getRoleByStoreId(anyString())).thenReturn(role);
    }

    @AfterEach
    public void resetMocks() {
        userFacade.deleteInstance();
        marketFacade.deleteInstance();
        reset(user, role);
    }


    @Test
    void testSearchNameInStores_ValidInput() throws IllegalAccessException {
        marketFacade.addStore(validStoreName, validDescription, policy, validUsername, null);
        marketFacade.addProduct(validUsername, 1, validStoreName, "product1", "", 5, 5, 5, 1, new ArrayList<>(List.of("keyword")));
        String expected = "[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[keyword]}]";
        String actual = marketFacade.searchNameInStores(validUsername,"product1", null, null, null, -1, null);
        assertEquals(expected, actual);
    }

    @Test
    void testSearchNameInStores_NoNameProvided() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                marketFacade.searchNameInStores(validUsername,null, null, null, null, -1, null));
        assertEquals("No name provided", exception.getMessage());
    }

    @Test
    void testSearchNameInStores_NoProductsAvailable() {
        String result = marketFacade.searchNameInStores(validUsername,"product3", null, null, null, -1, null);
        assertEquals("{}", result);
    }

    @Test
    void testSearchCategoryInStore_ValidInput() throws IllegalAccessException {
        marketFacade.addStore(validStoreName, validDescription, policy, validUsername, null);
        marketFacade.addProduct(validUsername, 1, validStoreName, "product1", "", 5, 5, 5, 1, new ArrayList<>(List.of("keyword")));
        marketFacade.addProduct(validUsername, 2, validStoreName, "product2", "", 8, 5, 5, 1, new ArrayList<>());
        String result = marketFacade.searchCategoryInStores(validUsername,1, null, null, null, null);
        assertEquals("[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[keyword]}, {\"product_id\":2, \"store_name\":\"\", \"product_name\":\"product2\", \"product_description\":\"\", \"product_price\":8.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[]}]",
                result);
    }

    @Test
    void testSearchCategoryInStores_NoCategoryProvided() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                marketFacade.searchCategoryInStores(validUsername,-1, null, null, null, null));
        assertEquals("No category provided", exception.getMessage());
    }

    @Test
    void testSearchCategoryInStores_NoProductsAvailable() {
        String result = marketFacade.searchCategoryInStores(validUsername,2, null, null, null, null);
        assertEquals("{}", result);
    }

    @Test
    void testSearchKeywordsInStores_ValidInput() throws IllegalAccessException {
        marketFacade.addStore(validStoreName, validDescription, policy, validUsername, null);
        marketFacade.addProduct(validUsername, 1, validStoreName, "product1", "", 5, 5, 5, 1, new ArrayList<>(List.of("keyword")));
        String result = marketFacade.searchKeywordsInStores(validUsername,"keyword", null, null, null, -1, null);
        assertEquals("[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[keyword]}]",
                result);

    }

    @Test
    void testSearchKeywordsInStores_NoKeywordsProvided() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                marketFacade.searchKeywordsInStores(validUsername,null, null, null, null, -1, null));
        assertEquals("No keywords provided", exception.getMessage());
    }

    @Test
    void testSearchKeywordsInStores_NoProductsAvailable() {
        String result = marketFacade.searchKeywordsInStores(validUsername,"keyword3", null, null, null, -1, null);
        assertEquals("{}", result);
    }

    @Test
    void testFilterProducts_AllParametersNull() throws IllegalAccessException {
        marketFacade.addStore(validStoreName, validDescription, policy, validUsername, null);
        marketFacade.addProduct(validUsername, 1, validStoreName, "product1", "", 5, 5, 5, 1, new ArrayList<>(List.of("keyword")));
        String expected = "[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[keyword]}]";
        String actual = marketFacade.searchNameInStores(validUsername,"product1", null, null, null, -1, null);
        assertEquals(expected, actual);
    }

    @Test
    void testFilterProducts_MinPriceOnly() throws IllegalAccessException {
        marketFacade.addStore(validStoreName, validDescription, policy, validUsername, null);
        marketFacade.addProduct(validUsername, 1, validStoreName, "product1", "", 5, 5, 5, 1, new ArrayList<>(List.of("keyword")));
        marketFacade.addProduct(validUsername, 2, validStoreName, "product2", "", 8, 5, 5, 1, new ArrayList<>());        String actual = marketFacade.searchCategoryInStores(validUsername,1, 3.0, null, null, null);
        assertEquals("[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[keyword]}, {\"product_id\":2, \"store_name\":\"\", \"product_name\":\"product2\", \"product_description\":\"\", \"product_price\":8.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[]}]",
                actual);
    }

    @Test
    void testFilterProducts_MaxPriceOnly() throws IllegalAccessException {
        marketFacade.addStore(validStoreName, validDescription, policy, validUsername, null);
        marketFacade.addProduct(validUsername, 1, validStoreName, "product1", "", 5, 5, 5, 1,new ArrayList<>(List.of("keyword")));
        String expected = "[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[keyword]}]";
        String actual = marketFacade.searchNameInStores(validUsername,"product1", null, 6.0, null, -1, null);
        assertEquals(expected, actual);
    }

    @Test
    void testFilterProducts_MinRatingOnly() throws IllegalAccessException {
        marketFacade.addStore(validStoreName, validDescription, policy, validUsername, null);
        marketFacade.addProduct(validUsername, 1, validStoreName, "product1", "", 5, 5, 5, 1, new ArrayList<>(List.of("keyword")));
        marketFacade.addProduct(validUsername, 2, validStoreName, "product2", "", 8, 5, 5, 1, new ArrayList<>());        String actual = marketFacade.searchCategoryInStores(validUsername,1, null, null, 3.0, null);
        assertEquals("[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[keyword]}, {\"product_id\":2, \"store_name\":\"\", \"product_name\":\"product2\", \"product_description\":\"\", \"product_price\":8.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[]}]",
                actual);
    }

    @Test
    void testFilterProducts_MinPriceAndMaxPrice() throws IllegalAccessException {
        marketFacade.addStore(validStoreName, validDescription, policy, validUsername, null);
        marketFacade.addProduct(validUsername, 1, validStoreName, "product1", "", 5, 5, 5, 1, new ArrayList<>(List.of("keyword")));
        String expected = "[{\"product_id\":1, \"store_name\":\"\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":Sport, \"keyWords\":[keyword]}]";
        String actual = marketFacade.searchNameInStores(validUsername,"product1", 2.0, 6.0, null, -1, null);
        assertEquals(expected, actual);
    }

}