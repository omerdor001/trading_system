package com.example.trading_system.domain.externalservices;

import javax.management.InstanceAlreadyExistsException;

public interface ServiceFacade {
    void addService(String serviceName) throws InstanceAlreadyExistsException;
    void replaceService(String newServiceName, String oldServiceName);
    void changeServiceName(String serviceToChangeAtName,String newName);

    void makePayment(String serviceName,double amount);
    void makeDelivery(String serviceName,String address);


}
