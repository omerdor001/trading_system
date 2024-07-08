package com.example.trading_system.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Message {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private final String senderId;

    @Column(nullable = false)
    private final String senderUsername;

    @Column(nullable = false, length = 1000)
    private final String content;

    public Message(String senderId, String senderUsername, String content){
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.content = content;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public String getContent() {
        return content;
    }

    public String toJson(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        }
        catch (JsonProcessingException e){
            throw new RuntimeException("Failed to convert message to JSON: " + e.getMessage());
        }
    }

    public static String toJsonList(List<Message> messageList){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(messageList);
        }
        catch (JsonProcessingException e){
            throw new RuntimeException("Failed to convert message list to JSON: " + e.getMessage());
        }
    }
}
