package com.example.trading_system.UnitTests.Market;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.MarketFacadeImp;
import com.example.trading_system.domain.stores.StoreMemoryRepository;
import com.example.trading_system.domain.stores.StoreRepository;
import com.example.trading_system.domain.users.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetProductsInfoUnitTests {

    @Mock
    User user;
    @Mock
    Role role;
    UserFacadeImp userFacade;
    MarketFacadeImp marketFacade;
    String validUsername = "validUser";
    String validStoreName = "ValidStore";
    String validDescription = "This is a valid description.";
    private UserRepository userRepository;
    private StoreRepository storeRepository;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        storeRepository= StoreMemoryRepository.getInstance();
        userRepository = UserMemoryRepository.getInstance();
        marketFacade = MarketFacadeImp.getInstance(storeRepository);
        userFacade = UserFacadeImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class), mock(NotificationSender.class),userRepository,storeRepository);
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

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {""})
    public void givenInvalidStoreName_WhenGetStoreProducts_ThenThrowRuntimeException(String storeName) {
        Assertions.assertThrows(RuntimeException.class, () -> marketFacade.getStoreProducts("user1", storeName));
    }

    @Test
    public void givenInvalidProductId_WhenGetProductInfo_ThenThrowRuntimeException() throws IllegalAccessException {
        // Ensure store exists
        marketFacade.addStore(validStoreName, validDescription, validUsername, null);
        // Ensure a valid product is added
        marketFacade.addProduct(validUsername, 1, validStoreName, "product1", "", 5, 5, 5, 1, new ArrayList<>());
        when(user.getRoleByStoreId(validStoreName)).thenReturn(role);

        // Attempting to fetch product info with an invalid product ID should throw RuntimeException
        Assertions.assertThrows(RuntimeException.class, () -> marketFacade.getProductInfo(validUsername, validStoreName, 999));
    }

    @Test
    public void givenValidDetails_WhenGetAllStores_ThenSuccess() {
        // Ensure stores exist
        userFacade.createStore(validUsername, "store1", "description");
        userFacade.createStore(validUsername, "store2", "description");

        String expected = "[\"stores\":store1,store2]";
        String actual = marketFacade.getAllStores(validUsername);
        assertEquals(expected, actual);
    }

    @Test
    public void givenValidStoreName_WhenGetStoreProducts_ThenSuccess() throws Exception {
        // Ensure store and product exist
        marketFacade.addStore(validStoreName, validDescription, validUsername, null);
        marketFacade.addProduct(validUsername, 1, validStoreName, "product1", "", 5, 5, 5, 1, new ArrayList<>());

        String expected = "{\"name_id\":\"ValidStore\", \"description\":\"This is a valid description.\", \"products\":[{\"product_id\":1, \"store_name\":\"ValidStore\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":\"Sport\", \"keyWords\":[]}, ]}";
        String actual = marketFacade.getStoreProducts(validUsername, validStoreName);
        assertEquals(expected, actual);
    }

    @Test
    public void givenValidProductId_WhenGetProductInfo_ThenSuccess() throws Exception {
        // Ensure store and product exist
        marketFacade.addStore(validStoreName, validDescription, validUsername, null);
        marketFacade.addProduct(validUsername, 1, validStoreName, "product1", "", 5, 5, 5, 1, new ArrayList<>());

        String expected = "{\"product_id\":1, \"store_name\":\"ValidStore\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":\"Sport\", \"keyWords\":[]}";
        String actual = marketFacade.getProductInfo(validUsername, validStoreName, 1);
        assertEquals(expected, actual);
    }
}