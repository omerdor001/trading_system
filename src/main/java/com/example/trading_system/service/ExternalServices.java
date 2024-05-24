package com.example.trading_system.service;

import com.example.trading_system.domain.externalservices.Service;

public interface ExternalServices {
    public boolean addService(Service service);

    public boolean replaceService(Service newService, Service oldService);

    public boolean changeServiceName(Service serviceToChangeAt,String newName);

    public boolean makePayment(String serviceName,double amount);

    public boolean makeDelivery(String serviceName,String address);
}
