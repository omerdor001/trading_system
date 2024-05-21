package com.example.trading_system.Domain.users;

public class Notification {
    public String description;
    public int senderId;
    public int receiverId;

    public Notification(String description,int senderId,int receiverId) {
        this.description = description;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
