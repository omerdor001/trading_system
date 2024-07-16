package com.example.trading_system.UnitTests.Users;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.*;
import com.example.trading_system.domain.users.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
@SpringBootTest
@Transactional
class PermissionsUnitTests {
    @Autowired
    private UserDatabaseRepository userRepository;

    @Autowired
    private StoreDatabaseRepository storeRepository;
    private UserFacade userFacade;
    private MarketFacade marketFacade;
    private String username1;
    private String username2;
    private String username3;

    @BeforeEach
    public void setUp() {

        userFacade = UserFacadeImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class), mock(NotificationSender.class), userRepository, storeRepository);
        marketFacade= MarketFacadeImp.getInstance(storeRepository);
        username1 = "testuser1";
        username2 = "testuser2";
        username3 = "testuser3";
        try {
            userFacade.register(username1, username1, LocalDate.now());
            userFacade.register(username2, username2, LocalDate.now());
            userFacade.register(username3, username3, LocalDate.now());
            userFacade.enter(0);
            userFacade.enter(1);
            userFacade.enter(2);
            userFacade.login("v0", username1, username1);
            userFacade.login("v1", username2, username2);
            userFacade.login("v2", username3, username3);
            userFacade.createStore("r" + username1, "Adidas", "");
            userFacade.suggestManager("r" + username1, "r" + username2, "Adidas", true, true ,true, true, true);
            userFacade.approveManager("r" + username2, "Adidas", "r" + username1, true, true, true, true, true);
        } catch (Exception e) {
        }
    }

    @AfterEach
    public void tearDown() {
        userFacade.logout(0, "r" + username1);
        userFacade.logout(1, "r" + username2);
        userFacade.logout(2, "r" + username3);
        try {
            userFacade.exit("v0");
            userFacade.exit("v1");
            userFacade.exit("v2");
            userFacade.deleteInstance();
            marketFacade.deleteInstance();
        } catch (Exception e) {
        }
    }

    @Test
    void addProductManager_Success() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Samba");
        int numOfProducts_before=marketFacade.getStore("Adidas").getProducts().size();
        assertDoesNotThrow(() -> {
            marketFacade.addProduct("rtestuser2", 123, "Adidas", "Samba", "Snickers shoes",
                    330.0, 100, 5.0, 1, keyWords);
        }, "addProduct should not throw any exceptions");
        int numOfProducts_after=marketFacade.getStore("Adidas").getProducts().size();
        assertEquals(numOfProducts_before,numOfProducts_after-1);
    }

    @Test
    void addProductManager_NoPermission() {
        try{
            userFacade.editPermissionForManager("rtestuser1","rtestuser2","Adidas",true,false,true,true, true);
        }
        catch (Exception e){}
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Samba");
        int numOfProducts_before=marketFacade.getStore("Adidas").getProducts().size();
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> marketFacade.addProduct("rtestuser2", 123, "Adidas", "Samba", "Snickers shoes",
                100.0, 100, 5.0, 1, keyWords));
        assertEquals("Manager cannot add products", exception.getMessage());
        int numOfProducts_after=marketFacade.getStore("Adidas").getProducts().size();
        assertEquals(numOfProducts_before,numOfProducts_after);
    }

    @Test
    void addDiscountPolicyManager_Success() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Samba");
        try{
            marketFacade.addProduct("rtestuser2",124,"Adidas" ,"product1", "", 1, 1, 1, 1, new LinkedList<>());
        }
        catch (Exception e){}
        ProductInSaleDTO product=new ProductInSaleDTO();
        product.setStoreId("Adidas");
        product.setId(124);
        product.setPrice(1);
        product.setQuantity(1);
        product.setCategory(1);
        LinkedList<ProductInSaleDTO> bag = new LinkedList<>();
        bag.add(product);
        Store store=marketFacade.getStore("Adidas");
        assertDoesNotThrow(() -> {
           marketFacade.addCategoryPercentageDiscount("rtestuser2","Adidas",1,0.5);
        }, "addDiscountPolicy should not throw any exceptions");
        double price = store.calculatePrice(bag);
        assertEquals(0.5, price);
    }

    @Test
    void addDiscountPolicyManager_NoPermission() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Samba");
        try{
            userFacade.editPermissionForManager("rtestuser1","rtestuser2","Adidas",true,true,true,false, true);
            marketFacade.addProduct("rtestuser2",124,"Adidas" ,"product1", "", 1, 1, 1, 1, new LinkedList<>());

        }
        catch (Exception e){}
        ProductInSaleDTO product=new ProductInSaleDTO();
        product.setStoreId("Adidas");
        product.setId(124);
        product.setPrice(1);
        product.setQuantity(1);
        product.setCategory(1);
        LinkedList<ProductInSaleDTO> bag = new LinkedList<>();
        bag.add(product);
        Store store=marketFacade.getStore("Adidas");
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> marketFacade.addCategoryPercentageDiscount("rtestuser2","Adidas",1,0.5));
        assertEquals("Only managers can access isEditDiscountPolicy", exception.getMessage());
        double price = store.calculatePrice(bag);
        assertEquals(1.0, price);
    }

    @Test
    void addPurchasePolicyManager_Success() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Samba");
        try{
            marketFacade.addProduct("rtestuser2",124,"Adidas" ,"product1", "", 1, 1, 1, 1, new LinkedList<>());
        }
        catch (Exception e){}
        ProductInSaleDTO product=new ProductInSaleDTO();
        product.setStoreId("Adidas");
        product.setId(124);
        product.setPrice(1);
        product.setQuantity(1);
        product.setCategory(1);
        LinkedList<ProductInSaleDTO> bag = new LinkedList<>();
        bag.add(product);
        Store store=marketFacade.getStore("Adidas");

        assertDoesNotThrow(() -> {
            marketFacade.addPurchasePolicyByDate("rtestuser2","Adidas",LocalDateTime.now().minusDays(1));
        }, "addPurchasePolicy should not throw any exceptions");
        boolean result = store.validatePurchasePolicies(bag, 20);
        assertTrue(result);
    }

    @Test
    void addPurchasePolicyManager_NoPermission() {
        ArrayList<String> keyWords=new ArrayList<>();
        keyWords.add("Samba");
        try{
            userFacade.editPermissionForManager("rtestuser1","rtestuser2","Adidas",true,true,false,true, true);
            marketFacade.addProduct("rtestuser2",124,"Adidas" ,"product1", "", 1, 1, 1, 1, new LinkedList<>());

        }
        catch (Exception e){}
        ProductInSaleDTO product=new ProductInSaleDTO();
        product.setStoreId("Adidas");
        product.setId(124);
        product.setPrice(1);
        product.setQuantity(1);
        product.setCategory(1);
        LinkedList<ProductInSaleDTO> bag = new LinkedList<>();
        bag.add(product);
        Store store=marketFacade.getStore("Adidas");
        IllegalAccessException exception = assertThrows(IllegalAccessException.class, () -> marketFacade.addPurchasePolicyByDate("rtestuser2","Adidas",LocalDateTime.now().minusDays(1)));
        assertEquals("Only managers can access isEditPurchasePolicy", exception.getMessage());
        boolean result = store.validatePurchasePolicies(bag, 20);
        assertTrue(result);
    }








}