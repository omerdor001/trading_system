package com.example.trading_system.Market;

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
    @BeforeEach
    public void setUp() {
        tradingSystem = TradingSystemImp.getInstance();
        tradingSystem.register(0, "testuser", "password123", LocalDate.now());
        tradingSystem.openSystem();
        ResponseEntity<String> response = tradingSystem.enter();
        token1 = response.getBody();
        tradingSystem.register(1, "testuser1", "password1231", LocalDate.now());
        tradingSystem.register(2, "testuser2", "password1232", LocalDate.now());
        tradingSystem.openStore("testuser","Adidas","shoes",mock(StorePolicy.class));
        tradingSystem.appointOwner("testuser","testuser2","Adidas");
        tradingSystem.login(token1,1,"testuser1", "password1231");
    }

    @AfterEach
    public void tearDown(){
        tradingSystem.exit(token1,1);
        tradingSystem.deleteInstance();
    }

    @Test
    void addProduct_Success() {
        ArrayList<String> keyWords = new ArrayList<String>();
        keyWords.add("Samba");
        tradingSystem.appointOwner("testuser2","testuser1","Adidas");
        ResponseEntity<String> response=tradingSystem.addProduct("testuser1",123,"Adidas","Samba",
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
