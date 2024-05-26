package com.example.trading_system.domain.users;

import com.example.trading_system.domain.externalservices.*;
import com.example.trading_system.domain.stores.MarketFacadeImp;
import com.example.trading_system.domain.stores.Product;
import com.example.trading_system.domain.stores.Store;
import com.example.trading_system.service.UserServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class PaymentFacadeImp implements PaymentFacade  {
    MarketFacadeImp marketFacade = MarketFacadeImp.getInstance();
    UserFacadeImp userFacade = UserFacadeImp.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);
    private static class Singleton {
        private static final PaymentFacadeImp INSTANCE = new PaymentFacadeImp();
    }

    public static PaymentFacadeImp getInstance() {
        return PaymentFacadeImp.Singleton.INSTANCE;
    }
    public synchronized boolean VisitorCheckAvailabilityAndConditions(int visitorId) {
        if (!userFacade.getVisitors().containsKey(visitorId)) {
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
        Visitor visitor = userFacade.getVisitors().get(visitorId);
        Cart cart = visitor.getShopping_cart();
        HashMap<String, ShoppingBag> shoppingBags = cart.getShoppingBags();
        for (Map.Entry<String, ShoppingBag> entry : shoppingBags.entrySet()) {
            String storeId = entry.getKey();
            ShoppingBag shoppingBag = entry.getValue();
            Store store = marketFacade.getStores().get(storeId);
            for (Map.Entry<Integer, Integer> productEntry : shoppingBag.getProducts_list().entrySet()) {
                Product product = store.getProducts().get(productEntry.getKey());
                if (product == null || product.getProduct_quantity() < productEntry.getValue()) {
                    logger.error("Product doesn't exist or not enough quantity");
                    throw new RuntimeException("Product doesn't exist or not enough quantity");
                }
            }
        }
        return true;
    }

    public synchronized boolean registeredCheckAvailabilityAndConditions(String registeredId) {
        if (!userFacade.getRegistered().containsKey(registeredId)) {
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
        Registered registered1 = userFacade.getRegistered().get(registeredId);
        Cart cart = registered1.getShopping_cart();
        HashMap<String, ShoppingBag> shoppingBags = cart.getShoppingBags();
        for (Map.Entry<String, ShoppingBag> entry : shoppingBags.entrySet()) {
            String storeId = entry.getKey();
            ShoppingBag shoppingBag = entry.getValue();
            Store store = marketFacade.getStores().get(storeId);
            for (Map.Entry<Integer, Integer> productEntry : shoppingBag.getProducts_list().entrySet()) {
                Product product = store.getProducts().get(productEntry.getKey());
                if (product == null || product.getProduct_quantity() < productEntry.getValue()) {
                    logger.error("Product doesn't exist or not enough quantity");
                    throw new RuntimeException("Product doesn't exist or not enough quantity");
                }
            }
        }
        return true;
    }

    public synchronized void VisitorApprovePurchase(int visitorId, String paymentService) {
        if (!VisitorCheckAvailabilityAndConditions(visitorId)) {
            logger.error("Products are not available or do not meet purchase conditions.");
            throw new RuntimeException("Products are not available or do not meet purchase conditions.");
        }

        if (!userFacade.getVisitors().containsKey(visitorId)) {
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
        Visitor visitor = userFacade.getVisitors().get(visitorId);
        Cart cart = visitor.getShopping_cart();
        HashMap<String, ShoppingBag> shoppingBags = cart.getShoppingBags();
        for (Map.Entry<String, ShoppingBag> entry : shoppingBags.entrySet()) {
            String storeId = entry.getKey();
            ShoppingBag shoppingBag = entry.getValue();

            Store store = marketFacade.getStores().get(storeId);
            for (Map.Entry<Integer, Integer> productEntry : shoppingBag.getProducts_list().entrySet()) {
                int productId = productEntry.getKey();
                int quantity = productEntry.getValue();

                Product product = store.getProducts().get(productId);
                product.setProduct_quantity(product.getProduct_quantity() - quantity);
            }
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                VisitorReleaseReservedProducts(visitor);
            }
        }, 10 * 60 * 1000);

        PaymentServiceProxy paymentServiceProxy = new PaymentServiceProxy(paymentService);
        double totalPrice = calculateTotalPrice(cart);
        boolean paymentSuccess = paymentServiceProxy.processPayment(totalPrice);

        if (paymentSuccess) {
            logger.info("Purchase successful. Inventory updated.");
        } else {
            logger.error("Payment failed. Please try again.");
        }
    }

    private synchronized void VisitorReleaseReservedProducts(Visitor visitor) {
        Cart cart = visitor.getShopping_cart();
        HashMap<String, ShoppingBag> shoppingBags = cart.getShoppingBags();
        for (Map.Entry<String, ShoppingBag> entry : shoppingBags.entrySet()) {
            String storeId = entry.getKey();
            ShoppingBag shoppingBag = entry.getValue();

            Store store = marketFacade.getStores().get(storeId);
            for (Map.Entry<Integer, Integer> productEntry : shoppingBag.getProducts_list().entrySet()) {
                int productId = productEntry.getKey();
                int quantity = productEntry.getValue();

                Product product = store.getProducts().get(productId);
                product.setProduct_quantity(product.getProduct_quantity() + quantity);
            }
        }
    }
    public synchronized void RegisteredApprovePurchase(String registeredId, String paymentService) {
        if (!registeredCheckAvailabilityAndConditions(registeredId)) {
            logger.error("Products are not available or do not meet purchase conditions.");
            throw new RuntimeException("Products are not available or do not meet purchase conditions.");
        }

        if (!userFacade.getVisitors().containsKey(registeredId)) {
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
        Registered registered = userFacade.getRegistered().get(registeredId);
        Cart cart = registered.getShopping_cart();
        HashMap<String, ShoppingBag> shoppingBags = cart.getShoppingBags();
        for (Map.Entry<String, ShoppingBag> entry : shoppingBags.entrySet()) {
            String storeId = entry.getKey();
            ShoppingBag shoppingBag = entry.getValue();

            Store store = marketFacade.getStores().get(storeId);
            for (Map.Entry<Integer, Integer> productEntry : shoppingBag.getProducts_list().entrySet()) {
                int productId = productEntry.getKey();
                int quantity = productEntry.getValue();

                Product product = store.getProducts().get(productId);
                product.setProduct_quantity(product.getProduct_quantity() - quantity);
            }
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                RegisteredReleaseReservedProducts(registered);
            }
        }, 10 * 60 * 1000);

        PaymentServiceProxy paymentServiceProxy = new PaymentServiceProxy(paymentService);
        double totalPrice = calculateTotalPrice(cart);
        boolean paymentSuccess = paymentServiceProxy.processPayment(totalPrice);

        if (paymentSuccess) {
            logger.info("Purchase successful. Inventory updated.");
        } else {
            logger.error("Payment failed. Please try again.");
        }
    }
    private synchronized void RegisteredReleaseReservedProducts(Registered registered) {
        Cart cart = registered.getShopping_cart();
        HashMap<String, ShoppingBag> shoppingBags = cart.getShoppingBags();
        for (Map.Entry<String, ShoppingBag> entry : shoppingBags.entrySet()) {
            String storeId = entry.getKey();
            ShoppingBag shoppingBag = entry.getValue();

            Store store = marketFacade.getStores().get(storeId);
            for (Map.Entry<Integer, Integer> productEntry : shoppingBag.getProducts_list().entrySet()) {
                int productId = productEntry.getKey();
                int quantity = productEntry.getValue();

                Product product = store.getProducts().get(productId);
                product.setProduct_quantity(product.getProduct_quantity() + quantity);
            }
        }
    }
    private double calculateTotalPrice(Cart cart) {
        double price = 0;
        for (Map.Entry<String, ShoppingBag> entry : cart.getShoppingBags().entrySet()) {
            String storeId = entry.getKey();
            ShoppingBag shoppingBag = entry.getValue();
            for (Map.Entry<Integer, Integer> productEntry : shoppingBag.getProducts_list().entrySet()) {
                int productId = productEntry.getKey();
                int quantity = productEntry.getValue();
                price = price + marketFacade.getStores().get(storeId).getProduct(productId).getProduct_price()*quantity;
            }
        }
        return price;
    }
}
