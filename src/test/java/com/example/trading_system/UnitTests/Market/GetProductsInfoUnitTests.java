package com.example.trading_system.UnitTests.Market;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.MarketFacadeImp;
import com.example.trading_system.domain.stores.Store;
import com.example.trading_system.domain.stores.StoreDatabaseRepository;
import com.example.trading_system.domain.users.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class GetProductsInfoUnitTests {

    private UserFacadeImp userFacade;
    private MarketFacadeImp marketFacade;
    private final String validUsername = "validUser";
    private final String validStoreName = "ValidStore";
    private final String validStoreName1 = "ValidStore1";

    private final String validDescription = "This is a valid description.";
    private User user;

    @Autowired
    private UserDatabaseRepository userRepository;

    @Autowired
    private StoreDatabaseRepository storeRepository;

    @BeforeEach
    public void init() {
        userFacade = UserFacadeImp.getInstance(
                mock(PaymentService.class),
                mock(DeliveryService.class),
                mock(NotificationSender.class),
                userRepository,
                storeRepository
        );
        marketFacade = MarketFacadeImp.getInstance(storeRepository);

        // Create and persist a store
        Store store = new Store(validStoreName, validDescription, validUsername, null);
        Store store1 = new Store(validStoreName1, validDescription, validUsername, null);

        store.addProduct(1, "product1", "", 5, 5, 5, 3, new ArrayList<>());
        storeRepository.save(store);
        storeRepository.save(store1);

        // Create and persist a user
        user = new Visitor(validUsername);
        userRepository.addVisitor(validUsername);

        // Adding the user to the facade's internal storage
        userFacade.getUsers().put(validUsername, user);
    }

    @AfterEach
    public void resetMocks() {
        marketFacade.deleteInstance();
        userFacade.deleteInstance();
        userRepository.deleteInstance();
        storeRepository.deleteInstance();
    }

    @Test
    public void givenInvalidStoreName_WhenGetStoreProducts_ThenThrowRuntimeException() {
        Assertions.assertThrows(RuntimeException.class, () -> marketFacade.getStoreProducts("user1", ""));
    }

    @Test
    public void givenInvalidProductId_WhenGetProductInfo_ThenThrowRuntimeException() throws IllegalAccessException {
        // Ensure store exists

        // Attempting to fetch product info with an invalid product ID should throw RuntimeException
        Assertions.assertThrows(RuntimeException.class, () -> marketFacade.getProductInfo(validUsername, validStoreName, 999));
    }

    @Test
    public void givenValidDetails_WhenGetAllStores_ThenSuccess() {
        // Ensure stores exist

        String expected = "[\"stores\":ValidStore,ValidStore1]";
        String actual = marketFacade.getAllStores(validUsername);
        assertEquals(expected, actual);
    }

    @Test
    public void givenValidStoreName_WhenGetStoreProducts_ThenSuccess() throws Exception {

        String expected = "{\"name_id\":\"ValidStore\", \"description\":\"This is a valid description.\", \"products\":[{\"product_id\":1, \"store_name\":\"ValidStore\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":\"Food\", \"keyWords\":[]}, ]}";
        String actual = marketFacade.getStoreProducts(validUsername, validStoreName);
        assertEquals(expected, actual);
    }

    @Test
    public void givenValidProductId_WhenGetProductInfo_ThenSuccess() throws Exception {

        String expected = "{\"product_id\":1, \"store_name\":\"ValidStore\", \"product_name\":\"product1\", \"product_description\":\"\", \"product_price\":5.0, \"product_quantity\":5, \"rating\":5.0, \"category\":\"Food\", \"keyWords\":[]}";
        String actual = marketFacade.getProductInfo(validUsername, validStoreName, 1);
        System.out.println(actual);
        assertEquals(expected, actual);
    }
}
