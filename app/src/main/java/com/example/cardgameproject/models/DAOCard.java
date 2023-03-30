package com.example.cardgameproject.models;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * The DAOCard class provides data access methods for a card game using Firebase Realtime Database.
 * It allows inserting new cards and retrieving a list of existing cards from the database.
 */
public class DAOCard {
    //FireBase
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference ref;
    //Firebase URL constant
    private final String URL = "https://cardgame-15ba2-default-rtdb.europe-west1.firebasedatabase.app";

    //Constructs a DAOCard object and initializes the Firebase instance and the database reference.
    public DAOCard() {
        firebaseDatabase = FirebaseDatabase.getInstance(URL);
        databaseReference = firebaseDatabase.getReference();
        ref = databaseReference.child("Cards");
    }

    //Inserts a new Card object into the Firebase Realtime Database under its name.
    public void insertCard(Card card) {
        ref.child(card.getName()).setValue(card);
    }

    /*
     * Retrieves a list of Card objects from the Firebase Realtime Database.
     * The method uses an asynchronous callback to retrieve the data from the database,
     * so it returns an empty list initially and populates it when the data is available.
     * @return an ArrayList of Card objects retrieved from the database.
     */
    public ArrayList<Card> getCard() {
        ArrayList<Card> listCards = new ArrayList<>();
        ref.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                Card card;
                HashMap<String, HashMap> map = (HashMap<String, HashMap>) task.getResult().getValue();
                for (Map.Entry<String, HashMap> entry : map.entrySet()) {
                    HashMap<String, Object> value = entry.getValue();
                    card = new Card();
                    card.setRarity(String.valueOf(value.get("rarity")));
                    card.setName(String.valueOf(value.get("name")));
                    card.setImageUrl(String.valueOf(value.get("image")));
                    card.setPrice((Long) value.get("price"));
                    card.setHealth((Long) value.get("Health"));
                    card.setDamage((Long) value.get("Damage"));

                    listCards.add(card);
                }
            }
        });
        return listCards;
    }


}
