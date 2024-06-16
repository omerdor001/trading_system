package com.example.trading_system.AcceptanceTests.Users;

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

public class OpenStoreExistAcceptanceTests {

    private TradingSystemImp tradingSystemImp ;
    private String token;
    private String userName;
    private String password = "123456";
    private String storeName = "Store1";

    @BeforeEach
    public void setUp() {
        tradingSystemImp = TradingSystemImp.getInstance();
        tradingSystemImp.register("admin",password, LocalDate.now());
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
        userToken = tradingSystemImp.login(token,"v0","admin", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            userName = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystemImp.openStore(userName,token,storeName,"My Store is the best");
        String[] keyWords = {"CarPlay", "iPhone"};
        tradingSystemImp.addProduct(userName,token,111,storeName,"CarPlay","CarPlay for iPhones",15,5,6,1, new ArrayList<>(Arrays.asList(keyWords)));
        tradingSystemImp.closeStoreExist(userName,token,storeName);
    }


    @Test
    public void givenNotExistStore_WhenOpenStoreExist_ThenThrowException(){
        String response =tradingSystemImp.openStoreExist(userName,token,"Store2").getBody();
        Assertions.assertEquals("Store must exist to close",response);

    }


    @Test
    public void givenNotFounderTryCloseStore_WhenCloseStoreExist_ThenThrowException(){
        tradingSystemImp.register("owner",password, LocalDate.now());
        tradingSystemImp.openSystem();
        ResponseEntity<String> response = tradingSystemImp.enter();
        String userToken = response.getBody();
        String userNameOwner ="";
        String tokenOwner = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            userNameOwner = rootNode.get("username").asText();
            tokenOwner = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken = tradingSystemImp.login(tokenOwner,"v1","owner", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            userNameOwner = rootNode.get("username").asText();
            tokenOwner = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystemImp.suggestOwner(userName,token,userNameOwner,storeName);
        tradingSystemImp.approveOwner(userNameOwner,tokenOwner,storeName,userName);
        String response2 = tradingSystemImp.openStoreExist(userNameOwner,tokenOwner,storeName).getBody();

        Assertions.assertEquals("Only founder can open store exist",response2);

    }

    @Test
    public void givenOpenStoreTwoTimes_WhenCloseStoreExist_ThenThrowException(){
        Assertions.assertEquals(HttpStatus.OK,tradingSystemImp.openStoreExist(userName, token, storeName).getStatusCode());

        ResponseEntity<String> response = tradingSystemImp.openStoreExist(userName,token,storeName);
        Assertions.assertEquals("Store is already active",response.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }

    @Test
    public void givenValidStoreToClose_WhenCloseStoreExist_ThenSuccess(){
        ResponseEntity<String> response2 = tradingSystemImp.searchNameInStore(userName,"CarPlay", token,storeName,1.0,1000.0,1.0,1);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST,response2.getStatusCode());
        Assertions.assertEquals("Store is not open, can't search",response2.getBody());

        ResponseEntity<String> response3 = tradingSystemImp.openStoreExist(userName,token,storeName);
        Assertions.assertEquals(HttpStatus.OK,response3.getStatusCode());
        Assertions.assertEquals("FINISHED open store exist successfully", response3.getBody());

        Assertions.assertEquals(HttpStatus.OK,tradingSystemImp.searchNameInStore(userName,"CarPlay", token,storeName,1.0,1000.0,1.0,1).getStatusCode());
    } //, also valid notification

    @AfterEach
    public void tearDown(){
        tradingSystemImp.deleteInstance();
    }
}
