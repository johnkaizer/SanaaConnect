package com.example.sanaaconnect.models;

public class MessageModel {
    String messageId;
    String recieverId;
    String senderId;
    String content;
    String date;
    String userName;

    public MessageModel() {
    }

    public MessageModel(String messageId, String recieverId, String senderId, String content, String date, String userName) {
        this.messageId = messageId;
        this.recieverId = recieverId;
        this.senderId = senderId;
        this.content = content;
        this.date = date;
        this.userName = userName;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getRecieverId() {
        return recieverId;
    }

    public void setRecieverId(String recieverId) {
        this.recieverId = recieverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
