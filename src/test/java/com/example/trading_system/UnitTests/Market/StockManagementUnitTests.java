package com.example.trading_system.UnitTests.Market;

import com.example.trading_system.domain.stores.MarketFacade;
import com.example.trading_system.domain.stores.MarketFacadeImp;
import com.example.trading_system.domain.users.UserFacade;
import com.example.trading_system.domain.users.UserFacadeImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class StockManagementUnitTests {
    MarketFacade marketFacade;
    UserFacade userFacade;
    List<String> keyWords;
    int category;
    @BeforeEach
    public void setUp() {
        marketFacade=MarketFacadeImp.getInstance();
        userFacade= UserFacadeImp.getInstance();
        List<String> keyWords=new ArrayList();
        keyWords.add("Samba");
        category=1;
    }


    //Cannot be done because tests of Appointment is not working
    @Test
    void addProduct_Success() {
    }

    @Test
    void removeProduct() {
    }

    @Test
    void setProduct_name() {
    }

    @Test
    void setProduct_description() {
    }

    @Test
    void setProduct_price() {
    }
}