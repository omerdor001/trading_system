package com.example.trading_system.domain.externalservices;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

public class DeliveryService extends Service{

    public DeliveryService(String serviceName) {
        super(serviceName);
    }

    public void processDelivery(String address) {
        System.out.println("Processing delivery of $" + address);
        // Here would be the real delivery processing logic, e.g., calling an external delivery gateway API
    }

}
