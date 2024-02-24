package com.example.sanaaconnect.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReviewModel {
    String clientId;
    String ownerId;
    String ownerName;
    String content;
    String date;
    int reactions;

    public ReviewModel() {
    }
    public ReviewModel(String clientId, String ownerId, String ownerName, String content, String date, int reactions) {
        this.clientId = clientId;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.content = content;
        this.date = date;
        this.reactions = reactions;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
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

    public int getReactions() {
        return reactions;
    }

    public void setReactions(int reactions) {
        this.reactions = reactions;
    }
    // Method to get the current date in the desired format
    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd h:mma", Locale.getDefault());
        return dateFormat.format(new Date());
    }
}
