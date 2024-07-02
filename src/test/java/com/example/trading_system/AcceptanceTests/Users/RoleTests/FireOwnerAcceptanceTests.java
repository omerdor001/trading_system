package com.example.trading_system.AcceptanceTests.Users.RoleTests;

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
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

public class FireOwnerAcceptanceTests {

    private TradingSystemImp tradingSystemImp;
    private String userName = "";
    private String token = "";
    private final String storeName = "Store1";
    private String ownerUserName = "";
    private String ownerToken = "";
    private String userNameManager = "";
    private String tokenManager = "";
    private String ownerUserName2 = "";
    private String ownerToken2 = "";
    private UserRepository userRepository;
    private StoreRepository storeRepository;

    @BeforeEach
    public void setUp() {
        userRepository= UserMemoryRepository.getInstance();    //May be change later
        storeRepository= StoreMemoryRepository.getInstance();  //May be change later
        tradingSystemImp = TradingSystemImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class), mock(NotificationSender.class),userRepository,storeRepository);
        String password = "123456";
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
        tradingSystemImp.register("owner", password, LocalDate.now());
        tradingSystemImp.openSystem(storeRepository);
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
        tradingSystemImp.openSystem(storeRepository);
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
        tradingSystemImp.register("owner2", password, LocalDate.now());
        tradingSystemImp.openSystem(storeRepository);
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
        userToken4 = tradingSystemImp.login(ownerToken, "v3", "owner2", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken4);
            ownerUserName2 = rootNode.get("username").asText();
            ownerToken2 = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystemImp.openStore(userName, token, storeName, "My Store is the best");
        tradingSystemImp.suggestOwner(userName, token, ownerUserName, storeName);
        tradingSystemImp.approveOwner(ownerUserName, ownerToken, storeName, userName);
        tradingSystemImp.suggestManage(ownerUserName, ownerToken, userNameManager, storeName, false, false, false, false);
        tradingSystemImp.approveManage(userNameManager, tokenManager, storeName, ownerUserName, false, false, false, false);
        tradingSystemImp.suggestOwner(ownerUserName, ownerToken, ownerUserName2, storeName);
        tradingSystemImp.approveOwner(ownerUserName2, ownerToken2, storeName, ownerUserName);
    }

    @AfterEach
    public void tearDown() {
        tradingSystemImp.deleteInstance();
    }

    @Test
    public void GivenNotExistStore_WhenFireOwner_ThenThrowException() {
        ResponseEntity<String> res = tradingSystemImp.fireOwner(userName, token, "BadStoreName", ownerUserName);
        assertEquals("No store called BadStoreName exist", res.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void GivenOwnerNotExistUser_WhenFireOwner_ThenThrowException() {
        ResponseEntity<String> res = tradingSystemImp.fireOwner(userName, token, storeName, "BadUserName");
        assertEquals("No user called BadUserName exist", res.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void GivenNotOwnerUser_WhenFireOwner_ThenThrowException() {
        ResponseEntity<String> res = tradingSystemImp.fireOwner(userNameManager, tokenManager, storeName, ownerUserName2);
        assertEquals("The user that fire owner must be Owner", res.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }


    @Test
    public void GivenNotOwnerFired_WhenFireOwner_ThenThrowException() {
        ResponseEntity<String> res = tradingSystemImp.fireOwner(userName, token, storeName, userNameManager);
        assertEquals("The user that will be fired must be Owner", res.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void GivenOwnerNotAppointedByThisOwner_WhenFireOwner_ThenThrowException() {
        ResponseEntity<String> res = tradingSystemImp.fireOwner(userName, token, storeName, ownerUserName2);
        assertEquals("Owner cant fire owner that he/she didn't appointed", res.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void GivenValidInput_WhenFireOwner_ThenSuccess() {
        ResponseEntity<String> res = tradingSystemImp.fireOwner(userName, token, storeName, ownerUserName);
        assertEquals("Success fire owner", res.getBody());
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals("User is not employed in this store.", tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName, token, storeName, userNameManager).getBody());
        assertEquals("User is not employed in this store.", tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName, token, storeName, ownerUserName).getBody());
        assertEquals("User is not employed in this store.", tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName, token, storeName, ownerUserName2).getBody());
    }



    @Test
    public void GivenRecursiveOwners_WhenFireOwner_ThenSuccess(){
        String password = "123456";
        String thirdOwnerUserName = "";
        String thirdOwnerToken = "";

        tradingSystemImp.register("owner3", password, LocalDate.now());

        ResponseEntity<String> response3 = tradingSystemImp.enter();
        String userToken3 = response3.getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken3);
            thirdOwnerUserName = rootNode.get("username").asText();
            thirdOwnerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken3 = tradingSystemImp.login(thirdOwnerToken, "v4", "owner3", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken3);
            thirdOwnerUserName = rootNode.get("username").asText();
            thirdOwnerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }


        String secondManagerUserName = "";
        String secondManagerToken = "";

        tradingSystemImp.register("manager2", password, LocalDate.now());

        ResponseEntity<String> response4 = tradingSystemImp.enter();
        String userToken4 = response4.getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken4);
            secondManagerUserName = rootNode.get("username").asText();
            secondManagerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken4 = tradingSystemImp.login(secondManagerToken, "v5", "manager2", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken4);
            secondManagerUserName = rootNode.get("username").asText();
            secondManagerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }


        String thirdManagerUserName = "";
        String thirdManagerToken = "";

        tradingSystemImp.register("manager3", password, LocalDate.now());

        ResponseEntity<String> response5 = tradingSystemImp.enter();
        String userToken5 = response5.getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken5);
            thirdManagerUserName = rootNode.get("username").asText();
            thirdManagerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken5 = tradingSystemImp.login(thirdManagerToken, "v6", "manager3", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken5);
            thirdManagerUserName = rootNode.get("username").asText();
            thirdManagerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }



        Assertions.assertEquals(HttpStatus.OK,tradingSystemImp.suggestManage(ownerUserName2, ownerToken2, secondManagerUserName, storeName, true, true, true, true).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.approveManage(secondManagerUserName, secondManagerToken, storeName, ownerUserName2, true, true, true, true).getStatusCode());

        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestOwner(ownerUserName2,ownerToken2,thirdOwnerUserName, storeName).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.approveOwner(thirdOwnerUserName, thirdOwnerToken, storeName, ownerUserName2).getStatusCode());

        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestManage(thirdOwnerUserName, thirdOwnerToken, thirdManagerUserName, storeName, true ,true, true, true).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.approveManage(thirdManagerUserName, thirdManagerToken, storeName, thirdOwnerUserName, true ,true ,true ,true).getStatusCode());


        tradingSystemImp.register("owner4", password, LocalDate.now());

        ResponseEntity<String> response6 = tradingSystemImp.enter();
        String userToken6 = response6.getBody();
        String fourthOwnerToken = "";
        String fourthOwnerUserName = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken6);
            fourthOwnerUserName = rootNode.get("username").asText();
            fourthOwnerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken6 = tradingSystemImp.login(thirdManagerToken, "v7", "owner4", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken6);
            fourthOwnerUserName = rootNode.get("username").asText();
            fourthOwnerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }

        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestOwner(userName, token, fourthOwnerUserName, storeName).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.approveOwner(fourthOwnerUserName, fourthOwnerToken, storeName, userName).getStatusCode());


        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName,token,storeName, userNameManager).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName,token,storeName, ownerUserName2).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName,token,storeName, secondManagerUserName).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName,token,storeName, thirdOwnerUserName).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName,token,storeName, thirdManagerUserName).getStatusCode());


        ResponseEntity<String> resp = tradingSystemImp.fireOwner(userName,token,storeName,ownerUserName);
        Assertions.assertEquals("Success fire owner",resp.getBody());
        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
        ResponseEntity<String> resp3 = tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName,token,storeName, ownerUserName);
        Assertions.assertEquals("User is not employed in this store.",resp3.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp3.getStatusCode());
        ResponseEntity<String> resp4 = tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName,token,storeName, userNameManager);
        Assertions.assertEquals("User is not employed in this store.",resp4.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp4.getStatusCode());
        ResponseEntity<String> resp5 = tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName,token,storeName, ownerUserName2);
        Assertions.assertEquals("User is not employed in this store.",resp5.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp5.getStatusCode());
        ResponseEntity<String> resp6 = tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName,token,storeName, secondManagerUserName);
        Assertions.assertEquals("User is not employed in this store.",resp6.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp6.getStatusCode());

        ResponseEntity<String> resp7 = tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName,token,storeName, thirdOwnerUserName);
        Assertions.assertEquals("User is not employed in this store.",resp7.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp7.getStatusCode());

        ResponseEntity<String> resp8 = tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName,token,storeName, thirdManagerUserName);
        Assertions.assertEquals("User is not employed in this store.",resp8.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp8.getStatusCode());

        ResponseEntity<String> resp9 = tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName,token,storeName, fourthOwnerUserName);
        Assertions.assertEquals(HttpStatus.OK, resp9.getStatusCode());
        Assertions.assertNotEquals("User is not employed in this store.",resp9.getBody());

    }
}
