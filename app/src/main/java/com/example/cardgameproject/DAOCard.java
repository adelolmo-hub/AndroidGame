package com.example.cardgameproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
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
        ref.child(card.getUid()).setValue(card);
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
                    HashMap<String,String> value = entry.getValue();
                    card = new Card();

                    card.setUid(entry.getKey());
                    card.setCode(value.get("code"));
                    card.setName(value.get("name"));
                    card.setImageUrl(value.get("image"));

                    listCards.add(card);
                }

            }
        });
        return listCards;
    }


}
