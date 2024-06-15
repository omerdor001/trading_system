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

public class ManagerAppointmentAcceptanceTests {

    private TradingSystemImp tradingSystemImp;
    private String password = "123456";
    private String userName ="";
    private String token ="";
    private String storeName = "Store1";
    private String tokenManager ="";
    private String userNameManager ="";
    private String regularUser = "";
    private String regularUserToken = "";

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

        tradingSystemImp.register("manager", password, LocalDate.now());
        tradingSystemImp.openSystem();
        ResponseEntity<String> response2 = tradingSystemImp.enter();
        String userToken2 = response2.getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken2);
            userNameManager = rootNode.get("username").asText();
            tokenManager = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken2 = tradingSystemImp.login(tokenManager, "v1", "manager", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken2);
            userNameManager = rootNode.get("username").asText();
            tokenManager = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
    }

        @Test
        public void GivenStoreNotExist_WhenSuggestManage_ThenThrowException(){

            ResponseEntity<String> resp = tradingSystemImp.suggestManage(userName,token,userNameManager,"BadStoreName",true,true,true,true);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
            Assertions.assertEquals("No store called BadStoreName exist", resp.getBody());

        }

        @Test
        public void GivenManagerNotExist_WhenSuggestManage_ThenThrowException()
        {
            ResponseEntity<String> resp = tradingSystemImp.suggestManage(userName,token,"badUserName",storeName,true,true,true,true);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
            Assertions.assertEquals("No user called badUserName exist", resp.getBody());
        }

        @Test
        public void GivenAppointerIsNotOwner_WhenSuggestManage_ThenThrowException(){
            ResponseEntity<String> resp = tradingSystemImp.suggestManage(userNameManager,tokenManager,userNameManager,storeName,true,true,true,true);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
            Assertions.assertEquals("User must be Owner", resp.getBody());
        }

        @Test
        public void GivenAppointerIsLogout_WhenSuggestManage_ThenThrowException(){
            tradingSystemImp.logout(token, userName);
            ResponseEntity<String> resp = tradingSystemImp.suggestManage(userName,token,userNameManager,storeName,true,true,true,true);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
            Assertions.assertEquals("Appoint user is not logged", resp.getBody());
        }

        @Test
        public void GivenNewManagerIsAlreadyManager_WhenSuggestManage_ThenThrowException(){
            Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestManage(userName,token,userNameManager,storeName,true,true,true,true).getStatusCode());
            Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.approveManage(userNameManager,tokenManager,storeName,userName).getStatusCode());
            ResponseEntity<String> resp = tradingSystemImp.suggestManage(userName,token,userNameManager,storeName,true,true,true,true);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
            Assertions.assertEquals("User already Manager of this store", resp.getBody());
        }

        @Test
        public void GivenNewManagerIsAlreadyOwner_WhenSuggestManage_ThenThrowException(){
            ResponseEntity<String> resp = tradingSystemImp.suggestManage(userName,token,userName,storeName,true,true,true,true);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
            Assertions.assertEquals("User cannot be owner of this store", resp.getBody());
        }

        @Test
        public void GivenValidInput_WhenSuggestManage_ThenSuccess(){
            Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestManage(userName,token,userNameManager,storeName,true,true,true,true).getStatusCode());
        }

        @Test
                public void GivenNotSuggestManage_WhenApproveManage_ThenThrowException(){
            ResponseEntity<String> res = tradingSystemImp.approveManage(userNameManager,tokenManager,storeName,userName);
            Assertions.assertEquals("No one suggest manager to this user",res.getBody());
            Assertions.assertEquals(HttpStatus.BAD_REQUEST,res.getStatusCode());
    }

        @Test
        public void GivenStoreNotExist_WhenApproveManage_ThenThrowException(){
            Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestManage(userName,token,userNameManager,storeName,true,true,true,true).getStatusCode());

           ResponseEntity<String> res = tradingSystemImp.approveManage(userNameManager,tokenManager,"BadStoreName",userName);
           Assertions.assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
            Assertions.assertEquals("No store called BadStoreName exist", res.getBody());

        }

        @Test
        public void GivenNotExistAppointer_WhenApproveManage_ThenThrowException(){

            Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestManage(userName,token,userNameManager,storeName,true,true,true,true).getStatusCode());

            ResponseEntity<String> res = tradingSystemImp.approveManage(userNameManager,tokenManager,storeName,"BadName");
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
            Assertions.assertEquals("No user called BadName exist", res.getBody());

        }

        @Test
        public void GivenAppointerIsNotOwner_WhenApproveManage_ThenThrowException(){
            Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestManage(userName,token,userNameManager,storeName,true,true,true,true).getStatusCode());

            ResponseEntity<String> res = tradingSystemImp.approveManage(userNameManager,tokenManager,storeName,userNameManager);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
            Assertions.assertEquals("User must be Owner", res.getBody());
        }

        @Test
        public void GivenUserIsAlreadyManager_WhenApproveManage_ThenThrowException(){
            Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestManage(userName,token,userNameManager,storeName,true,true,true,true).getStatusCode());
            Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.approveManage(userNameManager,tokenManager,storeName,userName).getStatusCode());

            ResponseEntity<String> resp = tradingSystemImp.approveManage(userNameManager,tokenManager,storeName,userName);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
            Assertions.assertEquals("User already Manager of this store", resp.getBody());

        }

        @Test
        public void GivenValidInput_WhenApproveManager_ThenSuccess(){
            Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestManage(userName,token,userNameManager,storeName,true,true,true,true).getStatusCode());
            ResponseEntity<String> resp = tradingSystemImp.approveManage(userNameManager,tokenManager,storeName,userName);
            Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
            Assertions.assertEquals("Success approving manage", resp.getBody());
        }

        @Test
        public void GivenStoreNotExist_WhenRejectManage_ThenThrowException(){
            Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestManage(userName,token,userNameManager,storeName,true,true,true,true).getStatusCode());
            ResponseEntity<String> res = tradingSystemImp.rejectToManageStore(userNameManager,tokenManager,"BadStoreName",userName);

            Assertions.assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
            Assertions.assertEquals("No store called BadStoreName exist", res.getBody());

        }

        @Test
        public void GivenAppointerNotExist_WhenRejectManage_ThenThrowException(){
            Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestManage(userName,token,userNameManager,storeName,true,true,true,true).getStatusCode());

            ResponseEntity<String> res = tradingSystemImp.rejectToManageStore(userNameManager,tokenManager,storeName,"BadUserName");

            Assertions.assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
            Assertions.assertEquals("No user called BadUserName exist", res.getBody());

        }

        @Test
        public void GivenAppointerIsNotOwner_WhenRejectManage_ThenThrowException(){
            Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestManage(userName,token,userNameManager,storeName,true,true,true,true).getStatusCode());

            ResponseEntity<String> res = tradingSystemImp.rejectToManageStore(userNameManager,tokenManager,storeName,userNameManager);

            Assertions.assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
            Assertions.assertEquals("User must be Owner", res.getBody());


        }

        @Test
        public void GivenAlreadyManager_WhenRejectManage_ThenThrowException(){

            Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestManage(userName,token,userNameManager,storeName,true,true,true,true).getStatusCode());
            Assertions.assertEquals(HttpStatus.OK,tradingSystemImp.approveManage(userNameManager,tokenManager,storeName,userName).getStatusCode());

            ResponseEntity<String> res = tradingSystemImp.rejectToManageStore(userNameManager,tokenManager,storeName,userName);

            Assertions.assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
            Assertions.assertEquals("User already Manager of this store", res.getBody());


        }


        @Test
        public void GivenNoOneSuggest_WhenRejectManage_ThenThrowException(){
            ResponseEntity<String> res = tradingSystemImp.rejectToManageStore(userNameManager,tokenManager,storeName,userName);
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
            Assertions.assertEquals("No one suggest this user to be a manager", res.getBody());
        }

        @Test
        public void GivenValidInput_WhenRejectManage_ThenSuccess(){
        Assertions.assertEquals(HttpStatus.OK,tradingSystemImp.suggestManage(userName,token,userNameManager,storeName,true,true,true,true).getStatusCode());

        ResponseEntity<String> response = tradingSystemImp.rejectToManageStore(userNameManager,tokenManager,storeName,userName);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Success rejecting manage", response.getBody());
        }




    @AfterEach
                public void tearDown(){
            tradingSystemImp.deleteInstance();
        }


//    all cases :
//    reject all fails  (after suggest)
//        success cases

}

