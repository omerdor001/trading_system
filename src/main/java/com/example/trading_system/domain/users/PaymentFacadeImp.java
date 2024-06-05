package com.example.trading_system.domain.users;

import com.example.trading_system.domain.externalservices.*;
import com.example.trading_system.domain.stores.*;
import com.example.trading_system.service.UserServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class PaymentFacadeImp implements PaymentFacade {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);
    private static PaymentFacadeImp instance = null;
    private MarketFacade marketFacade;
    private UserFacade userFacade;
    private List<Purchase> purchases;
    PaymentService paymentService;
    DeliveryService deliveryService;

    public PaymentFacadeImp(PaymentService paymentService,DeliveryService deliveryService) {
        purchases = new ArrayList<>();
        this.paymentService=paymentService;
        this.deliveryService=deliveryService;
        this.marketFacade = MarketFacadeImp.getInstance();
        this.userFacade = UserFacadeImp.getInstance();
    }

    private static class Singleton {
        private static final PaymentFacadeImp INSTANCE = null; //Will be deleted
    }

    public static PaymentFacadeImp getInstance() {
        if (instance == null)
            instance = PaymentFacadeImp.Singleton.INSTANCE;
        return instance;
    }

    public void deleteInstance() {
        instance = null;
        this.purchases = null;
        this.marketFacade.deleteInstance();
        this.marketFacade = null;
        this.userFacade.deleteInstance();
        this.userFacade = null;
    }

    @Override
    public synchronized boolean checkAvailabilityAndConditions(String username) {
        if (!userFacade.getUsers().containsKey(username)) {
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
        User user = userFacade.getUsers().get(username);
        Cart cart = user.getShopping_cart();
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

    @Override
    public synchronized void approvePurchase(String username) {
        if (!checkAvailabilityAndConditions(username)) {
            logger.error("Products are not available or do not meet purchase conditions.");
            throw new RuntimeException("Products are not available or do not meet purchase conditions.");
        }

        if (!userFacade.getUsers().containsKey(username)) {
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
        User user = userFacade.getUsers().get(username);
        Cart cart = user.getShopping_cart();
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
                releaseReservedProducts(username);
                throw new RuntimeException("time out !");
            }
        }, 10 * 60 * 1000);
        double totalPrice = calculateTotalPrice(cart);
        //TODO: fix process Payment
        //Making delivery
        int deliveryId=0;  //For cancelling
        String address = ""; //TODO : Need to be a parameter of this function
        try {
            deliveryId=deliveryService.makeDelivery(address);
        }
        catch (Exception e){
            //Release the products and message for another try
        }
        //Making payment
        int paymentId=0;   //For cancelling
        try {
            paymentId=paymentService.makePayment(totalPrice);
        }
        catch (Exception e){
            deliveryService.cancelDelivery(deliveryId);
            //Release the products and message for another try
        }
        addPurchase(username);
        timer.cancel();
        timer.purge();
    }

    private synchronized void releaseReservedProducts(String username) {
        if (!userFacade.getUsers().containsKey(username)) {
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
        User user = userFacade.getUsers().get(username);
        Cart cart = user.getShopping_cart();
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

    public double calculateTotalPrice(Cart cart) {
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

    @Override
    public String getPurchaseHistory(String username, String storeName, Integer productBarcode) {
        if (!userFacade.getUsers().containsKey(username)) {    //Change when Repo
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
        if (username.charAt(0)=='r' && !userFacade.getUsers().get(username).getLogged()) {
            logger.error("User is not logged");
            throw new RuntimeException("User is not logged");
        }
        if (!userFacade.isAdmin(username)) {
            logger.error("User is not commercial manager");
            throw new RuntimeException("User is not commercial manager");
        }

        List<Purchase> filteredPurchases = purchases;

        if (username != null) {
            filteredPurchases = filteredPurchases.stream()
                    .filter(p -> p.getCustomerUsername() == username)
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


    public String getStoresPurchaseHistory(String username, String storeName, Integer productBarcode) {
        if (!userFacade.getUsers().containsKey(username)) {    //Change when Repo
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
        if (username.charAt(0)=='r' && !userFacade.getUsers().get(username).getLogged()) {
            logger.error("User is not logged");
            throw new RuntimeException("User is not logged");
        }
        if (!userFacade.isAdmin(username)) {
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

    private void addPurchase(String registeredId) {
        Cart cart = userFacade.getUsers().get(registeredId).getShopping_cart();
        double totalcount = 0;
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
                totalcount = totalcount + quantity * product.getProduct_price();
                ProductInSale productInSale = new ProductInSale(productId, product.getProduct_price(), quantity, storeId);
                productInSales.add(productInSale);
            }

            Purchase purchase = new Purchase(userFacade.getUsers().get(registeredId).getUsername(), productInSales, storeId, totalcount);
        }
    }
}