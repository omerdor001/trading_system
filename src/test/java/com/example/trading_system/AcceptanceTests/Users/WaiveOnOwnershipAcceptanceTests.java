package com.example.trading_system.AcceptanceTests.Users;

import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.StoreMemoryRepository;
import com.example.trading_system.domain.stores.StoreRepository;
import com.example.trading_system.domain.users.UserFacadeImp;
import com.example.trading_system.domain.users.UserMemoryRepository;
import com.example.trading_system.domain.users.UserRepository;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

public class WaiveOnOwnershipAcceptanceTests {
    private static final Logger logger = LoggerFactory.getLogger(UserFacadeImp.class);
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

    @BeforeEach
    public void setUp() {
        userRepository= UserMemoryRepository.getInstance();    //May be change later
        storeRepository= StoreMemoryRepository.getInstance();  //May be change later
        tradingSystemImp = TradingSystemImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class),userRepository,storeRepository);
        String password = "123456";
        tradingSystemImp.register("admin", password, LocalDate.now());
        if(tradingSystemImp.marketService.getMarketFacade().getStoreRepository()!=null){
            logger.info("0");
        }
        tradingSystemImp.openSystem(storeRepository);
        ResponseEntity<String> response = tradingSystemImp.enter();
        String userToken = response.getBody();
        if(tradingSystemImp.marketService.getMarketFacade().getStoreRepository()!=null){
            logger.info("1");
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            userName = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken = tradingSystemImp.login(token, "v0", "admin", password).getBody();
        if(tradingSystemImp.marketService.getMarketFacade().getStoreRepository()!=null){
            logger.info("2");
        }
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
        if(tradingSystemImp.marketService.getMarketFacade().getStoreRepository()!=null){
            logger.info("3");
        }
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
        tradingSystemImp.appointOwner(userName, token, userName, ownerUserName, storeName);
    }

    @AfterEach
    public void tearDown() {
        tradingSystemImp.deleteInstance();
    }

    @Test
    public void GivenValidOwner_WhenWaiveOnOwnerShip_ThenSuccess(){ //check that manager appointment also deleted
        logger.info("Problematic test");
        Assertions.assertEquals(HttpStatus.OK,tradingSystemImp.suggestManage(ownerUserName,ownerToken,userNameManager,storeName,true,true,true,true).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.approveManage(userNameManager,tokenManager,storeName,ownerUserName).getStatusCode());
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
        Assertions.assertEquals("Founder cant waive on ownership",resp.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }
}