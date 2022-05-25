package com.example.cardgameproject;

import java.util.ArrayList;
import java.util.HashMap;

public class User {

    private String userName;
    private String email;
    private int berry;
    private HashMap<String, Integer> obtainedFragments;


    public User(){

    }

    public User(String userName, String email) {
        this.userName = userName;
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
