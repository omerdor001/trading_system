package com.example.trading_system.domain.users;

public interface PaymentFacade {
    boolean VisitorCheckAvailabilityAndConditions(int visitorId);

    boolean registeredCheckAvailabilityAndConditions(String registeredId);
    void VisitorApprovePurchase(int visitorId, String paymentService);

    void RegisteredApprovePurchase(String registeredId, String paymentService);

    double calculateTotalPrice(Cart cart);
}
