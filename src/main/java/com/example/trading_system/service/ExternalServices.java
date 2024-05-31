package com.example.trading_system.service;

import javax.management.InstanceAlreadyExistsException;

public interface ExternalServices {
    void deleteInstance();

    void addPaymentService(String serviceName) throws InstanceAlreadyExistsException;

    void addPaymentProxyService(String serviceName) throws InstanceAlreadyExistsException;

    void addDeliveryService(String serviceName) throws InstanceAlreadyExistsException;

    void addDeliveryProxyService(String serviceName) throws InstanceAlreadyExistsException;

    void replaceService(String newService, String oldService);

    void changeServiceName(String serviceToChangeAt, String newName);

    void clearServices();

    void makePayment(String serviceName, double amount);

    void makeDelivery(String serviceName, String address);
}
