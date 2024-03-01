package com.example.sanaaconnect.models;

public class Portfolio {
    String imageUrl;
    Long timeStamp;
    String userUid;

    public Portfolio() {
    }

    public Portfolio(String imageUrl, Long timeStamp, String userUid) {
        this.imageUrl = imageUrl;
        this.timeStamp = timeStamp;
        this.userUid = userUid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }
}
