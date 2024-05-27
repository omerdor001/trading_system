package com.example.trading_system.Market;

import com.example.trading_system.domain.users.Registered;
import com.example.trading_system.service.TradingSystemImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StockManagementAcceptanceTests {
    TradingSystemImp facade;
    List<String> keyWords;
    int category;
    @BeforeEach
    public void setUp() {
        facade= Mockito.mock(TradingSystemImp.class);
        List<String> keyWords=new ArrayList();
        keyWords.add("Samba");
        category=1;
    }

    @Test
    void addProduct_Success() {
        Registered registered=mock(Registered.class);
        registered.addOwnerRole("aaaa","Adidas");
        when(facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,category,keyWords)).
                thenReturn(new ResponseEntity("Success adding products", HttpStatus.OK));
        ResponseEntity<String> response=facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,category,keyWords);
        assertEquals(response,new ResponseEntity("Success adding products", HttpStatus.OK));
    }

    @Test
    void addProduct_UserNotExist() {
        when(facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,category,keyWords)).
                thenReturn(new ResponseEntity("User must exist", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> response=facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,category,keyWords);
        assertEquals(response,new ResponseEntity("User must exist", HttpStatus.BAD_REQUEST));
    }

    @Test
    void addProduct_StoreNotExist() {
        Registered registered=mock(Registered.class);
        registered.addOwnerRole("aaaa","Adidas");
        when(facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,category,keyWords)).
                thenReturn(new ResponseEntity("Store must exist", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> response=facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,category,keyWords);
        assertEquals(response,new ResponseEntity("Store must exist", HttpStatus.BAD_REQUEST));
    }

    @Test
    void addProduct_PriceLessThanZero() {
        Registered registered=mock(Registered.class);
        registered.addOwnerRole("aaaa","Adidas");
        when(facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",-350.0,1,8.0,category,keyWords)).
                thenReturn(new ResponseEntity("Price can't be negative number", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> response=facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",-350.0,1,8.0,category,keyWords);
        assertEquals(response,new ResponseEntity("Price can't be negative number", HttpStatus.BAD_REQUEST));
    }

    @Test
    void addProduct_QuantityLessEqualThanZero() {
        Registered registered=mock(Registered.class);
        registered.addOwnerRole("aaaa","Adidas");
        when(facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,-1,8.0,category,keyWords)).
                thenReturn(new ResponseEntity("Quantity must be natural number", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> response=facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,-1,8.0,category,keyWords);
        assertEquals(response,new ResponseEntity("Quantity must be natural number", HttpStatus.BAD_REQUEST));
    }

    @Test
    void addProduct_RatingLessThanZero() {
        Registered registered=mock(Registered.class);
        registered.addOwnerRole("aaaa","Adidas");
        when(facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,-8.0,category,keyWords)).
                thenReturn(new ResponseEntity("Rating can't be negative number", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> response=facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,-8.0,category,keyWords);
        assertEquals(response,new ResponseEntity("Rating can't be negative number", HttpStatus.BAD_REQUEST));
    }

    @Test
    void addProduct_NotManager() {
        Registered registered=mock(Registered.class);
        when(facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,category,keyWords)).
                thenReturn(new ResponseEntity("User doesn't have permission to this store", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> response=facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,category,keyWords);
        assertEquals(response,new ResponseEntity("User doesn't have permission to this store", HttpStatus.BAD_REQUEST));
    }

    @Test
    void removeProduct_success() {
        Registered registered=mock(Registered.class);
        registered.addOwnerRole("aaaa","Adidas");
        facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,category,keyWords);
        when(facade.removeProduct("testuser","Adidas",123)).thenReturn(new ResponseEntity("Success removing products", HttpStatus.OK));
        ResponseEntity<String> response=facade.removeProduct("testuser","Adidas",123);
        assertEquals(response,new ResponseEntity("Success removing products", HttpStatus.OK));
    }

    @Test
    void removeProduct_UserNotExist() {
        facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,category,keyWords);
        when(facade.removeProduct("testuser","Adidas",123)).thenReturn(new ResponseEntity("User must exist", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> response=facade.removeProduct("testuser","Adidas",123);
        assertEquals(response,new ResponseEntity("User must exist", HttpStatus.BAD_REQUEST));
    }

    @Test
    void removeProduct_StoreNotExist() {
        Registered registered=mock(Registered.class);
        registered.addOwnerRole("aaaa","Adidas");
        facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,category,keyWords);
        when(facade.removeProduct("testuser","Adidas",123)).thenReturn(new ResponseEntity("Store must exist", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> response=facade.removeProduct("testuser","Adidas",123);
        assertEquals(response,new ResponseEntity("Store must exist", HttpStatus.BAD_REQUEST));
    }

    @Test
    void removeProduct_ProductNotExist() {
        Registered registered=mock(Registered.class);
        registered.addOwnerRole("aaaa","Adidas");
        when(facade.removeProduct("testuser","Adidas",123)).thenReturn(new ResponseEntity("Product must exist", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> response=facade.removeProduct("testuser","Adidas",123);
        assertEquals(response,new ResponseEntity("Product must exist", HttpStatus.BAD_REQUEST));
    }

    @Test
    void removeProduct_NotManager() {
        Registered registered=mock(Registered.class);
        facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,category,keyWords);
        when(facade.removeProduct("testuser","Adidas",123)).thenReturn(new ResponseEntity("User doesn't have permission to this store", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> response=facade.removeProduct("testuser","Adidas",123);
        assertEquals(response,new ResponseEntity("User doesn't have permission to this store", HttpStatus.BAD_REQUEST));
    }

    @Test
    void setProduct_name_success() {
        Registered registered=mock(Registered.class);
        registered.addOwnerRole("aaaa","Adidas");
        facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,0,keyWords);
        when(facade.setProductName("testuser","Adidas",123,"Samba Shoes")).thenReturn(new ResponseEntity("Success editing name to product", HttpStatus.OK));
        ResponseEntity<String> response=facade.setProductName("testuser","Adidas",123,"Samba Shoes");
        assertEquals(response,new ResponseEntity("Success editing name to product", HttpStatus.OK));
    }

    @Test
    void setProduct_Price_PriceLessThanZero() {
        Registered registered=mock(Registered.class);
        registered.addOwnerRole("aaaa","Adidas");
        facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,0,keyWords);
        when(facade.setProductPrice("testuser","Adidas",123,-300)).thenReturn(new ResponseEntity("Price can't be negative number", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> response=facade.setProductPrice("testuser","Adidas",123,-300);
        assertEquals(response,new ResponseEntity("Price can't be negative number", HttpStatus.BAD_REQUEST));
    }

    @Test
    void setProduct_description_UserNotExist() {
        facade.addProduct("testuser",123,"Adidas","Samba shoes","White and black snickers shoes",350.0,1,8.0,0,keyWords);
        when(facade.setProductDescription("testuser","Adidas",123,"White and black cool snickers shoes")).thenReturn(new ResponseEntity("User must exist", HttpStatus.BAD_REQUEST));
        ResponseEntity<String> response=facade.setProductDescription("testuser","Adidas",123,"White and black cool snickers shoes");
        assertEquals(response,new ResponseEntity("User must exist", HttpStatus.BAD_REQUEST));
    }

}