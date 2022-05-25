package com.example.cardgameproject;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class DAOUser {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private final String URL = "https://cardgame-15ba2-default-rtdb.europe-west1.firebasedatabase.app";

    public DAOUser(){
        firebaseDatabase = FirebaseDatabase.getInstance(URL);
        databaseReference = firebaseDatabase.getReference().child("User");
    }

    public void insertCard(String id, User user){
        databaseReference.child(id).setValue(user);
    }

    public User getUser(String id){
        User user = new User();
        Query myUsersQuery = databaseReference;
        myUsersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue().equals(id)) {
                    user.setUserName(snapshot.child("userName").getValue(String.class));
                    user.setEmail(snapshot.child("email").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                throw error.toException();
            }
        });
        return user;
    }
}
