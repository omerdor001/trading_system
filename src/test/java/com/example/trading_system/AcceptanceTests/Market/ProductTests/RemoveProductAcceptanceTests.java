package com.example.trading_system.AcceptanceTests.Market.ProductTests;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.StoreMemoryRepository;
import com.example.trading_system.domain.stores.StoreRepository;
import com.example.trading_system.domain.users.UserMemoryRepository;
import com.example.trading_system.domain.users.UserRepository;
import com.example.trading_system.service.TradingSystemImp;
import com.example.trading_system.service.UserServiceImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

public class RemoveProductAcceptanceTests {
    private TradingSystemImp tradingSystemImp;
    private final String password = "123456";
    private String userName = "";
    private String token = "";
    private final String storeName = "Store1";
    private final String[] keyWords = {"CarPlay", "iPhone"};
    private final int productID = 111;

    @BeforeEach
    public void setUp() {
        UserRepository userRepository = UserMemoryRepository.getInstance();    //May be change later
        StoreRepository storeRepository = StoreMemoryRepository.getInstance();  //May be change later
        tradingSystemImp = TradingSystemImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class), mock(NotificationSender.class), userRepository, storeRepository);
        tradingSystemImp.userService= UserServiceImp.getInstance(mock(PaymentService.class), mock(DeliveryService.class), mock(NotificationSender.class), userRepository, storeRepository);
        tradingSystemImp.userService.getUserFacade().setUserRepository(userRepository);
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
            fail("Setup failed: Unable to extract username and token from JSON response1");
        }
        userToken = tradingSystemImp.login(token, "v0", "admin", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            userName = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response2");
        }
        tradingSystemImp.openStore(userName, token, storeName, "My Store is the best");
        tradingSystemImp.addProduct(userName, token, productID, storeName, "Product1", "ProductDescription", 10, 5, 6, 1, new ArrayList<>(Arrays.asList(keyWords)));
    }

    @AfterEach
    public void tearDown() {
        tradingSystemImp.setSystemOpen(true);
        tradingSystemImp.deleteInstance();
    }

    @Test
    public void GivenNotExistsStore_WhenRemoveProduct_ThenThrowException() {
        ResponseEntity<String> response = tradingSystemImp.removeProduct(userName, token, "BadStoreName", productID);
        Assertions.assertEquals("Store must exist", response.getBody());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void givenProductNotExist_WhenRemoveProduct_ThenThrowException() {
        ResponseEntity<String> response = tradingSystemImp.removeProduct(userName, token, storeName, 222);
        Assertions.assertEquals("Product must exist", response.getBody());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void givenRegularUser_WhenAddProduct_ThenThrowException() {
        tradingSystemImp.register("regularUser", password, LocalDate.now());
        ResponseEntity<String> response = tradingSystemImp.enter();
        String userToken = response.getBody();
        String regularUser = "";
        String regularToken = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            regularUser = rootNode.get("username").asText();
            regularToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken = tradingSystemImp.login(regularToken, "v1", "regularUser", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            regularUser = rootNode.get("username").asText();
            regularToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        ResponseEntity<String> response2 = tradingSystemImp.removeProduct(regularUser, regularToken, storeName, productID);
        Assertions.assertEquals("User doesn't have roles", response2.getBody());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response2.getStatusCode());
    }

    @Test
    public void givenManagerWithoutPermission_WhenAddProduct_ThenThrowException() {
        tearDown();
        setUp();
        tradingSystemImp.register("managerWithoutPermissions", password, LocalDate.now());
        ResponseEntity<String> response = tradingSystemImp.enter();
        String userToken = response.getBody();
        String userNameManager = "";
        String tokenManager = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            userNameManager = rootNode.get("username").asText();
            tokenManager = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken = tradingSystemImp.login(tokenManager, "v1", "managerWithoutPermissions", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            userNameManager = rootNode.get("username").asText();
            tokenManager = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystemImp.suggestManage(userName, token, userNameManager, storeName, true, false, true, true);
        tradingSystemImp.approveManage(userNameManager, tokenManager, storeName, userName);
        tradingSystemImp.appointManager(userName, token, userName, userNameManager, storeName, true, false, true, true);
        ResponseEntity<String> response2 = tradingSystemImp.removeProduct(userNameManager, tokenManager, storeName, productID);
        Assertions.assertEquals("Manager cannot remove products", response2.getBody());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response2.getStatusCode());
    }

    @Test
    public void givenManagerWithPermission_WhenAddProduct_ThenThrowException() {
        tradingSystemImp.register("managerWithPermissions", password, LocalDate.now());
        ResponseEntity<String> response = tradingSystemImp.enter();
        String userToken = response.getBody();
        String userNameManager = "";
        String tokenManager = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            userNameManager = rootNode.get("username").asText();
            tokenManager = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken = tradingSystemImp.login(tokenManager, "v1", "managerWithPermissions", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            userNameManager = rootNode.get("username").asText();
            tokenManager = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystemImp.suggestManage(userName, token, userNameManager, storeName, true, true, true, true);
        tradingSystemImp.approveManage(userNameManager, tokenManager, storeName, userName);
        tradingSystemImp.appointManager(userName, token, userName, userNameManager, storeName, true, true, true, true);
        ResponseEntity<String> response2 = tradingSystemImp.removeProduct(userNameManager, tokenManager, storeName, productID);
        Assertions.assertEquals("Product was removed successfully.", response2.getBody());
        Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
    }

    @Test
    public void givenProductExist_WhenRemoveProduct_ThenSuccess() {
        ResponseEntity<String> response = tradingSystemImp.removeProduct(userName, token, storeName, productID);
        Assertions.assertEquals("Product was removed successfully.", response.getBody());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
