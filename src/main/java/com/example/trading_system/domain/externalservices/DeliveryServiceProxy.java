package com.example.trading_system.domain.externalservices;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeliveryServiceProxy {
    private DeliveryService realDeliveryService;

    public DeliveryServiceProxy(String serviceName) {
        this.realDeliveryService = new DeliveryService(serviceName);
    }

    private static final String ADDRESS_PATTERN =
            "^\\d+\\s+[A-Za-z0-9\\s]+,\\s+[A-Za-z\\s]+,\\s+[A-Z]{2},\\s+\\d{5}(-\\d{4})?$";
    private static final Pattern pattern = Pattern.compile(ADDRESS_PATTERN);

    public static boolean isValidAddress(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }
        Matcher matcher = pattern.matcher(address);
        return matcher.matches();
    }


    public void processDelivery(String address) {
        // Additional logic before delegating to the real payment service
        if (isValidAddress(address)) {
            realDeliveryService.processDelivery(address);
        } else {
            throw new IllegalArgumentException("Payment authorization failed");
        }
    }
}


