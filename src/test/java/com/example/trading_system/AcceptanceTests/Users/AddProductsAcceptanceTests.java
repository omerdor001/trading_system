package com.example.trading_system.AcceptanceTests.Users;

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

import static org.junit.jupiter.api.Assertions.fail;

public class AddProductsAcceptanceTests {


    private TradingSystemImp tradingSystemImp = TradingSystemImp.getInstance();
    private String password = "123456";
    private String userName ="";
    private String token ="";
    private String storeName = "Store1";
    private String productName = "Product1";
    private String[] keyWords = {"CarPlay", "iPhone"};

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
        tradingSystemImp.openStore(userName,token,storeName,"My Store is the best");
    }



    @Test
    public void givenNotExistStore_WhenAddProduct_ThenThrowException(){
        String response =tradingSystemImp.addProduct(userName,token,111,"BadStoreName",productName,"newDescription",15.0,6,1,1,new ArrayList<>(Arrays.asList(keyWords))).getBody();
        Assertions.assertEquals("Store must exist",response);

    }

    @Test
    public void givenManagerWithoutPermission_WhenAddProduct_ThenThrowException(){

        tradingSystemImp.register("managerWithoutPermissions",password, LocalDate.now());
        ResponseEntity<String> response = tradingSystemImp.enter();
        String userToken = response.getBody();
        String userNameManager ="";
        String tokenManager = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            userNameManager = rootNode.get("username").asText();
            tokenManager = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken = tradingSystemImp.login(tokenManager,"1","managerWithoutPermissions", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            userNameManager = rootNode.get("username").asText();
            tokenManager = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystemImp.suggestManage(userName,token,userNameManager,storeName,true,false,true,true);
        tradingSystemImp.approveManage(userNameManager,tokenManager,storeName,userName);
        ResponseEntity<String> response2 =tradingSystemImp.addProduct(userNameManager,tokenManager,111,storeName,productName,"newDescription",15.0,6,1,1,new ArrayList<>(Arrays.asList(keyWords)));
        Assertions.assertEquals("Manager cannot add products",response2.getBody());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response2.getStatusCode());

    }

    @Test
    public void givenManagerWithPermission_WhenAddProduct_ThenThrowException(){
        tradingSystemImp.register("managerWithPermissions",password, LocalDate.now());
        ResponseEntity<String> response = tradingSystemImp.enter();
        String userToken = response.getBody();
        String userNameManager ="";
        String tokenManager = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            userNameManager = rootNode.get("username").asText();
            tokenManager = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken = tradingSystemImp.login(tokenManager,"1","managerWithPermissions", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            userNameManager = rootNode.get("username").asText();
            tokenManager = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystemImp.suggestManage(userName,token,userNameManager,storeName,true,true,true,true);
        tradingSystemImp.approveManage(userNameManager,tokenManager,storeName,userName);
        ResponseEntity<String> response2 =tradingSystemImp.addProduct(userNameManager,tokenManager,111,storeName,productName,"newDescription",15.0,6,1,1,new ArrayList<>(Arrays.asList(keyWords)));
        Assertions.assertEquals("Product was added successfully.",response2.getBody());
        Assertions.assertEquals(HttpStatus.OK,response2.getStatusCode());

    }

    @Test
    public void givenRegularUser_WhenAddProduct_ThenThrowException(){

        tradingSystemImp.register("regularUser",password, LocalDate.now());
        ResponseEntity<String> response = tradingSystemImp.enter();
        String userToken = response.getBody();
        String regularUser ="";
        String regularToken = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            regularUser = rootNode.get("username").asText();
            regularToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken = tradingSystemImp.login(regularToken,"1","regularUser", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            regularUser = rootNode.get("username").asText();
            regularToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        ResponseEntity<String> response2 =tradingSystemImp.addProduct(regularUser,regularToken,111,storeName,productName,"newDescription",15.0,6,1,1,new ArrayList<>(Arrays.asList(keyWords)));
        Assertions.assertEquals("User doesn't have roles",response2.getBody());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response2.getStatusCode());

    }

    @Test
    public void givenProductIDExist_WhenAddProduct_ThenThrowException(){
        ResponseEntity<String> response =tradingSystemImp.addProduct(userName,token,111,storeName,productName,"newDescription",15.0,6,1,1,new ArrayList<>(Arrays.asList(keyWords)));
        Assertions.assertEquals("Product was added successfully.",response.getBody());
        Assertions.assertEquals(HttpStatus.OK,response.getStatusCode());
        ResponseEntity<String> response2 =tradingSystemImp.addProduct(userName,token,111,storeName,"anotherProduct","newDescription",15.0,6,1,1,new ArrayList<>(Arrays.asList(keyWords)));
        Assertions.assertEquals("Product with id 111 already exists",response2.getBody());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response2.getStatusCode());
    }

    @Test
    public void givenNegativePrice_WhenAddProduct_ThenThrowException(){
        String response =tradingSystemImp.addProduct(userName,token,111,storeName,productName,"newDescription",-15.0,6,1,1,new ArrayList<>(Arrays.asList(keyWords))).getBody();
        Assertions.assertEquals("Price can't be negative number",response);

    }
    @Test
    public void givenNegativeQuantity_WhenAddProduct_ThenThrowException(){
        String response =tradingSystemImp.addProduct(userName,token,111,storeName,productName,"newDescription",15.0,-3,1,1,new ArrayList<>(Arrays.asList(keyWords))).getBody();
        Assertions.assertEquals("Quantity must be natural number",response);

    }

    @Test
    public void givenNegativeRating_WhenAddProduct_ThenThrowException(){
        String response =tradingSystemImp.addProduct(userName,token,111,storeName,productName,"newDescription",15.0,6,-3,1,new ArrayList<>(Arrays.asList(keyWords))).getBody();
        Assertions.assertEquals("Rating can't be negative number",response);

    }

    @Test
    public void givenOwner_WhenAddProduct_ThenSuccess(){
        ResponseEntity<String> response = tradingSystemImp.addProduct(userName,token,111,storeName,productName,"newDescription",15.0,6,1,1,new ArrayList<>(Arrays.asList(keyWords)));
        Assertions.assertEquals(HttpStatus.OK,response.getStatusCode());
        Assertions.assertEquals("Product was added successfully.",response.getBody());
    }


    @AfterEach
    public void tearDown(){
        try {
            tradingSystemImp.removeProduct(userName,token,storeName,111);
        }
        catch (Exception e){}
    }



}
