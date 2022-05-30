package com.example.cardgameproject;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.HashMap;
import java.util.Set;


public class DAOUser {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private final String URL = "https://cardgame-15ba2-default-rtdb.europe-west1.firebasedatabase.app";
    private User user;

    public DAOUser(String id){
        firebaseDatabase = FirebaseDatabase.getInstance(URL);
        databaseReference = firebaseDatabase.getReference().child("User").child(id);
        this.user = new User();
    }

    public void insertUser(User user){
        databaseReference.setValue(user);
    }

    public void addListener(){

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    user.setUserName(snapshot.child("userName").getValue(String.class));
                    user.setEmail(snapshot.child("email").getValue(String.class));
                    user.setBerry(snapshot.child("berry").getValue(Integer.class));
                    user.setObtainedFragments((HashMap<String, String>) snapshot.child("CardFragments").getValue());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                throw error.toException();
            }
        });
    }

    public Task<Void> updateBerry(int berry){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("berry", user.getBerry() + berry);
        return databaseReference.updateChildren(hashMap);
    }

    public Task<Void> updateCardFragments(String cardName, int fragments){
        HashMap<String, Object> hashMap = new HashMap<>();
        if(user.getObtainedFragments() == null){
            user.setObtainedFragments(new HashMap<>());
        }
        if(!TextUtils.isEmpty(user.getObtainedFragments().get(cardName))) {
            if ("complete".equals(user.getObtainedFragments().get(cardName))) {
                updateBerry(fragments * 10);
            } else {
                int currentFragments = Integer.parseInt(user.getObtainedFragments().get(cardName));
                if ((currentFragments + fragments) >= 6) {
                    hashMap.put(cardName, "complete");
                } else {
                    hashMap.put(cardName, String.valueOf(currentFragments + fragments));
                }
            }
        }else{
            hashMap.put(cardName, String.valueOf(fragments));
        }
        return databaseReference.child("CardFragments").updateChildren(hashMap);
    }

    public User getUser(){
        return user;
    }

    public Task<Void> updateUserBuyCard(HashMap<String, Object> hashMap){
        return databaseReference.child("CardFragments").updateChildren(hashMap);
    }
}
