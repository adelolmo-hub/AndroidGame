package com.example.cardgameproject;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {

    private String userName;
    private String email;
    private int berry;
    private HashMap<String, String> obtainedFragments;


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

    public int getBerry() {
        return berry;
    }

    public void setBerry(int berry) {
        this.berry = berry;
    }

    public HashMap<String, String> getObtainedFragments() {
        return obtainedFragments;
    }

    public void setObtainedFragments(HashMap<String, String> obtainedFragments) {
        this.obtainedFragments = obtainedFragments;
    }

}
