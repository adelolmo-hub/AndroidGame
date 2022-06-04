package com.example.cardgameproject;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class MenuActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    private SharedPreferences spShonenCard;
    public static MediaPlayer musicShonenCard = new MediaPlayer();
    private final static int ID1 = 0;
    private final static int ID2 = 1;
    private int musicPosition;
    private static int RESULT_OK = -1;
    private static int RESULT_CANCELED = 0;
    public static ArrayList<Card> cards;
    private User user;
    DAOUser daoUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        DAOCard dao = new DAOCard();
        firebaseAuth = FirebaseAuth.getInstance();
        daoUser = new DAOUser(firebaseAuth.getCurrentUser().getUid());
        daoUser.addListener();
        if(cards == null) {
            cards = dao.getCard();
        }
        musicMainTheme();
    }


    public void musicMainTheme(){
        spShonenCard = getSharedPreferences("shonenCardPreference", Context.MODE_PRIVATE);
        if(spShonenCard.getBoolean("music", true)) {
            musicShonenCard = MediaPlayer.create(this, R.raw.main_theme);
            if (musicPosition > 0){
                musicShonenCard.seekTo(musicPosition);
            }
            musicShonenCard.start();
            musicShonenCard.setLooping(true);
        }
    }

    public void signOut(View view){
        FirebaseAuth.getInstance().signOut();
        finish();
    }

    public void collectionActivity(View view){
        user = daoUser.getUser();
        Intent i = new Intent(this, CollectionActivity.class);
        i.putExtra("mediaPlayerTimePos", musicShonenCard.getCurrentPosition());
        i.putExtra("user", user);
        i.putExtra("cards", cards);
        musicShonenCard.pause();
        startActivityForResult(i, ID2);
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
        settings.putExtra("mediaPlayerTimePos", musicShonenCard.getCurrentPosition());
        musicShonenCard.pause();
        startActivityForResult(settings, ID1);
    }

    protected void onPause() {
        super.onPause();
        musicShonenCard.pause();
    }

    /*protected void onResume() {
        super.onResume();
        spShonenCard = getSharedPreferences("shonenCardPreference", Context.MODE_PRIVATE);
        if(spShonenCard.getBoolean("music", true)) {
            if (musicPosition > 0){
                musicShonenCard.seekTo(musicPosition);
            }
            musicShonenCard.start();
            musicShonenCard.setLooping(true);
        }
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (ID1) :
            case (ID2) : {
                if (resultCode == RESULT_OK) {
                    musicPosition = data.getExtras().getInt("mediaPlayerTimePos");
                    spShonenCard = getSharedPreferences("shonenCardPreference", Context.MODE_PRIVATE);
                    if(spShonenCard.getBoolean("music", true)) {
                        if (musicPosition > 0){
                            musicShonenCard.seekTo(musicPosition);
                        }
                        musicShonenCard.start();
                        musicShonenCard.setLooping(true);
                    }
                }
                else if (resultCode == RESULT_CANCELED) {
                    musicPosition = 0;
                }
                break;
            }
        }
    }

    public void ganarPartida(View view){
        int numCard = (int) (Math.random() * (cards.size()));
        int numFragments = (int) (Math.random() * (5 - 1)+1);

        daoUser.updateCardFragments(cards.get(numCard).getName(), numFragments).addOnSuccessListener(suc -> {
           String message = "You have been rewarded with " + numFragments + " fragments of " + cards.get(numCard).getName();
            alertDialogReward(message);
        }).addOnFailureListener(e -> {
           String message = "An unknown error ocurred";
            alertDialogReward(message);
        });

    }


    public void alertDialogReward(String message){
        AlertDialog.Builder createAccountBuilder = new AlertDialog.Builder(this);
        createAccountBuilder.setTitle("Reward");
        createAccountBuilder.setMessage(message);
        createAccountBuilder.setPositiveButton("Ok", null);

        createAccountBuilder.show();
    }

    public void onClickPlay(View view){
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra("mainuser", user);
        startActivity(i);
    }

}