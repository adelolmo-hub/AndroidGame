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

/**
 * This class represents the main menu activity of the application.
 * It allows users to move between all the activities, start a game or enter to settings.
 */
public class MenuActivity extends AppCompatActivity {

    private SharedPreferences spShonenCard;
    public static MediaPlayer musicShonenCard = new MediaPlayer();
    private final static int ID1 = 0;
    private final static int ID2 = 1;
    private int musicPosition;
    public ArrayList<Card> cards;
    private User user;


    /**
     * ONCREATE
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        DAOCard dao = new DAOCard();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DAOUser daoUser = new DAOUser(firebaseAuth.getCurrentUser().getUid());
        daoUser.addListener();
        user = User.getInstance();
        if (cards == null) {
            cards = dao.getCard();
        }
        musicMainTheme();
    }


    /**
     * This method plays the main theme music using the MediaPlayer class.
     */
    public void musicMainTheme() {
        spShonenCard = getSharedPreferences("shonenCardPreference", Context.MODE_PRIVATE);
        if (spShonenCard.getBoolean("music", true)) {
            musicShonenCard = MediaPlayer.create(this, R.raw.main_theme);
            if (musicPosition > 0) {
                musicShonenCard.seekTo(musicPosition);
            }
            musicShonenCard.start();
            musicShonenCard.setLooping(true);
        }
    }

    /**
     * This method signs out the current user and finishes the activity.
     *
     * @param view Current view
     */
    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        finish();
    }

    /**
     * This method starts the CollectionActivity and passes it the cards data and the current
     * position of the main theme music.
     *
     * @param view Current view
     */
    public void collectionActivity(View view) {
        Intent i = new Intent(this, CollectionActivity.class);
        i.putExtra("mediaPlayerTimePos", musicShonenCard.getCurrentPosition());
        musicShonenCard.pause();
        i.putExtra("cards", cards);
        startActivityForResult(i, ID2);
    }

    /**
     * This method is called when the back button is pressed. It shows an alert dialog asking the
     * user if they want to sign out.
     */
    @Override
    public void onBackPressed() {
        alertDialogSignOut();
    }


    /**
     * Displays an alert dialog asking the user if they want to sign out of the current session.
     * If the user confirms, it signs out the user and finishes the activity.
     */
    public void alertDialogSignOut() {
        AlertDialog.Builder signOutAlert = new AlertDialog.Builder(this);
        signOutAlert.setTitle("Do you want to sign out?");
        // Set negative button
        signOutAlert.setNegativeButton("Cancel", null);
        // Create a button to sign out and add a click listener to it
        Button signOutButton = new Button(this);
        signOutButton.setText("Sign Out");
        signOutButton.setOnClickListener(v -> {
            // Sign out the user and finish the activity
            FirebaseAuth.getInstance().signOut();
            finish();
        });
        // Set the sign out button as the view of the alert dialog
        signOutAlert.setView(signOutButton);
        // Show the alert dialog
        signOutAlert.show();
    }

    /**
     * This method starts the SettingsActivity and passes it the current position of the main theme music.
     *
     * @param view Current view
     */
    public void onClickSettings(View view) {
        Intent settings = new Intent(this, SettingsActivity.class);
        settings.putExtra("mediaPlayerTimePos", musicShonenCard.getCurrentPosition());
        musicShonenCard.pause();
        startActivityForResult(settings, ID1);
    }

    /**
     * This method is called when the activity is paused. It pauses the main theme music.
     */
    protected void onPause() {
        super.onPause();
        musicShonenCard.pause();
    }

    /**
     * This method is called when an activity that was started for a result has finished and returned a result.
     * It is used to receive and process the data sent from the called activity.
     *
     * @param requestCode The request code passed to startActivityForResult() to identify the activity that returns the result.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param data        An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (ID1):
            case (ID2): {
                int RESULT_OK = -1;
                int RESULT_CANCELED = 0;
                if (resultCode == RESULT_OK) {
                    // Retrieve the current position of the media player from the data passed back by the called activity
                    musicPosition = data.getExtras().getInt("mediaPlayerTimePos");
                    spShonenCard = getSharedPreferences("shonenCardPreference", Context.MODE_PRIVATE);
                    if (spShonenCard.getBoolean("music", true)) {
                        if (musicPosition > 0) {
                            // Set the media player to the retrieved position
                            musicShonenCard.seekTo(musicPosition);
                        }
                        musicShonenCard.start();
                        musicShonenCard.setLooping(true);
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    musicPosition = 0;
                }
                break;
            }
        }
    }

    /**
     * This method is called when the play button is pressed.
     * Starts the game activity if the deck is valid.
     *
     * @param view Current view
     */
    public void onClickPlay(View view) {
        if (user.getDeck().size() < 5) {
            // Display an error message if the user doesn't have enough cards to play
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
        } else {
            // Launch the game activity
            Intent i = new Intent(this, GameActivity.class);
            //TODO - Esto se puede eliminar (SINGLETON)
            i.putExtra("mainuser", user);
            i.putExtra("allCards", cards);
            startActivity(i);
        }
    }


}