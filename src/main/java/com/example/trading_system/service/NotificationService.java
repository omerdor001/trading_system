package com.example.trading_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotification(String receiver, String notificationJson) {
        String destination = String.format("/user/%s/queue/notifications", receiver);
        messagingTemplate.convertAndSend(destination, notificationJson);
    }
}
