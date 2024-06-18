package com.example.trading_system.domain.users;

import com.example.trading_system.service.NotificationService;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.TimeZone;

public class Notification {
    private String senderUsername;
    private String receiverUsername;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dateOfCreation;
    private String textContent;

    public Notification(String senderUsername, String receiverUsername, String textContent) {
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.dateOfCreation = LocalDateTime.now();
        this.textContent = textContent;
    }

    public static Notification fromString(String str) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setTimeZone(TimeZone.getDefault());
            return objectMapper.readValue(str, Notification.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing Notification from JSON", e);
        }
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public LocalDateTime getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(LocalDateTime dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    @Override
    public String toString() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setTimeZone(TimeZone.getDefault());
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting Notification to JSON", e);
        }
    }

    public static String createNotificationJson(String senderUsername, String receiverUsername, String textContent){
        Notification notification = new Notification(senderUsername, receiverUsername, textContent);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setTimeZone(TimeZone.getDefault());
        try{
            return objectMapper.writeValueAsString(notification);
        }
        catch (JsonProcessingException e){
            throw new RuntimeException("Error creating notification to send");
        }
    }
}
