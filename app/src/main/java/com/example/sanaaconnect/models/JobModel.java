package com.example.sanaaconnect.models;

public class JobModel {
    String clientId;
    String jobTitle;
    String description;
    String amount;
    String postDate;
    String deadlineDate;
    String email;

    public JobModel() {
    }

    public JobModel(String clientId, String jobTitle, String description, String amount, String postDate, String deadlineDate, String email) {
        this.clientId = clientId;
        this.jobTitle = jobTitle;
        this.description = description;
        this.amount = amount;
        this.postDate = postDate;
        this.deadlineDate = deadlineDate;
        this.email = email;
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getDeadlineDate() {
        return deadlineDate;
    }
    public void setDeadlineDate(String deadlineDate) {
        this.deadlineDate = deadlineDate;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
