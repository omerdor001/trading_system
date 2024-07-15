package com.example.trading_system.domain.externalservices;

import com.example.trading_system.service.TradingSystemImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Service
public class DeliveryServiceProxy implements DeliveryService {
    private static final Logger logger = LoggerFactory.getLogger(TradingSystemImp.class);
    private static final String ADDRESS_PATTERN = "^[\\w\\s.,'-]+$";  // More flexible pattern
    private static final Pattern pattern = Pattern.compile(ADDRESS_PATTERN);
    int id = 1;

    @Autowired
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
        if (isValidAddress(address)) {
            System.out.println("Processing delivery to " + address);
        } else {
            logger.error("Delivery authorization failed to address : {}", address);
            throw new IllegalArgumentException("Delivery authorization failed");
        }
        logger.info("Succeeded making delivery to {}", address);
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

