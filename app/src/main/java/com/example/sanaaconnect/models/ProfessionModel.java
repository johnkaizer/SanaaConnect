package com.example.sanaaconnect.models;

public class ProfessionModel {
    String proffId;
    String fullName;
    String title;
    String education;
    String charges;
    String imageUrl;
    String experience;
    String location;
    String email;
    String phone;

    public ProfessionModel() {
    }

    public ProfessionModel(String proffId, String fullName, String title, String education, String charges, String imageUrl, String experience, String location, String email, String phone) {
        this.proffId = proffId;
        this.fullName = fullName;
        this.title = title;
        this.education = education;
        this.charges = charges;
        this.imageUrl = imageUrl;
        this.experience = experience;
        this.location = location;
        this.email = email;
        this.phone = phone;
    }

    public String getProffId() {
        return proffId;
    }

    public void setProffId(String proffId) {
        this.proffId = proffId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getCharges() {
        return charges;
    }

    public void setCharges(String charges) {
        this.charges = charges;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
