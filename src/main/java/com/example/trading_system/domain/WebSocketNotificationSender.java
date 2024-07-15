package com.example.trading_system.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketNotificationSender implements NotificationSender {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketNotificationSender(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotification(String receiver, String notificationJson) {
        String destination = String.format("/user/%s/notifications", receiver);
        messagingTemplate.convertAndSend(destination, notificationJson);
    }
}
