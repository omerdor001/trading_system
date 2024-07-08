package com.example.trading_system.AcceptanceTests.Market.BidAcceptanceTests;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.Store;
import com.example.trading_system.domain.stores.StoreMemoryRepository;
import com.example.trading_system.domain.stores.StoreRepository;
import com.example.trading_system.domain.users.UserMemoryRepository;
import com.example.trading_system.domain.users.UserRepository;
import com.example.trading_system.service.TradingSystem;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.net.http.HttpClient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

public class LotteryAcceptanceTests {
    private NotificationSender mockNotificationSender;
    private TradingSystem tradingSystem;
    private String ownerToken;
    private String ownerUserName;
    private String regularUserToken;
    private String regularUserName;
    private String secondOwnerUserName = "owner2";
    private String secondOwnerToken;
    private final String password = "123456";
    private final String productName = "Product1";
    private final LinkedList<String> keyWords = new LinkedList<>(Arrays.asList("CarPlay", "iPhone"));

    private final String storeName = "Store1";
    private UserRepository userRepository;
    private StoreRepository storeRepository;
    private int productID = 111 ;
    private String productDescription = "Product1 Description";

    @BeforeEach
    void setUp() {
        userRepository = UserMemoryRepository.getInstance();    //May be change later
        storeRepository = StoreMemoryRepository.getInstance();  //May be change later
        mockNotificationSender = mock(NotificationSender.class);
        tradingSystem = TradingSystemImp.getInstance(mock(PaymentService.class), mock(DeliveryService.class), mockNotificationSender, userRepository, storeRepository);
        tradingSystem.register("owner1", "password123", LocalDate.now());
        tradingSystem.openSystem(storeRepository);
        String userToken = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            ownerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
        userToken = tradingSystem.login(ownerToken, "v0", "owner1", "password123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            ownerUserName = rootNode.get("username").asText();
            ownerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystem.openStore(ownerUserName, ownerToken, storeName, "General Store");
        tradingSystem.addProduct(ownerUserName, ownerToken, productID, storeName, productName, productDescription, 15, 5, 6, 1, "[\"CarPlay\", \"iPhone\"]");

        tradingSystem.register("regularUser", "password123", LocalDate.now());
        String userToken2 = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken2);
            regularUserToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
        userToken2 = tradingSystem.login(regularUserToken, "v1", "regularUser", "password123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken2);
            regularUserName = rootNode.get("username").asText();
            regularUserToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }


        tradingSystem.register(secondOwnerUserName, "password123", LocalDate.now());
        String userToken3 = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken3);
            secondOwnerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
        userToken3 = tradingSystem.login(regularUserToken, "v2", secondOwnerUserName, "password123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken3);
            secondOwnerUserName = rootNode.get("username").asText();
            secondOwnerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
    }

