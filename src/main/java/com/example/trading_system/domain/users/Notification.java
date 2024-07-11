package com.example.trading_system.domain.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.*;
import java.util.TimeZone;

@Entity
public class Notification {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String senderUsername;

    @Column(nullable = false)
    private String receiverUsername;

    @Column(nullable = false)
    private String textContent;

    public Notification(String senderUsername, String receiverUsername, String textContent) {
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.textContent = textContent;
    }

    public Notification() {

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
