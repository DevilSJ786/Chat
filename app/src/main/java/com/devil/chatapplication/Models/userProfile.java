package com.devil.chatapplication.Models;

import java.util.Map;

public class userProfile {
    private String name,status,uid,image,token,number;
    private Map<String,Boolean> friendlist;

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

    public userProfile(String name, String status, String uid, String image, String token, String number, Map<String, Boolean> friendlist) {
        this.name = name;
        this.status = status;
        this.uid = uid;
        this.image = image;
        this.token = token;
        this.number = number;
        this.friendlist = friendlist;
    }


    public userProfile(String name, String status, String uid, String image) {
        this.name = name;
        this.status = status;
        this.uid = uid;
        this.image = image;
    }

    public userProfile(String name, String status, String uid, String image, String token) {
        this.name = name;
        this.status = status;
        this.uid = uid;
        this.image = image;
        this.token = token;
    }

    public userProfile() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Map<String, Boolean> getFriendlist() {
        return friendlist;
    }

    public void setFriendlist(Map<String, Boolean> friendlist) {
        this.friendlist = friendlist;
    }
}
