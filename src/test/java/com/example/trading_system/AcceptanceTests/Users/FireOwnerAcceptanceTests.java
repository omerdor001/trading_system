package com.example.trading_system.AcceptanceTests.Users;

import com.example.trading_system.domain.stores.Store;
import com.example.trading_system.domain.stores.StorePolicy;
import com.example.trading_system.domain.users.User;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class FireOwnerAcceptanceTests {

    private TradingSystemImp tradingSystemImp = TradingSystemImp.getInstance();
    private String password = "123456";
    private String userName ="";
    private String token ="";
    private String storeName = "Store1";
    private String ownerUserName = "";
    private String ownerToken = "";
    private String userNameManager = "";
    private String tokenManager = "";
    private String ownerUserName2 = "";
    private String ownerToken2 = "";

    @BeforeEach
    public void setUp() {
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
        userToken = tradingSystemImp.login(token,"0","admin", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            userName = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystemImp.register("owner",password, LocalDate.now());
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
        userToken2 = tradingSystemImp.login(ownerToken,"1","owner", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken2);
            ownerUserName = rootNode.get("username").asText();
            ownerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }

        tradingSystemImp.register("manager",password, LocalDate.now());
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
        userToken3 = tradingSystemImp.login(tokenManager,"2","manager", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken3);
            userNameManager = rootNode.get("username").asText();
            tokenManager = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }

        tradingSystemImp.register("owner2",password, LocalDate.now());
        tradingSystemImp.openSystem();
        ResponseEntity<String> response4 = tradingSystemImp.enter();
        String userToken4 = response4.getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken4);
            ownerUserName2 = rootNode.get("username").asText();
            ownerToken2 = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken4 = tradingSystemImp.login(ownerToken,"3","owner2", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken4);
            ownerUserName2 = rootNode.get("username").asText();
            ownerToken2 = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }

        tradingSystemImp.openStore(userName,token,storeName,"My Store is the best",new StorePolicy());
        tradingSystemImp.suggestOwner(userName,token,ownerUserName,storeName);
        tradingSystemImp.approveOwner(ownerUserName,ownerToken,storeName,userName);
        tradingSystemImp.suggestManage(ownerUserName,ownerToken,userNameManager,storeName,false,false,false,false);
        tradingSystemImp.approveManage(userNameManager,tokenManager,storeName,ownerUserName);
        tradingSystemImp.suggestOwner(ownerUserName,ownerToken,ownerUserName2,storeName);
        tradingSystemImp.approveOwner(ownerUserName2,ownerToken2,storeName,ownerUserName);
    }





    @Test
    public void GivenNotExistStore_WhenFireOwner_ThenThrowException(){
        ResponseEntity<String> res =tradingSystemImp.fireOwner(userName,token,"BadStoreName",ownerUserName);
        assertEquals("No store called BadStoreName exist",res.getBody());
        assertEquals(HttpStatus.BAD_REQUEST,res.getStatusCode());
    }

    @Test
    public void GivenOwnerNotExistUser_WhenFireOwner_ThenThrowException(){
        ResponseEntity<String> res =tradingSystemImp.fireOwner(userName,token,storeName,"BadUserName");
        assertEquals("No user called BadUserName exist",res.getBody());
        assertEquals(HttpStatus.BAD_REQUEST,res.getStatusCode());
    }

    @Test
    public void GivenNotOwnerUser_WhenFireOwner_ThenThrowException(){
        ResponseEntity<String> res =tradingSystemImp.fireOwner(userNameManager,tokenManager,storeName,ownerUserName2);
        assertEquals("The user that fire owner must be Owner",res.getBody());
        assertEquals(HttpStatus.BAD_REQUEST,res.getStatusCode());
    }



    @Test
    public void GivenNotOwnerFired_WhenFireOwner_ThenThrowException(){
        ResponseEntity<String> res = tradingSystemImp.fireOwner(userName,token,storeName,userNameManager);
        assertEquals("The user that will be fired must be Owner",res.getBody());
        assertEquals(HttpStatus.BAD_REQUEST,res.getStatusCode());
    }

    @Test
    public void GivenOwnerNotAppointedByThisOwner_WhenFireOwner_ThenThrowException(){

        ResponseEntity<String> res =tradingSystemImp.fireOwner(userName,token,storeName,ownerUserName2);
        assertEquals("Owner cant fire owner that he/she didn't appointed",res.getBody());
        assertEquals(HttpStatus.BAD_REQUEST,res.getStatusCode());

    }

    @Test
    public void GivenValidInput_WhenFireOwner_ThenSuccess(){
        ResponseEntity<String> res = tradingSystemImp.fireOwner(userName,token,storeName,ownerUserName);
        assertEquals("Success fire owner",res.getBody());
        assertEquals(HttpStatus.OK,res.getStatusCode());

        assertEquals("User doesn't have roles", tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName,token,storeName,userNameManager).getBody());

        assertEquals("User is not employeed in this store.", tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName,token,storeName,ownerUserName).getBody());

        assertEquals("User is not employeed in this store.", tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName,token,storeName,ownerUserName2).getBody());
    }
}
