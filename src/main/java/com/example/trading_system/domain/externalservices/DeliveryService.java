package com.example.trading_system.domain.externalservices;

import org.springframework.web.client.RestTemplate;

public class DeliveryService extends Service{

    public DeliveryService(String serviceName, RestTemplate restTemplate) {
        super(serviceName,restTemplate);
    }

}
