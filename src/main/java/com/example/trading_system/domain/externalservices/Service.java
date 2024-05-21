package com.example.trading_system.Domain.externalservices;

import org.springframework.web.client.RestTemplate;

public class Service {
    private String serviceName;

    public Service(String serviceName){
        this.serviceName=serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
