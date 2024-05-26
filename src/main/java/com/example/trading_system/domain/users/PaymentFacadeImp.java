package com.example.trading_system.domain.users;

import com.example.trading_system.domain.externalservices.*;
import com.example.trading_system.domain.stores.*;
import com.example.trading_system.service.UserServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class PaymentFacadeImp implements PaymentFacade {
    MarketFacadeImp marketFacade = MarketFacadeImp.getInstance();
    UserFacadeImp userFacade = UserFacadeImp.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);
    private List<Purchase> purchases;

    public PaymentFacadeImp() {
        purchases = new ArrayList<>();
    }

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
                throw  new RuntimeException("time out !");

            }
        }, 10 * 60 * 1000);

        PaymentServiceProxy paymentServiceProxy = new PaymentServiceProxy(paymentService);
        double totalPrice = calculateTotalPrice(cart);
        boolean paymentSuccess = paymentServiceProxy.processPayment(totalPrice);

        if (paymentSuccess) {
            logger.info("Purchase successful. Inventory updated.");
        } else {
            logger.error("Payment failed. Please try again.");
            VisitorReleaseReservedProducts(visitor);
            throw new RuntimeException("Payment failed. Please try again.");
        }
        addPurchaseVisitor(visitorId);
        timer.cancel();
        timer.purge();
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
                throw  new RuntimeException("time out !");
            }
        }, 10 * 60 * 1000);

        PaymentServiceProxy paymentServiceProxy = new PaymentServiceProxy(paymentService);
        double totalPrice = calculateTotalPrice(cart);
        boolean paymentSuccess = paymentServiceProxy.processPayment(totalPrice);

        if (paymentSuccess) {
            logger.info("Purchase successful. Inventory updated.");
        } else {
            logger.error("Payment failed. Please try again.");
            registeredCheckAvailabilityAndConditions(registeredId);
            throw new RuntimeException("Payment failed. Please try again.");
        }
        addPurchaseRegistered(registeredId);
        timer.cancel();
        timer.purge();
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
                price = price + marketFacade.getStores().get(storeId).getProduct(productId).getProduct_price() * quantity;
            }
        }
        return price;
    }

    public String getPurchaseHistory(String username, String storeName, Integer id, Integer productBarcode) {
        if (!userFacade.getRegistered().containsKey(username)) {
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
        if (!userFacade.getRegistered().get(username).getLogged()) {
            logger.error("User is not logged");
            throw new RuntimeException("User is not logged");
        }
        if (!userFacade.getRegistered().get(username).isCommercialManager()) {
            logger.error("User is not commercial manager");
            throw new RuntimeException("User is not commercial manager");
        }

        List<Purchase> filteredPurchases = purchases;

        if (id != null) {
            filteredPurchases = filteredPurchases.stream()
                    .filter(p -> p.getCustomerId() == id)
                    .collect(Collectors.toList());
        }

        if (storeName != null) {
            filteredPurchases = filteredPurchases.stream()
                    .filter(p -> p.getStoreName().equals(storeName))
                    .collect(Collectors.toList());
        }
        return filteredPurchases.stream()
                .map(Purchase::toString)
                .collect(Collectors.joining("\n"));
    }


    public String getStoresPurchaseHistory(String username, String storeName, Integer id, Integer productBarcode) {
        if (!userFacade.getRegistered().containsKey(username)) {
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
        if (!userFacade.getRegistered().get(username).getLogged()) {
            logger.error("User is not logged");
            throw new RuntimeException("User is not logged");
        }
        if (!userFacade.getRegistered().get(username).isCommercialManager()) {
            logger.error("User is not commercial manager");
            throw new RuntimeException("User is not commercial manager");
        }

        List<Purchase> filteredPurchases = purchases;
        if (storeName != null) {
            filteredPurchases = filteredPurchases.stream()
                    .filter(p -> p.getStoreName().equals(storeName))
                    .collect(Collectors.toList());
        }
        return filteredPurchases.stream()
                .map(Purchase::toString)
                .collect(Collectors.joining("\n"));
    }

    private void addPurchaseRegistered(String registeredId) {
        Cart cart = userFacade.getRegistered().get(registeredId).getShopping_cart();
        double totalcount=0;
        List<ProductInSale> productInSales = new ArrayList<>();
        HashMap<String, ShoppingBag> shoppingBags = cart.getShoppingBags();
        for (Map.Entry<String, ShoppingBag> entry : shoppingBags.entrySet()) {
            String storeId = entry.getKey();
            ShoppingBag shoppingBag = entry.getValue();
            Store store = marketFacade.getStores().get(storeId);
            for (Map.Entry<Integer, Integer> productEntry : shoppingBag.getProducts_list().entrySet()) {
                int productId = productEntry.getKey();
                int quantity = productEntry.getValue();
                Product product = store.getProducts().get(productId);
                totalcount =totalcount + quantity*product.getProduct_price();
                ProductInSale productInSale = new ProductInSale(productId, product.getProduct_price(), quantity, storeId);
                productInSales.add(productInSale);
            }

            Purchase purchase = new Purchase(userFacade.getRegistered().get(registeredId).getId(registeredId), productInSales,storeId,totalcount );
        }
    }
    private void addPurchaseVisitor(int id) {
        Cart cart = userFacade.getVisitors().get(id).getShopping_cart();
        double totalcount=0;
        List<ProductInSale> productInSales = new ArrayList<>();
        HashMap<String, ShoppingBag> shoppingBags = cart.getShoppingBags();
        for (Map.Entry<String, ShoppingBag> entry : shoppingBags.entrySet()) {
            String storeId = entry.getKey();
            ShoppingBag shoppingBag = entry.getValue();
            Store store = marketFacade.getStores().get(storeId);
            for (Map.Entry<Integer, Integer> productEntry : shoppingBag.getProducts_list().entrySet()) {
                int productId = productEntry.getKey();
                int quantity = productEntry.getValue();
                Product product = store.getProducts().get(productId);
                totalcount =totalcount + quantity*product.getProduct_price();
                ProductInSale productInSale = new ProductInSale(productId, product.getProduct_price(), quantity, storeId);
                productInSales.add(productInSale);
            }

            Purchase purchase = new Purchase(id, productInSales,storeId,totalcount );
        }
    }
}