package com.example.trading_system.Domain.externalservices;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class Service {
    private String serviceName;
    protected final RestTemplate restTemplate;

    public Service(String serviceName,RestTemplate restTemplate){
        this.serviceName=serviceName;
        this.restTemplate = restTemplate;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
