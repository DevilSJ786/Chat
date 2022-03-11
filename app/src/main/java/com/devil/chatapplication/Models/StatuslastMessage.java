package com.devil.chatapplication.Models;

import com.google.firebase.Timestamp;

public class StatuslastMessage {
    String status,lastMessage;
    Timestamp timestamp;

    public StatuslastMessage() {
    }

    public StatuslastMessage(String status) {
        this.status = status;
    }

    public StatuslastMessage(String lastMessage, Timestamp timestamp) {
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    public StatuslastMessage(String status, String lastMessage, Timestamp timestamp) {
        this.status = status;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
