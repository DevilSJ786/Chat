package com.devil.chatapplication.Models;

public class userProfile {
    private String name,status,uid,image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public userProfile(String name, String status, String uid, String image) {
        this.name = name;
        this.status = status;
        this.uid = uid;
        this.image = image;
    }

    public userProfile() {
    }
}
