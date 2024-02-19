package com.example.sanaaconnect.models;

public class MessageModel {
    String messageId;
    String clientId;
    String creativesId;
    String content;
    String date;

    public MessageModel() {
    }

    public MessageModel(String messageId, String clientId, String creativesId, String content, String date) {
        this.messageId = messageId;
        this.clientId = clientId;
        this.creativesId = creativesId;
        this.content = content;
        this.date = date;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getCreativesId() {
        return creativesId;
    }

    public void setCreativesId(String creativesId) {
        this.creativesId = creativesId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
