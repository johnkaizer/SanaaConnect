package com.example.sanaaconnect.models;

public class JobModel {
    String clientId;
    String jobTitle;
    String description;
    String postDate;

    public JobModel() {
    }

    public JobModel(String clientId, String jobTitle, String description, String postDate) {
        this.clientId = clientId;
        this.jobTitle = jobTitle;
        this.description = description;
        this.postDate = postDate;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }
}
