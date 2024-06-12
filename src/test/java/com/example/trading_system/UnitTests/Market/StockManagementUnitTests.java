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
            userFacade.register("testuser0","1pA22w0rd", LocalDate.now());
            userFacade.register("testuser1","pA22w0rd1", LocalDate.now());
            userFacade.register("testuser2","pA22w0rd2", LocalDate.now());
            userFacade.enter(0);
            userFacade.enter(1);
            userFacade.enter(2);
            userFacade.login("v0","testuser0","1pA22w0rd");
            userFacade.login("v1","testuser1","pA22w0rd1");
            userFacade.login("v2","testuser2","pA22w0rd2");
            userFacade.createStore("rtestuser0","Adidas","sport shop",mock(StorePolicy.class));
            userFacade.createStore("rtestuser0","Nike","sport shop",mock(StorePolicy.class));
            userFacade.appointOwner("rtestuser0","rtestuser1","Adidas");
            userFacade.appointManager("rtestuser0","rtestuser2","Adidas",false,false,false,false);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    public void tearDown(){
        userFacade.logout(0,"rtestuser0");
        userFacade.logout(1,"rtestuser1");
        userFacade.logout(2,"rtestuser2");
        marketFacade.deleteInstance();
        userFacade.deleteInstance();
    }

    @Test
    void addProduct_Success() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Samba");
        int numOfProducts_before=marketFacade.getStore("Adidas").getProducts().size();
        assertDoesNotThrow(() -> {
            marketFacade.addProduct("rtestuser1", 123, "Adidas", "Samba", "Snickers shoes",
                    330.0, 100, 5.0, 1, keyWords);
        }, "addProduct should not throw any exceptions");
        int numOfProducts_after=marketFacade.getStore("Adidas").getProducts().size();
        assertEquals(numOfProducts_before,numOfProducts_after-1);
    }

    @Test
    void addProduct_UserNotExist() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Samba");
        int numOfProducts_before=marketFacade.getStore("Adidas").getProducts().size();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            marketFacade.addProduct("rtestuser4", 123, "Adidas", "Samba", "Snickers shoes",
                    100.0, 100, 5.0, 1, keyWords);
        });
        assertEquals("User must exist", exception.getMessage());
        int numOfProducts_after=marketFacade.getStore("Adidas").getProducts().size();
        assertEquals(numOfProducts_before,numOfProducts_after);
    }

    @Test
    void addProduct_StoreNotExist() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Samba");
        int numOfProducts_before=marketFacade.getStore("Adidas").getProducts().size();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            marketFacade.addProduct("rtestuser1", 123, "Adidas1", "Samba", "Snickers shoes",
                    100.0, 100, 5.0, 1, keyWords);
        });
        assertEquals("Store must exist", exception.getMessage());
        int numOfProducts_after=marketFacade.getStore("Adidas").getProducts().size();
        assertEquals(numOfProducts_before,numOfProducts_after);
    }

    @Test
    void addProduct_PriceLessThanZero() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Samba");
        int numOfProducts_before=marketFacade.getStore("Adidas").getProducts().size();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            marketFacade.addProduct("rtestuser1", 123, "Adidas", "Samba", "Snickers shoes",
                    -100.0, 100, 5.0, 1, keyWords);
        });
        int numOfProducts_after=marketFacade.getStore("Adidas").getProducts().size();
        assertEquals("Price can't be negative number", exception.getMessage());
        assertEquals(numOfProducts_before,numOfProducts_after);
    }

    @Test
    void addProduct_QuantityLessEqualThanZero() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Samba");
        int numOfProducts_before=marketFacade.getStore("Adidas").getProducts().size();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            marketFacade.addProduct("rtestuser1", 123, "Adidas", "Samba", "Snickers shoes",
                    100.0, -100, 5.0, 1, keyWords);
        });
        int numOfProducts_after=marketFacade.getStore("Adidas").getProducts().size();
        assertEquals("Quantity must be natural number", exception.getMessage());
        assertEquals(numOfProducts_before,numOfProducts_after);

    }

    @Test
    void addProduct_RatingLessThanZero() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Samba");
        int numOfProducts_before=marketFacade.getStore("Adidas").getProducts().size();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            marketFacade.addProduct("rtestuser1", 123, "Adidas", "Samba", "Snickers shoes",
                    100.0, 100, -5.0, 1, keyWords);
        });
        int numOfProducts_after=marketFacade.getStore("Adidas").getProducts().size();
        assertEquals("Rating can't be negative number", exception.getMessage());
        assertEquals(numOfProducts_before,numOfProducts_after);
    }

    @Test
    void addProduct_NotOwnerButDiffRole() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Samba");
        int numOfProducts_before=marketFacade.getStore("Adidas").getProducts().size();
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> {
            marketFacade.addProduct("rtestuser2", 123, "Adidas", "Samba", "Snickers shoes",
                    100.0, 100, 5.0, 1, keyWords);
        });
        int numOfProducts_after=marketFacade.getStore("Adidas").getProducts().size();
        assertEquals("Manager cannot add products", exception.getMessage());
        assertEquals(numOfProducts_before,numOfProducts_after);
    }

    @Test
    void addProduct_NotOwnerOfThisStore() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Samba");
        int numOfProducts_before=marketFacade.getStore("Adidas").getProducts().size();
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            marketFacade.addProduct("rtestuser2", 123, "Nike", "Samba", "Snickers shoes",
                    100.0, 100, 5.0, 1, keyWords);
        });
        int numOfProducts_after=marketFacade.getStore("Adidas").getProducts().size();
        assertEquals("User doesn't have permission to this store", exception.getMessage());
        assertEquals(numOfProducts_before,numOfProducts_after);
    }

    @Test
    void removeProduct_Success() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("rtestuser0", 150, "Nike", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 1, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        int numOfProducts_before=marketFacade.getStore("Nike").getProducts().size();
        assertDoesNotThrow(() -> {
            marketFacade.removeProduct("rtestuser0","Nike",150);
        }, "removeProduct should not throw any exceptions");
        int numOfProducts_after=marketFacade.getStore("Nike").getProducts().size();
        assertEquals(numOfProducts_before,numOfProducts_after+1);
    }

    @Test
    void removeProduct_UserNotExist() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("rtestuser0", 143, "Nike", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 1, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        int numOfProducts_before=marketFacade.getStore("Nike").getProducts().size();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            marketFacade.removeProduct("rtestuser4","Nike",143);
        });
        int numOfProducts_after=marketFacade.getStore("Nike").getProducts().size();
        assertEquals("User must exist", exception.getMessage());
        assertEquals(numOfProducts_before,numOfProducts_after);
    }

    @Test
    void removeProduct_StoreNotExist() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("rtestuser0", 140, "Nike", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 1, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        int numOfProducts_before=marketFacade.getStore("Nike").getProducts().size();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            marketFacade.removeProduct("rtestuser0","Nike1",140);
        });
        int numOfProducts_after=marketFacade.getStore("Nike").getProducts().size();
        assertEquals("Store must exist", exception.getMessage());
        assertEquals(numOfProducts_before,numOfProducts_after);
    }

    @Test
    void removeProduct_ProductNotExist() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("rtestuser0", 155, "Nike", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 1, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        int numOfProducts_before=marketFacade.getStore("Nike").getProducts().size();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            marketFacade.removeProduct("rtestuser0","Nike",101);
        });
        int numOfProducts_after=marketFacade.getStore("Nike").getProducts().size();
        assertEquals("Product must exist", exception.getMessage());
        assertEquals(numOfProducts_before,numOfProducts_after);
    }

    @Test
    void removeProduct_NotOwnerToThisStore() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("rtestuser0", 124, "Nike", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 1, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        int numOfProducts_before=marketFacade.getStore("Nike").getProducts().size();
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            marketFacade.removeProduct("rtestuser2","Nike",124);
        });
        int numOfProducts_after=marketFacade.getStore("Nike").getProducts().size();
        assertEquals("User doesn't have permission to this store", exception.getMessage());
        assertEquals(numOfProducts_before,numOfProducts_after);
    }

    @Test
    void removeProduct_NotOwnerButtDiffRole() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("rtestuser0", 124, "Adidas", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 1, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        int numOfProducts_before=marketFacade.getStore("Nike").getProducts().size();
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> {
            marketFacade.removeProduct("rtestuser2","Adidas",124);
        });
        int numOfProducts_after=marketFacade.getStore("Nike").getProducts().size();
        assertEquals("Manager cannot remove products", exception.getMessage());
        assertEquals(numOfProducts_before,numOfProducts_after);
    }

    @Test
    void setProduct_name_success() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("rtestuser0", 141, "Nike", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 1, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        assertDoesNotThrow(() -> {
            marketFacade.setProductName("rtestuser0","Nike",141,"Shirt1");
        }, "setProductName should not throw any exceptions");
        assertEquals(marketFacade.getStore("Nike").getProduct(141).getProduct_name(),"Shirt1");
    }

    @Test
    void setProduct_Price_PriceLessThanZero() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("rtestuser0", 126, "Nike", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 1, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            marketFacade.setProductPrice("rtestuser0","Nike",126,-150.0);
        });
        assertEquals("Price can't be negative number", exception.getMessage());
        assertEquals(marketFacade.getStore("Nike").getProduct(126).getProduct_price(),100.0);
    }

    @Test
    void setProduct_description_UserNotExist() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("rtestuser0", 127, "Nike", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 1, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            marketFacade.setProductPrice("rtestuser4","Nike",127,100.0);
        });
        assertEquals("User must exist", exception.getMessage());
        assertEquals(marketFacade.getStore("Nike").getProduct(127).getProduct_description(),"Sport shirt");
    }

    @Test
    void setProduct_Rating_ProductNotExist() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("rtestuser0", 128, "Nike", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 1, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            marketFacade.setRating("rtestuser0","Nike",100,4.0);
        });
        assertEquals("Product must exist", exception.getMessage());
        assertEquals(marketFacade.getStore("Nike").getProduct(128).getRating(),5.0);
    }

    @Test
    void setProduct_quantity_StoreNotExist() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("rtestuser0", 129, "Nike", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 1, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            marketFacade.setProductQuantity("rtestuser0","Nike1",129,50);
        });
        assertEquals("Store must exist", exception.getMessage());
        assertEquals(marketFacade.getStore("Nike").getProduct(129).getProduct_quantity(),100);
    }

    @Test
    void setProduct_description_NotOwnerOfThisStore() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("rtestuser0", 130, "Nike", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 1, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            marketFacade.setProductDescription("rtestuser2","Nike",130,"Sport shirt1");
        });
        assertEquals("User doesn't have permission to this store", exception.getMessage());
        assertEquals(marketFacade.getStore("Nike").getProduct(130).getProduct_description(),"Sport shirt");
    }

    @Test
    void setProduct_category_NotOwnerButtDiffRol() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("rtestuser0", 142, "Adidas", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 1, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> {
            marketFacade.setProductDescription("rtestuser2","Adidas",142,"Sport shirt1");
        });
        assertEquals("Manager cannot edit products", exception.getMessage());
        assertEquals(marketFacade.getStore("Adidas").getProduct(142).getCategory().getIntValue(),1);
    }

    //Concurrency tests
    @Test
    void addProduct_ConcurrencySuccess(){
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        int numOfProducts_before=marketFacade.getStore("Adidas").getProducts().size();
        Runnable task = () -> {
            try {
                marketFacade.addProduct("rtestuser1", 143, "Adidas", "Samba", "Snickers shoes",
                        330.0, 100, 5.0, 1, keyWords);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        };
        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        int numOfProducts_after=marketFacade.getStore("Adidas").getProducts().size();
        assertEquals(numOfProducts_before,numOfProducts_after-1);
    }

    @Test
    void addProduct_ConcurrencyPriceLessThanZero() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Samba");
        int numOfProducts_before=marketFacade.getStore("Adidas").getProducts().size();
        Runnable task = () -> {
            try {
                marketFacade.addProduct("rtestuser1", 123, "Adidas", "Samba", "Snickers shoes",
                        -100.0, 100, 5.0, 1, keyWords);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        };
        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        int numOfProducts_after=marketFacade.getStore("Adidas").getProducts().size();
        assertEquals(numOfProducts_before,numOfProducts_after);
    }

    @Test
    void removeProduct_ConcurrencySuccess(){
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("rtestuser0", 150, "Nike", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 1, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        int numOfProducts_before=marketFacade.getStore("Nike").getProducts().size();
        Runnable task = () -> {
            try {
                marketFacade.removeProduct("rtestuser0","Nike",150);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        };
        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        int numOfProducts_after=marketFacade.getStore("Nike").getProducts().size();
        assertEquals(numOfProducts_before,numOfProducts_after+1);
    }

    @Test
    void removeProduct_ConcurrencyProductNotExist(){
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("rtestuser0", 151, "Nike", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 1, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        int numOfProducts_before=marketFacade.getStore("Nike").getProducts().size();
        Runnable task = () -> {
            try {
                marketFacade.removeProduct("rtestuser0","Nike",101);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        };
        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        int numOfProducts_after=marketFacade.getStore("Nike").getProducts().size();
        assertEquals(numOfProducts_before,numOfProducts_after);
    }

    @Test
    void setProduct_Concurrency_name_success() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("rtestuser0", 145, "Nike", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 1, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        Runnable task = () -> {
            try {
                marketFacade.setProductName("rtestuser0","Nike",145,"Shirt1");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        };
        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        assertEquals(marketFacade.getStore("Nike").getProduct(145).getProduct_name(),"Shirt1");
    }

    @Test
    void setProduct_Price_ConcurrencyPriceLessThanZero() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Shirt11");
        try{
            marketFacade.addProduct("rtestuser0", 156, "Nike", "Shirt11", "Sport shirt",
                    100.0, 100, 5.0, 1, keyWords);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        Runnable task = () -> {
            try {
                marketFacade.setProductPrice("rtestuser0","Nike",156,-150.0);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        };
        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        assertEquals(marketFacade.getStore("Nike").getProduct(156).getProduct_price(),100.0);
    }


}