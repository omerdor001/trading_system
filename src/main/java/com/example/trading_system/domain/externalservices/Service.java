package com.example.trading_system.domain.externalservices;


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
