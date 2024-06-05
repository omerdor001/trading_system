package com.example.trading_system.domain.users;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.TimeZone;

@Component
public class Notification {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // Configure the ObjectMapper to use the local time zone
        objectMapper.setTimeZone(TimeZone.getDefault());
    }

    private String senderUsername;
    private String receiverUsername;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    private Date dateOfCreation;
    private String textContent;

    @JsonCreator
    public Notification(
            @JsonProperty("senderUsername") String senderUsername,
            @JsonProperty("receiverUsername") String receiverUsername,
            @JsonProperty("dateOfCreation") Date dateOfCreation,
            @JsonProperty("textContent") String textContent
    ) {
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.dateOfCreation = dateOfCreation;
        this.textContent = textContent;
    }

    public static Notification fromString(String str) {
        try {
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

    //TODO change method and uses of it
    @Override
    public String toString() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting Notification to JSON", e);
        }
    }
}
