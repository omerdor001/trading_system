package com.example.trading_system.Market;

import com.example.trading_system.domain.stores.Category;
import com.example.trading_system.service.Facade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StockManagementAcceptanceTests {
    Facade facade;
    @BeforeEach
    public void setUp() {
        facade=mock(Facade.class);
    }

    @Test
    void addProduct_Success() {
        List<String> keyWords=new ArrayList();
        keyWords.add("Samba");
        Category category=Category.Sport;
        when(facade.addProduct("omer123",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,category,keyWords)).
                thenReturn(new ResponseEntity("Success adding products", HttpStatus.OK));
        ResponseEntity<String> response=facade.addProduct("omer123",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,Category.Sport,keyWords);
        assertEquals(response.getBody(),"Success adding products");
    }

    @Test
    void removeProduct() {
    }
}