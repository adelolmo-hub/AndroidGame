package com.example.cardgameproject.models;

import java.util.ArrayList;
import java.util.List;

public class MainPlayer {

    private String playerName;
    private List<Card> playerDeck;
    private int playerHP;

    public MainPlayer(String playerName, int playerHP) {
        this.playerName = playerName;
        this.playerHP = playerHP;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getPlayerHP() {
        return playerHP;
    }

    public void setPlayerHP(int playerHP) {
        this.playerHP = playerHP;
    }

    public ArrayList<Card> getPlayerDeck(){
        DAOCard daoCard = new DAOCard();
        ArrayList<Card> cards = daoCard.getCard();
        return cards;
    }
}
