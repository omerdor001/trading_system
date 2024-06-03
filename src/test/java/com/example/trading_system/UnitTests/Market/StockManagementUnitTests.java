package com.example.trading_system.UnitTests.Market;

import com.example.trading_system.domain.stores.MarketFacade;
import com.example.trading_system.domain.stores.MarketFacadeImp;
import com.example.trading_system.domain.stores.StorePolicy;
import com.example.trading_system.domain.users.UserFacade;
import com.example.trading_system.domain.users.UserFacadeImp;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StockManagementUnitTests {
    MarketFacade marketFacade;
    UserFacade userFacade;

    @BeforeAll
    void setUp() {
        marketFacade=MarketFacadeImp.getInstance();
        userFacade= UserFacadeImp.getInstance();
        try {
            userFacade.register(0,"testuser0","1pA22w0rd", LocalDate.now());
            userFacade.register(1,"testuser1","pA22w0rd1", LocalDate.now());
            userFacade.register(2,"testuser2","pA22w0rd2", LocalDate.now());
            userFacade.login(0,"testuser0","1pA22w0rd");
            userFacade.login(1,"testuser1","pA22w0rd1");
            userFacade.login(2,"testuser2","pA22w0rd2");
            userFacade.openStore("testuser0","Adidas","sport shop",mock(StorePolicy.class));
            userFacade.openStore("testuser0","Nike","sport shop",mock(StorePolicy.class));
            userFacade.appointOwner("testuser0","testuser1","Adidas");
            userFacade.appointManager("testuser0","testuser2","Adidas",false,false,false,false);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    public void tearDown(){
        userFacade.logout(0,"testuser0");
        userFacade.logout(1,"testuser1");
        userFacade.logout(2,"testuser2");
        marketFacade.deleteInstance();
        userFacade.deleteInstance();
    }

    //TODO add concurrency tests

    @Test
    void addProduct_Success() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Samba");
        assertDoesNotThrow(() -> {
            marketFacade.addProduct("testuser1", 123, "Adidas", "Samba", "Snickers shoes",
                    330.0, 100, 5.0, 0, keyWords);
        }, "addProduct should not throw any exceptions");
    }

    @Test
    void addProduct_UserNotExist() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Samba");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            marketFacade.addProduct("testuser4", 123, "Adidas", "Samba", "Snickers shoes",
                    100.0, 100, 5.0, 0, keyWords);
        });
        assertEquals("User must exist", exception.getMessage());
    }

    @Test
    void addProduct_StoreNotExist() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Samba");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            marketFacade.addProduct("testuser1", 123, "Adidas1", "Samba", "Snickers shoes",
                    100.0, 100, 5.0, 0, keyWords);
        });
        assertEquals("Store must exist", exception.getMessage());
    }

    @Test
    void addProduct_PriceLessThanZero() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Samba");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            marketFacade.addProduct("testuser1", 123, "Adidas", "Samba", "Snickers shoes",
                    -100.0, 100, 5.0, 0, keyWords);
        });
        assertEquals("Price can't be negative number", exception.getMessage());
    }

    @Test
    void addProduct_QuantityLessEqualThanZero() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Samba");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            marketFacade.addProduct("testuser1", 123, "Adidas", "Samba", "Snickers shoes",
                    100.0, -100, 5.0, 0, keyWords);
        });
        assertEquals("Quantity must be natural number", exception.getMessage());
    }

    @Test
    void addProduct_RatingLessThanZero() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Samba");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            marketFacade.addProduct("testuser1", 123, "Adidas", "Samba", "Snickers shoes",
                    100.0, 100, -5.0, 0, keyWords);
        });
        assertEquals("Rating can't be negative number", exception.getMessage());
    }

    @Test
    void addProduct_NotOwnerButDiffRole() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Samba");
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> {
            marketFacade.addProduct("testuser2", 123, "Adidas", "Samba", "Snickers shoes",
                    100.0, 100, 5.0, 0, keyWords);
        });
        assertEquals("Manager cannot add products", exception.getMessage());
    }

    @Test
    void addProduct_NotOwnerOfThisStore() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Samba");
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            marketFacade.addProduct("testuser2", 123, "Nike", "Samba", "Snickers shoes",
                    100.0, 100, 5.0, 0, keyWords);
        });
        assertEquals("User doesn't have permission to this store", exception.getMessage());
    }


    @Test
    void removeProduct_Success() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("testuser0", 124, "Nike", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 0, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        assertDoesNotThrow(() -> {
            marketFacade.removeProduct("testuser0","Nike",124);
        }, "removeProduct should not throw any exceptions");
    }

    @Test
    void removeProduct_UserNotExist() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("testuser0", 124, "Nike", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 0, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            marketFacade.removeProduct("testuser4","Nike",124);
        });
        assertEquals("User must exist", exception.getMessage());
    }

    @Test
    void removeProduct_StoreNotExist() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("testuser0", 124, "Nike", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 0, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            marketFacade.removeProduct("testuser0","Nike1",124);
        });
        assertEquals("Store must exist", exception.getMessage());
    }

    @Test
    void removeProduct_ProductNotExist() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("testuser0", 124, "Nike", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 0, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            marketFacade.removeProduct("testuser0","Nike",125);
        });
        assertEquals("Product must exist", exception.getMessage());
    }

    @Test
    void removeProduct_NotOwnerToThisStore() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("testuser0", 124, "Nike", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 0, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            marketFacade.removeProduct("testuser2","Nike",124);
        });
        assertEquals("User doesn't have permission to this store", exception.getMessage());
    }

    @Test
    void removeProduct_NotOwnerButtDiffRole() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("testuser0", 124, "Adidas", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 0, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> {
            marketFacade.removeProduct("testuser2","Adidas",124);
        });
        assertEquals("Manager cannot remove products", exception.getMessage());
    }

    @Test
    void setProduct_name_success() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("testuser0", 124, "Nike", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 0, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        assertDoesNotThrow(() -> {
            marketFacade.setProductName("testuser0","Nike",124,"Shirt1");
        }, "setProductName should not throw any exceptions");
    }

    @Test
    void setProduct_Price_PriceLessThanZero() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("testuser0", 124, "Nike", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 0, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            marketFacade.setProductPrice("testuser0","Nike",124,-150.0);
        });
        assertEquals("Price can't be negative number", exception.getMessage());
    }

    @Test
    void setProduct_description_UserNotExist() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("testuser0", 124, "Nike", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 0, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            marketFacade.setProductPrice("testuser4","Nike",124,100.0);
        });
        assertEquals("User must exist", exception.getMessage());
    }

    @Test
    void setProduct_Rating_ProductNotExist() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("testuser0", 124, "Nike", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 0, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            marketFacade.setRating("testuser0","Nike",125,4.0);
        });
        assertEquals("Product must exist", exception.getMessage());
    }

    @Test
    void setProduct_quantity_StoreNotExist() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("testuser0", 124, "Nike", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 0, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            marketFacade.setProductQuantity("testuser0","Nike1",124,50);
        });
        assertEquals("Store must exist", exception.getMessage());
    }

    @Test
    void setProduct_description_NotOwnerOfThisStore() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("testuser0", 124, "Nike", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 0, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            marketFacade.setProductDescription("testuser2","Nike",124,"Sport shirt1");
        });
        assertEquals("User doesn't have permission to this store", exception.getMessage());
    }

    @Test
    void setProduct_category_NotOwnerButtDiffRol() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("testuser0", 124, "Adidas", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 0, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> {
            marketFacade.setProductDescription("testuser2","Adidas",124,"Sport shirt1");
        });
        assertEquals("Manager cannot edit products", exception.getMessage());
    }


}