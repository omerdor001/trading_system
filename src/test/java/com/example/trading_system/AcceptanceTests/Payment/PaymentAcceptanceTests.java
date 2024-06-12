package com.example.trading_system.AcceptanceTests.Payment;

import com.example.trading_system.domain.externalservices.PaymentServiceProxy;
import com.example.trading_system.domain.stores.*;
import com.example.trading_system.domain.users.*;
import com.example.trading_system.service.TradingSystem;
import com.example.trading_system.service.TradingSystemImp;
import com.example.trading_system.service.UserServiceImp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentAcceptanceTests {
    private TradingSystem tradingSystem;
//    private PaymentFacade paymentFacade;
    private MarketFacadeImp marketFacade;
    private UserFacadeImp userFacade;
    private UserServiceImp userService;
    private PaymentServiceProxy paymentServiceProxy;
    private String username;
    private String token;

    @BeforeEach
    void setUp() {
        tradingSystem = TradingSystemImp.getInstance();
        tradingSystem.register("owner1", "password123",LocalDate.now());
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
        tradingSystem.openStore(username,token, "store1", "", new StorePolicy());
        tradingSystem.addProduct(username,token, 0,"store1","product1", "", 1, 5, 1, 1, new LinkedList<>());
//        paymentFacade = PaymentFacadeImp.getInstance();
        marketFacade = MarketFacadeImp.getInstance();
        userFacade = UserFacadeImp.getInstance();
        userService = mock(UserServiceImp.class);
        paymentServiceProxy = mock(PaymentServiceProxy.class);
        tradingSystem.enter();
    }

    @AfterEach
    void setDown(){
        tradingSystem.deleteInstance();
    }

//    @Test
//    void testVisitorCheckAvailabilityAndConditions_Success() {
//        tradingSystem.visitorAddToCart(username, token,1, 0,"store1",1);
//        assertTrue(paymentFacade.VisitorCheckAvailabilityAndConditions(1));
//    }

//    @Test
//    void testVisitorCheckAvailabilityAndConditions_ProductNotAvailable() {
//        int visitorId = 1;
//        Visitor visitor = new Visitor(visitorId);
//        Cart cart = new Cart();
//        ShoppingBag shoppingBag = new ShoppingBag("store1");
//        shoppingBag.addProduct(1, 2);
//        cart.addShoppingBag("store1", shoppingBag);
//        visitor.setShopping_cart(cart);
//        userFacade.getVisitors().put(visitorId, visitor);
//
//        Store store = new Store("store1", "owner1");
//        Product product = new Product(1, "product1", 100, 1);  // Less quantity
//        store.addProduct(product);
//        marketFacade.getStores().put("store1", store);
//
//        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
//            paymentFacade.VisitorCheckAvailabilityAndConditions(visitorId);
//        });
//        assertEquals("Product doesn't exist or not enough quantity", thrown.getMessage());
//    }
//
//    @Test
//    void testRegisteredCheckAvailabilityAndConditions_Success() {
//        String registeredId = "user1";
//        Registered registered = new Registered(1, registeredId, "encryptedPass", LocalDate.of(1990, 1, 1));
//        Cart cart = new Cart();
//        ShoppingBag shoppingBag = new ShoppingBag("store1");
//        shoppingBag.addProduct(1, 2);
//        cart.addShoppingBag("store1", shoppingBag);
//        registered.setShopping_cart(cart);
//        userFacade.getRegistered().put(registeredId, registered);
//
//        Store store = new Store("store1", "owner1");
//        Product product = new Product(1, "product1", 100, 5);
//        store.addProduct(product);
//        marketFacade.getStores().put("store1", store);
//
//        assertTrue(paymentFacade.registeredCheckAvailabilityAndConditions(registeredId));
//    }
//
//    @Test
//    void testRegisteredCheckAvailabilityAndConditions_ProductNotAvailable() {
//        String registeredId = "user1";
//        Registered registered = new Registered(1, registeredId, "encryptedPass", LocalDate.of(1990, 1, 1));
//        Cart cart = new Cart();
//        ShoppingBag shoppingBag = new ShoppingBag("store1");
//        shoppingBag.addProduct(1, 2);
//        cart.addShoppingBag("store1", shoppingBag);
//        registered.setShopping_cart(cart);
//        userFacade.getRegistered().put(registeredId, registered);
//
//        Store store = new Store("store1", "owner1");
//        Product product = new Product(1, "product1", 100, 1);  // Less quantity
//        store.addProduct(product);
//        marketFacade.getStores().put("store1", store);
//
//        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
//            paymentFacade.registeredCheckAvailabilityAndConditions(registeredId);
//        });
//        assertEquals("Product doesn't exist or not enough quantity", thrown.getMessage());
//    }
//
//    @Test
//    void testVisitorApprovePurchase_Success() {
//        int visitorId = 1;
//        Visitor visitor = new Visitor(visitorId);
//        Cart cart = new Cart();
//        ShoppingBag shoppingBag = new ShoppingBag("store1");
//        shoppingBag.addProduct(1, 2);
//        cart.addShoppingBag("store1", shoppingBag);
//        visitor.setShopping_cart(cart);
//        userFacade.getVisitors().put(visitorId, visitor);
//
//        Store store = new Store("store1", "owner1");
//        Product product = new Product(1, "product1", 100, 5);
//        store.addProduct(product);
//        marketFacade.getStores().put("store1", store);
//
//        when(paymentServiceProxy.processPayment(anyDouble())).thenReturn(true);
//
//        // Simulate method to inject the mocked PaymentServiceProxy
//        paymentFacade.VisitorApprovePurchase(visitorId, "mockPaymentService");
//
//        assertEquals(3, store.getProducts().get(1).getProduct_quantity());
//    }
//
//    @Test
//    void testRegisteredApprovePurchase_Success() {
//        String registeredId = "user1";
//        Registered registered = new Registered(1, registeredId, "encryptedPass", LocalDate.of(1990, 1, 1));
//        Cart cart = new Cart();
//        ShoppingBag shoppingBag = new ShoppingBag("store1");
//        shoppingBag.addProduct(1, 2);
//        cart.addShoppingBag("store1", shoppingBag);
//        registered.setShopping_cart(cart);
//        userFacade.getRegistered().put(registeredId, registered);
//
//        Store store = new Store("store1", "owner1");
//        Product product = new Product(1, "product1", 100, 5);
//        store.addProduct(product);
//        marketFacade.getStores().put("store1", store);
//
//        when(paymentServiceProxy.processPayment(anyDouble())).thenReturn(true);
//
//        // Simulate method to inject the mocked PaymentServiceProxy
//        paymentFacade.RegisteredApprovePurchase(registeredId, "mockPaymentService");
//
//        assertEquals(3, store.getProducts().get(1).getProduct_quantity());
//    }
//
//    @Test
//    void testVisitorApprovePurchase_FailedDueToAvailability() {
//        int visitorId = 1;
//        Visitor visitor = new Visitor(visitorId);
//        Cart cart = new Cart();
//        ShoppingBag shoppingBag = new ShoppingBag("store1");
//        shoppingBag.addProduct(1, 2);
//        cart.addShoppingBag("store1", shoppingBag);
//        visitor.setShopping_cart(cart);
//        userFacade.getVisitors().put(visitorId, visitor);
//
//        Store store = new Store("store1", "owner1");
//        Product product = new Product(1, "product1", 100, 1);  // Less quantity
//        store.addProduct(product);
//        marketFacade.getStores().put("store1", store);
//
//        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
//            paymentFacade.VisitorApprovePurchase(visitorId, "mockPaymentService");
//        });
//        assertEquals("Products are not available or do not meet purchase conditions.", thrown.getMessage());
//    }
//
//    @Test
//    void testRegisteredApprovePurchase_FailedDueToAvailability() {
//        String registeredId = "user1";
//        Registered registered = new Registered(1, registeredId, "encryptedPass", LocalDate.of(1990, 1, 1));
//        Cart cart = new Cart();
//        ShoppingBag shoppingBag = new ShoppingBag("store1");
//        shoppingBag.addProduct(1, 2);
//        cart.addShoppingBag("store1", shoppingBag);
//        registered.setShopping_cart(cart);
//        userFacade.getRegistered().put(registeredId, registered);
//
//        Store store = new Store("store1", "owner1");
//        Product product = new Product(1, "product1", 100, 1);  // Less quantity
//        store.addProduct(product);
//        marketFacade.getStores().put("store1", store);
//
//        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
//            paymentFacade.RegisteredApprovePurchase(registeredId, "mockPaymentService");
//        });
//        assertEquals("Products are not available or do not meet purchase conditions.", thrown.getMessage());
//    }
}
