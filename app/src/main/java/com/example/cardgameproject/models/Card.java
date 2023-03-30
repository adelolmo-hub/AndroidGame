package com.example.cardgameproject.models;


import java.io.Serializable;
import java.util.Objects;


/*
 * The Card class represents a card in a trading card game.
 * It contains information about the card's name, image, rarity, price, health, and damage.
 * The class implements the Serializable interface for serialization purposes.
 * TODO - Serializable puede que no sea necesario y se pueda retirar
 */
public class Card implements Serializable {

    private String name;
    private String imageUrl;
    private String rarity;
    private long price;
    private long health;
    private long damage;

    public Card(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getHealth() {
        return health;
    }

    public void setHealth(long health) {
        this.health = health;
    }

    public long getDamage() {
        return damage;
    }

    public void setDamage(long damage) {
        this.damage = damage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(name, card.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
