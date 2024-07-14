package com.example.trading_system.AcceptanceTests.Users.RoleTests;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.StoreDatabaseRepository;
import com.example.trading_system.domain.stores.StoreMemoryRepository;
import com.example.trading_system.domain.stores.StoreRepository;
import com.example.trading_system.domain.users.UserDatabaseRepository;
import com.example.trading_system.domain.users.UserMemoryRepository;
import com.example.trading_system.domain.users.UserRepository;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import jakarta.transaction.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
public class AppointOwnerAcceptanceTests {

    private TradingSystemImp tradingSystemImp;
    private String userName = "";
    private String token = "";
    private final String storeName = "Store1";
    private String ownerToken = "";
    private String ownerUserName = "";
    @Autowired
    private UserDatabaseRepository userRepository;

    @Autowired
    private StoreDatabaseRepository storeRepository;
    @BeforeEach
    public void setUp() {

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
        tradingSystemImp.openStore(userName, token, storeName, "My Store is the best");
        tradingSystemImp.register("owner", password, LocalDate.now());
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
    }

    @AfterEach
    public void tearDown() {
        tradingSystemImp.deleteInstance();
    }

    @Test
    public void GivenStoreNotExist_WhenSuggestOwner_ThenThrowException() {
        ResponseEntity<String> resp = tradingSystemImp.suggestOwner(userName, token, ownerUserName, "BadStoreName");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertEquals("No store called BadStoreName exist", resp.getBody());
    }

    @Test
    public void GivenAppointerNotExist_WhenSuggestOwner_ThenThrowException() {
        ResponseEntity<String> resp = tradingSystemImp.suggestOwner(userName, token, "badUserName", storeName);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertEquals("No user called badUserName exist", resp.getBody());
    }

    @Test
    public void GivenAppointerIsNotOwner_WhenSuggestOwner_ThenThrowException() {
        ResponseEntity<String> resp = tradingSystemImp.suggestOwner(ownerUserName, ownerToken, ownerUserName, storeName);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertEquals("Appoint user must be Owner", resp.getBody());
    }

    @Test
    public void GivenAppointerIsLogout_WhenSuggestOwner_ThenThrowException() {
        tradingSystemImp.logout(token, userName);
        ResponseEntity<String> resp = tradingSystemImp.suggestOwner(userName, token, ownerUserName, storeName);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertEquals("Appoint user is not logged", resp.getBody());
    }

