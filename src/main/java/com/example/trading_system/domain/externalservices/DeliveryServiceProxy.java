package com.example.trading_system.domain.externalservices;

import com.example.trading_system.service.TradingSystemImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeliveryServiceProxy implements DeliveryService {
    private static final Logger logger = LoggerFactory.getLogger(TradingSystemImp.class);
    private static final String ADDRESS_PATTERN =
            "^\\d+\\s+[A-Za-z0-9\\s]+,\\s+[A-Za-z\\s]+,\\s+[A-Z]{2},\\s+\\d{5}(-\\d{4})?$";
    private static final Pattern pattern = Pattern.compile(ADDRESS_PATTERN);
    int id = 1;

    public DeliveryServiceProxy() {
    }

    public static boolean isValidAddress(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }
        Matcher matcher = pattern.matcher(address);
        return matcher.matches();
    }

    @Override
    public int makeDelivery(String address) {
        // Additional logic before delegating to the real payment service
        if (isValidAddress(address)) {
            System.out.println("Processing delivery of $" + address);
        } else {
            logger.error("Delivery authorization failed to address : {}", address);
            throw new IllegalArgumentException("Delivery authorization failed");
        }
        logger.info("Succeed making delivery to {}", address);
        int deliveryId = id;
        id++;
        return deliveryId;
    }

    @Override
    public void cancelDelivery(int deliveryId) {
        logger.info("Cancel delivery with id {}", deliveryId);
        System.out.println("Delivery cancelled successfully");
    }

    @Override
    public boolean connect() {
        return true;
    }
}
