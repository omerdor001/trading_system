package com.example.trading_system.AcceptanceTests.Users;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.service.TradingSystemImp;
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
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

public class EditProductsAcceptanceTest {
    private final String storeName = "Store1";
    private final String productName = "Product1";
    private final String[] keyWords = {"CarPlay", "iPhone"};
    private final int productID = 111;
    private TradingSystemImp tradingSystemImp;
    private String userName = "";
    private String token = "";
    private String ownerUserName = "";
    private String ownerToken = "";
    private String userNameManager = "";
    private String tokenManager = "";

    @BeforeEach
    public void setUp() {
        tradingSystemImp = TradingSystemImp.getInstance(mock(PaymentService.class), mock(DeliveryService.class), mock(NotificationSender.class));
        String password = "123456";
        tradingSystemImp.register("admin", password, LocalDate.now());
        tradingSystemImp.openSystem();
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
        tradingSystemImp.register("owner", password, LocalDate.now());
        tradingSystemImp.openSystem();
        ResponseEntity<String> response2 = tradingSystemImp.enter();
        String userToken2 = response2.getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken2);
            ownerUserName = rootNode.get("username").asText();
            ownerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken2 = tradingSystemImp.login(ownerToken, "v1", "owner", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken2);
            ownerUserName = rootNode.get("username").asText();
            ownerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystemImp.register("manager", password, LocalDate.now());
        tradingSystemImp.openSystem();
        ResponseEntity<String> response3 = tradingSystemImp.enter();
        String userToken3 = response3.getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken3);
            userNameManager = rootNode.get("username").asText();
            tokenManager = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken3 = tradingSystemImp.login(tokenManager, "v2", "manager", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken3);
            userNameManager = rootNode.get("username").asText();
            tokenManager = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystemImp.openStore(userName, token, storeName, "My Store is the best");
        tradingSystemImp.suggestOwner(userName, token, ownerUserName, storeName);
        tradingSystemImp.approveOwner(ownerUserName, ownerToken, storeName, userName);
        tradingSystemImp.appointOwner(userName, token, userName, ownerUserName, storeName);
        tradingSystemImp.suggestManage(ownerUserName, ownerToken, userNameManager, storeName, false, false, false, false);
        tradingSystemImp.approveManage(userNameManager, tokenManager, storeName, ownerUserName);
        tradingSystemImp.appointManager(ownerUserName, ownerToken, ownerUserName, userNameManager, storeName, false, false, false, false);
        tradingSystemImp.addProduct(ownerUserName, ownerToken, productID, storeName, productName, "oldDescription", 15.0, 6, 1, 1, new ArrayList<>(Arrays.asList(keyWords)));
    }

    @AfterEach
    public void tearDown() {
        tradingSystemImp.deleteInstance();
    }

    @Test
    public void GivenStoreNotExist_WhenSetProductName_ThenThrowException() {
        ResponseEntity<String> resp = tradingSystemImp.setProductName(ownerUserName, ownerToken, "BadStoreName", productID, "newProductName");
        Assertions.assertEquals("Store must exist", resp.getBody());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
    }

    @Test
    public void GivenProductNotExist_WhenSetProductName_ThenThrowException() {
        ResponseEntity<String> resp = tradingSystemImp.setProductName(ownerUserName, ownerToken, storeName, 222, "newProductName");
        Assertions.assertEquals("Product must exist", resp.getBody());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
    }

    @Test
    public void GivenManagerWithoutPermission_WhenSetProductName_ThenThrowException() {
        ResponseEntity<String> resp = tradingSystemImp.getStoreProducts(userName, token, storeName);
        Assertions.assertTrue(Objects.requireNonNull(resp.getBody()).contains("\"product_id\":111"));
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.editPermissionForManager(ownerUserName, ownerToken, userNameManager, storeName, false, false, true, true).getStatusCode());
        ResponseEntity<String> resp2 = tradingSystemImp.setProductName(userNameManager, tokenManager, storeName, productID, "newName");
        Assertions.assertEquals("Manager cannot edit products", resp2.getBody());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp2.getStatusCode());
    }

    @Test
    public void GivenManagerWithPermission_WhenSetProductName_ThenSuccess() {
        ResponseEntity<String> resp = tradingSystemImp.getStoreProducts(userName, token, storeName);
        Assertions.assertTrue(Objects.requireNonNull(resp.getBody()).contains("\"product_id\":111"));
        Assertions.assertTrue(resp.getBody().contains("\"product_name\":\"" + productName));
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.editPermissionForManager(ownerUserName, ownerToken, userNameManager, storeName, true, true, true, true).getStatusCode());
        ResponseEntity<String> resp2 = tradingSystemImp.setProductName(userNameManager, tokenManager, storeName, productID, "newName");
        Assertions.assertEquals("Product name was set successfully.", resp2.getBody());
        Assertions.assertEquals(HttpStatus.OK, resp2.getStatusCode());
        ResponseEntity<String> resp3 = tradingSystemImp.getProductInfo(userNameManager, tokenManager, storeName, productID);
        Assertions.assertTrue(Objects.requireNonNull(resp3.getBody()).contains("\"product_name\":\"newName"));
        Assertions.assertEquals(HttpStatus.OK, resp3.getStatusCode());
    }

    @Test
    public void GivenOwner_WhenSetProductName_ThenSuccess() {
        ResponseEntity<String> resp = tradingSystemImp.getStoreProducts(userName, token, storeName);
        Assertions.assertTrue(Objects.requireNonNull(resp.getBody()).contains("\"product_id\":111"));
        Assertions.assertTrue(resp.getBody().contains("\"product_name\":\"Product1"));
        ResponseEntity<String> resp2 = tradingSystemImp.setProductName(ownerUserName, ownerToken, storeName, productID, "newName2");
        Assertions.assertEquals("Product name was set successfully.", resp2.getBody());
        Assertions.assertEquals(HttpStatus.OK, resp2.getStatusCode());
        ResponseEntity<String> resp3 = tradingSystemImp.getProductInfo(ownerUserName, ownerToken, storeName, productID);
        Assertions.assertTrue(Objects.requireNonNull(resp3.getBody()).contains("\"product_name\":\"newName2"));
        Assertions.assertEquals(HttpStatus.OK, resp3.getStatusCode());
    }

    @Test
    public void GivenOwner_WhenSetProductDescription_ThenSuccess() {
        ResponseEntity<String> resp = tradingSystemImp.getStoreProducts(userName, token, storeName);
        Assertions.assertTrue(Objects.requireNonNull(resp.getBody()).contains("\"product_id\":111"));
        Assertions.assertTrue(resp.getBody().contains("\"product_description\":\"oldDescription"));
        ResponseEntity<String> resp2 = tradingSystemImp.setProductDescription(ownerUserName, ownerToken, storeName, productID, "newDescription");
        Assertions.assertEquals("Product description was set successfully.", resp2.getBody());
        Assertions.assertEquals(HttpStatus.OK, resp2.getStatusCode());
        ResponseEntity<String> resp3 = tradingSystemImp.getProductInfo(ownerUserName, ownerToken, storeName, productID);
        Assertions.assertTrue(Objects.requireNonNull(resp3.getBody()).contains("\"product_description\":\"newDescription"));
        Assertions.assertEquals(HttpStatus.OK, resp3.getStatusCode());
    }

    @Test
    public void GivenOwner_WhenSetProductPrice_ThenSuccess() {
        ResponseEntity<String> resp = tradingSystemImp.getStoreProducts(userName, token, storeName);
        Assertions.assertTrue(Objects.requireNonNull(resp.getBody()).contains("\"product_id\":111"));
        Assertions.assertTrue(resp.getBody().contains("\"product_price\":15.0"));
        ResponseEntity<String> resp2 = tradingSystemImp.setProductPrice(ownerUserName, ownerToken, storeName, productID, 16.0);
        Assertions.assertEquals("Product price was set successfully.", resp2.getBody());
        Assertions.assertEquals(HttpStatus.OK, resp2.getStatusCode());
        ResponseEntity<String> resp3 = tradingSystemImp.getProductInfo(ownerUserName, ownerToken, storeName, productID);
        Assertions.assertTrue(Objects.requireNonNull(resp3.getBody()).contains("\"product_price\":16"));
        Assertions.assertEquals(HttpStatus.OK, resp3.getStatusCode());
    }

    @Test
    public void GivenOwner_WhenSetProductQuantity_ThenSuccess() {
        ResponseEntity<String> resp = tradingSystemImp.getStoreProducts(userName, token, storeName);
        Assertions.assertTrue(Objects.requireNonNull(resp.getBody()).contains("\"product_id\":111"));
        Assertions.assertTrue(resp.getBody().contains("\"product_quantity\":6"));
        ResponseEntity<String> resp2 = tradingSystemImp.setProductQuantity(ownerUserName, ownerToken, storeName, productID, 8);
        Assertions.assertEquals("Product quantity was set successfully.", resp2.getBody());
        Assertions.assertEquals(HttpStatus.OK, resp2.getStatusCode());
        ResponseEntity<String> resp3 = tradingSystemImp.getProductInfo(ownerUserName, ownerToken, storeName, productID);
        Assertions.assertTrue(Objects.requireNonNull(resp3.getBody()).contains("\"product_quantity\":8"));
        Assertions.assertEquals(HttpStatus.OK, resp3.getStatusCode());
    }
}
