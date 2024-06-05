package com.example.trading_system.AcceptanceTests.Market;

import com.example.trading_system.domain.stores.StorePolicy;
import com.example.trading_system.service.TradingSystemImp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class StockManagementAcceptanceTests {
    private TradingSystemImp tradingSystem;
    String token1;
    String token2;
    String token3;
    @BeforeEach
    public void setUp() {
        tradingSystem = TradingSystemImp.getInstance();
        tradingSystem.register(0, "testuser1", "password123", LocalDate.now());
        tradingSystem.openSystem();
        ResponseEntity<String> response1 = tradingSystem.enter();
        token1 = response1.getBody();
        tradingSystem.register(1, "testuser2", "password1232", LocalDate.now());
        tradingSystem.register(2, "testuser3", "password1233", LocalDate.now());
        tradingSystem.login(token1,"1","testuser1", "password123");
        tradingSystem.appointOwner("testuser1",token1,"testuser1","testuser3","Adidas");
        ResponseEntity<String> response2 = tradingSystem.enter();
        token2 = response2.getBody();
        tradingSystem.login(token2,"1","testuser2", "password1232");
        tradingSystem.openStore("testuser2",token2,"Adidas","shoes",mock(StorePolicy.class));
        ResponseEntity<String> response3 = tradingSystem.enter();
        token3=response3.getBody();
    }

    @AfterEach
    public void tearDown(){
        tradingSystem.exit(token1,"0");
        tradingSystem.exit(token2,"1");
        tradingSystem.exit(token3,"2");
        tradingSystem.deleteInstance();
    }

    @Test
    void addProduct_Success() {
        ArrayList<String> keyWords = new ArrayList<String>();
        keyWords.add("Samba");
        tradingSystem.appointOwner("testuser3",token3,"testuser3","testuser2","Adidas");
        ResponseEntity<String> response=tradingSystem.addProduct("testuser2",token2,123,"Adidas","Samba",
                "white black shoes",300.0,100,5.0,0,keyWords);
        assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    @Test
    void addProduct_StoreNotExist() {

    }

    @Test
    void addProduct_PriceLessThanZero() {

    }

    @Test
    void addProduct_QuantityLessEqualThanZero() {

    }

    @Test
    void addProduct_RatingLessThanZero() {

    }

    @Test
    void addProduct_NotManager() {

    }

    @Test
    void removeProduct_success() {

    }

    @Test
    void removeProduct_UserNotExist() {

    }

    @Test
    void removeProduct_StoreNotExist() {

    }

    @Test
    void removeProduct_ProductNotExist() {

    }

    @Test
    void removeProduct_NotManager() {

    }

    @Test
    void setProduct_name_success() {

    }

    @Test
    void setProduct_Price_PriceLessThanZero() {

    }

    @Test
    void setProduct_description_UserNotExist() {

    }

}
