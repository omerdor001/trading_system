package com.example.trading_system.domain.externalservices;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeliveryServiceProxy extends Service{

    public DeliveryServiceProxy(String serviceName) {
        super(serviceName);

    }

    private static final String ADDRESS_PATTERN =
            "^\\d+\\s+[A-Za-z0-9\\s]+,\\s+[A-Za-z\\s]+,\\s+[A-Z]{2},\\s+\\d{5}(-\\d{4})?$";
    private static final Pattern pattern = Pattern.compile(ADDRESS_PATTERN);

    @Override
    public void makePayment(String serviceName, double amount) {}

    @Override
    public void cancelPayment(String serviceName) {}


    public static boolean isValidAddress(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }
        Matcher matcher = pattern.matcher(address);
        return matcher.matches();
    }

    @Override
    public void makeDelivery(String serviceName, String address) {
        // Additional logic before delegating to the real payment service
        if (isValidAddress(address)) {
            System.out.println("Processing delivery of $" + address);
            // Here would be the real delivery processing logic, e.g., calling an external delivery gateway API
        } else {
            throw new IllegalArgumentException("Delivery authorization failed");
        }
    }

    @Override
    public void cancelDelivery(String serviceName, String address) {

    }

    @Override
    public boolean connect() {
        return true;
    }
}