    @Test
    void createProductLottery_whenStoreNotExist_ThenThrowException(){
        ResponseEntity<String> response = tradingSystem.createProductLottery(ownerUserName,ownerToken,"BadStoreName",productID, LocalDateTime.now().plusMinutes(2), 12);
        Assertions.assertEquals("Store must exist", response.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


    @Test
    void createProductLottery_whenProductNotExist_ThenThrowException(){
        ResponseEntity<String> response = tradingSystem.createProductLottery(ownerUserName,ownerToken,storeName,555, LocalDateTime.now().plusMinutes(2), 12);
        Assertions.assertEquals("Product must exist in store", response.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createProductLottery_whenManagerHasNoPermission_ThenThrowException(){
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.suggestManage(ownerUserName,ownerToken,secondOwnerUserName,storeName,true,true,true,true,true,false).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.approveManage(secondOwnerUserName,secondOwnerToken,storeName,ownerUserName,true,true,true,true,true,false).getStatusCode());
        ResponseEntity<String> response = tradingSystem.createProductLottery(secondOwnerUserName, secondOwnerToken, storeName, productID, LocalDateTime.now().plusMinutes(2), 12.0);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
        Assertions.assertEquals("Manager has not permission for create product lottery",response.getBody());
    }

    @Test
    void createProductLottery_whenRegularUserTry_ThenThrowException(){
        ResponseEntity<String> response = tradingSystem.createProductLottery(regularUserName, regularUserToken, storeName, productID, LocalDateTime.now().plusMinutes(2), 12.0);
        Assertions.assertEquals("User doesn't have roles",response.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }

    @Test
    void createProductLottery_whenManagerHasPermissions_ThenSuccess(){
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.suggestManage(ownerUserName,ownerToken,secondOwnerUserName,storeName,true,true,true,true,true,true).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.approveManage(secondOwnerUserName,secondOwnerToken,storeName,ownerUserName,true,true,true,true,true,true).getStatusCode());
        ResponseEntity<String> response = tradingSystem.createProductLottery(secondOwnerUserName, secondOwnerToken, storeName, productID, LocalDateTime.now().plusMinutes(2), 12.0);
        Assertions.assertEquals("Created product lottery successfully",response.getBody());
        Assertions.assertEquals(HttpStatus.OK,response.getStatusCode());

    }

    @Test
    void createProductLottery_forOwner_ThenSuccess(){
        ResponseEntity<String> response = tradingSystem.createProductLottery(ownerUserName,ownerToken,storeName,productID, LocalDateTime.now().plusMinutes(2),12.0);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Created product lottery successfully", response.getBody());
    }

    @Test
    void createProductLottery_forPast_ThenThrowException(){
        ResponseEntity<String> response = tradingSystem.createProductLottery(ownerUserName,ownerToken,storeName,productID, LocalDateTime.now().minusMinutes(10),12.0);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Cant create lottery for past or present", response.getBody());
    }

    @Test
    void createProductLottery_forPresent_ThenThrowException(){
        ResponseEntity<String> response = tradingSystem.createProductLottery(ownerUserName,ownerToken,storeName,productID, LocalDateTime.now(),12.0);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Cant create lottery for past or present", response.getBody());
    }


//    public String buyLotteryProductTicket(String userName, String storeName, int productID, double price) throws Exception{
//        validateUserAndStore(userName, storeName);
//        Store store = storeRepository.getStore(storeName);
//        if(store.buyLotteryProductTicket(userName, productID, price)){
//            return store.makeLotteryOnProduct(productID);
//        }
//        else
//            return "Ticket Bought Successfully";
//
//    }
    @Test
    void buyLotteryProductTicket_WhenStoreNotExist_ThenThrowException(){
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.createProductLottery(ownerUserName,ownerToken,storeName,productID, LocalDateTime.now().plusMinutes(2),12.0).getStatusCode());
        ResponseEntity<String> response = tradingSystem.buyLotteryProductTicket(regularUserName, regularUserToken, "BadStoreName", productID, 3);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Store must exist", response.getBody());

    }

    @Test
    void buyLotteryProductTicket_WhenLotteryDontExist_ThenThrowException(){
        ResponseEntity<String> response = tradingSystem.buyLotteryProductTicket(regularUserName, regularUserToken, storeName, productID, 3);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Lottery does not exist", response.getBody());
    }

    @Test
    void buyLotteryProductTicket_ProbabilityWillBelowOne_Success(){
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.createProductLottery(ownerUserName,ownerToken,storeName,productID, LocalDateTime.now().plusMinutes(2),12.0).getStatusCode());
        ResponseEntity<String> response = tradingSystem.buyLotteryProductTicket(regularUserName, regularUserToken, storeName, productID, 3);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Ticket Bought Successfully", response.getBody());

        //TODO: get lottery and verify the probabilites
    }


    @Test
    void buyLotteryProductTicket_WhenProbabilityWillBeGreaterThenOne_ThenThrowException(){
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.createProductLottery(ownerUserName,ownerToken,storeName,productID, LocalDateTime.now().plusMinutes(2),12.0).getStatusCode());
        ResponseEntity<String> response = tradingSystem.buyLotteryProductTicket(regularUserName, regularUserToken, storeName, productID, 3);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Ticket Bought Successfully", response.getBody());

        ResponseEntity<String> response1 = tradingSystem.buyLotteryProductTicket(secondOwnerUserName,secondOwnerToken,storeName,productID,10.0);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        Assertions.assertEquals("Cant buy ticket in this price, maximum is 9.0", response1.getBody());
    }


    @Test
    void buyLotteryProductTicket_ProbabilityWillEqualOne_Success(){
        Assertions.assertEquals(HttpStatus.OK,tradingSystem.createProductLottery(ownerUserName,ownerToken,storeName,productID, LocalDateTime.now().plusMinutes(2),12.0).getStatusCode());
        ResponseEntity<String> response = tradingSystem.buyLotteryProductTicket(regularUserName, regularUserToken, storeName, productID, 6);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Ticket Bought Successfully", response.getBody());

        ResponseEntity<String> response1 = tradingSystem.buyLotteryProductTicket(secondOwnerUserName,secondOwnerToken,storeName,productID,6);
        Assertions.assertEquals(HttpStatus.OK, response1.getStatusCode());
        System.out.println(response1.getBody());
        Assertions.assertTrue("rregularUser won the product 111".equals(response1.getBody()) || "rowner2 won the product 111".equals(response1.getBody()));

        //Todo : verify Client get The product ( performBuy with 0 shekels)
        //Todo: validate lottery is stop
    }

    //TODO: how we check the proccess after lottery is done( with not enough money) - need to define if there will be a tab for this or it will be in same page

    @AfterEach
    public void tearDown() {
        tradingSystem.deleteInstance();
        userRepository.deleteInstance();
        storeRepository.deleteInstance();
    }


}
