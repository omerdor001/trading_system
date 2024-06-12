package com.example.trading_system.AcceptanceTests.Users;

import com.example.trading_system.domain.stores.StorePolicy;
import com.example.trading_system.service.TradingSystem;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class AppointManagerAcceptanceTests {
    private TradingSystem tradingSystem;
    private String token;
    private String username;

    @BeforeEach
    void setup() {
        tradingSystem = TradingSystemImp.getInstance();
        tradingSystem.register("owner1", "password123", LocalDate.now());
        tradingSystem.register("manager", "password123", LocalDate.now());
        tradingSystem.openSystem();
        String userToken = tradingSystem.enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
        userToken = tradingSystem.login(token, "0", "owner1", "password123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userToken);
            username = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystem.openStore(username,token,"existingStore", "General Store", new StorePolicy());
    }

//    @Test
//    @Disabled
//    void appointManager_Success() {
//        when(facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true)).
//                thenReturn(new ResponseEntity<String>("Success appointing manager", HttpStatus.OK));
//        ResponseEntity<String> result=facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true);
//        assertEquals(result,new ResponseEntity<String>("Success appointing manager", HttpStatus.OK));
//    }
//
//    @Test
//    @Disabled
//    void appointManager_AppointeeNotExist() {
//        when(facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true)).
//                thenReturn(new ResponseEntity<>("No user called testAppointee exist", HttpStatus.BAD_REQUEST));
//        ResponseEntity<String> result=facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true);
//        assertEquals(new ResponseEntity<>("No user called testAppointee exist", HttpStatus.BAD_REQUEST),result);
//    }
//
//    @Test
//    @Disabled
//    void appointManager_newManagerNotExist() {
//        when(facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true)).
//                thenReturn(new ResponseEntity<>("No user called testNewManager exist", HttpStatus.BAD_REQUEST));
//        ResponseEntity<String> result=facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true);
//        assertEquals(new ResponseEntity<>("No user called testNewManager exist", HttpStatus.BAD_REQUEST),result);
//    }
//
//    @Test
//    @Disabled
//    void appointManager_appointeeNotOwner() {
//        when(facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true)).
//                thenReturn(new ResponseEntity<>("User must be Owner", HttpStatus.BAD_REQUEST));
//        ResponseEntity<String> result=facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true);
//        assertEquals(new ResponseEntity<>("User must be Owner", HttpStatus.BAD_REQUEST),result);
//    }
//
//    @Test
//    @Disabled
//    void appointManager_appointeeNotLogged() {
//        when(facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true)).
//                thenReturn(new ResponseEntity<>("Appoint user is not logged", HttpStatus.BAD_REQUEST));
//        ResponseEntity<String> result=facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true);
//        assertEquals(new ResponseEntity<>("Appoint user is not logged", HttpStatus.BAD_REQUEST),result);
//    }
//
//    @Test
//    @Disabled
//    void appointManager_newManagerAlreadyManager() {
//        when(facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true)).
//                thenReturn(new ResponseEntity<>("User already Manager of this store", HttpStatus.BAD_REQUEST));
//        ResponseEntity<String> result=facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true);
//        assertEquals(new ResponseEntity<>("User already Manager of this store", HttpStatus.BAD_REQUEST),result);
//    }
//
//    @Test
//    @Disabled
//    void appointManager_newManagerAlreadyOwner() {
//        when(facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true)).
//                thenReturn(new ResponseEntity<>("User cannot be owner of this store", HttpStatus.BAD_REQUEST));
//        ResponseEntity<String> result=facade.appointManager("testAppoint","testNewManager","testStore",true,true,true,true);
//        assertEquals(new ResponseEntity<>("User cannot be owner of this store", HttpStatus.BAD_REQUEST),result);
//    }


}