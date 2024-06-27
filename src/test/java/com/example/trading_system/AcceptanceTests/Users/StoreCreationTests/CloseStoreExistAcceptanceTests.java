package com.example.trading_system.AcceptanceTests.Users.StoreCreationTests;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.StoreMemoryRepository;
import com.example.trading_system.domain.stores.StoreRepository;
import com.example.trading_system.domain.users.UserMemoryRepository;
import com.example.trading_system.domain.users.UserRepository;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

public class CloseStoreExistAcceptanceTests {

    private TradingSystemImp tradingSystemImp;
    private String token;
    private String userName;
    private final String password = "123456";
    private final String storeName = "Store1";
    private UserRepository userRepository;
    private StoreRepository storeRepository;

    @BeforeEach
    public void setUp() {
        userRepository= UserMemoryRepository.getInstance();    //May be change later
        storeRepository= StoreMemoryRepository.getInstance();  //May be change later
        tradingSystemImp = TradingSystemImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class), mock(NotificationSender.class),userRepository,storeRepository);
        tradingSystemImp.register("admin", password, LocalDate.now());
        tradingSystemImp.openSystem(storeRepository);
        ResponseEntity<String> response = tradingSystemImp.enter();
        String userToken = response.getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            userName = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken = tradingSystemImp.login(token, "v0", "admin", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            userName = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystemImp.openStore(userName, token, storeName, "My Store is the best");
        String[] keyWords = {"CarPlay", "iPhone"};
        tradingSystemImp.addProduct(userName, token, 111, storeName, "CarPlay", "CarPlay for iPhones", 15, 5, 6, 1, new ArrayList<>(Arrays.asList(keyWords)));
    }


    @Test
    public void givenNotExistStore_WhenCloseStoreExist_ThenThrowException() {
        String response = tradingSystemImp.closeStoreExist(userName, token, "Store2").getBody();
        Assertions.assertEquals("Store must exist", response);

    }


    @Test
    public void givenNotFounderTryCloseStore_WhenCloseStoreExist_ThenThrowException() {
        tradingSystemImp.register("owner", password, LocalDate.now());
        tradingSystemImp.openSystem(storeRepository);
        ResponseEntity<String> response = tradingSystemImp.enter();
        String userToken = response.getBody();
        String userNameOwner = "";
        String tokenOwner = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            userNameOwner = rootNode.get("username").asText();
            tokenOwner = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken = tradingSystemImp.login(tokenOwner, "v1", "owner", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            userNameOwner = rootNode.get("username").asText();
            tokenOwner = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystemImp.suggestManage(userName, token, userNameOwner, storeName, true, true, true, true);
        tradingSystemImp.approveManage(userNameOwner, tokenOwner, storeName, userName, true, true, true, true);
        String response2 = tradingSystemImp.closeStoreExist(userNameOwner, tokenOwner, storeName).getBody();

        Assertions.assertEquals("Only founder can close store exist", response2);

    }

    @Test
    public void givenAlreadyClosedStore_WhenCloseStoreExist_ThenThrowException() {
        HttpStatusCode statusCode = tradingSystemImp.closeStoreExist(userName, token, storeName).getStatusCode();
        Assertions.assertEquals(statusCode, HttpStatus.OK);
        String response = tradingSystemImp.closeStoreExist(userName, token, storeName).getBody();
        Assertions.assertEquals("Store is not active", response);
    }

    @Test
    public void givenValidStoreToClose_WhenCloseStoreExist_ThenSuccess() {
        ResponseEntity<String> response2 = tradingSystemImp.searchNameInStore(userName, "CarPlay", token, storeName, 1.0, 1000.0, 1.0, 1);
        Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
        HttpStatusCode statusCode = tradingSystemImp.closeStoreExist(userName, token, storeName).getStatusCode();
        Assertions.assertEquals(statusCode, HttpStatus.OK);

        ResponseEntity<String> response3 = tradingSystemImp.searchNameInStore(userName, "CarPlay", token, storeName, 1.0, 1000.0, 1.0, 1);
        Assertions.assertEquals("Store is not open, can't search", response3.getBody());
    } //, also valid notification

    @AfterEach
    public void tearDown() {
        try {
            tradingSystemImp.openStoreExist(userName, token, storeName);
        } catch (Exception e) {
            System.out.println("Store is already Open");
        }
        tradingSystemImp.deleteInstance();
        userRepository.deleteInstance();
        storeRepository.deleteInstance();
    }
}
