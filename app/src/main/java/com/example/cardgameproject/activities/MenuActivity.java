package com.example.cardgameproject.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.cardgameproject.R;
import com.example.cardgameproject.models.Card;
import com.example.cardgameproject.models.DAOCard;
import com.example.cardgameproject.models.DAOUser;
import com.example.cardgameproject.models.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    private SharedPreferences spShonenCard;
    public static MediaPlayer musicShonenCard = new MediaPlayer();
    private final static int ID1 = 0;
    private final static int ID2 = 1;
    private int musicPosition;
    private static int RESULT_OK = -1;
    private static int RESULT_CANCELED = 0;
    public ArrayList<Card> cards;
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
        user = User.getInstance();
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
        Intent i = new Intent(this, CollectionActivity.class);
        i.putExtra("mediaPlayerTimePos", musicShonenCard.getCurrentPosition());
        musicShonenCard.pause();
        i.putExtra("cards", cards);
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

    public void onClickPlay(View view){
        if(user.getDeck().size() < 5){
            AlertDialog.Builder createAccountBuilder = new AlertDialog.Builder(this);
            createAccountBuilder.setTitle("Deck Error");
            createAccountBuilder.setMessage("You can't play without a deck, go to Collection to create one");
            createAccountBuilder.setNegativeButton("Cancel", null);
            createAccountBuilder.setPositiveButton("Go to Collection", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent collection = new Intent(MenuActivity.this, CollectionActivity.class);
                    collection.putExtra("mediaPlayerTimePos", musicShonenCard.getCurrentPosition());
                    musicShonenCard.pause();
                    startActivityForResult(collection, ID2);
                }
            });
            createAccountBuilder.show();
        }else {
            Intent i = new Intent(this, GameActivity.class);
            i.putExtra("mainuser", user);
            i.putExtra("allCards", cards);
            startActivity(i);
        }
    }


}