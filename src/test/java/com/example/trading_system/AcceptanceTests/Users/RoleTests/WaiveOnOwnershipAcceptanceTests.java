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

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

public class WaiveOnOwnershipAcceptanceTests {
    private TradingSystemImp tradingSystemImp;
    private String userName = "";
    private String token = "";
    private final String storeName = "Store1";
    private String ownerUserName = "";
    private String ownerToken = "";
    private String userNameManager = "";
    private String tokenManager = "";
    private UserRepository userRepository;
    private StoreRepository storeRepository;
    private String secondOwnerUserName = "";
    private String secondOwnerToken = "";
    private String thirdOwnerUserName = "";
    private String thirdOwnerToken = "";
    private String secondManagerToken = "";
    private String secondManagerUserName = "";
    private String thirdManagerUserName = "";
    private String thirdManagerToken = "";


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
        userToken3 = tradingSystemImp.login(tokenManager,"v2","manager", password).getBody();
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

    @AfterEach
    public void tearDown() {
        tradingSystemImp.deleteInstance();
    }

    @Test
    public void GivenValidOwner_WhenWaiveOnOwnerShip_ThenSuccess(){ //check that manager appointment also deleted
        Assertions.assertEquals(HttpStatus.OK,tradingSystemImp.suggestManage(ownerUserName,ownerToken,userNameManager,storeName,true,true,true,true, true).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.approveManage(userNameManager,tokenManager,storeName,ownerUserName,true,true,true,true, true).getStatusCode());
        ResponseEntity<String> resp = tradingSystemImp.waiverOnOwnership(ownerUserName,ownerToken,storeName);
        Assertions.assertEquals("Success waiver to own",resp.getBody());
        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
        ResponseEntity<String> resp2 = tradingSystemImp.waiverOnOwnership(ownerUserName,ownerToken,storeName);
        Assertions.assertEquals("User is not owner of this store",resp2.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp2.getStatusCode());
        ResponseEntity<String> resp3 = tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName,token,storeName, ownerUserName);
        Assertions.assertEquals("User is not employed in this store.",resp3.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp3.getStatusCode());
        ResponseEntity<String> resp4 = tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName,token,storeName, userNameManager);
        Assertions.assertEquals("User is not employed in this store.",resp4.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp4.getStatusCode());
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
        Assertions.assertEquals(HttpStatus.OK,tradingSystemImp.suggestManage(ownerUserName,ownerToken,userNameManager,storeName,true,true,true,true, true).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.approveManage(userNameManager,tokenManager,storeName,ownerUserName,true,true,true,true, true).getStatusCode());
        ResponseEntity<String> resp = tradingSystemImp.waiverOnOwnership(userNameManager,tokenManager,storeName);
        Assertions.assertEquals("User is not owner of this store",resp.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    public void GivenFounder_WhenWaiveOnOwnership_ThenThrowException()
    {
        ResponseEntity<String> resp = tradingSystemImp.waiverOnOwnership(userName,token,storeName);
        Assertions.assertEquals("Founder cant waive on ownership",resp.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }


//    private void registerAndLogin(String userName){
//        tradingSystemImp.register("admin", password, LocalDate.now());
//
//        ResponseEntity<String> response2 = tradingSystemImp.enter();
//        String userToken2 = response2.getBody();
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode rootNode = objectMapper.readTree(userToken2);
//            ownerUserName = rootNode.get("username").asText();
//            ownerToken = rootNode.get("token").asText();
//        } catch (Exception e) {
//            fail("Setup failed: Unable to extract username and token from JSON response");
//        }
//        userToken2 = tradingSystemImp.login(ownerToken, "v1", "owner", password).getBody();
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode rootNode = objectMapper.readTree(userToken2);
//            ownerUserName = rootNode.get("username").asText();
//            ownerToken = rootNode.get("token").asText();
//        } catch (Exception e) {
//            fail("Setup failed: Unable to extract username and token from JSON response");
//        }
//
//
//    }


    @Test
    public void GivenRecursiveOwners_WhenWaiveOnOwnership_ThenSuccess(){
        String password = "123456";

        tradingSystemImp.register("owner2", password, LocalDate.now());

        ResponseEntity<String> response2 = tradingSystemImp.enter();
        String userToken2 = response2.getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken2);
            secondOwnerUserName = rootNode.get("username").asText();
            secondOwnerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }

        userToken2 = tradingSystemImp.login(secondOwnerToken, "v3", "owner2", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken2);
            secondOwnerUserName = rootNode.get("username").asText();
            secondOwnerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }

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


        Assertions.assertEquals(HttpStatus.OK,tradingSystemImp.suggestManage(ownerUserName,ownerToken,userNameManager,storeName,true,true,true,true, true).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.approveManage(userNameManager,tokenManager,storeName,ownerUserName,true,true,true,true, true).getStatusCode());

        Assertions.assertEquals(HttpStatus.OK,tradingSystemImp.suggestOwner(ownerUserName,ownerToken,secondOwnerUserName,storeName).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK,tradingSystemImp.approveOwner(secondOwnerUserName,secondOwnerToken,storeName,ownerUserName).getStatusCode());

        Assertions.assertEquals(HttpStatus.OK,tradingSystemImp.suggestManage(secondOwnerUserName, secondOwnerToken, secondManagerUserName, storeName, true, true, true, true, true).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.approveManage(secondManagerUserName, secondManagerToken, storeName, secondOwnerUserName, true, true, true, true, true).getStatusCode());

        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestOwner(secondOwnerUserName,secondOwnerToken,thirdOwnerUserName, storeName).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.approveOwner(thirdOwnerUserName, thirdOwnerToken, storeName, secondOwnerUserName).getStatusCode());

        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestManage(thirdOwnerUserName, thirdOwnerToken, thirdManagerUserName, storeName, true ,true, true, true, true).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.approveManage(thirdManagerUserName, thirdManagerToken, storeName, thirdOwnerUserName, true ,true ,true ,true, true).getStatusCode());


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
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName,token,storeName, secondOwnerUserName).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName,token,storeName, secondManagerUserName).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName,token,storeName, thirdOwnerUserName).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName,token,storeName, thirdManagerUserName).getStatusCode());


        ResponseEntity<String> resp = tradingSystemImp.waiverOnOwnership(ownerUserName,ownerToken,storeName);
        Assertions.assertEquals("Success waiver to own",resp.getBody());
        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
        ResponseEntity<String> resp2 = tradingSystemImp.waiverOnOwnership(ownerUserName,ownerToken,storeName);
        Assertions.assertEquals("User is not owner of this store",resp2.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp2.getStatusCode());
        ResponseEntity<String> resp3 = tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName,token,storeName, ownerUserName);
        Assertions.assertEquals("User is not employed in this store.",resp3.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp3.getStatusCode());
        ResponseEntity<String> resp4 = tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName,token,storeName, userNameManager);
        Assertions.assertEquals("User is not employed in this store.",resp4.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp4.getStatusCode());
        ResponseEntity<String> resp5 = tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName,token,storeName, secondOwnerUserName);
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