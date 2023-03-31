package com.example.cardgameproject.activities;

import static com.example.cardgameproject.activities.MenuActivity.musicShonenCard;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.media.MediaPlayer;

import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cardgameproject.adapters.GridAdapter;
import com.example.cardgameproject.R;
import com.example.cardgameproject.databinding.ActivityCollectionBinding;
import com.example.cardgameproject.models.Card;
import com.example.cardgameproject.models.DAOUser;
import com.example.cardgameproject.models.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


/**
 * This class represents the collection activity in the Shonen Card game.
 * It displays the user's collection of cards and allows them to buy cards.
 */
public class CollectionActivity extends AppCompatActivity {


    ActivityCollectionBinding binding;
    private SharedPreferences spShonenCard;
    public static ArrayList<Card> cards;
    DAOUser daoUser;
    private User user;

    /**
     * ON CREATE
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout
        binding = ActivityCollectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Call musicMainThemeColl method to play music
        musicMainThemeColl();

        // Get current user instance and initialize DAOUser object
        user = User.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        daoUser = new DAOUser(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());

        // Get cards ArrayList from the intent
        cards = (ArrayList<Card>) getIntent().getSerializableExtra("cards");

        // Set up the grid adapter and populate the grid view
        GridAdapter gridAdapter = new GridAdapter(this, cards, user);
        binding.gridView.setAdapter(gridAdapter);

        // Set up the on item click listener for the grid view
        binding.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // Get the card, text view, and image view of the selected item
                Card card = cards.get(i);
                TextView textPrice = view.findViewById(R.id.tvPrice);
                TextView textFragments = view.findViewById(R.id.tvName);
                ImageView imageView = view.findViewById(R.id.grid_image);

                // Create an alert dialog to prompt the user to buy the card
                AlertDialog.Builder createAccountBuilder = new AlertDialog.Builder(adapterView.getContext());
                createAccountBuilder.setTitle("Do you want to buy this card??");
                createAccountBuilder.setMessage(textPrice.getText().toString() + "\nYour money: " + user.getBerry());

                // Set up the negative button for the alert dialog
                createAccountBuilder.setNegativeButton("Cancel", null);

                // Set up the positive button for the alert dialog
                createAccountBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // Calculate the new amount of money the user will have
                        int newMoney = user.getBerry() - Integer.parseInt(textPrice.getText().toString().replaceAll("\\D+", ""));

                        // If the user has enough money to buy the card, update the user's data
                        if (newMoney >= 0) {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put(card.getName(), "complete");
                            imageView.setColorFilter(null);
                            textFragments.setText("Complete");
                            textPrice.setText("");
                            daoUser.updateUserBuyCard(map);
                            daoUser.updateBerry(newMoney);

                            // If the user doesn't have enough money to buy the card, show a toast message
                        } else {
                            Toast.makeText(CollectionActivity.this, "You don't have enough money", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                createAccountBuilder.show();
            }
        });
    }

    /**
     * This method plays the main theme music for the collection activity.
     */
    public void musicMainThemeColl() {
        // Get the bundle containing any extras that were passed to the activity
        Bundle bundle = getIntent().getExtras();
        // Get the shared preferences for the shonenCardPreference file
        spShonenCard = getSharedPreferences("shonenCardPreference", Context.MODE_PRIVATE);
        // Check if the music preference is enabled in the shared preferences
        if (spShonenCard.getBoolean("music", true)) {
            // Create a new MediaPlayer object with the main theme audio file
            musicShonenCard = MediaPlayer.create(this, R.raw.main_theme);
            // Check if there is a saved time position for the MediaPlayer in the bundle
            if (bundle.getInt("mediaPlayerTimePos") > 0) {
                // Seek to the saved time position
                musicShonenCard.seekTo(bundle.getInt("mediaPlayerTimePos"));
            }
            musicShonenCard.start();
            // Set the MediaPlayer to loop the audio file
            musicShonenCard.setLooping(true);
        }
    }

    /**
     * This method handles the behavior when the back button is clicked in the CollectionActivity.
     * It sets the result of the activity as RESULT_OK and adds the current position of the music player
     * to the intent as an extra. Then, it stops and resets the music player and finishes the activity.
     *
     * @param view The view that was clicked
     */
    public void onClickBackColl(View view) {
        Intent back = getIntent();
        setResult(RESULT_OK, back);
        back.putExtra("mediaPlayerTimePos", musicShonenCard.getCurrentPosition());
        musicShonenCard.stop();
        musicShonenCard.reset();
        finish();
    }

}