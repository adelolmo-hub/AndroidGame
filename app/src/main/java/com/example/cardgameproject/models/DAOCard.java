package com.example.cardgameproject.models;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DAOCard {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference ref;
    private final String URL = "https://cardgame-15ba2-default-rtdb.europe-west1.firebasedatabase.app";

    public DAOCard(){
        firebaseDatabase = FirebaseDatabase.getInstance(URL);
        databaseReference = firebaseDatabase.getReference();
        ref = databaseReference.child("Cards");
    }

    public void insertCard(Card card){
        ref.child(card.getName()).setValue(card);
    }

    public ArrayList<Card> getCard(){
        ArrayList<Card> listCards = new ArrayList<>();
         ref.get().addOnCompleteListener(task -> {
            if(!task.isSuccessful()){
                Log.e("firebase", "Error getting data",task.getException());
            }
            else{
                Card card;
                HashMap<String, HashMap> map = (HashMap<String, HashMap>) task.getResult().getValue();
                for(Map.Entry<String, HashMap> entry : map.entrySet()){
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
