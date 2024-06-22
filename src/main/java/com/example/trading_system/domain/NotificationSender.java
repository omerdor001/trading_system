package com.example.trading_system.domain;

public interface NotificationSender {
    void sendNotification(String receiver, String notification);
}
