package com.example.trading_system.domain.externalservices;

public abstract class Service {
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

    public abstract void makePayment(String serviceName,double amount);
    public abstract void cancelPayment(String serviceName);
    public abstract void makeDelivery(String serviceName, String address);
    public abstract void cancelDelivery(String serviceName,String address);
    public abstract boolean connect();
}
