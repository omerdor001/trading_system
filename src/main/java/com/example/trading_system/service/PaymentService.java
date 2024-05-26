package com.example.trading_system.service;

public interface PaymentService {
      boolean VisitorCheckAvailabilityAndConditions(int visitorId);

      boolean registeredCheckAvailabilityAndConditions(String registeredId);

      void VisitorApprovePurchase(int visitorId, String paymentService);

      void RegisteredApprovePurchase(String registeredId, String paymentService);

}

