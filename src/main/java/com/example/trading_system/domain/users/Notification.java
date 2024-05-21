package com.example.trading_system.domain.users;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Component
public class Notification {
    private int senderId;
    private int receiverId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    private Date dateOfCreation;

    private String textContent;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        // Configure the ObjectMapper to use the local time zone
        objectMapper.setTimeZone(TimeZone.getDefault());
    }

    @JsonCreator
    public Notification(
            @JsonProperty("senderId") int senderId,
            @JsonProperty("receiverId") int receiverId,
            @JsonProperty("dateOfCreation") Date dateOfCreation,
            @JsonProperty("textContent") String textContent
    ) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.dateOfCreation = dateOfCreation;
        this.textContent = textContent;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Date dateOfCreation) {
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
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting Notification to JSON", e);
        }
    }

    public static Notification fromString(String str) {
        try {
            return objectMapper.readValue(str, Notification.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing Notification from JSON", e);
        }
    }
}
