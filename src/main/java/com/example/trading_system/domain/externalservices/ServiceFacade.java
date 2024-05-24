package com.example.trading_system.domain.externalservices;

import javax.management.InstanceAlreadyExistsException;

public interface ServiceFacade {
    public void addService(Service service) throws InstanceAlreadyExistsException;
    public void replaceService(Service newService, Service oldService);
    public void changeServiceName(Service serviceToChangeAt, String newName);

    public void makePayment(String serviceName,double amount);
    public void makeDelivery(String serviceName,String address);


}
