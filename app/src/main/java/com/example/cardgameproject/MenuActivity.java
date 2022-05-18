package com.example.cardgameproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MenuActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


    }

    public void signOut(View view){
        FirebaseAuth.getInstance().signOut();
        finish();
    }

    public void collectionActivity(View view){
        Intent i = new Intent(this, CollectionActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed(){
        alertDialogSignOut();
    }


    public void alertDialogSignOut(){
        AlertDialog.Builder signOutAlert = new AlertDialog.Builder(this);
        signOutAlert.setTitle("Do you want to close session?");

        signOutAlert.setNegativeButton("Cancel", null);

        Button signOutButton = new Button(this);
        signOutButton.setText("Sign Out");
        signOutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            finish();
        });

        signOutAlert.setView(signOutButton);

        signOutAlert.show();
    }

    public void onClickSettings(View view){
        Intent settings = new Intent(this, SettingsActivity.class);
        startActivity(settings);
    }

}