    @Test
    public void GivenNewOwnerIsAlreadyOwner_WhenSuggestOwner_ThenThrowException() {
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestOwner(userName, token, ownerUserName, storeName).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.approveOwner(ownerUserName, ownerToken, storeName, userName).getStatusCode());
        ResponseEntity<String> resp = tradingSystemImp.suggestOwner(userName, token, ownerUserName, storeName);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertEquals("User already Owner of this store", resp.getBody());
    }


    @Test
    public void GivenValidInput_WhenSuggestOwner_ThenSuccess() {
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestOwner(userName, token, ownerUserName, storeName).getStatusCode());
    }

    @Test
    public void GivenNotSuggestOwner_WhenApproveOwner_ThenThrowException() {
        ResponseEntity<String> res = tradingSystemImp.approveOwner(ownerUserName, ownerToken, storeName, userName);
        Assertions.assertEquals("No one suggest this user to be a owner", res.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void GivenStoreNotExist_WhenApproveOwner_ThenThrowException() {
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestOwner(userName, token, ownerUserName, storeName).getStatusCode());
        ResponseEntity<String> res = tradingSystemImp.approveOwner(ownerUserName, ownerToken, "BadStoreName", userName);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        Assertions.assertEquals("No store called BadStoreName exist", res.getBody());
    }

    @Test
    public void GivenNotExistAppointer_WhenApproveOwner_ThenThrowException() {
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestOwner(userName, token, ownerUserName, storeName).getStatusCode());
        ResponseEntity<String> res = tradingSystemImp.approveOwner(ownerUserName, ownerToken, storeName, "BadName");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        Assertions.assertEquals("No user called BadName exist", res.getBody());
    }

    @Test
    public void GivenAppointerIsNotOwner_WhenApproveOwner_ThenThrowException() {
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestOwner(userName, token, ownerUserName, storeName).getStatusCode());
        ResponseEntity<String> res = tradingSystemImp.approveOwner(ownerUserName, ownerToken, storeName, ownerUserName);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        Assertions.assertEquals("User must be Owner", res.getBody());
    }

    @Test
    public void GivenUserIsAlreadyOwner_WhenApproveOwner_ThenThrowException() {
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestOwner(userName, token, ownerUserName, storeName).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.approveOwner(ownerUserName, ownerToken, storeName, userName).getStatusCode());
        ResponseEntity<String> resp = tradingSystemImp.approveOwner(ownerUserName, ownerToken, storeName, userName);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertEquals("User already Owner of this store", resp.getBody());
    }

    @Test
    public void GivenValidInput_WhenApproveOwner_ThenSuccess() {
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestOwner(userName, token, ownerUserName, storeName).getStatusCode());
        ResponseEntity<String> resp = tradingSystemImp.approveOwner(ownerUserName, ownerToken, storeName, userName);
        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
        Assertions.assertEquals("Success approving owner", resp.getBody());
    }

    @Test
    public void GivenUserIsManagerValid_WhenApprove_Owner_ThenSuccess() {
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestManage(userName, token, ownerUserName, storeName, true, true, true, true, true, true).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.approveManage(ownerUserName, ownerToken, storeName, userName, true, true, true, true, true, true).getStatusCode());
        ResponseEntity<String> response = tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName, token, storeName, ownerUserName);
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("Manager"));
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestOwner(userName, token, ownerUserName, storeName).getStatusCode());
        ResponseEntity<String> resp = tradingSystemImp.approveOwner(ownerUserName, ownerToken, storeName, userName);
        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
        Assertions.assertEquals("Success approving owner", resp.getBody());
        ResponseEntity<String> response2 = tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName, token, storeName, ownerUserName);
        Assertions.assertFalse(Objects.requireNonNull(response2.getBody()).contains("Manager"));
        Assertions.assertTrue(response2.getBody().contains("Owner"));
    }

    @Test
    public void GivenStoreNotExist_WhenRejectOwner_ThenThrowException() {
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestOwner(userName, token, ownerUserName, storeName).getStatusCode());
        ResponseEntity<String> res = tradingSystemImp.rejectToOwnStore(ownerUserName, ownerToken, "BadStoreName", userName);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        Assertions.assertEquals("No store called BadStoreName exist", res.getBody());
    }

    @Test
    public void GivenAppointerNotExist_WhenRejectOwner_ThenThrowException() {
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestOwner(userName, token, ownerUserName, storeName).getStatusCode());
        ResponseEntity<String> res = tradingSystemImp.rejectToOwnStore(ownerUserName, ownerToken, storeName, "BadUserName");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        Assertions.assertEquals("No user called BadUserName exist", res.getBody());
    }

    @Test
    public void GivenAppointerIsNotOwner_WhenRejectOwner_ThenThrowException() {
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestOwner(userName, token, ownerUserName, storeName).getStatusCode());
        ResponseEntity<String> res = tradingSystemImp.rejectToOwnStore(ownerUserName, ownerToken, storeName, ownerUserName);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        Assertions.assertEquals("User must be Owner", res.getBody());
    }

    @Test
    public void GivenAlreadyOwner_WhenRejectOwner_ThenThrowException() {
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestOwner(userName, token, ownerUserName, storeName).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.approveOwner(ownerUserName, ownerToken, storeName, userName).getStatusCode());
        ResponseEntity<String> res = tradingSystemImp.rejectToOwnStore(ownerUserName, ownerToken, storeName, userName);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        Assertions.assertEquals("User already Owner of this store", res.getBody());
    }

    @Test
    public void GivenNoOneSuggest_WhenRejectOwner_ThenThrowException() {
        ResponseEntity<String> res = tradingSystemImp.rejectToOwnStore(ownerUserName, ownerToken, storeName, userName);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        Assertions.assertEquals("No one suggest this user to be a owner", res.getBody());
    }

    @Test
    public void GivenValidInput_WhenRejectOwner_ThenSuccess() {
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.suggestOwner(userName, token, ownerUserName, storeName).getStatusCode());
        ResponseEntity<String> response = tradingSystemImp.rejectToOwnStore(ownerUserName, ownerToken, storeName, userName);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Success reject to be owner", response.getBody());
    }
}
