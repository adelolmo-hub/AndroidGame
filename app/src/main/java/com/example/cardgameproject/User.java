package com.example.cardgameproject;

public class User {

    private String uid;
    private String email;
    private String userName;


    public User(){

    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
