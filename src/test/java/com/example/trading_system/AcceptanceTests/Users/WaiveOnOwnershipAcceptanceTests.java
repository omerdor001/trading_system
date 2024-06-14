package com.example.trading_system.AcceptanceTests.Users;

import com.example.trading_system.domain.users.User;
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
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.fail;

public class WaiveOnOwnershipAcceptanceTests {


    private TradingSystemImp tradingSystemImp;
    private String password = "123456";
    private String userName = "";
    private String token = "";
    private String storeName = "Store1";
    private String ownerUserName = "";
    private String ownerToken = "";
    private String userNameManager = "";
    private String tokenManager = "";

    @BeforeEach
    public void setUp() {
        tradingSystemImp = TradingSystemImp.getInstance();
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
        userToken = tradingSystemImp.login(token, "0", "admin", password).getBody();
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
        userToken2 = tradingSystemImp.login(ownerToken, "1", "owner", password).getBody();
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
        userToken3 = tradingSystemImp.login(tokenManager,"1","manager", password).getBody();
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
    }


    @Test
    public void GivenStoreNotExist_WhenWaiveOnOwnerShip_ThenThrowException()
    {

        ResponseEntity<String> res = tradingSystemImp.waiverOnOwnership(ownerUserName,ownerToken,"BadStoreName");
        Assertions.assertEquals("No store called BadStoreName exist",res.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());

    }

    @Test
    public void GivenNotOwnerUser_WhenWaiveOnOwnership_ThenThrowException()
    {
        Assertions.assertEquals(HttpStatus.OK,tradingSystemImp.suggestManage(ownerUserName,ownerToken,userNameManager,storeName,true,true,true,true).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.approveManage(userNameManager,tokenManager,storeName,ownerUserName).getStatusCode());

        ResponseEntity<String> resp = tradingSystemImp.waiverOnOwnership(userNameManager,tokenManager,storeName);
        Assertions.assertEquals("User is not owner of this store",resp.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());


    }

    @Test
    public void GivenFounder_WhenWaiveOnOwnership_ThenThrowException()
    {
        ResponseEntity<String> resp = tradingSystemImp.waiverOnOwnership(userName,token,storeName);
        Assertions.assertEquals("founder cant waive on ownership",resp.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    public void GivenValidOwner_WhenWaiveOnOwnerShip_ThenSuccess(){ //check that manager minui also deleted

        Assertions.assertEquals(HttpStatus.OK,tradingSystemImp.suggestManage(ownerUserName,ownerToken,userNameManager,storeName,true,true,true,true).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.approveManage(userNameManager,tokenManager,storeName,ownerUserName).getStatusCode());

        ResponseEntity<String> resp = tradingSystemImp.waiverOnOwnership(ownerUserName,ownerToken,storeName);
        Assertions.assertEquals("Success waiver to own",resp.getBody());
        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());

        ResponseEntity<String> resp2 = tradingSystemImp.waiverOnOwnership(ownerUserName,ownerToken,storeName);
        Assertions.assertEquals("User is not owner of this store",resp2.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp2.getStatusCode());

        ResponseEntity<String> resp3 = tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName,token,storeName, ownerUserName);
        Assertions.assertEquals("User is not employeed in this store.",resp3.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp3.getStatusCode());

        ResponseEntity<String> resp4 = tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName,token,storeName, userNameManager);
        Assertions.assertEquals("User doesn't have roles",resp4.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp4.getStatusCode());

    }

    @AfterEach
    public void tearDown() {
        tradingSystemImp.deleteInstance();
    }
}