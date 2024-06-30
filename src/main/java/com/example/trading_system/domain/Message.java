package com.example.trading_system.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class Message {
    private final String senderId;
    private final String senderUsername;
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
