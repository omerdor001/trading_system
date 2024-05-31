package com.example.trading_system.domain.externalservices;

import javax.management.InstanceAlreadyExistsException;

public interface ServiceFacade {
    void deleteInstance();

    void addPaymentService(String serviceName) throws InstanceAlreadyExistsException;

    void addPaymentProxyService(String serviceName) throws InstanceAlreadyExistsException;

    void addDeliveryService(String serviceName) throws InstanceAlreadyExistsException;

    void addDeliveryProxyService(String serviceName) throws InstanceAlreadyExistsException;

    void replaceService(String newServiceName, String oldServiceName);
    void changeServiceName(String serviceToChangeAtName,String newName);

    void makePayment(String serviceName,double amount);
    void makeDelivery(String serviceName,String address);

    public void clearServices();


}
