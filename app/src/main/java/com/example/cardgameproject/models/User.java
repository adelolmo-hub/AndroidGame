package com.example.cardgameproject.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * The User class represents a card in a trading card game.
 * It contains information about the User name, email, berries(money), obtained fragments and deck
 * The class implements the Serializable interface for serialization purposes.
 * This class is Singleton
 * TODO - Serializable puede que no sea necesario y se pueda retirar
 */
public class User implements Serializable {

    private String userName;
    private String email;
    private int berry;
    private HashMap<String, String> obtainedFragments;
    private ArrayList<Card> deck;
    private static User user;


    //Private constructor to ensure that no instance of the User class can be created from outside the class.
    private User() {

    }

    /*
     * Returns the singleton instance of the User class.
     * If the instance does not exist, it creates one and returns it.
     * @return The singleton instance of the User class.
     */
    public static User getInstance() {
        if (user == null) {
            user = new User();
        }
        return user;
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

    public ArrayList<Card> getDeck() {
        return deck;
    }

    public void setDeck(ArrayList<Card> deck) {
        this.deck = deck;
    }
}
