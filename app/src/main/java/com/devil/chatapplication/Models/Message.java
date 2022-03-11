package com.devil.chatapplication.Models;

import com.google.firebase.Timestamp;

public class Message {
    private String messageId,message,senderid;
    private int feeling;
    private Long timestamp;

    public String getMessageId() {
        return messageId;
    }

    public Message(String message, String senderid, Long timestamp) {
        this.message = message;
        this.senderid = senderid;
        this.timestamp = timestamp;
    }

    public Message(String message, Long timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public Message() {
    }

    public Message(String messageId, String message, String senderid, int feeling, Long timestamp) {
        this.messageId = messageId;
        this.message = message;
        this.senderid = senderid;
        this.feeling = feeling;
        this.timestamp = timestamp;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public int getFeeling() {
        return feeling;
    }

    public void setFeeling(int feeling) {
        this.feeling = feeling;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
