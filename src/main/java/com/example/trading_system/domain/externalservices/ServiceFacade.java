package com.example.trading_system.domain.externalservices;

import javax.management.InstanceAlreadyExistsException;

public interface ServiceFacade {
    boolean addService(Service service) throws InstanceAlreadyExistsException;
    boolean replaceService(Service newService, Service oldService);
    boolean changeServiceName(Service serviceToChangeAt, String newName);

    boolean makePayment(String serviceName,double amount);
    boolean makeDelivery(String serviceName,String address);


}
