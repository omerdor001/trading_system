package com.example.trading_system.Domain.externalservices;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

public class DeliveryService extends Service{

    public DeliveryService(String serviceName) {
        super(serviceName);
    }

}
