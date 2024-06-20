package com.example.trading_system.AcceptanceTests.Users.RoleTests;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
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
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

public class GetRoleInfoAcceptanceTests {

    private TradingSystemImp tradingSystemImp;
    private String userName = "";
    private String token = "";
    private final String storeName = "Storetest";
    private String tokenManager = "";
    private String userNameManager = "";
    private String ownerUser = "";
    private String ownerToken = "";

    @BeforeEach
    public void setUp() {
        tradingSystemImp = TradingSystemImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class), mock(NotificationSender.class));
        String password = "123456";
        tradingSystemImp.register("adminUser", password, LocalDate.now());
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
        userToken = tradingSystemImp.login(token, "v0", "adminUser", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            userName = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystemImp.openStore(userName, token, storeName, "My Store is the best");

        tradingSystemImp.register("managerUser", password, LocalDate.now());
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
        userToken2 = tradingSystemImp.login(tokenManager, "v1", "managerUser", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken2);
            userNameManager = rootNode.get("username").asText();
            tokenManager = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }

        tradingSystemImp.register("ownerUser", password, LocalDate.now());
        ResponseEntity<String> response3 = tradingSystemImp.enter();
        String userToken3 = response3.getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken3);
            ownerUser = rootNode.get("username").asText();
            ownerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        userToken3 = tradingSystemImp.login(ownerToken, "v2", "ownerUser", password).getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken3);
            ownerUser = rootNode.get("username").asText();
            ownerToken = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystemImp.suggestOwner(userName, token, ownerUser, storeName);
        tradingSystemImp.approveOwner(ownerUser, ownerToken, storeName, userName);
        tradingSystemImp.appointOwner(userName, token, userName, ownerUser, storeName);
        tradingSystemImp.suggestManage(ownerUser, ownerToken, userNameManager, storeName, true, true, true, true);
        tradingSystemImp.approveManage(userNameManager, tokenManager, storeName, ownerUser);
        tradingSystemImp.appointManager(ownerUser, ownerToken, ownerUser, userNameManager, storeName, true, true, true, true);

    }

    @AfterEach
    public void tearDown() {
        tradingSystemImp.deleteInstance();
    }

    @Test
    public void GivenStoreNotExist_WhenRequestInfoAboutSpecificOfficial_ThenThrowException() {
        ResponseEntity<String> response = tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName, token, "BadStoreName", ownerUser);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Store must exist", response.getBody());
    }

    @Test
    public void GivenUserNotExist_WhenRequestInfoAboutSpecificOfficial_ThenThrowException() {
        ResponseEntity<String> response = tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName, token, storeName, "BadUserName");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("User must exist", response.getBody());
    }

    @Test
    public void GivenManagerNotValid_WhenRequestInfoAboutSpecificOfficial_ThenThrowException() {
        ResponseEntity<String> response = tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userNameManager, tokenManager, storeName, ownerUser);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("only owners can request information about specific employee", response.getBody());
    }

    @Test
    public void GivenNotAnEmployee_WhenRequestInfoAboutOfficials_ThenThrowException() {
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.fireManager(ownerUser, ownerToken, storeName, userNameManager).getStatusCode());
        ResponseEntity<String> response = tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName, token, storeName, userNameManager);
        Assertions.assertEquals("User is not employed in this store.", response.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void GivenValidInput_WhenRequestInfoAboutSpecificOfficialOwner_ThenSuccess() {
        ResponseEntity<String> response = tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName, token, storeName, ownerUser);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("Owner ownerUser"));
        ResponseEntity<String> response2 = tradingSystemImp.requestInformationAboutSpecificOfficialInStore(ownerUser, ownerToken, storeName, userName);
        Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(response2.getBody()).contains("Founder adminUser"));
    }

    @Test
    public void GivenValidInput_WhenRequestInfoAboutSpecificOfficialManager_ThenSuccess() {
        ResponseEntity<String> response = tradingSystemImp.requestInformationAboutSpecificOfficialInStore(userName, token, storeName, userNameManager);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("Manager managerUser " + userNameManager));
        ResponseEntity<String> response2 = tradingSystemImp.requestInformationAboutSpecificOfficialInStore(ownerUser, ownerToken, storeName, userNameManager);
        Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(response2.getBody()).contains("Manager managerUser " + userNameManager));
    }

    @Test
    public void GivenStoreNotExist_WhenRequestInfoAboutOfficials_ThenThrowException() {
        ResponseEntity<String> response = tradingSystemImp.requestInformationAboutOfficialsInStore(userName, token, "BadStoreName");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Store must exist", response.getBody());
    }

    @Test
    public void GivenManagerNotValid_WhenRequestInfoAboutOfficials_ThenThrowException() {
        ResponseEntity<String> response = tradingSystemImp.requestInformationAboutOfficialsInStore(userNameManager, tokenManager, storeName);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("only owners can request information about officials in store", response.getBody());
    }

    @Test
    public void GivenFounder_WhenRequestInfoAboutOfficials_ThenSuccess() {
        ResponseEntity<String> response = tradingSystemImp.requestInformationAboutOfficialsInStore(userName, token, storeName);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("Founder adminUser " + userName));
        Assertions.assertTrue(response.getBody().contains("Manager managerUser " + userNameManager));
        Assertions.assertTrue(response.getBody().contains("Owner ownerUser " + ownerUser));
    }

    @Test
    public void GivenOwner_WhenRequestInfoAboutOfficials_ThenSuccess() {
        ResponseEntity<String> response = tradingSystemImp.requestInformationAboutOfficialsInStore(ownerUser, ownerToken, storeName);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("Founder adminUser " + userName));
        Assertions.assertTrue(response.getBody().contains("Manager managerUser " + userNameManager));
        Assertions.assertTrue(response.getBody().contains("Owner ownerUser " + ownerUser));
    }

    @Test
    public void GivenStoreNotExist_WhenRequestManagerPermission_ThenThrowException() {
        ResponseEntity<String> response = tradingSystemImp.requestManagersPermissions(userName, token, "BadStoreName");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Store must exist", response.getBody());
    }

    @Test
    public void GivenManagerNotValid_WhenRequestManagerPermission_ThenThrowException() {
        ResponseEntity<String> response = tradingSystemImp.requestManagersPermissions(userNameManager, tokenManager, storeName);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("only owners can request manager permissions", response.getBody());
    }

    @Test
    public void GivenNoManagers_WhenRequestManagerPermission_ThenSuccess() {
        tradingSystemImp.fireManager(ownerUser, ownerToken, storeName, userNameManager);
        ResponseEntity<String> response = tradingSystemImp.requestManagersPermissions(ownerUser, ownerToken, storeName);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void GivenValidOwner_WhenRequestManagerPermission_ThenSuccess() {
        //also change permission
        ResponseEntity<String> response = tradingSystemImp.requestManagersPermissions(ownerUser, ownerToken, storeName);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("managerUser " + userNameManager + " true true true true"));
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.editPermissionForManager(ownerUser, ownerToken, userNameManager, storeName, true, false, true, false).getStatusCode());
        ResponseEntity<String> response2 = tradingSystemImp.requestManagersPermissions(ownerUser, ownerToken, storeName);
        Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(response2.getBody()).contains("managerUser " + userNameManager + " true false true false"));
    }

    @Test
    public void GivenValidFounder_WhenRequestManagerPermission_ThenSuccess() {
        //TODO - check why these 2 lines are required for build to succeed (probably other test class has bad cleanup after tests)
        tearDown();
        setUp();
        ResponseEntity<String> response = tradingSystemImp.requestManagersPermissions(userName, token, storeName);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("managerUser " + userNameManager + " true true true true"));
        Assertions.assertEquals(HttpStatus.OK, tradingSystemImp.editPermissionForManager(ownerUser, ownerToken, userNameManager, storeName, true, false, true, false).getStatusCode());
        ResponseEntity<String> response2 = tradingSystemImp.requestManagersPermissions(userName, token, storeName);
        Assertions.assertEquals(HttpStatus.OK, response2.getStatusCode());
        Assertions.assertTrue(Objects.requireNonNull(response2.getBody()).contains("managerUser " + userNameManager + " true false true false"));
    }
}
