/*
package com.example.trading_system.Market;

import com.example.trading_system.domain.stores.Category;
import com.example.trading_system.domain.users.Registered;
import com.example.trading_system.service.TradingSystemImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class StockManagementAcceptanceTests {
    TradingSystemImp facade;
    List<String> keyWords;
    int category;
    String token1;
    String token2;
    String token3;
    @BeforeEach
    public void setUp() {
        facade=TradingSystemImp.getInstance();
        List<String> keyWords=new ArrayList();
        keyWords.add("Samba");
        category=1;
        token1=facade.enter().getBody();
        token2=facade.enter().getBody();
        facade.register(0,"testuser","pass123456",LocalDate.of(2001,02,01));
        facade.openSystem();
        facade.register(1,"testuser1","password1",LocalDate.of(2001,02,01));
        token3=facade.login(token2,1,"testuser1","password1").getBody();

        facade.suggestOwner("testuser","testuser1");
        ////TODO Open a store called Adidas
    }

    @Test
    void addProduct_Success() {
        ////TODO register user
        //TODO Make him manager
        when(facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,category,keyWords,token)).
                thenReturn(new ResponseEntity("Success adding products", HttpStatus.OK));
        ResponseEntity<String> response=facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,category,keyWords,token);
        assertEquals(response,new ResponseEntity("Success adding products", HttpStatus.OK));
    }

    @Test
    void addProduct_UserNotExist() {
        //TODO Make this test
    }

    @Test
    void addProduct_StoreNotExist() {
        ////TODO register user
        //TODO Make him manager
        when(facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,category,keyWords,token)).
                thenReturn(new ResponseEntity("Store must exist", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> response=facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,category,keyWords,token);
        assertEquals(response,new ResponseEntity("Store must exist", HttpStatus.BAD_REQUEST));
    }

    @Test
    void addProduct_PriceLessThanZero() {
        ////TODO register user
        //TODO Make him manager
        when(facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",-350.0,1,8.0,category,keyWords,token)).
                thenReturn(new ResponseEntity("Price can't be negative number", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> response=facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",-350.0,1,8.0,category,keyWords,token);
        assertEquals(response,new ResponseEntity("Price can't be negative number", HttpStatus.BAD_REQUEST));
    }

    @Test
    void addProduct_QuantityLessEqualThanZero() {
        ////TODO register user
        //TODO Make him manager
        when(facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,-1,8.0,category,keyWords,token)).
                thenReturn(new ResponseEntity("Quantity must be natural number", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> response=facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,-1,8.0,category,keyWords,token);
        assertEquals(response,new ResponseEntity("Quantity must be natural number", HttpStatus.BAD_REQUEST));
    }

    @Test
    void addProduct_RatingLessThanZero() {
        ////TODO register user
        //TODO Make him manager
        when(facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,-8.0,category,keyWords,token)).
                thenReturn(new ResponseEntity("Rating can't be negative number", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> response=facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,-8.0,category,keyWords,token);
        assertEquals(response,new ResponseEntity("Rating can't be negative number", HttpStatus.BAD_REQUEST));
    }

    @Test
    void addProduct_NotManager() {
        ////TODO register user
        when(facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,category,keyWords,token)).
                thenReturn(new ResponseEntity("User doesn't have permission to this store", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> response=facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,category,keyWords,token);
        assertEquals(response,new ResponseEntity("User doesn't have permission to this store", HttpStatus.BAD_REQUEST));
    }

    @Test
    void removeProduct_success() {
        ////TODO register user
        //TODO Make him manager
        facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,category,keyWords,token);
        when(facade.removeProduct("testuser","Adidas",123,token)).thenReturn(new ResponseEntity("Success removing products", HttpStatus.OK));
        ResponseEntity<String> response=facade.removeProduct("testuser","Adidas",123,token);
        assertEquals(response,new ResponseEntity("Success removing products", HttpStatus.OK));
    }

    @Test
    void removeProduct_UserNotExist() {
        //TODO Make this test
    }

    @Test
    void removeProduct_StoreNotExist() {
        ////TODO register user
        //TODO Make him manager
        facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,category,keyWords,token);
        when(facade.removeProduct("testuser","Adidas",123,token)).thenReturn(new ResponseEntity("Store must exist", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> response=facade.removeProduct("testuser","Adidas",123,token);
        assertEquals(response,new ResponseEntity("Store must exist", HttpStatus.BAD_REQUEST));
    }

    @Test
    void removeProduct_ProductNotExist() {
        ////TODO register user
        //TODO Make him manager
        when(facade.removeProduct("testuser","Adidas",123,token)).thenReturn(new ResponseEntity("Product must exist", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> response=facade.removeProduct("testuser","Adidas",123,token);
        assertEquals(response,new ResponseEntity("Product must exist", HttpStatus.BAD_REQUEST));
    }

    @Test
    void removeProduct_NotManager() {
        ////TODO register user
        facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,category,keyWords,token);
        when(facade.removeProduct("testuser","Adidas",123,token)).thenReturn(new ResponseEntity("User doesn't have permission to this store", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> response=facade.removeProduct("testuser","Adidas",123,token);
        assertEquals(response,new ResponseEntity("User doesn't have permission to this store", HttpStatus.BAD_REQUEST));
    }

    @Test
    void setProduct_name_success() {
        ////TODO register user
        //TODO Make him manager
        facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,Category.Sport,keyWords);
        when(facade.setProductName("testuser","Adidas",123,"Samba Shoes")).thenReturn(new ResponseEntity("Success editing name to product", HttpStatus.OK));
        ResponseEntity<String> response=facade.setProductName("testuser","Adidas",123,"Samba Shoes");
        assertEquals(response,new ResponseEntity("Success editing name to product", HttpStatus.OK));
    }

    @Test
    void setProduct_Price_PriceLessThanZero() {
        ////TODO register user
        //TODO Make him manager
        facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,Category.Sport,keyWords);
        when(facade.setProductPrice("testuser","Adidas",123,-300)).thenReturn(new ResponseEntity("Price can't be negative number", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> response=facade.setProductPrice("testuser","Adidas",123,-300);
        assertEquals(response,new ResponseEntity("Price can't be negative number", HttpStatus.BAD_REQUEST));
    }

    @Test
    void setProduct_description_UserNotExist() {
        //TODO Make this test
    }

}*/
