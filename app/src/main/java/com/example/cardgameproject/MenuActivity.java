package com.example.cardgameproject;

import androidx.activity.result.contract.ActivityResultContracts;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MenuActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    private SharedPreferences spShonenCard;
    public static MediaPlayer musicShonenCard = new MediaPlayer();
    private final static int ID1 = 0;
    private int musicPosition;
    private static int RESULT_OK = -1;
    private static int RESULT_CANCELED = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

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
        startActivityForResult(i, ID1);
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
            case (ID1) : {
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


}